package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

import java.text.SimpleDateFormat;
import java.util.*;

public class PedidoEditarActivity extends AppCompatActivity {

    private EditText editTextIdBuscar, editTextFecha;
    private Spinner spinnerEstado;
    private Button btnBuscar, btnActualizar;
    private HashMap<Integer, Pedido> pedidosMock;
    private Pedido pedidoActual;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_editar);

        editTextIdBuscar = findViewById(R.id.editTextIdPedidoBuscar);
        editTextFecha = findViewById(R.id.editTextFechaEditar);
        spinnerEstado = findViewById(R.id.spinnerEstadoEditar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnActualizar = findViewById(R.id.btnActualizar);
        calendar = Calendar.getInstance();

        // Spinner estado
        spinnerEstado.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("Pendiente", "Despachado", "Entregado", "Cancelado")));

        // Mock
        pedidosMock = new HashMap<>();
        Pedido p = new Pedido();
        p.setIdPedido(1);
        p.setEstadoPedido("Pendiente");
        p.setFechaHoraPedido("01/05/2025 17:00");
        pedidosMock.put(p.getIdPedido(), p);

        btnBuscar.setOnClickListener(v -> buscarPedido());
        editTextFecha.setOnClickListener(v -> mostrarDateTimePicker());
        btnActualizar.setOnClickListener(v -> actualizarPedido());
    }

    private void buscarPedido() {
        String id = editTextIdBuscar.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "Ingrese un ID", Toast.LENGTH_SHORT).show();
            return;
        }

        int idPedido = Integer.parseInt(id);
        if (pedidosMock.containsKey(idPedido)) {
            pedidoActual = pedidosMock.get(idPedido);
            spinnerEstado.setSelection(obtenerIndiceEstado(pedidoActual.getEstadoPedido()));
            editTextFecha.setText(pedidoActual.getFechaHoraPedido());
            Toast.makeText(this, "Pedido encontrado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Pedido no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDateTimePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            TimePickerDialog timePicker = new TimePickerDialog(this, (view1, hour, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                editTextFecha.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePicker.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private int obtenerIndiceEstado(String estado) {
        List<String> estados = Arrays.asList("Pendiente", "Despachado", "Entregado", "Cancelado");
        return estados.indexOf(estado);
    }

    private void actualizarPedido() {
        if (pedidoActual == null) {
            Toast.makeText(this, "Debe buscar un pedido primero", Toast.LENGTH_SHORT).show();
            return;
        }

        pedidoActual.setEstadoPedido(spinnerEstado.getSelectedItem().toString());
        pedidoActual.setFechaHoraPedido(editTextFecha.getText().toString());

        Toast.makeText(this, "Pedido actualizado (simulado)", Toast.LENGTH_SHORT).show();
    }
}
