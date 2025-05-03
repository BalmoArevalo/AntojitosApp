package sv.ues.fia.eisi.proyecto01_antojitos.db;

import android.database.sqlite.SQLiteDatabase;

public class BaseDatosSeeder {

    public static void insertarDatosIniciales(SQLiteDatabase db) {
        // 🔁 Limpiar todas las tablas (en orden inverso)
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

        // 🌎 Datos para DEPARTAMENTO
        db.execSQL("INSERT OR REPLACE INTO DEPARTAMENTO VALUES (1, 'San Salvador');");
        db.execSQL("INSERT OR REPLACE INTO DEPARTAMENTO VALUES (2, 'La Libertad');");
        db.execSQL("INSERT OR REPLACE INTO DEPARTAMENTO VALUES (3, 'Santa Ana');");
        db.execSQL("INSERT OR REPLACE INTO DEPARTAMENTO VALUES (4, 'San Miguel');");
        db.execSQL("INSERT OR REPLACE INTO DEPARTAMENTO VALUES (5, 'Usulután');");

        // 🏙️ Datos para MUNICIPIO
        db.execSQL("INSERT OR REPLACE INTO MUNICIPIO VALUES (1, 1, 'San Salvador');");
        db.execSQL("INSERT OR REPLACE INTO MUNICIPIO VALUES (2, 2, 'Santa Tecla');");
        db.execSQL("INSERT OR REPLACE INTO MUNICIPIO VALUES (3, 3, 'Metapán');");
        db.execSQL("INSERT OR REPLACE INTO MUNICIPIO VALUES (4, 4, 'San Miguel');");
        db.execSQL("INSERT OR REPLACE INTO MUNICIPIO VALUES (5, 5, 'Usulután');");

        // 🏘️ Datos para DISTRITO
        db.execSQL("INSERT OR REPLACE INTO DISTRITO VALUES (1, 1, 1, 'Centro', '1101');");
        db.execSQL("INSERT OR REPLACE INTO DISTRITO VALUES (2, 2, 2, 'Merliot', '1102');");
        db.execSQL("INSERT OR REPLACE INTO DISTRITO VALUES (3, 3, 3, 'Metapán Norte', '1103');");
        db.execSQL("INSERT OR REPLACE INTO DISTRITO VALUES (4, 4, 4, 'Ciudad Pacífica', '1104');");
        db.execSQL("INSERT OR REPLACE INTO DISTRITO VALUES (5, 5, 5, 'Puerto Parada', '1105');");

        // 🗂️ Datos para CATEGORIAPRODUCTO
        db.execSQL("INSERT OR REPLACE INTO CATEGORIAPRODUCTO VALUES (1, 'Antojitos', 'Comida típica', 1, '15:00', '18:00');");
        db.execSQL("INSERT OR REPLACE INTO CATEGORIAPRODUCTO VALUES (2, 'Bebidas', 'Bebidas frías', 1, '00:00', '23:59');");
        db.execSQL("INSERT OR REPLACE INTO CATEGORIAPRODUCTO VALUES (3, 'Comidas rápidas', 'Hamburguesas y hotdogs', 1, '10:00', '22:00');");
        db.execSQL("INSERT OR REPLACE INTO CATEGORIAPRODUCTO VALUES (4, 'Postres', 'Dulces típicos', 1, '12:00', '20:00');");
        db.execSQL("INSERT OR REPLACE INTO CATEGORIAPRODUCTO VALUES (5, 'Platos fuertes', 'Almuerzos tradicionales', 1, '11:00', '15:00');");

        // 🍽️ Datos para PRODUCTO
        db.execSQL("INSERT OR REPLACE INTO PRODUCTO VALUES (1, 1, 'Pupusa de Queso', 'Clásica pupusa salvadoreña');");
        db.execSQL("INSERT OR REPLACE INTO PRODUCTO VALUES (2, 1, 'Pupusa Revueltas', 'Con chicharrón, queso y frijoles');");
        db.execSQL("INSERT OR REPLACE INTO PRODUCTO VALUES (3, 2, 'Horchata', 'Bebida tradicional salvadoreña');");
        db.execSQL("INSERT OR REPLACE INTO PRODUCTO VALUES (4, 3, 'Hamburguesa', 'Con papas y bebida');");
        db.execSQL("INSERT OR REPLACE INTO PRODUCTO VALUES (5, 4, 'Empanada de Plátano', 'Rellena de leche');");

        // 🏬 Datos para SUCURSAL
        db.execSQL("INSERT OR REPLACE INTO SUCURSAL VALUES (1, 1, 1, 1, 'Sucursal Centro', 'Calle El Progreso', '2200-1111', '08:00', '20:00');");
        db.execSQL("INSERT OR REPLACE INTO SUCURSAL VALUES (2, 2, 2, 2, 'Sucursal Merliot', 'Blvd. Merliot', '2200-2222', '09:00', '21:00');");
        db.execSQL("INSERT OR REPLACE INTO SUCURSAL VALUES (3, 3, 3, 3, 'Sucursal Metapán', 'Av. Central', '2200-3333', '07:00', '19:00');");
        db.execSQL("INSERT OR REPLACE INTO SUCURSAL VALUES (4, 4, 4, 4, 'Sucursal San Miguel', 'Col. Ciudad Pacífica', '2200-4444', '08:30', '19:30');");
        db.execSQL("INSERT OR REPLACE INTO SUCURSAL VALUES (5, 5, 5, 5, 'Sucursal Usulután', 'Centro Usulután', '2200-5555', '08:00', '18:00');");

        // 🧑‍🤝‍🧑 Datos para CLIENTE
        db.execSQL("INSERT OR REPLACE INTO CLIENTE VALUES (1, 200, '7010-1111', 'Carlos', 'Ramírez');");
        db.execSQL("INSERT OR REPLACE INTO CLIENTE VALUES (2, 201, '7010-2222', 'Ana', 'González');");
        db.execSQL("INSERT OR REPLACE INTO CLIENTE VALUES (3, 202, '7010-3333', 'Luis', 'Martínez');");
        db.execSQL("INSERT OR REPLACE INTO CLIENTE VALUES (4, 203, '7010-4444', 'Diana', 'López');");
        db.execSQL("INSERT OR REPLACE INTO CLIENTE VALUES (5, 204, '7010-5555', 'José', 'Hernández');");

        // 🛵 Datos para REPARTIDOR
        db.execSQL("INSERT OR REPLACE INTO REPARTIDOR VALUES (1, 300, 1, 1, 1, 'Moto', 1, '7200-0001', 'Luis', 'Gómez');");
        db.execSQL("INSERT OR REPLACE INTO REPARTIDOR VALUES (2, 301, 2, 2, 2, 'Bicicleta', 1, '7200-0002', 'Mario', 'Ruiz');");
        db.execSQL("INSERT OR REPLACE INTO REPARTIDOR VALUES (3, 302, 3, 3, 3, 'Carro', 1, '7200-0003', 'Tatiana', 'Martínez');");
        db.execSQL("INSERT OR REPLACE INTO REPARTIDOR VALUES (4, 303, 4, 4, 4, 'Moto', 1, '7200-0004', 'Kevin', 'Morales');");
        db.execSQL("INSERT OR REPLACE INTO REPARTIDOR VALUES (5, 304, 5, 5, 5, 'Camioneta', 1, '7200-0005', 'Sofía', 'Aguilar');");

        // 🎉 Datos para TIPOEVENTO
        db.execSQL("INSERT OR REPLACE INTO TIPOEVENTO VALUES (1, 'Fiesta Infantil', 'Evento privado', 30.00, 300.00);");
        db.execSQL("INSERT OR REPLACE INTO TIPOEVENTO VALUES (2, 'Reunión Empresarial', 'Coffee break', 50.00, 400.00);");
        db.execSQL("INSERT OR REPLACE INTO TIPOEVENTO VALUES (3, 'Cumpleaños', 'Celebración familiar', 25.00, 250.00);");
        db.execSQL("INSERT OR REPLACE INTO TIPOEVENTO VALUES (4, 'Boda Civil', 'Recepción sencilla', 100.00, 800.00);");
        db.execSQL("INSERT OR REPLACE INTO TIPOEVENTO VALUES (5, 'Cena de Fin de Año', 'Convivio navideño', 60.00, 600.00);");

        // 📦 Datos para PEDIDO
        db.execSQL("INSERT OR REPLACE INTO PEDIDO VALUES (1, 1, 1, 1, '2025-05-01 10:00', 'pendiente');");
        db.execSQL("INSERT OR REPLACE INTO PEDIDO VALUES (2, 2, 2, 2, '2025-05-01 11:00', 'enviado');");
        db.execSQL("INSERT OR REPLACE INTO PEDIDO VALUES (3, 3, 3, 3, '2025-05-01 12:00', 'entregado');");
        db.execSQL("INSERT OR REPLACE INTO PEDIDO VALUES (4, 4, 4, 4, '2025-05-01 13:00', 'cancelado');");
        db.execSQL("INSERT OR REPLACE INTO PEDIDO VALUES (5, 5, 5, 5, '2025-05-01 14:00', 'pendiente');");

        // 🧾 Datos para FACTURA
        db.execSQL("INSERT OR REPLACE INTO FACTURA VALUES (1, 1, '2025-05-01', 50.00, 'efectivo', 1);");
        db.execSQL("INSERT OR REPLACE INTO FACTURA VALUES (2, 2, '2025-05-01', 60.00, 'tarjeta', 1);");
        db.execSQL("INSERT OR REPLACE INTO FACTURA VALUES (3, 3, '2025-05-01', 70.00, 'bitcoin', 1);");
        db.execSQL("INSERT OR REPLACE INTO FACTURA VALUES (4, 4, '2025-05-01', 40.00, 'efectivo', 0);");
        db.execSQL("INSERT OR REPLACE INTO FACTURA VALUES (5, 5, '2025-05-01', 90.00, 'tarjeta', 1);");

        // 💳 Datos para CREDITO
        db.execSQL("INSERT OR REPLACE INTO CREDITO VALUES (1, 1, 1, 25.00, 25.00, '2025-06-01');");
        db.execSQL("INSERT OR REPLACE INTO CREDITO VALUES (2, 2, 1, 30.00, 30.00, '2025-06-01');");
        db.execSQL("INSERT OR REPLACE INTO CREDITO VALUES (3, 3, 1, 35.00, 35.00, '2025-06-01');");
        db.execSQL("INSERT OR REPLACE INTO CREDITO VALUES (4, 4, 1, 20.00, 20.00, '2025-06-01');");
        db.execSQL("INSERT OR REPLACE INTO CREDITO VALUES (5, 5, 1, 45.00, 45.00, '2025-06-01');");

        // 🛒 Datos para DATOSPRODUCTO
        db.execSQL("INSERT OR REPLACE INTO DATOSPRODUCTO VALUES (1, 1, 1, 1.00, 1);");
        db.execSQL("INSERT OR REPLACE INTO DATOSPRODUCTO VALUES (2, 1, 2, 1.25, 1);");
        db.execSQL("INSERT OR REPLACE INTO DATOSPRODUCTO VALUES (3, 1, 3, 0.75, 1);");
        db.execSQL("INSERT OR REPLACE INTO DATOSPRODUCTO VALUES (4, 1, 4, 3.50, 1);");
        db.execSQL("INSERT OR REPLACE INTO DATOSPRODUCTO VALUES (5, 1, 5, 1.75, 1);");

        // 🏠 Datos para DIRECCION
        db.execSQL("INSERT OR REPLACE INTO DIRECCION VALUES (1, 1, 1, 1, 1, 'Col. Escalón #123', 'Casa esquina azul');");
        db.execSQL("INSERT OR REPLACE INTO DIRECCION VALUES (2, 1, 2, 2, 2, 'Santa Tecla Norte', 'Casa blanca dos niveles');");
        db.execSQL("INSERT OR REPLACE INTO DIRECCION VALUES (3, 1, 3, 3, 3, 'Col. Santa Lucía', 'Apartamento 2-B');");
        db.execSQL("INSERT OR REPLACE INTO DIRECCION VALUES (4, 1, 4, 4, 4, 'Ciudad Pacífica', 'Pasaje 4');");
        db.execSQL("INSERT OR REPLACE INTO DIRECCION VALUES (5, 1, 5, 5, 5, 'Usulután centro', 'Casa colonial');");

        // 🚚 Datos para REPARTOPEDIDO
        db.execSQL("INSERT OR REPLACE INTO REPARTOPEDIDO VALUES (1, 1, 1, '2025-05-01 10:30', 'Escalón', '2025-05-01 11:00');");
        db.execSQL("INSERT OR REPLACE INTO REPARTOPEDIDO VALUES (2, 2, 1, '2025-05-01 11:30', 'Merliot', '2025-05-01 12:00');");
        db.execSQL("INSERT OR REPLACE INTO REPARTOPEDIDO VALUES (3, 3, 1, '2025-05-01 12:30', 'Metapán', '2025-05-01 13:00');");
        db.execSQL("INSERT OR REPLACE INTO REPARTOPEDIDO VALUES (4, 4, 1, '2025-05-01 13:30', 'San Miguel', '2025-05-01 14:00');");
        db.execSQL("INSERT OR REPLACE INTO REPARTOPEDIDO VALUES (5, 5, 1, '2025-05-01 14:30', 'Usulután', '2025-05-01 15:00');");

        // 📋 Datos para DETALLEPEDIDO
        db.execSQL("INSERT OR REPLACE INTO DETALLEPEDIDO VALUES (1, 1, 1, 2, 2.00);");
        db.execSQL("INSERT OR REPLACE INTO DETALLEPEDIDO VALUES (2, 2, 1, 4, 5.00);");
        db.execSQL("INSERT OR REPLACE INTO DETALLEPEDIDO VALUES (3, 3, 1, 1, 0.75);");
        db.execSQL("INSERT OR REPLACE INTO DETALLEPEDIDO VALUES (4, 4, 1, 2, 7.00);");
        db.execSQL("INSERT OR REPLACE INTO DETALLEPEDIDO VALUES (5, 5, 1, 3, 5.25);");

    }
}
