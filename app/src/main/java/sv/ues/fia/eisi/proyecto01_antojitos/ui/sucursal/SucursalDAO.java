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

    public long insertar(Sucursal s) {
        ContentValues values = new ContentValues();
        values.put("ID_DEPARTAMENTO", s.getIdDepartamento());
        values.put("ID_MUNICIPIO", s.getIdMunicipio());
        values.put("ID_DISTRITO", s.getIdDistrito());
        values.put("ID_USUARIO", s.getIdUsuario()); // Asumido fijo
        values.put("NOMBRE_SUCURSAL", s.getNombreSucursal());
        values.put("DIRECCION_SUCURSAL", s.getDireccionSucursal());
        values.put("TELEFONO_SUCURSAL", s.getTelefonoSucursal());
        values.put("HORARIO_APERTURA_SUCURSAL", s.getHorarioApertura());
        values.put("HORARIO_CIERRE_SUCURSAL", s.getHorarioCierre());

        return db.insert("SUCURSAL", null, values);
    }

    public Sucursal obtenerPorId(int idSucursal) {
        Cursor c = db.rawQuery("SELECT * FROM SUCURSAL WHERE ID_SUCURSAL = ?", new String[]{String.valueOf(idSucursal)});
        Sucursal s = null;

        if (c.moveToFirst()) {
            s = new Sucursal();
            s.setIdSucursal(c.getInt(0));
            s.setIdDepartamento(c.getInt(1));
            s.setIdMunicipio(c.getInt(2));
            s.setIdDistrito(c.getInt(3));
            s.setIdUsuario(c.getInt(4));
            s.setNombreSucursal(c.getString(5));
            s.setDireccionSucursal(c.getString(6));
            s.setTelefonoSucursal(c.getString(7));
            s.setHorarioApertura(c.getString(8));
            s.setHorarioCierre(c.getString(9));
        }
        c.close();
        return s;
    }

    public List<Sucursal> obtenerTodas() {
        List<Sucursal> lista = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM SUCURSAL", null);

        if (c.moveToFirst()) {
            do {
                Sucursal s = new Sucursal();
                s.setIdSucursal(c.getInt(0));
                s.setIdDepartamento(c.getInt(1));
                s.setIdMunicipio(c.getInt(2));
                s.setIdDistrito(c.getInt(3));
                s.setIdUsuario(c.getInt(4));
                s.setNombreSucursal(c.getString(5));
                s.setDireccionSucursal(c.getString(6));
                s.setTelefonoSucursal(c.getString(7));
                s.setHorarioApertura(c.getString(8));
                s.setHorarioCierre(c.getString(9));
                lista.add(s);
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    public int actualizar(Sucursal s) {
        ContentValues values = new ContentValues();
        values.put("ID_DEPARTAMENTO", s.getIdDepartamento());
        values.put("ID_MUNICIPIO", s.getIdMunicipio());
        values.put("ID_DISTRITO", s.getIdDistrito());
        values.put("NOMBRE_SUCURSAL", s.getNombreSucursal());
        values.put("DIRECCION_SUCURSAL", s.getDireccionSucursal());
        values.put("TELEFONO_SUCURSAL", s.getTelefonoSucursal());
        values.put("HORARIO_APERTURA_SUCURSAL", s.getHorarioApertura());
        values.put("HORARIO_CIERRE_SUCURSAL", s.getHorarioCierre());

        return db.update("SUCURSAL", values, "ID_SUCURSAL = ?", new String[]{String.valueOf(s.getIdSucursal())});
    }

    public int eliminar(int idSucursal) {
        return db.delete("SUCURSAL", "ID_SUCURSAL = ?", new String[]{String.valueOf(idSucursal)});
    }
}
