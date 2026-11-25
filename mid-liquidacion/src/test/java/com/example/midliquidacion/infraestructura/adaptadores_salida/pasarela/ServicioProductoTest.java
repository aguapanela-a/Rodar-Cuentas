package com.example.midliquidacion.infraestructura.adaptadores_salida.pasarela;

import com.stripe.StripeClient;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServicioProductoTest {
    private final ServicioProducto servicio = new ServicioProducto();
    private final StripeClient cliente = mock(StripeClient.class, RETURNS_DEEP_STUBS);

    @Test
    void creaPrecioEnUnidadesMenoresExactas() throws Exception {
        Product producto = new Product();
        producto.setId("prod_123");
        Price precio = new Price();
        when(cliente.v1().products().create(any(ProductCreateParams.class))).thenReturn(producto);
        when(cliente.v1().prices().create(any(PriceCreateParams.class))).thenReturn(precio);
        assertSame(precio, servicio.crearPrecio(cliente, new BigDecimal("1234.00")));
        ArgumentCaptor<PriceCreateParams> captor = ArgumentCaptor.forClass(PriceCreateParams.class);
        verify(cliente.v1().prices()).create(captor.capture());
        assertEquals("prod_123", captor.getValue().getProduct());
        assertEquals("COP", captor.getValue().getCurrency());
        assertEquals(123400L, captor.getValue().getUnitAmount());
    }

    @Test
    void rechazaImportesInvalidosAntesDeLlamarStripe() {
        for (BigDecimal valor : new BigDecimal[]{null, BigDecimal.ZERO, BigDecimal.valueOf(-1),
                new BigDecimal("1.01"), new BigDecimal("92233720368547759")}) {
            assertThrows(IllegalArgumentException.class, () -> servicio.crearPrecio(cliente, valor));
        }
        verifyNoInteractions(cliente);
    }
}