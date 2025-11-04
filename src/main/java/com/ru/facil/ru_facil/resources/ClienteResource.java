package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/clientes")
public class ClienteResource {

    @Autowired
    private ClienteService service;
    @Operation(description = "retorna uma lista de todos os clientes")
    @GetMapping
    public ResponseEntity<List<Cliente>> findAll(){
        List<Cliente> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }
}
