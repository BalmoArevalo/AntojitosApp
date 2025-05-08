package sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DepartamentoDAO {

    private final SQLiteDatabase db;

    public DepartamentoDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertar(Departamento departamento) {
        ContentValues values = new ContentValues();
        values.put("NOMBRE_DEPARTAMENTO", departamento.getNombreDepartamento());
        return db.insert("DEPARTAMENTO", null, values);
    }

    public Departamento consultarPorId(int idDepartamento) {
        Cursor cursor = db.query("DEPARTAMENTO", null, "ID_DEPARTAMENTO = ?",
                new String[]{String.valueOf(idDepartamento)}, null, null, null);

        if (cursor.moveToFirst()) {
            Departamento departamento = new Departamento();
            departamento.setIdDepartamento(cursor.getInt(0));
            departamento.setNombreDepartamento(cursor.getString(1));
            cursor.close();
            return departamento;
        }
        cursor.close();
        return null;
    }

    public int actualizar(Departamento departamento) {
        ContentValues values = new ContentValues();
        values.put("NOMBRE_DEPARTAMENTO", departamento.getNombreDepartamento());
        return db.update("DEPARTAMENTO", values, "ID_DEPARTAMENTO = ?",
                new String[]{String.valueOf(departamento.getIdDepartamento())});
    }

    public int eliminar(int idDepartamento) {
        return db.delete("DEPARTAMENTO", "ID_DEPARTAMENTO = ?",
                new String[]{String.valueOf(idDepartamento)});
    }

    public List<Departamento> obtenerTodos() {
        List<Departamento> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM DEPARTAMENTO", null);

        if (cursor.moveToFirst()) {
            do {
                Departamento departamento = new Departamento();
                departamento.setIdDepartamento(cursor.getInt(0));
                departamento.setNombreDepartamento(cursor.getString(1));
                lista.add(departamento);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}


