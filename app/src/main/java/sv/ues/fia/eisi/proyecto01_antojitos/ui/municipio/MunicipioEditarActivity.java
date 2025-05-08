package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.db.MunicipioDAO;

public class MunicipioEditarActivity extends AppCompatActivity {

    private EditText editTextIdMunicipio, editTextNombreMunicipio, editTextIdDepartamento;
    private Button buttonActualizar;
    private MunicipioDAO municipioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipio_editar);

        editTextIdMunicipio = findViewById(R.id.editTextIdMunicipioEditar);
        editTextNombreMunicipio = findViewById(R.id.editTextNombreMunicipioEditar);
        editTextIdDepartamento = findViewById(R.id.editTextIdDepartamentoEditar);
        buttonActualizar = findViewById(R.id.buttonActualizarMunicipio);

        municipioDAO = new MunicipioDAO(new DBHelper(this).getWritableDatabase());

        buttonActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarMunicipio();
            }
        });
    }

    private void editarMunicipio() {
        String idMunicipioStr = editTextIdMunicipio.getText().toString();
        String nombre = editTextNombreMunicipio.getText().toString();
        String idDepartamentoStr = editTextIdDepartamento.getText().toString();

        if (idMunicipioStr.isEmpty() || nombre.isEmpty() || idDepartamentoStr.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int idMunicipio = Integer.parseInt(idMunicipioStr);
        int idDepartamento = Integer.parseInt(idDepartamentoStr);

        Municipio municipio = new Municipio();
        municipio.setIdMunicipio(idMunicipio);
        municipio.setNombreMunicipio(nombre);
        municipio.setIdDepartamento(idDepartamento);

        int filasActualizadas = municipioDAO.actualizar(municipio);

        if (filasActualizadas > 0) {
            Toast.makeText(this, "Municipio actualizado con éxito", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se encontró el municipio", Toast.LENGTH_SHORT).show();
        }
    }
}
