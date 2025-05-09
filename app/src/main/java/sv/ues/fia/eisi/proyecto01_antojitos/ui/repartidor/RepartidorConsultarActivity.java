package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor;

import android.os.Bundle;
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

        spinnerRepartidor      = findViewById(R.id.spinnerRepartidor);
        btnConsultarRepartidor = findViewById(R.id.btnConsultarRepartidor);
        tvResultado            = findViewById(R.id.tvResultado);

        dbHelper = new DBHelper(this);
        dao      = new RepartidorDAO(dbHelper.getReadableDatabase());

        cargarSpinnerRepartidores();

        btnConsultarRepartidor.setOnClickListener(v -> mostrarDetalleRepartidor());
    }

    private void cargarSpinnerRepartidores() {
        repartidorIds.clear();
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.spinner_placeholder));
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

    private void mostrarDetalleRepartidor() {
        int pos = spinnerRepartidor.getSelectedItemPosition();
        int id  = repartidorIds.get(pos);
        if (id < 0) {
            Toast.makeText(this, getString(R.string.repartidor_consultar_toast_seleccionar), Toast.LENGTH_SHORT).show();
            return;
        }

        Repartidor r = dao.obtenerPorId(id);
        if (r == null) {
            tvResultado.setText(getString(R.string.repartidor_consultar_msg_no_encontrado));
        } else {
            String disponible = r.getDisponible() == 1 ? getString(R.string.respuesta_si) : getString(R.string.respuesta_no);
            String estado     = r.getActivoRepartidor() == 1 ? getString(R.string.estado_activo) : getString(R.string.estado_inactivo);
            String detalle    = getString(
                    R.string.repartidor_consultar_detalle_formato,
                    r.getIdRepartidor(),
                    r.getIdDepartamento(),
                    r.getIdMunicipio(),
                    r.getIdDistrito(),
                    r.getTipoVehiculo(),
                    disponible,
                    r.getTelefonoRepartidor(),
                    r.getNombreRepartidor(),
                    r.getApellidoRepartidor(),
                    estado
            );
            tvResultado.setText(detalle);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}