package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RepartidorDAO {
    private final SQLiteDatabase db;

    public RepartidorDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertar(Repartidor r) {
        ContentValues v = new ContentValues();
        // ID_USUARIO es NOT NULL en la tabla; asignamos valor por defecto o el real si se tuviera
        v.put("ID_USUARIO", 0);
        v.put("ID_DEPARTAMENTO", r.getIdDepartamento());
        v.put("ID_MUNICIPIO", r.getIdMunicipio());
        v.put("ID_DISTRITO", r.getIdDistrito());
        v.put("TIPO_VEHICULO", r.getTipoVehiculo());
        v.put("DISPONIBLE", r.getDisponible());
        v.put("TELEFONO_REPARTIDOR", r.getTelefonoRepartidor());
        v.put("NOMBRE_REPARTIDOR", r.getNombreRepartidor());
        v.put("APELLIDO_REPARTIDOR", r.getApellidoRepartidor());
        v.put("ACTIVO_REPARTIDOR", r.getActivoRepartidor());
        return db.insert("REPARTIDOR", null, v);
    }

    public Repartidor obtenerPorId(int id) {
        Cursor c = db.rawQuery(
                "SELECT * FROM REPARTIDOR WHERE ID_REPARTIDOR = ?",
                new String[]{String.valueOf(id)});
        Repartidor r = null;
        if (c.moveToFirst()) {
            r = cursorToRepartidor(c);
        }
        c.close();
        return r;
    }

    public List<Repartidor> obtenerTodos() {
        List<Repartidor> list = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM REPARTIDOR", null);
        if (c.moveToFirst()) {
            do {
                list.add(cursorToRepartidor(c));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<Repartidor> obtenerActivos() {
        List<Repartidor> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT * FROM REPARTIDOR WHERE ACTIVO_REPARTIDOR = 1", null);
        if (c.moveToFirst()) {
            do {
                list.add(cursorToRepartidor(c));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public int actualizar(Repartidor r) {
        ContentValues v = new ContentValues();
        // ID_USUARIO no cambia aquí, se mantiene o actualizar si se quiere
        v.put("ID_DEPARTAMENTO", r.getIdDepartamento());
        v.put("ID_MUNICIPIO", r.getIdMunicipio());
        v.put("ID_DISTRITO", r.getIdDistrito());
        v.put("TIPO_VEHICULO", r.getTipoVehiculo());
        v.put("DISPONIBLE", r.getDisponible());
        v.put("TELEFONO_REPARTIDOR", r.getTelefonoRepartidor());
        v.put("NOMBRE_REPARTIDOR", r.getNombreRepartidor());
        v.put("APELLIDO_REPARTIDOR", r.getApellidoRepartidor());
        v.put("ACTIVO_REPARTIDOR", r.getActivoRepartidor());
        return db.update(
                "REPARTIDOR", v,
                "ID_REPARTIDOR = ?", new String[]{String.valueOf(r.getIdRepartidor())});
    }

    public int eliminar(int id) {
        ContentValues v = new ContentValues();
        v.put("ACTIVO_REPARTIDOR", 0);
        return db.update(
                "REPARTIDOR", v,
                "ID_REPARTIDOR = ?", new String[]{String.valueOf(id)});
    }

    private Repartidor cursorToRepartidor(Cursor c) {
        Repartidor r = new Repartidor();
        r.setIdRepartidor(c.getInt(c.getColumnIndexOrThrow("ID_REPARTIDOR")));
        // Saltamos ID_USUARIO si no se modela, o leerlo si existe en la clase
        r.setIdDepartamento(c.getInt(c.getColumnIndexOrThrow("ID_DEPARTAMENTO")));
        r.setIdMunicipio(c.getInt(c.getColumnIndexOrThrow("ID_MUNICIPIO")));
        r.setIdDistrito(c.getInt(c.getColumnIndexOrThrow("ID_DISTRITO")));
        r.setTipoVehiculo(c.getString(c.getColumnIndexOrThrow("TIPO_VEHICULO")));
        r.setDisponible(c.getInt(c.getColumnIndexOrThrow("DISPONIBLE")));
        r.setTelefonoRepartidor(c.getString(c.getColumnIndexOrThrow("TELEFONO_REPARTIDOR")));
        r.setNombreRepartidor(c.getString(c.getColumnIndexOrThrow("NOMBRE_REPARTIDOR")));
        r.setApellidoRepartidor(c.getString(c.getColumnIndexOrThrow("APELLIDO_REPARTIDOR")));
        r.setActivoRepartidor(c.getInt(c.getColumnIndexOrThrow("ACTIVO_REPARTIDOR")));
        return r;
    }
}
