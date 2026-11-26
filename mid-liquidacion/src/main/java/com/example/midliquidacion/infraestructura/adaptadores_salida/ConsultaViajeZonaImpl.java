package com.example.midliquidacion.infraestructura.adaptadores_salida;

import org.springframework.core.ParameterizedTypeReference;

import com.example.midliquidacion.aplicacion.puertos.puertos_salida.ConsultaViajeZonaOut;
import com.example.midliquidacion.dominio.modelos.PaginatedResponse;
import com.example.midliquidacion.dominio.modelos.ViajesResponse;
import com.example.midliquidacion.dominio.modelos.ZonaResponse;
import com.example.midliquidacion.infraestructura.config.RestClientConfig;

//Contiene la conexion a la API de viajes y zonas

@Component
public class ConsultaViajeZonaImpl implements ConsultaViajeZonaOut {
    //TO DO: Agregar ruta API Gateway en app properties
    
    private final RestClient restClient;

    public ConsultaViajeZonaImpl(
        RestClient.Builder builder,
        @Value("${api.gateway.url}") String gatewayUrl
    ) {
        this.restClient = builder
            .baseUrl(gatewayUrl)
            .build();
    }

    @Override
    public ViajesResponse consultarViaje(String viajeId) {

        return restClient.get()
            .uri("/viajes/{viajeId}", viajeId)
            .retrieve()
            .body(ViajesResponse.class);
    }

    @Override
    public ZonaResponse consultarZona(String zonaId) {

        return restClient.get()
            .uri("/zonas/{zonaId}", zonaId)
            .retrieve()
            .body(ZonaResponse.class);
    }

    @Override
    public boolean esPrimerViaje(String usuarioId) {

        PaginatedResponse<ViajesResponse> response = restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/viajes")
                .queryParam("usuarioId", usuarioId)
                .build())
            .retrieve()
            .body(new ParameterizedTypeReference<PaginatedResponse<ViajesResponse>>() {});

        return response == null || response.totalElements() <= 1;
    }

}