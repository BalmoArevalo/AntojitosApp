package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class PedidoConsultarActivity extends AppCompatActivity {

    private EditText editTextIdPedido;
    private Button btnBuscar;
    private TextView tvResultado;

    private PedidoDAO pedidoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_consultar);

        editTextIdPedido = findViewById(R.id.editTextIdPedido);
        btnBuscar = findViewById(R.id.btnBuscarPedido);
        tvResultado = findViewById(R.id.tvResultadoPedido);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        pedidoDAO = new PedidoDAO(db);

        btnBuscar.setOnClickListener(v -> buscarPedido());
    }

    private void buscarPedido() {
        String input = editTextIdPedido.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(this, "Ingrese un ID válido", Toast.LENGTH_SHORT).show();
            return;
        }

        int idPedido = Integer.parseInt(input);
        Pedido pedido = pedidoDAO.consultarPorId(idPedido);

        if (pedido != null) {
            String resultado = "ID Pedido: " + pedido.getIdPedido() +
                    "\nID Cliente: " + pedido.getIdCliente() +
                    "\nID Repartidor: " + pedido.getIdRepartidor() +
                    "\nID Tipo Evento: " + (pedido.getIdTipoEvento() == 0 ? "Ninguno" : pedido.getIdTipoEvento()) +
                    "\nFecha/Hora: " + pedido.getFechaHoraPedido() +
                    "\nEstado: " + pedido.getEstadoPedido();

            tvResultado.setText(resultado);
        } else {
            tvResultado.setText("No se encontró un pedido con ese ID.");
        }
    }
}
