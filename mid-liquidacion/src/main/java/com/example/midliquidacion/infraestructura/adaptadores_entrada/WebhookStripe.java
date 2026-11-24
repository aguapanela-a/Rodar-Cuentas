package com.example.midliquidacion.infraestructura.adaptadores_entrada;

import com.example.midliquidacion.aplicacion.DTOs.MoviemientoSalidaDTO;
import com.example.midliquidacion.aplicacion.puertos.puertos_entrada.CrearMovimientoIn;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/webhook-stripe")
public class WebhookStripe {

    private final CrearMovimientoIn crearMovimientoIn;
    private final String webhookSecret;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public WebhookStripe(CrearMovimientoIn crearMovimientoIn,
                        @Value("${stripe.webhook.secret:${STRIPE_WEBHOOK_SECRET:}}") String webhookSecret) {
        this.crearMovimientoIn = crearMovimientoIn;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping
    public ResponseEntity<MoviemientoSalidaDTO> recibirWebhook(@RequestHeader(value = "Stripe-Signature", required = false) String stripeSignature,
                                                              @RequestBody String payload) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            throw new IllegalStateException("Falta la variable stripe.webhook.secret / STRIPE_WEBHOOK_SECRET");
        }
        if (stripeSignature == null || stripeSignature.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            Event event = Webhook.constructEvent(payload, stripeSignature, webhookSecret);
            if (!"checkout.session.completed".equals(event.getType())) {
                return ResponseEntity.ok().build();
            }

            JsonNode payloadJson = objectMapper.readTree(payload);
            JsonNode metadata = payloadJson.path("data").path("object").path("metadata");

            if (metadata == null || metadata.isMissingNode() || metadata.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Long usuarioId = Long.valueOf(metadata.path("usuarioId").asText("0"));
            BigDecimal monto = new BigDecimal(metadata.path("monto").asText("0"));

            return ResponseEntity.ok(
                    crearMovimientoIn.crearMovimiento(usuarioId, "recarga", "", monto)
            );
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo procesar el webhook de Stripe", e);
        }
    }
}
