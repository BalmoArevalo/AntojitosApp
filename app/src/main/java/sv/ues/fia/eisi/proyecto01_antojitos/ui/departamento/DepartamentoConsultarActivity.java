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

public class DepartamentoConsultarActivity extends AppCompatActivity {

    private EditText editTextId, editTextNombre;
    private Button btnBuscar;
    private DepartamentoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departamento_consultar);

        editTextId = findViewById(R.id.editTextIdDepartamentoBuscar);
        btnBuscar = findViewById(R.id.btnBuscarDepartamento);

        SQLiteDatabase db = new DBHelper(this).getReadableDatabase();
        dao = new DepartamentoDAO(db);

        btnBuscar.setOnClickListener(v -> buscarDepartamento());
    }

    private void buscarDepartamento() {
        try {
            int id = Integer.parseInt(editTextId.getText().toString().trim());
            Departamento departamento = dao.consultarPorId(id);

            if (departamento != null) {
                editTextNombre.setText(departamento.getNombreDepartamento());
            } else {
                Toast.makeText(this, "Departamento no encontrado", Toast.LENGTH_SHORT).show();
                editTextNombre.setText("");
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show();
        }
    }
}