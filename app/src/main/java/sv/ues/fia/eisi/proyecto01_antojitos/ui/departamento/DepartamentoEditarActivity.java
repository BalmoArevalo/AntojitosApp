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

public class DepartamentoEditarActivity extends AppCompatActivity {

    private EditText editTextId, editTextNombre;
    private Button btnActualizar;
    private DepartamentoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departamento_editar);

        editTextId = findViewById(R.id.editTextIdDepartamentoEditar);
        editTextNombre = findViewById(R.id.editTextNombreDepartamentoEditar);
        btnActualizar = findViewById(R.id.btnActualizarDepartamento);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        dao = new DepartamentoDAO(db);

        btnActualizar.setOnClickListener(v -> actualizarDepartamento());
    }

    private void actualizarDepartamento() {
        try {
            int id = Integer.parseInt(editTextId.getText().toString().trim());
            String nombre = editTextNombre.getText().toString().trim();

            if (nombre.isEmpty()) {
                Toast.makeText(this, "Nombre vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            Departamento departamento = new Departamento();
            departamento.setIdDepartamento(id);
            departamento.setNombreDepartamento(nombre);

            int filas = dao.actualizar(departamento);

            if (filas > 0) {
                Toast.makeText(this, "Departamento actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se encontró el departamento", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show();
        }
    }
}