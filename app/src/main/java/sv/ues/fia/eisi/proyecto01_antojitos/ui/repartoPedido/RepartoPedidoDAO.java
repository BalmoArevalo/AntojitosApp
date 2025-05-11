package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RepartoPedidoDAO {
    private final SQLiteDatabase db;

    public RepartoPedidoDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertar(RepartoPedido reparto) {
        ContentValues values = new ContentValues();
        values.put("ID_PEDIDO", reparto.getIdPedido());
        values.put("ID_REPARTO_PEDIDO", reparto.getIdRepartoPedido());
        values.put("HORA_ASIGNACION", reparto.getHoraAsignacion());
        values.put("UBICACION_ENTREGA", reparto.getUbicacionEntrega());
        values.put("FECHA_HORA_ENTREGA", reparto.getFechaHoraEntrega());
        return db.insert("REPARTOPEDIDO", null, values);
    }

    public int actualizar(RepartoPedido reparto) {
        ContentValues values = new ContentValues();
        values.put("HORA_ASIGNACION", reparto.getHoraAsignacion());
        values.put("UBICACION_ENTREGA", reparto.getUbicacionEntrega());
        values.put("FECHA_HORA_ENTREGA", reparto.getFechaHoraEntrega());
        return db.update("REPARTOPEDIDO", values,
                "ID_PEDIDO = ? AND ID_REPARTO_PEDIDO = ?",
                new String[]{String.valueOf(reparto.getIdPedido()), String.valueOf(reparto.getIdRepartoPedido())});
    }

    public int eliminar(int idPedido, int idRepartoPedido) {
        return db.delete("REPARTOPEDIDO",
                "ID_PEDIDO = ? AND ID_REPARTO_PEDIDO = ?",
                new String[]{String.valueOf(idPedido), String.valueOf(idRepartoPedido)});
    }

    public RepartoPedido obtenerUno(int idPedido, int idRepartoPedido) {
        Cursor c = db.query("REPARTOPEDIDO", null,
                "ID_PEDIDO = ? AND ID_REPARTO_PEDIDO = ?",
                new String[]{String.valueOf(idPedido), String.valueOf(idRepartoPedido)},
                null, null, null);
        if (c.moveToFirst()) {
            RepartoPedido r = cursorToReparto(c);
            c.close();
            return r;
        }
        c.close();
        return null;
    }

    public List<RepartoPedido> obtenerTodos() {
        List<RepartoPedido> lista = new ArrayList<>();
        Cursor c = db.query("REPARTOPEDIDO", null, null, null, null, null,
                "ID_PEDIDO ASC, ID_REPARTO_PEDIDO ASC");
        if (c.moveToFirst()) {
            do {
                lista.add(cursorToReparto(c));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    private RepartoPedido cursorToReparto(Cursor c) {
        RepartoPedido r = new RepartoPedido();
        r.setIdPedido(c.getInt(c.getColumnIndexOrThrow("ID_PEDIDO")));
        r.setIdRepartoPedido(c.getInt(c.getColumnIndexOrThrow("ID_REPARTO_PEDIDO")));
        r.setHoraAsignacion(c.getString(c.getColumnIndexOrThrow("HORA_ASIGNACION")));
        r.setUbicacionEntrega(c.getString(c.getColumnIndexOrThrow("UBICACION_ENTREGA")));
        r.setFechaHoraEntrega(c.getString(c.getColumnIndexOrThrow("FECHA_HORA_ENTREGA")));
        return r;
    }
}
