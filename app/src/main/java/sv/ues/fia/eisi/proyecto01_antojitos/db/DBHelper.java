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
    public void onCreate(SQLiteDatabase db) {
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
                "HORARIO_CIERRE_SUCURSAL TEXT NOT NULL);");

        // Aquí puedes agregar el resto de tus tablas cuando las implementes
        // Por ejemplo:
        // db.execSQL("CREATE TABLE CLIENTE (...)");
        // db.execSQL("CREATE TABLE REPARTIDOR (...)");
        // db.execSQL("CREATE TABLE DATOSPRODUCTO (...)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Borra las tablas si ya existen (reinstalación o actualización)
        db.execSQL("DROP TABLE IF EXISTS SUCURSAL");
        onCreate(db); // recrea las tablas
    }
}
