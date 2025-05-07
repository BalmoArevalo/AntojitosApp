package sv.ues.fia.eisi.proyecto01_antojitos.ui.datosProducto;

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
 * ViewModel para gestionar operaciones de DatosProducto fuera del hilo principal.
 */
public class DatosProductoViewModel extends AndroidViewModel {
    private final MutableLiveData<List<DatosProducto>> listaDatos = new MutableLiveData<>();
    private final MutableLiveData<DatosProducto> datoSeleccionado = new MutableLiveData<>();
    private final MutableLiveData<Long> idInsertado = new MutableLiveData<>();
    private final MutableLiveData<Integer> filasAfectadas = new MutableLiveData<>();
    private final DBHelper dbHelper;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public DatosProductoViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DBHelper(application);
    }

    public LiveData<List<DatosProducto>> getListaDatos() {
        return listaDatos;
    }

    public LiveData<DatosProducto> getDatoSeleccionado() {
        return datoSeleccionado;
    }

    public LiveData<Long> getIdInsertado() {
        return idInsertado;
    }

    public LiveData<Integer> getFilasAfectadas() {
        return filasAfectadas;
    }

    /**
     * Carga todos los registros activos.
     */
    public void cargarDatos() {
        executor.execute(() -> {
            List<DatosProducto> list = new DatosProductoDAO(dbHelper.getReadableDatabase()).findAll();
            listaDatos.postValue(list);
        });
    }

    /**
     * Selecciona un registro específico por clave compuesta.
     */
    public void seleccionar(int idSucursal, int idProducto) {
        executor.execute(() -> {
            DatosProducto dp = new DatosProductoDAO(dbHelper.getReadableDatabase()).find(idSucursal, idProducto);
            datoSeleccionado.postValue(dp);
        });
    }

    /**
     * Inserta un nuevo registro y publica el ID generado.
     */
    public void insertar(final DatosProducto dp) {
        executor.execute(() -> {
            long newId = new DatosProductoDAO(dbHelper.getWritableDatabase()).insert(dp);
            idInsertado.postValue(newId);
            cargarDatos();
        });
    }

    /**
     * Actualiza un registro existente y publica el número de filas afectadas.
     */
    public void actualizar(final DatosProducto dp) {
        executor.execute(() -> {
            int count = new DatosProductoDAO(dbHelper.getWritableDatabase()).update(dp);
            filasAfectadas.postValue(count);
            cargarDatos();
        });
    }

    /**
     * Elimina (soft-delete) un registro y publica el número de filas afectadas.
     */
    public void eliminar(final int idSucursal, final int idProducto) {
        executor.execute(() -> {
            int count = new DatosProductoDAO(dbHelper.getWritableDatabase()).delete(idSucursal, idProducto);
            filasAfectadas.postValue(count);
            cargarDatos();
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Cerrar DBHelper al destruir el ViewModel
        executor.execute(() -> dbHelper.close());
    }
}