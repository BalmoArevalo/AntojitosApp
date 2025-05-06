package sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.database.sqlite.SQLiteDatabase;
import android.widget.*;
import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DepartamentoCrearActivity extends AppCompatActivity {

    private EditText editTextNombre;
    private Button btnGuardar;
    private DepartamentoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departamento_crear);

        editTextNombre = findViewById(R.id.editTextNombreDepartamento);
        btnGuardar = findViewById(R.id.btnGuardarDepartamento);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        dao = new DepartamentoDAO(db);

        btnGuardar.setOnClickListener(v -> guardarDepartamento());
    }

    private void guardarDepartamento() {
        String nombre = editTextNombre.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese el nombre del departamento", Toast.LENGTH_SHORT).show();
            return;
        }

        Departamento departamento = new Departamento();
        departamento.setNombreDepartamento(nombre);

        long id = dao.insertar(departamento);
        if (id > 0) {
            Toast.makeText(this, "Departamento creado con ID: " + id, Toast.LENGTH_LONG).show();
            editTextNombre.setText("");
        } else {
            Toast.makeText(this, "Error al crear departamento", Toast.LENGTH_SHORT).show();
        }
    }
}