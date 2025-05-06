package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class TipoEventoConsultarActivity extends AppCompatActivity {

    private EditText editTextIdBuscar, editTextNombre, editTextDescripcion, editTextMontoMin, editTextMontoMax;
    private Button btnBuscar;

    private TipoEventoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_evento_consultar);

        editTextIdBuscar = findViewById(R.id.editTextIdTipoEventoBuscar);
        editTextNombre = findViewById(R.id.editTextNombreTipoEventoConsultado);
        editTextDescripcion = findViewById(R.id.editTextDescripcionTipoEventoConsultado);
        editTextMontoMin = findViewById(R.id.editTextMontoMinimoConsultado);
        editTextMontoMax = findViewById(R.id.editTextMontoMaximoConsultado);
        btnBuscar = findViewById(R.id.btnBuscarTipoEvento);

        SQLiteDatabase db = new DBHelper(this).getReadableDatabase();
        dao = new TipoEventoDAO(db);

        btnBuscar.setOnClickListener(v -> {
            String idTexto = editTextIdBuscar.getText().toString();
            if (!idTexto.isEmpty()) {
                int id = Integer.parseInt(idTexto);
                TipoEvento tipoEvento = dao.consultarPorId(id);
                if (tipoEvento != null) {
                    editTextNombre.setText(tipoEvento.getNombreTipoEvento());
                    editTextDescripcion.setText(tipoEvento.getDescripcionTipoEvento());
                    editTextMontoMin.setText(String.valueOf(tipoEvento.getMontoMinimo()));
                    editTextMontoMax.setText(String.valueOf(tipoEvento.getMontoMaximo()));
                } else {
                    Toast.makeText(this, "No se encontró el Tipo de Evento", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                }
            } else {
                Toast.makeText(this, "Ingrese un ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limpiarCampos() {
        editTextNombre.setText("");
        editTextDescripcion.setText("");
        editTextMontoMin.setText("");
        editTextMontoMax.setText("");
    }
}
