package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException; // Importante para manejo de errores

import java.util.ArrayList;
import java.util.List;

public class FacturaDAO {

    private final SQLiteDatabase db;

    // Constructor que recibe la instancia de la base de datos
    public FacturaDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Calcula el siguiente ID_FACTURA para un pedido dado
     * (MAX(ID_FACTURA) + 1 para ese ID_PEDIDO, o 1 si no hay ninguna).
     * La tabla FACTURA tiene clave primaria compuesta (ID_PEDIDO, ID_FACTURA).
     */
    public int getNextIdFactura(int idPedido) {
        int nextId = 1; // Valor inicial si no hay facturas para ese pedido
        Cursor cursor = null;
        try {
            // Consulta para obtener el máximo ID_FACTURA para el ID_PEDIDO específico
            cursor = db.rawQuery(
                    "SELECT MAX(ID_FACTURA) FROM FACTURA WHERE ID_PEDIDO = ?",
                    new String[]{ String.valueOf(idPedido) }
            );

            // Si el cursor se mueve al primer resultado y la columna no es nula
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                nextId = cursor.getInt(0) + 1; // El siguiente ID es el máximo actual + 1
            }
        } catch (SQLiteException e) {
            // Manejar la excepción (e.g., loggear el error)
            System.err.println("Error al obtener nextIdFactura: " + e.getMessage());
        } finally {
            // Asegurarse de cerrar el cursor para liberar recursos
            if (cursor != null) {
                cursor.close();
            }
        }
        return nextId;
    }

    /**
     * Inserta una nueva factura en la base de datos.
     * @param factura El objeto Factura a insertar.
     * @return El ID de la fila insertada, o -1 si ocurrió un error.
     */
    public long insertar(Factura factura) {
        ContentValues values = new ContentValues();
        // ID_FACTURA se asume calculado previamente con getNextIdFactura
        values.put("ID_PEDIDO", factura.getIdPedido());
        values.put("ID_FACTURA", factura.getIdFactura());
        values.put("FECHA_EMISION", factura.getFechaEmision());
        values.put("MONTO_TOTAL", factura.getMontoTotal());
        values.put("TIPO_PAGO", factura.getTipoPago());
        values.put("PAGADO", factura.getPagado()); // 0 o 1

        try {
            return db.insertOrThrow("FACTURA", null, values); // Usar insertOrThrow para detectar errores
        } catch (SQLiteException e) {
            System.err.println("Error al insertar factura: " + e.getMessage());
            return -1; // Indicar error
        }
    }

    /**
     * Consulta una factura específica por su clave primaria compuesta.
     * @param idPedido ID del Pedido.
     * @param idFactura ID de la Factura dentro del Pedido.
     * @return El objeto Factura encontrado, o null si no existe.
     */
    public Factura consultarPorId(int idPedido, int idFactura) {
        Factura factura = null;
        Cursor cursor = null;
        try {
            cursor = db.query("FACTURA",
                    new String[]{"ID_PEDIDO", "ID_FACTURA", "FECHA_EMISION", "MONTO_TOTAL", "TIPO_PAGO", "PAGADO"}, // Columnas a obtener
                    "ID_PEDIDO = ? AND ID_FACTURA = ?", // Cláusula WHERE
                    new String[]{String.valueOf(idPedido), String.valueOf(idFactura)}, // Argumentos del WHERE
                    null, null, null); // Sin GROUP BY, HAVING, ORDER BY

            if (cursor != null && cursor.moveToFirst()) {
                factura = new Factura();
                factura.setIdPedido(cursor.getInt(0));
                factura.setIdFactura(cursor.getInt(1));
                factura.setFechaEmision(cursor.getString(2));
                factura.setMontoTotal(cursor.getDouble(3));
                factura.setTipoPago(cursor.getString(4));
                factura.setPagado(cursor.getInt(5));
            }
        } catch (SQLiteException e) {
            System.err.println("Error al consultar factura por ID: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return factura;
    }

    /**
     * Obtiene todas las facturas asociadas a un pedido específico.
     * @param idPedido El ID del pedido.
     * @return Una lista de objetos Factura. La lista estará vacía si no hay facturas para ese pedido.
     */
    public List<Factura> obtenerPorPedido(int idPedido) {
        List<Factura> lista = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query("FACTURA",
                    new String[]{"ID_PEDIDO", "ID_FACTURA", "FECHA_EMISION", "MONTO_TOTAL", "TIPO_PAGO", "PAGADO"},
                    "ID_PEDIDO = ?",
                    new String[]{ String.valueOf(idPedido) },
                    null, null, "ID_FACTURA ASC"); // Ordenar por ID_FACTURA

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Factura f = new Factura(
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getString(2),
                            cursor.getDouble(3),
                            cursor.getString(4),
                            cursor.getInt(5)
                    );
                    lista.add(f);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            System.err.println("Error al obtener facturas por pedido: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return lista;
    }


    /**
     * Actualiza una factura existente en la base de datos.
     * @param factura El objeto Factura con los datos actualizados.
     * @return El número de filas afectadas (debería ser 1 si la actualización fue exitosa).
     */
    public int actualizar(Factura factura) {
        ContentValues values = new ContentValues();
        // No se actualizan las claves primarias (ID_PEDIDO, ID_FACTURA)
        values.put("FECHA_EMISION", factura.getFechaEmision());
        values.put("MONTO_TOTAL", factura.getMontoTotal());
        values.put("TIPO_PAGO", factura.getTipoPago());
        values.put("PAGADO", factura.getPagado());

        String whereClause = "ID_PEDIDO = ? AND ID_FACTURA = ?";
        String[] whereArgs = {
                String.valueOf(factura.getIdPedido()),
                String.valueOf(factura.getIdFactura())
        };

        try {
            return db.update("FACTURA", values, whereClause, whereArgs);
        } catch (SQLiteException e) {
            System.err.println("Error al actualizar factura: " + e.getMessage());
            return 0; // Indicar que no se afectaron filas debido al error
        }
    }

    /**
     * Elimina una factura de la base de datos usando su clave primaria compuesta.
     * @param idPedido ID del Pedido.
     * @param idFactura ID de la Factura.
     * @return El número de filas eliminadas (debería ser 1 si se encontró y eliminó).
     */
    public int eliminar(int idPedido, int idFactura) {
        String whereClause = "ID_PEDIDO = ? AND ID_FACTURA = ?";
        String[] whereArgs = {
                String.valueOf(idPedido),
                String.valueOf(idFactura)
        };

        try {
            // Antes de eliminar, podrías necesitar verificar/eliminar registros dependientes (ej. CREDITO)
            // si las restricciones ON DELETE no están configuradas o no son adecuadas.
            // db.delete("CREDITO", "ID_PEDIDO = ? AND ID_FACTURA = ?", whereArgs); // Ejemplo si fuera necesario

            return db.delete("FACTURA", whereClause, whereArgs);
        } catch (SQLiteException e) {
            System.err.println("Error al eliminar factura: " + e.getMessage());
            // Considera si una excepción aquí debería propagarse o manejarse devolviendo 0
            // Si la FK está activa, podría fallar si hay créditos asociados.
            return 0; // Indicar que no se eliminaron filas
        }
    }

    /**
     * Obtiene todas las facturas de la base de datos.
     * ¡Precaución! Esto puede devolver muchos datos si la tabla es grande.
     * @return Una lista con todas las facturas.
     */
    public List<Factura> obtenerTodas() {
        List<Factura> lista = new ArrayList<>();
        Cursor cursor = null;
        try {
            // Ordenar por pedido y luego por factura puede ser útil
            cursor = db.rawQuery("SELECT * FROM FACTURA ORDER BY ID_PEDIDO ASC, ID_FACTURA ASC", null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Factura f = new Factura(
                            cursor.getInt(cursor.getColumnIndexOrThrow("ID_PEDIDO")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("ID_FACTURA")),
                            cursor.getString(cursor.getColumnIndexOrThrow("FECHA_EMISION")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("MONTO_TOTAL")),
                            cursor.getString(cursor.getColumnIndexOrThrow("TIPO_PAGO")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("PAGADO"))
                    );
                    lista.add(f);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            System.err.println("Error al obtener todas las facturas: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Error si getColumnIndexOrThrow no encuentra la columna (inesperado si la query es *)
            System.err.println("Error de columna al obtener todas las facturas: " + e.getMessage());
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return lista;
    }
}