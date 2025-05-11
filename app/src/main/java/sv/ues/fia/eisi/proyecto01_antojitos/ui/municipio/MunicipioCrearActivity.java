package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.db.MunicipioDAO;

public class MunicipioCrearActivity extends AppCompatActivity {

    private EditText editTextId, editTextNombre, editTextIdDepartamento;
    private Button btnGuardar;

    private MunicipioDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipio_crear);

        editTextId = findViewById(R.id.editTextIdMunicipio);
        editTextNombre = findViewById(R.id.editTextNombreMunicipio);
        editTextIdDepartamento = findViewById(R.id.editTextIdDepartamento);
        btnGuardar = findViewById(R.id.btnGuardarMunicipio);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        dao = new MunicipioDAO(db);

        btnGuardar.setOnClickListener(v -> {
            if (camposValidos()) {
                Municipio municipio = new Municipio();
                municipio.setIdMunicipio(Integer.parseInt(editTextId.getText().toString()));
                municipio.setNombreMunicipio(editTextNombre.getText().toString());
                municipio.setIdDepartamento(Integer.parseInt(editTextIdDepartamento.getText().toString()));

                long resultado = dao.insertar(municipio);
                if (resultado > 0) {
                    Toast.makeText(this, "Municipio creado correctamente", Toast.LENGTH_LONG).show();
                    limpiar();
                } else {
                    Toast.makeText(this, "Error al crear municipio", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean camposValidos() {
        return !editTextId.getText().toString().isEmpty() &&
                !editTextNombre.getText().toString().isEmpty() &&
                !editTextIdDepartamento.getText().toString().isEmpty();
    }

    private void limpiar() {
        editTextId.setText("");
        editTextNombre.setText("");
        editTextIdDepartamento.setText("");
    }
}
