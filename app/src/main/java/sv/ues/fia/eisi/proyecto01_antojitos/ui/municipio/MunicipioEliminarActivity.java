package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class MunicipioEliminarActivity extends AppCompatActivity {

    private Spinner spinnerMunicipio;
    private TextView tvResultado;
    private Button btnBuscar, btnEliminar, btnLimpiar;

    private MunicipioViewModel municipioViewModel;
    private List<Municipio> municipios = new ArrayList<>();
    private Municipio municipioSeleccionado = null;

    private ArrayAdapter<String> adapterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipio_eliminar);

        spinnerMunicipio = findViewById(R.id.spinnerMunicipio);
        tvResultado = findViewById(R.id.tvResultado);
        btnBuscar = findViewById(R.id.btnBuscarMunicipio);
        btnEliminar = findViewById(R.id.btnEliminarMunicipio);
        btnLimpiar = findViewById(R.id.btnLimpiarCampos);

        municipioViewModel = new ViewModelProvider(this).get(MunicipioViewModel.class);

        municipioViewModel.getListaMunicipios().observe(this, lista -> actualizarSpinner(lista));
        municipioViewModel.cargarMunicipios();

        btnBuscar.setOnClickListener(v -> mostrarDetalles());
        btnEliminar.setOnClickListener(v -> eliminarMunicipio());
        btnLimpiar.setOnClickListener(v -> limpiarCampos());
    }

    private void actualizarSpinner(List<Municipio> lista) {
        municipios.clear();
        List<String> items = new ArrayList<>();
        items.add("Seleccione...");
        municipios.add(null);

        for (Municipio m : lista) {
            if (m.getActivoMunicipio() == 1) {
                municipios.add(m);
                items.add(m.getIdMunicipio() + " - " + m.getNombreMunicipio());
            }
        }

        adapterSpinner = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMunicipio.setAdapter(adapterSpinner);
    }

    private void mostrarDetalles() {
        int pos = spinnerMunicipio.getSelectedItemPosition();
        if (pos <= 0 || municipios.get(pos) == null) {
            Toast.makeText(this, "Selecciona un municipio válido", Toast.LENGTH_SHORT).show();
            return;
        }

        municipioSeleccionado = municipios.get(pos);

        StringBuilder sb = new StringBuilder();
        sb.append("ID Departamento: ").append(municipioSeleccionado.getIdDepartamento()).append("\n");
        sb.append("ID Municipio: ").append(municipioSeleccionado.getIdMunicipio()).append("\n");
        sb.append("Nombre: ").append(municipioSeleccionado.getNombreMunicipio()).append("\n");
        sb.append("Activo: ").append(municipioSeleccionado.getActivoMunicipio() == 1 ? "Sí" : "No");

        tvResultado.setText(sb.toString());
    }

    private void eliminarMunicipio() {
        if (municipioSeleccionado == null) {
            Toast.makeText(this, "Busca primero un municipio", Toast.LENGTH_SHORT).show();
            return;
        }

        municipioSeleccionado.setActivoMunicipio(0); // Desactivación lógica
        int filas = municipioViewModel.actualizar(municipioSeleccionado);

        if (filas > 0) {
            Toast.makeText(this, "Municipio desactivado correctamente", Toast.LENGTH_LONG).show();
            limpiarCampos();
            municipioViewModel.cargarMunicipios(); // Recarga lista desde DB
        } else {
            Toast.makeText(this, "Error al desactivar municipio", Toast.LENGTH_LONG).show();
        }
    }

    private void limpiarCampos() {
        spinnerMunicipio.setSelection(0);
        tvResultado.setText("Aquí se mostrará la información del municipio seleccionado.");
        municipioSeleccionado = null;
    }
}
