package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class RepartidorViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Repartidor>> listaRepartidores = new MutableLiveData<>();
    private final MutableLiveData<Repartidor> repartidorSeleccionado = new MutableLiveData<>();
    private final DBHelper dbHelper;

    public RepartidorViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DBHelper(application);
    }

    /**
     * LiveData con la lista de repartidores.
     */
    public LiveData<List<Repartidor>> getListaRepartidores() {
        return listaRepartidores;
    }

    /**
     * LiveData con el repartidor seleccionado.
     */
    public LiveData<Repartidor> getRepartidorSeleccionado() {
        return repartidorSeleccionado;
    }

    /**
     * Carga todos los repartidores (activos e inactivos) y actualiza el LiveData.
     */
    public void cargarRepartidores() {
        RepartidorDAO dao = new RepartidorDAO(dbHelper.getReadableDatabase());
        listaRepartidores.setValue(dao.obtenerTodos());
    }

    /**
     * Selecciona un repartidor por su ID y actualiza el LiveData.
     */
    public void seleccionarPorId(int idRepartidor) {
        RepartidorDAO dao = new RepartidorDAO(dbHelper.getReadableDatabase());
        repartidorSeleccionado.setValue(dao.obtenerPorId(idRepartidor));
    }

    /**
     * Inserta un nuevo repartidor y devuelve el ID generado.
     */
    public long insertarRepartidor(Repartidor r) {
        RepartidorDAO dao = new RepartidorDAO(dbHelper.getWritableDatabase());
        return dao.insertar(r);
    }

    /**
     * Actualiza los datos de un repartidor existente.
     */
    public int actualizarRepartidor(Repartidor r) {
        RepartidorDAO dao = new RepartidorDAO(dbHelper.getWritableDatabase());
        return dao.actualizar(r);
    }

    /**
     * Realiza soft delete de un repartidor, marcándolo como inactivo.
     */
    public int eliminarRepartidor(int idRepartidor) {
        RepartidorDAO dao = new RepartidorDAO(dbHelper.getWritableDatabase());
        return dao.eliminar(idRepartidor);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        dbHelper.close();
    }
}
