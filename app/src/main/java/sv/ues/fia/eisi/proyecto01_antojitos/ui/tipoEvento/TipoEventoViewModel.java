package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento; // Ajusta el paquete si es necesario

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask; // Opción para hilos de fondo si no usas coroutines/Executors directamente
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
// Asumiendo que TipoEvento.java y TipoEventoDAO.java están en el mismo paquete o importados
// import sv.ues.fia.eisi.proyecto01_antojitos.TipoEvento;
// import sv.ues.fia.eisi.proyecto01_antojitos.TipoEventoDAO;


public class TipoEventoViewModel extends AndroidViewModel {

    private static final String TAG = "TipoEventoViewModel";
    private final DBHelper dbHelper;
    private final TipoEventoDAO tipoEventoDAO; // Instancia del DAO

    // LiveData para la lista de todos los tipos de evento
    private final MutableLiveData<List<TipoEvento>> listaTodosTiposEvento = new MutableLiveData<>();
    // LiveData para la lista de tipos de evento ACTIVOS (útil para Spinners, etc.)
    private final MutableLiveData<List<TipoEvento>> listaTiposEventoActivos = new MutableLiveData<>();

    // LiveData para notificar el resultado de operaciones (ej. inserción, actualización)
    private final MutableLiveData<Boolean> operacionExitosa = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();
    private final MutableLiveData<TipoEvento> tipoEventoSeleccionado = new MutableLiveData<>();

    // Executor para tareas en segundo plano
    private final ExecutorService executorService;

