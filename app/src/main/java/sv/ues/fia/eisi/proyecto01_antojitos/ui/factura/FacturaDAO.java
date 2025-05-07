package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log; // Para logs

import java.util.ArrayList;
import java.util.List;
import java.util.Collections; // Para lista vacía

public class FacturaDAO {

    private final SQLiteDatabase db;
    private static final String TAG = "FacturaDAO";

    // Columnas de la tabla FACTURA (según DBHelper con relación 1:1)
    private static final String TABLA_FACTURA = "FACTURA";
    private static final String COL_ID_FACTURA = "ID_FACTURA";
    private static final String COL_ID_PEDIDO = "ID_PEDIDO";
    private static final String COL_FECHA_EMISION = "FECHA_EMISION";
    private static final String COL_MONTO_TOTAL = "MONTO_TOTAL";
    private static final String COL_TIPO_PAGO = "TIPO_PAGO";
    private static final String COL_PAGADO = "PAGADO";

    private String[] columnasFactura = {
            COL_ID_FACTURA, COL_ID_PEDIDO, COL_FECHA_EMISION,
            COL_MONTO_TOTAL, COL_TIPO_PAGO, COL_PAGADO
    };

    public FacturaDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // MÉTODO getNextIdFactura ELIMINADO (ID_FACTURA es AUTOINCREMENT)

    /**
     * Inserta una nueva factura en la base de datos.
     * ID_FACTURA es autoincremental.
     * @param factura El objeto Factura a insertar (debe tener ID_PEDIDO seteado).
     * @return El ID de la factura recién insertada (nuevo ID_FACTURA), o -1 si ocurrió un error.
     */
    public long insertar(Factura factura) {
        ContentValues values = new ContentValues();
        // ID_FACTURA no se incluye, es AUTOINCREMENT
        values.put(COL_ID_PEDIDO, factura.getIdPedido()); // ID_PEDIDO es NOT NULL UNIQUE
        values.put(COL_FECHA_EMISION, factura.getFechaEmision());
        values.put(COL_MONTO_TOTAL, factura.getMontoTotal());
        values.put(COL_TIPO_PAGO, factura.getTipoPago());
        values.put(COL_PAGADO, factura.getPagado());

        try {
            long nuevoId = db.insertOrThrow(TABLA_FACTURA, null, values);
            Log.d(TAG, "Factura insertada con ID: " + nuevoId + " para Pedido ID: " + factura.getIdPedido());
            return nuevoId;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error al insertar factura para Pedido ID " + factura.getIdPedido() + ": " + e.getMessage());
            // Podría ser por violación de la restricción UNIQUE en ID_PEDIDO
            return -1;
        }
    }

