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
        values.put("ID_SUCURSAL", pedido.getIdSucursal()); // Nuevo campo
        values.put("FECHA_HORA_PEDIDO", pedido.getFechaHoraPedido());
        values.put("ESTADO_PEDIDO", pedido.getEstadoPedido());
        values.put("ACTIVO_PEDIDO", pedido.getActivoPedido()); // Nuevo campo

        return db.insert("PEDIDO", null, values);
    }

    public Pedido consultarPorId(int idPedido) {
        Cursor cursor = db.query("PEDIDO", null, "ID_PEDIDO = ?",
                new String[]{String.valueOf(idPedido)}, null, null, null);

        if (cursor.moveToFirst()) {
            Pedido pedido = new Pedido();
            pedido.setIdPedido(cursor.getInt(cursor.getColumnIndexOrThrow("ID_PEDIDO")));
            pedido.setIdCliente(cursor.getInt(cursor.getColumnIndexOrThrow("ID_CLIENTE")));
            pedido.setIdTipoEvento(cursor.getInt(cursor.getColumnIndexOrThrow("ID_TIPO_EVENTO")));
            pedido.setIdRepartidor(cursor.getInt(cursor.getColumnIndexOrThrow("ID_REPARTIDOR")));
            pedido.setIdSucursal(cursor.getInt(cursor.getColumnIndexOrThrow("ID_SUCURSAL")));
            pedido.setFechaHoraPedido(cursor.getString(cursor.getColumnIndexOrThrow("FECHA_HORA_PEDIDO")));
            pedido.setEstadoPedido(cursor.getString(cursor.getColumnIndexOrThrow("ESTADO_PEDIDO")));
            pedido.setActivoPedido(cursor.getInt(cursor.getColumnIndexOrThrow("ACTIVO_PEDIDO")));
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
        values.put("ID_SUCURSAL", pedido.getIdSucursal());
        values.put("FECHA_HORA_PEDIDO", pedido.getFechaHoraPedido());
        values.put("ESTADO_PEDIDO", pedido.getEstadoPedido());
        values.put("ACTIVO_PEDIDO", pedido.getActivoPedido());

        return db.update("PEDIDO", values, "ID_PEDIDO = ?",
                new String[]{String.valueOf(pedido.getIdPedido())});
    }

    /**
     * Elimina un pedido con validaciones:
     * - Si tiene FACTURA asociada → no se elimina (return 0)
     * - Si tiene DETALLEPEDIDO o REPARTOPEDIDO → se desactiva (return 1)
     * - Si no tiene relaciones → se elimina (return 2)
     */
    public int eliminar(int idPedido) {
        try {
            if (existeEnTabla("FACTURA", "ID_PEDIDO", idPedido)) {
                return 0;
            }

            boolean tieneDetalle = existeEnTabla("DETALLEPEDIDO", "ID_PEDIDO", idPedido);
            boolean tieneReparto = existeEnTabla("REPARTOPEDIDO", "ID_PEDIDO", idPedido);

            if (tieneDetalle || tieneReparto) {
                ContentValues values = new ContentValues();
                values.put("ACTIVO_PEDIDO", 0);
                db.update("PEDIDO", values, "ID_PEDIDO = ?", new String[]{String.valueOf(idPedido)});
                return 1;
            }

            int filas = db.delete("PEDIDO", "ID_PEDIDO = ?", new String[]{String.valueOf(idPedido)});
            return filas > 0 ? 2 : 0;

        } catch (Exception e) {
            Log.e("PedidoDAO", "Error al eliminar pedido con ID " + idPedido + ": " + e.getMessage());
            return -1;
        }
    }

    private boolean existeEnTabla(String tabla, String campo, int id) {
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + tabla + " WHERE " + campo + " = ?",
                new String[]{String.valueOf(id)}
        );
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }

    public List<Pedido> obtenerTodos() {
        List<Pedido> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM PEDIDO WHERE ACTIVO_PEDIDO = 1", null);

        if (cursor.moveToFirst()) {
            do {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(cursor.getInt(cursor.getColumnIndexOrThrow("ID_PEDIDO")));
                pedido.setIdCliente(cursor.getInt(cursor.getColumnIndexOrThrow("ID_CLIENTE")));
                pedido.setIdTipoEvento(cursor.getInt(cursor.getColumnIndexOrThrow("ID_TIPO_EVENTO")));
                pedido.setIdRepartidor(cursor.getInt(cursor.getColumnIndexOrThrow("ID_REPARTIDOR")));
                pedido.setIdSucursal(cursor.getInt(cursor.getColumnIndexOrThrow("ID_SUCURSAL")));
                pedido.setFechaHoraPedido(cursor.getString(cursor.getColumnIndexOrThrow("FECHA_HORA_PEDIDO")));
                pedido.setEstadoPedido(cursor.getString(cursor.getColumnIndexOrThrow("ESTADO_PEDIDO")));
                pedido.setActivoPedido(cursor.getInt(cursor.getColumnIndexOrThrow("ACTIVO_PEDIDO")));
                lista.add(pedido);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public List<Pedido> obtenerTodosIncluyendoInactivos() {
        List<Pedido> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM PEDIDO", null); // sin filtro de activo

        if (cursor.moveToFirst()) {
            do {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(cursor.getInt(cursor.getColumnIndexOrThrow("ID_PEDIDO")));
                pedido.setIdCliente(cursor.getInt(cursor.getColumnIndexOrThrow("ID_CLIENTE")));
                pedido.setIdTipoEvento(cursor.getInt(cursor.getColumnIndexOrThrow("ID_TIPO_EVENTO")));
                pedido.setIdRepartidor(cursor.getInt(cursor.getColumnIndexOrThrow("ID_REPARTIDOR")));
                pedido.setIdSucursal(cursor.getInt(cursor.getColumnIndexOrThrow("ID_SUCURSAL")));
                pedido.setFechaHoraPedido(cursor.getString(cursor.getColumnIndexOrThrow("FECHA_HORA_PEDIDO")));
                pedido.setEstadoPedido(cursor.getString(cursor.getColumnIndexOrThrow("ESTADO_PEDIDO")));
                pedido.setActivoPedido(cursor.getInt(cursor.getColumnIndexOrThrow("ACTIVO_PEDIDO")));
                lista.add(pedido);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }


}
