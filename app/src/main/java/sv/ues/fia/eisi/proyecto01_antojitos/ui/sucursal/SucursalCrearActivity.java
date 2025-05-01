package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

import android.database.sqlite.SQLiteDatabase;

public class SucursalCrearActivity extends AppCompatActivity {

    private EditText editIdSucursal, editIdDepartamento, editIdMunicipio, editIdDistrito, editIdUsuario;
    private EditText editNombreSucursal, editDireccionSucursal, editTelefonoSucursal;
    private EditText editHorarioApertura, editHorarioCierre;
    private Button btnGuardar, btnLimpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sucursal_crear);

        Toast.makeText(this, "Activity cargada", Toast.LENGTH_SHORT).show();

        // Asociar elementos visuales con sus IDs
        editIdSucursal = findViewById(R.id.editIdSucursal);
        editIdDepartamento = findViewById(R.id.editIdDepartamento);
        editIdMunicipio = findViewById(R.id.editIdMunicipio);
        editIdDistrito = findViewById(R.id.editIdDistrito);
        editIdUsuario = findViewById(R.id.editIdUsuario);
        editNombreSucursal = findViewById(R.id.editNombreSucursal);
        editDireccionSucursal = findViewById(R.id.editDireccionSucursal);
        editTelefonoSucursal = findViewById(R.id.editTelefonoSucursal);
        editHorarioApertura = findViewById(R.id.editHorarioApertura);
        editHorarioCierre = findViewById(R.id.editHorarioCierre);

        btnGuardar = findViewById(R.id.btnGuardarSucursal);
        btnLimpiar = findViewById(R.id.btnLimpiarCampos);

        btnGuardar.setOnClickListener(v -> guardarSucursal());
        btnLimpiar.setOnClickListener(v -> limpiarCampos());
    }

    private void guardarSucursal() {
        try {
            int idSucursal = Integer.parseInt(editIdSucursal.getText().toString());
            int idDepartamento = Integer.parseInt(editIdDepartamento.getText().toString());
            int idMunicipio = Integer.parseInt(editIdMunicipio.getText().toString());
            int idDistrito = Integer.parseInt(editIdDistrito.getText().toString());
            int idUsuario = Integer.parseInt(editIdUsuario.getText().toString());
            String nombre = editNombreSucursal.getText().toString();
            String direccion = editDireccionSucursal.getText().toString();
            String telefono = editTelefonoSucursal.getText().toString();
            String horaApertura = editHorarioApertura.getText().toString();
            String horaCierre = editHorarioCierre.getText().toString();

            if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || horaApertura.isEmpty() || horaCierre.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            SucursalDAO dao = new SucursalDAO(db);
            Sucursal sucursal = new Sucursal(
                    idSucursal, idDepartamento, idMunicipio, idDistrito, idUsuario,
                    nombre, direccion, telefono, horaApertura, horaCierre
            );

            long resultado = dao.insertar(sucursal);
            db.close();

            if (resultado != -1) {
                Toast.makeText(this, "Sucursal creada exitosamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            } else {
                Toast.makeText(this, "Error al insertar. Verifica si el ID ya existe.", Toast.LENGTH_LONG).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Verifica que los campos numéricos sean válidos", Toast.LENGTH_LONG).show();
        }
    }

    private void limpiarCampos() {
        editIdSucursal.setText("");
        editIdDepartamento.setText("");
        editIdMunicipio.setText("");
        editIdDistrito.setText("");
        editIdUsuario.setText("");
        editNombreSucursal.setText("");
        editDireccionSucursal.setText("");
        editTelefonoSucursal.setText("");
        editHorarioApertura.setText("");
        editHorarioCierre.setText("");
    }
}
