package com.ru.facil.ru_facil.suporte;

import com.ru.facil.ru_facil.entities.SuporteMensagem;
import com.ru.facil.ru_facil.enuns.SupportCategory;
import com.ru.facil.ru_facil.enuns.SupportStatus;
import com.ru.facil.ru_facil.repositories.SuporteMensagemRepository;
import com.ru.facil.ru_facil.resources.SuporteResource;
import com.ru.facil.ru_facil.suporte.dto.AtualizarStatusSuporteRequest;
import com.ru.facil.ru_facil.suporte.dto.SuporteRequest;
import com.ru.facil.ru_facil.suporte.dto.SuporteResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuporteResourceTest {

    @Mock
    private SuporteMensagemRepository suporteMensagemRepository;

    @InjectMocks
    private SuporteResource suporteResource;

    @Test
    @DisplayName("Deve criar uma mensagem de suporte com sucesso e status CREATED")
    void criarMensagem_DeveRetornarCreated() {
        SuporteRequest request = new SuporteRequest(
                "João Silva",
                "joao@email.com",
                SupportCategory.DUVIDA,
                "Problema no login",
                "Não consigo entrar"
        );

        SuporteMensagem mensagemSalva = new SuporteMensagem();
        mensagemSalva.setId(1L);
        mensagemSalva.setNome(request.nome());
        mensagemSalva.setEmail(request.email());
        mensagemSalva.setCategoria(request.categoria());
        mensagemSalva.setAssunto(request.assunto());
        mensagemSalva.setMensagem(request.mensagem());
        mensagemSalva.setStatus(SupportStatus.ABERTA);
        mensagemSalva.setCriadoEm(LocalDateTime.now());

        when(suporteMensagemRepository.save(any(SuporteMensagem.class))).thenReturn(mensagemSalva);

        ResponseEntity<SuporteResponse> response = suporteResource.criarMensagem(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().status()).isEqualTo(SupportStatus.ABERTA);
        
        verify(suporteMensagemRepository, times(1)).save(any(SuporteMensagem.class));
    }

    @Test
    @DisplayName("Deve listar todas as mensagens quando email não é informado")
    void listarMensagens_SemFiltro_DeveRetornarTodas() {
        SuporteMensagem m1 = new SuporteMensagem();
        m1.setId(1L);
        
        when(suporteMensagemRepository.findAllByOrderByCriadoEmDesc())
                .thenReturn(List.of(m1));

        List<SuporteResponse> resultado = suporteResource.listarMensagens(null);

        assertThat(resultado).hasSize(1);
        verify(suporteMensagemRepository, times(1)).findAllByOrderByCriadoEmDesc();
        verify(suporteMensagemRepository, never()).findByEmailOrderByCriadoEmDesc(anyString());
    }

    @Test
    @DisplayName("Deve listar mensagens filtradas quando email é informado")
    void listarMensagens_ComFiltroEmail_DeveRetornarFiltradas() {
        String email = "teste@email.com";
        SuporteMensagem m1 = new SuporteMensagem();
        m1.setEmail(email);

        when(suporteMensagemRepository.findByEmailOrderByCriadoEmDesc(email))
                .thenReturn(List.of(m1));

        List<SuporteResponse> resultado = suporteResource.listarMensagens(email);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).email()).isEqualTo(email);
        
        verify(suporteMensagemRepository, times(1)).findByEmailOrderByCriadoEmDesc(email);
        verify(suporteMensagemRepository, never()).findAllByOrderByCriadoEmDesc();
    }
    
    @Test
    @DisplayName("Deve retornar lista vazia se não encontrar mensagens")
    void listarMensagens_DeveRetornarVazio() {
        when(suporteMensagemRepository.findAllByOrderByCriadoEmDesc())
                .thenReturn(Collections.emptyList());

        List<SuporteResponse> resultado = suporteResource.listarMensagens(null);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar o status da mensagem com sucesso")
    void atualizarStatus_DeveAtualizarERetornarMensagem() {
        Long id = 10L;
        AtualizarStatusSuporteRequest request = new AtualizarStatusSuporteRequest(SupportStatus.FECHADA);
        
        SuporteMensagem mensagemExistente = new SuporteMensagem();
        mensagemExistente.setId(id);
        mensagemExistente.setStatus(SupportStatus.ABERTA);

        when(suporteMensagemRepository.findById(id)).thenReturn(Optional.of(mensagemExistente));
        
        when(suporteMensagemRepository.save(any(SuporteMensagem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SuporteResponse response = suporteResource.atualizarStatus(id, request);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.status()).isEqualTo(SupportStatus.FECHADA);
        
        verify(suporteMensagemRepository).save(mensagemExistente);
    }

    @Test
    @DisplayName("Deve lançar exception 404 quando tentar atualizar ID inexistente")
    void atualizarStatus_IdInexistente_DeveLancarException() {
        Long id = 99L;
        AtualizarStatusSuporteRequest request = new AtualizarStatusSuporteRequest(SupportStatus.FECHADA);

        when(suporteMensagemRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> suporteResource.atualizarStatus(id, request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status") 
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(suporteMensagemRepository, never()).save(any());
    }
}