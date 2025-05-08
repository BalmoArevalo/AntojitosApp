package sv.ues.fia.eisi.proyecto01_antojitos.db.seeders;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Inserta usuarios, opciones CRUD y sus accesos.
 * Debe llamarse justo después de crear las 3 tablas de seguridad.
 */
public final class SeguridadSeeder {

    private SeguridadSeeder() { }

    public static void poblar(SQLiteDatabase db) {

        Log.i("SeguridadSeeder", "Insertando datos de seguridad…");
        db.beginTransaction();
        try {
            /* ─────  LIMPIEZA  ───── */
            db.execSQL("DELETE FROM ACCESOUSUARIO;");
            db.execSQL("DELETE FROM OPCIONCRUD;");
            db.execSQL("DELETE FROM USUARIO;");

            /* ─────  USUARIOS  ───── */
            db.execSQL(
                    "INSERT OR REPLACE INTO USUARIO(ID_USUARIO,NOM_USUARIO,CLAVE) VALUES" +
                            "('SU','superusuario','12345')," +
                            "('CL','cliente','123')," +
                            "('RP','repartidor','123')," +
                            "('SC','sucursal','123');");

            /* ─────  OPCIONES CRUD  ───── */
            db.execSQL(
                    "INSERT OR REPLACE INTO OPCIONCRUD(ID_OPCION,DES_OPCION,NUM_CRUD) VALUES" +
                            "('cliente_crear',     'Crear Cliente',       1)," +
                            "('cliente_consultar', 'Consultar Cliente',   2)," +
                            "('cliente_editar',    'Editar Cliente',      3)," +
                            "('cliente_eliminar',  'Eliminar Cliente',    4)," +
                            "('producto_consultar','Consultar Producto',  2)," +
                            "('reparto_consultar', 'Consultar Reparto',   2)," +
                            "('factura_consultar','Consultar factura',2)," +
                            "('pedido_consultar','Consultar pedido',2)," +
                            "('direccion_crear', 'Crear dirección',   1)," +
                            "('todo_admin',        'Acceso total',        0);");




            /* ─────  ACCESOS USUARIO  ───── */
            // Superusuario
            db.execSQL("INSERT OR REPLACE INTO ACCESOUSUARIO VALUES ('todo_admin','SU');");

            // Cliente
            db.execSQL("INSERT OR REPLACE INTO ACCESOUSUARIO VALUES" +
                    "('cliente_consultar','CL')," +
                    "('factura_consultar','CL')," +
                    "('pedido_consultar','CL')," +
                    "('direccion_crear','CL')," +
                     "('producto_consultar','CL');");

            // Repartidor
            db.execSQL("INSERT OR REPLACE INTO ACCESOUSUARIO VALUES" +
                    "('reparto_consultar','RP');");

            // Sucursal
            db.execSQL("INSERT OR REPLACE INTO ACCESOUSUARIO VALUES" +
                    "('producto_consultar','SC')," +
                    "('cliente_consultar','SC');");

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        Log.i("SeguridadSeeder", "Datos de seguridad insertados.");
    }
}
