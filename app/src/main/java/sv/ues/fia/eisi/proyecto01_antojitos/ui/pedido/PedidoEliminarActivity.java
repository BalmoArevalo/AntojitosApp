package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

import java.util.HashMap;

public class PedidoEliminarActivity extends AppCompatActivity {

    private EditText editTextId;
    private Button btnBuscar, btnEliminar;
    private TextView textViewResultado;

    private HashMap<Integer, Pedido> pedidosMock;
    private Pedido pedidoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_eliminar);

        editTextId = findViewById(R.id.editTextIdEliminar);
        btnBuscar = findViewById(R.id.btnBuscarEliminar);
        btnEliminar = findViewById(R.id.btnEliminar);
        textViewResultado = findViewById(R.id.textViewResultado);

        // Datos simulados
        pedidosMock = new HashMap<>();
        Pedido p = new Pedido();
        p.setIdPedido(1);
        p.setIdCliente(101);
        p.setIdRepartidor(201);
        p.setEstadoPedido("Pendiente");
        p.setFechaHoraPedido("02/05/2025 13:00");
        pedidosMock.put(p.getIdPedido(), p);

        // Buscar pedido
        btnBuscar.setOnClickListener(v -> {
            String input = editTextId.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(this, "Ingrese un ID", Toast.LENGTH_SHORT).show();
                return;
            }

            int id = Integer.parseInt(input);
            if (pedidosMock.containsKey(id)) {
                pedidoActual = pedidosMock.get(id);
                String info = "Pedido encontrado:\n" +
                        "ID: " + pedidoActual.getIdPedido() + "\n" +
                        "Cliente: " + pedidoActual.getIdCliente() + "\n" +
                        "Repartidor: " + pedidoActual.getIdRepartidor() + "\n" +
                        "Estado: " + pedidoActual.getEstadoPedido();
                textViewResultado.setText(info);
                btnEliminar.setEnabled(true);
            } else {
                textViewResultado.setText("No se encontró un pedido con ese ID.");
                btnEliminar.setEnabled(false);
                pedidoActual = null;
            }
        });

        // Eliminar pedido
        btnEliminar.setOnClickListener(v -> {
            if (pedidoActual != null) {
                pedidosMock.remove(pedidoActual.getIdPedido());
                Toast.makeText(this, "Pedido eliminado correctamente", Toast.LENGTH_SHORT).show();
                textViewResultado.setText("");
                editTextId.setText("");
                btnEliminar.setEnabled(false);
            }
        });
    }
}


