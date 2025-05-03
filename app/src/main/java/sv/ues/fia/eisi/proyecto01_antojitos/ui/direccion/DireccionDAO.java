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
        Direccion direccion = null;

        Cursor cursor = db.query("DIRECCION",
                new String[]{"ID_CLIENTE", "ID_DIRECCION", "ID_DEPARTAMENTO", "ID_MUNICIPIO", "ID_DISTRITO",
                        "DIRECCION_ESPECIFICA", "DESCRIPCION_DIRECCION"},
                "ID_CLIENTE=? AND ID_DIRECCION=?",
                new String[]{String.valueOf(idCliente), String.valueOf(idDireccion)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            direccion = new Direccion();
            direccion.setIdCliente(cursor.getInt(0));
            direccion.setIdDireccion(cursor.getInt(1));
            direccion.setIdDepartamento(cursor.getInt(2));
            direccion.setIdMunicipio(cursor.getInt(3));
            direccion.setIdDistrito(cursor.getInt(4));
            direccion.setDireccionEspecifica(cursor.getString(5));
            direccion.setDescripcionDireccion(cursor.getString(6));
            cursor.close();
        }

        return direccion;
    }

    // Método para actualizar una dirección existente

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
    public int actualizar(Direccion direccion) {
        ContentValues valores = new ContentValues();
        valores.put("ID_DEPARTAMENTO", direccion.getIdDepartamento());
        valores.put("ID_MUNICIPIO", direccion.getIdMunicipio());
        valores.put("ID_DISTRITO", direccion.getIdDistrito());
        valores.put("DIRECCION_ESPECIFICA", direccion.getDireccionEspecifica());
        valores.put("DESCRIPCION_DIRECCION", direccion.getDescripcionDireccion());

        return db.update("DIRECCION", valores,
                "ID_CLIENTE=? AND ID_DIRECCION=?",
                new String[]{String.valueOf(direccion.getIdCliente()), String.valueOf(direccion.getIdDireccion())});
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
