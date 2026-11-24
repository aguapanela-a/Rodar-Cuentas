package com.example.midliquidacion.infraestructura.adaptadores_salida;

import com.example.midliquidacion.aplicacion.puertos.puertos_salida.PasarelaPagosOut;
import com.example.midliquidacion.dominio.modelos.ResultadoPago;
import com.example.midliquidacion.infraestructura.adaptadores_salida.pasarela.ServicioProducto;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PasarelaPagosOutImpl implements PasarelaPagosOut {

    private final ServicioProducto servicioProducto;
    private final String apiKey;
    private final String urlExito;
    private final String urlCancelacion;

    public PasarelaPagosOutImpl(ServicioProducto servicioProducto,
                               @Value("${stripie.secretkey}") String apiKey,
                               @Value("${stripe.checkout.success-url}") String urlExito,
                               @Value("${stripe.checkout.cancel-url}") String urlCancelacion) {
        this.servicioProducto = servicioProducto;
        this.apiKey = apiKey;
        this.urlExito = urlExito;
        this.urlCancelacion = urlCancelacion;
    }


    @Override
    public ResultadoPago procesarPago(Long usuarioId, BigDecimal valor) {
        try {
            StripeClient cliente = new StripeClient(apiKey);
            Price precio = servicioProducto.crearPrecio(cliente, valor);
            SessionCreateParams parametros = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(urlExito)
                    .setCancelUrl(urlCancelacion)
                    .putMetadata("usuarioId", usuarioId.toString())
                    .putMetadata("monto", valor.toString())
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setPrice(precio.getId())
                            .setQuantity(1L)
                            .build())
                    .build();
            Session sesion = cliente.v1().checkout().sessions().create(parametros);
            String url = sesion.getUrl();
            return new ResultadoPago(url != null && !url.isBlank(), url);
        } catch (StripeException e) {
            throw new IllegalStateException("No se pudo crear la sesión de pago", e);
        }
    }
}
