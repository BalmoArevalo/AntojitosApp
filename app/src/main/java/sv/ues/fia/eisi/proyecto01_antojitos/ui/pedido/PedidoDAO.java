package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    private final SQLiteDatabase db;

    public PedidoDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertar(Pedido pedido) {
        ContentValues values = new ContentValues();
        values.put("ID_CLIENTE", pedido.getIdCliente());
        values.put("ID_TIPO_EVENTO", pedido.getIdTipoEvento());
        values.put("ID_REPARTIDOR", pedido.getIdRepartidor());
        values.put("FECHA_HORA_PEDIDO", pedido.getFechaHoraPedido());
        values.put("ESTADO_PEDIDO", pedido.getEstadoPedido());

        return db.insert("PEDIDO", null, values);
    }

    public Pedido consultarPorId(int idPedido) {
        Cursor cursor = db.query("PEDIDO", null, "ID_PEDIDO = ?",
                new String[]{String.valueOf(idPedido)}, null, null, null);

        if (cursor.moveToFirst()) {
            Pedido pedido = new Pedido();
            pedido.setIdPedido(cursor.getInt(0));
            pedido.setIdCliente(cursor.getInt(1));
            pedido.setIdTipoEvento(cursor.getInt(2));
            pedido.setIdRepartidor(cursor.getInt(3));
            pedido.setFechaHoraPedido(cursor.getString(4));
            pedido.setEstadoPedido(cursor.getString(5));
            cursor.close();
            return pedido;
        }
        cursor.close();
        return null;
    }

    public int actualizar(Pedido pedido) {
        ContentValues values = new ContentValues();
        values.put("ID_CLIENTE", pedido.getIdCliente());
        values.put("ID_TIPO_EVENTO", pedido.getIdTipoEvento());
        values.put("ID_REPARTIDOR", pedido.getIdRepartidor());
        values.put("FECHA_HORA_PEDIDO", pedido.getFechaHoraPedido());
        values.put("ESTADO_PEDIDO", pedido.getEstadoPedido());

        return db.update("PEDIDO", values, "ID_PEDIDO = ?",
                new String[]{String.valueOf(pedido.getIdPedido())});
    }

    public int eliminar(int idPedido) {
        try {
            return db.delete("PEDIDO", "ID_PEDIDO = ?",
                    new String[]{String.valueOf(idPedido)});
        } catch (Exception e) {
            Log.e("PedidoDAO", "Error al eliminar pedido con ID " + idPedido + ": " + e.getMessage());
            return -1;
        }
    }

    public List<Pedido> obtenerTodos() {
        List<Pedido> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM PEDIDO", null);

        if (cursor.moveToFirst()) {
            do {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(cursor.getInt(0));
                pedido.setIdCliente(cursor.getInt(1));
                pedido.setIdTipoEvento(cursor.getInt(2));
                pedido.setIdRepartidor(cursor.getInt(3));
                pedido.setFechaHoraPedido(cursor.getString(4));
                pedido.setEstadoPedido(cursor.getString(5));
                lista.add(pedido);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}
