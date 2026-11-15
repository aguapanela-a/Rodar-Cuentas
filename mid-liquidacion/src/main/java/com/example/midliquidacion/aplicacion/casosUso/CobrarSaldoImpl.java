package com.example.midliquidacion.aplicacion.casosUso;

import com.example.midliquidacion.aplicacion.puertos.puertos_entrada.CobrarSaldoIn;

public class CobrarSaldoImpl implements CobrarSaldoIn {
    //clases Orquetadoras SI PUEDE USAR SPRINGBOOT
    // por ejemplo aqui
    // 1. llama al  puerto de salida que consulta viaje a Viajes y Zonas
    // 2. Llama a dominio la clase de calcular tarifa y le pasa la info del viaje -- luego recibe solo el dinero que debe descontar
    // 3. con ese dinero, llama a puerto de salida de leer saldo, ese saldo disponible y el que debe descontar se lo pasa al domionio descontar saldo
    // 4. descontar saldo solamente dice si si se puede o no se puede
    // 5. si si se puede se llama al puerto de salida crear movimiento para que cree un debito y ese crud descuente
    // 6.  si no pues retorna error
    // NOTA. si se da cuenta, los casos de uso en la capa de aplicacion orquestan, mas no calcula, verifica, etc.
}
