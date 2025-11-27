package com.ru.facil.ru_facil.repositories;

import com.ru.facil.ru_facil.entities.CompraFicha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompraFichaRepository extends JpaRepository<CompraFicha, Long> {

    List<CompraFicha> findByClienteIdOrderByCriadoEmDesc(Long clienteId);

    Optional<CompraFicha> findByCodigoValidacao(String codigoValidacao);

    Optional<CompraFicha> findByGatewayOrderId(String gatewayOrderId);

}
