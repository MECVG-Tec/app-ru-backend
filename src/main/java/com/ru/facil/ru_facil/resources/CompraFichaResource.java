package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.entities.CompraFicha;
import com.ru.facil.ru_facil.enuns.PaymentMethod;
import com.ru.facil.ru_facil.enuns.PaymentStatus;
import com.ru.facil.ru_facil.enuns.TicketPriceType;
import com.ru.facil.ru_facil.fichas.dto.CompraFichaRequest;
import com.ru.facil.ru_facil.fichas.dto.CompraFichaResponse;
import com.ru.facil.ru_facil.pontuacao.PontuacaoService;
import com.ru.facil.ru_facil.qrcode.QrCodeService;
import com.ru.facil.ru_facil.repositories.ClienteRepository;
import com.ru.facil.ru_facil.repositories.CompraFichaRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fichas")
public class CompraFichaResource {

    private final ClienteRepository clienteRepository;
    private final CompraFichaRepository compraFichaRepository;
    private final QrCodeService qrCodeService;
    private final PontuacaoService pontuacaoService;

    public CompraFichaResource(ClienteRepository clienteRepository,
                               CompraFichaRepository compraFichaRepository,
                               QrCodeService qrCodeService,
                               PontuacaoService pontuacaoService) {
        this.clienteRepository = clienteRepository;
        this.compraFichaRepository = compraFichaRepository;
        this.qrCodeService = qrCodeService;
        this.pontuacaoService = pontuacaoService;
    }

    @Operation(summary = "Realiza a compra de fichas informando o e-mail do cliente e a forma de pagamento")
    @PostMapping("/compras")
    public ResponseEntity<CompraFichaResponse> comprarFichas(@Valid @RequestBody CompraFichaRequest request) {

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
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
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

        // Pagamento digital (simulado: sempre aprovado)
        compra.setFormaPagamento(formaPagamento);
        compra.setStatusPagamento(PaymentStatus.PAGO);

        // Gera código único para o QR de entrada no RU
        compra.setCodigoValidacao(UUID.randomUUID().toString());
        compra.setUsada(Boolean.FALSE);
        compra.setUsadaEm(null);

        compra = compraFichaRepository.save(compra);

        // >>> Gamificação: registra pontos pela compra <<<
        pontuacaoService.registrarCompra(cliente, quantidade, compra.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CompraFichaResponse.of(compra));
    }

    @Operation(summary = "Lista as compras de fichas de um cliente pelo e-mail")
    @GetMapping("/compras")
    public List<CompraFichaResponse> listarComprasPorEmail(@RequestParam String email) {

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

    @Operation(summary = "Retorna o QR Code (PNG) da compra de ficha")
    @GetMapping(value = "/compras/{id}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrCode(@PathVariable Long id) {
        CompraFicha compra = compraFichaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra não encontrada"));

        String payload = compra.getCodigoValidacao();

        byte[] image = qrCodeService.generateQrCode(payload, 300, 300);

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }

    @Operation(summary = "Valida um QR Code de ficha dentro do restaurante")
    @PostMapping("/compras/validar")
    public ResponseEntity<CompraFichaResponse> validarQrCode(@RequestParam("codigo") String codigo) {

        CompraFicha compra = compraFichaRepository.findByCodigoValidacao(codigo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "QR Code inválido ou não encontrado"
                ));

        if (compra.getStatusPagamento() != PaymentStatus.PAGO) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Pagamento ainda não confirmado para esta ficha."
            );
        }

        if (Boolean.TRUE.equals(compra.getUsada())) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "QR Code já utilizado em: " + compra.getUsadaEm()
            );
        }

        compra.setUsada(Boolean.TRUE);
        compra.setUsadaEm(LocalDateTime.now());
        compra = compraFichaRepository.save(compra);

        return ResponseEntity.ok(CompraFichaResponse.of(compra));
    }
}
