package com.ru.facil.ru_facil.email;

import com.ru.facil.ru_facil.email.impl.LoggingEmailService;
import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.entities.CompraFicha;
import com.ru.facil.ru_facil.enuns.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class LoggingEmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private final String from = "ru.facil2025@gmail.com";
    private final String frontendBaseUrl = "http://localhost:3000"; // TODO: ajustar quando o front existir

    private LoggingEmailService loggingEmailService;

    @BeforeEach
    void setUp() {
        loggingEmailService = new LoggingEmailService(mailSender, from, frontendBaseUrl);
    }

    @Test
    @DisplayName("Deve enviar email de confirmação de compra quando compra e cliente são válidos")
    void enviarEmailCompraConfirmada_sucesso() {
        Cliente cliente = new Cliente();
        cliente.setNome("João");
        cliente.setEmail("joao@example.com");

        CompraFicha compra = new CompraFicha();
        compra.setId(1L);
        compra.setCliente(cliente);
        compra.setQuantidade(3);
        compra.setValorTotal(BigDecimal.valueOf(15.50));
        compra.setFormaPagamento(PaymentMethod.PIX);
        compra.setCriadoEm(LocalDateTime.now());

        loggingEmailService.enviarEmailCompraConfirmada(compra);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getFrom()).isEqualTo(from);
        assertThat(message.getTo()).containsExactly("joao@example.com");
        assertThat(message.getSubject()).contains("Confirmação de compra");
        assertThat(message.getText()).contains("João");
    }

    @Test
    @DisplayName("Não deve enviar email quando compra ou cliente são nulos")
    void enviarEmailCompraConfirmada_compraOuClienteNulos() {
        loggingEmailService.enviarEmailCompraConfirmada(null);

        CompraFicha compraSemCliente = new CompraFicha();
        loggingEmailService.enviarEmailCompraConfirmada(compraSemCliente);

        verifyNoInteractions(mailSender);
    }
}
