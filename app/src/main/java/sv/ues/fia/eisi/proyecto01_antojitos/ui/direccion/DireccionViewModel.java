package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DireccionViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Direccion>> listaDirecciones = new MutableLiveData<>();

    public DireccionViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Direccion>> getListaDirecciones() {
        return listaDirecciones;
    }

    public void cargarDirecciones() {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        DireccionDAO dao = new DireccionDAO(db);
        listaDirecciones.setValue( dao.obtenerTodas() );
        db.close();
    }

    public Direccion consultarPorId(int idCliente, int idDireccion) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        Direccion dir = new DireccionDAO(db).consultarPorId(idCliente, idDireccion);
        db.close();
        return dir;
    }
}
