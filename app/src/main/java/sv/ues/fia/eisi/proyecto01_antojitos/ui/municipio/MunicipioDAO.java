package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio.Municipio;

public class MunicipioDAO {
    private final SQLiteDatabase db;

    public MunicipioDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Insertar nuevo municipio
    public long insertar(Municipio municipio) {
        ContentValues valores = new ContentValues();
        valores.put("ID_DEPARTAMENTO", municipio.getIdDepartamento());
        valores.put("ID_MUNICIPIO", municipio.getIdMunicipio());
        valores.put("NOMBRE_MUNICIPIO", municipio.getNombreMunicipio());
        valores.put("ACTIVO_MUNICIPIO", municipio.getActivoMunicipio());

        return db.insert("MUNICIPIO", null, valores);
    }

    // Actualizar municipio usando ID global (si solo fuera ID único)
    public int actualizar(Municipio municipio) {
        ContentValues valores = new ContentValues();
        valores.put("NOMBRE_MUNICIPIO", municipio.getNombreMunicipio());
        valores.put("ACTIVO_MUNICIPIO", municipio.getActivoMunicipio());

        String condicion = "ID_MUNICIPIO = ?";
        String[] args = { String.valueOf(municipio.getIdMunicipio()) };

        return db.update("MUNICIPIO", valores, condicion, args);
    }

    // ✅ NUEVO: Actualizar municipio cuando tiene clave primaria compuesta
    public int actualizarConClaveCompuesta(Municipio municipio, int idOriginalDepartamento, int idOriginalMunicipio) {
        ContentValues valores = new ContentValues();
        valores.put("ID_DEPARTAMENTO", municipio.getIdDepartamento());
        valores.put("ID_MUNICIPIO", municipio.getIdMunicipio());
        valores.put("NOMBRE_MUNICIPIO", municipio.getNombreMunicipio());
        valores.put("ACTIVO_MUNICIPIO", municipio.getActivoMunicipio());

        String condicion = "ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ?";
        String[] args = {
                String.valueOf(idOriginalDepartamento),
                String.valueOf(idOriginalMunicipio)
        };

        return db.update("MUNICIPIO", valores, condicion, args);
    }

    // Eliminar municipio por ID global (si ID fuera único)
    public int eliminar(int idMunicipio) {
        String condicion = "ID_MUNICIPIO = ?";
        String[] args = { String.valueOf(idMunicipio) };
        return db.delete("MUNICIPIO", condicion, args);
    }

    // Consultar municipio por ID global
    public Municipio consultar(int idMunicipio) {
        String consulta = "SELECT ID_DEPARTAMENTO, ID_MUNICIPIO, NOMBRE_MUNICIPIO, ACTIVO_MUNICIPIO " +
                "FROM MUNICIPIO WHERE ID_MUNICIPIO = ?";
        String[] args = { String.valueOf(idMunicipio) };

        Cursor cursor = db.rawQuery(consulta, args);
        if (cursor.moveToFirst()) {
            Municipio municipio = new Municipio();
            municipio.setIdDepartamento(cursor.getInt(0));
            municipio.setIdMunicipio(cursor.getInt(1));
            municipio.setNombreMunicipio(cursor.getString(2));
            municipio.setActivoMunicipio(cursor.getInt(3));
            cursor.close();
            return municipio;
        }
        cursor.close();
        return null;
    }

    // Obtener todos los municipios
    public ArrayList<Municipio> obtenerTodos() {
        ArrayList<Municipio> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM MUNICIPIO", null);

        if (cursor.moveToFirst()) {
            do {
                Municipio municipio = new Municipio();
                municipio.setIdDepartamento(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DEPARTAMENTO")));
                municipio.setIdMunicipio(cursor.getInt(cursor.getColumnIndexOrThrow("ID_MUNICIPIO")));
                municipio.setNombreMunicipio(cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_MUNICIPIO")));
                municipio.setActivoMunicipio(cursor.getInt(cursor.getColumnIndexOrThrow("ACTIVO_MUNICIPIO")));
                lista.add(municipio);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}
