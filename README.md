# Rodar-Cuentas
Es un servicio que gestiona el cobro  y registro de cuentas por cobrar para un sistema de scooters y bicicletas compartidas en la ciudad.

## Configuración de Checkout

En `mid-liquidacion`, configure mediante variables de entorno `STRIPIE_SECRETKEY`,
`STRIPE_CHECKOUT_SUCCESS_URL` y `STRIPE_CHECKOUT_CANCEL_URL`. La primera contiene
la clave secreta de Stripe y las otras las URL de redirección. No incluya claves en
el repositorio. La redirección de éxito no acredita saldo; la confirmación por
webhook y los adaptadores de usuarios y movimientos siguen pendientes.
