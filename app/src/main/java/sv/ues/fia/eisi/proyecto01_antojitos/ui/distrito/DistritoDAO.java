package sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito.Distrito;

public class DistritoDAO {
    private SQLiteDatabase db;

    public DistritoDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertarDistrito(Distrito distrito) {
        try {
            ContentValues valores = new ContentValues();
            valores.put("ID_DEPARTAMENTO", distrito.getIdDepartamento());
            valores.put("ID_MUNICIPIO", distrito.getIdMunicipio());
            valores.put("ID_DISTRITO", distrito.getIdDistrito());
            valores.put("NOMBRE_DISTRITO", distrito.getNombreDistrito());
            valores.put("CODIGO_POSTAL", distrito.getCodigoPostal());
            valores.put("ACTIVO_DISTRITO", distrito.getActivoDistrito());

            return db.insert("DISTRITO", null, valores);
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al insertar distrito: " + e.getMessage());
        }
    }

    public int actualizarDistrito(Distrito distrito) {
        try {
            ContentValues valores = new ContentValues();
            valores.put("NOMBRE_DISTRITO", distrito.getNombreDistrito());
            valores.put("CODIGO_POSTAL", distrito.getCodigoPostal());
            valores.put("ACTIVO_DISTRITO", distrito.getActivoDistrito());

            String whereClause = "ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ? AND ID_DISTRITO = ?";
            String[] whereArgs = {
                    String.valueOf(distrito.getIdDepartamento()),
                    String.valueOf(distrito.getIdMunicipio()),
                    String.valueOf(distrito.getIdDistrito())
            };

            return db.update("DISTRITO", valores, whereClause, whereArgs);
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al actualizar distrito: " + e.getMessage());
        }
    }

    public int eliminarDistrito(int idDepartamento, int idMunicipio, int idDistrito) {
        try {
            ContentValues valores = new ContentValues();
            valores.put("ACTIVO_DISTRITO", 0);

            String whereClause = "ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ? AND ID_DISTRITO = ?";
            String[] whereArgs = {
                    String.valueOf(idDepartamento),
                    String.valueOf(idMunicipio),
                    String.valueOf(idDistrito)
            };

            return db.update("DISTRITO", valores, whereClause, whereArgs);
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al eliminar (soft delete) distrito: " + e.getMessage());
        }
    }

    public Distrito obtenerDistritoPorId(int idDepartamento, int idMunicipio, int idDistrito) {
        Distrito distrito = null;
        String[] columnas = {
                "ID_DEPARTAMENTO", "ID_MUNICIPIO", "ID_DISTRITO",
                "NOMBRE_DISTRITO", "CODIGO_POSTAL", "ACTIVO_DISTRITO"
        };
        String whereClause = "ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ? AND ID_DISTRITO = ?";
        String[] whereArgs = {
                String.valueOf(idDepartamento),
                String.valueOf(idMunicipio),
                String.valueOf(idDistrito)
        };

        try (Cursor cursor = db.query("DISTRITO", columnas, whereClause, whereArgs,
                null, null, null)) {
            if (cursor.moveToFirst()) {
                distrito = cursorADistrito(cursor);
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al obtener distrito: " + e.getMessage());
        }

        return distrito;
    }

    public List<Distrito> obtenerTodosLosDistritos() {
        List<Distrito> distritos = new ArrayList<>();
        String[] columnas = {
                "ID_DEPARTAMENTO", "ID_MUNICIPIO", "ID_DISTRITO",
                "NOMBRE_DISTRITO", "CODIGO_POSTAL", "ACTIVO_DISTRITO"
        };

        try (Cursor cursor = db.query("DISTRITO", columnas, null, null,
                null, null, "NOMBRE_DISTRITO")) {
            while (cursor.moveToNext()) {
                distritos.add(cursorADistrito(cursor));
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al obtener distritos: " + e.getMessage());
        }

        return distritos;
    }

    public List<Distrito> obtenerDistritosPorMunicipio(int idDepartamento, int idMunicipio) {
        List<Distrito> distritos = new ArrayList<>();
        String[] columnas = {
                "ID_DEPARTAMENTO", "ID_MUNICIPIO", "ID_DISTRITO",
                "NOMBRE_DISTRITO", "CODIGO_POSTAL", "ACTIVO_DISTRITO"
        };
        String whereClause = "ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ?";
        String[] whereArgs = {
                String.valueOf(idDepartamento),
                String.valueOf(idMunicipio)
        };

        try (Cursor cursor = db.query("DISTRITO", columnas, whereClause, whereArgs,
                null, null, "NOMBRE_DISTRITO")) {
            while (cursor.moveToNext()) {
                distritos.add(cursorADistrito(cursor));
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al obtener distritos por municipio: " + e.getMessage());
        }

        return distritos;
    }

    public List<Distrito> obtenerDistritosActivos() {
        List<Distrito> distritos = new ArrayList<>();
        String[] columnas = {
                "ID_DEPARTAMENTO", "ID_MUNICIPIO", "ID_DISTRITO",
                "NOMBRE_DISTRITO", "CODIGO_POSTAL", "ACTIVO_DISTRITO"
        };
        String whereClause = "ACTIVO_DISTRITO = 1";

        try (Cursor cursor = db.query("DISTRITO", columnas, whereClause, null,
                null, null, "NOMBRE_DISTRITO")) {
            while (cursor.moveToNext()) {
                distritos.add(cursorADistrito(cursor));
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al obtener distritos activos: " + e.getMessage());
        }

        return distritos;
    }

    private Distrito cursorADistrito(Cursor cursor) {
        Distrito distrito = new Distrito();
        distrito.setIdDepartamento(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DEPARTAMENTO")));
        distrito.setIdMunicipio(cursor.getInt(cursor.getColumnIndexOrThrow("ID_MUNICIPIO")));
        distrito.setIdDistrito(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DISTRITO")));
        distrito.setNombreDistrito(cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_DISTRITO")));
        distrito.setCodigoPostal(cursor.getString(cursor.getColumnIndexOrThrow("CODIGO_POSTAL")));
        distrito.setActivoDistrito(cursor.getInt(cursor.getColumnIndexOrThrow("ACTIVO_DISTRITO")));
        return distrito;
    }
}