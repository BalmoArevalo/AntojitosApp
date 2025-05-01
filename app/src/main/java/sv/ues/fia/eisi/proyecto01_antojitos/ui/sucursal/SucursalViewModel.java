package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import android.database.sqlite.SQLiteDatabase;

public class SucursalViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Sucursal>> listaSucursales = new MutableLiveData<>();

    public SucursalViewModel(@NonNull Application application) {
        super(application);
    }

    // Retorna la lista observable
    public LiveData<List<Sucursal>> getListaSucursales() {
        return listaSucursales;
    }

    // Carga todas las sucursales desde la base de datos
    public void cargarSucursales() {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        SucursalDAO dao = new SucursalDAO(db);
        List<Sucursal> lista = dao.obtenerTodas();
        db.close();

        listaSucursales.setValue(lista);
    }

    public Sucursal consultarSucursalPorId(int id) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        SucursalDAO dao = new SucursalDAO(db);
        Sucursal sucursal = dao.consultarPorId(id);
        db.close();
        return sucursal;
    }
}
