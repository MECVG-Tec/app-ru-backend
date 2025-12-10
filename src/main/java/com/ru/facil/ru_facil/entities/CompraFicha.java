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

    @Column(name = "qtd_almoco", nullable = false)
    private Integer quantidadeAlmoco = 0;

    @Column(name = "qtd_jantar", nullable = false)
    private Integer quantidadeJantar = 0;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 32)
    private PaymentMethod formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false, length = 32)
    private PaymentStatus statusPagamento = PaymentStatus.PENDENTE;

    // ... (Mantenha gatewayProvider, orderId, qrCode, cardBrand, etc...) ...
    @Column(name = "gateway_provider", length = 32)
    private String gatewayProvider;
    @Column(name = "gateway_order_id", length = 64)
    private String gatewayOrderId;
    @Column(name = "gateway_qrcode_text", columnDefinition = "TEXT")
    private String gatewayQrCodeText;
    @Column(name = "gateway_qrcode_image_url")
    private String gatewayQrCodeImageUrl;
    @Column(name = "codigo_validacao", nullable = false, unique = true, length = 64)
    private String codigoValidacao;
    @Column(name = "usada", nullable = false)
    private Boolean usada = Boolean.FALSE;
    @Column(name = "usada_em")
    private LocalDateTime usadaEm;
    @Column(name = "card_brand", length = 32)
    private String cardBrand;
    @Column(name = "card_last4", length = 4)
    private String cardLast4;

    // --- GETTERS E SETTERS NOVOS ---

    public Integer getQuantidadeAlmoco() {
        return quantidadeAlmoco;
    }

    public void setQuantidadeAlmoco(Integer quantidadeAlmoco) {
        this.quantidadeAlmoco = quantidadeAlmoco;
    }

    public Integer getQuantidadeJantar() {
        return quantidadeJantar;
    }

    public void setQuantidadeJantar(Integer quantidadeJantar) {
        this.quantidadeJantar = quantidadeJantar;
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

    public String getGatewayProvider() {
        return gatewayProvider;
    }

    public void setGatewayProvider(String gatewayProvider) {
        this.gatewayProvider = gatewayProvider;
    }

    public String getGatewayOrderId() {
        return gatewayOrderId;
    }

    public void setGatewayOrderId(String gatewayOrderId) {
        this.gatewayOrderId = gatewayOrderId;
    }

    public String getGatewayQrCodeText() {
        return gatewayQrCodeText;
    }

    public void setGatewayQrCodeText(String gatewayQrCodeText) {
        this.gatewayQrCodeText = gatewayQrCodeText;
    }

    public String getGatewayQrCodeImageUrl() {
        return gatewayQrCodeImageUrl;
    }

    public void setGatewayQrCodeImageUrl(String gatewayQrCodeImageUrl) {
        this.gatewayQrCodeImageUrl = gatewayQrCodeImageUrl;
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

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }
}