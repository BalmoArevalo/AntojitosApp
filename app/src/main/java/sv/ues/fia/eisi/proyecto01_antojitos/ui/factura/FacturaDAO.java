package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException; // Específico para constraint errors
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Importar el POJO que acabamos de definir
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.Factura;

public class FacturaDAO {

    private final SQLiteDatabase db;
    private static final String TAG = "FacturaDAO";

    // Constantes para la tabla y columnas (basado en DBHelper v4)
    private static final String TABLA_FACTURA = "FACTURA";
    private static final String COL_ID_FACTURA = "ID_FACTURA";
    private static final String COL_ID_PEDIDO = "ID_PEDIDO";
    private static final String COL_FECHA_EMISION = "FECHA_EMISION";
    private static final String COL_MONTO_TOTAL = "MONTO_TOTAL";
    private static final String COL_TIPO_PAGO = "TIPO_PAGO";
    private static final String COL_ESTADO_FACTURA = "ESTADO_FACTURA";
    private static final String COL_ES_CREDITO = "ES_CREDITO";

    // Array con todas las columnas para usar en las consultas
    private static final String[] COLUMNAS_FACTURA = {
            COL_ID_FACTURA, COL_ID_PEDIDO, COL_FECHA_EMISION,
            COL_MONTO_TOTAL, COL_TIPO_PAGO, COL_ESTADO_FACTURA, COL_ES_CREDITO
    };

    // Constructor
    public FacturaDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Inserta una nueva factura en la base de datos.
     * ID_FACTURA es autoincremental y será asignado por la BD.
     * @param factura El objeto Factura a insertar (debe tener ID_PEDIDO y los demás campos seteados).
     * @return El ID_FACTURA generado si la inserción es exitosa, o -1 si ocurre un error (ej. ID_PEDIDO duplicado).
     */
    public long insertar(Factura factura) {
        ContentValues values = new ContentValues();
        // ID_FACTURA es AUTOINCREMENT, no se incluye aquí.
        values.put(COL_ID_PEDIDO, factura.getIdPedido()); // Campo UNIQUE, puede lanzar excepción
        values.put(COL_FECHA_EMISION, factura.getFechaEmision());
        values.put(COL_MONTO_TOTAL, factura.getMontoTotal());
        values.put(COL_TIPO_PAGO, factura.getTipoPago());
        values.put(COL_ESTADO_FACTURA, factura.getEstadoFactura()); // Usar nuevo campo
        values.put(COL_ES_CREDITO, factura.getEsCredito());         // Usar nuevo campo

        try {
            // Usar insertOrThrow para que falle si hay violación de constraint (ej: UNIQUE de ID_PEDIDO)
            long nuevoId = db.insertOrThrow(TABLA_FACTURA, null, values);
            Log.i(TAG, "Factura insertada con ID: " + nuevoId + " para Pedido ID: " + factura.getIdPedido());
            return nuevoId;
        } catch (SQLiteConstraintException e) {
            Log.e(TAG, "Error de constraint al insertar factura para Pedido ID " + factura.getIdPedido() + ". ¿Pedido ya tiene factura?", e);
            return -1; // Falla por restricción (probablemente UNIQUE)
        } catch (SQLiteException e) {
            Log.e(TAG, "Error SQLite al insertar factura para Pedido ID " + factura.getIdPedido(), e);
            return -1; // Otro error
        }
    }

    /**
     * Actualiza una factura existente, identificada por su ID_FACTURA.
     * No permite cambiar el ID_PEDIDO asociado.
     * La lógica de negocio para permitir o no la edición de MONTO_TOTAL debe ir en la capa superior (ViewModel/Activity).
     * @param factura El objeto Factura con los datos actualizados (debe incluir ID_FACTURA).
     * @return El número de filas afectadas (debería ser 1 si la actualización fue exitosa).
     */
    public int actualizar(Factura factura) {
        ContentValues values = new ContentValues();
        // ID_FACTURA se usa en WHERE. ID_PEDIDO no se actualiza.
        values.put(COL_FECHA_EMISION, factura.getFechaEmision());
        values.put(COL_MONTO_TOTAL, factura.getMontoTotal());
        values.put(COL_TIPO_PAGO, factura.getTipoPago());
        values.put(COL_ESTADO_FACTURA, factura.getEstadoFactura());
        values.put(COL_ES_CREDITO, factura.getEsCredito());

        String whereClause = COL_ID_FACTURA + " = ?";
        String[] whereArgs = {String.valueOf(factura.getIdFactura())};

        try {
            int rowsAffected = db.update(TABLA_FACTURA, values, whereClause, whereArgs);
            if (rowsAffected > 0) {
                Log.i(TAG, "Factura actualizada con ID: " + factura.getIdFactura());
            } else {
                Log.w(TAG, "No se actualizó factura con ID: " + factura.getIdFactura() + " (quizás no existe)");
            }
            return rowsAffected;
        } catch (SQLiteException e) {
            // Podría fallar por CHECK constraints si se intenta poner monto <= 0, etc.
            Log.e(TAG, "Error al actualizar factura ID " + factura.getIdFactura(), e);
            return 0;
        }
    }

