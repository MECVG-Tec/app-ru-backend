package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.entities.CompraFicha;
import com.ru.facil.ru_facil.enuns.TicketPriceType;
import com.ru.facil.ru_facil.fichas.dto.CompraFichaRequest;
import com.ru.facil.ru_facil.fichas.dto.CompraFichaResponse;
import com.ru.facil.ru_facil.repositories.ClienteRepository;
import com.ru.facil.ru_facil.repositories.CompraFichaRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fichas")
public class CompraFichaResource {

    private final ClienteRepository clienteRepository;
    private final CompraFichaRepository compraFichaRepository;

    public CompraFichaResource(ClienteRepository clienteRepository,
                               CompraFichaRepository compraFichaRepository) {
        this.clienteRepository = clienteRepository;
        this.compraFichaRepository = compraFichaRepository;
    }

    @Operation(summary = "Realiza a compra de fichas informando o e-mail do cliente")
    @PostMapping("/compras")
    public ResponseEntity<CompraFichaResponse> comprarFichas(@Valid @RequestBody CompraFichaRequest request) {

        String email = request.email();

        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado para o e-mail: " + email
                ));

        int quantidade = request.quantidade();
        if (quantidade <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade deve ser maior que zero");
        }

        boolean aluno = Boolean.TRUE.equals(cliente.getEhAluno());
        boolean moradorResidencia = Boolean.TRUE.equals(cliente.getMoradorResidencia());

        // Se for aluno, exige matrícula preenchida
        if (aluno && (cliente.getMatricula() == null || cliente.getMatricula().isBlank())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Aluno precisa ter matrícula cadastrada para comprar fichas.");
        }

        TicketPriceType priceType;
        BigDecimal unitPrice;

        if (aluno && moradorResidencia) {
            priceType = TicketPriceType.MORADOR_RESIDENCIA;
            unitPrice = BigDecimal.ZERO;
        } else if (aluno) {
            priceType = TicketPriceType.ALUNO_UFRPE;
            unitPrice = new BigDecimal("3.00");
        } else {
            priceType = TicketPriceType.VISITANTE;
            unitPrice = new BigDecimal("20.00");
        }

        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantidade));

        CompraFicha compra = new CompraFicha();
        compra.setCliente(cliente);
        compra.setQuantidade(quantidade);
        compra.setValorUnitario(unitPrice);
        compra.setValorTotal(total);
        compra.setPriceType(priceType);
        compra.setCriadoEm(LocalDateTime.now());

        compra = compraFichaRepository.save(compra);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CompraFichaResponse.of(compra));
    }

    @Operation(summary = "Lista as compras de fichas de um cliente pelo e-mail")
    @GetMapping("/compras")
    public List<CompraFichaResponse> listarComprasPorEmail(@RequestParam String email) {

        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado para o e-mail: " + email
                ));

        return compraFichaRepository.findByClienteIdOrderByCriadoEmDesc(cliente.getId())
                .stream()
                .map(CompraFichaResponse::of)
                .toList();
    }
}
