package sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class ClienteEditarActivity extends AppCompatActivity {

    private ClienteViewModel clienteViewModel;
    private AutoCompleteTextView spinnerClientes;
    private MaterialCardView cardFormulario;
    private TextInputEditText editTextNombre, editTextApellido, editTextTelefono;
    private MaterialButton btnGuardar, btnCancelar;
    private List<Cliente> listaClientes;
    private Cliente clienteSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_editar);

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
        cardFormulario = findViewById(R.id.cardFormulario);
        editTextNombre = findViewById(R.id.editTextNombreCliente);
        editTextApellido = findViewById(R.id.editTextApellidoCliente);
        editTextTelefono = findViewById(R.id.editTextTelefonoCliente);
        btnGuardar = findViewById(R.id.btnGuardarCambios);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Configurar listeners
        spinnerClientes.setOnItemClickListener((parent, view, position, id) -> {
            clienteSeleccionado = listaClientes.get(position);
            mostrarFormularioEdicion(clienteSeleccionado);
        });

        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnCancelar.setOnClickListener(v -> cancelarEdicion());
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
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        // Observar operación exitosa
        clienteViewModel.getOperacionExitosa().observe(this, exitoso -> {
            if (exitoso != null && exitoso) {
                Toast.makeText(this, "Cliente actualizado exitosamente", Toast.LENGTH_SHORT).show();
                cardFormulario.setVisibility(View.GONE);
                spinnerClientes.setText("");
                clienteViewModel.cargarClientes(); // Recargar la lista
            }
        });
    }

    private void configurarSpinnerClientes(List<Cliente> clientes) {
        List<String> nombresClientes = new ArrayList<>();
        for (Cliente cliente : clientes) {
            nombresClientes.add(String.format("%s - %s %s",
                    cliente.getIdCliente(),
                    cliente.getNombreCliente(),
                    cliente.getApellidoCliente()));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                nombresClientes
        );

        spinnerClientes.setAdapter(adapter);
    }

    private void mostrarFormularioEdicion(Cliente cliente) {
        editTextNombre.setText(cliente.getNombreCliente());
        editTextApellido.setText(cliente.getApellidoCliente());
        editTextTelefono.setText(cliente.getTelefonoCliente());
        cardFormulario.setVisibility(View.VISIBLE);
    }

    private void guardarCambios() {
        if (clienteSeleccionado != null) {
            String nombre = editTextNombre.getText().toString().trim();
            String apellido = editTextApellido.getText().toString().trim();
            String telefono = editTextTelefono.getText().toString().trim();

            clienteViewModel.actualizarCliente(
                    clienteSeleccionado.getIdCliente(),
                    nombre,
                    apellido,
                    telefono
            );
        }
    }

    private void cancelarEdicion() {
        cardFormulario.setVisibility(View.GONE);
        spinnerClientes.setText("");
        clienteSeleccionado = null;
    }
}