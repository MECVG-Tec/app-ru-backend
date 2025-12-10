package com.ru.facil.ru_facil.fichas;

import com.ru.facil.ru_facil.email.EmailService;
import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.entities.CompraFicha;
import com.ru.facil.ru_facil.enuns.PaymentMethod;
import com.ru.facil.ru_facil.enuns.PaymentStatus;
import com.ru.facil.ru_facil.enuns.TicketPriceType;
import com.ru.facil.ru_facil.fichas.dto.CompraFichaRequest;
import com.ru.facil.ru_facil.payments.PagBankClient;
import com.ru.facil.ru_facil.payments.dto.CardPaymentResult;
import com.ru.facil.ru_facil.pontuacao.PontuacaoService;
import com.ru.facil.ru_facil.qrcode.QrCodeService;
import com.ru.facil.ru_facil.repositories.ClienteRepository;
import com.ru.facil.ru_facil.repositories.CompraFichaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraFichaServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CompraFichaRepository compraFichaRepository;

    @Mock
    private QrCodeService qrCodeService;

    @Mock
    private PontuacaoService pontuacaoService;

    @Mock
    private PagBankClient pagBankClient;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private CompraFichaService compraFichaService;

    private Cliente buildAlunoNaoMorador() {
        Cliente c = new Cliente();
        c.setId(1L);
        c.setEmail("aluno@ufrpe.br");
        c.setNome("Aluno");
        c.setEhAluno(true);
        c.setMoradorResidencia(false);
        c.setMatricula("20240001");
        return c;
    }

    @Test
    @DisplayName("Deve comprar fichas com cartão quando pagamento autorizado, registrar pontos e enviar email")
    void comprarFichas_cartaoSucesso() {
        Cliente cliente = buildAlunoNaoMorador();

        when(clienteRepository.findByEmail(cliente.getEmail()))
                .thenReturn(Optional.of(cliente));

        // pagamento autorizado pelo PagBank
        when(pagBankClient.autorizarPagamentoCartao(anyString(), any(BigDecimal.class), anyString()))
                .thenReturn(new CardPaymentResult("CARD-123", true));

        // simula salvar compra retornando a própria entidade com id preenchido
        when(compraFichaRepository.save(any(CompraFicha.class)))
                .thenAnswer(invocation -> {
                    CompraFicha c = invocation.getArgument(0);
                    c.setId(10L);
                    return c;
                });

        CompraFichaRequest request = new CompraFichaRequest(
                cliente.getEmail(),
                2,                         // quantidade
                PaymentMethod.CARTAO_CREDITO,
                "TOKEN-OK",                // paymentToken
                "VISA",                    // cardBrand
                "1234"                     // cardLast4
        );

        var response = compraFichaService.comprarFichas(request);

        ArgumentCaptor<CompraFicha> captor = ArgumentCaptor.forClass(CompraFicha.class);
        verify(compraFichaRepository, atLeastOnce()).save(captor.capture());
        CompraFicha salva = captor.getValue();

        // preço de aluno: 3.00 * 2 = 6.00
        assertThat(salva.getPriceType()).isEqualTo(TicketPriceType.ALUNO_UFRPE);
        assertThat(salva.getValorUnitario()).isEqualByComparingTo("3.00");
        assertThat(salva.getValorTotal()).isEqualByComparingTo("6.00");
        assertThat(salva.getStatusPagamento()).isEqualTo(PaymentStatus.PAGO);
        assertThat(salva.getGatewayOrderId()).isEqualTo("CARD-123");
        assertThat(salva.getCardBrand()).isEqualTo("VISA");
        assertThat(salva.getCardLast4()).isEqualTo("1234");

        // pontos registrados e email enviado
        verify(pontuacaoService).registrarCompra(cliente, 2, 10L);
        verify(emailService).enviarEmailCompraConfirmada(salva);

        // resposta consistente
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.statusPagamento()).isEqualTo(PaymentStatus.PAGO);
    }

    @Test
    @DisplayName("Deve lançar erro 402 quando pagamento com cartão não for autorizado")
    void comprarFichas_cartaoNaoAutorizado() {
        Cliente cliente = buildAlunoNaoMorador();

        when(clienteRepository.findByEmail(cliente.getEmail()))
                .thenReturn(Optional.of(cliente));

        when(pagBankClient.autorizarPagamentoCartao(anyString(), any(BigDecimal.class), anyString()))
                .thenReturn(new CardPaymentResult("CARD-FAIL", false));

        CompraFichaRequest request = new CompraFichaRequest(
                cliente.getEmail(),
                1,
                PaymentMethod.CARTAO_CREDITO,
                "FAIL-XYZ",
                "VISA",
                "0000"
        );

        assertThatThrownBy(() -> compraFichaService.comprarFichas(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Pagamento não autorizado");

        // não salva compra, não pontua nem envia email
        verify(compraFichaRepository, never()).save(any());
        verify(pontuacaoService, never()).registrarCompra(any(), anyInt(), anyLong());
        verify(emailService, never()).enviarEmailCompraConfirmada(any());
    }
}
