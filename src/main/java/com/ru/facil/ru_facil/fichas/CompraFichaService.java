package com.ru.facil.ru_facil.fichas;

import com.ru.facil.ru_facil.email.EmailService;
import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.entities.CompraFicha;
import com.ru.facil.ru_facil.enuns.PaymentMethod;
import com.ru.facil.ru_facil.enuns.PaymentStatus;
import com.ru.facil.ru_facil.enuns.TicketPriceType;
import com.ru.facil.ru_facil.fichas.dto.CompraFichaRequest;
import com.ru.facil.ru_facil.fichas.dto.CompraFichaResponse;
import com.ru.facil.ru_facil.fichas.dto.PagamentoPorMetodoResumo;
import com.ru.facil.ru_facil.fichas.dto.ResumoComprasResponse;
import com.ru.facil.ru_facil.payments.PagBankClient;
import com.ru.facil.ru_facil.payments.dto.CardPaymentResult;
import com.ru.facil.ru_facil.payments.dto.PagBankPixResponse;
import com.ru.facil.ru_facil.payments.dto.PagBankWebhookSimuladoRequest;
import com.ru.facil.ru_facil.pontuacao.PontuacaoService;
import com.ru.facil.ru_facil.qrcode.QrCodeService;
import com.ru.facil.ru_facil.repositories.ClienteRepository;
import com.ru.facil.ru_facil.repositories.CompraFichaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompraFichaService {

    private final ClienteRepository clienteRepository;
    private final CompraFichaRepository compraFichaRepository;
    private final QrCodeService qrCodeService;
    private final PontuacaoService pontuacaoService;
    private final PagBankClient pagBankClient;
    private final EmailService emailService;

    public CompraFichaService(ClienteRepository clienteRepository,
                              CompraFichaRepository compraFichaRepository,
                              QrCodeService qrCodeService,
                              PontuacaoService pontuacaoService,
                              PagBankClient pagBankClient,
                              EmailService emailService) {
        this.clienteRepository = clienteRepository;
        this.compraFichaRepository = compraFichaRepository;
        this.qrCodeService = qrCodeService;
        this.pontuacaoService = pontuacaoService;
        this.pagBankClient = pagBankClient;
        this.emailService = emailService;
    }

    // ------------------------------------------------------------------------
    // COMPRA DE FICHAS
    // ------------------------------------------------------------------------

    @Transactional
    public CompraFichaResponse comprarFichas(CompraFichaRequest request) {

        String email = request.email();
        PaymentMethod formaPagamento = request.formaPagamento();

        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado para o e-mail: " + email
                ));

        int quantidade = request.quantidade();
        if (quantidade <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade deve ser maior que zero");
        }

        boolean aluno = Boolean.TRUE.equals(cliente.getEhAluno());
        boolean moradorResidencia = Boolean.TRUE.equals(cliente.getMoradorResidencia());

        if (aluno && (cliente.getMatricula() == null || cliente.getMatricula().isBlank())) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Aluno precisa ter matrícula cadastrada para comprar fichas.");
        }

        TicketPriceType priceType;
        BigDecimal unitPrice;

        if (aluno && moradorResidencia) {
            priceType = TicketPriceType.MORADOR_RESIDENCIA;
            unitPrice = BigDecimal.ZERO;
        } else if (aluno) {
            priceType = TicketPriceType.ALUNO_UFRPE;
            unitPrice = new BigDecimal("3.00");
        } else {
            priceType = TicketPriceType.VISITANTE;
            unitPrice = new BigDecimal("20.00");
        }

        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantidade));

        CompraFicha compra = new CompraFicha();
        compra.setCliente(cliente);
        compra.setQuantidade(quantidade);
        compra.setValorUnitario(unitPrice);
        compra.setValorTotal(total);
        compra.setPriceType(priceType);
        compra.setCriadoEm(LocalDateTime.now());
        compra.setFormaPagamento(formaPagamento);

        boolean enviarEmailAposSalvar = false;

        // --- Decisão de fluxo por forma de pagamento ---
        if (formaPagamento == PaymentMethod.PIX) {

            // === Fluxo Pix + PagBank (pedido pendente) ===
            String referencia = "COMPRA-" + UUID.randomUUID();
            PagBankPixResponse pixResponse = pagBankClient.criarPedidoPix(cliente, total, referencia);

            compra.setStatusPagamento(PaymentStatus.PENDENTE);
            compra.setGatewayProvider("PAGBANK");
            compra.setGatewayOrderId(pixResponse.orderId());
            compra.setGatewayQrCodeText(pixResponse.qrCodeText());
            compra.setGatewayQrCodeImageUrl(pixResponse.qrCodeImageUrl());

            // Ticket interno já gerado, mas só será aceito se statusPagamento = PAGO
            compra.setCodigoValidacao(UUID.randomUUID().toString());
            compra.setUsada(Boolean.FALSE);
            compra.setUsadaEm(null);

        } else if (formaPagamento == PaymentMethod.CARTAO_CREDITO
                || formaPagamento == PaymentMethod.CARTAO_DEBITO
                || formaPagamento == PaymentMethod.CARTEIRA_DIGITAL) {

            // === Fluxo cartão/carteira com tokenização ===

            if (request.paymentToken() == null || request.paymentToken().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Token de pagamento é obrigatório para pagamentos com cartão ou carteira digital."
                );
            }

            String referencia = "COMPRA-" + UUID.randomUUID();
            CardPaymentResult cardResult = pagBankClient.autorizarPagamentoCartao(
                    request.paymentToken(),
                    total,
                    referencia
            );

            if (!cardResult.autorizado()) {
                // 402 Payment Required indica que o pagamento não foi autorizado
                throw new ResponseStatusException(
                        HttpStatus.PAYMENT_REQUIRED,
                        "Pagamento não autorizado pelo provedor de pagamento."
                );
            }

            compra.setStatusPagamento(PaymentStatus.PAGO);
            compra.setGatewayProvider("PAGBANK");
            compra.setGatewayOrderId(cardResult.transactionId());

            // Apenas informações não sensíveis para exibir no histórico
            compra.setCardBrand(request.cardBrand());
            compra.setCardLast4(request.cardLast4());

            compra.setCodigoValidacao(UUID.randomUUID().toString());
            compra.setUsada(Boolean.FALSE);
            compra.setUsadaEm(null);

            // Vamos enviar e-mail após salvar a compra
            enviarEmailAposSalvar = true;

        } else {
            // Fallback para outros métodos que venham a ser adicionados no futuro
            compra.setStatusPagamento(PaymentStatus.PAGO);
            compra.setCodigoValidacao(UUID.randomUUID().toString());
            compra.setUsada(Boolean.FALSE);
            compra.setUsadaEm(null);

            enviarEmailAposSalvar = true;
        }

        compra = compraFichaRepository.save(compra);

        // >>> Gamificação: registra pontos pela compra <<<
        // Para Pix, os pontos são dados apenas quando o webhook confirmar (status = PAGO).
        if (formaPagamento != PaymentMethod.PIX) {
            pontuacaoService.registrarCompra(cliente, quantidade, compra.getId());
        }

        // Envia e-mail somente para compras já confirmadas (PAGO)
        if (enviarEmailAposSalvar && compra.getStatusPagamento() == PaymentStatus.PAGO) {
            emailService.enviarEmailCompraConfirmada(compra);
        }

        return CompraFichaResponse.of(compra);
    }

    // ------------------------------------------------------------------------
    // LISTAGEM E RESUMO
    // ------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<CompraFichaResponse> listarComprasPorEmail(String email) {

        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado para o e-mail: " + email
                ));

        return compraFichaRepository.findByClienteIdOrderByCriadoEmDesc(cliente.getId())
                .stream()
                .map(CompraFichaResponse::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResumoComprasResponse resumoComprasPorMetodo(String email) {

        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado para o e-mail: " + email
                ));

        List<CompraFicha> compras = compraFichaRepository
                .findByClienteIdOrderByCriadoEmDesc(cliente.getId());

        Map<PaymentMethod, List<CompraFicha>> agrupadoPorMetodo = compras.stream()
                .collect(Collectors.groupingBy(CompraFicha::getFormaPagamento));

        List<PagamentoPorMetodoResumo> porMetodo = agrupadoPorMetodo.entrySet().stream()
                .map(entry -> {
                    PaymentMethod metodo = entry.getKey();
                    List<CompraFicha> lista = entry.getValue();

                    long qtd = lista.size();
                    BigDecimal total = lista.stream()
                            .map(CompraFicha::getValorTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new PagamentoPorMetodoResumo(metodo, qtd, total);
                })
                .toList();

        return new ResumoComprasResponse(cliente.getEmail(), porMetodo);
    }

    // ------------------------------------------------------------------------
    // QR CODE
    // ------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public byte[] gerarQrCodeCompra(Long id) {
        CompraFicha compra = compraFichaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Compra não encontrada"
                ));

        String payload = compra.getCodigoValidacao();
        return qrCodeService.generateQrCode(payload, 300, 300);
    }

    @Transactional
    public CompraFichaResponse validarQrCode(String codigo) {

        CompraFicha compra = compraFichaRepository.findByCodigoValidacao(codigo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "QR Code inválido ou não encontrado"));

        if (compra.getStatusPagamento() != PaymentStatus.PAGO) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Pagamento ainda não confirmado para esta ficha.");
        }

        if (Boolean.TRUE.equals(compra.getUsada())) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "QR Code já utilizado em: " + compra.getUsadaEm());
        }

        compra.setUsada(Boolean.TRUE);
        compra.setUsadaEm(LocalDateTime.now());
        compra = compraFichaRepository.save(compra);

        return CompraFichaResponse.of(compra);
    }

    // ------------------------------------------------------------------------
    // WEBHOOK PIX
    // ------------------------------------------------------------------------

    @Transactional
    public void processarWebhookPagBank(PagBankWebhookSimuladoRequest request) {

        // Exemplo de corpo:
        // { "orderId": "ORDE_xxx", "status": "PAID" }

        if (!"PAID".equalsIgnoreCase(request.status()) &&
            !"PAGO".equalsIgnoreCase(request.status())) {
            // Outros status, não faz nada
            return;
        }

        CompraFicha compra = compraFichaRepository.findByGatewayOrderId(request.orderId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Compra não encontrada para orderId do PagBank: " + request.orderId()
                ));

        // Se já estiver pago, não faz nada (idempotente)
        if (compra.getStatusPagamento() == PaymentStatus.PAGO) {
            return;
        }

        compra.setStatusPagamento(PaymentStatus.PAGO);
        compra = compraFichaRepository.save(compra);

        // >>> Gamificação: pontos na confirmação do Pix <<<
        pontuacaoService.registrarCompra(compra.getCliente(), compra.getQuantidade(), compra.getId());

        // Envia e-mail de confirmação na confirmação do Pix
        emailService.enviarEmailCompraConfirmada(compra);
    }
}
