package com.ru.facil.ru_facil.fichas;

import com.ru.facil.ru_facil.email.EmailService;
import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.entities.CompraFicha;
import com.ru.facil.ru_facil.enuns.PaymentStatus;
import com.ru.facil.ru_facil.pontuacao.PontuacaoService;
import com.ru.facil.ru_facil.qrcode.QrCodeService;
import com.ru.facil.ru_facil.repositories.ClienteRepository;
import com.ru.facil.ru_facil.repositories.CompraFichaRepository;
import com.ru.facil.ru_facil.payments.PagBankClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraFichaServiceQrCodeTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CompraFichaRepository compraFichaRepository;

    @Mock
    private QrCodeService qrCodeService;

    @Mock
    private PontuacaoService pontuacaoService;

    @Mock
    private PagBankClient pagBankClient;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private CompraFichaService compraFichaService;

    @Test
    @DisplayName("Deve gerar QR Code a partir do código de validação da compra")
    void gerarQrCodeCompra_sucesso() {
        CompraFicha compra = new CompraFicha();
        compra.setId(1L);
        compra.setCodigoValidacao("VALID-CODE-123");

        when(compraFichaRepository.findById(1L))
                .thenReturn(Optional.of(compra));

        byte[] qrBytesMock = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCode("VALID-CODE-123", 300, 300))
                .thenReturn(qrBytesMock);

        byte[] result = compraFichaService.gerarQrCodeCompra(1L);

        assertThat(result).isEqualTo(qrBytesMock);
        verify(qrCodeService).generateQrCode("VALID-CODE-123", 300, 300);
    }

    @Test
    @DisplayName("validarQrCode deve marcar ficha como usada quando pago e ainda não usada")
    void validarQrCode_sucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(10L);

        CompraFicha compra = new CompraFicha();
        compra.setId(1L);
        compra.setCliente(cliente);
        compra.setCodigoValidacao("QR-OK");
        compra.setStatusPagamento(PaymentStatus.PAGO);
        compra.setUsada(false);
        compra.setUsadaEm(null);

        when(compraFichaRepository.findByCodigoValidacao("QR-OK"))
                .thenReturn(Optional.of(compra));
        when(compraFichaRepository.save(any(CompraFicha.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = compraFichaService.validarQrCode("QR-OK");

        ArgumentCaptor<CompraFicha> captor = ArgumentCaptor.forClass(CompraFicha.class);
        verify(compraFichaRepository).save(captor.capture());
        CompraFicha salva = captor.getValue();

        assertThat(salva.getUsada()).isTrue();
        assertThat(salva.getUsadaEm()).isNotNull();
        // resposta já foi criada, aqui só garantimos que não houve exceção
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("validarQrCode deve lançar 422 quando pagamento ainda não estiver confirmado")
    void validarQrCode_pagamentoNaoConfirmado() {
        CompraFicha compra = new CompraFicha();
        compra.setCodigoValidacao("QR-PENDENTE");
        compra.setStatusPagamento(PaymentStatus.PENDENTE);
        compra.setUsada(false);

        when(compraFichaRepository.findByCodigoValidacao("QR-PENDENTE"))
                .thenReturn(Optional.of(compra));

        assertThatThrownBy(() -> compraFichaService.validarQrCode("QR-PENDENTE"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));

        verify(compraFichaRepository, never()).save(any());
    }

    @Test
    @DisplayName("validarQrCode deve lançar 422 quando QR Code já tiver sido utilizado")
    void validarQrCode_jaUtilizado() {
        CompraFicha compra = new CompraFicha();
        compra.setCodigoValidacao("QR-USADO");
        compra.setStatusPagamento(PaymentStatus.PAGO);
        compra.setUsada(true);
        compra.setUsadaEm(LocalDateTime.now().minusMinutes(5));

        when(compraFichaRepository.findByCodigoValidacao("QR-USADO"))
                .thenReturn(Optional.of(compra));

        assertThatThrownBy(() -> compraFichaService.validarQrCode("QR-USADO"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));

        verify(compraFichaRepository, never()).save(any());
    }
}
