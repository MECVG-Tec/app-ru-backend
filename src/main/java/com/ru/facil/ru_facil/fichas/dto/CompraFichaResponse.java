package com.ru.facil.ru_facil.fichas.dto;

import com.ru.facil.ru_facil.entities.CompraFicha;
import com.ru.facil.ru_facil.enuns.PaymentMethod;
import com.ru.facil.ru_facil.enuns.PaymentStatus;
import com.ru.facil.ru_facil.enuns.TicketPriceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompraFichaResponse(
        Long id,
        Long clienteId,
        String clienteNome,
        Integer quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorTotal,
        TicketPriceType priceType,
        LocalDateTime criadoEm,
        String codigoValidacao,
        PaymentMethod formaPagamento,
        PaymentStatus statusPagamento,
        String pixQrCodeText,
        String pixQrCodeImageUrl,
        String gatewayProvider,
        String gatewayOrderId,
        String cardBrand,
        String cardLast4
) {

    public static CompraFichaResponse of(CompraFicha c) {

        String qrText = null;
        String qrUrl = null;

        if (c.getFormaPagamento() == PaymentMethod.PIX) {
            qrText = c.getGatewayQrCodeText();
            qrUrl = c.getGatewayQrCodeImageUrl();
        }

        return new CompraFichaResponse(
                c.getId(),
                c.getCliente().getId(),
                c.getCliente().getNome(),
                c.getQuantidade(),
                c.getValorUnitario(),
                c.getValorTotal(),
                c.getPriceType(),
                c.getCriadoEm(),
                c.getCodigoValidacao(),
                c.getFormaPagamento(),
                c.getStatusPagamento(),
                qrText,
                qrUrl,
                c.getGatewayProvider(),
                c.getGatewayOrderId(),
                c.getCardBrand(),
                c.getCardLast4()
        );
    }
}
