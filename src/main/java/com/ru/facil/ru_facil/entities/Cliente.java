package com.ru.facil.ru_facil.entities;


import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "tb_cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // to usando essa anotação para chave primaria se autoincrementar
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private Boolean ehAluno;

    public Cliente(Long id, String nome, String email, String senha, Boolean ehAluno) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.ehAluno = ehAluno;
    }

    public Cliente() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getEhAluno() {
        return ehAluno;
    }

    public void setEhAluno(Boolean ehAluno) {
        this.ehAluno = ehAluno;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente cliente)) return false;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", ehAluno=" + ehAluno +
                '}';
    }
}
