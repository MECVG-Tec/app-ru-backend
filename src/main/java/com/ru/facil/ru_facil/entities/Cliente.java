package com.ru.facil.ru_facil.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "tb_cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    private String senha;

    @Column(name = "eh_aluno")
    private Boolean ehAluno;

    /**
     * Matrícula do aluno na UFRPE. Para visitantes pode ficar null.
     */
    private String matricula;

    /**
     * Indica se o aluno mora na residência/moradia estudantil.
     * Usado para aplicar gratuidade nas fichas.
     */
    @Column(name = "morador_residencia")
    private Boolean moradorResidencia;

    public Cliente() {
    }

    public Cliente(Long id, String nome, String email, String senha,
                   Boolean ehAluno, String matricula, Boolean moradorResidencia) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.ehAluno = ehAluno;
        this.matricula = matricula;
        this.moradorResidencia = moradorResidencia;
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

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Boolean getMoradorResidencia() {
        return moradorResidencia;
    }

    public void setMoradorResidencia(Boolean moradorResidencia) {
        this.moradorResidencia = moradorResidencia;
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
                ", ehAluno=" + ehAluno +
                ", matricula='" + matricula + '\'' +
                ", moradorResidencia=" + moradorResidencia +
                '}';
    }
}
