package com.ru.facil.ru_facil.entities;

import com.ru.facil.ru_facil.enuns.PontosEventoTipo;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_historico_pontos")
public class HistoricoPontos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pontuacao_usuario_id", nullable = false)
    private PontuacaoUsuario pontuacaoUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 32)
    private PontosEventoTipo tipoEvento;

    @Column(nullable = false)
    private Integer pontos;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    public HistoricoPontos() {
    }

    @PrePersist
    public void prePersist() {
        if (this.criadoEm == null) {
            this.criadoEm = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PontuacaoUsuario getPontuacaoUsuario() {
        return pontuacaoUsuario;
    }

    public void setPontuacaoUsuario(PontuacaoUsuario pontuacaoUsuario) {
        this.pontuacaoUsuario = pontuacaoUsuario;
    }

    public PontosEventoTipo getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(PontosEventoTipo tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Integer getPontos() {
        return pontos;
    }

    public void setPontos(Integer pontos) {
        this.pontos = pontos;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
