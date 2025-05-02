package sv.ues.fia.eisi.proyecto01_antojitos.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "antojitos.db";
    public static final int DB_VERSION = 1;

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
        // Crear tabla DEPARTAMENTO
        db.execSQL("CREATE TABLE DEPARTAMENTO (" +
                "ID_DEPARTAMENTO INTEGER PRIMARY KEY," +
                "NOMBRE_DEPARTAMENTO TEXT NOT NULL);");

        // Crear tabla MUNICIPIO
        db.execSQL("CREATE TABLE MUNICIPIO (" +
                "ID_DEPARTAMENTO INTEGER NOT NULL," +
                "ID_MUNICIPIO INTEGER NOT NULL," +
                "NOMBRE_MUNICIPIO TEXT NOT NULL," +
                "PRIMARY KEY (ID_DEPARTAMENTO, ID_MUNICIPIO)," +
                "FOREIGN KEY (ID_DEPARTAMENTO) REFERENCES DEPARTAMENTO(ID_DEPARTAMENTO));");

        // Crear tabla DISTRITO
        db.execSQL("CREATE TABLE DISTRITO (" +
                "ID_DEPARTAMENTO INTEGER NOT NULL," +
                "ID_MUNICIPIO INTEGER NOT NULL," +
                "ID_DISTRITO INTEGER NOT NULL," +
                "NOMBRE_DISTRITO TEXT NOT NULL," +
                "CODIGO_POSTAL TEXT NOT NULL," +
                "PRIMARY KEY (ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO)," +
                "FOREIGN KEY (ID_DEPARTAMENTO, ID_MUNICIPIO) REFERENCES MUNICIPIO(ID_DEPARTAMENTO, ID_MUNICIPIO));");

        // Crear tabla CATEGORIAPRODUCTO
        db.execSQL("CREATE TABLE CATEGORIAPRODUCTO (" +
                "ID_CATEGORIAPRODUCTO INTEGER PRIMARY KEY," +
                "NOMBRE_CATEGORIA TEXT NOT NULL," +
                "DESCRIPCION_CATEGORIA TEXT NOT NULL," +
                "DISPONIBLE_CATEGORIA INTEGER NOT NULL," +
                "HORA_DISPONIBLE_DESDE TEXT NOT NULL," +
                "HORA_DISPONIBLE_HASTA TEXT NOT NULL);");

        // Crear tabla PRODUCTO
        db.execSQL("CREATE TABLE PRODUCTO (" +
                "ID_PRODUCTO INTEGER PRIMARY KEY," +
                "ID_CATEGORIAPRODUCTO INTEGER NOT NULL," +
                "NOMBRE_PRODUCTO TEXT NOT NULL," +
                "DESCRIPCION_PRODUCTO TEXT," +
                "FOREIGN KEY (ID_CATEGORIAPRODUCTO) REFERENCES CATEGORIAPRODUCTO(ID_CATEGORIAPRODUCTO));");

        // Crear tabla SUCURSAL
        db.execSQL("CREATE TABLE SUCURSAL (" +
                "ID_SUCURSAL INTEGER PRIMARY KEY," +
                "ID_DEPARTAMENTO INTEGER NOT NULL," +
                "ID_MUNICIPIO INTEGER NOT NULL," +
                "ID_DISTRITO INTEGER NOT NULL," +
                "ID_USUARIO INTEGER NOT NULL," +
                "NOMBRE_SUCURSAL TEXT NOT NULL," +
                "DIRECCION_SUCURSAL TEXT NOT NULL," +
                "TELEFONO_SUCURSAL TEXT NOT NULL," +
                "HORARIO_APERTURA_SUCURSAL TEXT NOT NULL," +
                "HORARIO_CIERRE_SUCURSAL TEXT NOT NULL," +
                "FOREIGN KEY (ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO) REFERENCES DISTRITO(ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO));");

        // Crear tabla CLIENTE
        db.execSQL("CREATE TABLE CLIENTE (" +
                "ID_CLIENTE INTEGER PRIMARY KEY," +
                "ID_USUARIO INTEGER NOT NULL," +
                "TELEFONO_CLIENTE TEXT NOT NULL," +
                "NOMBRE_CLIENTE TEXT NOT NULL," +
                "APELLIDO_CLIIENTE TEXT NOT NULL);");

        // Crear tabla REPARTIDOR
        db.execSQL("CREATE TABLE REPARTIDOR (" +
                "ID_REPARTIDOR INTEGER PRIMARY KEY," +
                "ID_USUARIO INTEGER NOT NULL," +
                "ID_DEPARTAMENTO INTEGER," +
                "ID_MUNICIPIO INTEGER," +
                "ID_DISTRITO INTEGER," +
                "TIPO_VEHICULO TEXT," +
                "DISPONIBLE INTEGER NOT NULL," +
                "TELEFONO_REPARTIDOR TEXT NOT NULL," +
                "NOMBRE_REPARTIDOR TEXT NOT NULL," +
                "APELLIDO_REPARTIDOR TEXT NOT NULL," +
                "FOREIGN KEY (ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO) REFERENCES DISTRITO(ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO));");

        // Crear tabla TIPOEVENTO
        db.execSQL("CREATE TABLE TIPOEVENTO (" +
                "ID_TIPO_EVENTO INTEGER PRIMARY KEY," +
                "NOMBRE_TIPO_EVENTO TEXT NOT NULL," +
                "DESCRIPCION_TIPO_EVENTO TEXT," +
                "MONTO_MINIMO REAL NOT NULL," +
                "MONOTO_MAXIMO REAL NOT NULL);");

        // Crear tabla PEDIDO
        db.execSQL("CREATE TABLE PEDIDO (" +
                "ID_PEDIDO INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ID_CLIENTE INTEGER," +
                "ID_TIPO_EVENTO INTEGER," +
                "ID_REPARTIDOR INTEGER NOT NULL," +
                "FECHA_HORA_PEDIDO TEXT NOT NULL," +
                "ESTADO_PEDIDO TEXT NOT NULL," +
                "FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE)," +
                "FOREIGN KEY (ID_TIPO_EVENTO) REFERENCES TIPOEVENTO(ID_TIPO_EVENTO)," +
                "FOREIGN KEY (ID_REPARTIDOR) REFERENCES REPARTIDOR(ID_REPARTIDOR));");

        // Crear tabla FACTURA
        db.execSQL("CREATE TABLE FACTURA (" +
                "ID_PEDIDO INTEGER NOT NULL," +
                "ID_FACTURA INTEGER NOT NULL," +
                "FECHA_EMISION TEXT NOT NULL," +
                "MONTO_TOTAL REAL NOT NULL," +
                "TIPO_PAGO TEXT NOT NULL," +
                "PAGADO INTEGER NOT NULL," +
                "PRIMARY KEY (ID_PEDIDO, ID_FACTURA)," +
                "FOREIGN KEY (ID_PEDIDO) REFERENCES PEDIDO(ID_PEDIDO));");

        // Crear tabla CREDITO
        db.execSQL("CREATE TABLE CREDITO (" +
                "ID_PEDIDO INTEGER NOT NULL," +
                "ID_FACTURA INTEGER NOT NULL," +
                "ID_CREDITO INTEGER NOT NULL," +
                "MONTO_PAGADO REAL NOT NULL," +
                "SALDO_PENDIENTE REAL NOT NULL," +
                "FECHA_LIMITE_PAGO TEXT NOT NULL," +
                "PRIMARY KEY (ID_PEDIDO, ID_FACTURA, ID_CREDITO)," +
                "FOREIGN KEY (ID_PEDIDO, ID_FACTURA) REFERENCES FACTURA(ID_PEDIDO, ID_FACTURA));");

        // Crear tabla DATOSPRODUCTO
        db.execSQL("CREATE TABLE DATOSPRODUCTO (" +
                "ID_SUCURSAL INTEGER NOT NULL," +
                "ID_LISTAPRODUCTO INTEGER NOT NULL," +
                "ID_PRODUCTO INTEGER NOT NULL," +
                "PRECIO_SUCURSAL_LISTAPRODUCTO REAL NOT NULL," +
                "DISPONIBLE_LISTAPRODUCTO INTEGER NOT NULL," +
                "PRIMARY KEY (ID_SUCURSAL, ID_LISTAPRODUCTO)," +
                "FOREIGN KEY (ID_SUCURSAL) REFERENCES SUCURSAL(ID_SUCURSAL)," +
                "FOREIGN KEY (ID_PRODUCTO) REFERENCES PRODUCTO(ID_PRODUCTO));");

        // Crear tabla DIRECCION
        db.execSQL("CREATE TABLE DIRECCION (" +
                "ID_CLIENTE INTEGER NOT NULL," +
                "ID_DIRECCION INTEGER NOT NULL," +
                "ID_DEPARTAMENTO INTEGER NOT NULL," +
                "ID_MUNICIPIO INTEGER NOT NULL," +
                "ID_DISTRITO INTEGER NOT NULL," +
                "DIRECCION_ESPECIFICA TEXT NOT NULL," +
                "DESCRIPCION_DIRECCION TEXT," +
                "PRIMARY KEY (ID_CLIENTE, ID_DIRECCION)," +
                "FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE)," +
                "FOREIGN KEY (ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO) REFERENCES DISTRITO(ID_DEPARTAMENTO, ID_MUNICIPIO, ID_DISTRITO));");

        // Crear tabla REPARTOPEDIDO
        db.execSQL("CREATE TABLE REPARTOPEDIDO (" +
                "ID_REPARTIDOR INTEGER NOT NULL," +
                "ID_PEDIDO INTEGER NOT NULL," +
                "ID_REPARTO_PEDIDO INTEGER NOT NULL," +
                "HORA_ASIGNACION TEXT NOT NULL," +
                "UBICACION_ENTREGA TEXT NOT NULL," +
                "FECHA_HORA_ENTREGA TEXT," +
                "PRIMARY KEY (ID_REPARTIDOR, ID_PEDIDO, ID_REPARTO_PEDIDO)," +
                "FOREIGN KEY (ID_REPARTIDOR) REFERENCES REPARTIDOR(ID_REPARTIDOR)," +
                "FOREIGN KEY (ID_PEDIDO) REFERENCES PEDIDO(ID_PEDIDO));");

        // Crear tabla DETALLEPEDIDO
        db.execSQL("CREATE TABLE DETALLEPEDIDO (" +
                "ID_PRODUCTO INTEGER NOT NULL," +
                "ID_PEDIDO INTEGER NOT NULL," +
                "ID_DETALLA_PEDIDO INTEGER NOT NULL," +
                "CANTIDAD INTEGER NOT NULL," +
                "SUBTOTAL REAL NOT NULL," +
                "PRIMARY KEY (ID_PRODUCTO, ID_PEDIDO, ID_DETALLA_PEDIDO)," +
                "FOREIGN KEY (ID_PRODUCTO) REFERENCES PRODUCTO(ID_PRODUCTO)," +
                "FOREIGN KEY (ID_PEDIDO) REFERENCES PEDIDO(ID_PEDIDO));");

        BaseDatosSeeder.insertarDatosIniciales(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar tablas en orden inverso al de creación
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

        // Volver a crear la base de datos
        onCreate(db);
    }

}
