package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log; // Para logs

import java.util.ArrayList;
import java.util.List;

public class DireccionDAO {

    private final SQLiteDatabase db;
    private static final String TAG = "DireccionDAO";

    // Constantes para la tabla y columnas
    private static final String TABLA_DIRECCION = "DIRECCION";
    private static final String COL_ID_CLIENTE = "ID_CLIENTE";
    private static final String COL_ID_DIRECCION = "ID_DIRECCION";
    private static final String COL_ID_DEPARTAMENTO = "ID_DEPARTAMENTO";
    private static final String COL_ID_MUNICIPIO = "ID_MUNICIPIO";
    private static final String COL_ID_DISTRITO = "ID_DISTRITO";
    private static final String COL_DIRECCION_ESPECIFICA = "DIRECCION_ESPECIFICA";
    private static final String COL_DESCRIPCION_DIRECCION = "DESCRIPCION_DIRECCION";
    private static final String COL_ACTIVO_DIRECCION = "ACTIVO_DIRECCION"; // Nueva columna

    // Array con todas las columnas para consultas
    private static final String[] COLUMNAS_DIRECCION = {
            COL_ID_CLIENTE, COL_ID_DIRECCION, COL_ID_DEPARTAMENTO, COL_ID_MUNICIPIO,
            COL_ID_DISTRITO, COL_DIRECCION_ESPECIFICA, COL_DESCRIPCION_DIRECCION,
            COL_ACTIVO_DIRECCION // Incluir nueva columna
    };

    public DireccionDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Calcula el siguiente ID_DIRECCION para un cliente dado
     * (MAX(ID_DIRECCION) + 1, o 1 si no hay ninguno).
     * La lógica original se mantiene.
     */
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

    /**
     * Inserta una nueva dirección con clave compuesta.
     * Incluye el campo ACTIVO_DIRECCION. Se espera que el valor (0 o 1) venga en el objeto Direccion.
     * Si no se setea en el objeto Direccion antes de llamar, y la DB tiene DEFAULT 1, ese valor se usará.
     * Por seguridad, es mejor setearlo explícitamente (ej. a 1) en la lógica de creación.
     */
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

    /**
     * Consulta una dirección específica por su clave primaria compuesta (cliente, dirección).
     * Ahora devuelve también el estado ACTIVO_DIRECCION.
     */
    public Direccion consultarPorId(int idCliente, int idDireccion) {
        Direccion direccion = null;
        Cursor cursor = null;
        String selection = COL_ID_CLIENTE + "=? AND " + COL_ID_DIRECCION + "=?";
        String[] selectionArgs = {String.valueOf(idCliente), String.valueOf(idDireccion)};

        try {
            cursor = db.query(TABLA_DIRECCION, COLUMNAS_DIRECCION, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                direccion = cursorToDireccion(cursor); // Usar helper
                Log.d(TAG, "Dirección consultada: Cliente " + idCliente + ", ID_Direccion " + idDireccion);
            } else {
                Log.d(TAG, "Dirección NO encontrada: Cliente " + idCliente + ", ID_Direccion " + idDireccion);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error consultando dirección: Cliente " + idCliente + ", ID_Direccion " + idDireccion, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return direccion;
    }

    /**
     * Obtiene todas las direcciones (activas e inactivas) de un cliente dado.
     * La lógica original no filtraba por estado activo, se mantiene así.
     */
    public List<Direccion> obtenerPorCliente(int idCliente) {
        List<Direccion> lista = new ArrayList<>();
        Cursor cursor = null;
        // Usar nombres de columna explícitos en lugar de SELECT *
        String query = "SELECT " + String.join(",", COLUMNAS_DIRECCION) +
                " FROM " + TABLA_DIRECCION +
                " WHERE " + COL_ID_CLIENTE + " = ?";
        try {
            cursor = db.rawQuery(query, new String[]{ String.valueOf(idCliente) });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    lista.add(cursorToDireccion(cursor)); // Usar helper
                } while (cursor.moveToNext());
                Log.d(TAG, "Se obtuvieron " + lista.size() + " direcciones para el Cliente " + idCliente);
            } else {
                Log.d(TAG, "No se encontraron direcciones para el Cliente " + idCliente);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error obteniendo direcciones para Cliente " + idCliente, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return lista;
    }

    /**
     * Actualiza una dirección existente. Ahora permite actualizar también ACTIVO_DIRECCION.
     */
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

    /**
     * Elimina físicamente la dirección identificada (Hard Delete).
     * La lógica original se mantiene.
     * Considera implementar un método 'desactivar' que use 'actualizar'
     * para poner ACTIVO_DIRECCION = 0 (Soft Delete) si es necesario.
     */
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

    /**
     * Devuelve todas las direcciones (activas e inactivas) de la tabla.
     * La lógica original no filtraba por estado activo, se mantiene así.
     */
    public List<Direccion> obtenerTodas() {
        List<Direccion> lista = new ArrayList<>();
        Cursor cursor = null;
        try {
            // Usar nombres de columna explícitos
            cursor = db.query(TABLA_DIRECCION, COLUMNAS_DIRECCION, null, null, null, null, COL_ID_CLIENTE + " ASC, " + COL_ID_DIRECCION + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    lista.add(cursorToDireccion(cursor)); // Usar helper
                } while (cursor.moveToNext());
                Log.d(TAG, "Se obtuvieron " + lista.size() + " direcciones en total.");
            } else {
                Log.d(TAG, "No se encontraron direcciones en obtenerTodas().");
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error obteniendo todas las direcciones", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return lista;
    }

    /**
     * Método helper para convertir una fila del Cursor en un objeto Direccion.
     * @param cursor Cursor posicionado en la fila deseada.
     * @return Objeto Direccion poblado.
     */
    private Direccion cursorToDireccion(Cursor cursor) {
        Direccion dir = new Direccion();
        dir.setIdCliente(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_CLIENTE)));
        dir.setIdDireccion(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_DIRECCION)));
        dir.setIdDepartamento(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_DEPARTAMENTO)));
        dir.setIdMunicipio(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_MUNICIPIO)));
        dir.setIdDistrito(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_DISTRITO)));
        dir.setDireccionEspecifica(cursor.getString(cursor.getColumnIndexOrThrow(COL_DIRECCION_ESPECIFICA)));
        dir.setDescripcionDireccion(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPCION_DIRECCION)));
        dir.setActivoDireccion(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIVO_DIRECCION))); // Obtener nuevo campo
        return dir;
    }
}