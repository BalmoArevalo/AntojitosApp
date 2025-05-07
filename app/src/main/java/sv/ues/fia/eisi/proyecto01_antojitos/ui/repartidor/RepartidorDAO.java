package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD sobre la tabla REPARTIDOR.
 */
public class RepartidorDAO {
    private final SQLiteDatabase db;

    public RepartidorDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Inserta un nuevo repartidor en la base de datos.
     * @param r entidad Repartidor a insertar
     * @return ID generado o -1 si fallo
     */
    public long insertar(Repartidor r) {
        ContentValues values = new ContentValues();
        // No incluir ID_REPARTIDOR para que sea autogenerado
        values.put("ID_DEPARTAMENTO", r.getIdDepartamento());
        values.put("ID_MUNICIPIO", r.getIdMunicipio());
        values.put("ID_DISTRITO", r.getIdDistrito());
        values.put("TIPO_VEHICULO", r.getTipoVehiculo());
        values.put("DISPONIBLE", r.getDisponible());
        values.put("TELEFONO_REPARTIDOR", r.getTelefonoRepartidor());
        values.put("NOMBRE_REPARTIDOR", r.getNombreRepartidor());
        values.put("APELLIDO_REPARTIDOR", r.getApellidoRepartidor());
        values.put("ACTIVO_REPARTIDOR", r.getActivoRepartidor());
        return db.insert("REPARTIDOR", null, values);
    }

    /**
     * Obtiene un repartidor por su ID.
     */
    public Repartidor obtenerPorId(int id) {
        Cursor c = db.rawQuery(
                "SELECT * FROM REPARTIDOR WHERE ID_REPARTIDOR = ?",
                new String[]{ String.valueOf(id) }
        );
        Repartidor r = null;
        if (c.moveToFirst()) {
            r = cursorToRepartidor(c);
        }
        c.close();
        return r;
    }

    /**
     * Devuelve todos los repartidores.
     */
    public List<Repartidor> obtenerTodos() {
        List<Repartidor> lista = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM REPARTIDOR", null);
        if (c.moveToFirst()) {
            do {
                lista.add(cursorToRepartidor(c));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    /**
     * Devuelve sólo los repartidores activos.
     */
    public List<Repartidor> obtenerActivos() {
        List<Repartidor> lista = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT * FROM REPARTIDOR WHERE ACTIVO_REPARTIDOR = 1", null
        );
        if (c.moveToFirst()) {
            do {
                lista.add(cursorToRepartidor(c));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    /**
     * Actualiza un repartidor existente.
     * @return número de filas afectadas
     */
    public int actualizar(Repartidor r) {
        ContentValues values = new ContentValues();
        values.put("ID_DEPARTAMENTO", r.getIdDepartamento());
        values.put("ID_MUNICIPIO", r.getIdMunicipio());
        values.put("ID_DISTRITO", r.getIdDistrito());
        values.put("TIPO_VEHICULO", r.getTipoVehiculo());
        values.put("DISPONIBLE", r.getDisponible());
        values.put("TELEFONO_REPARTIDOR", r.getTelefonoRepartidor());
        values.put("NOMBRE_REPARTIDOR", r.getNombreRepartidor());
        values.put("APELLIDO_REPARTIDOR", r.getApellidoRepartidor());
        values.put("ACTIVO_REPARTIDOR", r.getActivoRepartidor());
        return db.update(
                "REPARTIDOR",
                values,
                "ID_REPARTIDOR = ?",
                new String[]{ String.valueOf(r.getIdRepartidor()) }
        );
    }

    /**
     * 'Elimina' un repartidor desactivándolo (soft delete).
     */
    public int eliminar(int id) {
        ContentValues values = new ContentValues();
        values.put("ACTIVO_REPARTIDOR", 0);
        return db.update(
                "REPARTIDOR",
                values,
                "ID_REPARTIDOR = ?",
                new String[]{ String.valueOf(id) }
        );
    }

    /**
     * Mapea un Cursor a una entidad Repartidor.
     */
    private Repartidor cursorToRepartidor(Cursor c) {
        Repartidor r = new Repartidor();
        r.setIdRepartidor(c.getInt(c.getColumnIndexOrThrow("ID_REPARTIDOR")));
        int idx;
        idx = c.getColumnIndexOrThrow("ID_DEPARTAMENTO");
        r.setIdDepartamento(c.isNull(idx) ? null : c.getInt(idx));
        idx = c.getColumnIndexOrThrow("ID_MUNICIPIO");
        r.setIdMunicipio(c.isNull(idx) ? null : c.getInt(idx));
        idx = c.getColumnIndexOrThrow("ID_DISTRITO");
        r.setIdDistrito(c.isNull(idx) ? null : c.getInt(idx));
        r.setTipoVehiculo(c.getString(c.getColumnIndexOrThrow("TIPO_VEHICULO")));
        r.setDisponible(c.getInt(c.getColumnIndexOrThrow("DISPONIBLE")));
        r.setTelefonoRepartidor(c.getString(c.getColumnIndexOrThrow("TELEFONO_REPARTIDOR")));
        r.setNombreRepartidor(c.getString(c.getColumnIndexOrThrow("NOMBRE_REPARTIDOR")));
        r.setApellidoRepartidor(c.getString(c.getColumnIndexOrThrow("APELLIDO_REPARTIDOR")));
        r.setActivoRepartidor(c.getInt(c.getColumnIndexOrThrow("ACTIVO_REPARTIDOR")));
        return r;
    }
}