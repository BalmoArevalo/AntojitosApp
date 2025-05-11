package sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento;

import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DepartamentoEditarActivity extends AppCompatActivity {

    private Spinner spinnerDepartamentos;
    private EditText editTextNombre;
    private Switch switchEstado;
    private Button btnBuscar, btnActualizar;
    private DepartamentoDAO dao;
    private List<Departamento> listaDepartamentos = new ArrayList<>();
    private Departamento departamentoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departamento_editar);

        spinnerDepartamentos = findViewById(R.id.spinnerIdDepartamentoEditar);
        editTextNombre = findViewById(R.id.editTextNombreDepartamentoEditar);
        switchEstado = findViewById(R.id.switchEstadoDepartamento);
        btnBuscar = findViewById(R.id.btnBuscarDepartamentoEditar);
        btnActualizar = findViewById(R.id.btnActualizarDepartamento);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        dao = new DepartamentoDAO(db);

        cargarDepartamentos();

        btnBuscar.setOnClickListener(v -> {
            int pos = spinnerDepartamentos.getSelectedItemPosition();
            if (pos >= 0 && pos < listaDepartamentos.size()) {
                departamentoSeleccionado = listaDepartamentos.get(pos);
                editTextNombre.setText(departamentoSeleccionado.getNombreDepartamento());
                switchEstado.setChecked(departamentoSeleccionado.getActivoDepartamento() == 1);
            } else {
                Toast.makeText(this, "Selecciona un departamento válido", Toast.LENGTH_SHORT).show();
            }
        });

        btnActualizar.setOnClickListener(v -> actualizarDepartamento());
    }

    private void cargarDepartamentos() {
        listaDepartamentos = dao.obtenerTodos();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                obtenerNombresSpinner(listaDepartamentos)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartamentos.setAdapter(adapter);
    }

    private List<String> obtenerNombresSpinner(List<Departamento> departamentos) {
        List<String> nombres = new ArrayList<>();
        for (Departamento d : departamentos) {
            nombres.add(d.getIdDepartamento() + " - " + d.getNombreDepartamento());
        }
        return nombres;
    }

    private void actualizarDepartamento() {
        if (departamentoSeleccionado == null) {
            Toast.makeText(this, "Primero debes buscar un departamento", Toast.LENGTH_SHORT).show();
            return;
        }

        String nuevoNombre = editTextNombre.getText().toString().trim();
        if (nuevoNombre.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        departamentoSeleccionado.setNombreDepartamento(nuevoNombre);
        departamentoSeleccionado.setActivoDepartamento(switchEstado.isChecked() ? 1 : 0); // 🔥 actualizar estado

        int filas = dao.actualizar(departamentoSeleccionado);

        if (filas > 0) {
            Toast.makeText(this, "Departamento actualizado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
        }
    }
}

