package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SucursalDAO {

    private final SQLiteDatabase db;

    public SucursalDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Inserta una sucursal. Por defecto activo = 1.
     */
    public long insertar(Sucursal s) {
        ContentValues values = new ContentValues();
        values.put("ID_DEPARTAMENTO", s.getIdDepartamento());
        values.put("ID_MUNICIPIO", s.getIdMunicipio());
        values.put("ID_DISTRITO", s.getIdDistrito());
        values.put("NOMBRE_SUCURSAL", s.getNombreSucursal());
        values.put("DIRECCION_SUCURSAL", s.getDireccionSucursal());
        values.put("TELEFONO_SUCURSAL", s.getTelefonoSucursal());
        values.put("HORARIO_APERTURA_SUCURSAL", s.getHorarioApertura());
        values.put("HORARIO_CIERRE_SUCURSAL", s.getHorarioCierre());
        values.put("ACTIVO_SUCURSAL", s.getActivoSucursal());
        return db.insert("SUCURSAL", null, values);
    }

    /**
     * Obtiene sucursal por ID (sin filtrar por estado).
     */
    public Sucursal obtenerPorId(int idSucursal) {
        Cursor c = db.rawQuery(
                "SELECT * FROM SUCURSAL WHERE ID_SUCURSAL = ?",
                new String[]{String.valueOf(idSucursal)});
        Sucursal s = null;
        if (c.moveToFirst()) {
            s = cursorToSucursal(c);
        }
        c.close();
        return s;
    }

    /**
     * Devuelve todas las sucursales (activas e inactivas).
     */
    public List<Sucursal> obtenerTodos() {
        List<Sucursal> lista = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM SUCURSAL", null);
        if (c.moveToFirst()) {
            do {
                lista.add(cursorToSucursal(c));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    /**
     * Devuelve sólo las sucursales activas.
     */
    public List<Sucursal> obtenerActivos() {
        List<Sucursal> lista = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT * FROM SUCURSAL WHERE ACTIVO_SUCURSAL = 1", null);
        if (c.moveToFirst()) {
            do {
                lista.add(cursorToSucursal(c));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    /**
     * Actualiza todos los campos de la sucursal, incluyendo estado activo.
     */
    public int actualizar(Sucursal s) {
        ContentValues values = new ContentValues();
        values.put("ID_DEPARTAMENTO", s.getIdDepartamento());
        values.put("ID_MUNICIPIO", s.getIdMunicipio());
        values.put("ID_DISTRITO", s.getIdDistrito());
        values.put("NOMBRE_SUCURSAL", s.getNombreSucursal());
        values.put("DIRECCION_SUCURSAL", s.getDireccionSucursal());
        values.put("TELEFONO_SUCURSAL", s.getTelefonoSucursal());
        values.put("HORARIO_APERTURA_SUCURSAL", s.getHorarioApertura());
        values.put("HORARIO_CIERRE_SUCURSAL", s.getHorarioCierre());
        values.put("ACTIVO_SUCURSAL", s.getActivoSucursal());
        return db.update(
                "SUCURSAL", values,
                "ID_SUCURSAL = ?", new String[]{String.valueOf(s.getIdSucursal())}
        );
    }

    /**
     * Soft delete: marca la sucursal como inactiva.
     */
    public int eliminar(int idSucursal) {
        ContentValues values = new ContentValues();
        values.put("ACTIVO_SUCURSAL", 0);
        return db.update(
                "SUCURSAL", values,
                "ID_SUCURSAL = ?", new String[]{String.valueOf(idSucursal)}
        );
    }

    /**
     * Mapea un cursor a un objeto Sucursal.
     */
    private Sucursal cursorToSucursal(Cursor c) {
        Sucursal s = new Sucursal();
        s.setIdSucursal(c.getInt(c.getColumnIndexOrThrow("ID_SUCURSAL")));
        s.setIdDepartamento(c.getInt(c.getColumnIndexOrThrow("ID_DEPARTAMENTO")));
        s.setIdMunicipio(c.getInt(c.getColumnIndexOrThrow("ID_MUNICIPIO")));
        s.setIdDistrito(c.getInt(c.getColumnIndexOrThrow("ID_DISTRITO")));
        s.setNombreSucursal(c.getString(c.getColumnIndexOrThrow("NOMBRE_SUCURSAL")));
        s.setDireccionSucursal(c.getString(c.getColumnIndexOrThrow("DIRECCION_SUCURSAL")));
        s.setTelefonoSucursal(c.getString(c.getColumnIndexOrThrow("TELEFONO_SUCURSAL")));
        s.setHorarioApertura(c.getString(c.getColumnIndexOrThrow("HORARIO_APERTURA_SUCURSAL")));
        s.setHorarioCierre(c.getString(c.getColumnIndexOrThrow("HORARIO_CIERRE_SUCURSAL")));
        s.setActivoSucursal(c.getInt(c.getColumnIndexOrThrow("ACTIVO_SUCURSAL")));
        return s;
    }
}
