package sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private final SQLiteDatabase db;

    public ClienteDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertar(Cliente cliente) {
        ContentValues values = new ContentValues();
        values.put("ID_CLIENTE", cliente.getIdCliente());
        values.put("ID_USUARIO", cliente.getIdUsuario());
        values.put("TELEFONO_CLIENTE", cliente.getTelefonoCliente());
        values.put("NOMBRE_CLIENTE", cliente.getNombreCliente());
        values.put("APELLIDO_CLIIENTE", cliente.getApellidoCliente());

        return db.insert("CLIENTE", null, values);
    }

    public Cliente consultarPorId(int idCliente) {
        Cursor cursor = db.query("CLIENTE", null, "ID_CLIENTE = ?",
                new String[]{String.valueOf(idCliente)}, null, null, null);

        if (cursor.moveToFirst()) {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(cursor.getInt(0));
            cliente.setIdUsuario(cursor.getInt(1));
            cliente.setTelefonoCliente(cursor.getString(2));
            cliente.setNombreCliente(cursor.getString(3));
            cliente.setApellidoCliente(cursor.getString(4));
            cursor.close();
            return cliente;
        }
        cursor.close();
        return null;
    }

    public int actualizar(Cliente cliente) {
        ContentValues values = new ContentValues();
        values.put("ID_USUARIO", cliente.getIdUsuario());
        values.put("TELEFONO_CLIENTE", cliente.getTelefonoCliente());
        values.put("NOMBRE_CLIENTE", cliente.getNombreCliente());
        values.put("APELLIDO_CLIIENTE", cliente.getApellidoCliente());

        return db.update("CLIENTE", values, "ID_CLIENTE = ?",
                new String[]{String.valueOf(cliente.getIdCliente())});
    }

    public int eliminar(int idCliente) {
        return db.delete("CLIENTE", "ID_CLIENTE = ?",
                new String[]{String.valueOf(idCliente)});
    }

    public List<Cliente> obtenerTodos() {
        List<Cliente> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM CLIENTE", null);

        if (cursor.moveToFirst()) {
            do {
                Cliente cliente = new Cliente();
                cliente.setIdCliente(cursor.getInt(0));
                cliente.setIdUsuario(cursor.getInt(1));
                cliente.setTelefonoCliente(cursor.getString(2));
                cliente.setNombreCliente(cursor.getString(3));
                cliente.setApellidoCliente(cursor.getString(4));
                lista.add(cliente);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}
