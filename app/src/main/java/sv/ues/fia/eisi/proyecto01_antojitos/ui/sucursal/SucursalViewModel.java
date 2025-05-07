package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

/**
 * ViewModel para gestionar operaciones de Sucursal fuera del hilo principal.
 */
public class SucursalViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Sucursal>> listaSucursales = new MutableLiveData<>();
    private final MutableLiveData<Sucursal> sucursalSeleccionada = new MutableLiveData<>();
    private final MutableLiveData<Long> idInsertado = new MutableLiveData<>();
    private final MutableLiveData<Integer> filasAfectadas = new MutableLiveData<>();
    private final DBHelper dbHelper;
    private final Executor executor = Executors.newSingleThreadExecutor();

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

    public LiveData<Long> getIdInsertado() {
        return idInsertado;
    }

    public LiveData<Integer> getFilasAfectadas() {
        return filasAfectadas;
    }

    /**
     * Carga todas las sucursales en un hilo de fondo.
     */
    public void cargarSucursales() {
        executor.execute(() -> {
            List<Sucursal> resultados = new SucursalDAO(dbHelper.getReadableDatabase()).obtenerTodos();
            listaSucursales.postValue(resultados);
        });
    }

    /**
     * Selecciona una sucursal por ID en un hilo de fondo.
     */
    public void seleccionarPorId(int idSucursal) {
        executor.execute(() -> {
            Sucursal s = new SucursalDAO(dbHelper.getReadableDatabase()).obtenerPorId(idSucursal);
            sucursalSeleccionada.postValue(s);
        });
    }

    /**
     * Inserta una sucursal en un hilo de fondo y publica el ID generado.
     */
    public void insertarSucursal(final Sucursal sucursal) {
        executor.execute(() -> {
            long nuevoId = new SucursalDAO(dbHelper.getWritableDatabase()).insertar(sucursal);
            idInsertado.postValue(nuevoId);
        });
    }

    /**
     * Actualiza una sucursal existente en un hilo de fondo y publica filas afectadas.
     */
    public void actualizarSucursal(final Sucursal sucursal) {
        executor.execute(() -> {
            int count = new SucursalDAO(dbHelper.getWritableDatabase()).actualizar(sucursal);
            filasAfectadas.postValue(count);
        });
    }

    /**
     * Elimina (soft delete) una sucursal y publica filas afectadas.
     */
    public void eliminarSucursal(final int idSucursal) {
        executor.execute(() -> {
            int count = new SucursalDAO(dbHelper.getWritableDatabase()).eliminar(idSucursal);
            filasAfectadas.postValue(count);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Cerrar DBHelper al finalizar
        executor.execute(() -> dbHelper.close());
    }
}