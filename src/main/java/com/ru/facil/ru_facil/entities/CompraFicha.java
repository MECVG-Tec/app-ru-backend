package com.ru.facil.ru_facil.entities;

import com.ru.facil.ru_facil.enuns.PaymentMethod;
import com.ru.facil.ru_facil.enuns.PaymentStatus;
import com.ru.facil.ru_facil.enuns.TicketPriceType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_compra_ficha")
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

    // -------- Pagamento digital --------

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 32)
    private PaymentMethod formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false, length = 32)
    private PaymentStatus statusPagamento = PaymentStatus.PENDENTE;

    // -------- QR Code / validação --------

    @Column(name = "codigo_validacao", nullable = false, unique = true, length = 64)
    private String codigoValidacao;

    @Column(name = "usada", nullable = false)
    private Boolean usada = Boolean.FALSE;

    @Column(name = "usada_em")
    private LocalDateTime usadaEm;

    public CompraFicha() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public TicketPriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(TicketPriceType priceType) {
        this.priceType = priceType;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public PaymentMethod getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(PaymentMethod formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public PaymentStatus getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(PaymentStatus statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public String getCodigoValidacao() {
        return codigoValidacao;
    }

    public void setCodigoValidacao(String codigoValidacao) {
        this.codigoValidacao = codigoValidacao;
    }

    public Boolean getUsada() {
        return usada;
    }

    public void setUsada(Boolean usada) {
        this.usada = usada;
    }

    public LocalDateTime getUsadaEm() {
        return usadaEm;
    }

    public void setUsadaEm(LocalDateTime usadaEm) {
        this.usadaEm = usadaEm;
    }
}
