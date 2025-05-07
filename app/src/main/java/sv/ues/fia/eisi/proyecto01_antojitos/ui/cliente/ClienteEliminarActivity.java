package sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class ClienteEliminarActivity extends AppCompatActivity {

    private ClienteViewModel clienteViewModel;
    private AutoCompleteTextView spinnerClientes;
    private MaterialCardView cardConfirmacion;
    private TextView tvConfirmacion;
    private MaterialButton btnConfirmarEliminar, btnCancelar;
    private List<Cliente> listaClientes;
    private Cliente clienteSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_eliminar);

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
        cardConfirmacion = findViewById(R.id.cardConfirmacion);
        tvConfirmacion = findViewById(R.id.tvConfirmacion);
        btnConfirmarEliminar = findViewById(R.id.btnConfirmarEliminar);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Configurar listeners
        spinnerClientes.setOnItemClickListener((parent, view, position, id) -> {
            clienteSeleccionado = listaClientes.get(position);
            mostrarConfirmacion(clienteSeleccionado);
        });

        btnConfirmarEliminar.setOnClickListener(v -> confirmarEliminacion());
        btnCancelar.setOnClickListener(v -> cancelarEliminacion());
    }

    private void configurarObservadores() {
        // Observar la lista de clientes
        clienteViewModel.getClientes().observe(this, clientes -> {
            if (clientes != null && !clientes.isEmpty()) {
                listaClientes = clientes;
                configurarSpinnerClientes(clientes);
            } else {
                Toast.makeText(this, "No hay clientes registrados", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Observar mensajes de error
        clienteViewModel.getMensajeError().observe(this, mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            }
        });

        // Observar operación exitosa
        clienteViewModel.getOperacionExitosa().observe(this, exitoso -> {
            if (exitoso != null && exitoso) {
                Toast.makeText(this, "Cliente eliminado exitosamente", Toast.LENGTH_SHORT).show();
                cardConfirmacion.setVisibility(View.GONE);
                spinnerClientes.setText("");
                clienteViewModel.cargarClientes();
            }
        });
    }

    private void configurarSpinnerClientes(List<Cliente> clientes) {
        List<String> nombresClientes = new ArrayList<>();
        for (Cliente cliente : clientes) {
            nombresClientes.add(String.format("%s %s - %s",
                    cliente.getNombreCliente(),
                    cliente.getApellidoCliente(),
                    cliente.getTelefonoCliente()));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                nombresClientes
        );

        spinnerClientes.setAdapter(adapter);
    }

    private void mostrarConfirmacion(Cliente cliente) {
        String mensaje = String.format("¿Está seguro que desea eliminar al cliente?\n\n" +
                        "Nombre: %s %s\n" +
                        "Teléfono: %s",
                cliente.getNombreCliente(),
                cliente.getApellidoCliente(),
                cliente.getTelefonoCliente());

        tvConfirmacion.setText(mensaje);
        cardConfirmacion.setVisibility(View.VISIBLE);
    }

    private void confirmarEliminacion() {
        if (clienteSeleccionado != null) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Confirmar eliminación")
                    .setMessage("Esta acción no se puede deshacer. ¿Desea continuar?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        clienteViewModel.eliminarCliente(clienteSeleccionado.getIdCliente());
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        }
    }

    private void cancelarEliminacion() {
        cardConfirmacion.setVisibility(View.GONE);
        spinnerClientes.setText("");
        clienteSeleccionado = null;
    }
}