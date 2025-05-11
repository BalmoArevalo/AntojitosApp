package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.db.MunicipioDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento.Departamento;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio.Municipio;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento.DepartamentoViewModel;

public class MunicipioCrearActivity extends AppCompatActivity {

    private EditText editTextNombre;
    private Spinner spinnerDepartamento;
    private Button btnGuardar;

    private MunicipioDAO dao;
    private DepartamentoViewModel departamentoViewModel;
    private List<Departamento> listaDepartamentos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipio_crear);

        editTextNombre = findViewById(R.id.editTextNombreMunicipio);
        spinnerDepartamento = findViewById(R.id.spinnerDepartamento);
        btnGuardar = findViewById(R.id.btnGuardarMunicipio);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        dao = new MunicipioDAO(db);

        // ViewModel para llenar el spinner de departamentos
        departamentoViewModel = new ViewModelProvider(this).get(DepartamentoViewModel.class);
        departamentoViewModel.getListaDepartamentos().observe(this, departamentos -> {
            listaDepartamentos = departamentos;

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    obtenerNombresDepartamentos(departamentos)
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDepartamento.setAdapter(adapter);
        });

        departamentoViewModel.cargarDepartamentos();

        btnGuardar.setOnClickListener(v -> {
            if (camposValidos()) {
                Municipio municipio = new Municipio();
                municipio.setNombreMunicipio(editTextNombre.getText().toString());

                int pos = spinnerDepartamento.getSelectedItemPosition();
                if (pos >= 0 && pos < listaDepartamentos.size()) {
                    municipio.setIdDepartamento(listaDepartamentos.get(pos).getIdDepartamento());
                } else {
                    Toast.makeText(this, "Selecciona un departamento válido", Toast.LENGTH_SHORT).show();
                    return;
                }

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
        return !editTextNombre.getText().toString().trim().isEmpty();
    }

    private void limpiar() {
        editTextNombre.setText("");
        if (!listaDepartamentos.isEmpty()) {
            spinnerDepartamento.setSelection(0);
        }
    }

    private List<String> obtenerNombresDepartamentos(List<Departamento> departamentos) {
        List<String> nombres = new ArrayList<>();
        for (Departamento d : departamentos) {
            nombres.add(d.getNombreDepartamento());
        }
        return nombres;
    }
}
