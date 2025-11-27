package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.fichas.CompraFichaService;
import com.ru.facil.ru_facil.fichas.dto.CompraFichaRequest;
import com.ru.facil.ru_facil.fichas.dto.CompraFichaResponse;
import com.ru.facil.ru_facil.fichas.dto.ResumoComprasResponse;
import com.ru.facil.ru_facil.payments.dto.PagBankWebhookSimuladoRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fichas")
public class CompraFichaResource {

    private final CompraFichaService compraFichaService;

    public CompraFichaResource(CompraFichaService compraFichaService) {
        this.compraFichaService = compraFichaService;
    }

    @Operation(summary = "Realiza a compra de fichas informando o e-mail do cliente e a forma de pagamento")
    @PostMapping("/compras")
    public ResponseEntity<CompraFichaResponse> comprarFichas(
            @Valid @RequestBody CompraFichaRequest request) {

        CompraFichaResponse response = compraFichaService.comprarFichas(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Lista as compras de fichas de um cliente pelo e-mail")
    @GetMapping("/compras")
    public List<CompraFichaResponse> listarComprasPorEmail(@RequestParam String email) {
        return compraFichaService.listarComprasPorEmail(email);
    }

    @Operation(summary = "Resumo das compras de fichas por forma de pagamento (para gráficos)")
    @GetMapping("/compras/resumo")
    public ResumoComprasResponse resumoComprasPorMetodo(@RequestParam String email) {
        return compraFichaService.resumoComprasPorMetodo(email);
    }

    @Operation(summary = "Retorna o QR Code (PNG) da compra de ficha")
    @GetMapping(value = "/compras/{id}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrCode(@PathVariable Long id) {

        byte[] image = compraFichaService.gerarQrCodeCompra(id);

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }

    @Operation(summary = "Valida um QR Code de ficha dentro do restaurante")
    @PostMapping("/compras/validar")
    public ResponseEntity<CompraFichaResponse> validarQrCode(@RequestParam("codigo") String codigo) {

        CompraFichaResponse response = compraFichaService.validarQrCode(codigo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Webhook simulado do PagBank para confirmação de pagamento Pix")
    @PostMapping("/pagamentos/webhook/pagbank-simulado")
    public ResponseEntity<Void> webhookPagBankSimulado(
            @Valid @RequestBody PagBankWebhookSimuladoRequest request) {

        compraFichaService.processarWebhookPagBank(request);
        return ResponseEntity.ok().build();
    }
}
