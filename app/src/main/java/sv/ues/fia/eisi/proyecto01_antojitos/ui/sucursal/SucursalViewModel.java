package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class SucursalViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Sucursal>> listaSucursales = new MutableLiveData<>();

    public SucursalViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Sucursal>> getListaSucursales() {
        return listaSucursales;
    }

    public void cargarSucursales() {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        SucursalDAO dao = new SucursalDAO(db);
        List<Sucursal> resultado = dao.obtenerTodas();
        db.close();
        listaSucursales.setValue(resultado);
    }

    public Sucursal consultarPorId(int idSucursal) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        Sucursal sucursal = new SucursalDAO(db).obtenerPorId(idSucursal);
        db.close();
        return sucursal;
    }

    // Métodos opcionales
    public long insertarSucursal(Sucursal s) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getWritableDatabase();
        long res = new SucursalDAO(db).insertar(s);
        db.close();
        return res;
    }

    public int actualizarSucursal(Sucursal s) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getWritableDatabase();
        int res = new SucursalDAO(db).actualizar(s);
        db.close();
        return res;
    }

    public int eliminarSucursal(int id) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getWritableDatabase();
        int res = new SucursalDAO(db).eliminar(id);
        db.close();
        return res;
    }
}
