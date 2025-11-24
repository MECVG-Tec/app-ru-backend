package com.ru.facil.ru_facil.fichas.dto;

import com.ru.facil.ru_facil.entities.CompraFicha;
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
        String codigoValidacao
) {

    public static CompraFichaResponse of(CompraFicha c) {
        return new CompraFichaResponse(
                c.getId(),
                c.getCliente().getId(),
                c.getCliente().getNome(),
                c.getQuantidade(),
                c.getValorUnitario(),
                c.getValorTotal(),
                c.getPriceType(),
                c.getCriadoEm(),
                c.getCodigoValidacao()
        );
    }
}
