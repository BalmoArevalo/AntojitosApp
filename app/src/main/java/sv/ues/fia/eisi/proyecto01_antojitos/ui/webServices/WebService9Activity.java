package sv.ues.fia.eisi.proyecto01_antojitos.ui.webServices;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.network.helpers.RepartidorHelper;

public class WebService9Activity extends AppCompatActivity {

    EditText etNombre, etApellido, etTelefono, etTipoVehiculo;
    CheckBox cbDisponible, cbActivo;
    Button btnCrearRepartidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service9);

        etNombre = findViewById(R.id.txtNombre);
        etApellido = findViewById(R.id.txtApellido);
        etTelefono = findViewById(R.id.txtTelefono);
        etTipoVehiculo = findViewById(R.id.txtTipoVehiculo);
        cbDisponible = findViewById(R.id.chkDisponible);
        cbActivo = findViewById(R.id.chkActivo);
        btnCrearRepartidor = findViewById(R.id.btnGuardar);

        btnCrearRepartidor.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String apellido = etApellido.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String tipoVehiculo = etTipoVehiculo.getText().toString().trim();
            int disponible = cbDisponible.isChecked() ? 1 : 0;
            int activo = cbActivo.isChecked() ? 1 : 0;

            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || tipoVehiculo.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            RepartidorHelper.crearRepartidor(this, nombre, apellido, telefono, tipoVehiculo, disponible, activo);
        });
    }
}
