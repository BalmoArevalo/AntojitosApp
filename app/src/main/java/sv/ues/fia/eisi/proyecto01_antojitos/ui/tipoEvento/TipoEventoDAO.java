package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento; // Ajusta el paquete si es necesario

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// Asumiendo que TipoEvento.java está en el mismo paquete o importado correctamente
// import sv.ues.fia.eisi.proyecto01_antojitos.TipoEvento; // Si estuviera en un paquete de modelo/entidad

public class TipoEventoDAO {

    private final SQLiteDatabase db;
    private static final String TAG = "TipoEventoDAO";

    // Constantes para la tabla TIPOEVENTO y sus columnas
    private static final String TABLA_TIPOEVENTO = "TIPOEVENTO";
    private static final String COL_ID_TIPO_EVENTO = "ID_TIPO_EVENTO";
    private static final String COL_NOMBRE_TIPO_EVENTO = "NOMBRE_TIPO_EVENTO";
    private static final String COL_DESCRIPCION_TIPO_EVENTO = "DESCRIPCION_TIPO_EVENTO";
    private static final String COL_MONTO_MINIMO = "MONTO_MINIMO";
    private static final String COL_MONTO_MAXIMO = "MONTO_MAXIMO";
    private static final String COL_ACTIVO_TIPOEVENTO = "ACTIVO_TIPOEVENTO";

    // Array con todas las columnas a seleccionar
    private static final String[] TODAS_COLUMNAS = {
            COL_ID_TIPO_EVENTO,
            COL_NOMBRE_TIPO_EVENTO,
            COL_DESCRIPCION_TIPO_EVENTO,
            COL_MONTO_MINIMO,
            COL_MONTO_MAXIMO,
            COL_ACTIVO_TIPOEVENTO
    };

    public TipoEventoDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Inserta un nuevo TipoEvento en la base de datos.
     * Como ID_TIPO_EVENTO es AUTOINCREMENT, no es necesario pasarlo en el objeto TipoEvento
     * si se está creando uno nuevo. El ID se generará y se puede obtener
     * si se consulta después o si la inserción devuelve el rowId.
     *
     * @param tipoEvento El objeto TipoEvento a insertar (sin ID si es nuevo).
     * @return el row ID de la nueva fila insertada, o -1 si ocurrió un error.
     */
    public long insertar(TipoEvento tipoEvento) {
        ContentValues values = new ContentValues();
        // No incluimos ID_TIPO_EVENTO si es AUTOINCREMENT y es una nueva inserción.
        // Si tipoEvento.getIdTipoEvento() es 0 o un valor no significativo, está bien.
        // Si se está insertando con un ID específico (ej. desde un seeder con INSERT OR REPLACE),
        // se podría añadir: values.put(COL_ID_TIPO_EVENTO, tipoEvento.getIdTipoEvento());
        values.put(COL_NOMBRE_TIPO_EVENTO, tipoEvento.getNombreTipoEvento());
        values.put(COL_DESCRIPCION_TIPO_EVENTO, tipoEvento.getDescripcionTipoEvento());
        values.put(COL_MONTO_MINIMO, tipoEvento.getMontoMinimo());
        values.put(COL_MONTO_MAXIMO, tipoEvento.getMontoMaximo());
        values.put(COL_ACTIVO_TIPOEVENTO, tipoEvento.getActivoTipoEvento());

        try {
            long result = db.insertOrThrow(TABLA_TIPOEVENTO, null, values);
            Log.i(TAG, "TipoEvento insertado con ID: " + result + ", Nombre: " + tipoEvento.getNombreTipoEvento());
            return result; // Devuelve el ID de la fila insertada
        } catch (SQLiteException e) {
            Log.e(TAG, "Error al insertar TipoEvento: " + tipoEvento.getNombreTipoEvento(), e);
            return -1; // Indicar error
        }
    }

