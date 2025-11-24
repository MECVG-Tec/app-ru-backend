package com.ru.facil.ru_facil.repositories;

import com.ru.facil.ru_facil.entities.HistoricoPontos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoPontosRepository extends JpaRepository<HistoricoPontos, Long> {

    List<HistoricoPontos> findByPontuacaoUsuarioIdOrderByCriadoEmDesc(Long pontuacaoUsuarioId);
}
