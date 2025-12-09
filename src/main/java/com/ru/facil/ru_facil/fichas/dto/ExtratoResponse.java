package com.ru.facil.ru_facil.fichas.dto;


import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import com.ru.facil.ru_facil.entities.TicketExtract;
import com.ru.facil.ru_facil.enuns.PaymentStatus;
import com.ru.facil.ru_facil.enuns.TicketOperationType;

public record ExtratoResponse(
    Long id,
    String tipoOperacao,
    String descricao,
    Integer qtdAlmoco,
    Integer qtdJantar,
    Integer saldoAlmocoResultante,
    Integer saldoJantarResultante,
    String dataHora,
    Long compraId,
    BigDecimal valorPago,
    PaymentStatus statusPagamento,
    String formaPagamento
) {
    public static ExtratoResponse fromEntity(TicketExtract entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        Long compraId = null;
        BigDecimal valorPago = null;
        String formaPagamento = null;
        PaymentStatus statusPagamento = null;


        if (entity.getPurchase() != null) {
            compraId = entity.getPurchase().getId();
            valorPago = entity.getPurchase().getValorTotal();
            formaPagamento = entity.getPurchase().getFormaPagamento().toString();
            statusPagamento = entity.getPurchase().getStatusPagamento();
        }

        return new ExtratoResponse(
            entity.getId(),
            traduzirOperacao(entity.getOperationType()),
            entity.getDescription(),
            entity.getLunchAmount(),
            entity.getDinnerAmount(),
            entity.getCurrentLunchBalance(),
            entity.getCurrentDinnerBalance(),
            entity.getCreatedAt().format(formatter),
            compraId,
            valorPago,
            statusPagamento,
            formaPagamento
        );
    }

    private static String traduzirOperacao(TicketOperationType type) {
        return switch (type) {
            case COMPRA -> "Compra";
            case CONSUMO -> "Consumo";
            case ESTORNO -> "Estorno";
        };
    }
}