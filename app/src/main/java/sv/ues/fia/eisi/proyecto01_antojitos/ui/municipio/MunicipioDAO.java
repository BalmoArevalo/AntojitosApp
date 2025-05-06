package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;
package sv.ues.fia.eisi.proyecto01_antojitos.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.modelos.Municipio;

public class MunicipioDAO {
    private SQLiteDatabase db;

    public MunicipioDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertar(Municipio municipio) {
        ContentValues valores = new ContentValues();
        valores.put("idMunicipio", municipio.getIdMunicipio());
        valores.put("nombre", municipio.getNombre());
        valores.put("idDepartamento", municipio.getIdDepartamento());
        return db.insert("Municipio", null, valores);
    }

    public int actualizar(Municipio municipio) {
        ContentValues valores = new ContentValues();
        valores.put("nombre", municipio.getNombre());
        valores.put("idDepartamento", municipio.getIdDepartamento());
        return db.update("Municipio", valores, "idMunicipio = ?", new String[]{String.valueOf(municipio.getIdMunicipio())});
    }

    public int eliminar(int idMunicipio) {
        return db.delete("Municipio", "idMunicipio = ?", new String[]{String.valueOf(idMunicipio)});
    }

    public Municipio consultarPorId(int idMunicipio) {
        Cursor cursor = db.rawQuery("SELECT * FROM Municipio WHERE idMunicipio = ?", new String[]{String.valueOf(idMunicipio)});
        if (cursor.moveToFirst()) {
            Municipio municipio = new Municipio(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2)
            );
            cursor.close();
            return municipio;
        }
        cursor.close();
        return null;
    }

    public List<Municipio> obtenerTodos() {
        List<Municipio> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Municipio", null);
        while (cursor.moveToNext()) {
            Municipio municipio = new Municipio(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2)
            );
            lista.add(municipio);
        }
        cursor.close();
        return lista;
    }
}

