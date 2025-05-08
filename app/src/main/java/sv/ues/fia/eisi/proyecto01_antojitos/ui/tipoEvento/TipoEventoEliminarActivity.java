package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class TipoEventoEliminarActivity extends AppCompatActivity {

    private EditText editTextIdTipoEvento;
    private Button buttonEliminar;
    private TipoEventoDAO tipoEventoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_evento_eliminar);

        editTextIdTipoEvento = findViewById(R.id.editTextIdTipoEvento);
        buttonEliminar = findViewById(R.id.buttonEliminar);
        tipoEventoDAO = new TipoEventoDAO(new DBHelper(this).getWritableDatabase());

        buttonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarTipoEvento();
            }
        });
    }

    private void eliminarTipoEvento() {
        String idTipoEventoStr = editTextIdTipoEvento.getText().toString();

        if (idTipoEventoStr.isEmpty()) {
            Toast.makeText(this, "Ingrese un ID válido", Toast.LENGTH_SHORT).show();
            return;
        }

        int idTipoEvento = Integer.parseInt(idTipoEventoStr);

        int filasEliminadas = tipoEventoDAO.eliminar(idTipoEvento);

        if (filasEliminadas > 0) {
            Toast.makeText(this, "Tipo de Evento eliminado con éxito", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se encontró el Tipo de Evento", Toast.LENGTH_SHORT).show();
        }
    }
}
