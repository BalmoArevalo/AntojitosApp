package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class TipoEventoEditarActivity extends AppCompatActivity {

    private EditText editTextIdTipoEvento, editTextNombre, editTextDescripcion, editTextMontoMinimo, editTextMontoMaximo;
    private Button buttonActualizar;
    private TipoEventoDAO tipoEventoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_evento_editar);

        editTextIdTipoEvento = findViewById(R.id.editTextIdTipoEvento);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        editTextMontoMinimo = findViewById(R.id.editTextMontoMinimo);
        editTextMontoMaximo = findViewById(R.id.editTextMontoMaximo);
        buttonActualizar = findViewById(R.id.buttonActualizar);

        tipoEventoDAO = new TipoEventoDAO(new DBHelper(this).getWritableDatabase());

        buttonActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarTipoEvento();
            }
        });
    }

    private void editarTipoEvento() {
        String idTipoEventoStr = editTextIdTipoEvento.getText().toString();
        String nombre = editTextNombre.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();
        String montoMinimoStr = editTextMontoMinimo.getText().toString();
        String montoMaximoStr = editTextMontoMaximo.getText().toString();

        if (idTipoEventoStr.isEmpty() || nombre.isEmpty() || descripcion.isEmpty() || montoMinimoStr.isEmpty() || montoMaximoStr.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int idTipoEvento = Integer.parseInt(idTipoEventoStr);
        double montoMinimo = Double.parseDouble(montoMinimoStr);
        double montoMaximo = Double.parseDouble(montoMaximoStr);

        TipoEvento tipoEvento = new TipoEvento();
        tipoEvento.setIdTipoEvento(idTipoEvento);
        tipoEvento.setNombreTipoEvento(nombre);
        tipoEvento.setDescripcionTipoEvento(descripcion);
        tipoEvento.setMontoMinimo(montoMinimo);
        tipoEvento.setMontoMaximo(montoMaximo);

        int filasActualizadas = tipoEventoDAO.actualizar(tipoEvento);

        if (filasActualizadas > 0) {
            Toast.makeText(this, "Tipo de Evento actualizado con éxito", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se encontró el Tipo de Evento", Toast.LENGTH_SHORT).show();
        }
    }
}
