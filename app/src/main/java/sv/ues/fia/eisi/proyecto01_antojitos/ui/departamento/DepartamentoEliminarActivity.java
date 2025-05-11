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

public class DepartamentoEliminarActivity extends AppCompatActivity {

    private EditText editTextId;
    private Button btnEliminar;
    private DepartamentoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departamento_eliminar);

        editTextId = findViewById(R.id.editTextIdDepartamentoEliminar);
        btnEliminar = findViewById(R.id.btnEliminarDepartamento);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        dao = new DepartamentoDAO(db);

        btnEliminar.setOnClickListener(v -> eliminarDepartamento());
    }

    private void eliminarDepartamento() {
        try {
            int id = Integer.parseInt(editTextId.getText().toString().trim());

            int filas = dao.eliminar(id);
            if (filas > 0) {
                Toast.makeText(this, "Departamento eliminado", Toast.LENGTH_SHORT).show();
                editTextId.setText("");
            } else {
                Toast.makeText(this, "No se encontró el departamento", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show();
        }
    }
}