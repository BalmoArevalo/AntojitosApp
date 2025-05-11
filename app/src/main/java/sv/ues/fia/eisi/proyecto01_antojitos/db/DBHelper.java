package sv.ues.fia.eisi.proyecto01_antojitos.db;
import sv.ues.fia.eisi.proyecto01_antojitos.db.seeders.SeguridadSeeder;
import sv.ues.fia.eisi.proyecto01_antojitos.db.seeders.DatosInicialesSeeder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "antojitos.db";
    // Incrementar la versión si se añaden/modifican triggers o tablas
    public static final int DB_VERSION = 7;

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

        /* 2. Negocio */
        crearTablasSeguridad(db);
        crearTablasNegocio(db);

        Log.i("DBHelper", "Estructura y datos iniciales creados.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DBHelper", "Actualizando base de datos de la versión " + oldVersion + " a " + newVersion + ", se eliminarán los datos antiguos.");
        // eliminar tablas en orden inverso para evitar problemas de FK
        dropTablasNegocio(db);
        dropTablasSeguridad(db);
        onCreate(db);
    }

    private void crearTablasSeguridad(SQLiteDatabase db) {
        /* ======================  TABLAS DE SEGURIDAD  ====================== */

        // tabla USUARIO
        db.execSQL("CREATE TABLE USUARIO ("
                + "ID_USUARIO TEXT PRIMARY KEY,"
                + "NOM_USUARIO TEXT NOT NULL,"
                + "CLAVE TEXT NOT NULL"
                + ");");

        //tabla OPCIONCRUD
        db.execSQL("CREATE TABLE OPCIONCRUD ("
                + "ID_OPCION TEXT PRIMARY KEY,"
                + "DES_OPCION TEXT NOT NULL,"
                + "NUM_CRUD INTEGER NOT NULL"       // 1-Crear, 2-Consultar, 3-Editar, 4-Eliminar
                + ");");

        //tabla ACCESOUSUARIO (FK a las dos anteriores)
        db.execSQL("CREATE TABLE ACCESOUSUARIO ("
                + "ID_OPCION TEXT NOT NULL,"
                + "ID_USUARIO TEXT NOT NULL,"
                + "PRIMARY KEY (ID_OPCION, ID_USUARIO),"
                + "FOREIGN KEY (ID_OPCION) REFERENCES OPCIONCRUD(ID_OPCION),"
                + "FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO(ID_USUARIO)"
                + ");");

        SeguridadSeeder.poblar(db);   // ← inserta roles, permisos, accesos
    }

    private void crearTablasNegocio(SQLiteDatabase db) {
        /* -------- TABLAS DE NEGOCIO -------- */
        // 1 - tabla de DEPARTAMENTO
        db.execSQL("CREATE TABLE DEPARTAMENTO ("
                + "ID_DEPARTAMENTO INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "NOMBRE_DEPARTAMENTO TEXT NOT NULL,"
                + "ACTIVO_DEPARTAMENTO INTEGER DEFAULT 1"
                + ");");

        // 2 - tabla de MUNICIPIO
        /*db.execSQL("CREATE TABLE MUNICIPIO ("
                + "ID_MUNICIPIO INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ID_DEPARTAMENTO INTEGER NOT NULL,"
                + "NOMBRE_MUNICIPIO TEXT NOT NULL,"
                + "ACTIVO_MUNICIPIO INTEGER DEFAULT 1,"
                + "FOREIGN KEY (ID_DEPARTAMENTO) REFERENCES DEPARTAMENTO(ID_DEPARTAMENTO)"
                + ");");*/
        db.execSQL("CREATE TABLE MUNICIPIO ("
                + "ID_DEPARTAMENTO INTEGER NOT NULL,"
                + "ID_MUNICIPIO INTEGER NOT NULL,"
                + "NOMBRE_MUNICIPIO TEXT NOT NULL,"
                + "ACTIVO_MUNICIPIO INTEGER DEFAULT 1,"
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
                + "ACTIVO_DISTRITO INTEGER DEFAULT 1,"
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
                + "MONTO_MAXIMO REAL NOT NULL,"
                + "ACTIVO_TIPOEVENTO INTEGER NOT NULL DEFAULT 1"
                + ");");

        // 10 - tabla de PEDIDO (Relación 1:1 con Factura se define en FACTURA)
        db.execSQL("CREATE TABLE PEDIDO ("
                + "ID_PEDIDO INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ID_CLIENTE INTEGER,"
                + "ID_TIPO_EVENTO INTEGER,"
                + "ID_SUCURSAL INTEGER NOT NULL,"
                + "ID_REPARTIDOR INTEGER NOT NULL,"
                + "FECHA_HORA_PEDIDO TEXT NOT NULL,"
                + "ESTADO_PEDIDO TEXT NOT NULL,"
                + "ACTIVO_PEDIDO INTEGER NOT NULL DEFAULT 1,"
                + "FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE),"
                + "FOREIGN KEY (ID_TIPO_EVENTO) REFERENCES TIPOEVENTO(ID_TIPO_EVENTO),"
                + "FOREIGN KEY (ID_REPARTIDOR) REFERENCES REPARTIDOR(ID_REPARTIDOR),"
                + "FOREIGN KEY (ID_SUCURSAL) REFERENCES SUCURSAL(ID_SUCURSAL)"
                + ");");

        // NUEVA tabla de FACTURA (1:1 con PEDIDO)
        db.execSQL("CREATE TABLE FACTURA ("
                + "ID_FACTURA INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ID_PEDIDO INTEGER NOT NULL UNIQUE,"
                + "FECHA_EMISION TEXT NOT NULL,"
                + "MONTO_TOTAL REAL NOT NULL CHECK(MONTO_TOTAL > 0),"
                + "TIPO_PAGO TEXT NOT NULL,"
                + "ESTADO_FACTURA TEXT NOT NULL,"
                + "ES_CREDITO INTEGER NOT NULL CHECK(ES_CREDITO IN (0,1)),"
                + "FOREIGN KEY (ID_PEDIDO) REFERENCES PEDIDO(ID_PEDIDO) ON DELETE RESTRICT ON UPDATE CASCADE"
                + ");");

        // NUEVA tabla de CREDITO
        db.execSQL("CREATE TABLE CREDITO ("
                + "ID_CREDITO INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID_FACTURA INTEGER NOT NULL UNIQUE, "
                + "MONTO_AUTORIZADO_CREDITO REAL NOT NULL CHECK(MONTO_AUTORIZADO_CREDITO > 0), "
                + "MONTO_PAGADO REAL NOT NULL DEFAULT 0 CHECK(MONTO_PAGADO >= 0), "
                + "SALDO_PENDIENTE REAL NOT NULL CHECK(SALDO_PENDIENTE >= 0), "
                + "FECHA_LIMITE_PAGO TEXT NOT NULL, "
                + "ESTADO_CREDITO TEXT NOT NULL, "
                + "FOREIGN KEY (ID_FACTURA) REFERENCES FACTURA(ID_FACTURA) ON DELETE RESTRICT ON UPDATE CASCADE, "
                + "CHECK(MONTO_PAGADO <= MONTO_AUTORIZADO_CREDITO)"
                + ");");

        // 13 - tabla de DATOSPRODUCTO
        db.execSQL("CREATE TABLE DATOSPRODUCTO ("
                + "ID_SUCURSAL INTEGER NOT NULL,"
                + "ID_PRODUCTO INTEGER NOT NULL,"
                + "PRECIO_SUCURSAL_PRODUCTO REAL NOT NULL,"
                + "STOCK INTEGER NOT NULL,"
                + "ACTIVO_DATOSPRODUCTO INTEGER NOT NULL DEFAULT 1,"
                + "PRIMARY KEY (ID_SUCURSAL, ID_PRODUCTO),"
                + "FOREIGN KEY (ID_SUCURSAL) REFERENCES SUCURSAL(ID_SUCURSAL),"
                + "FOREIGN KEY (ID_PRODUCTO) REFERENCES PRODUCTO(ID_PRODUCTO)"
                + ");");

        // 14 - tabla de DETALLEPEDIDO
        db.execSQL("CREATE TABLE DETALLEPEDIDO ("
                + "ID_DETALLE_PEDIDO INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ID_PRODUCTO INTEGER NOT NULL,"
                + "ID_PEDIDO INTEGER NOT NULL,"
                + "CANTIDAD INTEGER NOT NULL,"
                + "SUBTOTAL REAL NOT NULL,"
                + "FOREIGN KEY (ID_PRODUCTO) REFERENCES PRODUCTO(ID_PRODUCTO),"
                + "FOREIGN KEY (ID_PEDIDO) REFERENCES PEDIDO(ID_PEDIDO)"
                + ");");

        // 15 - tabla de REPARTOPEDIDO
        db.execSQL("CREATE TABLE REPARTOPEDIDO ("
                + "ID_PEDIDO INTEGER NOT NULL,"
                + "ID_REPARTO_PEDIDO INTEGER NOT NULL,"
                + "HORA_ASIGNACION TEXT NOT NULL,"
                + "UBICACION_ENTREGA TEXT NOT NULL,"
                + "FECHA_HORA_ENTREGA TEXT,"
                + "PRIMARY KEY (ID_PEDIDO, ID_REPARTO_PEDIDO),"
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
                + "ACTIVO_DIRECCION INTEGER NOT NULL DEFAULT 1,"
                + "PRIMARY KEY (ID_CLIENTE, ID_DIRECCION),"
                + "FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE),"
                + "FOREIGN KEY (ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO) REFERENCES DISTRITO(ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO)"
                + ");");
    }

    private void dropTablasNegocio(SQLiteDatabase db) {
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
    }

    private void dropTablasSeguridad(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS ACCESOUSUARIO;");
        db.execSQL("DROP TABLE IF EXISTS OPCIONCRUD;");
        db.execSQL("DROP TABLE IF EXISTS USUARIO;");
    }
}
