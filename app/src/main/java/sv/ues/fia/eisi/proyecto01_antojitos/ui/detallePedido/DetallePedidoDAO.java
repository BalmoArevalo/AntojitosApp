package sv.ues.fia.eisi.proyecto01_antojitos.ui.detallePedido;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DetallePedidoDAO {

    private final SQLiteDatabase db;

    public DetallePedidoDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /** Calcula el precio desde DATOSPRODUCTO y guarda el detalle */
    public long insertar(DetallePedido detalle) {
        double precioUnitario = obtenerPrecioProducto(detalle.getIdProducto(), detalle.getIdPedido());
        if (precioUnitario <= 0) return -1;

        double subtotal = precioUnitario * detalle.getCantidad();

        ContentValues values = new ContentValues();
        values.put("ID_PRODUCTO", detalle.getIdProducto());
        values.put("ID_PEDIDO", detalle.getIdPedido());
        values.put("CANTIDAD", detalle.getCantidad());
        values.put("SUBTOTAL", subtotal);

        return db.insert("DETALLEPEDIDO", null, values);
    }

    private double obtenerPrecioProducto(int idProducto, int idPedido) {
        Cursor cursor = db.rawQuery(
                "SELECT P.ID_SUCURSAL FROM PEDIDO P WHERE P.ID_PEDIDO = ?",
                new String[]{String.valueOf(idPedido)}
        );
        int idSucursal = -1;
        if (cursor.moveToFirst()) {
            idSucursal = cursor.getInt(0);
        }
        cursor.close();
        if (idSucursal == -1) return -1;

        cursor = db.rawQuery(
                "SELECT PRECIO_SUCURSAL_PRODUCTO FROM DATOSPRODUCTO WHERE ID_SUCURSAL = ? AND ID_PRODUCTO = ?",
                new String[]{String.valueOf(idSucursal), String.valueOf(idProducto)}
        );
        double precio = -1;
        if (cursor.moveToFirst()) {
            precio = cursor.getDouble(0);
        }
        cursor.close();
        return precio;
    }

    public List<DetallePedido> obtenerPorPedido(int idPedido) {
        List<DetallePedido> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM DETALLEPEDIDO WHERE ID_PEDIDO = ?",
                new String[]{String.valueOf(idPedido)});
        if (cursor.moveToFirst()) {
            do {
                DetallePedido d = new DetallePedido();
                d.setIdDetallePedido(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DETALLE_PEDIDO")));
                d.setIdProducto(cursor.getInt(cursor.getColumnIndexOrThrow("ID_PRODUCTO")));
                d.setIdPedido(cursor.getInt(cursor.getColumnIndexOrThrow("ID_PEDIDO")));
                d.setCantidad(cursor.getInt(cursor.getColumnIndexOrThrow("CANTIDAD")));
                d.setSubtotal(cursor.getDouble(cursor.getColumnIndexOrThrow("SUBTOTAL")));
                lista.add(d);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public int eliminar(int idDetallePedido) {
        return db.delete("DETALLEPEDIDO", "ID_DETALLE_PEDIDO = ?", new String[]{String.valueOf(idDetallePedido)});
    }

    public DetallePedido consultarPorId(int idDetallePedido) {
        Cursor cursor = db.rawQuery("SELECT * FROM DETALLEPEDIDO WHERE ID_DETALLE_PEDIDO = ?",
                new String[]{String.valueOf(idDetallePedido)});
        DetallePedido d = null;
        if (cursor.moveToFirst()) {
            d = new DetallePedido();
            d.setIdDetallePedido(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DETALLE_PEDIDO")));
            d.setIdProducto(cursor.getInt(cursor.getColumnIndexOrThrow("ID_PRODUCTO")));
            d.setIdPedido(cursor.getInt(cursor.getColumnIndexOrThrow("ID_PEDIDO")));
            d.setCantidad(cursor.getInt(cursor.getColumnIndexOrThrow("CANTIDAD")));
            d.setSubtotal(cursor.getDouble(cursor.getColumnIndexOrThrow("SUBTOTAL")));
        }
        cursor.close();
        return d;
    }

    public int actualizar(DetallePedido detalle) {
        ContentValues values = new ContentValues();
        values.put("ID_PRODUCTO", detalle.getIdProducto());
        values.put("ID_PEDIDO", detalle.getIdPedido());
        values.put("CANTIDAD", detalle.getCantidad());
        values.put("SUBTOTAL", detalle.getSubtotal());

        return db.update(
                "DETALLEPEDIDO",
                values,
                "ID_DETALLE_PEDIDO = ?",
                new String[]{String.valueOf(detalle.getIdDetallePedido())}
        );
    }
}
