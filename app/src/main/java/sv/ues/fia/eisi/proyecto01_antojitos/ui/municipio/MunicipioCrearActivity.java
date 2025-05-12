package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio.MunicipioDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento.Departamento;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento.DepartamentoViewModel;

public class MunicipioCrearActivity extends AppCompatActivity {

    private EditText editTextNombre;
    private Spinner spinnerDepartamento;
    private Button btnGuardar;

    private MunicipioDAO dao;
    private DepartamentoViewModel departamentoViewModel;
    private List<Departamento> listaDepartamentos = new ArrayList<>();

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipio_crear);

        editTextNombre = findViewById(R.id.editTextNombreMunicipio);
        spinnerDepartamento = findViewById(R.id.spinnerDepartamento);
        btnGuardar = findViewById(R.id.btnGuardarMunicipio);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        dao = new MunicipioDAO(db);

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
                int pos = spinnerDepartamento.getSelectedItemPosition();
                if (pos < 0 || pos >= listaDepartamentos.size()) {
                    Toast.makeText(this, "Selecciona un departamento válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                int idDep = listaDepartamentos.get(pos).getIdDepartamento();
                String nombreMun = editTextNombre.getText().toString().trim();

                // Verificar si ya existe ese municipio en ese departamento
                if (existeMunicipio(idDep, nombreMun)) {
                    Toast.makeText(this, "El municipio ya existe en este departamento", Toast.LENGTH_SHORT).show();
                    return;
                }

                int nuevoId = generarNuevoIdMunicipio(idDep);

                Municipio municipio = new Municipio();
                municipio.setIdDepartamento(idDep);
                municipio.setIdMunicipio(nuevoId);
                municipio.setNombreMunicipio(nombreMun);
                municipio.setActivoMunicipio(1); // Guardar como activo

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

    private int generarNuevoIdMunicipio(int idDepartamento) {
        Cursor cursor = db.rawQuery(
                "SELECT MAX(ID_MUNICIPIO) FROM MUNICIPIO WHERE ID_DEPARTAMENTO = ?",
                new String[]{String.valueOf(idDepartamento)}
        );
        int nuevoId = 1;
        if (cursor.moveToFirst()) {
            nuevoId = cursor.isNull(0) ? 1 : cursor.getInt(0) + 1;
        }
        cursor.close();
        return nuevoId;
    }

    private boolean existeMunicipio(int idDepartamento, String nombreMunicipio) {
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM MUNICIPIO WHERE ID_DEPARTAMENTO = ? AND NOMBRE_MUNICIPIO = ?",
                new String[]{String.valueOf(idDepartamento), nombreMunicipio}
        );
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
