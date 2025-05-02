package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DireccionDAO {

    private final SQLiteDatabase db;

    public DireccionDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Insertar nueva dirección
    public long insertar(Direccion dir) {
        ContentValues values = new ContentValues();
        values.put("ID_CLIENTE", dir.getIdCliente());
        values.put("ID_DIRECCION", dir.getIdDireccion());
        values.put("ID_DEPARTAMENTO", dir.getIdDepartamento());
        values.put("ID_MUNICIPIO", dir.getIdMunicipio());
        values.put("ID_DISTRITO", dir.getIdDistrito());
        values.put("DIRECCION_ESPECIFICA", dir.getDireccionEspecifica());
        values.put("DESCRIPCION_DIRECCION", dir.getDescripcionDireccion());
        return db.insert("DIRECCION", null, values);
    }

    // Consultar por ID (cliente y dirección)
    public Direccion consultarPorId(int idCliente, int idDireccion) {
        String where = "ID_CLIENTE = ? AND ID_DIRECCION = ?";
        String[] args = {
                String.valueOf(idCliente),
                String.valueOf(idDireccion)
        };
        Cursor cursor = db.query("DIRECCION", null, where, args, null, null, null);
        if (cursor.moveToFirst()) {
            Direccion dir = new Direccion();
            dir.setIdCliente(cursor.getInt(0));
            dir.setIdDireccion(cursor.getInt(1));
            dir.setIdDepartamento(cursor.getInt(2));
            dir.setIdMunicipio(cursor.getInt(3));
            dir.setIdDistrito(cursor.getInt(4));
            dir.setDireccionEspecifica(cursor.getString(5));
            dir.setDescripcionDireccion(cursor.getString(6));
            cursor.close();
            return dir;
        }
        cursor.close();
        return null;
    }

    // Actualizar dirección existente
    public int actualizar(Direccion dir) {
        ContentValues values = new ContentValues();
        values.put("ID_DEPARTAMENTO", dir.getIdDepartamento());
        values.put("ID_MUNICIPIO", dir.getIdMunicipio());
        values.put("ID_DISTRITO", dir.getIdDistrito());
        values.put("DIRECCION_ESPECIFICA", dir.getDireccionEspecifica());
        values.put("DESCRIPCION_DIRECCION", dir.getDescripcionDireccion());
        String where = "ID_CLIENTE = ? AND ID_DIRECCION = ?";
        String[] args = {
                String.valueOf(dir.getIdCliente()),
                String.valueOf(dir.getIdDireccion())
        };
        return db.update("DIRECCION", values, where, args);
    }

    // Eliminar dirección
    public int eliminar(int idCliente, int idDireccion) {
        String where = "ID_CLIENTE = ? AND ID_DIRECCION = ?";
        String[] args = {
                String.valueOf(idCliente),
                String.valueOf(idDireccion)
        };
        return db.delete("DIRECCION", where, args);
    }

    // Obtener todas las direcciones
    public List<Direccion> obtenerTodas() {
        List<Direccion> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM DIRECCION", null);
        if (cursor.moveToFirst()) {
            do {
                Direccion dir = new Direccion();
                dir.setIdCliente(cursor.getInt(0));
                dir.setIdDireccion(cursor.getInt(1));
                dir.setIdDepartamento(cursor.getInt(2));
                dir.setIdMunicipio(cursor.getInt(3));
                dir.setIdDistrito(cursor.getInt(4));
                dir.setDireccionEspecifica(cursor.getString(5));
                dir.setDescripcionDireccion(cursor.getString(6));
                lista.add(dir);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}