    /**
     * Actualiza un TipoEvento existente en la base de datos.
     *
     * @param tipoEvento El objeto TipoEvento con los datos actualizados.
     * Se usa su ID para encontrar el registro a actualizar.
     * @return el número de filas afectadas.
     */
    public int actualizar(TipoEvento tipoEvento) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE_TIPO_EVENTO, tipoEvento.getNombreTipoEvento());
        values.put(COL_DESCRIPCION_TIPO_EVENTO, tipoEvento.getDescripcionTipoEvento());
        values.put(COL_MONTO_MINIMO, tipoEvento.getMontoMinimo());
        values.put(COL_MONTO_MAXIMO, tipoEvento.getMontoMaximo());
        values.put(COL_ACTIVO_TIPOEVENTO, tipoEvento.getActivoTipoEvento());

        String whereClause = COL_ID_TIPO_EVENTO + " = ?";
        String[] whereArgs = {String.valueOf(tipoEvento.getIdTipoEvento())};

        try {
            int rowsAffected = db.update(TABLA_TIPOEVENTO, values, whereClause, whereArgs);
            if (rowsAffected > 0) {
                Log.i(TAG, "TipoEvento actualizado: ID " + tipoEvento.getIdTipoEvento());
            } else {
                Log.w(TAG, "No se actualizó TipoEvento: ID " + tipoEvento.getIdTipoEvento() + " (quizás no existe)");
            }
            return rowsAffected;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error al actualizar TipoEvento: ID " + tipoEvento.getIdTipoEvento(), e);
            return 0;
        }
    }

    /**
     * Elimina un TipoEvento de la base de datos.
     * (Este es un borrado físico. Considerar borrado lógico si es necesario).
     *
     * @param idTipoEvento El ID del TipoEvento a eliminar.
     * @return el número de filas afectadas.
     */
    public int eliminar(int idTipoEvento) {
        String whereClause = COL_ID_TIPO_EVENTO + " = ?";
        String[] whereArgs = {String.valueOf(idTipoEvento)};

        try {
            int rowsAffected = db.delete(TABLA_TIPOEVENTO, whereClause, whereArgs);
            if (rowsAffected > 0) {
                Log.i(TAG, "TipoEvento eliminado: ID " + idTipoEvento);
            } else {
                Log.w(TAG, "No se eliminó TipoEvento: ID " + idTipoEvento + " (quizás no existía)");
            }
            return rowsAffected;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error al eliminar TipoEvento: ID " + idTipoEvento, e);
            return 0;
        }
    }

    /**
     * Consulta un TipoEvento específico por su ID.
     *
     * @param idTipoEvento El ID del TipoEvento a consultar.
     * @return El objeto TipoEvento si se encuentra, o null si no.
     */
    public TipoEvento consultarPorId(int idTipoEvento) {
        Cursor cursor = null;
        TipoEvento tipoEvento = null;
        String selection = COL_ID_TIPO_EVENTO + " = ?";
        String[] selectionArgs = {String.valueOf(idTipoEvento)};

        try {
            cursor = db.query(
                    TABLA_TIPOEVENTO,
                    TODAS_COLUMNAS,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                tipoEvento = cursorToTipoEvento(cursor);
                Log.d(TAG, "TipoEvento consultado: ID " + idTipoEvento);
            } else {
                Log.d(TAG, "TipoEvento NO encontrado: ID " + idTipoEvento);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error consultando TipoEvento: ID " + idTipoEvento, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tipoEvento;
    }

    /**
     * Obtiene todos los TiposDeEvento de la base de datos.
     *
     * @return Una lista de todos los TiposDeEvento. Puede estar vacía.
     */
    public List<TipoEvento> obtenerTodos() {
        List<TipoEvento> listaTiposEvento = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLA_TIPOEVENTO,
                    TODAS_COLUMNAS,
                    null, null, null, null,
                    COL_NOMBRE_TIPO_EVENTO + " ASC" // Ordenar por nombre
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    listaTiposEvento.add(cursorToTipoEvento(cursor));
                } while (cursor.moveToNext());
                Log.d(TAG, "Se obtuvieron " + listaTiposEvento.size() + " TiposDeEvento.");
            } else {
                Log.d(TAG, "No se encontraron TiposDeEvento en obtenerTodos().");
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error obteniendo todos los TiposDeEvento", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return listaTiposEvento;
    }

    /**
     * Obtiene todos los TiposDeEvento activos de la base de datos.
     *
     * @return Una lista de todos los TiposDeEvento activos. Puede estar vacía.
     */
    public List<TipoEvento> obtenerTodosActivos() {
        List<TipoEvento> listaTiposEvento = new ArrayList<>();
        Cursor cursor = null;
        String selection = COL_ACTIVO_TIPOEVENTO + " = ?";
        String[] selectionArgs = {"1"}; // "1" para activo

        try {
            cursor = db.query(
                    TABLA_TIPOEVENTO,
                    TODAS_COLUMNAS,
                    selection,
                    selectionArgs,
                    null, null,
                    COL_NOMBRE_TIPO_EVENTO + " ASC" // Ordenar por nombre
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    listaTiposEvento.add(cursorToTipoEvento(cursor));
                } while (cursor.moveToNext());
                Log.d(TAG, "Se obtuvieron " + listaTiposEvento.size() + " TiposDeEvento activos.");
            } else {
                Log.d(TAG, "No se encontraron TiposDeEvento activos.");
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error obteniendo TiposDeEvento activos", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return listaTiposEvento;
    }


    /**
     * Método helper para convertir una fila del Cursor en un objeto TipoEvento.
     *
     * @param cursor El Cursor posicionado en la fila deseada.
     * @return Un objeto TipoEvento.
     */
    private TipoEvento cursorToTipoEvento(Cursor cursor) {
        TipoEvento tipoEvento = new TipoEvento();
        tipoEvento.setIdTipoEvento(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_TIPO_EVENTO)));
        tipoEvento.setNombreTipoEvento(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_TIPO_EVENTO)));
        tipoEvento.setDescripcionTipoEvento(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPCION_TIPO_EVENTO)));
        tipoEvento.setMontoMinimo(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_MONTO_MINIMO)));
        tipoEvento.setMontoMaximo(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_MONTO_MAXIMO)));
        tipoEvento.setActivoTipoEvento(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIVO_TIPOEVENTO)));
        return tipoEvento;
    }
}