package com.ru.facil.ru_facil.entities;

import com.ru.facil.ru_facil.enuns.TicketOperationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_ticket_extract")
public class TicketExtract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Cliente client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = true)
    private CompraFicha purchase;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 20)
    private TicketOperationType operationType;

    @Column(name = "lunch_amount", nullable = false)
    private Integer lunchAmount;

    @Column(name = "dinner_amount", nullable = false)
    private Integer dinnerAmount;

    @Column(name = "current_lunch_balance", nullable = false)
    private Integer currentLunchBalance;

    @Column(name = "current_dinner_balance", nullable = false)
    private Integer currentDinnerBalance;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "description")
    private String description;

    public TicketExtract() {
    }

    public TicketExtract(Cliente client, TicketOperationType operationType, Integer lunchAmount, Integer dinnerAmount,
            Integer currentLunchBalance, Integer currentDinnerBalance, String description, CompraFicha purchase) {
        this.client = client;
        this.operationType = operationType;
        this.lunchAmount = lunchAmount;
        this.dinnerAmount = dinnerAmount;
        this.currentLunchBalance = currentLunchBalance;
        this.currentDinnerBalance = currentDinnerBalance;
        this.description = description;
        this.purchase = purchase;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getClient() {
        return client;
    }

    public void setClient(Cliente client) {
        this.client = client;
    }

    public TicketOperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(TicketOperationType operationType) {
        this.operationType = operationType;
    }

    public CompraFicha getPurchase() {
        return purchase;
    }

    public void setPurchase(CompraFicha purchase) {
        this.purchase = purchase;
    }

    public Integer getLunchAmount() {
        return lunchAmount;
    }

    public void setLunchAmount(Integer lunchAmount) {
        this.lunchAmount = lunchAmount;
    }

    public Integer getDinnerAmount() {
        return dinnerAmount;
    }

    public void setDinnerAmount(Integer dinnerAmount) {
        this.dinnerAmount = dinnerAmount;
    }

    public Integer getCurrentLunchBalance() {
        return currentLunchBalance;
    }

    public void setCurrentLunchBalance(Integer currentLunchBalance) {
        this.currentLunchBalance = currentLunchBalance;
    }

    public Integer getCurrentDinnerBalance() {
        return currentDinnerBalance;
    }

    public void setCurrentDinnerBalance(Integer currentDinnerBalance) {
        this.currentDinnerBalance = currentDinnerBalance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}