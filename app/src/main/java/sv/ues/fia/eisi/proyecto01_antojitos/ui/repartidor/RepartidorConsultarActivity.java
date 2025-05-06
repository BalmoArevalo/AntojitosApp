package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor;

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

public class RepartidorConsultarActivity extends AppCompatActivity {

    private Spinner spinnerRepartidor;
    private Button btnConsultarRepartidor;
    private TextView tvResultado;
    private DBHelper dbHelper;
    private RepartidorDAO dao;
    private List<Integer> repartidorIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repartidor_consultar);

        spinnerRepartidor       = findViewById(R.id.spinnerRepartidor);
        btnConsultarRepartidor  = findViewById(R.id.btnConsultarRepartidor);
        tvResultado             = findViewById(R.id.tvResultado);

        dbHelper = new DBHelper(this);
        dao      = new RepartidorDAO(dbHelper.getReadableDatabase());

        cargarSpinnerRepartidores();

        btnConsultarRepartidor.setOnClickListener(v -> mostrarDetalleRepartidor());
    }

    /**
     * Carga solo repartidores activos en el spinner.
     */
    private void cargarSpinnerRepartidores() {
        repartidorIds.clear();
        List<String> items = new ArrayList<>();
        items.add("Seleccione...");
        repartidorIds.add(-1);

        List<Repartidor> activos = dao.obtenerActivos();
        for (Repartidor r : activos) {
            repartidorIds.add(r.getIdRepartidor());
            items.add(r.getIdRepartidor() + " - " + r.getNombreRepartidor() + " " + r.getApellidoRepartidor());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepartidor.setAdapter(adapter);
    }

    /**
     * Muestra todos los detalles del repartidor seleccionado.
     */
    private void mostrarDetalleRepartidor() {
        int pos = spinnerRepartidor.getSelectedItemPosition();
        int id  = repartidorIds.get(pos);
        if (id < 0) {
            Toast.makeText(this, "Selecciona un repartidor válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Repartidor r = dao.obtenerPorId(id);
        if (r == null) {
            tvResultado.setText("Repartidor no encontrado.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("ID Repartidor: ").append(r.getIdRepartidor()).append("\n")
                    .append("Departamento: ").append(r.getIdDepartamento()).append("\n")
                    .append("Municipio: ").append(r.getIdMunicipio()).append("\n")
                    .append("Distrito: ").append(r.getIdDistrito()).append("\n")
                    .append("Tipo Vehículo: ").append(r.getTipoVehiculo()).append("\n")
                    .append("Disponible: ").append(r.getDisponible() == 1 ? "Sí" : "No").append("\n")
                    .append("Teléfono: ").append(r.getTelefonoRepartidor()).append("\n")
                    .append("Nombre: ").append(r.getNombreRepartidor()).append("\n")
                    .append("Apellido: ").append(r.getApellidoRepartidor()).append("\n")
                    .append("Estado: ").append(r.getActivoRepartidor() == 1 ? "Activo" : "Inactivo");
            tvResultado.setText(sb.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
