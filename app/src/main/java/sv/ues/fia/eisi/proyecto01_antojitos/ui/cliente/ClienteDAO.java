package sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private final SQLiteDatabase db;
    private static final String TABLA_CLIENTE = "CLIENTE";

    // Nombres de columnas actualizados
    private static final String[] COLUMNAS = {
            "ID_CLIENTE",
            "TELEFONO_CLIENTE",
            "NOMBRE_CLIENTE",
            "APELLIDO_CLIENTE",
            "ACTIVO_CLIENTE"
    };

    public ClienteDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Inserta un nuevo cliente en la base de datos
     */
    public long insertar(Cliente cliente) {
        try {
            ContentValues valores = new ContentValues();

            valores.put("TELEFONO_CLIENTE", cliente.getTelefonoCliente());
            valores.put("NOMBRE_CLIENTE", cliente.getNombreCliente());
            valores.put("APELLIDO_CLIENTE", cliente.getApellidoCliente());
            valores.put("ACTIVO_CLIENTE", 1); // Por defecto activo

            return db.insertOrThrow(TABLA_CLIENTE, null, valores);
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al insertar cliente: " + e.getMessage());
        }
    }

    /**
     * Consulta un cliente por su ID
     */
    public Cliente consultarPorId(int idCliente) {
        Cliente cliente = null;
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLA_CLIENTE,
                    COLUMNAS,
                    "ID_CLIENTE = ? AND ACTIVO_CLIENTE = 1",
                    new String[]{String.valueOf(idCliente)},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                cliente = cursorACliente(cursor);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar cliente: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return cliente;
    }

    /**
     * Actualiza los datos de un cliente existente
     */
    public int actualizar(Cliente cliente) {
        try {
            ContentValues valores = new ContentValues();
            valores.put("TELEFONO_CLIENTE", cliente.getTelefonoCliente());
            valores.put("NOMBRE_CLIENTE", cliente.getNombreCliente());
            valores.put("APELLIDO_CLIENTE", cliente.getApellidoCliente());

            return db.update(
                    TABLA_CLIENTE,
                    valores,
                    "ID_CLIENTE = ? AND ACTIVO_CLIENTE = 1",
                    new String[]{String.valueOf(cliente.getIdCliente())}
            );
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al actualizar cliente: " + e.getMessage());
        }
    }

    /**
     * Elimina lógicamente un cliente (soft delete)
     */
    public int eliminar(int idCliente) {
        try {
            ContentValues valores = new ContentValues();
            valores.put("ACTIVO_CLIENTE", 0);

            return db.update(
                    TABLA_CLIENTE,
                    valores,
                    "ID_CLIENTE = ? AND ACTIVO_CLIENTE = 1",
                    new String[]{String.valueOf(idCliente)}
            );
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al eliminar cliente: " + e.getMessage());
        }
    }

    /**
     * Convierte un cursor a un objeto Cliente
     */
    private Cliente cursorACliente(Cursor cursor) {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(cursor.getInt(cursor.getColumnIndexOrThrow("ID_CLIENTE")));
        cliente.setTelefonoCliente(cursor.getString(cursor.getColumnIndexOrThrow("TELEFONO_CLIENTE")));
        cliente.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_CLIENTE")));
        cliente.setApellidoCliente(cursor.getString(cursor.getColumnIndexOrThrow("APELLIDO_CLIENTE")));
        cliente.setActivoCliente(cursor.getInt(cursor.getColumnIndexOrThrow("ACTIVO_CLIENTE")));
        return cliente;
    }

    /**
     * Busca clientes por nombre o teléfono
     */
    public List<Cliente> buscarPorNombreOTelefono(String filtro) {
        List<Cliente> clientes = new ArrayList<>();
        Cursor cursor = null;
        String busqueda = "%" + filtro + "%";

        try {
            cursor = db.query(
                    TABLA_CLIENTE,
                    COLUMNAS,
                    "(NOMBRE_CLIENTE LIKE ? OR APELLIDO_CLIENTE LIKE ? OR TELEFONO_CLIENTE LIKE ?) AND ACTIVO_CLIENTE = 1",
                    new String[]{busqueda, busqueda, busqueda},
                    null,
                    null,
                    "NOMBRE_CLIENTE ASC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Cliente cliente = cursorACliente(cursor);
                    clientes.add(cliente);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return clientes;
    }

    /**
     * Obtiene todos los clientes activos
     * @return Lista de todos los clientes activos
     */
    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLA_CLIENTE,
                    COLUMNAS,
                    "ACTIVO_CLIENTE = 1",
                    null,
                    null,
                    null,
                    "NOMBRE_CLIENTE ASC" // Ordenar por nombre
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Cliente cliente = cursorACliente(cursor);
                    clientes.add(cliente);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la lista de clientes: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return clientes;
    }
}