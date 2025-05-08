package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class TipoEventoViewModel extends AndroidViewModel {
    private final MutableLiveData<List<TipoEvento>> listaTipoEventos = new MutableLiveData<>();

    public TipoEventoViewModel(@NonNull Application application) {
        super(application);
    }

    // Retorna la lista de tipo de eventos como LiveData
    public LiveData<List<TipoEvento>> getListaTipoEventos() {
        return listaTipoEventos;
    }

    // Carga todos los tipo de eventos desde la base de datos
    public void cargarTipoEventos() {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        TipoEventoDAO dao = new TipoEventoDAO(db);
        List<TipoEvento> lista = dao.obtenerTodos();
        db.close();
        listaTipoEventos.setValue(lista);
    }

    // Consulta un tipo de evento por su ID
    public TipoEvento consultarTipoEventoPorId(int id) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        TipoEventoDAO dao = new TipoEventoDAO(db);
        TipoEvento tipoEvento = dao.consultarPorId(id);
        db.close();
        return tipoEvento;
    }

    // Inserta un nuevo tipo de evento
    public long insertarTipoEvento(TipoEvento tipoEvento) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getWritableDatabase();
        TipoEventoDAO dao = new TipoEventoDAO(db);
        long id = dao.insertar(tipoEvento);
        db.close();
        return id;
    }

    // Actualiza un tipo de evento
    public int actualizarTipoEvento(TipoEvento tipoEvento) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getWritableDatabase();
        TipoEventoDAO dao = new TipoEventoDAO(db);
        int filas = dao.actualizar(tipoEvento);
        db.close();
        return filas;
    }

    // Elimina un tipo de evento por su ID
    public int eliminarTipoEvento(int id) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getWritableDatabase();
        TipoEventoDAO dao = new TipoEventoDAO(db);
        int filas = dao.eliminar(id);
        db.close();
        return filas;
    }
}
