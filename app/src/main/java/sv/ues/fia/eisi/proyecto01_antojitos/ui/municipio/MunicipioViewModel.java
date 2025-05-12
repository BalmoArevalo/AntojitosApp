package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio.MunicipioDAO;

public class MunicipioViewModel extends AndroidViewModel {
    private final MutableLiveData<ArrayList<Municipio>> listaMunicipios = new MutableLiveData<>();
    private final MunicipioDAO municipioDAO;
    private final SQLiteDatabase db;

    public MunicipioViewModel(@NonNull Application application) {
        super(application);
        DBHelper dbHelper = new DBHelper(application.getApplicationContext());
        db = dbHelper.getWritableDatabase();
        municipioDAO = new MunicipioDAO(db);
    }

    // Cargar todos los municipios
    public void cargarMunicipios() {
        ArrayList<Municipio> municipios = municipioDAO.obtenerTodos();
        listaMunicipios.setValue(municipios);
    }

    // Obtener lista reactiva
    public LiveData<ArrayList<Municipio>> getListaMunicipios() {
        return listaMunicipios;
    }

    // Insertar nuevo municipio
    public long insertar(Municipio municipio) {
        return municipioDAO.insertar(municipio);
    }

    // Actualizar municipio (usando solo ID global)
    public int actualizar(Municipio municipio) {
        return municipioDAO.actualizar(municipio);
    }

    // ✅ NUEVO: Actualizar con clave primaria compuesta
    public int actualizarConClaveCompuesta(Municipio municipio, int idOriginalDep, int idOriginalMun) {
        return municipioDAO.actualizarConClaveCompuesta(municipio, idOriginalDep, idOriginalMun);
    }

    // Eliminar municipio (desactivación lógica)
    public int eliminar(int idMunicipio) {
        return municipioDAO.eliminar(idMunicipio);
    }

    // Consultar municipio por ID
    public Municipio consultar(int idMunicipio) {
        return municipioDAO.consultar(idMunicipio);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
