package sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento;

import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DepartamentoConsultarActivity extends AppCompatActivity {

    private Spinner spinnerId;
    private Button btnBuscar;
    private TextView tvResultado;
    private DepartamentoDAO dao;
    private List<Departamento> listaDepartamentos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departamento_consultar);

        spinnerId = findViewById(R.id.editTextIdDepartamentoBuscar); // Verifica que este ID esté en tu XML
        btnBuscar = findViewById(R.id.btnBuscarDepartamento);
        tvResultado = findViewById(R.id.tvResultadoDepartamento);

        SQLiteDatabase db = new DBHelper(this).getReadableDatabase();
        dao = new DepartamentoDAO(db);

        // Cargar solo departamentos activos
        listaDepartamentos = dao.obtenerActivos();  // 👈 cambio clave
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                obtenerOpcionesSpinner(listaDepartamentos)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerId.setAdapter(adapter);

        btnBuscar.setOnClickListener(v -> buscarDepartamento());
    }

    private List<String> obtenerOpcionesSpinner(List<Departamento> departamentos) {
        List<String> lista = new ArrayList<>();
        for (Departamento dep : departamentos) {
            lista.add(dep.getIdDepartamento() + " - " + dep.getNombreDepartamento());
        }
        return lista;
    }

    private void buscarDepartamento() {
        int pos = spinnerId.getSelectedItemPosition();
        if (pos >= 0 && pos < listaDepartamentos.size()) {
            Departamento departamento = listaDepartamentos.get(pos);
            tvResultado.setText("ID: " + departamento.getIdDepartamento() +
                    "\nNombre: " + departamento.getNombreDepartamento());
        } else {
            tvResultado.setText("Departamento no encontrado.");
        }
    }
}
