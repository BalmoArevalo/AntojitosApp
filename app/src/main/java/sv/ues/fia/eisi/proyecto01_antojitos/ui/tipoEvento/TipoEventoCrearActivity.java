package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class TipoEventoCrearActivity extends AppCompatActivity {

    private EditText editTextId, editTextNombre, editTextDescripcion, editTextMontoMin, editTextMontoMax;
    private Button btnGuardar;

    private TipoEventoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_evento_crear);

        editTextId = findViewById(R.id.editTextIdTipoEvento);
        editTextNombre = findViewById(R.id.editTextNombreTipoEvento);
        editTextDescripcion = findViewById(R.id.editTextDescripcionTipoEvento);
        editTextMontoMin = findViewById(R.id.editTextMontoMinimo);
        editTextMontoMax = findViewById(R.id.editTextMontoMaximo);
        btnGuardar = findViewById(R.id.btnGuardarTipoEvento);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        dao = new TipoEventoDAO(db);

        btnGuardar.setOnClickListener(v -> {
            if (camposValidos()) {
                TipoEvento tipoEvento = new TipoEvento();
                tipoEvento.setIdTipoEvento(Integer.parseInt(editTextId.getText().toString()));
                tipoEvento.setNombreTipoEvento(editTextNombre.getText().toString());
                tipoEvento.setDescripcionTipoEvento(editTextDescripcion.getText().toString());
                tipoEvento.setMontoMinimo(Double.parseDouble(editTextMontoMin.getText().toString()));
                tipoEvento.setMontoMaximo(Double.parseDouble(editTextMontoMax.getText().toString()));

                long resultado = dao.insertar(tipoEvento);
                if (resultado > 0) {
                    Toast.makeText(this, "Tipo de Evento creado correctamente", Toast.LENGTH_LONG).show();
                    limpiar();
                } else {
                    Toast.makeText(this, "Error al crear", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean camposValidos() {
        return !editTextId.getText().toString().isEmpty() &&
                !editTextNombre.getText().toString().isEmpty() &&
                !editTextDescripcion.getText().toString().isEmpty() &&
                !editTextMontoMin.getText().toString().isEmpty() &&
                !editTextMontoMax.getText().toString().isEmpty();
    }

    private void limpiar() {
        editTextId.setText("");
        editTextNombre.setText("");
        editTextDescripcion.setText("");
        editTextMontoMin.setText("");
        editTextMontoMax.setText("");
    }
}
