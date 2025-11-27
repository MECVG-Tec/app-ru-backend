package com.ru.facil.ru_facil.payments;

import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.payments.dto.PagBankPixResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.ru.facil.ru_facil.payments.dto.CardPaymentResult;


import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PagBankClient {

    @Value("${pagbank.base-url}")
    private String baseUrl;

    @Value("${pagbank.token:}")
    private String token;

    @Value("${pagbank.notification-url:}")
    private String notificationUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Cria um pedido Pix no PagBank e retorna os dados do QR Code.
     * Se o token não estiver configurado ou a chamada falhar (ex.: 401),
     * retorna um Pix simulado para uso em ambiente de desenvolvimento.
     *
     * @param cliente    cliente do RU
     * @param valor      valor total em reais
     * @param referencia referência interna (ex.: COMPRA-123)
     */
    public PagBankPixResponse criarPedidoPix(Cliente cliente, BigDecimal valor, String referencia) {

        // Se não tiver token configurado, simula direto
        if (token == null || token.isBlank() || token.startsWith("SEU_TOKEN_SANDBOX")) {
            System.out.println("[PagBankClient] Token não configurado. Usando modo SIMULADO.");
            return simularPix(cliente, valor, referencia, "SEM_TOKEN");
        }

        try {
            String url = baseUrl + "/orders";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("reference_id", referencia);

            // customer (dados mínimos para sandbox)
            Map<String, Object> customer = new HashMap<>();
            customer.put("name", cliente.getNome() != null ? cliente.getNome() : "Cliente RU");
            customer.put("email", cliente.getEmail());
            // CPF de teste (exemplo de CPF válido para sandbox)
            customer.put("tax_id", "12345678909");

            Map<String, Object> phone = new HashMap<>();
            phone.put("country", "55");
            phone.put("area", "81");
            phone.put("number", "999999999");
            phone.put("type", "MOBILE");
            customer.put("phones", List.of(phone));

            body.put("customer", customer);

            // item "Fichas RU"
            Map<String, Object> item = new HashMap<>();
            item.put("name", "Fichas Restaurante Universitário");
            item.put("quantity", 1);
            // valor em centavos
            item.put("unit_amount", valor.multiply(BigDecimal.valueOf(100)).intValue());

            body.put("items", List.of(item));

            // qr_codes
            Map<String, Object> qr = new HashMap<>();
            Map<String, Object> amount = new HashMap<>();
            amount.put("value", valor.multiply(BigDecimal.valueOf(100)).intValue());
            qr.put("amount", amount);

            // expiração em 24h
            OffsetDateTime exp = OffsetDateTime.now().plusHours(24);
            qr.put("expiration_date", exp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            body.put("qr_codes", List.of(qr));

            // webhook (notificações) – opcional no protótipo
            if (notificationUrl != null && !notificationUrl.isBlank()) {
                body.put("notification_urls", List.of(notificationUrl));
            }

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                System.out.println("[PagBankClient] Resposta não 2xx do PagBank: " + response.getStatusCode());
                return simularPix(cliente, valor, referencia, "STATUS_" + response.getStatusCode());
            }

            Map<String, Object> respBody = response.getBody();

            String orderId = (String) respBody.get("id");

            // pegar o primeiro qr_code
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) respBody.get("qr_codes");
            if (qrCodes == null || qrCodes.isEmpty()) {
                System.out.println("[PagBankClient] Resposta do PagBank sem qr_codes. Usando modo SIMULADO.");
                return simularPix(cliente, valor, referencia, "SEM_QRCODES");
            }

            Map<String, Object> qr0 = qrCodes.get(0);
            String qrCodeId = (String) qr0.get("id");
            String qrCodeText = (String) qr0.get("text");

            String qrCodeImageUrl = null;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> links = (List<Map<String, Object>>) qr0.get("links");
            if (links != null) {
                for (Map<String, Object> link : links) {
                    String rel = (String) link.get("rel");
                    String media = (String) link.get("media");
                    if ("QRCODE.PNG".equals(rel) && "image/png".equals(media)) {
                        qrCodeImageUrl = (String) link.get("href");
                        break;
                    }
                }
            }

            return new PagBankPixResponse(orderId, qrCodeId, qrCodeText, qrCodeImageUrl);

        } catch (HttpClientErrorException e) {
            System.out.println("[PagBankClient] Erro HTTP ao chamar PagBank: " + e.getStatusCode());
            // 401, 403, etc -> simula
            return simularPix(cliente, valor, referencia, "HTTP_" + e.getStatusCode());
        } catch (RestClientException e) {
            System.out.println("[PagBankClient] Erro de comunicação com PagBank: " + e.getMessage());
            return simularPix(cliente, valor, referencia, "EXCEPTION");
        }
    }

    private PagBankPixResponse simularPix(Cliente cliente, BigDecimal valor, String referencia, String motivo) {
        String orderId = "SIMULADO-" + UUID.randomUUID();
        String qrId = "SIMULADO-QR-" + UUID.randomUUID();
        String text = "PIX-SIMULADO-" + orderId;

        System.out.println("[PagBankClient] Gerando Pix SIMULADO. Motivo: " + motivo +
                " | referencia=" + referencia + " | orderId=" + orderId);

        // Não precisamos de imagem real no protótipo
        return new PagBankPixResponse(orderId, qrId, text, null);
    }

        /**
     * "Autoriza" um pagamento com cartão ou carteira digital.
     * No mundo real, aqui chamaríamos o endpoint de pagamento do provedor
     * passando o token do cartão/carteira. Neste projeto, simulamos:
     *
     * - Se o valor for maior que R$ 200,00, o pagamento é recusado (antifraude simples).
     * - Se o token começar com "FAIL", o pagamento também é recusado.
     * - Caso contrário, é autorizado.
     *
     * Nunca registramos ou logamos dados sensíveis do cartão.
     */
    public CardPaymentResult autorizarPagamentoCartao(String paymentToken,
                                                      BigDecimal valor,
                                                      String referencia) {

        boolean autorizado = true;

        // Regra antifraude simulada: valores muito altos são recusados
        if (valor.compareTo(BigDecimal.valueOf(200)) > 0) {
            autorizado = false;
        }

        // Regra de simulação: tokens que começam com "FAIL" são recusados
        if (paymentToken != null && paymentToken.toUpperCase().startsWith("FAIL")) {
            autorizado = false;
        }

        String transactionId = "CARD-" + UUID.randomUUID();

        return new CardPaymentResult(transactionId, autorizado);
    }

}
