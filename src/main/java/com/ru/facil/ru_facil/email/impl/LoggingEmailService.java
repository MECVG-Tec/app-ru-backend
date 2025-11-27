package com.ru.facil.ru_facil.email.impl;

import com.ru.facil.ru_facil.email.EmailService;
import com.ru.facil.ru_facil.entities.CompraFicha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class LoggingEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingEmailService.class);

    private final JavaMailSender mailSender;
    private final String from;

    public LoggingEmailService(JavaMailSender mailSender,
                               @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void enviarEmailCompraConfirmada(CompraFicha compra) {
        if (compra == null || compra.getCliente() == null) {
            logger.warn("[EMAIL] Não foi possível enviar e-mail: compra ou cliente nulos.");
            return;
        }

        var cliente = compra.getCliente();

        String to = cliente.getEmail();
        String assunto = "Confirmação de compra de fichas do Restaurante Universitário";
        String texto = String.format("""
                Olá, %s!

                Sua compra de fichas no Restaurante Universitário foi confirmada.

                Detalhes da compra:
                - ID da compra: %d
                - Quantidade de fichas: %d
                - Valor total: R$ %s
                - Forma de pagamento: %s
                - Data da compra: %s

                Guarde este e-mail como comprovante. Você também pode consultar suas compras pelo aplicativo RU Fácil.

                Atenciosamente,
                Equipe RU Fácil
                """,
                cliente.getNome(),
                compra.getId(),
                compra.getQuantidade(),
                compra.getValorTotal(),
                compra.getFormaPagamento(),
                compra.getCriadoEm()
        );

        logger.info("[EMAIL] Preparando envio de confirmação de compra para {}", to);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(assunto);
            message.setText(texto);

            mailSender.send(message);

            logger.info("[EMAIL] Confirmação de compra enviada para {}", to);
        } catch (Exception e) {
            logger.error("[EMAIL] Erro ao enviar e-mail para {}: {}", to, e.getMessage(), e);
        }
    }
}
