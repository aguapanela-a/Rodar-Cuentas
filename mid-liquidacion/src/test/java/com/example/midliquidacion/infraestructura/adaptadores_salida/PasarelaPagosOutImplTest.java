package com.example.midliquidacion.infraestructura.adaptadores_salida;

import com.example.midliquidacion.dominio.modelos.ResultadoPago;
import com.example.midliquidacion.infraestructura.adaptadores_salida.pasarela.ServicioProducto;
import com.stripe.StripeClient;
import com.stripe.exception.ApiException;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PasarelaPagosOutImplTest {
    private final ServicioProducto productos = mock(ServicioProducto.class);
    private final PasarelaPagosOutImpl pasarela = new PasarelaPagosOutImpl(productos, "sk_test_ficticia",
            "https://example.com/exito", "https://example.com/cancelacion");

    @Test
    void creaSesionConPrecioUsuarioYUrls() throws Exception {
        Price precio = new Price();
        precio.setId("price_123");
        Session sesion = new Session();
        sesion.setUrl("https://checkout.example/sesion");
        when(productos.crearPrecio(any(), eq(new BigDecimal("1000")))).thenReturn(precio);
        try (MockedConstruction<StripeClient> clientes = mockConstruction(StripeClient.class,
                withSettings().defaultAnswer(RETURNS_DEEP_STUBS), (cliente, contexto) ->
                        when(cliente.v1().checkout().sessions().create(any(SessionCreateParams.class))).thenReturn(sesion))) {
            ResultadoPago resultado = pasarela.procesarPago(42L, new BigDecimal("1000"));
            assertTrue(resultado.isExitoso());
            assertEquals("https://checkout.example/sesion", resultado.getUrl());
            StripeClient cliente = clientes.constructed().getFirst();
            verify(productos).crearPrecio(cliente, new BigDecimal("1000"));
            ArgumentCaptor<SessionCreateParams> captor = ArgumentCaptor.forClass(SessionCreateParams.class);
            verify(cliente.v1().checkout().sessions()).create(captor.capture());
            SessionCreateParams params = captor.getValue();
            assertEquals(SessionCreateParams.Mode.PAYMENT, params.getMode());
            assertEquals("https://example.com/exito", params.getSuccessUrl());
            assertEquals("https://example.com/cancelacion", params.getCancelUrl());
            assertEquals("42", params.getMetadata().get("usuarioId"));
            assertEquals(1, params.getLineItems().size());
            assertEquals("price_123", params.getLineItems().getFirst().getPrice());
            assertEquals(1L, params.getLineItems().getFirst().getQuantity());
        }
    }

    @Test
    void urlAusenteNoEsExito() throws Exception {
        Price precio = new Price();
        precio.setId("price_123");
        when(productos.crearPrecio(any(), any())).thenReturn(precio);
        try (MockedConstruction<StripeClient> clientes = mockConstruction(StripeClient.class,
                withSettings().defaultAnswer(RETURNS_DEEP_STUBS), (cliente, contexto) ->
                        when(cliente.v1().checkout().sessions().create(any(SessionCreateParams.class))).thenReturn(new Session()))) {
            assertFalse(pasarela.procesarPago(1L, BigDecimal.ONE).isExitoso());
        }
    }

    @Test
    void propagaErroresDeStripe() throws Exception {
        ApiException error = new ApiException("fallo", null, null, 500, null);
        when(productos.crearPrecio(any(), any())).thenThrow(error);
        IllegalStateException resultado = assertThrows(IllegalStateException.class,
                () -> pasarela.procesarPago(1L, BigDecimal.ONE));
        assertSame(error, resultado.getCause());
    }

    @Test
    void propagaErrorAlCrearSesion() throws Exception {
        Price precio = new Price();
        precio.setId("price_123");
        when(productos.crearPrecio(any(), any())).thenReturn(precio);
        ApiException error = new ApiException("fallo", null, null, 500, null);
        try (MockedConstruction<StripeClient> clientes = mockConstruction(StripeClient.class,
                withSettings().defaultAnswer(RETURNS_DEEP_STUBS), (cliente, contexto) ->
                        when(cliente.v1().checkout().sessions().create(any(SessionCreateParams.class))).thenThrow(error))) {
            IllegalStateException resultado = assertThrows(IllegalStateException.class,
                    () -> pasarela.procesarPago(1L, BigDecimal.ONE));
            assertSame(error, resultado.getCause());
        }
    }
}