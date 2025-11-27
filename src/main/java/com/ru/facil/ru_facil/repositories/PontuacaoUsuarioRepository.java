package com.ru.facil.ru_facil.repositories;

import com.ru.facil.ru_facil.entities.PontuacaoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PontuacaoUsuarioRepository extends JpaRepository<PontuacaoUsuario, Long> {

    Optional<PontuacaoUsuario> findByClienteId(Long clienteId);
}
