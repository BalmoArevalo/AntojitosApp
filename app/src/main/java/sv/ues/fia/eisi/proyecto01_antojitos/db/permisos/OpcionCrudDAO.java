package sv.ues.fia.eisi.proyecto01_antojitos.db.permisos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

/**
 * DAO de la tabla OPCIONCRUD.
 *  • CRUD completo
 *  • utilidades para listar por tipo CRUD
 */
public class OpcionCrudDAO {

    private final SQLiteDatabase db;

    public OpcionCrudDAO(Context ctx) {
        this.db = new DBHelper(ctx).getWritableDatabase();
    }

    /* ============ INSERT / UPDATE / DELETE ============ */

    public long insertar(OpcionCrud oc) {
        return db.insertWithOnConflict("OPCIONCRUD", null, oc.toCV(),
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int actualizar(OpcionCrud oc) {
        return db.update("OPCIONCRUD", oc.toCV(),
                "ID_OPCION = ?", new String[]{oc.getIdOpcion()});
    }

    public int eliminar(String idOpcion) {
        return db.delete("OPCIONCRUD", "ID_OPCION = ?", new String[]{idOpcion});
    }

    /* ============ CONSULTAS ============ */

    public OpcionCrud obtenerPorId(String idOpcion) {
        Cursor c = db.rawQuery("SELECT * FROM OPCIONCRUD WHERE ID_OPCION = ?",
                new String[]{idOpcion});
        OpcionCrud oc = null;
        if (c.moveToFirst()) oc = map(c);
        c.close();
        return oc;
    }

    public List<OpcionCrud> obtenerTodos() {
        List<OpcionCrud> lista = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM OPCIONCRUD ORDER BY ID_OPCION", null);
        while (c.moveToNext()) lista.add(map(c));
        c.close();
        return lista;
    }

    /** Lista solo las opciones de un tipo CRUD (1-4, 0 = comodín). */
    public List<OpcionCrud> obtenerPorTipo(int numCrud) {
        List<OpcionCrud> lista = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM OPCIONCRUD WHERE NUM_CRUD = ?",
                new String[]{String.valueOf(numCrud)});
        while (c.moveToNext()) lista.add(map(c));
        c.close();
        return lista;
    }

    /* ============ util ============ */
    private OpcionCrud map(Cursor c) {
        return new OpcionCrud(
                c.getString(c.getColumnIndexOrThrow("ID_OPCION")),
                c.getString(c.getColumnIndexOrThrow("DES_OPCION")),
                c.getInt   (c.getColumnIndexOrThrow("NUM_CRUD"))
        );
    }
}
