package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SucursalDAO {

    private final SQLiteDatabase db;

    public SucursalDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Insertar una nueva sucursal
    public long insertar(Sucursal sucursal) {
        ContentValues values = new ContentValues();
        values.put("ID_SUCURSAL", sucursal.getIdSucursal());
        values.put("ID_DEPARTAMENTO", sucursal.getIdDepartamento());
        values.put("ID_MUNICIPIO", sucursal.getIdMunicipio());
        values.put("ID_DISTRITO", sucursal.getIdDistrito());
        values.put("ID_USUARIO", sucursal.getIdUsuario());
        values.put("NOMBRE_SUCURSAL", sucursal.getNombreSucursal());
        values.put("DIRECCION_SUCURSAL", sucursal.getDireccionSucursal());
        values.put("TELEFONO_SUCURSAL", sucursal.getTelefonoSucursal());
        values.put("HORARIO_APERTURA_SUCURSAL", sucursal.getHorarioApertura());
        values.put("HORARIO_CIERRE_SUCURSAL", sucursal.getHorarioCierre());

        return db.insert("SUCURSAL", null, values);
    }

    // Consultar por ID
    public Sucursal consultarPorId(int idSucursal) {
        Cursor cursor = db.query("SUCURSAL", null, "ID_SUCURSAL = ?",
                new String[]{String.valueOf(idSucursal)},
                null, null, null);

        if (cursor.moveToFirst()) {
            Sucursal sucursal = new Sucursal();
            sucursal.setIdSucursal(cursor.getInt(0));
            sucursal.setIdDepartamento(cursor.getInt(1));
            sucursal.setIdMunicipio(cursor.getInt(2));
            sucursal.setIdDistrito(cursor.getInt(3));
            sucursal.setIdUsuario(cursor.getInt(4));
            sucursal.setNombreSucursal(cursor.getString(5));
            sucursal.setDireccionSucursal(cursor.getString(6));
            sucursal.setTelefonoSucursal(cursor.getString(7));
            sucursal.setHorarioApertura(cursor.getString(8));
            sucursal.setHorarioCierre(cursor.getString(9));
            cursor.close();
            return sucursal;
        } else {
            cursor.close();
            return null;
        }
    }

    // Actualizar sucursal existente
    public int actualizar(Sucursal sucursal) {
        ContentValues values = new ContentValues();
        values.put("ID_DEPARTAMENTO", sucursal.getIdDepartamento());
        values.put("ID_MUNICIPIO", sucursal.getIdMunicipio());
        values.put("ID_DISTRITO", sucursal.getIdDistrito());
        values.put("ID_USUARIO", sucursal.getIdUsuario());
        values.put("NOMBRE_SUCURSAL", sucursal.getNombreSucursal());
        values.put("DIRECCION_SUCURSAL", sucursal.getDireccionSucursal());
        values.put("TELEFONO_SUCURSAL", sucursal.getTelefonoSucursal());
        values.put("HORARIO_APERTURA_SUCURSAL", sucursal.getHorarioApertura());
        values.put("HORARIO_CIERRE_SUCURSAL", sucursal.getHorarioCierre());

        return db.update("SUCURSAL", values, "ID_SUCURSAL = ?",
                new String[]{String.valueOf(sucursal.getIdSucursal())});
    }

    // Eliminar sucursal
    public int eliminar(int idSucursal) {
        return db.delete("SUCURSAL", "ID_SUCURSAL = ?", new String[]{String.valueOf(idSucursal)});
    }

    // Obtener todas las sucursales (opcional)
    public List<Sucursal> obtenerTodas() {
        List<Sucursal> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM SUCURSAL", null);

        if (cursor.moveToFirst()) {
            do {
                Sucursal sucursal = new Sucursal();
                sucursal.setIdSucursal(cursor.getInt(0));
                sucursal.setIdDepartamento(cursor.getInt(1));
                sucursal.setIdMunicipio(cursor.getInt(2));
                sucursal.setIdDistrito(cursor.getInt(3));
                sucursal.setIdUsuario(cursor.getInt(4));
                sucursal.setNombreSucursal(cursor.getString(5));
                sucursal.setDireccionSucursal(cursor.getString(6));
                sucursal.setTelefonoSucursal(cursor.getString(7));
                sucursal.setHorarioApertura(cursor.getString(8));
                sucursal.setHorarioCierre(cursor.getString(9));
                lista.add(sucursal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}
