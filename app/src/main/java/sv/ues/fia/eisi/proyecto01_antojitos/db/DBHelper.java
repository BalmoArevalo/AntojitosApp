package sv.ues.fia.eisi.proyecto01_antojitos.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log; // Importar Log

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "antojitos.db";
    // Incrementar la versión si se añaden/modifican triggers o tablas
    public static final int DB_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DBHelper", "onCreate: Creando tablas y triggers para la versión " + DB_VERSION);
        // 1 - tabla de DEPARTAMENTO
        db.execSQL("CREATE TABLE DEPARTAMENTO ("
                + "ID_DEPARTAMENTO INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "NOMBRE_DEPARTAMENTO TEXT NOT NULL"
                + ");");

        // 2 - tabla de MUNICIPIO
        db.execSQL("CREATE TABLE MUNICIPIO ("
                + "ID_DEPARTAMENTO INTEGER NOT NULL,"
                + "ID_MUNICIPIO INTEGER NOT NULL,"
                + "NOMBRE_MUNICIPIO TEXT NOT NULL,"
                + "PRIMARY KEY (ID_DEPARTAMENTO, ID_MUNICIPIO),"
                + "FOREIGN KEY (ID_DEPARTAMENTO) REFERENCES DEPARTAMENTO(ID_DEPARTAMENTO)"
                + ");");

        // 3 - tabla de DISTRITO
        db.execSQL("CREATE TABLE DISTRITO ("
                + "ID_DEPARTAMENTO INTEGER NOT NULL,"
                + "ID_MUNICIPIO INTEGER NOT NULL,"
                + "ID_DISTRITO INTEGER NOT NULL,"
                + "NOMBRE_DISTRITO TEXT NOT NULL,"
                + "CODIGO_POSTAL TEXT NOT NULL,"
                + "PRIMARY KEY (ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO),"
                + "FOREIGN KEY (ID_DEPARTAMENTO, ID_MUNICIPIO) REFERENCES MUNICIPIO(ID_DEPARTAMENTO, ID_MUNICIPIO)"
                + ");");

        // 4 - tabla de CATEGORIAPRODUCTO
        db.execSQL("CREATE TABLE CATEGORIAPRODUCTO ("
                + "ID_CATEGORIAPRODUCTO INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "NOMBRE_CATEGORIA TEXT NOT NULL,"
                + "DESCRIPCION_CATEGORIA TEXT NOT NULL,"
                + "DISPONIBLE_CATEGORIA INTEGER NOT NULL,"
                + "HORA_DISPONIBLE_DESDE TEXT NOT NULL,"
                + "HORA_DISPONIBLE_HASTA TEXT NOT NULL,"
                + "ACTIVO_CATEGORIAPRODUCTO INTEGER DEFAULT 1"
                + ");");

        // 5 - tabla de PRODUCTO
        db.execSQL("CREATE TABLE PRODUCTO ("
                + "ID_PRODUCTO INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ID_CATEGORIAPRODUCTO INTEGER NOT NULL,"
                + "NOMBRE_PRODUCTO TEXT NOT NULL,"
                + "DESCRIPCION_PRODUCTO TEXT,"
                + "ACTIVO_PRODUCTO INTEGER DEFAULT 1,"
                + "FOREIGN KEY (ID_CATEGORIAPRODUCTO) REFERENCES CATEGORIAPRODUCTO(ID_CATEGORIAPRODUCTO)"
                + ");");

        // 6 - tabla de SUCURSAL
        db.execSQL("CREATE TABLE SUCURSAL ("
                + "ID_SUCURSAL INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ID_DEPARTAMENTO INTEGER NOT NULL,"
                + "ID_MUNICIPIO INTEGER NOT NULL,"
                + "ID_DISTRITO INTEGER NOT NULL,"
                + "NOMBRE_SUCURSAL TEXT NOT NULL,"
                + "DIRECCION_SUCURSAL TEXT NOT NULL,"
                + "TELEFONO_SUCURSAL TEXT NOT NULL,"
                + "HORARIO_APERTURA_SUCURSAL TEXT NOT NULL,"
                + "HORARIO_CIERRE_SUCURSAL TEXT NOT NULL,"
                + "ACTIVO_SUCURSAL INTEGER DEFAULT 1,"
                + "FOREIGN KEY (ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO) REFERENCES DISTRITO(ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO)"
                + ");");

        // 7 - tabla de CLIENTE
        db.execSQL("CREATE TABLE CLIENTE ("
                + "ID_CLIENTE INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "TELEFONO_CLIENTE TEXT NOT NULL,"
                + "NOMBRE_CLIENTE TEXT NOT NULL,"
                + "APELLIDO_CLIENTE TEXT NOT NULL,"
                + "ACTIVO_CLIENTE INTEGER DEFAULT 1"
                + ");");

        // 8 - tabla de REPARTIDOR
        db.execSQL("CREATE TABLE REPARTIDOR ("
                + "ID_REPARTIDOR INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ID_DEPARTAMENTO INTEGER,"
                + "ID_MUNICIPIO INTEGER,"
                + "ID_DISTRITO INTEGER,"
                + "TIPO_VEHICULO TEXT,"
                + "DISPONIBLE INTEGER NOT NULL," // Campo para el trigger
                + "TELEFONO_REPARTIDOR TEXT NOT NULL,"
                + "NOMBRE_REPARTIDOR TEXT NOT NULL,"
                + "APELLIDO_REPARTIDOR TEXT NOT NULL,"
                + "ACTIVO_REPARTIDOR INTEGER DEFAULT 1,"
                + "FOREIGN KEY (ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO) REFERENCES DISTRITO(ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO)"
                + ");");

        // 9 - tabla de TIPOEVENTO
        db.execSQL("CREATE TABLE TIPOEVENTO ("
                + "ID_TIPO_EVENTO INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "NOMBRE_TIPO_EVENTO TEXT NOT NULL,"
                + "DESCRIPCION_TIPO_EVENTO TEXT,"
                + "MONTO_MINIMO REAL NOT NULL,"
                + "MONTO_MAXIMO REAL NOT NULL"
                + ");");

        // 10 - tabla de PEDIDO (Relación 1:1 con Factura se define en FACTURA)
        db.execSQL("CREATE TABLE PEDIDO ("
                + "ID_PEDIDO INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ID_CLIENTE INTEGER,"
                + "ID_TIPO_EVENTO INTEGER,"
                + "ID_SUCURSAL INTEGER NOT NULL,"
                + "ID_REPARTIDOR INTEGER NOT NULL,"
                + "FECHA_HORA_PEDIDO TEXT NOT NULL,"
                + "ESTADO_PEDIDO TEXT NOT NULL," // Campo para el trigger
                + "FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE),"
                + "FOREIGN KEY (ID_TIPO_EVENTO) REFERENCES TIPOEVENTO(ID_TIPO_EVENTO),"
                + "FOREIGN KEY (ID_REPARTIDOR) REFERENCES REPARTIDOR(ID_REPARTIDOR),"
                + "FOREIGN KEY (ID_SUCURSAL) REFERENCES SUCURSAL(ID_SUCURSAL)"
                + ");");

        // NUEVA tabla de FACTURA (1:1 con PEDIDO)
        db.execSQL("CREATE TABLE FACTURA ("
                + "ID_FACTURA INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ID_PEDIDO INTEGER NOT NULL UNIQUE," // Asegura 1:1 con Pedido
                + "FECHA_EMISION TEXT NOT NULL,"
                + "MONTO_TOTAL REAL NOT NULL CHECK(MONTO_TOTAL > 0)," // Añadido CHECK sugerido
                + "TIPO_PAGO TEXT NOT NULL," // Podría tener valores como 'Contado', 'Crédito'
                + "ESTADO_FACTURA TEXT NOT NULL," // Ej: "Pendiente", "En Crédito", "Pagada", "Anulada"
                + "ES_CREDITO INTEGER NOT NULL CHECK(ES_CREDITO IN (0,1))," // 0 para No, 1 para Sí
                + "FOREIGN KEY (ID_PEDIDO) REFERENCES PEDIDO(ID_PEDIDO) ON DELETE RESTRICT ON UPDATE CASCADE" // Políticas de FK
                + ");");

        // NUEVA tabla de CREDITO
        db.execSQL("CREATE TABLE CREDITO ("
                + "ID_CREDITO INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID_FACTURA INTEGER NOT NULL UNIQUE, " // UNIQUE aquí asegura 1:1 entre Factura y su Crédito
                + "MONTO_AUTORIZADO_CREDITO REAL NOT NULL CHECK(MONTO_AUTORIZADO_CREDITO > 0), "
                + "MONTO_PAGADO REAL NOT NULL DEFAULT 0 CHECK(MONTO_PAGADO >= 0), "
                + "SALDO_PENDIENTE REAL NOT NULL CHECK(SALDO_PENDIENTE >= 0), " // Podrías añadir CHECK(SALDO_PENDIENTE <= MONTO_AUTORIZADO_CREDITO)
                + "FECHA_LIMITE_PAGO TEXT NOT NULL, "
                + "ESTADO_CREDITO TEXT NOT NULL, " // Ej: "Activo", "Pagado", "Vencido"
                + "FOREIGN KEY (ID_FACTURA) REFERENCES FACTURA(ID_FACTURA) ON DELETE RESTRICT ON UPDATE CASCADE, " // Coma al final
                + "CHECK(MONTO_PAGADO <= MONTO_AUTORIZADO_CREDITO)" // Última restricción, sin coma después
                + ");");

        // 13 - tabla de DATOSPRODUCTO
        db.execSQL("CREATE TABLE DATOSPRODUCTO ("
                + "ID_SUCURSAL INTEGER NOT NULL,"
                + "ID_PRODUCTO INTEGER NOT NULL,"
                + "PRECIO_SUCURSAL_PRODUCTO REAL NOT NULL,"
                + "DISPONIBLE_PRODUCTO INTEGER NOT NULL," // Campo para el trigger
                + "PRIMARY KEY (ID_SUCURSAL, ID_PRODUCTO),"
                + "FOREIGN KEY (ID_SUCURSAL) REFERENCES SUCURSAL(ID_SUCURSAL),"
                + "FOREIGN KEY (ID_PRODUCTO) REFERENCES PRODUCTO(ID_PRODUCTO)"
                + ");");

        // 14 - tabla de DETALLEPEDIDO (Recomendación: simplificar PK si es posible)
        db.execSQL("CREATE TABLE DETALLEPEDIDO ("
                + "ID_PRODUCTO INTEGER NOT NULL,"
                + "ID_PEDIDO INTEGER NOT NULL,"
                + "ID_DETALLA_PEDIDO INTEGER NOT NULL," // Considerar PRIMARY KEY AUTOINCREMENT aquí
                + "CANTIDAD INTEGER NOT NULL,"          // Campo para el trigger de stock
                + "SUBTOTAL REAL NOT NULL,"
                + "PRIMARY KEY (ID_PRODUCTO, ID_PEDIDO, ID_DETALLA_PEDIDO),"
                + "FOREIGN KEY (ID_PRODUCTO) REFERENCES PRODUCTO(ID_PRODUCTO),"
                + "FOREIGN KEY (ID_PEDIDO) REFERENCES PEDIDO(ID_PEDIDO)"
                + ");");

        // 15 - tabla de REPARTOPEDIDO
        db.execSQL("CREATE TABLE REPARTOPEDIDO ("
                + "ID_REPARTIDOR INTEGER NOT NULL,"
                + "ID_PEDIDO INTEGER NOT NULL,"
                + "ID_REPARTO_PEDIDO INTEGER NOT NULL,"
                + "HORA_ASIGNACION TEXT NOT NULL,"
                + "UBICACION_ENTREGA TEXT NOT NULL,"
                + "FECHA_HORA_ENTREGA TEXT,"
                + "PRIMARY KEY (ID_REPARTIDOR, ID_PEDIDO, ID_REPARTO_PEDIDO),"
                + "FOREIGN KEY (ID_REPARTIDOR) REFERENCES REPARTIDOR(ID_REPARTIDOR),"
                + "FOREIGN KEY (ID_PEDIDO) REFERENCES PEDIDO(ID_PEDIDO)"
                + ");");

        // 16 - tabla de DIRECCION
        db.execSQL("CREATE TABLE DIRECCION ("
                + "ID_CLIENTE INTEGER NOT NULL,"
                + "ID_DIRECCION INTEGER NOT NULL,"
                + "ID_DEPARTAMENTO INTEGER NOT NULL,"
                + "ID_MUNICIPIO INTEGER NOT NULL,"
                + "ID_DISTRITO INTEGER NOT NULL,"
                + "DIRECCION_ESPECIFICA TEXT NOT NULL,"
                + "DESCRIPCION_DIRECCION TEXT,"
                + "PRIMARY KEY (ID_CLIENTE, ID_DIRECCION),"
                + "FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE),"
                + "FOREIGN KEY (ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO) REFERENCES DISTRITO(ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO)"
                + ");");

        Log.i("DBHelper", "Tablas creadas.");