    /**
     * Consulta una factura específica por su ID_FACTURA (Clave Primaria).
     * @param idFactura ID de la Factura a consultar.
     * @return El objeto Factura encontrado, o null si no existe.
     */
    public Factura consultarPorId(int idFactura) {
        Factura factura = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLA_FACTURA,
                    columnasFactura,
                    COL_ID_FACTURA + " = ?", // Cláusula WHERE por ID_FACTURA
                    new String[]{String.valueOf(idFactura)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                factura = cursorToFactura(cursor);
                Log.d(TAG, "Factura encontrada por ID: " + idFactura);
            } else {
                Log.d(TAG, "Factura con ID: " + idFactura + " no encontrada.");
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error al consultar factura por ID " + idFactura + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return factura;
    }

    /**
     * Consulta la factura asociada a un ID_PEDIDO específico (Relación 1:1).
     * @param idPedido ID del Pedido.
     * @return El objeto Factura encontrado, o null si no existe una factura para ese pedido.
     */
    public Factura consultarPorIdPedido(int idPedido) {
        Factura factura = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLA_FACTURA,
                    columnasFactura,
                    COL_ID_PEDIDO + " = ?", // Cláusula WHERE por ID_PEDIDO
                    new String[]{String.valueOf(idPedido)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                factura = cursorToFactura(cursor);
                Log.d(TAG, "Factura encontrada para Pedido ID: " + idPedido);
            } else {
                Log.d(TAG, "No se encontró factura para Pedido ID: " + idPedido);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error al consultar factura por Pedido ID " + idPedido + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return factura;
    }


    /**
     * Obtiene la factura asociada a un pedido específico (si existe).
     * En una relación 1:1, esta lista contendrá 0 o 1 factura.
     * @param idPedido El ID del pedido.
     * @return Una lista de objetos Factura (0 o 1 elemento).
     */
    public List<Factura> obtenerPorPedido(int idPedido) {
        Factura factura = consultarPorIdPedido(idPedido);
        if (factura != null) {
            List<Factura> lista = new ArrayList<>();
            lista.add(factura);
            return lista;
        }
        return Collections.emptyList(); // Devuelve lista vacía si no se encuentra
    }


    /**
     * Actualiza una factura existente en la base de datos.
     * Se identifica por ID_FACTURA. ID_PEDIDO no se modifica.
     * @param factura El objeto Factura con los datos actualizados.
     * @return El número de filas afectadas (debería ser 1 si la actualización fue exitosa).
     */
    public int actualizar(Factura factura) {
        ContentValues values = new ContentValues();
        // ID_FACTURA es para el WHERE. ID_PEDIDO no se actualiza.
        values.put(COL_FECHA_EMISION, factura.getFechaEmision());
        values.put(COL_MONTO_TOTAL, factura.getMontoTotal());
        values.put(COL_TIPO_PAGO, factura.getTipoPago());
        values.put(COL_PAGADO, factura.getPagado());

        String whereClause = COL_ID_FACTURA + " = ?";
        String[] whereArgs = {String.valueOf(factura.getIdFactura())};

        try {
            int filasAfectadas = db.update(TABLA_FACTURA, values, whereClause, whereArgs);
            if (filasAfectadas > 0) {
                Log.d(TAG, "Factura actualizada con ID: " + factura.getIdFactura());
            } else {
                Log.w(TAG, "No se actualizó factura con ID: " + factura.getIdFactura() + " (quizás no existe)");
            }
            return filasAfectadas;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error al actualizar factura ID " + factura.getIdFactura() + ": " + e.getMessage());
            return 0;
        }
    }

    /**
     * Elimina una factura de la base de datos usando su ID_FACTURA.
     * @param idFactura ID de la Factura a eliminar.
     * @return El número de filas eliminadas.
     */
    public int eliminar(int idFactura) {
        String whereClause = COL_ID_FACTURA + " = ?";
        String[] whereArgs = {String.valueOf(idFactura)};

        try {
            // Considerar si se deben eliminar créditos asociados ANTES o si ON DELETE CASCADE está configurado en CREDITO.
            // Por simplicidad, no se maneja aquí. La FK de CREDITO a FACTURA podría tener ON DELETE CASCADE.
            // db.delete("CREDITO", "ID_FACTURA = ?", new String[]{String.valueOf(idFactura)});

            int filasAfectadas = db.delete(TABLA_FACTURA, whereClause, whereArgs);
            if (filasAfectadas > 0) {
                Log.d(TAG, "Factura eliminada con ID: " + idFactura);
            } else {
                Log.w(TAG, "No se eliminó factura con ID: " + idFactura + " (quizás no existía)");
            }
            return filasAfectadas;
        } catch (SQLiteException e) {
            // Un trigger (trg_evitar_borrar_pedido_con_factura en PEDIDO) no aplicaría aquí directamente,
            // pero la FK desde FACTURA a PEDIDO (ON DELETE RESTRICT por defecto) evitaría
            // que se borre un PEDIDO si esta FACTURA lo referencia.
            Log.e(TAG, "Error al eliminar factura ID " + idFactura + ": " + e.getMessage());
            return 0;
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
            cursor = db.query(TABLA_FACTURA, columnasFactura, null, null, null, null, COL_ID_FACTURA + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    lista.add(cursorToFactura(cursor));
                } while (cursor.moveToNext());
                Log.d(TAG, "Se obtuvieron " + lista.size() + " facturas en total.");
            } else {
                Log.d(TAG, "No se encontraron facturas en obtenerTodas().");
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error al obtener todas las facturas: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return lista;
    }

    /**
     * Helper para convertir un Cursor a un objeto Factura.
     */
    private Factura cursorToFactura(Cursor cursor) {
        Factura factura = new Factura();
        // Asume que Factura.java tiene los setters correspondientes
        factura.setIdFactura(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_FACTURA)));
        factura.setIdPedido(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_PEDIDO)));
        factura.setFechaEmision(cursor.getString(cursor.getColumnIndexOrThrow(COL_FECHA_EMISION)));
        factura.setMontoTotal(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_MONTO_TOTAL)));
        factura.setTipoPago(cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPO_PAGO)));
        factura.setPagado(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAGADO)));
        return factura;
    }
}