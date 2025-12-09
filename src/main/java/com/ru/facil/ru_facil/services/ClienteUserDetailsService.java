package com.ru.facil.ru_facil.services;

import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
public class ClienteUserDetailsService implements UserDetailsService {
    @Autowired
    private ClienteRepository clienteRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Cliente cliente = clienteRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontraro" + email));

        return User.builder().username(cliente.getEmail()).password(cliente.getSenha()).roles("USER").build();
    }
}
