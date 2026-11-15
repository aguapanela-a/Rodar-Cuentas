-- Datos semilla compartidos con el grupo Viajes y zonas.
-- Cédulas fijas 10000000NN. Ids fijos: billeteras 33333333-...-0000000000NN, recargas 22222222-...-0000000000NN.
-- Regla: el saldo de cada billetera es igual a la suma de sus movimientos.
-- Nunca editar esta migración una vez aplicada en un entorno compartido: los cambios van en V3, V4...

INSERT INTO usuarios (cedula, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, correo, estado) VALUES
    ('1000000001', 'Ana',      NULL,     'Torres',  NULL,       'ana.torres@example.com',     'ACTIVO'),
    ('1000000002', 'Bruno',    NULL,     'Méndez',  NULL,       'bruno.mendez@example.com',   'ACTIVO'),
    ('1000000003', 'Camila',   'Andrea', 'Rojas',   'Gómez',    'camila.rojas@example.com',   'ACTIVO'),
    ('1000000004', 'Diego',    NULL,     'Herrera', NULL,       'diego.herrera@example.com',  'ACTIVO'),
    ('1000000005', 'Elena',    NULL,     'Castro',  'Ruiz',     'elena.castro@example.com',   'ACTIVO'),
    ('1000000006', 'Felipe',   NULL,     'Vargas',  NULL,       'felipe.vargas@example.com',  'ACTIVO'),
    ('1000000007', 'Gabriela', NULL,     'Ortiz',   NULL,       'gabriela.ortiz@example.com', 'ACTIVO'),
    ('1000000008', 'Hugo',     'Andrés', 'Salazar', NULL,       'hugo.salazar@example.com',   'ACTIVO'),
    ('1000000009', 'Isabel',   NULL,     'Moreno',  NULL,       'isabel.moreno@example.com',  'INACTIVO'),
    ('1000000010', 'Julián',   NULL,     'Pérez',   'Londoño',  'julian.perez@example.com',   'ACTIVO');

INSERT INTO billeteras (billetera_id, usuario_id, saldo) VALUES
    ('33333333-0000-4000-8000-000000000001', '1000000001',     0),
    ('33333333-0000-4000-8000-000000000002', '1000000002', 50000),
    ('33333333-0000-4000-8000-000000000003', '1000000003', 20000),
    ('33333333-0000-4000-8000-000000000004', '1000000004',  5000),
    ('33333333-0000-4000-8000-000000000005', '1000000005', 12500),
    ('33333333-0000-4000-8000-000000000006', '1000000006',  3000),
    ('33333333-0000-4000-8000-000000000007', '1000000007',  1500),
    ('33333333-0000-4000-8000-000000000008', '1000000008', 35000),
    ('33333333-0000-4000-8000-000000000009', '1000000009',  8000),
    ('33333333-0000-4000-8000-000000000010', '1000000010', 27000);

-- Una recarga por cada saldo > 0 (el usuario 01 queda sin movimientos).
INSERT INTO movimientos (movimiento_id, usuario_id, tipo, monto, referencia, creado_en) VALUES
    ('22222222-0000-4000-8000-000000000002', '1000000002', 'RECARGA', 50000, 'seed-02', '2026-01-01 13:02:00+00'),
    ('22222222-0000-4000-8000-000000000003', '1000000003', 'RECARGA', 20000, 'seed-03', '2026-01-01 13:03:00+00'),
    ('22222222-0000-4000-8000-000000000004', '1000000004', 'RECARGA',  5000, 'seed-04', '2026-01-01 13:04:00+00'),
    ('22222222-0000-4000-8000-000000000005', '1000000005', 'RECARGA', 12500, 'seed-05', '2026-01-01 13:05:00+00'),
    ('22222222-0000-4000-8000-000000000006', '1000000006', 'RECARGA',  3000, 'seed-06', '2026-01-01 13:06:00+00'),
    ('22222222-0000-4000-8000-000000000007', '1000000007', 'RECARGA',  1500, 'seed-07', '2026-01-01 13:07:00+00'),
    ('22222222-0000-4000-8000-000000000008', '1000000008', 'RECARGA', 35000, 'seed-08', '2026-01-01 13:08:00+00'),
    ('22222222-0000-4000-8000-000000000009', '1000000009', 'RECARGA',  8000, 'seed-09', '2026-01-01 13:09:00+00'),
    ('22222222-0000-4000-8000-000000000010', '1000000010', 'RECARGA', 27000, 'seed-10', '2026-01-01 13:10:00+00');
