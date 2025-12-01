package com.ru.facil.ru_facil.repositories;

import com.ru.facil.ru_facil.entities.Cliente;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
class ClienteRepositoryTest {
    @Autowired
    EntityManager entityManager;
    @Autowired
    ClienteRepository clienteRepository;
    @Test
    @DisplayName("Deve retornar um cliente ao buscar por email existente")
    void findByEmailSuccess() {
        String email = "Joao@gmail.com";
        Cliente cliente = new Cliente(null, "Joao", email, "123456", true, "2021001", false);
        createCliente(cliente);

        Optional<Cliente> resultado = clienteRepository.findByEmail(email);
        assertThat(resultado.isPresent()).isTrue();
    }

    private Cliente createCliente(Cliente cliente) {
        Cliente novoCliente = new Cliente(cliente.getId(), cliente.getNome(), cliente.getEmail(), cliente.getSenha(), cliente.getEhAluno(), cliente.getMatricula(), cliente.getMoradorResidencia());
        this.entityManager.persist(novoCliente);
        return cliente;
    }
}