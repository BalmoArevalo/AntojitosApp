package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.db.MunicipioDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio.Municipio;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio.MunicipioViewModel;

public class MunicipioEditarActivity extends AppCompatActivity {

    private Spinner spinnerMunicipios;
    private EditText editTextNombreMunicipio, editTextIdDepartamento;
    private Button buttonBuscar, buttonActualizar;

    private MunicipioDAO municipioDAO;
    private MunicipioViewModel viewModel;
    private List<Municipio> listaMunicipios = new ArrayList<>();
    private Municipio municipioSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipio_editar);

        spinnerMunicipios = findViewById(R.id.spinnerIdMunicipio);
        buttonBuscar = findViewById(R.id.buttonBuscarMunicipio);
        editTextNombreMunicipio = findViewById(R.id.editTextNombreMunicipioEditar);
        editTextIdDepartamento = findViewById(R.id.editTextIdDepartamentoEditar);
        buttonActualizar = findViewById(R.id.buttonActualizarMunicipio);

        municipioDAO = new MunicipioDAO(new DBHelper(this).getWritableDatabase());
        viewModel = new ViewModelProvider(this).get(MunicipioViewModel.class);

        viewModel.getListaMunicipios().observe(this, municipios -> {
            listaMunicipios = municipios;

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    obtenerListaIdsMunicipio(municipios)
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMunicipios.setAdapter(adapter);
        });

        viewModel.cargarMunicipios();

        buttonBuscar.setOnClickListener(v -> {
            int pos = spinnerMunicipios.getSelectedItemPosition();
            if (pos >= 0 && pos < listaMunicipios.size()) {
                municipioSeleccionado = listaMunicipios.get(pos);
                editTextNombreMunicipio.setText(municipioSeleccionado.getNombreMunicipio());
                editTextIdDepartamento.setText(String.valueOf(municipioSeleccionado.getIdDepartamento()));
            } else {
                Toast.makeText(this, "Selecciona un municipio válido", Toast.LENGTH_SHORT).show();
            }
        });

        buttonActualizar.setOnClickListener(v -> {
            if (municipioSeleccionado == null) {
                Toast.makeText(this, "Busca primero un municipio", Toast.LENGTH_SHORT).show();
                return;
            }

            String nuevoNombre = editTextNombreMunicipio.getText().toString().trim();
            String idDepStr = editTextIdDepartamento.getText().toString().trim();

            if (nuevoNombre.isEmpty() || idDepStr.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            municipioSeleccionado.setNombreMunicipio(nuevoNombre);
            municipioSeleccionado.setIdDepartamento(Integer.parseInt(idDepStr));

            int filas = municipioDAO.actualizar(municipioSeleccionado);
            if (filas > 0) {
                Toast.makeText(this, "Municipio actualizado con éxito", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> obtenerListaIdsMunicipio(List<Municipio> municipios) {
        List<String> lista = new ArrayList<>();
        for (Municipio m : municipios) {
            lista.add(String.valueOf(m.getIdMunicipio()));
        }
        return lista;
    }
}
