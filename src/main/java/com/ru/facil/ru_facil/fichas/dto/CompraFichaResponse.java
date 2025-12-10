package com.ru.facil.ru_facil.fichas.dto;

import com.ru.facil.ru_facil.entities.CompraFicha;
import com.ru.facil.ru_facil.enuns.PaymentMethod;
import com.ru.facil.ru_facil.enuns.PaymentStatus;
import com.ru.facil.ru_facil.enuns.TicketPriceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record CompraFichaResponse(
        Long id,
        Long clienteId,
        String clienteNome,
        Integer quantidade,
        Integer quantidadeAlmoco,
        Integer quantidadeJantar,
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
        String cardLast4,
        String dataCompraFormatada,
        String formaPagamentoDescricao
) {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static CompraFichaResponse of(CompraFicha c) {

        String qrText = null;
        String qrUrl = null;

        if (c.getFormaPagamento() == PaymentMethod.PIX) {
            qrText = c.getGatewayQrCodeText();
            qrUrl = c.getGatewayQrCodeImageUrl();
        }

        String dataFormatada = null;
        if (c.getCriadoEm() != null) {
            dataFormatada = c.getCriadoEm()
                    .toLocalDate()
                    .format(DATE_FORMATTER);
        }

        String formaDesc = null;
        if (c.getFormaPagamento() != null) {
            formaDesc = switch (c.getFormaPagamento()) {
                case PIX -> "Pix";
                case CARTAO_CREDITO -> "Cartão de crédito";
                case CARTAO_DEBITO -> "Cartão de débito";
                case CARTEIRA_DIGITAL -> "Carteira digital";
                default -> c.getFormaPagamento().name();
            };
        }

        return new CompraFichaResponse(
                c.getId(),
                c.getCliente().getId(),
                c.getCliente().getNome(),
                c.getQuantidade(),
                c.getQuantidadeAlmoco(),
                c.getQuantidadeJantar(),
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
                c.getCardLast4(),
                dataFormatada,
                formaDesc
        );
    }
}
