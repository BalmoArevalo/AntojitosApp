package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class PedidoEliminarActivity extends AppCompatActivity {

    private EditText editTextIdEliminar;
    private TextView textViewResultado;
    private Button btnBuscar, btnEliminar;
    private PedidoDAO pedidoDAO;
    private Pedido pedidoEncontrado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_eliminar);

        editTextIdEliminar = findViewById(R.id.editTextIdEliminar);
        textViewResultado = findViewById(R.id.textViewResultadoEliminar);
        btnBuscar = findViewById(R.id.btnBuscarEliminar);
        btnEliminar = findViewById(R.id.btnEliminar);

        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        pedidoDAO = new PedidoDAO(db);

        btnBuscar.setOnClickListener(v -> buscarPedido());
        btnEliminar.setOnClickListener(v -> eliminarPedido());
    }

    private void buscarPedido() {
        String idStr = editTextIdEliminar.getText().toString().trim();
        if (idStr.isEmpty()) {
            Toast.makeText(this, "Ingrese un ID", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = Integer.parseInt(idStr);
        pedidoEncontrado = pedidoDAO.consultarPorId(id);

        if (pedidoEncontrado != null) {
            String info = "Pedido encontrado:\n" +
                    "ID: " + pedidoEncontrado.getIdPedido() + "\n" +
                    "Cliente: " + pedidoEncontrado.getIdCliente() + "\n" +
                    "Repartidor: " + pedidoEncontrado.getIdRepartidor() + "\n" +
                    "Estado: " + pedidoEncontrado.getEstadoPedido();
            textViewResultado.setText(info);
            btnEliminar.setEnabled(true);
        } else {
            textViewResultado.setText("No se encontró el pedido.");
            btnEliminar.setEnabled(false);
        }
    }

    private void eliminarPedido() {
        if (pedidoEncontrado != null) {
            int filas = pedidoDAO.eliminar(pedidoEncontrado.getIdPedido());
            if (filas > 0) {
                Toast.makeText(this, "Pedido eliminado correctamente", Toast.LENGTH_SHORT).show();
                textViewResultado.setText("");
                editTextIdEliminar.setText("");
                btnEliminar.setEnabled(false);
                pedidoEncontrado = null;
            } else {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
