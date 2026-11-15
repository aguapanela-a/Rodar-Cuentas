package co.edu.uni.rodar.cuentas.billeteras.dto;

import co.edu.uni.rodar.cuentas.billeteras.models.Billetera;

/** Respuesta de GET /usuarios/{id}/billetera. */
public record BilleteraResponse(String cedula, long saldo, String moneda) {

    public static BilleteraResponse desde(Billetera billetera) {
        return new BilleteraResponse(billetera.getUsuario().getCedula(), billetera.getSaldo(), "COP");
    }
}
