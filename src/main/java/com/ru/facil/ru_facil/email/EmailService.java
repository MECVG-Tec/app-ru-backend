package com.ru.facil.ru_facil.email;

import com.ru.facil.ru_facil.entities.CompraFicha;

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
}
