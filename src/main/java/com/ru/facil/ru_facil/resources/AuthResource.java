package com.ru.facil.ru_facil.resources;



import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.repositories.ClienteRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthResource {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
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

}
