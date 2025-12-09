package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.email.EmailService;
import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.repositories.ClienteRepository;
import com.ru.facil.ru_facil.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.RSocket.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthResource {

    private static final Logger logger = LoggerFactory.getLogger(AuthResource.class);

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public record LoginRequest(String email, String senha) {}

    public record LoginResponse(String token, Cliente cliente) {
        public LoginResponse {
            if (cliente != null) {
                cliente.setSenha(null);
            }
        }
    }
    // -----------------------------------------------

    @Operation(description = "Registra um novo usuário")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Cliente cliente) {
        logger.info("[AUTH] Tentativa de registro para email={}", cliente.getEmail());

        if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            logger.warn("[AUTH] Registro bloqueado: email já cadastrado={}", cliente.getEmail());
            return ResponseEntity.badRequest().body("E-mail já cadastrado");
        }

        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        clienteRepository.save(cliente);

        logger.info("[AUTH] Usuário registrado com sucesso, clienteId={}", cliente.getId());
        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }

    @Operation(description = "Efetua o login e retorna o Token JWT")
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest data) {
        logger.info("[AUTH] Tentativa de login para {}", data.email());

        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());

            var auth = this.authenticationManager.authenticate(usernamePassword);

            var cliente = clienteRepository.findByEmail(data.email())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado após autenticação (erro inesperado)"));

            var token = tokenService.generateToken(cliente);

            logger.info("[AUTH] Login sucesso. Token gerado para {}", data.email());
            return ResponseEntity.ok(new LoginResponse(token, cliente));

        } catch (Exception e) {
            logger.error("[AUTH] Erro no login: {}", e.getMessage());
            return ResponseEntity.status(401).body("Email ou senha inválidos");
        }
    }

    @Operation(description = "Solicita redefinição de senha (envia e-mail com token)")
    @PostMapping("/esqueci-senha")
    public ResponseEntity<String> esqueciSenha(@RequestParam String email) {
        logger.info("[AUTH] Solicitação de redefinição de senha para email={}", email);

        Optional<Cliente> opt = clienteRepository.findByEmail(email);
        if (opt.isEmpty()) {
            return ResponseEntity.ok("Se o e-mail existir, enviaremos instruções para redefinir a senha.");
        }

        Cliente cliente = opt.get();
        Instant agora = Instant.now();

        if (cliente.getResetToken() != null &&
                cliente.getResetTokenExpiraEm() != null &&
                cliente.getResetTokenExpiraEm().isAfter(agora)) {
            
            emailService.enviarEmailRedefinicaoSenha(cliente, cliente.getResetToken());
            return ResponseEntity.ok("Se o e-mail existir, enviaremos instruções para redefinir a senha.");
        }

        String token = UUID.randomUUID().toString();
        Instant expiraEm = agora.plus(30, ChronoUnit.MINUTES);

        cliente.setResetToken(token);
        cliente.setResetTokenExpiraEm(expiraEm);
        clienteRepository.save(cliente);

        emailService.enviarEmailRedefinicaoSenha(cliente, token);

        return ResponseEntity.ok("Se o e-mail existir, enviaremos instruções para redefinir a senha.");
    }

    @Operation(description = "Redefine a senha a partir de um token válido")
    @PostMapping("/redefinir-senha")
    public ResponseEntity<String> redefinirSenha(@RequestParam String token,
                                                 @RequestParam String novaSenha) {
        logger.info("[AUTH] Tentativa de redefinir senha com token={}", token);

        Optional<Cliente> opt = clienteRepository.findByResetToken(token);
        
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Token inválido.");
        }

        Cliente cliente = opt.get();

        if (cliente.getResetTokenExpiraEm() == null ||
                cliente.getResetTokenExpiraEm().isBefore(Instant.now())) {
            return ResponseEntity.badRequest().body("Token expirado.");
        }

        cliente.setSenha(passwordEncoder.encode(novaSenha));
        cliente.setResetToken(null);
        cliente.setResetTokenExpiraEm(null);
        clienteRepository.save(cliente);

        logger.info("[AUTH] Senha redefinida com sucesso para clienteId={}", cliente.getId());

        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }
}