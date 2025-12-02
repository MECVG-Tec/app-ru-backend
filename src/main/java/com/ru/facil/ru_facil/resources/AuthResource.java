package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.email.EmailService;
import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.repositories.ClienteRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Operation(description = "Efetua o login do usuário")
    @PostMapping("/login")
    public ResponseEntity<String> login() {
        // Autenticação em si é tratada pelo Spring Security (filtro), aqui só resposta simbólica
        logger.info("[AUTH] Login efetuado com sucesso (via Spring Security)");
        return ResponseEntity.ok("Login efetuado com sucesso!");
    }

    @Operation(description = "Solicita redefinição de senha (envia e-mail com token)")
    @PostMapping("/esqueci-senha")
    public ResponseEntity<String> esqueciSenha(@RequestParam String email) {
        logger.info("[AUTH] Solicitação de redefinição de senha para email={}", email);

        Optional<Cliente> opt = clienteRepository.findByEmail(email);
        if (opt.isEmpty()) {
            logger.warn("[AUTH] Pedido de redefinição para email não cadastrado={}", email);
            return ResponseEntity.ok("Se o e-mail existir, enviaremos instruções para redefinir a senha.");
        }

        Cliente cliente = opt.get();
        Instant agora = Instant.now();

        // Se já existe token ainda válido, não gera outro, apenas reenvia
        if (cliente.getResetToken() != null &&
                cliente.getResetTokenExpiraEm() != null &&
                cliente.getResetTokenExpiraEm().isAfter(agora)) {

            logger.info("[AUTH] Já existe token válido para clienteId={}, expiraEm={}",
                    cliente.getId(), cliente.getResetTokenExpiraEm());

            emailService.enviarEmailRedefinicaoSenha(cliente, cliente.getResetToken());

            return ResponseEntity.ok("Se o e-mail existir, enviaremos instruções para redefinir a senha.");
        }

        // Caso não exista token ou já esteja expirado, gera um novo
        String token = UUID.randomUUID().toString();
        Instant expiraEm = agora.plus(30, ChronoUnit.MINUTES);

        cliente.setResetToken(token);
        cliente.setResetTokenExpiraEm(expiraEm);
        clienteRepository.save(cliente);

        logger.info("[AUTH] Novo token de redefinição gerado para clienteId={}, expiraEm={}",
                cliente.getId(), expiraEm);

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
            logger.warn("[AUTH] Token inválido na redefinição de senha");
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }

        Cliente cliente = opt.get();

        if (cliente.getResetTokenExpiraEm() == null ||
                cliente.getResetTokenExpiraEm().isBefore(Instant.now())) {
            logger.warn("[AUTH] Token expirado para clienteId={}", cliente.getId());
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }

        cliente.setSenha(passwordEncoder.encode(novaSenha));
        cliente.setResetToken(null);
        cliente.setResetTokenExpiraEm(null);
        clienteRepository.save(cliente);

        logger.info("[AUTH] Senha redefinida com sucesso para clienteId={}", cliente.getId());

        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }
}
