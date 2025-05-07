package sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class ClienteConsultarActivity extends AppCompatActivity {

    private ClienteViewModel clienteViewModel;
    private AutoCompleteTextView spinnerClientes;
    private MaterialCardView cardResultados;
    private TextView tvIdCliente, tvNombreCliente, tvApellidoCliente, tvTelefonoCliente;
    private List<Cliente> listaClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_consultar);

        // Inicializar ViewModel
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);

        // Inicializar vistas
        inicializarVistas();

        // Configurar observadores
        configurarObservadores();

        // Cargar clientes
        clienteViewModel.cargarClientes();
    }

    private void inicializarVistas() {
        spinnerClientes = findViewById(R.id.spinnerClientes);
        cardResultados = findViewById(R.id.cardResultados);
        tvIdCliente = findViewById(R.id.tvIdCliente);
        tvNombreCliente = findViewById(R.id.tvNombreCliente);
        tvApellidoCliente = findViewById(R.id.tvApellidoCliente);
        tvTelefonoCliente = findViewById(R.id.tvTelefonoCliente);

        // Configurar listener del spinner
        spinnerClientes.setOnItemClickListener((parent, view, position, id) -> {
            Cliente clienteSeleccionado = listaClientes.get(position);
            mostrarDatosCliente(clienteSeleccionado);
        });
    }

    private void configurarObservadores() {
        // Observar la lista de clientes
        clienteViewModel.getClientes().observe(this, clientes -> {
            if (clientes != null && !clientes.isEmpty()) {
                listaClientes = clientes;
                configurarSpinnerClientes(clientes);
            } else {
                Toast.makeText(this, "No hay clientes registrados", Toast.LENGTH_SHORT).show();
            }
        });

        // Observar mensajes de error
        clienteViewModel.getMensajeError().observe(this, mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarSpinnerClientes(List<Cliente> clientes) {
        List<String> nombresClientes = new ArrayList<>();
        for (Cliente cliente : clientes) {
            // Formato: "ID - Nombre Apellido"
            nombresClientes.add(String.format("%s- %s %s",
                    cliente.getIdCliente(),
                    cliente.getNombreCliente(),
                    cliente.getApellidoCliente()));
        }

        // Usar un layout predefinido de Android
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                nombresClientes
        );

        spinnerClientes.setAdapter(adapter);
    }

    private void mostrarDatosCliente(Cliente cliente) {
        tvIdCliente.setText(String.valueOf(cliente.getIdCliente()));
        tvNombreCliente.setText(cliente.getNombreCliente());
        tvApellidoCliente.setText(cliente.getApellidoCliente());
        tvTelefonoCliente.setText(cliente.getTelefonoCliente());
        cardResultados.setVisibility(View.VISIBLE);
    }
}