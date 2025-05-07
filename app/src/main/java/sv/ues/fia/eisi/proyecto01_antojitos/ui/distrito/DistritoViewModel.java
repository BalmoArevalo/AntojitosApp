package sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito.DistritoDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito.Distrito;

public class DistritoViewModel extends AndroidViewModel {

    private DistritoDAO distritoDAO;
    private MutableLiveData<List<Distrito>> distritos;
    private MutableLiveData<String> mensajeError;
    private MutableLiveData<Boolean> operacionExitosa;
    private SQLiteDatabase db;

    public DistritoViewModel(@NonNull Application application) {
        super(application);
        DBHelper dbHelper = new DBHelper(application);
        db = dbHelper.getWritableDatabase();
        distritoDAO = new DistritoDAO(db);
        distritos = new MutableLiveData<>();
        mensajeError = new MutableLiveData<>();
        operacionExitosa = new MutableLiveData<>();
    }

    public LiveData<List<Distrito>> getDistritos() {
        return distritos;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<Boolean> getOperacionExitosa() {
        return operacionExitosa;
    }

    public void cargarDistritos() {
        try {
            List<Distrito> listaDistritos = distritoDAO.obtenerTodosLosDistritos();
            distritos.setValue(listaDistritos);
        } catch (Exception e) {
            mensajeError.setValue("Error al cargar los distritos: " + e.getMessage());
        }
    }

    public void cargarDistritosActivos() {
        try {
            List<Distrito> listaDistritos = distritoDAO.obtenerDistritosActivos();
            distritos.setValue(listaDistritos);
        } catch (Exception e) {
            mensajeError.setValue("Error al cargar los distritos activos: " + e.getMessage());
        }
    }

    public void cargarDistritosPorMunicipio(int idDepartamento, int idMunicipio) {
        try {
            List<Distrito> listaDistritos = distritoDAO.obtenerDistritosPorMunicipio(idDepartamento, idMunicipio);
            distritos.setValue(listaDistritos);
        } catch (Exception e) {
            mensajeError.setValue("Error al cargar los distritos del municipio: " + e.getMessage());
        }
    }

    public void agregarDistrito(Distrito distrito) {
        try {
            // Verificar que los IDs no sean 0 o negativos
            if (distrito.getIdDepartamento() <= 0 || distrito.getIdMunicipio() <= 0 ||
                    distrito.getIdDistrito() <= 0) {
                mensajeError.setValue("Los IDs deben ser números positivos");
                return;
            }

            // Verificar que el nombre y código postal no estén vacíos
            if (distrito.getNombreDistrito() == null || distrito.getNombreDistrito().trim().isEmpty()) {
                mensajeError.setValue("El nombre del distrito no puede estar vacío");
                return;
            }

            if (distrito.getCodigoPostal() == null || distrito.getCodigoPostal().trim().isEmpty()) {
                mensajeError.setValue("El código postal no puede estar vacío");
                return;
            }

            long resultado = distritoDAO.insertarDistrito(distrito);
            if (resultado != -1) {
                operacionExitosa.setValue(true);
                cargarDistritos();
            } else {
                mensajeError.setValue("Error al insertar el distrito");
            }
        } catch (Exception e) {
            mensajeError.setValue("Error al agregar el distrito: " + e.getMessage());
            operacionExitosa.setValue(false);
        }
    }

    public void actualizarDistrito(Distrito distrito) {
        try {
            // Verificar que los IDs no sean 0 o negativos
            if (distrito.getIdDepartamento() <= 0 || distrito.getIdMunicipio() <= 0 ||
                    distrito.getIdDistrito() <= 0) {
                mensajeError.setValue("Los IDs deben ser números positivos");
                return;
            }

            // Verificar que el nombre y código postal no estén vacíos
            if (distrito.getNombreDistrito() == null || distrito.getNombreDistrito().trim().isEmpty()) {
                mensajeError.setValue("El nombre del distrito no puede estar vacío");
                return;
            }

            if (distrito.getCodigoPostal() == null || distrito.getCodigoPostal().trim().isEmpty()) {
                mensajeError.setValue("El código postal no puede estar vacío");
                return;
            }

            int filasActualizadas = distritoDAO.actualizarDistrito(distrito);
            if (filasActualizadas > 0) {
                operacionExitosa.setValue(true);
                cargarDistritos();
            } else {
                mensajeError.setValue("No se encontró el distrito para actualizar");
            }
        } catch (Exception e) {
            mensajeError.setValue("Error al actualizar el distrito: " + e.getMessage());
            operacionExitosa.setValue(false);
        }
    }

    public void eliminarDistrito(int idDepartamento, int idMunicipio, int idDistrito) {
        try {
            int filasActualizadas = distritoDAO.eliminarDistrito(idDepartamento, idMunicipio, idDistrito);
            if (filasActualizadas > 0) {
                operacionExitosa.setValue(true);
                cargarDistritos();
            } else {
                mensajeError.setValue("No se encontró el distrito para eliminar");
            }
        } catch (Exception e) {
            mensajeError.setValue("Error al eliminar el distrito: " + e.getMessage());
            operacionExitosa.setValue(false);
        }
    }

    // Agregar en DistritoViewModel.java
    public void desactivarDistrito(Distrito distrito) {

        try {
            SQLiteDatabase db = new DBHelper(getApplication()).getWritableDatabase();

            String sql = "UPDATE DISTRITO SET ACTIVO_DISTRITO = 0 " +
                    "WHERE ID_DEPARTAMENTO = ? " +
                    "AND ID_MUNICIPIO = ? " +
                    "AND ID_DISTRITO = ?";

            Object[] bindArgs = {
                    distrito.getIdDepartamento(),
                    distrito.getIdMunicipio(),
                    distrito.getIdDistrito()
            };

            db.execSQL(sql, bindArgs);

            operacionExitosa.postValue(true);
            mensajeError.postValue(null);
        } catch (Exception e) {
            operacionExitosa.postValue(false);
            mensajeError.postValue("Error al desactivar distrito: " + e.getMessage());
        }
    }

    public void obtenerDistrito(int idDepartamento, int idMunicipio, int idDistrito) {
        try {
            Distrito distrito = distritoDAO.obtenerDistritoPorId(idDepartamento, idMunicipio, idDistrito);
            if (distrito != null) {
                List<Distrito> listaDistrito = List.of(distrito);
                distritos.setValue(listaDistrito);
            } else {
                mensajeError.setValue("No se encontró el distrito especificado");
            }
        } catch (Exception e) {
            mensajeError.setValue("Error al obtener el distrito: " + e.getMessage());
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}