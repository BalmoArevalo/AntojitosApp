package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor;

import android.database.sqlite.SQLiteDatabase;
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

public class RepartidorEliminarActivity extends AppCompatActivity {

    private Spinner spinnerRepartidor;
    private Button btnBuscarRepartidor;
    private TextView tvResultado;
    private Button btnEliminarRepartidor;
    private Button btnLimpiarCampos;

    private DBHelper dbHelper;
    private RepartidorDAO dao;
    private List<Integer> repartidorIds = new ArrayList<>();
    private int idRepartidorSeleccionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repartidor_eliminar);

        spinnerRepartidor       = findViewById(R.id.spinnerRepartidor);
        btnBuscarRepartidor     = findViewById(R.id.btnBuscarRepartidor);
        tvResultado             = findViewById(R.id.tvResultado);
        btnEliminarRepartidor   = findViewById(R.id.btnEliminarRepartidor);
        btnLimpiarCampos        = findViewById(R.id.btnLimpiarCampos);

        dbHelper = new DBHelper(this);
        dao = new RepartidorDAO(dbHelper.getWritableDatabase());

        cargarSpinnerActivos();
        btnEliminarRepartidor.setEnabled(false);

        btnBuscarRepartidor.setOnClickListener(v -> mostrarDetalles());
        btnEliminarRepartidor.setOnClickListener(v -> eliminarRepartidor());
        btnLimpiarCampos.setOnClickListener(v -> limpiarCampos());
    }

    private void cargarSpinnerActivos() {
        repartidorIds.clear();
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.repartidor_crear_spinner_placeholder));
        repartidorIds.add(-1);

        List<Repartidor> activos = dao.obtenerActivos();
        for (Repartidor r : activos) {
            repartidorIds.add(r.getIdRepartidor());
            items.add(r.getIdRepartidor() + " - " + r.getNombreRepartidor() + " " + r.getApellidoRepartidor());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepartidor.setAdapter(adapter);
    }

    private void mostrarDetalles() {
        int pos = spinnerRepartidor.getSelectedItemPosition();
        int id  = repartidorIds.get(pos);
        if (id < 0) {
            Toast.makeText(this, getString(R.string.repartidor_editar_toast_seleccionar), Toast.LENGTH_SHORT).show();
            return;
        }
        idRepartidorSeleccionado = id;

        Repartidor r = dao.obtenerPorId(id);
        if (r != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("ID Repartidor: ").append(r.getIdRepartidor()).append("\n")
                    .append("Nombre: ").append(r.getNombreRepartidor()).append("\n")
                    .append("Apellido: ").append(r.getApellidoRepartidor()).append("\n")
                    .append("Tel\u00e9fono: ").append(r.getTelefonoRepartidor()).append("\n")
                    .append("Tipo Veh\u00edculo: ").append(r.getTipoVehiculo()).append("\n")
                    .append("Disponible: ").append(r.getDisponible() == 1 ? getString(R.string.respuesta_si) : getString(R.string.respuesta_no)).append("\n")
                    .append("Departamento ID: ").append(r.getIdDepartamento()).append("\n")
                    .append("Municipio ID: ").append(r.getIdMunicipio()).append("\n")
                    .append("Distrito ID: ").append(r.getIdDistrito()).append("\n")
                    .append("Estado: ").append(r.getActivoRepartidor() == 1 ? getString(R.string.estado_activo) : getString(R.string.estado_inactivo));
            tvResultado.setText(sb.toString());
            btnEliminarRepartidor.setEnabled(true);
        } else {
            tvResultado.setText(getString(R.string.repartidor_editar_toast_no_encontrado));
            btnEliminarRepartidor.setEnabled(false);
        }
    }

    private void eliminarRepartidor() {
        if (idRepartidorSeleccionado < 0) {
            Toast.makeText(this, getString(R.string.repartidor_editar_toast_no_busqueda), Toast.LENGTH_SHORT).show();
            return;
        }
        int filas = dao.eliminar(idRepartidorSeleccionado);
        if (filas > 0) {
            Toast.makeText(this, getString(R.string.repartidor_eliminar_toast_exito), Toast.LENGTH_LONG).show();
            cargarSpinnerActivos();
            limpiarCampos();
        } else {
            Toast.makeText(this, getString(R.string.repartidor_eliminar_toast_error), Toast.LENGTH_LONG).show();
        }
    }

    private void limpiarCampos() {
        spinnerRepartidor.setSelection(0);
        tvResultado.setText(getString(R.string.repartidor_eliminar_resultado_default));
        btnEliminarRepartidor.setEnabled(false);
        idRepartidorSeleccionado = -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}