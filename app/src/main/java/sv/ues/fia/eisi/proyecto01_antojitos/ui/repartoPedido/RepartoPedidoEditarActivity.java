package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.*;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class RepartoPedidoEditarActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteBuscar;
    private EditText editTextHoraAsignacion, editTextUbicacion, editTextFechaEntrega;
    private Button btnBuscar, btnActualizar;

    private RepartoPedidoDAO dao;
    private RepartoPedido repartoSeleccionado;
    private Map<String, RepartoPedido> mapRepartos;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reparto_pedido_editar);

        autoCompleteBuscar = findViewById(R.id.autoCompleteBuscar);
        editTextHoraAsignacion = findViewById(R.id.editTextHoraAsignacion);
        editTextUbicacion = findViewById(R.id.editTextUbicacion);
        editTextFechaEntrega = findViewById(R.id.editTextFechaEntrega);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnActualizar = findViewById(R.id.btnActualizar);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        dao = new RepartoPedidoDAO(db);

        cargarRepartos();

        editTextHoraAsignacion.setOnClickListener(v -> mostrarDateTimePickerAsignacion());
        editTextFechaEntrega.setOnClickListener(v -> mostrarDateTimePickerEntrega());

        btnBuscar.setOnClickListener(v -> buscarReparto());
        btnActualizar.setOnClickListener(v -> actualizar());
    }

    private void cargarRepartos() {
        List<RepartoPedido> lista = dao.obtenerTodos();
        mapRepartos = new HashMap<>();
        List<String> items = new ArrayList<>();

        for (RepartoPedido r : lista) {
            String label = getString(R.string.repartopedido_item_label, r.getIdPedido(), r.getIdRepartoPedido());
            mapRepartos.put(label, r);
            items.add(label);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
        autoCompleteBuscar.setAdapter(adapter);
    }

    private void buscarReparto() {
        String seleccion = autoCompleteBuscar.getText().toString().trim();
        repartoSeleccionado = null;

        for (Map.Entry<String, RepartoPedido> entry : mapRepartos.entrySet()) {
            if (entry.getKey().equals(seleccion)) {
                repartoSeleccionado = entry.getValue();
                break;
            }
        }

        if (repartoSeleccionado == null) {
            Toast.makeText(this, getString(R.string.repartopedido_toast_seleccione_valido), Toast.LENGTH_SHORT).show();
            return;
        }

        editTextHoraAsignacion.setText(repartoSeleccionado.getFechaHoraAsignacion());
        editTextUbicacion.setText(repartoSeleccionado.getUbicacionEntrega());
        editTextFechaEntrega.setText(repartoSeleccionado.getFechaHoraEntrega());
    }


    private void actualizar() {
        if (repartoSeleccionado == null) return;

        String fechaHora = editTextHoraAsignacion.getText().toString().trim();
        String ubicacion = editTextUbicacion.getText().toString().trim();
        String fecha = editTextFechaEntrega.getText().toString().trim();

        if (fechaHora.isEmpty() || ubicacion.isEmpty()) {
            Toast.makeText(this, getString(R.string.repartopedido_toast_campos_incompletos), Toast.LENGTH_SHORT).show();
            return;
        }

        repartoSeleccionado.setFechaHoraAsignacion(fechaHora);
        repartoSeleccionado.setUbicacionEntrega(ubicacion);
        repartoSeleccionado.setFechaHoraEntrega(fecha.isEmpty() ? null : fecha);

        int result = dao.actualizar(repartoSeleccionado);
        if (result > 0) {
            Toast.makeText(this, getString(R.string.repartopedido_toast_actualizado_ok), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.repartopedido_toast_actualizado_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDateTimePickerAsignacion() {
        new DatePickerDialog(this, (view, y, m, d) -> {
            calendar.set(y, m, d);
            new TimePickerDialog(this, (view1, h, min) -> {
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, min);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                editTextHoraAsignacion.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void mostrarDateTimePickerEntrega() {
        new DatePickerDialog(this, (view, y, m, d) -> {
            calendar.set(y, m, d);
            new TimePickerDialog(this, (view1, h, min) -> {
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, min);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                editTextFechaEntrega.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
