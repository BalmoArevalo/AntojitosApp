package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.db.MunicipioDAO;

public class MunicipioEliminarActivity extends AppCompatActivity {

    private Spinner spinnerMunicipio;
    private TextView tvResultado;
    private Button btnBuscar, btnEliminar, btnLimpiar;

    private DBHelper dbHelper;
    private MunicipioDAO dao;
    private List<Municipio> municipios = new ArrayList<>();
    private int idDepartamentoSeleccionado = -1;
    private int idMunicipioSeleccionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipio_eliminar);

        spinnerMunicipio = findViewById(R.id.spinnerMunicipio);
        tvResultado = findViewById(R.id.tvResultado);
        btnBuscar = findViewById(R.id.btnBuscarMunicipio);
        btnEliminar = findViewById(R.id.btnEliminarMunicipio);
        btnLimpiar = findViewById(R.id.btnLimpiarCampos);

        dbHelper = new DBHelper(this);
        dao = new MunicipioDAO(dbHelper.getWritableDatabase());

        cargarSpinnerMunicipios();

        btnBuscar.setOnClickListener(v -> mostrarDetalles());
        btnEliminar.setOnClickListener(v -> eliminarMunicipio());
        btnLimpiar.setOnClickListener(v -> limpiarCampos());
    }

    private void cargarSpinnerMunicipios() {
        municipios.clear();
        List<String> items = new ArrayList<>();
        items.add("Seleccione...");
        municipios.add(null);

        List<Municipio> listaActivos = dao.obtenerTodos();
        for (Municipio m : listaActivos) {
            municipios.add(m);
            items.add(m.getIdMunicipio() + " - " + m.getNombreMunicipio());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMunicipio.setAdapter(adapter);
    }

    private void mostrarDetalles() {
        int pos = spinnerMunicipio.getSelectedItemPosition();
        if (pos <= 0 || municipios.get(pos) == null) {
            Toast.makeText(this, "Selecciona un municipio válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Municipio m = municipios.get(pos);
        idDepartamentoSeleccionado = m.getIdDepartamento();
        idMunicipioSeleccionado = m.getIdMunicipio();

        StringBuilder sb = new StringBuilder();
        sb.append("ID Departamento: ").append(m.getIdDepartamento()).append("\n");
        sb.append("ID Municipio: ").append(m.getIdMunicipio()).append("\n");
        sb.append("Nombre: ").append(m.getNombreMunicipio()).append("\n");
        sb.append("Activo: ").append(m.getActivoMunicipio() == 1 ? "Sí" : "No");

        tvResultado.setText(sb.toString());
    }

    private void eliminarMunicipio() {
        if (idDepartamentoSeleccionado == -1 || idMunicipioSeleccionado == -1) {
            Toast.makeText(this, "Busca primero un municipio", Toast.LENGTH_SHORT).show();
            return;
        }

        int filas = dao.eliminar(idDepartamentoSeleccionado, idMunicipioSeleccionado);
        if (filas > 0) {
            Toast.makeText(this, "Municipio desactivado correctamente", Toast.LENGTH_LONG).show();
            cargarSpinnerMunicipios();
            limpiarCampos();
        } else {
            Toast.makeText(this, "Error al desactivar municipio", Toast.LENGTH_LONG).show();
        }
    }

    private void limpiarCampos() {
        spinnerMunicipio.setSelection(0);
        tvResultado.setText("Aquí se mostrará la información del municipio seleccionado.");
        idDepartamentoSeleccionado = -1;
        idMunicipioSeleccionado = -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
