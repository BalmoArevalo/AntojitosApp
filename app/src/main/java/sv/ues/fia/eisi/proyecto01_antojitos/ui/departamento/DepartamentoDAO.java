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

    // Insertar un nuevo departamento con estado activo
    public long insertar(Departamento departamento) {
        ContentValues values = new ContentValues();
        values.put("NOMBRE_DEPARTAMENTO", departamento.getNombreDepartamento());
        values.put("ACTIVO_DEPARTAMENTO", departamento.getActivoDepartamento()); // 👈 explícito
        return db.insert("DEPARTAMENTO", null, values);
    }

    // Consultar por ID (puede traer activo o inactivo)
    public Departamento consultarPorId(int idDepartamento) {
        Cursor cursor = db.query(
                "DEPARTAMENTO",
                null,
                "ID_DEPARTAMENTO = ?",
                new String[]{String.valueOf(idDepartamento)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            Departamento departamento = new Departamento();
            departamento.setIdDepartamento(cursor.getInt(0));
            departamento.setNombreDepartamento(cursor.getString(1));
            departamento.setActivoDepartamento(cursor.getInt(2));
            cursor.close();
            return departamento;
        }
        cursor.close();
        return null;
    }

    // Actualizar nombre y estado activo
    public int actualizar(Departamento departamento) {
        ContentValues values = new ContentValues();
        values.put("NOMBRE_DEPARTAMENTO", departamento.getNombreDepartamento());
        values.put("ACTIVO_DEPARTAMENTO", departamento.getActivoDepartamento());

        return db.update(
                "DEPARTAMENTO",
                values,
                "ID_DEPARTAMENTO = ?",
                new String[]{String.valueOf(departamento.getIdDepartamento())}
        );
    }

    // Eliminación física (no recomendado si se usa borrado lógico)
    public int eliminar(int idDepartamento) {
        return db.delete(
                "DEPARTAMENTO",
                "ID_DEPARTAMENTO = ?",
                new String[]{String.valueOf(idDepartamento)}
        );
    }

    // Obtener todos los departamentos (activos e inactivos)
    public List<Departamento> obtenerTodos() {
        List<Departamento> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM DEPARTAMENTO", null);

        if (cursor.moveToFirst()) {
            do {
                Departamento departamento = new Departamento();
                departamento.setIdDepartamento(cursor.getInt(0));
                departamento.setNombreDepartamento(cursor.getString(1));
                departamento.setActivoDepartamento(cursor.getInt(2));
                lista.add(departamento);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // Obtener solo departamentos activos
    public List<Departamento> obtenerActivos() {
        List<Departamento> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM DEPARTAMENTO WHERE ACTIVO_DEPARTAMENTO = 1", null);

        if (cursor.moveToFirst()) {
            do {
                Departamento departamento = new Departamento();
                departamento.setIdDepartamento(cursor.getInt(0));
                departamento.setNombreDepartamento(cursor.getString(1));
                departamento.setActivoDepartamento(cursor.getInt(2));
                lista.add(departamento);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}
