package sv.ues.fia.eisi.proyecto01_antojitos.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio.Municipio;

public class MunicipioDAO {
    private SQLiteDatabase db;

    public MunicipioDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertar(Municipio municipio) {
        ContentValues valores = new ContentValues();
        valores.put("ID_DEPARTAMENTO", municipio.getIdDepartamento());
        valores.put("ID_MUNICIPIO", municipio.getIdMunicipio());
        valores.put("NOMBRE_MUNICIPIO", municipio.getNombreMunicipio());
        valores.put("ACTIVO_MUNICIPIO", municipio.getActivoMunicipio());

        return db.insert("MUNICIPIO", null, valores);
    }

    public int actualizar(Municipio municipio) {
        ContentValues valores = new ContentValues();
        valores.put("NOMBRE_MUNICIPIO", municipio.getNombreMunicipio());
        valores.put("ACTIVO_MUNICIPIO", municipio.getActivoMunicipio());

        String condicion = "ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ?";
        String[] args = {
                String.valueOf(municipio.getIdDepartamento()),
                String.valueOf(municipio.getIdMunicipio())
        };

        return db.update("MUNICIPIO", valores, condicion, args);
    }

    public int eliminar(int idDepartamento, int idMunicipio) {
        String condicion = "ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ?";
        String[] args = { String.valueOf(idDepartamento), String.valueOf(idMunicipio) };
        return db.delete("MUNICIPIO", condicion, args);
    }

    public Municipio consultar(int idDepartamento, int idMunicipio) {
        String consulta = "SELECT * FROM MUNICIPIO WHERE ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ?";
        String[] args = { String.valueOf(idDepartamento), String.valueOf(idMunicipio) };

        Cursor cursor = db.rawQuery(consulta, args);
        if (cursor.moveToFirst()) {
            Municipio municipio = new Municipio();
            municipio.setIdDepartamento(cursor.getInt(0));
            municipio.setIdMunicipio(cursor.getInt(1));
            municipio.setNombreMunicipio(cursor.getString(2));
            municipio.setActivoMunicipio(cursor.getInt(3));
            cursor.close();
            return municipio;
        }
        cursor.close();
        return null;
    }

    public ArrayList<Municipio> obtenerTodos() {
        ArrayList<Municipio> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM MUNICIPIO", null);

        if (cursor.moveToFirst()) {
            do {
                Municipio municipio = new Municipio();
                municipio.setIdDepartamento(cursor.getInt(0));
                municipio.setIdMunicipio(cursor.getInt(1));
                municipio.setNombreMunicipio(cursor.getString(2));
                municipio.setActivoMunicipio(cursor.getInt(3));
                lista.add(municipio);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}