    public TipoEventoViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DBHelper(getApplication());
        SQLiteDatabase db = dbHelper.getReadableDatabase(); // Obtener DB para el DAO
        tipoEventoDAO = new TipoEventoDAO(db); // Inicializar DAO
        executorService = Executors.newSingleThreadExecutor(); // Inicializar Executor
        Log.d(TAG, "TipoEventoViewModel inicializado.");
    }

    // --- Getters para LiveData ---
    public LiveData<List<TipoEvento>> getListaTodosTiposEvento() {
        return listaTodosTiposEvento;
    }

    public LiveData<List<TipoEvento>> getListaTiposEventoActivos() {
        return listaTiposEventoActivos;
    }

    public LiveData<Boolean> getOperacionExitosa() {
        return operacionExitosa;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<TipoEvento> getTipoEventoSeleccionado() {
        return tipoEventoSeleccionado;
    }


    // --- Métodos para Cargar Datos (Asíncronos) ---

    public void cargarTodosTiposEvento() {
        Log.d(TAG, "Iniciando carga de todos los tipos de evento...");
        executorService.execute(() -> {
            try {
                List<TipoEvento> todos = tipoEventoDAO.obtenerTodos();
                listaTodosTiposEvento.postValue(todos);
                Log.d(TAG, "Todos los tipos de evento cargados: " + (todos != null ? todos.size() : 0));
            } catch (Exception e) {
                Log.e(TAG, "Error al cargar todos los tipos de evento", e);
                listaTodosTiposEvento.postValue(Collections.emptyList());
                mensajeError.postValue("Error al cargar todos los tipos de evento.");
            }
        });
    }

    public void cargarTiposEventoActivos() {
        Log.d(TAG, "Iniciando carga de tipos de evento activos...");
        executorService.execute(() -> {
            try {
                List<TipoEvento> activos = tipoEventoDAO.obtenerTodosActivos();
                listaTiposEventoActivos.postValue(activos);
                Log.d(TAG, "Tipos de evento activos cargados: " + (activos != null ? activos.size() : 0));
            } catch (Exception e) {
                Log.e(TAG, "Error al cargar tipos de evento activos", e);
                listaTiposEventoActivos.postValue(Collections.emptyList());
                mensajeError.postValue("Error al cargar tipos de evento activos.");
            }
        });
    }

    public void consultarTipoEventoPorId(int idTipoEvento) {
        Log.d(TAG, "Consultando tipo de evento por ID: " + idTipoEvento);
        executorService.execute(() -> {
            try {
                TipoEvento te = tipoEventoDAO.consultarPorId(idTipoEvento);
                tipoEventoSeleccionado.postValue(te);
                if (te != null) Log.d(TAG, "Tipo de evento encontrado.");
                else Log.d(TAG, "Tipo de evento no encontrado.");
            } catch (Exception e) {
                Log.e(TAG, "Error al consultar tipo de evento por ID", e);
                tipoEventoSeleccionado.postValue(null);
                mensajeError.postValue("Error al consultar el tipo de evento.");
            }
        });
    }

    // --- Métodos CRUD (Asíncronos) ---

    public void insertarTipoEvento(TipoEvento tipoEvento) {
        Log.d(TAG, "Insertando tipo de evento: " + tipoEvento.getNombreTipoEvento());
        executorService.execute(() -> {
            try {
                long resultado = tipoEventoDAO.insertar(tipoEvento);
                if (resultado != -1) {
                    operacionExitosa.postValue(true);
                    Log.i(TAG, "Tipo de evento insertado exitosamente.");
                    // Opcional: Recargar listas si es necesario inmediatamente
                    // cargarTodosTiposEvento();
                    // cargarTiposEventoActivos();
                } else {
                    operacionExitosa.postValue(false);
                    mensajeError.postValue("Error al insertar el tipo de evento.");
                    Log.w(TAG, "Fallo al insertar tipo de evento.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Excepción al insertar tipo de evento", e);
                operacionExitosa.postValue(false);
                mensajeError.postValue("Excepción: " + e.getMessage());
            }
        });
    }

    public void actualizarTipoEvento(TipoEvento tipoEvento) {
        Log.d(TAG, "Actualizando tipo de evento: ID " + tipoEvento.getIdTipoEvento());
        executorService.execute(() -> {
            try {
                int filasAfectadas = tipoEventoDAO.actualizar(tipoEvento);
                if (filasAfectadas > 0) {
                    operacionExitosa.postValue(true);
                    Log.i(TAG, "Tipo de evento actualizado exitosamente.");
                } else {
                    operacionExitosa.postValue(false);
                    mensajeError.postValue("No se pudo actualizar el tipo de evento (no encontrado o sin cambios).");
                    Log.w(TAG, "Fallo al actualizar tipo de evento o no se encontraron filas.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Excepción al actualizar tipo de evento", e);
                operacionExitosa.postValue(false);
                mensajeError.postValue("Excepción: " + e.getMessage());
            }
        });
    }

    /**
     * Desactiva un TipoEvento (borrado lógico).
     */
    public void desactivarTipoEvento(int idTipoEvento) {
        Log.d(TAG, "Desactivando tipo de evento: ID " + idTipoEvento);
        executorService.execute(() -> {
            try {
                TipoEvento te = tipoEventoDAO.consultarPorId(idTipoEvento);
                if (te != null) {
                    if (te.getActivoTipoEvento() == 0) {
                        Log.w(TAG, "El tipo de evento ya estaba inactivo: ID " + idTipoEvento);
                        operacionExitosa.postValue(true); // Considerar éxito si ya estaba inactivo
                        return;
                    }
                    te.setActivoTipoEvento(0); // Marcar como inactivo
                    int filasAfectadas = tipoEventoDAO.actualizar(te);
                    if (filasAfectadas > 0) {
                        operacionExitosa.postValue(true);
                        Log.i(TAG, "Tipo de evento desactivado exitosamente.");
                    } else {
                        operacionExitosa.postValue(false);
                        mensajeError.postValue("No se pudo desactivar el tipo de evento.");
                        Log.w(TAG, "Fallo al desactivar tipo de evento.");
                    }
                } else {
                    operacionExitosa.postValue(false);
                    mensajeError.postValue("Tipo de evento no encontrado para desactivar.");
                    Log.w(TAG, "Tipo de evento no encontrado para desactivar: ID " + idTipoEvento);
                }
            } catch (Exception e) {
                Log.e(TAG, "Excepción al desactivar tipo de evento", e);
                operacionExitosa.postValue(false);
                mensajeError.postValue("Excepción: " + e.getMessage());
            }
        });
    }

    /**
     * Reactiva un TipoEvento (borrado lógico).
     */
    public void reactivarTipoEvento(int idTipoEvento) {
        Log.d(TAG, "Reactivando tipo de evento: ID " + idTipoEvento);
        executorService.execute(() -> {
            try {
                TipoEvento te = tipoEventoDAO.consultarPorId(idTipoEvento);
                if (te != null) {
                    if (te.getActivoTipoEvento() == 1) {
                        Log.w(TAG, "El tipo de evento ya estaba activo: ID " + idTipoEvento);
                        operacionExitosa.postValue(true); // Considerar éxito si ya estaba activo
                        return;
                    }
                    te.setActivoTipoEvento(1); // Marcar como activo
                    int filasAfectadas = tipoEventoDAO.actualizar(te);
                    if (filasAfectadas > 0) {
                        operacionExitosa.postValue(true);
                        Log.i(TAG, "Tipo de evento reactivado exitosamente.");
                    } else {
                        operacionExitosa.postValue(false);
                        mensajeError.postValue("No se pudo reactivar el tipo de evento.");
                        Log.w(TAG, "Fallo al reactivar tipo de evento.");
                    }
                } else {
                    operacionExitosa.postValue(false);
                    mensajeError.postValue("Tipo de evento no encontrado para reactivar.");
                    Log.w(TAG, "Tipo de evento no encontrado para reactivar: ID " + idTipoEvento);
                }
            } catch (Exception e) {
                Log.e(TAG, "Excepción al reactivar tipo de evento", e);
                operacionExitosa.postValue(false);
                mensajeError.postValue("Excepción: " + e.getMessage());
            }
        });
    }


    /**
     * Elimina físicamente un TipoEvento. Usar con precaución.
     * Si se prefiere borrado lógico, usar desactivarTipoEvento.
     */
    public void eliminarTipoEventoFisico(int idTipoEvento) {
        Log.d(TAG, "Eliminando físicamente tipo de evento: ID " + idTipoEvento);
        executorService.execute(() -> {
            try {
                int filasAfectadas = tipoEventoDAO.eliminar(idTipoEvento); // Asume que DAO.eliminar() es borrado físico
                if (filasAfectadas > 0) {
                    operacionExitosa.postValue(true);
                    Log.i(TAG, "Tipo de evento eliminado físicamente exitosamente.");
                } else {
                    operacionExitosa.postValue(false);
                    mensajeError.postValue("No se pudo eliminar el tipo de evento (no encontrado).");
                    Log.w(TAG, "Fallo al eliminar físicamente tipo de evento.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Excepción al eliminar físicamente tipo de evento", e);
                operacionExitosa.postValue(false);
                mensajeError.postValue("Excepción: " + e.getMessage());
            }
        });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        // Apagar el executor service para liberar recursos
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        // No es necesario cerrar dbHelper aquí, Android maneja su ciclo de vida
        // con el contexto de la aplicación.
        Log.d(TAG, "TipoEventoViewModel cleared y executor apagado.");
    }
}