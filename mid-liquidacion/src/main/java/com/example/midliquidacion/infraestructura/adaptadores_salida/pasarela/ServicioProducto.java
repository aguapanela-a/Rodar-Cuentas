package com.example.midliquidacion.infraestructura.adaptadores_salida.pasarela;

import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ServicioProducto {

    private Product crearProducto( StripeClient miCuentaStripe) throws StripeException {

        ProductCreateParams parametrosProducto =
                ProductCreateParams.builder()
                        .setName("Tu Viaje")
                        .setDescription("este es cobro de viaje")
                        .build();

        return miCuentaStripe.v1().products().create(parametrosProducto);
    }

    public Price crearPrecio( StripeClient miCuentaStripe, BigDecimal valor) throws StripeException {

        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("El importe debe ser positivo y entero en pesos COP");
        }
        long unidadesMenores;
        try {
            valor.toBigIntegerExact();
            unidadesMenores = valor.movePointRight(2).longValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("El importe debe ser entero y estar dentro del rango permitido", e);
        }
        Product miProducto = crearProducto(miCuentaStripe);

        PriceCreateParams parametrosPrecio = PriceCreateParams
                .builder()
                .setProduct(miProducto.getId())
                .setCurrency("COP")
                .setUnitAmount(unidadesMenores)
                .build();

        return miCuentaStripe.v1().prices().create(parametrosPrecio);
    }
}
