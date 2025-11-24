package com.ru.facil.ru_facil.entities;

import com.ru.facil.ru_facil.enuns.TicketPriceType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_compra_ficha")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraFicha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_preco", nullable = false, length = 32)
    private TicketPriceType priceType;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;
}
