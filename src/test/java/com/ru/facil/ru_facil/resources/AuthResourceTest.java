package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.email.EmailService;
import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.repositories.ClienteRepository;
import com.ru.facil.ru_facil.resources.AuthResource.LoginRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthResourceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthResource authResource;

    private Cliente clienteValido;

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        clienteValido = new Cliente();
        clienteValido.setId(1L);
        clienteValido.setEmail("teste@novo.com");
        clienteValido.setSenha("senhaSegura123");
        loginRequest = new LoginRequest("teste@novo.com", "senhaSegura123");
    }

    @Test
    void register_deveRetornarBadRequestQuandoEmailJaCadastrado() {
        when(clienteRepository.findByEmail(clienteValido.getEmail()))
                .thenReturn(Optional.of(new Cliente()));

        ResponseEntity<String> response = authResource.register(clienteValido);

        assertEquals(400, response.getStatusCodeValue(), "Deve retornar status 400 Bad Request.");
        assertEquals("E-mail já cadastrado", response.getBody());

        verify(passwordEncoder, never()).encode(anyString());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void login_deveRetornarMensagemDeSucessoFixo() {
        ResponseEntity response = authResource.login(loginRequest);

        assertEquals(200, response.getStatusCodeValue(), "Deve retornar status 200 OK.");
        assertEquals("Login efetuado com sucesso!", response.getBody());
    }

    @Test
    void esqueciSenha_deveGerarTokenEEnviarEmailQuandoEmailExiste() {
        when(clienteRepository.findByEmail(clienteValido.getEmail()))
                .thenReturn(Optional.of(clienteValido));

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);

        ResponseEntity<String> response = authResource.esqueciSenha(clienteValido.getEmail());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Se o e-mail existir, enviaremos instruções para redefinir a senha.", response.getBody());

        verify(clienteRepository).save(captor.capture());
        Cliente salvo = captor.getValue();

        assertNotNull(salvo.getResetToken(), "Token deve ser gerado");
        assertNotNull(salvo.getResetTokenExpiraEm(), "Data de expiração deve ser preenchida");

        verify(emailService).enviarEmailRedefinicaoSenha(eq(clienteValido), anyString());
    }

    @Test
    void redefinirSenha_deveRetornarBadRequestQuandoTokenInvalido() {
        when(clienteRepository.findByResetToken("TOKEN_INVALIDO"))
                .thenReturn(Optional.empty());

        ResponseEntity<String> response = authResource.redefinirSenha("TOKEN_INVALIDO", "novaSenha123");

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Token inválido ou expirado.", response.getBody());
    }

    @Test
    void redefinirSenha_deveAtualizarSenhaQuandoTokenValido() {
        clienteValido.setResetToken("TOKEN_OK");
        clienteValido.setResetTokenExpiraEm(Instant.now().plusSeconds(600));

        when(clienteRepository.findByResetToken("TOKEN_OK"))
                .thenReturn(Optional.of(clienteValido));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("HASH_NOVA_SENHA");

        ResponseEntity<String> response = authResource.redefinirSenha("TOKEN_OK", "novaSenha123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Senha redefinida com sucesso!", response.getBody());

        verify(passwordEncoder).encode("novaSenha123");
        verify(clienteRepository).save(clienteValido);

        // Depois da redefinição, token deve ser limpo
        assertEquals("HASH_NOVA_SENHA", clienteValido.getSenha());
        assertEquals(null, clienteValido.getResetToken());
        assertEquals(null, clienteValido.getResetTokenExpiraEm());
    }
}
