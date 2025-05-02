package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

import java.util.HashMap;

public class PedidoConsultarActivity extends AppCompatActivity {

    private EditText editTextIdPedido;
    private Button btnBuscar;
    private TextView tvResultado;

    // Simulación de pedidos
    private HashMap<Integer, Pedido> pedidosMock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_consultar);

        editTextIdPedido = findViewById(R.id.editTextIdPedido);
        btnBuscar = findViewById(R.id.btnBuscarPedido);
        tvResultado = findViewById(R.id.tvResultadoPedido);

        inicializarMockData();

        btnBuscar.setOnClickListener(v -> buscarPedido());
    }

    private void inicializarMockData() {
        pedidosMock = new HashMap<>();

        Pedido p1 = new Pedido();
        p1.setIdPedido(1);
        p1.setIdCliente(101);
        p1.setIdTipoEvento(1);
        p1.setIdRepartidor(201);
        p1.setFechaHoraPedido("01/05/2025 15:30");
        p1.setEstadoPedido("Pendiente");

        Pedido p2 = new Pedido();
        p2.setIdPedido(1);
        p2.setIdCliente(102);
        p2.setIdTipoEvento(0); // Sin evento
        p2.setIdRepartidor(202);
        p2.setFechaHoraPedido("01/05/2025 16:00");
        p2.setEstadoPedido("Despachado");

        pedidosMock.put(p1.getIdPedido(), p1);
        pedidosMock.put(p2.getIdPedido(), p2);
    }

    private void buscarPedido() {
        String input = editTextIdPedido.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(this, "Ingrese un ID válido", Toast.LENGTH_SHORT).show();
            return;
        }

        int idBuscado = Integer.parseInt(input);

        if (pedidosMock.containsKey(idBuscado)) {
            Pedido p = pedidosMock.get(idBuscado);
            String detalle = "ID Pedido: " + p.getIdPedido() +
                    "\nID Cliente: " + p.getIdCliente() +
                    "\nID Repartidor: " + p.getIdRepartidor() +
                    "\nID Tipo Evento: " + (p.getIdTipoEvento() == 0 ? "Ninguno" : p.getIdTipoEvento()) +
                    "\nFecha/Hora: " + p.getFechaHoraPedido() +
                    "\nEstado: " + p.getEstadoPedido();

            tvResultado.setText(detalle);
        } else {
            tvResultado.setText("No se encontró un pedido con ese ID.");
        }
    }
}
