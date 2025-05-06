package sv.ues.fia.eisi.proyecto01_antojitos.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "antojitos.db";
    public static final int DB_VERSION = 2;

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
                + "ID_USUARIO INTEGER NOT NULL,"
                + "TELEFONO_CLIENTE TEXT NOT NULL,"
                + "NOMBRE_CLIENTE TEXT NOT NULL,"
                + "APELLIDO_CLIENTE TEXT NOT NULL,"
                + "ACTIVO_CLIENTE INTEGER DEFAULT 1"
                + ");");

        // 8 - tabla de REPARTIDOR
        db.execSQL("CREATE TABLE REPARTIDOR ("
                + "ID_REPARTIDOR INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ID_USUARIO INTEGER NOT NULL,"
                + "ID_DEPARTAMENTO INTEGER,"
                + "ID_MUNICIPIO INTEGER,"
                + "ID_DISTRITO INTEGER,"
                + "TIPO_VEHICULO TEXT,"
                + "DISPONIBLE INTEGER NOT NULL,"
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

        // 10 - tabla de PEDIDO
        db.execSQL("CREATE TABLE PEDIDO ("
                + "ID_PEDIDO INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ID_CLIENTE INTEGER,"
                + "ID_TIPO_EVENTO INTEGER,"
                + "ID_SUCURSAL INTEGER NOT NULL,"
                + "ID_REPARTIDOR INTEGER NOT NULL,"
                + "FECHA_HORA_PEDIDO TEXT NOT NULL,"
                + "ESTADO_PEDIDO TEXT NOT NULL,"
                + "FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE),"
                + "FOREIGN KEY (ID_TIPO_EVENTO) REFERENCES TIPOEVENTO(ID_TIPO_EVENTO),"
                + "FOREIGN KEY (ID_REPARTIDOR) REFERENCES REPARTIDOR(ID_REPARTIDOR),"
                + "FOREIGN KEY (ID_SUCURSAL) REFERENCES SUCURSAL(ID_SUCURSAL)"
                + ");");

        // 11 - tabla de FACTURA
        db.execSQL("CREATE TABLE FACTURA ("
                + "ID_PEDIDO INTEGER NOT NULL,"
                + "ID_FACTURA INTEGER NOT NULL,"
                + "FECHA_EMISION TEXT NOT NULL,"
                + "MONTO_TOTAL REAL NOT NULL,"
                + "TIPO_PAGO TEXT NOT NULL,"
                + "PAGADO INTEGER NOT NULL,"
                + "PRIMARY KEY (ID_PEDIDO, ID_FACTURA),"
                + "FOREIGN KEY (ID_PEDIDO) REFERENCES PEDIDO(ID_PEDIDO)"
                + ");");

        // 12 - tabla de CREDITO
        db.execSQL("CREATE TABLE CREDITO ("
                + "ID_PEDIDO INTEGER NOT NULL,"
                + "ID_FACTURA INTEGER NOT NULL,"
                + "ID_CREDITO INTEGER NOT NULL,"
                + "MONTO_PAGADO REAL NOT NULL,"
                + "SALDO_PENDIENTE REAL NOT NULL,"
                + "FECHA_LIMITE_PAGO TEXT NOT NULL,"
                + "PRIMARY KEY (ID_PEDIDO, ID_FACTURA, ID_CREDITO),"
                + "FOREIGN KEY (ID_PEDIDO, ID_FACTURA) REFERENCES FACTURA(ID_PEDIDO, ID_FACTURA)"
                + ");");

        // 13 - tabla de DATOSPRODUCTO
        db.execSQL("CREATE TABLE DATOSPRODUCTO ("
                + "ID_SUCURSAL INTEGER NOT NULL,"
                + "ID_PRODUCTO INTEGER NOT NULL,"
                + "PRECIO_SUCURSAL_PRODUCTO REAL NOT NULL,"
                + "DISPONIBLE_PRODUCTO INTEGER NOT NULL,"
                + "PRIMARY KEY (ID_SUCURSAL, ID_PRODUCTO),"
                + "FOREIGN KEY (ID_SUCURSAL) REFERENCES SUCURSAL(ID_SUCURSAL),"
                + "FOREIGN KEY (ID_PRODUCTO) REFERENCES PRODUCTO(ID_PRODUCTO)"
                + ");");

        // 14 - tabla de DETALLEPEDIDO
        db.execSQL("CREATE TABLE DETALLEPEDIDO ("
                + "ID_PRODUCTO INTEGER NOT NULL,"
                + "ID_PEDIDO INTEGER NOT NULL,"
                + "ID_DETALLA_PEDIDO INTEGER NOT NULL,"
                + "CANTIDAD INTEGER NOT NULL,"
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

        // TRIGGERS DE ACTUALIZACIÓN

        // trigger: actualizar estado del pedido a 'entregado' cuando la factura se paga
        db.execSQL("CREATE TRIGGER trg_actualizar_estado_pedido_factura_pagada "
                + "AFTER UPDATE OF PAGADO ON FACTURA "
                + "FOR EACH ROW WHEN NEW.PAGADO = 1 BEGIN "
                + "UPDATE PEDIDO SET ESTADO_PEDIDO = 'entregado' WHERE ID_PEDIDO = NEW.ID_PEDIDO; "
                + "END;");

        // trigger: reducir stock cuando se inserta un detalle de pedido
        db.execSQL("CREATE TRIGGER trg_reducir_stock_producto "
                + "AFTER INSERT ON DETALLEPEDIDO "
                + "FOR EACH ROW BEGIN "
                + "UPDATE DATOSPRODUCTO SET DISPONIBLE_PRODUCTO = DISPONIBLE_PRODUCTO - NEW.CANTIDAD "
                + "WHERE ID_PRODUCTO = NEW.ID_PRODUCTO AND ID_SUCURSAL = (SELECT ID_SUCURSAL FROM PEDIDO WHERE ID_PEDIDO = NEW.ID_PEDIDO); "
                + "END;");

        // trigger: bloquear cambio de metodo de pago si ya está pagado
        db.execSQL("CREATE TRIGGER trg_bloquear_cambio_metodo_pago_si_pagado "
                + "BEFORE UPDATE OF TIPO_PAGO ON FACTURA "
                + "FOR EACH ROW WHEN OLD.PAGADO = 1 BEGIN "
                + "SELECT RAISE(ABORT, 'No se puede cambiar el método de pago una vez pagado'); "
                + "END;");

        // TRIGGERS SEMÁNTICOS

        // trigger: evitar registrar un pedido con estado inválido
        db.execSQL("CREATE TRIGGER trg_estado_pedido_valido "
                + "BEFORE INSERT ON PEDIDO "
                + "FOR EACH ROW WHEN NEW.ESTADO_PEDIDO NOT IN ('pendiente','enviado','entregado','cancelado') BEGIN "
                + "SELECT RAISE(ABORT, 'Estado del pedido no es válido'); "
                + "END;");

        // trigger: asegurar que el monto de una factura sea positivo
        db.execSQL("CREATE TRIGGER trg_validar_monto_factura "
                + "BEFORE INSERT ON FACTURA "
                + "FOR EACH ROW WHEN NEW.MONTO_TOTAL <= 0 BEGIN "
                + "SELECT RAISE(ABORT, 'El monto total de la factura debe ser mayor a cero'); "
                + "END;");

        // trigger: evitar eliminar un pedido si tiene factura asociada
        db.execSQL("CREATE TRIGGER trg_evitar_borrar_pedido_con_factura "
                + "BEFORE DELETE ON PEDIDO "
                + "FOR EACH ROW WHEN EXISTS (SELECT 1 FROM FACTURA WHERE ID_PEDIDO = OLD.ID_PEDIDO) BEGIN "
                + "SELECT RAISE(ABORT, 'No se puede eliminar el pedido porque tiene una factura asociada'); "
                + "END;");

        // trigger: evitar eliminar un cliente si tiene pedidos activos
        db.execSQL("CREATE TRIGGER trg_evitar_borrar_cliente_con_pedidos "
                + "BEFORE DELETE ON CLIENTE "
                + "FOR EACH ROW WHEN EXISTS (SELECT 1 FROM PEDIDO WHERE ID_CLIENTE = OLD.ID_CLIENTE AND ESTADO_PEDIDO NOT IN ('entregado','cancelado')) BEGIN "
                + "SELECT RAISE(ABORT, 'No se puede eliminar el cliente porque tiene pedidos activos'); "
                + "END;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // eliminar tablas en orden inverso
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
        onCreate(db);
    }
}
