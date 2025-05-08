package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

// ... (imports sin cambios) ...
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DireccionDAO {

    private final SQLiteDatabase db;
    private static final String TAG = "DireccionDAO";

    // Constantes para tablas relacionadas
    private static final String TABLA_DIRECCION = "DIRECCION";
    private static final String TABLA_DEPTO = "DEPARTAMENTO";
    private static final String TABLA_MUN = "MUNICIPIO";
    private static final String TABLA_DIST = "DISTRITO";

    // Constantes para columnas (incluyendo las de tablas unidas)
    private static final String COL_ID_CLIENTE = "ID_CLIENTE";
    private static final String COL_ID_DIRECCION = "ID_DIRECCION";
    private static final String COL_ID_DEPARTAMENTO = "ID_DEPARTAMENTO";
    private static final String COL_ID_MUNICIPIO = "ID_MUNICIPIO";
    private static final String COL_ID_DISTRITO = "ID_DISTRITO";
    private static final String COL_DIRECCION_ESPECIFICA = "DIRECCION_ESPECIFICA";
    private static final String COL_DESCRIPCION_DIRECCION = "DESCRIPCION_DIRECCION";
    private static final String COL_ACTIVO_DIRECCION = "ACTIVO_DIRECCION";
    // Nombres de columnas de tablas unidas (usaremos alias para evitar ambigüedad si fuera necesario)
    private static final String COL_NOMBRE_DEPTO = "NOMBRE_DEPARTAMENTO";
    private static final String COL_NOMBRE_MUN = "NOMBRE_MUNICIPIO";
    private static final String COL_NOMBRE_DIST = "NOMBRE_DISTRITO";

    // Array con todas las columnas a seleccionar (de DIRECCION y las unidas)
    // Usamos alias D, DEP, MUN, DIST para las tablas
    private static final String[] COLUMNAS_DIRECCION_CON_NOMBRES = {
            "D." + COL_ID_CLIENTE, "D." + COL_ID_DIRECCION, "D." + COL_ID_DEPARTAMENTO,
            "D." + COL_ID_MUNICIPIO, "D." + COL_ID_DISTRITO, "D." + COL_DIRECCION_ESPECIFICA,
            "D." + COL_DESCRIPCION_DIRECCION, "D." + COL_ACTIVO_DIRECCION,
            "DEP." + COL_NOMBRE_DEPTO, "MUN." + COL_NOMBRE_MUN, "DIST." + COL_NOMBRE_DIST
    };


    public DireccionDAO(SQLiteDatabase db) { this.db = db; }

    // --- Métodos insertar, actualizar, eliminar, getNextId (Sin cambios lógicos internos) ---
    // (El código de estos métodos que ya adaptamos se mantiene igual)
    // ... (copia aquí los métodos insertar, actualizar, eliminar, getNextIdDireccion adaptados previamente) ...
    public int getNextIdDireccion(int idCliente) {
        int next = 1;
        Cursor cursor = null;
        String query = "SELECT MAX(" + COL_ID_DIRECCION + ") FROM " + TABLA_DIRECCION + " WHERE " + COL_ID_CLIENTE + " = ?";
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(idCliente)});
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                next = cursor.getInt(0) + 1;
            }
            Log.d(TAG, "Siguiente ID_DIRECCION para Cliente " + idCliente + ": " + next);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error obteniendo nextIdDireccion para Cliente " + idCliente, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return next;
    }
    public long insertar(Direccion dir) {
        ContentValues values = new ContentValues();
        values.put(COL_ID_CLIENTE, dir.getIdCliente());
        values.put(COL_ID_DIRECCION, dir.getIdDireccion());
        values.put(COL_ID_DEPARTAMENTO, dir.getIdDepartamento());
        values.put(COL_ID_MUNICIPIO, dir.getIdMunicipio());
        values.put(COL_ID_DISTRITO, dir.getIdDistrito());
        values.put(COL_DIRECCION_ESPECIFICA, dir.getDireccionEspecifica());
        values.put(COL_DESCRIPCION_DIRECCION, dir.getDescripcionDireccion());
        values.put(COL_ACTIVO_DIRECCION, dir.getActivoDireccion()); // Añadir el nuevo campo

        try {
            long result = db.insertOrThrow(TABLA_DIRECCION, null, values);
            Log.i(TAG, "Dirección insertada para Cliente " + dir.getIdCliente() + " con ID_Direccion " + dir.getIdDireccion());
            return result;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error al insertar dirección para Cliente " + dir.getIdCliente() + ", ID_Direccion " + dir.getIdDireccion(), e);
            return -1; // Indicar error
        }
    }
    public int actualizar(Direccion direccion) {
        ContentValues valores = new ContentValues();
        // No se actualizan las claves primarias (ID_CLIENTE, ID_DIRECCION)
        valores.put(COL_ID_DEPARTAMENTO, direccion.getIdDepartamento());
        valores.put(COL_ID_MUNICIPIO, direccion.getIdMunicipio());
        valores.put(COL_ID_DISTRITO, direccion.getIdDistrito());
        valores.put(COL_DIRECCION_ESPECIFICA, direccion.getDireccionEspecifica());
        valores.put(COL_DESCRIPCION_DIRECCION, direccion.getDescripcionDireccion());
        valores.put(COL_ACTIVO_DIRECCION, direccion.getActivoDireccion()); // Añadir el nuevo campo

        String whereClause = COL_ID_CLIENTE + "=? AND " + COL_ID_DIRECCION + "=?";
        String[] whereArgs = {
                String.valueOf(direccion.getIdCliente()),
                String.valueOf(direccion.getIdDireccion())
        };

        try {
            int rowsAffected = db.update(TABLA_DIRECCION, valores, whereClause, whereArgs);
            if (rowsAffected > 0) {
                Log.i(TAG, "Dirección actualizada: Cliente " + direccion.getIdCliente() + ", ID_Direccion " + direccion.getIdDireccion());
            } else {
                Log.w(TAG, "No se actualizó dirección: Cliente " + direccion.getIdCliente() + ", ID_Direccion " + direccion.getIdDireccion() + " (quizás no existe)");
            }
            return rowsAffected;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error al actualizar dirección: Cliente " + direccion.getIdCliente() + ", ID_Direccion " + direccion.getIdDireccion(), e);
            return 0;
        }
    }
    public int eliminar(int idCliente, int idDireccion) {
        String whereClause = COL_ID_CLIENTE + " = ? AND " + COL_ID_DIRECCION + " = ?";
        String[] whereArgs = {
                String.valueOf(idCliente),
                String.valueOf(idDireccion)
        };
        try {
            int rowsAffected = db.delete(TABLA_DIRECCION, whereClause, whereArgs);
            if (rowsAffected > 0) {
                Log.i(TAG, "Dirección eliminada: Cliente " + idCliente + ", ID_Direccion " + idDireccion);
            } else {
                Log.w(TAG, "No se eliminó dirección: Cliente " + idCliente + ", ID_Direccion " + idDireccion + " (quizás no existía)");
            }
            return rowsAffected;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error al eliminar dirección: Cliente " + idCliente + ", ID_Direccion " + idDireccion, e);
            return 0;
        }
    }

    // --- Métodos de Consulta Modificados ---

    /**
     * Consulta una dirección específica por su PK, incluyendo nombres de Depto/Mun/Dist.
     */
    public Direccion consultarPorId(int idCliente, int idDireccion) {
        Direccion direccion = null;
        Cursor cursor = null;
        String table = TABLA_DIRECCION + " D" +
                " INNER JOIN " + TABLA_DEPTO + " DEP ON D." + COL_ID_DEPARTAMENTO + " = DEP." + COL_ID_DEPARTAMENTO +
                " INNER JOIN " + TABLA_MUN + " MUN ON D." + COL_ID_MUNICIPIO + " = MUN." + COL_ID_MUNICIPIO + " AND D." + COL_ID_DEPARTAMENTO + " = MUN." + COL_ID_DEPARTAMENTO +
                " INNER JOIN " + TABLA_DIST + " DIST ON D." + COL_ID_DISTRITO + " = DIST." + COL_ID_DISTRITO + " AND D." + COL_ID_MUNICIPIO + " = DIST." + COL_ID_MUNICIPIO + " AND D." + COL_ID_DEPARTAMENTO + " = DIST." + COL_ID_DEPARTAMENTO;

        String selection = "D." + COL_ID_CLIENTE + "=? AND D." + COL_ID_DIRECCION + "=?";
        String[] selectionArgs = {String.valueOf(idCliente), String.valueOf(idDireccion)};

        try {
            cursor = db.query(table, COLUMNAS_DIRECCION_CON_NOMBRES, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                direccion = cursorToDireccion(cursor); // Usar helper actualizado
                Log.d(TAG, "Dirección consultada (con nombres): Cliente " + idCliente + ", ID_Direccion " + idDireccion);
            } else {
                Log.d(TAG, "Dirección NO encontrada (con nombres): Cliente " + idCliente + ", ID_Direccion " + idDireccion);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error consultando dirección (con nombres): Cliente " + idCliente + ", ID_Direccion " + idDireccion, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return direccion;
    }

    /**
     * Obtiene todas las direcciones (activas e inactivas) de un cliente dado,
     * incluyendo los nombres de Depto/Mun/Dist.
     */
    public List<Direccion> obtenerPorCliente(int idCliente) {
        List<Direccion> lista = new ArrayList<>();
        Cursor cursor = null;
        String query = "SELECT " + String.join(",", COLUMNAS_DIRECCION_CON_NOMBRES) +
                " FROM " + TABLA_DIRECCION + " D" +
                " INNER JOIN " + TABLA_DEPTO + " DEP ON D." + COL_ID_DEPARTAMENTO + " = DEP." + COL_ID_DEPARTAMENTO +
                " INNER JOIN " + TABLA_MUN + " MUN ON D." + COL_ID_MUNICIPIO + " = MUN." + COL_ID_MUNICIPIO + " AND D." + COL_ID_DEPARTAMENTO + " = MUN." + COL_ID_DEPARTAMENTO +
                " INNER JOIN " + TABLA_DIST + " DIST ON D." + COL_ID_DISTRITO + " = DIST." + COL_ID_DISTRITO + " AND D." + COL_ID_MUNICIPIO + " = DIST." + COL_ID_MUNICIPIO + " AND D." + COL_ID_DEPARTAMENTO + " = DIST." + COL_ID_DEPARTAMENTO +
                " WHERE D." + COL_ID_CLIENTE + " = ?" +
                " ORDER BY D." + COL_ID_DIRECCION + " ASC"; // Ordenar por ID de dirección

        try {
            cursor = db.rawQuery(query, new String[]{ String.valueOf(idCliente) });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    lista.add(cursorToDireccion(cursor)); // Usar helper actualizado
                } while (cursor.moveToNext());
                Log.d(TAG, "Se obtuvieron " + lista.size() + " direcciones (con nombres) para el Cliente " + idCliente);
            } else {
                Log.d(TAG, "No se encontraron direcciones (con nombres) para el Cliente " + idCliente);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error obteniendo direcciones (con nombres) para Cliente " + idCliente, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return lista;
    }

    /**
     * Devuelve todas las direcciones (activas e inactivas), incluyendo nombres de Depto/Mun/Dist.
     */
    public List<Direccion> obtenerTodas() {
        List<Direccion> lista = new ArrayList<>();
        Cursor cursor = null;
        String query = "SELECT " + String.join(",", COLUMNAS_DIRECCION_CON_NOMBRES) +
                " FROM " + TABLA_DIRECCION + " D" +
                " INNER JOIN " + TABLA_DEPTO + " DEP ON D." + COL_ID_DEPARTAMENTO + " = DEP." + COL_ID_DEPARTAMENTO +
                " INNER JOIN " + TABLA_MUN + " MUN ON D." + COL_ID_MUNICIPIO + " = MUN." + COL_ID_MUNICIPIO + " AND D." + COL_ID_DEPARTAMENTO + " = MUN." + COL_ID_DEPARTAMENTO +
                " INNER JOIN " + TABLA_DIST + " DIST ON D." + COL_ID_DISTRITO + " = DIST." + COL_ID_DISTRITO + " AND D." + COL_ID_MUNICIPIO + " = DIST." + COL_ID_MUNICIPIO + " AND D." + COL_ID_DEPARTAMENTO + " = DIST." + COL_ID_DEPARTAMENTO +
                " ORDER BY D." + COL_ID_CLIENTE + " ASC, D." + COL_ID_DIRECCION + " ASC";
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    lista.add(cursorToDireccion(cursor)); // Usar helper actualizado
                } while (cursor.moveToNext());
                Log.d(TAG, "Se obtuvieron " + lista.size() + " direcciones totales (con nombres).");
            } else {
                Log.d(TAG, "No se encontraron direcciones en obtenerTodas() (con nombres).");
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error obteniendo todas las direcciones (con nombres)", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return lista;
    }

    /**
     * Método helper para convertir una fila del Cursor en un objeto Direccion,
     * incluyendo los nombres de Depto/Mun/Dist obtenidos del JOIN.
     */
    private Direccion cursorToDireccion(Cursor cursor) {
        Direccion dir = new Direccion();
        // --- Campos de DIRECCION ---
        dir.setIdCliente(cursor.getInt(cursor.getColumnIndexOrThrow("D." + COL_ID_CLIENTE)));
        dir.setIdDireccion(cursor.getInt(cursor.getColumnIndexOrThrow("D." + COL_ID_DIRECCION)));
        dir.setIdDepartamento(cursor.getInt(cursor.getColumnIndexOrThrow("D." + COL_ID_DEPARTAMENTO)));
        dir.setIdMunicipio(cursor.getInt(cursor.getColumnIndexOrThrow("D." + COL_ID_MUNICIPIO)));
        dir.setIdDistrito(cursor.getInt(cursor.getColumnIndexOrThrow("D." + COL_ID_DISTRITO)));
        dir.setDireccionEspecifica(cursor.getString(cursor.getColumnIndexOrThrow("D." + COL_DIRECCION_ESPECIFICA)));
        dir.setDescripcionDireccion(cursor.getString(cursor.getColumnIndexOrThrow("D." + COL_DESCRIPCION_DIRECCION)));
        dir.setActivoDireccion(cursor.getInt(cursor.getColumnIndexOrThrow("D." + COL_ACTIVO_DIRECCION)));
        // --- Campos de Nombres (JOIN) ---
        dir.setNombreDepartamento(cursor.getString(cursor.getColumnIndexOrThrow("DEP." + COL_NOMBRE_DEPTO)));
        dir.setNombreMunicipio(cursor.getString(cursor.getColumnIndexOrThrow("MUN." + COL_NOMBRE_MUN)));
        dir.setNombreDistrito(cursor.getString(cursor.getColumnIndexOrThrow("DIST." + COL_NOMBRE_DIST)));
        return dir;
    }
}