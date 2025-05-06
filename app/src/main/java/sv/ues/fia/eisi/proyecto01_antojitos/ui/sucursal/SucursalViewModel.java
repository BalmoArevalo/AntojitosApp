package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class SucursalViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Sucursal>> listaSucursales = new MutableLiveData<>();
    private final MutableLiveData<Sucursal> sucursalSeleccionada = new MutableLiveData<>();
    private final DBHelper dbHelper;

    public SucursalViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DBHelper(application);
    }

    public LiveData<List<Sucursal>> getListaSucursales() {
        return listaSucursales;
    }

    public LiveData<Sucursal> getSucursalSeleccionada() {
        return sucursalSeleccionada;
    }

    /**
     * Carga todas las sucursales (activas e inactivas) y actualiza LiveData.
     */
    public void cargarSucursales() {
        SucursalDAO dao = new SucursalDAO(dbHelper.getReadableDatabase());
        listaSucursales.setValue(dao.obtenerTodos());
    }

    /**
     * Selecciona una sucursal por ID y actualiza LiveData.
     */
    public void seleccionarPorId(int idSucursal) {
        SucursalDAO dao = new SucursalDAO(dbHelper.getReadableDatabase());
        sucursalSeleccionada.setValue(dao.obtenerPorId(idSucursal));
    }

    /**
     * Inserta una nueva sucursal.
     */
    public long insertarSucursal(Sucursal s) {
        SucursalDAO dao = new SucursalDAO(dbHelper.getWritableDatabase());
        return dao.insertar(s);
    }

    /**
     * Actualiza una sucursal existente.
     */
    public int actualizarSucursal(Sucursal s) {
        SucursalDAO dao = new SucursalDAO(dbHelper.getWritableDatabase());
        return dao.actualizar(s);
    }

    /**
     * Elimina (soft delete) una sucursal por ID.
     */
    public int eliminarSucursal(int idSucursal) {
        SucursalDAO dao = new SucursalDAO(dbHelper.getWritableDatabase());
        return dao.eliminar(idSucursal);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        dbHelper.close();
    }
}
