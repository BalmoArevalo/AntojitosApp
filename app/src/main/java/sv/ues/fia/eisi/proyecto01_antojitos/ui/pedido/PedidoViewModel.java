package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import android.database.sqlite.SQLiteDatabase;

public class PedidoViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Pedido>> listaPedidos = new MutableLiveData<>();

    public PedidoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Pedido>> getListaPedidos() {
        return listaPedidos;
    }

    public void cargarPedidos() {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        PedidoDAO dao = new PedidoDAO(db);
        List<Pedido> lista = dao.obtenerTodos();
        db.close();
        listaPedidos.setValue(lista);
    }

    public Pedido consultarPedidoPorId(int id) {
        DBHelper helper = new DBHelper(getApplication());
        SQLiteDatabase db = helper.getReadableDatabase();
        PedidoDAO dao = new PedidoDAO(db);
        Pedido pedido = dao.consultarPorId(id);
        db.close();
        return pedido;
    }
}
