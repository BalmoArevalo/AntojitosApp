package sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento;

import androidx.lifecycle.ViewModel;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
public class DepartamentoViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Departamento>> listaDepartamentos = new MutableLiveData<>();

    public DepartamentoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Departamento>> getListaDepartamentos() {
        return listaDepartamentos;
    }

    public void cargarDepartamentos() {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        DepartamentoDAO dao = new DepartamentoDAO(db);
        List<Departamento> lista = dao.obtenerTodos();
        db.close();
        listaDepartamentos.setValue(lista);
    }

    public Departamento consultarDepartamentoPorId(int id) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        DepartamentoDAO dao = new DepartamentoDAO(db);
        Departamento departamento = dao.consultarPorId(id);
        db.close();
        return departamento;
    }

    public long insertarDepartamento(Departamento departamento) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getWritableDatabase();
        DepartamentoDAO dao = new DepartamentoDAO(db);
        long id = dao.insertar(departamento);
        db.close();
        return id;
    }

    public int actualizarDepartamento(Departamento departamento) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getWritableDatabase();
        DepartamentoDAO dao = new DepartamentoDAO(db);
        int filas = dao.actualizar(departamento);
        db.close();
        return filas;
    }

    public int eliminarDepartamento(int id) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getWritableDatabase();
        DepartamentoDAO dao = new DepartamentoDAO(db);
        int filas = dao.eliminar(id);
        db.close();
        return filas;
    }
}
