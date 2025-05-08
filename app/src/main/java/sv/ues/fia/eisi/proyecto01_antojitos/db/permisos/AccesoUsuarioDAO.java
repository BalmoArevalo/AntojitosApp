package sv.ues.fia.eisi.proyecto01_antojitos.db.permisos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

/**
 * DAO para la tabla ACCESOUSUARIO.
 *  • asignar / revocar accesos
 *  • consultar permisos de un usuario
 *  • validar si un usuario tiene acceso a una opción
 *
 *  *IMPORTANTE*: nunca cerrar la conexión devuelta por DBHelper.
 *  Android se encarga de gestionarla y reutilizarla.
 */
public class AccesoUsuarioDAO {

    private final DBHelper helper;

    public AccesoUsuarioDAO(Context ctx) {
        this.helper = new DBHelper(ctx.getApplicationContext());
    }

    /* =============== INSERT / DELETE =============== */

    public long insertar(AccesoUsuario au) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insertWithOnConflict("ACCESOUSUARIO", null, au.toCV(),
                SQLiteDatabase.CONFLICT_IGNORE);
    }

    public int eliminar(String idUsuario, String idOpcion) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete("ACCESOUSUARIO",
                "ID_USUARIO = ? AND ID_OPCION = ?",
                new String[]{idUsuario, idOpcion});
    }

    public int eliminarPorUsuario(String idUsuario) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete("ACCESOUSUARIO",
                "ID_USUARIO = ?",
                new String[]{idUsuario});
    }

    /* =============== CONSULTAS  =============== */

    /** Devuelve la lista de ID_OPCION que un usuario puede ejecutar. */
    public List<String> obtenerOpcionesPorUsuario(String idUsuario) {
        List<String> lista = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT ID_OPCION FROM ACCESOUSUARIO WHERE ID_USUARIO = ?",
                new String[]{idUsuario});
        try {
            while (c.moveToNext()) {
                lista.add(c.getString(0));
            }
        } finally {
            c.close();          // solo cerramos el cursor
        }
        return lista;
    }

    /** ¿Tiene este usuario acceso (directo o por comodín ‘todo_admin’) a la opción? */
    public boolean tieneAcceso(String idUsuario, String idOpcion) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM ACCESOUSUARIO " +
                        "WHERE ID_USUARIO = ? AND (ID_OPCION = ? OR ID_OPCION = 'todo_admin') " +
                        "LIMIT 1",
                new String[]{idUsuario, idOpcion});
        try {
            return c.moveToFirst();
        } finally {
            c.close();
        }
    }
}
