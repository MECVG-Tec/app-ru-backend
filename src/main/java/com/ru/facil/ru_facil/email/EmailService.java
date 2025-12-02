package com.ru.facil.ru_facil.email;

import com.ru.facil.ru_facil.entities.CompraFicha;
import com.ru.facil.ru_facil.entities.Cliente;

/**
 * Serviço de e-mail para envio de notificações transacionais.
 */
public interface EmailService {

    /**
     * Envia um e-mail de confirmação de compra de fichas.
     *
     * @param compra compra de fichas já confirmada (status PAGO)
     */
    void enviarEmailCompraConfirmada(CompraFicha compra);

    /**
     * Envia um e-mail com o link de redefinição de senha para o cliente.
     *
     * @param cliente cliente que solicitou redefinição
     * @param token   token de redefinição de senha (já gerado e salvo no banco)
     */
    void enviarEmailRedefinicaoSenha(Cliente cliente, String token);
}
