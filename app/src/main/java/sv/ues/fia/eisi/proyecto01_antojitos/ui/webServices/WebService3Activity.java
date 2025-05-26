package sv.ues.fia.eisi.proyecto01_antojitos.ui.webServices;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.network.helpers.ClienteHelper;

public class WebService3Activity extends AppCompatActivity {

    TextInputEditText etNombreCliente, etApellidoCliente, etTelefonoCliente;
    CheckBox cbActivoCliente;
    MaterialButton btnCrearCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service3);

        etNombreCliente = findViewById(R.id.etNombreCliente);
        etApellidoCliente = findViewById(R.id.etApellidoCliente);
        etTelefonoCliente = findViewById(R.id.etTelefonoCliente);
        cbActivoCliente = findViewById(R.id.cbActivoCliente);
        btnCrearCliente = findViewById(R.id.btnCrearCliente);

        btnCrearCliente.setOnClickListener(v -> {
            String nombre = etNombreCliente.getText().toString().trim();
            String apellido = etApellidoCliente.getText().toString().trim();
            String telefono = etTelefonoCliente.getText().toString().trim();
            int activo = cbActivoCliente.isChecked() ? 1 : 0;

            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(this, "⚠️ Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            ClienteHelper.crearCliente(
                    this,
                    telefono,
                    nombre,
                    apellido,
                    activo,
                    () -> {
                        etNombreCliente.setText("");
                        etApellidoCliente.setText("");
                        etTelefonoCliente.setText("");
                        cbActivoCliente.setChecked(true);
                        etNombreCliente.requestFocus();
                    },
                    btnCrearCliente // 👈 Esto es el anchorView
            );
        });
    }
}
