package com.example.midliquidacion.infraestructura.adaptadores_salida.pasarela;

import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ServicioProducto {

    @Value("${stripie.secretkey}")
    private String apiKey;

    private Product crearProducto( StripeClient miCuentaStripe) throws StripeException {

        ProductCreateParams parametrosProducto =
                ProductCreateParams.builder()
                        .setName("Tu Viaje")
                        .setDescription("este es cobro de viaje")
                        .build();

        return miCuentaStripe.v1().products().create(parametrosProducto);
    }

    public Price crearPrecio( StripeClient miCuentaStripe, BigDecimal valor) throws StripeException {

        Product miProducto = crearProducto(miCuentaStripe);

        PriceCreateParams parametrosPrecio = PriceCreateParams
                .builder()
                .setProduct(miProducto.getId())
                .setCurrency("COP")
                .setUnitAmount(valor.longValue())
                .build();

        return miCuentaStripe.v1().prices().create(parametrosPrecio);
    }
}
