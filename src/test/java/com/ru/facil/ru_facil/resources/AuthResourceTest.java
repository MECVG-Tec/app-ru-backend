package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.repositories.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AuthResourceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthResource authResource;

    private Cliente clienteValido;

    @BeforeEach
    void setUp() {
        clienteValido = new Cliente();
        clienteValido.setEmail("teste@novo.com");
        clienteValido.setSenha("senhaSegura123");
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
        ResponseEntity<String> response = authResource.login();

        assertEquals(200, response.getStatusCodeValue(), "Deve retornar status 200 OK.");
        assertEquals("Login efetuado com sucesso!", response.getBody());
    }
}