package com.ru.facil.ru_facil.repositories;

import com.ru.facil.ru_facil.entities.SuporteMensagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuporteMensagemRepository extends JpaRepository<SuporteMensagem, Long> {

    List<SuporteMensagem> findByEmailOrderByCriadoEmDesc(String email);

    List<SuporteMensagem> findAllByOrderByCriadoEmDesc();
}