/*
        // --- TRIGGERS ---
        Log.i("DBHelper", "Creando triggers...");
        // 1) Actualiza estado de pedido y disponibilidad de repartidor al pagar factura
        db.execSQL("CREATE TRIGGER trg_actualizar_estado_pedido_factura_pagada "
                + "AFTER UPDATE OF PAGADO ON FACTURA "
                + "FOR EACH ROW WHEN NEW.PAGADO = 1 AND OLD.PAGADO != 1 BEGIN "
                + "  UPDATE PEDIDO SET ESTADO_PEDIDO = 'entregado' WHERE ID_PEDIDO = NEW.ID_PEDIDO; "
                + "  UPDATE REPARTIDOR SET DISPONIBLE = 1 WHERE ID_REPARTIDOR = (SELECT P.ID_REPARTIDOR FROM PEDIDO P WHERE P.ID_PEDIDO = NEW.ID_PEDIDO); "
                + "END");

        // 2) Marca factura pagada cuando crédito se agota
        db.execSQL("CREATE TRIGGER trg_factura_pagada_por_credito "
                + "AFTER UPDATE OF SALDO_PENDIENTE ON CREDITO "
                + "FOR EACH ROW WHEN NEW.SALDO_PENDIENTE = 0 AND OLD.SALDO_PENDIENTE != 0 BEGIN "
                + "  UPDATE FACTURA SET PAGADO = 1 WHERE ID_FACTURA = NEW.ID_FACTURA; "
                + "END;");

        // 3) Validaciones semánticas: Estado de pedido
        db.execSQL("CREATE TRIGGER trg_estado_pedido_valido "
                + "BEFORE INSERT ON PEDIDO "
                + "FOR EACH ROW WHEN NEW.ESTADO_PEDIDO NOT IN ('pendiente','enviado','entregado','cancelado') BEGIN "
                + "  SELECT RAISE(ABORT, 'Estado del pedido no es válido. Valores permitidos: pendiente, enviado, entregado, cancelado.'); "
                + "END;");

        // 4) Validaciones semánticas: Monto de factura
        db.execSQL("CREATE TRIGGER trg_validar_monto_factura "
                + "BEFORE INSERT ON FACTURA "
                + "FOR EACH ROW WHEN NEW.MONTO_TOTAL <= 0 BEGIN "
                + "  SELECT RAISE(ABORT, 'El monto total de la factura debe ser mayor a cero.'); "
                + "END;");

        // 5) Evitar eliminar cliente con pedidos activos
        db.execSQL("CREATE TRIGGER trg_evitar_borrar_cliente_con_pedidos "
                + "BEFORE DELETE ON CLIENTE "
                + "FOR EACH ROW WHEN EXISTS(SELECT 1 FROM PEDIDO WHERE ID_CLIENTE = OLD.ID_CLIENTE AND ESTADO_PEDIDO NOT IN ('entregado','cancelado')) BEGIN "
                + "  SELECT RAISE(ABORT, 'No se puede eliminar el cliente porque tiene pedidos activos.'); "
                + "END;");

        // 6) Reducir stock de producto al insertar detalle de pedido
        db.execSQL("CREATE TRIGGER trg_reducir_stock_producto "
                + "AFTER INSERT ON DETALLEPEDIDO "
                + "FOR EACH ROW BEGIN "
                + "  UPDATE DATOSPRODUCTO SET DISPONIBLE_PRODUCTO = DISPONIBLE_PRODUCTO - NEW.CANTIDAD "
                + "  WHERE ID_PRODUCTO = NEW.ID_PRODUCTO AND ID_SUCURSAL = (SELECT P.ID_SUCURSAL FROM PEDIDO P WHERE P.ID_PEDIDO = NEW.ID_PEDIDO); "
                + "END;");

        // 7) Evitar eliminar un PEDIDO si tiene una FACTURA asociada
        db.execSQL("CREATE TRIGGER trg_evitar_borrar_pedido_con_factura "
                + "BEFORE DELETE ON PEDIDO "
                + "FOR EACH ROW WHEN EXISTS (SELECT 1 FROM FACTURA WHERE ID_PEDIDO = OLD.ID_PEDIDO) BEGIN "
                + "  SELECT RAISE(ABORT, 'No se puede eliminar el pedido porque tiene una factura asociada. Elimine la factura primero.'); "
                + "END;");
        Log.i("DBHelper", "Triggers creados.");
*/
        // LLAMADA AL SEEDER (Después de crear tablas y triggers)
        try {
            Log.i("DBHelper_OnCreate", "Llamando a BaseDatosSeeder.insertarDatosIniciales...");
            BaseDatosSeeder.insertarDatosIniciales(db);
            Log.i("DBHelper_OnCreate", "BaseDatosSeeder.insertarDatosIniciales completado.");
        } catch (Exception e) {
            Log.e("DBHelper_OnCreate", "Error al insertar datos iniciales", e);
            // throw new RuntimeException("Error en onCreate al llamar al Seeder", e); // Descomentar si quieres que la app falle si el seeder falla
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DBHelper", "Actualizando base de datos de la versión " + oldVersion + " a " + newVersion + ", se eliminarán los datos antiguos.");
        // eliminar tablas en orden inverso para evitar problemas de FK
        db.execSQL("DROP TABLE IF EXISTS DETALLEPEDIDO;");
        db.execSQL("DROP TABLE IF EXISTS REPARTOPEDIDO;");
        db.execSQL("DROP TABLE IF EXISTS DIRECCION;");
        db.execSQL("DROP TABLE IF EXISTS DATOSPRODUCTO;");
        db.execSQL("DROP TABLE IF EXISTS CREDITO;");
        db.execSQL("DROP TABLE IF EXISTS FACTURA;");
        db.execSQL("DROP TABLE IF EXISTS PEDIDO;");
        db.execSQL("DROP TABLE IF EXISTS TIPOEVENTO;");
        db.execSQL("DROP TABLE IF EXISTS REPARTIDOR;");
        db.execSQL("DROP TABLE IF EXISTS CLIENTE;");
        db.execSQL("DROP TABLE IF EXISTS SUCURSAL;");
        db.execSQL("DROP TABLE IF EXISTS PRODUCTO;");
        db.execSQL("DROP TABLE IF EXISTS CATEGORIAPRODUCTO;");
        db.execSQL("DROP TABLE IF EXISTS DISTRITO;");
        db.execSQL("DROP TABLE IF EXISTS MUNICIPIO;");
        db.execSQL("DROP TABLE IF EXISTS DEPARTAMENTO;");

        // Volver a crear la estructura
        onCreate(db);
    }
}