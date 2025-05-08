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
 */
public class AccesoUsuarioDAO {

    private final SQLiteDatabase db;

    public AccesoUsuarioDAO(Context ctx) {
        this.db = new DBHelper(ctx).getWritableDatabase();
    }

    /* =============== INSERT / DELETE =============== */

    public long insertar(AccesoUsuario au) {
        return db.insertWithOnConflict("ACCESOUSUARIO", null, au.toCV(),
                SQLiteDatabase.CONFLICT_IGNORE);
    }

    public int eliminar(String idUsuario, String idOpcion) {
        return db.delete("ACCESOUSUARIO",
                "ID_USUARIO = ? AND ID_OPCION = ?",
                new String[]{idUsuario, idOpcion});
    }

    public int eliminarPorUsuario(String idUsuario) {
        return db.delete("ACCESOUSUARIO",
                "ID_USUARIO = ?",
                new String[]{idUsuario});
    }

    /* =============== CONSULTAS  =============== */

    /** Devuelve la lista de ID_OPCION que un usuario puede ejecutar. */
    public List<String> obtenerOpcionesPorUsuario(String idUsuario) {
        List<String> lista = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT ID_OPCION FROM ACCESOUSUARIO WHERE ID_USUARIO = ?",
                new String[]{idUsuario});
        while (c.moveToNext()) {
            lista.add(c.getString(0));
        }
        c.close();
        db.close();
        return lista;
    }

    /** ¿Tiene este usuario acceso (directo o por comodín `todo_admin`) a la opción? */
    public boolean tieneAcceso(String idUsuario, String idOpcion) {
        Cursor c = db.rawQuery(
                "SELECT 1 FROM ACCESOUSUARIO " +
                        "WHERE ID_USUARIO = ? AND (ID_OPCION = ? OR ID_OPCION = 'todo_admin') LIMIT 1",
                new String[]{idUsuario, idOpcion});
        boolean ok = c.moveToFirst();
        c.close();
        db.close();
        return ok;
    }
}
