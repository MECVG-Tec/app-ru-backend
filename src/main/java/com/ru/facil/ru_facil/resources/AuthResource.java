package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.email.EmailService;
import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.repositories.ClienteRepository;
import io.swagger.v3.oas.annotations.Operation;
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

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Operation(description = "Registra um novo usuário")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Cliente cliente) {
        if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado");
        }
        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        clienteRepository.save(cliente);
        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }

    @Operation(description = "Efetua o login do usuário")
    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Login efetuado com sucesso!");
    }

    @Operation(description = "Solicita redefinição de senha (envia e-mail com token)")
    @PostMapping("/esqueci-senha")
    public ResponseEntity<String> esqueciSenha(@RequestParam String email) {
        Optional<Cliente> opt = clienteRepository.findByEmail(email);
        if (opt.isEmpty()) {
            // Para não revelar se o e-mail existe ou não, responde genérico
            return ResponseEntity.ok("Se o e-mail existir, enviaremos instruções para redefinir a senha.");
        }

        Cliente cliente = opt.get();

        String token = UUID.randomUUID().toString();
        Instant expiraEm = Instant.now().plus(30, ChronoUnit.MINUTES);

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
        Optional<Cliente> opt = clienteRepository.findByResetToken(token);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }

        Cliente cliente = opt.get();

        if (cliente.getResetTokenExpiraEm() == null ||
                cliente.getResetTokenExpiraEm().isBefore(Instant.now())) {
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }

        cliente.setSenha(passwordEncoder.encode(novaSenha));
        cliente.setResetToken(null);
        cliente.setResetTokenExpiraEm(null);
        clienteRepository.save(cliente);

        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }
}
