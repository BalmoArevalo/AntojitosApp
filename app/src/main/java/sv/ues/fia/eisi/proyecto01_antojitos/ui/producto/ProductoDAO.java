package sv.ues.fia.eisi.proyecto01_antojitos.ui.producto;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.Producto;

public class ProductoDAO {

    private SQLiteDatabase db;

    public ProductoDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public List<Producto> obtenerTodosLosProductos() {
        List<Producto> listaProductos = new ArrayList<>();

        Cursor cursor = db.query(
                "PRODUCTO",
                null,
                "ACTIVO_PRODUCTO = 1",
                null,
                null,
                null,
                "NOMBRE_PRODUCTO ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                Producto producto = new Producto();
                producto.setIdProducto(cursor.getInt(cursor.getColumnIndexOrThrow("ID_PRODUCTO")));
                producto.setIdCategoriaProducto(cursor.getInt(cursor.getColumnIndexOrThrow("ID_CATEGORIAPRODUCTO")));
                producto.setNombreProducto(cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_PRODUCTO")));
                producto.setDescripcionProducto(cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPCION_PRODUCTO")));
                producto.setActivoProducto(cursor.getInt(cursor.getColumnIndexOrThrow("ACTIVO_PRODUCTO")));

                listaProductos.add(producto);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return listaProductos;
    }

    public Producto obtenerProductoPorId(int id) {
        Producto producto = null;

        Cursor cursor = db.query(
                "PRODUCTO",
                null,
                "ID_PRODUCTO = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            producto = new Producto();
            producto.setIdProducto(cursor.getInt(cursor.getColumnIndexOrThrow("ID_PRODUCTO")));
            producto.setIdCategoriaProducto(cursor.getInt(cursor.getColumnIndexOrThrow("ID_CATEGORIAPRODUCTO")));
            producto.setNombreProducto(cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_PRODUCTO")));
            producto.setDescripcionProducto(cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPCION_PRODUCTO")));
            producto.setActivoProducto(cursor.getInt(cursor.getColumnIndexOrThrow("ACTIVO_PRODUCTO")));
        }

        cursor.close();
        return producto;
    }

    public long insertarProducto(Producto producto) {
        ContentValues values = new ContentValues();
        values.put("ID_CATEGORIAPRODUCTO", producto.getIdCategoriaProducto());
        values.put("NOMBRE_PRODUCTO", producto.getNombreProducto());
        values.put("DESCRIPCION_PRODUCTO", producto.getDescripcionProducto());
        values.put("ACTIVO_PRODUCTO", 1);

        return db.insert("PRODUCTO", null, values);
    }

    public int actualizarProducto(Producto producto) {
        ContentValues values = new ContentValues();
        values.put("ID_CATEGORIAPRODUCTO", producto.getIdCategoriaProducto());
        values.put("NOMBRE_PRODUCTO", producto.getNombreProducto());
        values.put("DESCRIPCION_PRODUCTO", producto.getDescripcionProducto());

        return db.update(
                "PRODUCTO",
                values,
                "ID_PRODUCTO = ?",
                new String[]{String.valueOf(producto.getIdProducto())}
        );
    }

    public int eliminarProducto(int idProducto) {
        ContentValues values = new ContentValues();
        values.put("ACTIVO_PRODUCTO", 0);

        return db.update(
                "PRODUCTO",
                values,
                "ID_PRODUCTO = ?",
                new String[]{String.valueOf(idProducto)}
        );
    }
}