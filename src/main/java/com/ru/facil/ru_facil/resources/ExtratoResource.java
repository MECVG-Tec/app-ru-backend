package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.fichas.CompraFichaService;
import com.ru.facil.ru_facil.fichas.dto.ExtratoResponse;
import com.ru.facil.ru_facil.fichas.dto.SaldoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/extrato")
@Tag(name = "Extrato e Saldo", description = "Endpoints para consulta de saldo de fichas e histórico de movimentações")
public class ExtratoResource {

    private final CompraFichaService compraFichaService;

    public ExtratoResource(CompraFichaService compraFichaService) {
        this.compraFichaService = compraFichaService;
    }

    @Operation(summary = "Retorna o saldo atual de fichas (Almoço e Jantar) do cliente")
    @GetMapping("/saldo")
    public ResponseEntity<SaldoResponse> getSaldo(@RequestParam String email) {
        
        SaldoResponse saldo = compraFichaService.consultarSaldo(email);
        
        return ResponseEntity.ok(saldo);
    }

    @Operation(summary = "Retorna o histórico completo de movimentações (Entradas e Saídas)")
    @GetMapping("/historico")
    public ResponseEntity<List<ExtratoResponse>> getHistorico(@RequestParam String email) {
        
        List<ExtratoResponse> extrato = compraFichaService.consultarExtrato(email);
        
        return ResponseEntity.ok(extrato);
    }
}