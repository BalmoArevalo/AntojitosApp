package sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;
import java.util.List;
import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class DepartamentoEliminarActivity extends AppCompatActivity {

    private Spinner spinnerDepartamentos;
    private Button btnEliminar;

    private DepartamentoViewModel viewModel;
    private List<Departamento> listaDepartamentos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departamento_eliminar);

        spinnerDepartamentos = findViewById(R.id.spinnerIdDepartamentoEliminar);

        btnEliminar = findViewById(R.id.btnEliminarDepartamento);

        viewModel = new ViewModelProvider(this).get(DepartamentoViewModel.class);

        viewModel.getListaDepartamentos().observe(this, departamentos -> {
            // Solo mostrar los activos
            listaDepartamentos = filtrarActivos(departamentos);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    obtenerIdsYNombreDesdeDepartamentos(listaDepartamentos)
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDepartamentos.setAdapter(adapter);

            btnEliminar.setEnabled(!listaDepartamentos.isEmpty());
        });

        viewModel.cargarDepartamentos();

        btnEliminar.setOnClickListener(v -> eliminarDepartamento());
    }

    private List<Departamento> filtrarActivos(List<Departamento> departamentos) {
        List<Departamento> activos = new ArrayList<>();
        for (Departamento d : departamentos) {
            if (d.getActivoDepartamento() == 1) {
                activos.add(d);
            }
        }
        return activos;
    }

    private List<String> obtenerIdsYNombreDesdeDepartamentos(List<Departamento> departamentos) {
        List<String> ids = new ArrayList<>();
        for (Departamento d : departamentos) {
            ids.add(d.getIdDepartamento() + " - " + d.getNombreDepartamento());
        }
        return ids;
    }

    private void eliminarDepartamento() {
        int position = spinnerDepartamentos.getSelectedItemPosition();
        if (position >= 0 && position < listaDepartamentos.size()) {
            Departamento seleccionado = listaDepartamentos.get(position);

            // Marcar como inactivo
            seleccionado.setActivoDepartamento(0);
            int filas = viewModel.actualizarDepartamento(seleccionado);

            if (filas > 0) {
                Toast.makeText(this, "Departamento desactivado", Toast.LENGTH_SHORT).show();
                viewModel.cargarDepartamentos(); // Refrescar
            } else {
                Toast.makeText(this, "Error al desactivar", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
