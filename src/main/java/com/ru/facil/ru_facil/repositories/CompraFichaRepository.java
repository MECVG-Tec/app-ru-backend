package com.ru.facil.ru_facil.repositories;

import com.ru.facil.ru_facil.entities.CompraFicha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraFichaRepository extends JpaRepository<CompraFicha, Long> {
    List<CompraFicha> findByClienteIdOrderByCriadoEmDesc(Long clienteId);
}
