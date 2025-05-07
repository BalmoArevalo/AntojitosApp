package sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class ClienteCrearActivity extends AppCompatActivity {

    private ClienteViewModel clienteViewModel;
    private TextInputEditText editTextNombre, editTextApellido, editTextTelefono;
    private MaterialButton btnGuardar, btnLimpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_crear);

        // Inicializar ViewModel
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);

        // Inicializar vistas
        inicializarVistas();

        // Configurar observadores
        configurarObservadores();

        // Configurar listeners
        configurarListeners();
    }

    private void inicializarVistas() {
        editTextNombre = findViewById(R.id.editTextNombreCliente);
        editTextApellido = findViewById(R.id.editTextApellidoCliente);
        editTextTelefono = findViewById(R.id.editTextTelefonoCliente);
        btnGuardar = findViewById(R.id.btnGuardarCliente);
        btnLimpiar = findViewById(R.id.btnLimpiarCampos);
    }

    private void configurarObservadores() {
        clienteViewModel.getMensajeError().observe(this, mensaje -> {
            if (mensaje != null) {
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
                clienteViewModel.limpiarError();
            }
        });

        clienteViewModel.getOperacionExitosa().observe(this, exitoso -> {
            if (exitoso != null && exitoso) {
                Toast.makeText(this, "Cliente guardado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            }
        });
    }

    private void configurarListeners() {
        btnGuardar.setOnClickListener(v -> guardarCliente());
        btnLimpiar.setOnClickListener(v -> limpiarCampos());
    }

    private void guardarCliente() {
        String nombre = editTextNombre.getText().toString().trim();
        String apellido = editTextApellido.getText().toString().trim();
        String telefono = editTextTelefono.getText().toString().trim();

        clienteViewModel.crearCliente(nombre, apellido, telefono);
    }

    private void limpiarCampos() {
        editTextNombre.setText("");
        editTextApellido.setText("");
        editTextTelefono.setText("");
        editTextNombre.requestFocus();
    }
}