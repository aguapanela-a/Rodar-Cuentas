package co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada;

import co.edu.uni.rodar.cuentas.dominio.modelos.EstadoUsuario;

public record ActualizarUsuarioComando(String nombre, String correo, EstadoUsuario estado) {
}
