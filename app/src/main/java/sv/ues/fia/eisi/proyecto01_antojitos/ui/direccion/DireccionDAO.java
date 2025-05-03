package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

public class DireccionDAO {

    private final SQLiteDatabase db;

    public DireccionDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Calcula el siguiente ID_DIRECCION para un cliente dado
     * (MAX(ID_DIRECCION) + 1, o 1 si no hay ninguno).
     */
    public int getNextIdDireccion(int idCliente) {
        int next = 1;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT MAX(ID_DIRECCION) FROM DIRECCION WHERE ID_CLIENTE = ?",
                    new String[]{ String.valueOf(idCliente) }
            );
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                next = cursor.getInt(0) + 1;
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return next;
    }

    /** Inserta una nueva dirección con clave compuesta. */
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

    /** Consulta por (cliente, dirección). */
    public Direccion consultarPorId(int idCliente, int idDireccion) {
        String where = "ID_CLIENTE = ? AND ID_DIRECCION = ?";
        String[] args = { String.valueOf(idCliente), String.valueOf(idDireccion) };
        Cursor cursor = db.query("DIRECCION", null, where, args, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                Direccion dir = new Direccion();
                dir.setIdCliente(cursor.getInt(0));
                dir.setIdDireccion(cursor.getInt(1));
                dir.setIdDepartamento(cursor.getInt(2));
                dir.setIdMunicipio(cursor.getInt(3));
                dir.setIdDistrito(cursor.getInt(4));
                dir.setDireccionEspecifica(cursor.getString(5));
                dir.setDescripcionDireccion(cursor.getString(6));
                return dir;
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    /** Obtiene todas las direcciones de un cliente dado */
    public List<Direccion> obtenerPorCliente(int idCliente) {
        List<Direccion> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM DIRECCION WHERE ID_CLIENTE = ?",
                new String[]{ String.valueOf(idCliente) }
        );
        try {
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
        } finally {
            cursor.close();
        }
        return lista;
    }



    /** Actualiza una dirección existente. */
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

    /** Elimina la dirección identificada. */
    public int eliminar(int idCliente, int idDireccion) {
        String where = "ID_CLIENTE = ? AND ID_DIRECCION = ?";
        String[] args = {
                String.valueOf(idCliente),
                String.valueOf(idDireccion)
        };
        return db.delete("DIRECCION", where, args);
    }

    /** Devuelve todas las direcciones sin detalle. */
    public List<Direccion> obtenerTodas() {
        List<Direccion> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM DIRECCION", null);
        try {
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
        } finally {
            cursor.close();
        }
        return lista;
    }

}
