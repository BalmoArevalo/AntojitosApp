package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito; // Ajusta el paquete si es necesario

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// Importar el POJO Credito
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.Credito;

public class CreditoDAO {

    private final SQLiteDatabase db;
    private static final String TAG = "CreditoDAO";

    // Constantes para la tabla y columnas (basado en DBHelper v4)
    private static final String TABLA_CREDITO = "CREDITO";
    private static final String COL_ID_CREDITO = "ID_CREDITO";
    private static final String COL_ID_FACTURA = "ID_FACTURA";
    private static final String COL_MONTO_AUTORIZADO = "MONTO_AUTORIZADO_CREDITO";
    private static final String COL_MONTO_PAGADO = "MONTO_PAGADO";
    private static final String COL_SALDO_PENDIENTE = "SALDO_PENDIENTE";
    private static final String COL_FECHA_LIMITE = "FECHA_LIMITE_PAGO";
    private static final String COL_ESTADO_CREDITO = "ESTADO_CREDITO";

    private static final String[] COLUMNAS_CREDITO = {
            COL_ID_CREDITO, COL_ID_FACTURA, COL_MONTO_AUTORIZADO,
            COL_MONTO_PAGADO, COL_SALDO_PENDIENTE, COL_FECHA_LIMITE, COL_ESTADO_CREDITO
    };

