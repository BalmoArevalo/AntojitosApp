package sv.ues.fia.eisi.proyecto01_antojitos.db.seeders;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Pobla todas las tablas de negocio (catálogos y datos demo).
 * Llamar después de haber creado TODAS las tablas de negocio.
 */
public final class DatosInicialesSeeder {

    private DatosInicialesSeeder() { }

    public static void poblar(SQLiteDatabase db) {

        Log.i("DatosInicialesSeeder", "Insertando datos de negocio…");

        //  En catálogos grandes conviene usar transacciones por bloque.
        db.beginTransaction();
        try {
            /* ───── LIMPIEZA (sin tocar tablas de seguridad) ───── */
            db.execSQL("DELETE FROM DETALLEPEDIDO;");
            db.execSQL("DELETE FROM REPARTOPEDIDO;");
            db.execSQL("DELETE FROM DIRECCION;");
            db.execSQL("DELETE FROM DATOSPRODUCTO;");
            db.execSQL("DELETE FROM CREDITO;");
            db.execSQL("DELETE FROM FACTURA;");
            db.execSQL("DELETE FROM PEDIDO;");
            db.execSQL("DELETE FROM TIPOEVENTO;");
            db.execSQL("DELETE FROM REPARTIDOR;");
            db.execSQL("DELETE FROM CLIENTE;");
            db.execSQL("DELETE FROM SUCURSAL;");
            db.execSQL("DELETE FROM PRODUCTO;");
            db.execSQL("DELETE FROM CATEGORIAPRODUCTO;");
            db.execSQL("DELETE FROM DISTRITO;");
            db.execSQL("DELETE FROM MUNICIPIO;");
            db.execSQL("DELETE FROM DEPARTAMENTO;");
            db.execSQL("DELETE FROM sqlite_sequence;");   // reset AUTOINCREMENT

            // 1 - Datos para DEPARTAMENTO
            db.execSQL("INSERT OR REPLACE INTO DEPARTAMENTO(ID_DEPARTAMENTO,NOMBRE_DEPARTAMENTO,ACTIVO_DEPARTAMENTO) VALUES (1,'San Salvador', 1);");
            db.execSQL("INSERT OR REPLACE INTO DEPARTAMENTO(ID_DEPARTAMENTO,NOMBRE_DEPARTAMENTO,ACTIVO_DEPARTAMENTO) VALUES (2,'La Libertad', 1);");
            db.execSQL("INSERT OR REPLACE INTO DEPARTAMENTO(ID_DEPARTAMENTO,NOMBRE_DEPARTAMENTO,ACTIVO_DEPARTAMENTO) VALUES (3,'Santa Ana', 1);");
            db.execSQL("INSERT OR REPLACE INTO DEPARTAMENTO(ID_DEPARTAMENTO,NOMBRE_DEPARTAMENTO,ACTIVO_DEPARTAMENTO) VALUES (4,'San Miguel', 1);");
            db.execSQL("INSERT OR REPLACE INTO DEPARTAMENTO(ID_DEPARTAMENTO,NOMBRE_DEPARTAMENTO,ACTIVO_DEPARTAMENTO) VALUES (5,'Usulután', 1);");

            // 1 - Datos para Municipio
            // San Salvador
            db.execSQL("INSERT OR REPLACE INTO MUNICIPIO(ID_DEPARTAMENTO, ID_MUNICIPIO, NOMBRE_MUNICIPIO, ACTIVO_MUNICIPIO) VALUES (1,1,'San Salvador', 1);");
            // La Libertad
            db.execSQL("INSERT OR REPLACE INTO MUNICIPIO(ID_DEPARTAMENTO, ID_MUNICIPIO, NOMBRE_MUNICIPIO, ACTIVO_MUNICIPIO) VALUES (2,1,'Santa Tecla', 1);");
            // Santa Ana
            db.execSQL("INSERT OR REPLACE INTO MUNICIPIO(ID_DEPARTAMENTO, ID_MUNICIPIO, NOMBRE_MUNICIPIO, ACTIVO_MUNICIPIO) VALUES (3,1,'Metapán', 1);");
            // San Miguel
            db.execSQL("INSERT OR REPLACE INTO MUNICIPIO(ID_DEPARTAMENTO, ID_MUNICIPIO, NOMBRE_MUNICIPIO, ACTIVO_MUNICIPIO) VALUES (4,1,'Ciudad Barrios', 1);");
            // Usulután
            db.execSQL("INSERT OR REPLACE INTO MUNICIPIO(ID_DEPARTAMENTO, ID_MUNICIPIO, NOMBRE_MUNICIPIO, ACTIVO_MUNICIPIO) VALUES (5,1,'Santiago de María', 1);");



            // 3 - Datos para DISTRITO
            db.execSQL("INSERT OR REPLACE INTO DISTRITO(ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,NOMBRE_DISTRITO,CODIGO_POSTAL) VALUES (1,1,1,'Centro','1101');");
            db.execSQL("INSERT OR REPLACE INTO DISTRITO(ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,NOMBRE_DISTRITO,CODIGO_POSTAL) VALUES (2,1,1,'Merliot','1102');");
            db.execSQL("INSERT OR REPLACE INTO DISTRITO(ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,NOMBRE_DISTRITO,CODIGO_POSTAL) VALUES (3,1,1,'Metapán Norte','1103');");
            db.execSQL("INSERT OR REPLACE INTO DISTRITO(ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,NOMBRE_DISTRITO,CODIGO_POSTAL) VALUES (4,1,1,'Ciudad Pacífica','1104');");
            db.execSQL("INSERT OR REPLACE INTO DISTRITO(ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,NOMBRE_DISTRITO,CODIGO_POSTAL) VALUES (5,1,1,'Puerto Parada','1105');");

            // 4 - Datos para CATEGORIAPRODUCTO
            db.execSQL("INSERT OR REPLACE INTO CATEGORIAPRODUCTO(ID_CATEGORIAPRODUCTO,NOMBRE_CATEGORIA,DESCRIPCION_CATEGORIA,DISPONIBLE_CATEGORIA,HORA_DISPONIBLE_DESDE,HORA_DISPONIBLE_HASTA,ACTIVO_CATEGORIAPRODUCTO) "
                    + "VALUES (1,'Antojitos','Comida típica',1,'15:00','18:00',1);");
            db.execSQL("INSERT OR REPLACE INTO CATEGORIAPRODUCTO(ID_CATEGORIAPRODUCTO,NOMBRE_CATEGORIA,DESCRIPCION_CATEGORIA,DISPONIBLE_CATEGORIA,HORA_DISPONIBLE_DESDE,HORA_DISPONIBLE_HASTA,ACTIVO_CATEGORIAPRODUCTO) "
                    + "VALUES (2,'Bebidas','Bebidas frías',1,'00:00','23:59',1);");
            db.execSQL("INSERT OR REPLACE INTO CATEGORIAPRODUCTO(ID_CATEGORIAPRODUCTO,NOMBRE_CATEGORIA,DESCRIPCION_CATEGORIA,DISPONIBLE_CATEGORIA,HORA_DISPONIBLE_DESDE,HORA_DISPONIBLE_HASTA,ACTIVO_CATEGORIAPRODUCTO) "
                    + "VALUES (3,'Comidas rápidas','Hamburguesas y hotdogs',1,'10:00','22:00',1);");
            db.execSQL("INSERT OR REPLACE INTO CATEGORIAPRODUCTO(ID_CATEGORIAPRODUCTO,NOMBRE_CATEGORIA,DESCRIPCION_CATEGORIA,DISPONIBLE_CATEGORIA,HORA_DISPONIBLE_DESDE,HORA_DISPONIBLE_HASTA,ACTIVO_CATEGORIAPRODUCTO) "
                    + "VALUES (4,'Postres','Dulces típicos',1,'12:00','20:00',1);");
            db.execSQL("INSERT OR REPLACE INTO CATEGORIAPRODUCTO(ID_CATEGORIAPRODUCTO,NOMBRE_CATEGORIA,DESCRIPCION_CATEGORIA,DISPONIBLE_CATEGORIA,HORA_DISPONIBLE_DESDE,HORA_DISPONIBLE_HASTA,ACTIVO_CATEGORIAPRODUCTO) "
                    + "VALUES (5,'Platos fuertes','Almuerzos tradicionales',1,'11:00','15:00',1);");

            // 5 - Datos para PRODUCTO
            db.execSQL("INSERT OR REPLACE INTO PRODUCTO(ID_PRODUCTO,ID_CATEGORIAPRODUCTO,NOMBRE_PRODUCTO,DESCRIPCION_PRODUCTO,ACTIVO_PRODUCTO) "
                    + "VALUES (1,1,'Pupusa de Queso','Clásica pupusa salvadoreña',1);");
            db.execSQL("INSERT OR REPLACE INTO PRODUCTO(ID_PRODUCTO,ID_CATEGORIAPRODUCTO,NOMBRE_PRODUCTO,DESCRIPCION_PRODUCTO,ACTIVO_PRODUCTO) "
                    + "VALUES (2,1,'Pupusa Revueltas','Con chicharrón, queso y frijoles',1);");
            db.execSQL("INSERT OR REPLACE INTO PRODUCTO(ID_PRODUCTO,ID_CATEGORIAPRODUCTO,NOMBRE_PRODUCTO,DESCRIPCION_PRODUCTO,ACTIVO_PRODUCTO) "
                    + "VALUES (3,2,'Horchata','Bebida tradicional salvadoreña',1);");
            db.execSQL("INSERT OR REPLACE INTO PRODUCTO(ID_PRODUCTO,ID_CATEGORIAPRODUCTO,NOMBRE_PRODUCTO,DESCRIPCION_PRODUCTO,ACTIVO_PRODUCTO) "
                    + "VALUES (4,3,'Hamburguesa','Con papas y bebida',1);");
            db.execSQL("INSERT OR REPLACE INTO PRODUCTO(ID_PRODUCTO,ID_CATEGORIAPRODUCTO,NOMBRE_PRODUCTO,DESCRIPCION_PRODUCTO,ACTIVO_PRODUCTO) "
                    + "VALUES (5,4,'Empanada de Plátano','Rellena de leche',1);");

            // 6 - Datos para SUCURSAL
            db.execSQL("INSERT OR REPLACE INTO SUCURSAL(ID_SUCURSAL,ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,NOMBRE_SUCURSAL,DIRECCION_SUCURSAL,TELEFONO_SUCURSAL,HORARIO_APERTURA_SUCURSAL,HORARIO_CIERRE_SUCURSAL,ACTIVO_SUCURSAL) " +
                    "VALUES (1,1,1,1,'Sucursal Centro','Calle El Progreso','2200-1111','08:00','20:00',1);");

            db.execSQL("INSERT OR REPLACE INTO SUCURSAL(ID_SUCURSAL,ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,NOMBRE_SUCURSAL,DIRECCION_SUCURSAL,TELEFONO_SUCURSAL,HORARIO_APERTURA_SUCURSAL,HORARIO_CIERRE_SUCURSAL,ACTIVO_SUCURSAL) " +
                    "VALUES (2,2,1,1,'Sucursal Merliot','Blvd. Merliot','2200-2222','09:00','21:00',1);");

            db.execSQL("INSERT OR REPLACE INTO SUCURSAL(ID_SUCURSAL,ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,NOMBRE_SUCURSAL,DIRECCION_SUCURSAL,TELEFONO_SUCURSAL,HORARIO_APERTURA_SUCURSAL,HORARIO_CIERRE_SUCURSAL,ACTIVO_SUCURSAL) " +
                    "VALUES (3,3,1,1,'Sucursal Metapán','Av. Central','2200-3333','07:00','19:00',1);");

            db.execSQL("INSERT OR REPLACE INTO SUCURSAL(ID_SUCURSAL,ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,NOMBRE_SUCURSAL,DIRECCION_SUCURSAL,TELEFONO_SUCURSAL,HORARIO_APERTURA_SUCURSAL,HORARIO_CIERRE_SUCURSAL,ACTIVO_SUCURSAL) " +
                    "VALUES (4,4,1,1,'Sucursal San Miguel','Col. Ciudad Pacífica','2200-4444','08:30','19:30',1);");

            db.execSQL("INSERT OR REPLACE INTO SUCURSAL(ID_SUCURSAL,ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,NOMBRE_SUCURSAL,DIRECCION_SUCURSAL,TELEFONO_SUCURSAL,HORARIO_APERTURA_SUCURSAL,HORARIO_CIERRE_SUCURSAL,ACTIVO_SUCURSAL) " +
                    "VALUES (5,5,1,1,'Sucursal Usulután','Centro Usulután','2200-5555','08:00','18:00',1);");


            // --- 7 - Datos para CLIENTE ---
// (Los primeros 5 inserts que ya tenías se mantienen)
// ... tus inserts para cliente 1 al 5 ...
            db.execSQL("INSERT OR REPLACE INTO CLIENTE(ID_CLIENTE,TELEFONO_CLIENTE,NOMBRE_CLIENTE,APELLIDO_CLIENTE,ACTIVO_CLIENTE) "
                    + "VALUES (1,'7010-1111','Carlos','Ramírez',1);");
            db.execSQL("INSERT OR REPLACE INTO CLIENTE(ID_CLIENTE,TELEFONO_CLIENTE,NOMBRE_CLIENTE,APELLIDO_CLIENTE,ACTIVO_CLIENTE) "
                    + "VALUES (2,'7010-2222','Ana','González',1);");
            db.execSQL("INSERT OR REPLACE INTO CLIENTE(ID_CLIENTE,TELEFONO_CLIENTE,NOMBRE_CLIENTE,APELLIDO_CLIENTE,ACTIVO_CLIENTE) "
                    + "VALUES (3,'7010-3333','Luis','Martínez',1);");
            db.execSQL("INSERT OR REPLACE INTO CLIENTE(ID_CLIENTE,TELEFONO_CLIENTE,NOMBRE_CLIENTE,APELLIDO_CLIENTE,ACTIVO_CLIENTE) "
                    + "VALUES (4,'7010-4444','Diana','López',1);");
            db.execSQL("INSERT OR REPLACE INTO CLIENTE(ID_CLIENTE,TELEFONO_CLIENTE,NOMBRE_CLIENTE,APELLIDO_CLIENTE,ACTIVO_CLIENTE) "
                    + "VALUES (5,'7010-5555','José','Hernández',1);");

// --- NUEVOS CLIENTES AÑADIDOS ---
            db.execSQL("INSERT OR REPLACE INTO CLIENTE(ID_CLIENTE,TELEFONO_CLIENTE,NOMBRE_CLIENTE,APELLIDO_CLIENTE,ACTIVO_CLIENTE) "
                    + "VALUES (6,'7010-6666','Elena','Fuentes',1);");
            db.execSQL("INSERT OR REPLACE INTO CLIENTE(ID_CLIENTE,TELEFONO_CLIENTE,NOMBRE_CLIENTE,APELLIDO_CLIENTE,ACTIVO_CLIENTE) "
                    + "VALUES (7,'7010-7777','Mario','Paz',1);");
            db.execSQL("INSERT OR REPLACE INTO CLIENTE(ID_CLIENTE,TELEFONO_CLIENTE,NOMBRE_CLIENTE,APELLIDO_CLIENTE,ACTIVO_CLIENTE) "
                    + "VALUES (8,'7010-8888','Sofia','Vargas',1);");

            // 8 - Datos para REPARTIDOR ---
            db.execSQL("INSERT OR REPLACE INTO REPARTIDOR(ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO, TIPO_VEHICULO, DISPONIBLE, TELEFONO_REPARTIDOR, NOMBRE_REPARTIDOR, APELLIDO_REPARTIDOR, ACTIVO_REPARTIDOR) " +
                    "VALUES (1,1,1,'Moto',1,'7200-0001','Luis','Gómez',1);");
            db.execSQL("INSERT OR REPLACE INTO REPARTIDOR(ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO, TIPO_VEHICULO, DISPONIBLE, TELEFONO_REPARTIDOR, NOMBRE_REPARTIDOR, APELLIDO_REPARTIDOR, ACTIVO_REPARTIDOR) " +
                    "VALUES (2,1,1,'Bicicleta',1,'7200-0002','Mario','Ruiz',1);");
            db.execSQL("INSERT OR REPLACE INTO REPARTIDOR(ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO, TIPO_VEHICULO, DISPONIBLE, TELEFONO_REPARTIDOR, NOMBRE_REPARTIDOR, APELLIDO_REPARTIDOR, ACTIVO_REPARTIDOR) " +
                    "VALUES (3,1,1,'Carro',1,'7200-0003','Tatiana','Martínez',1);");
            db.execSQL("INSERT OR REPLACE INTO REPARTIDOR(ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO, TIPO_VEHICULO, DISPONIBLE, TELEFONO_REPARTIDOR, NOMBRE_REPARTIDOR, APELLIDO_REPARTIDOR, ACTIVO_REPARTIDOR) " +
                    "VALUES (4,1,1,'Moto',1,'7200-0004','Kevin','Morales',1);");
            db.execSQL("INSERT OR REPLACE INTO REPARTIDOR(ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO, TIPO_VEHICULO, DISPONIBLE, TELEFONO_REPARTIDOR, NOMBRE_REPARTIDOR, APELLIDO_REPARTIDOR, ACTIVO_REPARTIDOR) " +
                    "VALUES (5,1,1,'Camioneta',1,'7200-0005','Sofía','Aguilar',1);");


            // 9 - Datos para TIPOEVENTO ---
            db.execSQL("INSERT OR REPLACE INTO TIPOEVENTO("
                    + "ID_TIPO_EVENTO,NOMBRE_TIPO_EVENTO,DESCRIPCION_TIPO_EVENTO,"
                    + "MONTO_MINIMO,MONTO_MAXIMO,ACTIVO_TIPOEVENTO) VALUES (1,'Fiesta Infantil','Evento privado',30.00,300.00,1);");
            db.execSQL("INSERT OR REPLACE INTO TIPOEVENTO("
                    + "ID_TIPO_EVENTO,NOMBRE_TIPO_EVENTO,DESCRIPCION_TIPO_EVENTO,"
                    + "MONTO_MINIMO,MONTO_MAXIMO,ACTIVO_TIPOEVENTO) VALUES (2,'Reunión Empresarial','Coffee break',50.00,400.00,1);");
            db.execSQL("INSERT OR REPLACE INTO TIPOEVENTO("
                    + "ID_TIPO_EVENTO,NOMBRE_TIPO_EVENTO,DESCRIPCION_TIPO_EVENTO,"
                    + "MONTO_MINIMO,MONTO_MAXIMO,ACTIVO_TIPOEVENTO) VALUES (3,'Cumpleaños','Celebración familiar',25.00,250.00,1);");
            db.execSQL("INSERT OR REPLACE INTO TIPOEVENTO("
                    + "ID_TIPO_EVENTO,NOMBRE_TIPO_EVENTO,DESCRIPCION_TIPO_EVENTO,"
                    + "MONTO_MINIMO,MONTO_MAXIMO,ACTIVO_TIPOEVENTO) VALUES (4,'Boda Civil','Recepción sencilla',100.00,800.00,1);");
            db.execSQL("INSERT OR REPLACE INTO TIPOEVENTO("
                    + "ID_TIPO_EVENTO,NOMBRE_TIPO_EVENTO,DESCRIPCION_TIPO_EVENTO,"
                    + "MONTO_MINIMO,MONTO_MAXIMO,ACTIVO_TIPOEVENTO) VALUES (5,'Cena de Fin de Año','Convivio navideño',60.00,600.00,1);");

            // --- 10 - Datos para PEDIDO (Expandidos a 8 para cubrir facturas) ---
            db.execSQL("INSERT OR REPLACE INTO PEDIDO("
                    + "ID_PEDIDO,ID_CLIENTE,ID_TIPO_EVENTO,ID_SUCURSAL,ID_REPARTIDOR,"
                    + "FECHA_HORA_PEDIDO,ESTADO_PEDIDO,ACTIVO_PEDIDO) VALUES (1,1,1,1,1,'2025-05-01 10:00','Entregado',1);"); // Para F1
            db.execSQL("INSERT OR REPLACE INTO PEDIDO("
                    + "ID_PEDIDO,ID_CLIENTE,ID_TIPO_EVENTO,ID_SUCURSAL,ID_REPARTIDOR,"
                    + "FECHA_HORA_PEDIDO,ESTADO_PEDIDO,ACTIVO_PEDIDO) VALUES (2,2,2,2,2,'2025-05-02 11:00','Enviado',1);"); // Para F2
            db.execSQL("INSERT OR REPLACE INTO PEDIDO("
                    + "ID_PEDIDO,ID_CLIENTE,ID_TIPO_EVENTO,ID_SUCURSAL,ID_REPARTIDOR,"
                    + "FECHA_HORA_PEDIDO,ESTADO_PEDIDO,ACTIVO_PEDIDO) VALUES (3,3,3,3,3,'2025-05-03 12:00','Entregado',1);"); // Para F3
            db.execSQL("INSERT OR REPLACE INTO PEDIDO("
                    + "ID_PEDIDO,ID_CLIENTE,ID_TIPO_EVENTO,ID_SUCURSAL,ID_REPARTIDOR,"
                    + "FECHA_HORA_PEDIDO,ESTADO_PEDIDO,ACTIVO_PEDIDO) VALUES (4,4,4,4,4,'2025-05-04 13:00','Entregado',1);"); // Para F4
            db.execSQL("INSERT OR REPLACE INTO PEDIDO("
                    + "ID_PEDIDO,ID_CLIENTE,ID_TIPO_EVENTO,ID_SUCURSAL,ID_REPARTIDOR,"
                    + "FECHA_HORA_PEDIDO,ESTADO_PEDIDO,ACTIVO_PEDIDO) VALUES (5,5,5,5,5,'2025-05-05 14:00','Enviado',1);"); // Para F5
            db.execSQL("INSERT OR REPLACE INTO PEDIDO("
                    + "ID_PEDIDO,ID_CLIENTE,ID_TIPO_EVENTO,ID_SUCURSAL,ID_REPARTIDOR,"
                    + "FECHA_HORA_PEDIDO,ESTADO_PEDIDO,ACTIVO_PEDIDO) VALUES (6,6,1,1,1,'2025-05-06 09:00','Cancelado',1);"); // Para F6
            db.execSQL("INSERT OR REPLACE INTO PEDIDO("
                    + "ID_PEDIDO,ID_CLIENTE,ID_TIPO_EVENTO,ID_SUCURSAL,ID_REPARTIDOR,"
                    + "FECHA_HORA_PEDIDO,ESTADO_PEDIDO,ACTIVO_PEDIDO) VALUES (7,7,2,2,2,'2025-05-07 15:00','Pendiente',1);"); // Para F7
            db.execSQL("INSERT OR REPLACE INTO PEDIDO("
                    + "ID_PEDIDO,ID_CLIENTE,ID_TIPO_EVENTO,ID_SUCURSAL,ID_REPARTIDOR,"
                    + "FECHA_HORA_PEDIDO,ESTADO_PEDIDO,ACTIVO_PEDIDO) VALUES (8,8,3,3,3,'2025-05-08 16:00','Enviado',1);"); // Para F8

            // --- 11 – Datos para FACTURA (Modificados y Expandidos) ---
            db.execSQL("INSERT OR REPLACE INTO FACTURA("
                    + "ID_FACTURA,ID_PEDIDO,FECHA_EMISION,MONTO_TOTAL,TIPO_PAGO,"
                    + "ESTADO_FACTURA,ES_CREDITO) VALUES (1,1,'2025-05-01',35.50,'Efectivo','Pagada',0);");
            db.execSQL("INSERT OR REPLACE INTO FACTURA("
                    + "ID_FACTURA,ID_PEDIDO,FECHA_EMISION,MONTO_TOTAL,TIPO_PAGO,"
                    + "ESTADO_FACTURA,ES_CREDITO) VALUES (2,2,'2025-05-02',70.00,'Tarjeta','Pendiente',0);");
            db.execSQL("INSERT OR REPLACE INTO FACTURA("
                    + "ID_FACTURA,ID_PEDIDO,FECHA_EMISION,MONTO_TOTAL,TIPO_PAGO,"
                    + "ESTADO_FACTURA,ES_CREDITO) VALUES (3,3,'2025-05-03',120.00,'Crédito','En Crédito',1);");
            db.execSQL("INSERT OR REPLACE INTO FACTURA("
                    + "ID_FACTURA,ID_PEDIDO,FECHA_EMISION,MONTO_TOTAL,TIPO_PAGO,"
                    + "ESTADO_FACTURA,ES_CREDITO) VALUES (4,4,'2025-05-04',65.75,'Crédito','Pagada',1);");
            db.execSQL("INSERT OR REPLACE INTO FACTURA("
                    + "ID_FACTURA,ID_PEDIDO,FECHA_EMISION,MONTO_TOTAL,TIPO_PAGO,"
                    + "ESTADO_FACTURA,ES_CREDITO) VALUES (5,5,'2025-05-05',40.00,'Crédito','En Crédito',1);");
            db.execSQL("INSERT OR REPLACE INTO FACTURA("
                    + "ID_FACTURA,ID_PEDIDO,FECHA_EMISION,MONTO_TOTAL,TIPO_PAGO,"
                    + "ESTADO_FACTURA,ES_CREDITO) VALUES (6,6,'2025-05-06',25.00,'Efectivo','Anulada',0);");
            db.execSQL("INSERT OR REPLACE INTO FACTURA("
                    + "ID_FACTURA,ID_PEDIDO,FECHA_EMISION,MONTO_TOTAL,TIPO_PAGO,"
                    + "ESTADO_FACTURA,ES_CREDITO) VALUES (7,7,'2025-05-07',88.20,'Transferencia','Pendiente',0);");

            // --- 12 – Datos para CREDITO (Modificados y Congruentes) ---
            db.execSQL("INSERT OR REPLACE INTO CREDITO("
                    + "ID_CREDITO,ID_FACTURA,MONTO_AUTORIZADO_CREDITO,MONTO_PAGADO,"
                    + "SALDO_PENDIENTE,FECHA_LIMITE_PAGO,ESTADO_CREDITO) "
                    + "VALUES (1,3,120.00,50.00,70.00,'2025-06-15','Activo');"); // Para F3
            db.execSQL("INSERT OR REPLACE INTO CREDITO("
                    + "ID_CREDITO,ID_FACTURA,MONTO_AUTORIZADO_CREDITO,MONTO_PAGADO,"
                    + "SALDO_PENDIENTE,FECHA_LIMITE_PAGO,ESTADO_CREDITO) "
                    + "VALUES (2,4,65.75,65.75,0.00,'2025-05-20','Pagado');"); // Para F4
            db.execSQL("INSERT OR REPLACE INTO CREDITO("
                    + "ID_CREDITO,ID_FACTURA,MONTO_AUTORIZADO_CREDITO,MONTO_PAGADO,"
                    + "SALDO_PENDIENTE,FECHA_LIMITE_PAGO,ESTADO_CREDITO) "
                    + "VALUES (3,5,40.00,0.00,40.00,'2025-06-25','Activo');"); // Para F5


            // 13 - Datos para DATOSPRODUCTO ---
            db.execSQL("INSERT OR REPLACE INTO DATOSPRODUCTO("
                    + "ID_SUCURSAL,ID_PRODUCTO,PRECIO_SUCURSAL_PRODUCTO,STOCK,"
                    + "ACTIVO_DATOSPRODUCTO) VALUES (1,1,1.00,10,1);");
            db.execSQL("INSERT OR REPLACE INTO DATOSPRODUCTO("
                    + "ID_SUCURSAL,ID_PRODUCTO,PRECIO_SUCURSAL_PRODUCTO,STOCK,"
                    + "ACTIVO_DATOSPRODUCTO) VALUES (2,2,1.25,15,1);");
            db.execSQL("INSERT OR REPLACE INTO DATOSPRODUCTO("
                    + "ID_SUCURSAL,ID_PRODUCTO,PRECIO_SUCURSAL_PRODUCTO,STOCK,"
                    + "ACTIVO_DATOSPRODUCTO) VALUES (3,3,0.75,20,1);");
            db.execSQL("INSERT OR REPLACE INTO DATOSPRODUCTO("
                    + "ID_SUCURSAL,ID_PRODUCTO,PRECIO_SUCURSAL_PRODUCTO,STOCK,"
                    + "ACTIVO_DATOSPRODUCTO) VALUES (4,4,3.50,8,1);");
            db.execSQL("INSERT OR REPLACE INTO DATOSPRODUCTO("
                    + "ID_SUCURSAL,ID_PRODUCTO,PRECIO_SUCURSAL_PRODUCTO,STOCK,"
                    + "ACTIVO_DATOSPRODUCTO) VALUES (5,5,1.75,12,1);");

            // --- 14 – Datos para DIRECCION ---

            db.execSQL("INSERT OR REPLACE INTO DIRECCION(ID_CLIENTE,ID_DIRECCION,ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,DIRECCION_ESPECIFICA,DESCRIPCION_DIRECCION,ACTIVO_DIRECCION) " +
                    "VALUES (1,1,1,1,1,'Col. Escalón #123','Casa esquina azul',1);");

            db.execSQL("INSERT OR REPLACE INTO DIRECCION(ID_CLIENTE,ID_DIRECCION,ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,DIRECCION_ESPECIFICA,DESCRIPCION_DIRECCION,ACTIVO_DIRECCION) " +
                    "VALUES (5,5,5,1,1,'Usulután centro','Casa colonial',1);");

            db.execSQL("INSERT OR REPLACE INTO DIRECCION(ID_CLIENTE,ID_DIRECCION,ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,DIRECCION_ESPECIFICA,DESCRIPCION_DIRECCION,ACTIVO_DIRECCION) " +
                    "VALUES (6,6,1,1,1,'Avenida Olímpica','Edificio gris, Apt 5',1);");

            db.execSQL("INSERT OR REPLACE INTO DIRECCION(ID_CLIENTE,ID_DIRECCION,ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,DIRECCION_ESPECIFICA,DESCRIPCION_DIRECCION,ACTIVO_DIRECCION) " +
                    "VALUES (7,7,2,1,1,'Residencial Las Palmas','Calle Los Almendros #7D',1);");

            db.execSQL("INSERT OR REPLACE INTO DIRECCION(ID_CLIENTE,ID_DIRECCION,ID_DEPARTAMENTO,ID_MUNICIPIO,ID_DISTRITO,DIRECCION_ESPECIFICA,DESCRIPCION_DIRECCION,ACTIVO_DIRECCION) " +
                    "VALUES (8,8,3,1,1,'Barrio El Calvario','Frente a parque',1);");


            // 15 Datos para REPARTOPEDIDO
            db.execSQL("INSERT OR REPLACE INTO REPARTOPEDIDO(" +
                    "ID_PEDIDO, ID_REPARTO_PEDIDO, HORA_ASIGNACION, UBICACION_ENTREGA, FECHA_HORA_ENTREGA) " +
                    "VALUES (1, 1, '01/05/2025 10:30', 'Escalón', '01/05/2025 11:00');");

            db.execSQL("INSERT OR REPLACE INTO REPARTOPEDIDO(" +
                    "ID_PEDIDO, ID_REPARTO_PEDIDO, HORA_ASIGNACION, UBICACION_ENTREGA, FECHA_HORA_ENTREGA) " +
                    "VALUES (2, 1, '01/05/2025 11:30', 'Merliot', '01/05/2025 12:00');");

            db.execSQL("INSERT OR REPLACE INTO REPARTOPEDIDO(" +
                    "ID_PEDIDO, ID_REPARTO_PEDIDO, HORA_ASIGNACION, UBICACION_ENTREGA, FECHA_HORA_ENTREGA) " +
                    "VALUES (3, 1, '01/05/2025 12:30', 'Metapán', '01/05/2025 13:00');");

            db.execSQL("INSERT OR REPLACE INTO REPARTOPEDIDO(" +
                    "ID_PEDIDO, ID_REPARTO_PEDIDO, HORA_ASIGNACION, UBICACION_ENTREGA, FECHA_HORA_ENTREGA) " +
                    "VALUES (4, 1, '01/05/2025 13:30', 'San Miguel', '01/05/2025 14:00');");

            db.execSQL("INSERT OR REPLACE INTO REPARTOPEDIDO(" +
                    "ID_PEDIDO, ID_REPARTO_PEDIDO, HORA_ASIGNACION, UBICACION_ENTREGA, FECHA_HORA_ENTREGA) " +
                    "VALUES (5, 1, '01/05/2025 14:30', 'Usulután', '01/05/2025 15:00');");

            // --- 16 – Datos para DETALLEPEDIDO ---
            db.execSQL("INSERT OR REPLACE INTO DETALLEPEDIDO(" +
                    "ID_PRODUCTO, ID_PEDIDO, CANTIDAD, SUBTOTAL) " +
                    "VALUES (1, 1, 2, 2.00);");

            db.execSQL("INSERT OR REPLACE INTO DETALLEPEDIDO(" +
                    "ID_PRODUCTO, ID_PEDIDO, CANTIDAD, SUBTOTAL) " +
                    "VALUES (5, 5, 3, 5.25);");

            db.execSQL("INSERT OR REPLACE INTO DETALLEPEDIDO(" +
                    "ID_PRODUCTO, ID_PEDIDO, CANTIDAD, SUBTOTAL) " +
                    "VALUES (1, 6, 5, 5.00);");
            db.execSQL("INSERT OR REPLACE INTO DETALLEPEDIDO(" +
                    "ID_PRODUCTO, ID_PEDIDO, CANTIDAD, SUBTOTAL) " +
                    "VALUES (3, 6, 5, 3.75);");

            db.execSQL("INSERT OR REPLACE INTO DETALLEPEDIDO(" +
                    "ID_PRODUCTO, ID_PEDIDO, CANTIDAD, SUBTOTAL) " +
                    "VALUES (2, 7, 10, 12.50);");
            db.execSQL("INSERT OR REPLACE INTO DETALLEPEDIDO(" +
                    "ID_PRODUCTO, ID_PEDIDO, CANTIDAD, SUBTOTAL) " +
                    "VALUES (3, 7, 4, 3.00);");

            db.execSQL("INSERT OR REPLACE INTO DETALLEPEDIDO(" +
                    "ID_PRODUCTO, ID_PEDIDO, CANTIDAD, SUBTOTAL) " +
                    "VALUES (4, 8, 3, 10.50);");
            db.execSQL("INSERT OR REPLACE INTO DETALLEPEDIDO(" +
                    "ID_PRODUCTO, ID_PEDIDO, CANTIDAD, SUBTOTAL) " +
                    "VALUES (5, 8, 2, 3.50);");
            db.execSQL("INSERT OR REPLACE INTO DETALLEPEDIDO(" +
                    "ID_PRODUCTO, ID_PEDIDO, CANTIDAD, SUBTOTAL) " +
                    "VALUES (1, 8, 2, 2.00);");


            //Trrigers de actualizacion

            db.execSQL("CREATE TRIGGER trg_credito_totalmente_pagado_actualiza_factura " +
                    "AFTER UPDATE ON CREDITO " +
                    "FOR EACH ROW " +
                    "WHEN NEW.ESTADO_CREDITO = 'Pagado' AND OLD.ESTADO_CREDITO != 'Pagado' AND NEW.SALDO_PENDIENTE <= 0.009 " +
                    "BEGIN " +
                    "    UPDATE FACTURA " +
                    "    SET ESTADO_FACTURA = 'Pagada' " +
                    "    WHERE ID_FACTURA = NEW.ID_FACTURA; " +
                    "END;");

            db.execSQL("CREATE TRIGGER trg_actualizar_estado_pedido_tras_factura " +
                    "AFTER INSERT ON FACTURA " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "    UPDATE PEDIDO " +
                    "    SET ESTADO_PEDIDO = 'despachado' " +
                    "    WHERE ID_PEDIDO = NEW.ID_PEDIDO; " +
                    "END;");

            db.execSQL("CREATE TRIGGER trg_actualizar_fecha_entrega_al_cambiar_estado " +
                    "AFTER UPDATE ON PEDIDO " +
                    "FOR EACH ROW " +
                    "WHEN NEW.ESTADO_PEDIDO = 'entregado' AND OLD.ESTADO_PEDIDO != 'entregado' " +
                    "BEGIN " +
                    "    UPDATE REPARTOPEDIDO " +
                    "    SET FECHA_HORA_ENTREGA = datetime('now') " +  // Formato ISO estándar
                    "    WHERE ID_PEDIDO = NEW.ID_PEDIDO; " +
                    "END;");


            //Triggers de semenantica

            db.execSQL("CREATE TRIGGER trg_prevenir_eliminar_factura_con_credito_activo_o_pagado " +
                    "BEFORE DELETE ON FACTURA " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "    SELECT RAISE(ABORT, 'No se puede eliminar la factura. Tiene un crédito Activo o Pagado asociado.') " +
                    "    WHERE EXISTS ( " +
                    "        SELECT 1 FROM CREDITO " +
                    "        WHERE CREDITO.ID_FACTURA = OLD.ID_FACTURA " +
                    "          AND (CREDITO.ESTADO_CREDITO = 'Activo' OR CREDITO.ESTADO_CREDITO = 'Pagado') " +
                    "    ); " +
                    "END;");

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        Log.i("DatosInicialesSeeder", "Datos de negocio insertados.");
    }
}