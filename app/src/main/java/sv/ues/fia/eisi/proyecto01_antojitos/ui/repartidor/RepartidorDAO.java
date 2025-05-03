package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RepartidorDAO {

    private final SQLiteDatabase db;

    public RepartidorDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertar(Repartidor repartidor) {
        ContentValues values = new ContentValues();
        values.put("ID_REPARTIDOR", repartidor.getIdRepartidor());
        values.put("ID_USUARIO", repartidor.getIdUsuario());
        values.put("ID_DEPARTAMENTO", repartidor.getIdDepartamento());
        values.put("ID_MUNICIPIO", repartidor.getIdMunicipio());
        values.put("ID_DISTRITO", repartidor.getIdDistrito());
        values.put("TIPO_VEHICULO", repartidor.getTipoVehiculo());
        values.put("DISPONIBLE", repartidor.getDisponible());
        values.put("TELEFONO_REPARTIDOR", repartidor.getTelefonoRepartidor());
        values.put("NOMBRE_REPARTIDOR", repartidor.getNombreRepartidor());
        values.put("APELLIDO_REPARTIDOR", repartidor.getApellidoRepartidor());

        return db.insert("REPARTIDOR", null, values);
    }

    public Repartidor consultarPorId(int idRepartidor) {
        Cursor cursor = db.query("REPARTIDOR", null, "ID_REPARTIDOR = ?",
                new String[]{String.valueOf(idRepartidor)}, null, null, null);

        if (cursor.moveToFirst()) {
            Repartidor repartidor = new Repartidor();
            repartidor.setIdRepartidor(cursor.getInt(0));
            repartidor.setIdUsuario(cursor.getInt(1));
            repartidor.setIdDepartamento(cursor.getInt(2));
            repartidor.setIdMunicipio(cursor.getInt(3));
            repartidor.setIdDistrito(cursor.getInt(4));
            repartidor.setTipoVehiculo(cursor.getString(5));
            repartidor.setDisponible(cursor.getInt(6));
            repartidor.setTelefonoRepartidor(cursor.getString(7));
            repartidor.setNombreRepartidor(cursor.getString(8));
            repartidor.setApellidoRepartidor(cursor.getString(9));
            cursor.close();
            return repartidor;
        }
        cursor.close();
        return null;
    }

    public int actualizar(Repartidor repartidor) {
        ContentValues values = new ContentValues();
        values.put("ID_USUARIO", repartidor.getIdUsuario());
        values.put("ID_DEPARTAMENTO", repartidor.getIdDepartamento());
        values.put("ID_MUNICIPIO", repartidor.getIdMunicipio());
        values.put("ID_DISTRITO", repartidor.getIdDistrito());
        values.put("TIPO_VEHICULO", repartidor.getTipoVehiculo());
        values.put("DISPONIBLE", repartidor.getDisponible());
        values.put("TELEFONO_REPARTIDOR", repartidor.getTelefonoRepartidor());
        values.put("NOMBRE_REPARTIDOR", repartidor.getNombreRepartidor());
        values.put("APELLIDO_REPARTIDOR", repartidor.getApellidoRepartidor());

        return db.update("REPARTIDOR", values, "ID_REPARTIDOR = ?",
                new String[]{String.valueOf(repartidor.getIdRepartidor())});
    }

    public int eliminar(int idRepartidor) {
        return db.delete("REPARTIDOR", "ID_REPARTIDOR = ?",
                new String[]{String.valueOf(idRepartidor)});
    }

    public List<Repartidor> obtenerTodos() {
        List<Repartidor> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM REPARTIDOR", null);

        if (cursor.moveToFirst()) {
            do {
                Repartidor repartidor = new Repartidor();
                repartidor.setIdRepartidor(cursor.getInt(0));
                repartidor.setIdUsuario(cursor.getInt(1));
                repartidor.setIdDepartamento(cursor.getInt(2));
                repartidor.setIdMunicipio(cursor.getInt(3));
                repartidor.setIdDistrito(cursor.getInt(4));
                repartidor.setTipoVehiculo(cursor.getString(5));
                repartidor.setDisponible(cursor.getInt(6));
                repartidor.setTelefonoRepartidor(cursor.getString(7));
                repartidor.setNombreRepartidor(cursor.getString(8));
                repartidor.setApellidoRepartidor(cursor.getString(9));
                lista.add(repartidor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}
