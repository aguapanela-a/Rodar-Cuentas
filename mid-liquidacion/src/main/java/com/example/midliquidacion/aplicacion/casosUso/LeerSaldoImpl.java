package com.example.midliquidacion.aplicacion.casosUso;

import com.example.midliquidacion.aplicacion.puertos.puertos_entrada.LeerSaldoIn;

public class LeerSaldoImpl implements LeerSaldoIn {
    // esta tambiénm  puede usar spriongoboot
    // pero como se da cuenta, es simplemente una puerta de entrada al crud
    // entonces no tiene logica de dominio
    // por lo tanto llama directamente al puerto de salida de leer saldo
}
