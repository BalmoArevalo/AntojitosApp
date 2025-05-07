package sv.ues.fia.eisi.proyecto01_antojitos.ui.datosProducto;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class DatosProductoDAO {

    private final SQLiteDatabase db;
    private static final String TABLE = "DATOSPRODUCTO";
    private static final String C1 = "ID_SUCURSAL";
    private static final String C2 = "ID_PRODUCTO";
    private static final String C3 = "PRECIO_SUCURSAL_PRODUCTO";
    private static final String C4 = "STOCK";
    private static final String C5 = "ACTIVO_DATOSPRODUCTO";

    public DatosProductoDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insert(DatosProducto dp) {
        ContentValues cv = new ContentValues();
        cv.put(C1, dp.getIdSucursal());
        cv.put(C2, dp.getIdProducto());
        cv.put(C3, dp.getPrecioSucursalProducto());
        cv.put(C4, dp.getStock());
        cv.put(C5, dp.isActivo() ? 1 : 0);
        return db.insert(TABLE, null, cv);
    }

    public DatosProducto find(int idSucursal, int idProducto) {
        String where = C1 + "=? AND " + C2 + "=? AND " + C5 + "=1";
        String[] args = {String.valueOf(idSucursal), String.valueOf(idProducto)};
        Cursor c = db.query(TABLE, null, where, args, null, null, null);
        if (!c.moveToFirst()) { c.close(); return null; }
        DatosProducto dp = new DatosProducto(
                c.getInt(c.getColumnIndexOrThrow(C1)),
                c.getInt(c.getColumnIndexOrThrow(C2)),
                c.getDouble(c.getColumnIndexOrThrow(C3)),
                c.getInt(c.getColumnIndexOrThrow(C4)),
                c.getInt(c.getColumnIndexOrThrow(C5))==1
        );
        c.close();
        return dp;
    }

    public List<DatosProducto> findAll() {
        List<DatosProducto> list = new ArrayList<>();
        Cursor c = db.query(TABLE, null, C5 + "=1", null, null, null, null);
        while (c.moveToNext()) {
            list.add(new DatosProducto(
                    c.getInt(c.getColumnIndexOrThrow(C1)),
                    c.getInt(c.getColumnIndexOrThrow(C2)),
                    c.getDouble(c.getColumnIndexOrThrow(C3)),
                    c.getInt(c.getColumnIndexOrThrow(C4)),
                    true
            ));
        }
        c.close();
        return list;
    }

    public int update(DatosProducto dp) {
        ContentValues cv = new ContentValues();
        cv.put(C3, dp.getPrecioSucursalProducto());
        cv.put(C4, dp.getStock());
        cv.put(C5, dp.isActivo() ? 1 : 0);
        String where = C1 + "=? AND " + C2 + "=?";
        String[] args = {String.valueOf(dp.getIdSucursal()), String.valueOf(dp.getIdProducto())};
        return db.update(TABLE, cv, where, args);
    }

    public int delete(int idSucursal, int idProducto) {
        ContentValues cv = new ContentValues();
        cv.put(C5, 0);
        String where = C1 + "=? AND " + C2 + "=?";
        String[] args = {String.valueOf(idSucursal), String.valueOf(idProducto)};
        return db.update(TABLE, cv, where, args);
    }
}
