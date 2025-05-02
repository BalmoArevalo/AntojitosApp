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

public class PedidoCrearActivity extends AppCompatActivity {

    private Spinner spinnerCliente, spinnerTipoEvento, spinnerRepartidor, spinnerEstado;
    private EditText editTextFechaHora;
    private Button btnGuardar;
    private Calendar calendario;

    private PedidoDAO pedidoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_crear);

        // UI
        spinnerCliente = findViewById(R.id.spinnerCliente);
        spinnerTipoEvento = findViewById(R.id.spinnerTipoEvento);
        spinnerRepartidor = findViewById(R.id.spinnerRepartidor);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        editTextFechaHora = findViewById(R.id.editTextFechaHora);
        btnGuardar = findViewById(R.id.btnGuardarPedido);

        calendario = Calendar.getInstance();

        // Inicializar DB y DAO
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        pedidoDAO = new PedidoDAO(db);

        // Cargar datos (temporalmente hardcoded)
        cargarSpinnersMock();

        // Fecha y hora
        editTextFechaHora.setOnClickListener(v -> mostrarDateTimePicker());

        // Guardar pedido
        btnGuardar.setOnClickListener(v -> guardarPedido());
    }

    private void cargarSpinnersMock() {
        // Simulados - luego puedes reemplazar por clientes/eventos reales
        List<String> clientes = Arrays.asList("Seleccione", "1 - Carlos Gómez", "2 - Ana López");
        List<String> eventos = Arrays.asList("Ninguno", "1 - Cumpleaños", "2 - Fiesta Empresa");
        List<String> repartidores = Arrays.asList("Seleccione", "1 - Luis Torres", "2 - María Pérez");
        List<String> estados = Arrays.asList("Pendiente", "Despachado", "Entregado", "Cancelado");

        spinnerCliente.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, clientes));
        spinnerTipoEvento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, eventos));
        spinnerRepartidor.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, repartidores));
        spinnerEstado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados));
    }

    private void mostrarDateTimePicker() {
        int year = calendario.get(Calendar.YEAR);
        int month = calendario.get(Calendar.MONTH);
        int day = calendario.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, y, m, d) -> {
            calendario.set(Calendar.YEAR, y);
            calendario.set(Calendar.MONTH, m);
            calendario.set(Calendar.DAY_OF_MONTH, d);

            new TimePickerDialog(this, (view1, hour, minute) -> {
                calendario.set(Calendar.HOUR_OF_DAY, hour);
                calendario.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                editTextFechaHora.setText(sdf.format(calendario.getTime()));
            }, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), true).show();

        }, year, month, day).show();
    }

    private void guardarPedido() {
        if (spinnerCliente.getSelectedItemPosition() == 0 ||
                spinnerRepartidor.getSelectedItemPosition() == 0 ||
                editTextFechaHora.getText().toString().isEmpty()) {
            Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Pedido pedido = new Pedido();
        pedido.setIdCliente(extraerId(spinnerCliente));
        pedido.setIdRepartidor(extraerId(spinnerRepartidor));
        pedido.setFechaHoraPedido(editTextFechaHora.getText().toString());
        pedido.setEstadoPedido(spinnerEstado.getSelectedItem().toString());

        if (spinnerTipoEvento.getSelectedItemPosition() != 0) {
            pedido.setIdTipoEvento(extraerId(spinnerTipoEvento));
        } else {
            pedido.setIdTipoEvento(0);
        }

        long idInsertado = pedidoDAO.insertar(pedido);
        if (idInsertado > 0) {
            Toast.makeText(this, "Pedido creado (ID: " + idInsertado + ")", Toast.LENGTH_LONG).show();
            limpiar();
        } else {
            Toast.makeText(this, "Error al insertar pedido", Toast.LENGTH_SHORT).show();
        }
    }

    private int extraerId(Spinner spinner) {
        try {
            String seleccionado = spinner.getSelectedItem().toString();
            return Integer.parseInt(seleccionado.split(" - ")[0].trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void limpiar() {
        spinnerCliente.setSelection(0);
        spinnerTipoEvento.setSelection(0);
        spinnerRepartidor.setSelection(0);
        spinnerEstado.setSelection(0);
        editTextFechaHora.setText("");
    }
}
