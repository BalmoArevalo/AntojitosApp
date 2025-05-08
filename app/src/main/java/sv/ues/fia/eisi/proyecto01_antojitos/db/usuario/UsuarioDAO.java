package sv.ues.fia.eisi.proyecto01_antojitos.db.usuario;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

/**
 * DAO mínimo para la tabla USUARIO.
 *  • crear/actualizar/eliminar usuarios
 *  • validar login
 *  • listar todos
 */
public class UsuarioDAO {

    private final SQLiteDatabase db;

    public UsuarioDAO(Context ctx) {
        this.db = new DBHelper(ctx).getWritableDatabase();
    }

    /* ======================= CRUD ======================= */

    public long insertar(Usuario u) {
        return db.insertWithOnConflict("USUARIO", null, u.toContentValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int actualizar(Usuario u) {
        return db.update("USUARIO", u.toContentValues(),
                "ID_USUARIO = ?", new String[]{u.getIdUsuario()});
    }

    public int eliminar(String idUsuario) {
        return db.delete("USUARIO", "ID_USUARIO = ?", new String[]{idUsuario});
    }

    public Usuario obtenerPorId(String idUsuario) {
        Cursor c = db.rawQuery("SELECT * FROM USUARIO WHERE ID_USUARIO = ?",
                new String[]{idUsuario});
        Usuario u = null;
        if (c.moveToFirst()) {
            u = mapCursor(c);
        }
        c.close();
        return u;
    }

    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM USUARIO", null);
        while (c.moveToNext()) {
            lista.add(mapCursor(c));
        }
        c.close();
        return lista;
    }

    /* =================== Login Helper =================== */

    /**
     * Devuelve el Usuario si las credenciales coinciden; null en caso contrario.
     */
    public Usuario validarLogin(String nomUsuario, String clave) {
        Cursor c = db.rawQuery(
                "SELECT * FROM USUARIO WHERE NOM_USUARIO = ? AND CLAVE = ?",
                new String[]{nomUsuario, clave});
        Usuario u = null;
        if (c.moveToFirst()) {
            u = mapCursor(c);
        }
        c.close();
        db.close();
        return u;
    }

    /* ============= Utilidad para mapear Cursor ============== */
    private Usuario mapCursor(Cursor c) {
        return new Usuario(
                c.getString(c.getColumnIndexOrThrow("ID_USUARIO")),
                c.getString(c.getColumnIndexOrThrow("NOM_USUARIO")),
                c.getString(c.getColumnIndexOrThrow("CLAVE"))
        );
    }
}
