package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

import java.text.SimpleDateFormat;
import java.util.*;

public class PedidoEditarActivity extends AppCompatActivity {

    private EditText editTextIdBuscar, editTextFecha;
    private Spinner spinnerCliente, spinnerRepartidor, spinnerEvento, spinnerEstado;
    private Button btnBuscar, btnActualizar;

    private PedidoDAO pedidoDAO;
    private Pedido pedidoActual;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_editar);

        editTextIdBuscar = findViewById(R.id.editTextIdBuscar);
        editTextFecha = findViewById(R.id.editTextFecha);
        spinnerCliente = findViewById(R.id.spinnerCliente);
        spinnerRepartidor = findViewById(R.id.spinnerRepartidor);
        spinnerEvento = findViewById(R.id.spinnerEvento);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        btnBuscar = findViewById(R.id.btnBuscarPedido);
        btnActualizar = findViewById(R.id.btnActualizar);

        calendar = Calendar.getInstance();

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        pedidoDAO = new PedidoDAO(db);

        cargarSpinners();

        editTextFecha.setOnClickListener(v -> mostrarDateTimePicker());
        btnBuscar.setOnClickListener(v -> buscarPedido());
        btnActualizar.setOnClickListener(v -> actualizarPedido());
    }

    private void cargarSpinners() {
        List<String> clientes = Arrays.asList("Seleccione", "1 - Carlos", "2 - Ana");
        List<String> repartidores = Arrays.asList("Seleccione", "1 - Luis", "2 - María");
        List<String> eventos = Arrays.asList("Ninguno", "1 - Cumpleaños", "2 - Empresa");
        List<String> estados = Arrays.asList("Pendiente", "Despachado", "Entregado", "Cancelado");

        spinnerCliente.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, clientes));
        spinnerRepartidor.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, repartidores));
        spinnerEvento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, eventos));
        spinnerEstado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados));
    }

    private void mostrarDateTimePicker() {
        new DatePickerDialog(this, (view, y, m, d) -> {
            calendar.set(y, m, d);
            new TimePickerDialog(this, (view1, h, min) -> {
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, min);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                editTextFecha.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void buscarPedido() {
        String idStr = editTextIdBuscar.getText().toString().trim();
        if (idStr.isEmpty()) {
            Toast.makeText(this, "Ingrese un ID", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = Integer.parseInt(idStr);
        pedidoActual = pedidoDAO.consultarPorId(id);
        if (pedidoActual != null) {
            // Rellenar datos
            spinnerCliente.setSelection(obtenerIndicePorId(spinnerCliente, pedidoActual.getIdCliente()));
            spinnerRepartidor.setSelection(obtenerIndicePorId(spinnerRepartidor, pedidoActual.getIdRepartidor()));
            spinnerEvento.setSelection(obtenerIndicePorId(spinnerEvento, pedidoActual.getIdTipoEvento()));
            spinnerEstado.setSelection(obtenerIndiceTexto(spinnerEstado, pedidoActual.getEstadoPedido()));
            editTextFecha.setText(pedidoActual.getFechaHoraPedido());
            btnActualizar.setEnabled(true);
        } else {
            Toast.makeText(this, "Pedido no encontrado", Toast.LENGTH_SHORT).show();
            btnActualizar.setEnabled(false);
        }
    }

    private void actualizarPedido() {
        if (pedidoActual == null) return;

        pedidoActual.setIdCliente(extraerId(spinnerCliente));
        pedidoActual.setIdRepartidor(extraerId(spinnerRepartidor));
        pedidoActual.setIdTipoEvento(extraerId(spinnerEvento));
        pedidoActual.setEstadoPedido(spinnerEstado.getSelectedItem().toString());
        pedidoActual.setFechaHoraPedido(editTextFecha.getText().toString());

        int filas = pedidoDAO.actualizar(pedidoActual);
        if (filas > 0) {
            Toast.makeText(this, "Pedido actualizado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private int extraerId(Spinner spinner) {
        try {
            return Integer.parseInt(spinner.getSelectedItem().toString().split(" - ")[0].trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private int obtenerIndicePorId(Spinner spinner, int id) {
        for (int i = 0; i < spinner.getCount(); i++) {
            String val = spinner.getItemAtPosition(i).toString();
            if (val.startsWith(id + " -")) return i;
        }
        return 0;
    }

    private int obtenerIndiceTexto(Spinner spinner, String texto) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(texto)) return i;
        }
        return 0;
    }
}
