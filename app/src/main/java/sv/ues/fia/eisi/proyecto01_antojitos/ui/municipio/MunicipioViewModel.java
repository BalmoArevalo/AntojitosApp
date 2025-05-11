package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.db.MunicipioDAO;

public class MunicipioViewModel extends AndroidViewModel {
    private MunicipioDAO municipioDAO;
    private SQLiteDatabase db;

    public MunicipioViewModel(@NonNull Application application) {
        super(application);
        DBHelper dbHelper = new DBHelper(application.getApplicationContext());
        db = dbHelper.getWritableDatabase();
        municipioDAO = new MunicipioDAO(db);
    }

    public long insertar(Municipio municipio) {
        return municipioDAO.insertar(municipio);
    }

    public int actualizar(Municipio municipio) {
        return municipioDAO.actualizar(municipio);
    }

    public int eliminar(int idDepartamento, int idMunicipio) {
        return municipioDAO.eliminar(idDepartamento, idMunicipio);
    }

    public Municipio consultar(int idDepartamento, int idMunicipio) {
        return municipioDAO.consultar(idDepartamento, idMunicipio);
    }

    public ArrayList<Municipio> obtenerTodos() {
        return municipioDAO.obtenerTodos();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