    /**
     * Elimina una factura por su ID_FACTURA.
     * ¡Precaución! Fallará si existe un CREDITO asociado debido a ON DELETE RESTRICT.
     * Considera eliminar el crédito asociado primero o cambiar la política de FK en CREDITO.
     * @param idFactura ID de la Factura a eliminar.
     * @return El número de filas eliminadas (0 o 1).
     */
    public int eliminar(int idFactura) {
        String whereClause = COL_ID_FACTURA + " = ?";
        String[] whereArgs = {String.valueOf(idFactura)};

        try {
            // Si se elimina la factura, ¿qué pasa con el pedido? La relación 1:1 implica
            // que el pedido quedaría sin factura. No hay problema de FK desde Pedido.
            int rowsAffected = db.delete(TABLA_FACTURA, whereClause, whereArgs);
            if (rowsAffected > 0) {
                Log.i(TAG, "Factura eliminada con ID: " + idFactura);
            } else {
                Log.w(TAG, "No se eliminó factura con ID: " + idFactura + " (quizás no existía)");
            }
            return rowsAffected;
        } catch (SQLiteConstraintException e){
            Log.e(TAG, "Error de constraint al eliminar factura ID " + idFactura + ". ¿Tiene un crédito asociado?", e);
            return 0;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error SQLite al eliminar factura ID " + idFactura, e);
            return 0;
        }
    }

    /**
     * Consulta una factura específica por su ID_FACTURA (Clave Primaria).
     * @param idFactura ID de la Factura a consultar.
     * @return El objeto Factura encontrado, o null si no existe.
     */
    public Factura consultarPorId(int idFactura) {
        Cursor cursor = null;
        try {
            cursor = db.query(TABLA_FACTURA,
                    COLUMNAS_FACTURA,
                    COL_ID_FACTURA + " = ?",
                    new String[]{String.valueOf(idFactura)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                Log.d(TAG, "Factura encontrada por ID: " + idFactura);
                return cursorToFactura(cursor); // Usar helper
            } else {
                Log.d(TAG, "Factura con ID: " + idFactura + " no encontrada.");
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al consultar factura por ID " + idFactura, e);
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    /**
     * Consulta la factura asociada a un ID_PEDIDO específico (Relación 1:1).
     * @param idPedido ID del Pedido.
     * @return El objeto Factura encontrado, o null si no existe factura para ese pedido.
     */
    public Factura consultarPorIdPedido(int idPedido) {
        Cursor cursor = null;
        try {
            cursor = db.query(TABLA_FACTURA,
                    COLUMNAS_FACTURA,
                    COL_ID_PEDIDO + " = ?", // WHERE por ID_PEDIDO
                    new String[]{String.valueOf(idPedido)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                Factura f = cursorToFactura(cursor);
                Log.d(TAG, "Factura encontrada para Pedido ID: " + idPedido + " (Factura ID: " + f.getIdFactura() + ")");
                return f;
            } else {
                Log.d(TAG, "No se encontró factura para Pedido ID: " + idPedido);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al consultar factura por Pedido ID " + idPedido, e);
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    /**
     * Obtiene todas las facturas de la base de datos.
     * @return Una lista con todas las facturas.
     */
    public List<Factura> obtenerTodas() {
        List<Factura> lista = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLA_FACTURA, COLUMNAS_FACTURA, null, null, null, null, COL_ID_FACTURA + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    lista.add(cursorToFactura(cursor));
                } while (cursor.moveToNext());
                Log.d(TAG, "Se obtuvieron " + lista.size() + " facturas en total.");
            } else {
                Log.d(TAG, "No se encontraron facturas en obtenerTodas().");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener todas las facturas", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return lista;
    }
    /**
     * Calcula la suma de los subtotales de DETALLEPEDIDO para un pedido específico.
     * @param idPedido El ID del pedido.
     * @return La suma de los subtotales, o 0.0 si no hay detalles o ocurre un error.
     */
    public double getSumSubtotalForPedido(int idPedido) {
        double sumaSubtotales = 0.0;
        String query = "SELECT SUM(SUBTOTAL) FROM DETALLEPEDIDO WHERE ID_PEDIDO = ?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(idPedido)});
            if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
                sumaSubtotales = cursor.getDouble(0);
                Log.d(TAG, "Suma de subtotales para Pedido ID " + idPedido + ": " + sumaSubtotales);
            } else {
                Log.w(TAG, "No se encontraron detalles o suma es NULL para Pedido ID: " + idPedido);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error calculando suma de subtotales para Pedido ID " + idPedido, e);
            sumaSubtotales = 0.0; // Retornar 0 en caso de error
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return sumaSubtotales;
    }
    /**
     * Helper para convertir una fila del Cursor a un objeto Factura.
     * Asume que el cursor contiene todas las columnas definidas en COLUMNAS_FACTURA.
     * @param cursor Cursor posicionado en la fila deseada.
     * @return Objeto Factura poblado.
     */
    private Factura cursorToFactura(Cursor cursor) {
        // El POJO Factura.java debe tener estos campos y sus setters
        Factura factura = new Factura();
        factura.setIdFactura(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_FACTURA)));
        factura.setIdPedido(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_PEDIDO)));
        factura.setFechaEmision(cursor.getString(cursor.getColumnIndexOrThrow(COL_FECHA_EMISION)));
        factura.setMontoTotal(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_MONTO_TOTAL)));
        factura.setTipoPago(cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPO_PAGO)));
        factura.setEstadoFactura(cursor.getString(cursor.getColumnIndexOrThrow(COL_ESTADO_FACTURA)));
        factura.setEsCredito(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ES_CREDITO)));
        return factura;
    }
}