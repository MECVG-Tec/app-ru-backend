package com.ru.facil.ru_facil.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ru.facil.ru_facil.entities.TicketExtract;

import java.util.List;

public interface TicketExtractRepository extends JpaRepository<TicketExtract, Long> {
    List<TicketExtract> findByClientIdOrderByCreatedAtDesc(Long clientId);
}