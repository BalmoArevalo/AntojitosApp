package sv.ues.fia.eisi.proyecto01_antojitos.ui.categoriaProducto;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class CategoriaProductoDAO {
    private final SQLiteDatabase db;

    public CategoriaProductoDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Insertar categoría
    public boolean insertar(CategoriaProducto c) {
        ContentValues values = new ContentValues();
        values.put("NOMBRE_CATEGORIA", c.getNombreCategoria());
        values.put("DESCRIPCION_CATEGORIA", c.getDescripcionCategoria());
        values.put("DISPONIBLE_CATEGORIA", c.getDisponibleCategoria());
        values.put("HORA_DISPONIBLE_DESDE", c.getHoraDisponibleDesde());
        values.put("HORA_DISPONIBLE_HASTA", c.getHoraDisponibleHasta());
        values.put("ACTIVO_CATEGORIAPRODUCTO", c.getActivoCategoriaProducto());

        long resultado = db.insert("CATEGORIAPRODUCTO", null, values);
        return resultado != -1;
    }

    // Obtener categoría por ID
    public CategoriaProducto obtenerPorId(int id) {
        Cursor cursor = db.query("CATEGORIAPRODUCTO", null, "ID_CATEGORIAPRODUCTO = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        CategoriaProducto categoria = cursorToCategoria(cursor);
        cursor.close();
        return categoria;
    }

    // Obtener todas las categorías (con opción activos/inactivos)
    public List<CategoriaProducto> obtenerTodos(boolean soloActivos) {
        List<CategoriaProducto> lista = new ArrayList<>();
        String condicion = soloActivos ? "ACTIVO_CATEGORIAPRODUCTO = 1" : null;

        Cursor cursor = db.query("CATEGORIAPRODUCTO", null, condicion, null, null, null, "ID_CATEGORIAPRODUCTO ASC");

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorToCategoria(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // Actualizar categoría
    public boolean actualizar(CategoriaProducto c) {
        ContentValues values = new ContentValues();
        values.put("NOMBRE_CATEGORIA", c.getNombreCategoria());
        values.put("DESCRIPCION_CATEGORIA", c.getDescripcionCategoria());
        values.put("DISPONIBLE_CATEGORIA", c.getDisponibleCategoria());
        values.put("HORA_DISPONIBLE_DESDE", c.getHoraDisponibleDesde());
        values.put("HORA_DISPONIBLE_HASTA", c.getHoraDisponibleHasta());
        values.put("ACTIVO_CATEGORIAPRODUCTO", c.getActivoCategoriaProducto());

        int filas = db.update("CATEGORIAPRODUCTO", values, "ID_CATEGORIAPRODUCTO = ?",
                new String[]{String.valueOf(c.getIdCategoriaProducto())});

        return filas > 0;
    }

    // Eliminar categoría (borrado lógico)
    public boolean eliminar(int idCategoria) {
        ContentValues values = new ContentValues();
        values.put("ACTIVO_CATEGORIAPRODUCTO", 0);

        int filas = db.update("CATEGORIAPRODUCTO", values, "ID_CATEGORIAPRODUCTO = ?",
                new String[]{String.valueOf(idCategoria)});

        return filas > 0;
    }

    //Metodo privado para convertir Cursor en objeto CategoriaProducto
    private CategoriaProducto cursorToCategoria(Cursor c) {
        CategoriaProducto cat = new CategoriaProducto();
        cat.setIdCategoriaProducto(c.getInt(c.getColumnIndexOrThrow("ID_CATEGORIAPRODUCTO")));
        cat.setNombreCategoria(c.getString(c.getColumnIndexOrThrow("NOMBRE_CATEGORIA")));
        cat.setDescripcionCategoria(c.getString(c.getColumnIndexOrThrow("DESCRIPCION_CATEGORIA")));
        cat.setDisponibleCategoria(c.getInt(c.getColumnIndexOrThrow("DISPONIBLE_CATEGORIA")));
        cat.setHoraDisponibleDesde(c.getString(c.getColumnIndexOrThrow("HORA_DISPONIBLE_DESDE")));
        cat.setHoraDisponibleHasta(c.getString(c.getColumnIndexOrThrow("HORA_DISPONIBLE_HASTA")));
        cat.setActivoCategoriaProducto(c.getInt(c.getColumnIndexOrThrow("ACTIVO_CATEGORIAPRODUCTO")));
        return cat;
    }
}
