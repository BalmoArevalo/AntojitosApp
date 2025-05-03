package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TipoEventoDAO {

    private final SQLiteDatabase db;

    public TipoEventoDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertar(TipoEvento tipoEvento) {
        ContentValues values = new ContentValues();
        values.put("ID_TIPO_EVENTO", tipoEvento.getIdTipoEvento());
        values.put("NOMBRE_TIPO_EVENTO", tipoEvento.getNombreTipoEvento());
        values.put("DESCRIPCION_TIPO_EVENTO", tipoEvento.getDescripcionTipoEvento());
        values.put("MONTO_MINIMO", tipoEvento.getMontoMinimo());
        values.put("MONOTO_MAXIMO", tipoEvento.getMontoMaximo());

        return db.insert("TIPOEVENTO", null, values);
    }

    public TipoEvento consultarPorId(int idTipoEvento) {
        Cursor cursor = db.query("TIPOEVENTO", null, "ID_TIPO_EVENTO = ?",
                new String[]{String.valueOf(idTipoEvento)}, null, null, null);

        if (cursor.moveToFirst()) {
            TipoEvento tipoEvento = new TipoEvento();
            tipoEvento.setIdTipoEvento(cursor.getInt(0));
            tipoEvento.setNombreTipoEvento(cursor.getString(1));
            tipoEvento.setDescripcionTipoEvento(cursor.getString(2));
            tipoEvento.setMontoMinimo(cursor.getDouble(3));
            tipoEvento.setMontoMaximo(cursor.getDouble(4));
            cursor.close();
            return tipoEvento;
        }
        cursor.close();
        return null;
    }

    public int actualizar(TipoEvento tipoEvento) {
        ContentValues values = new ContentValues();
        values.put("NOMBRE_TIPO_EVENTO", tipoEvento.getNombreTipoEvento());
        values.put("DESCRIPCION_TIPO_EVENTO", tipoEvento.getDescripcionTipoEvento());
        values.put("MONTO_MINIMO", tipoEvento.getMontoMinimo());
        values.put("MONOTO_MAXIMO", tipoEvento.getMontoMaximo());

        return db.update("TIPOEVENTO", values, "ID_TIPO_EVENTO = ?",
                new String[]{String.valueOf(tipoEvento.getIdTipoEvento())});
    }

    public int eliminar(int idTipoEvento) {
        return db.delete("TIPOEVENTO", "ID_TIPO_EVENTO = ?",
                new String[]{String.valueOf(idTipoEvento)});
    }

    public List<TipoEvento> obtenerTodos() {
        List<TipoEvento> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM TIPOEVENTO", null);

        if (cursor.moveToFirst()) {
            do {
                TipoEvento tipoEvento = new TipoEvento();
                tipoEvento.setIdTipoEvento(cursor.getInt(0));
                tipoEvento.setNombreTipoEvento(cursor.getString(1));
                tipoEvento.setDescripcionTipoEvento(cursor.getString(2));
                tipoEvento.setMontoMinimo(cursor.getDouble(3));
                tipoEvento.setMontoMaximo(cursor.getDouble(4));
                lista.add(tipoEvento);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}