    // Constructor
    public CreditoDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Inserta un nuevo registro de crédito para una factura específica.
     * Se asume que la factura existe y está marcada como ES_CREDITO=1.
     * ID_CREDITO es autoincremental.
     * @param credito El objeto Credito a insertar (debe tener ID_FACTURA, MONTO_AUTORIZADO, etc.).
     * @return El ID_CREDITO generado si la inserción es exitosa, o -1 si ocurre un error (ej. ID_FACTURA duplicado).
     */
    public long insertar(Credito credito) {
        ContentValues values = new ContentValues();
        // ID_CREDITO es AUTOINCREMENT, no se incluye.
        values.put(COL_ID_FACTURA, credito.getIdFactura()); // UNIQUE constraint
        values.put(COL_MONTO_AUTORIZADO, credito.getMontoAutorizadoCredito());
        values.put(COL_MONTO_PAGADO, credito.getMontoPagado()); // Normalmente 0 al crear
        values.put(COL_SALDO_PENDIENTE, credito.getSaldoPendiente()); // Normalmente == Monto Autorizado al crear
        values.put(COL_FECHA_LIMITE, credito.getFechaLimitePago());
        values.put(COL_ESTADO_CREDITO, credito.getEstadoCredito()); // Normalmente "Activo" al crear

        try {
            long nuevoId = db.insertOrThrow(TABLA_CREDITO, null, values);
            Log.i(TAG, "Crédito insertado con ID: " + nuevoId + " para Factura ID: " + credito.getIdFactura());
            return nuevoId;
        } catch (SQLiteConstraintException e) {
            Log.e(TAG, "Error de constraint al insertar crédito para Factura ID " + credito.getIdFactura() + ": " + e.getMessage());
            // Causa más probable: Ya existe un crédito para esa ID_FACTURA (violación de UNIQUE)
            return -1;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error SQLite al insertar crédito: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Actualiza un registro de crédito existente (normalmente tras un pago).
     * Se identifica por ID_CREDITO.
     * Típicamente actualiza MONTO_PAGADO, SALDO_PENDIENTE, ESTADO_CREDITO.
     * @param credito El objeto Credito con los datos actualizados.
     * @return El número de filas afectadas (debería ser 1).
     */
    public int actualizar(Credito credito) {
        ContentValues values = new ContentValues();
        // Campos que típicamente se actualizan al registrar un pago o cambiar estado
        values.put(COL_MONTO_PAGADO, credito.getMontoPagado());
        values.put(COL_SALDO_PENDIENTE, credito.getSaldoPendiente());
        values.put(COL_FECHA_LIMITE, credito.getFechaLimitePago()); // Quizás editable
        values.put(COL_ESTADO_CREDITO, credito.getEstadoCredito());
        // ID_FACTURA y MONTO_AUTORIZADO no deberían cambiar después de creado el crédito
        // (excepto bajo reglas de negocio muy específicas manejadas en la capa de aplicación/ViewModel).

        String whereClause = COL_ID_CREDITO + " = ?";
        String[] whereArgs = {String.valueOf(credito.getIdCredito())};

        try {
            int rowsAffected = db.update(TABLA_CREDITO, values, whereClause, whereArgs);
            if (rowsAffected > 0) {
                Log.i(TAG, "Crédito actualizado con ID: " + credito.getIdCredito());
            } else {
                Log.w(TAG, "No se actualizó crédito con ID: " + credito.getIdCredito());
            }
            return rowsAffected;
        } catch (SQLiteException e) {
            // Podría fallar por CHECK constraints (ej: monto pagado > monto autorizado)
            Log.e(TAG, "Error al actualizar crédito ID " + credito.getIdCredito() + ": " + e.getMessage());
            return 0;
        }
    }

    /**
     * Consulta el crédito asociado a un ID_FACTURA específico.
     * Dado que ID_FACTURA es UNIQUE en CREDITO, esto devolverá 0 o 1 resultado.
     * @param idFactura El ID de la factura.
     * @return El objeto Credito, o null si no se encuentra.
     */
    public Credito consultarPorIdFactura(int idFactura) {
        Cursor cursor = null;
        Credito credito = null;
        try {
            cursor = db.query(TABLA_CREDITO, COLUMNAS_CREDITO,
                    COL_ID_FACTURA + " = ?", new String[]{String.valueOf(idFactura)},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                credito = cursorToCredito(cursor);
                Log.d(TAG, "Crédito encontrado para Factura ID: " + idFactura);
            } else {
                Log.d(TAG, "No se encontró crédito para Factura ID: " + idFactura);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al consultar crédito por ID de Factura " + idFactura, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return credito;
    }

    /**
     * Consulta un crédito por su ID_CREDITO (PK).
     * @param idCredito El ID del crédito a consultar.
     * @return El objeto Credito, o null si no se encuentra.
     */
    public Credito consultarPorId(int idCredito) {
        Cursor cursor = null;
        Credito credito = null;
        try {
            cursor = db.query(TABLA_CREDITO, COLUMNAS_CREDITO,
                    COL_ID_CREDITO + " = ?", new String[]{String.valueOf(idCredito)},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                credito = cursorToCredito(cursor);
                Log.d(TAG, "Crédito encontrado por ID: " + idCredito);
            } else {
                Log.d(TAG, "Crédito no encontrado por ID: " + idCredito);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al consultar crédito por ID " + idCredito, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return credito;
    }

    /**
     * Obtiene todos los registros de crédito de la base de datos.
     * @return Una lista con todos los créditos.
     */
    public List<Credito> obtenerTodos() {
        List<Credito> lista = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLA_CREDITO, COLUMNAS_CREDITO, null, null, null, null, COL_ID_CREDITO + " ASC");
            while (cursor != null && cursor.moveToNext()) {
                lista.add(cursorToCredito(cursor));
            }
            Log.d(TAG, "Se obtuvieron " + lista.size() + " créditos en total.");
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener todos los créditos", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return lista;
    }


    /**
     * Helper para convertir una fila del Cursor a un objeto Credito.
     * @param cursor Cursor posicionado en la fila deseada.
     * @return Objeto Credito poblado.
     */
    private Credito cursorToCredito(Cursor cursor) {
        Credito credito = new Credito();
        // Usar getColumnIndexOrThrow para mayor seguridad ante cambios de orden/nombre
        credito.setIdCredito(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_CREDITO)));
        credito.setIdFactura(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_FACTURA)));
        credito.setMontoAutorizadoCredito(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_MONTO_AUTORIZADO)));
        credito.setMontoPagado(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_MONTO_PAGADO)));
        credito.setSaldoPendiente(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_SALDO_PENDIENTE)));
        credito.setFechaLimitePago(cursor.getString(cursor.getColumnIndexOrThrow(COL_FECHA_LIMITE)));
        credito.setEstadoCredito(cursor.getString(cursor.getColumnIndexOrThrow(COL_ESTADO_CREDITO)));
        return credito;
    }

}