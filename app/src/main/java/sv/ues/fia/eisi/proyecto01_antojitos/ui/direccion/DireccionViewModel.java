package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList; // Necesario para filtrar
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors; // Para usar Streams (requiere API 24+)

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DireccionViewModel extends AndroidViewModel {

    private static final String TAG = "DireccionViewModel";
    // LiveData para la lista COMPLETA (activas e inactivas)
    private final MutableLiveData<List<Direccion>> listaTodasDirecciones = new MutableLiveData<>();
    // LiveData opcional para solo las activas (útil para UI)
    private final MutableLiveData<List<Direccion>> listaDireccionesActivas = new MutableLiveData<>();

    private DBHelper dbHelper;

    public DireccionViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DBHelper(getApplication());
        Log.d(TAG, "DireccionViewModel inicializado.");
    }

    // --- Getters para LiveData ---

    /** Devuelve LiveData con TODAS las direcciones (activas e inactivas) */
    public LiveData<List<Direccion>> getListaTodasDirecciones() {
        return listaTodasDirecciones;
    }

    /** Devuelve LiveData solo con las direcciones ACTIVAS */
    public LiveData<List<Direccion>> getListaDireccionesActivas() {
        return listaDireccionesActivas;
    }

    // --- Métodos para Cargar Datos ---

    /** Carga TODAS las direcciones (activas e inactivas) y actualiza ambos LiveData */
    public void cargarDirecciones() {
        Log.d(TAG, "Cargando todas las direcciones...");
        SQLiteDatabase db = null;
        List<Direccion> todas = Collections.emptyList(); // Inicializar como vacía
        try {
            db = dbHelper.getReadableDatabase();
            DireccionDAO dao = new DireccionDAO(db);
            todas = dao.obtenerTodas(); // DAO devuelve objetos con activoDireccion
            listaTodasDirecciones.postValue(todas); // Actualizar LiveData de todas

            // Filtrar para actualizar LiveData de activas
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                // Usando Streams (API 24+)
                List<Direccion> activas = todas.stream()
                        .filter(d -> d.getActivoDireccion() == 1)
                        .collect(Collectors.toList());
                listaDireccionesActivas.postValue(activas);
                Log.d(TAG, "Direcciones cargadas: Todas=" + todas.size() + ", Activas=" + activas.size());
            } else {
                // Alternativa para APIs < 24
                List<Direccion> activas = new ArrayList<>();
                if (todas != null) {
                    for (Direccion d : todas) {
                        if (d.getActivoDireccion() == 1) {
                            activas.add(d);
                        }
                    }
                }
                listaDireccionesActivas.postValue(activas);
                Log.d(TAG, "Direcciones cargadas: Todas=" + todas.size() + ", Activas=" + activas.size());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar direcciones", e);
            listaTodasDirecciones.postValue(Collections.emptyList());
            listaDireccionesActivas.postValue(Collections.emptyList());
        } finally {
            // No cerrar la DB si se reutiliza el helper
        }
    }

    /** Consulta una dirección específica por su ID compuesto */
    public Direccion consultarPorId(int idCliente, int idDireccion) {
        Log.d(TAG, "Consultando dirección por ID: Cliente=" + idCliente + ", Direccion=" + idDireccion);
        SQLiteDatabase db = null;
        Direccion dir = null;
        try {
            db = dbHelper.getReadableDatabase();
            dir = new DireccionDAO(db).consultarPorId(idCliente, idDireccion);
            if (dir != null) Log.d(TAG, "Dirección encontrada.");
            else Log.d(TAG, "Dirección no encontrada.");
        } catch (Exception e) {
            Log.e(TAG, "Error al consultar dirección por ID", e);
        } finally {
            // No cerrar DB aquí
        }
        return dir;
    }

    // --- Métodos para Borrado Lógico (Soft Delete) ---

    /**
     * Desactiva una dirección (borrado lógico) estableciendo ACTIVO_DIRECCION = 0.
     * @param idCliente ID del cliente.
     * @param idDireccion ID de la dirección.
     * @return true si la desactivación fue exitosa, false en caso contrario.
     */
    public boolean desactivarDireccion(int idCliente, int idDireccion) {
        Log.i(TAG, "Intentando desactivar dirección: Cliente=" + idCliente + ", Direccion=" + idDireccion);
        SQLiteDatabase db = null;
        boolean exito = false;
        try {
            db = dbHelper.getWritableDatabase(); // Necesitamos escribir
            DireccionDAO dao = new DireccionDAO(db);
            Direccion dir = dao.consultarPorId(idCliente, idDireccion); // Consultar la dirección actual

            if (dir != null) {
                if (dir.getActivoDireccion() == 0) {
                    Log.w(TAG, "La dirección ya estaba inactiva.");
                    // Podrías retornar true o false aquí dependiendo de si consideras esto un "éxito"
                    return true; // Opcional: considerar éxito si ya estaba inactivo
                }
                dir.setActivoDireccion(0); // Marcar como inactivo
                int filas = dao.actualizar(dir); // Usar el método actualizar del DAO
                exito = (filas > 0);
            } else {
                Log.w(TAG, "No se puede desactivar, dirección no encontrada.");
                exito = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al desactivar dirección", e);
            exito = false;
        } finally {
            // No cerrar DB aquí
        }
        if (exito) {
            Log.i(TAG, "Dirección desactivada exitosamente.");
            // Recargar las listas para reflejar el cambio en la UI
            cargarDirecciones();
        }
        return exito;
    }

    /**
     * Reactiva una dirección previamente desactivada, estableciendo ACTIVO_DIRECCION = 1.
     * @param idCliente ID del cliente.
     * @param idDireccion ID de la dirección.
     * @return true si la reactivación fue exitosa, false en caso contrario.
     */
    public boolean reactivarDireccion(int idCliente, int idDireccion) {
        Log.i(TAG, "Intentando reactivar dirección: Cliente=" + idCliente + ", Direccion=" + idDireccion);
        SQLiteDatabase db = null;
        boolean exito = false;
        try {
            db = dbHelper.getWritableDatabase();
            DireccionDAO dao = new DireccionDAO(db);
            Direccion dir = dao.consultarPorId(idCliente, idDireccion); // Consultar

            if (dir != null) {
                if (dir.getActivoDireccion() == 1) {
                    Log.w(TAG, "La dirección ya estaba activa.");
                    return true; // Opcional: considerar éxito si ya estaba activo
                }
                dir.setActivoDireccion(1); // Marcar como activo
                int filas = dao.actualizar(dir); // Actualizar en BD
                exito = (filas > 0);
            } else {
                Log.w(TAG, "No se puede reactivar, dirección no encontrada.");
                exito = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al reactivar dirección", e);
            exito = false;
        } finally {
            // No cerrar DB aquí
        }
        if (exito) {
            Log.i(TAG, "Dirección reactivada exitosamente.");
            // Recargar las listas para reflejar el cambio
            cargarDirecciones();
        }
        return exito;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "DireccionViewModel cleared.");
    }
}