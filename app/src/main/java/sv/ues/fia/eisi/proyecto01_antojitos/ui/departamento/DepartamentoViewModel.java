package sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DepartamentoViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Departamento>> listaDepartamentos = new MutableLiveData<>();

    public DepartamentoViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Observa la lista de departamentos activos
     */
    public LiveData<List<Departamento>> getListaDepartamentos() {
        return listaDepartamentos;
    }

    /**
     * Carga todos los departamentos activos desde la base de datos
     */
    public void cargarDepartamentos() {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        DepartamentoDAO dao = new DepartamentoDAO(db);
        List<Departamento> activos = dao.obtenerActivos();
        db.close();
        listaDepartamentos.setValue(activos);
    }

    /**
     * Consulta un departamento por su ID (activo o no)
     */
    public Departamento consultarDepartamentoPorId(int id) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        DepartamentoDAO dao = new DepartamentoDAO(db);
        Departamento departamento = dao.consultarPorId(id);
        db.close();
        return departamento;
    }

    /**
     * Inserta un nuevo departamento
     */
    public long insertarDepartamento(Departamento departamento) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getWritableDatabase();
        DepartamentoDAO dao = new DepartamentoDAO(db);
        long resultado = dao.insertar(departamento);
        db.close();
        return resultado;
    }

    /**
     * Actualiza un departamento (nombre y/o estado activo)
     */
    public int actualizarDepartamento(Departamento departamento) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getWritableDatabase();
        DepartamentoDAO dao = new DepartamentoDAO(db);
        int filas = dao.actualizar(departamento);
        db.close();
        return filas;
    }

    /**
     * Elimina físicamente un departamento (no recomendado si se usa borrado lógico)
     */
    public int eliminarDepartamento(int id) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getWritableDatabase();
        DepartamentoDAO dao = new DepartamentoDAO(db);
        int filas = dao.eliminar(id);
        db.close();
        return filas;
    }

    /**
     * Obtiene la lista de departamentos activos directamente (sin LiveData)
     */
    public List<Departamento> obtenerDepartamentosActivosDirecto() {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        DepartamentoDAO dao = new DepartamentoDAO(db);
        List<Departamento> activos = dao.obtenerActivos();
        db.close();
        return activos;
    }
}
