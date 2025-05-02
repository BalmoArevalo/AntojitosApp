package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

import java.text.SimpleDateFormat;
import java.util.*;

public class PedidoCrearActivity extends AppCompatActivity {

    private Spinner spinnerCliente, spinnerTipoEvento, spinnerRepartidor, spinnerEstado;
    private EditText editTextFechaHora;
    private Button btnGuardar;

    private Calendar calendario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_crear);

        // Referencias
        spinnerCliente = findViewById(R.id.spinnerCliente);
        spinnerTipoEvento = findViewById(R.id.spinnerTipoEvento);
        spinnerRepartidor = findViewById(R.id.spinnerRepartidor);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        editTextFechaHora = findViewById(R.id.editTextFechaHora);
        btnGuardar = findViewById(R.id.btnGuardarPedido);

        // Inicializar fecha/hora actual
        calendario = Calendar.getInstance();

        // Mock de listas
        cargarSpinnersMock();

        // Mostrar selector de fecha/hora
        editTextFechaHora.setOnClickListener(v -> mostrarDateTimePicker());

        // Guardar Pedido
        btnGuardar.setOnClickListener(v -> guardarPedido());
    }

    private void cargarSpinnersMock() {
        // Datos de prueba
        List<String> clientes = Arrays.asList("Seleccione", "Carlos Gómez", "Ana López");
        List<String> eventos = Arrays.asList("Ninguno", "Cumpleaños", "Reunión Corporativa");
        List<String> repartidores = Arrays.asList("Seleccione", "Luis Torres", "María Ruiz");
        List<String> estados = Arrays.asList("Pendiente", "Despachado", "Entregado", "Cancelado");

        // Adaptadores
        spinnerCliente.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, clientes));
        spinnerTipoEvento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, eventos));
        spinnerRepartidor.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, repartidores));
        spinnerEstado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados));
    }

    private void mostrarDateTimePicker() {
        final int año = calendario.get(Calendar.YEAR);
        final int mes = calendario.get(Calendar.MONTH);
        final int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendario.set(Calendar.YEAR, year);
            calendario.set(Calendar.MONTH, month);
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                calendario.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendario.set(Calendar.MINUTE, minute);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                editTextFechaHora.setText(sdf.format(calendario.getTime()));

            }, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), true);

            timePickerDialog.show();

        }, año, mes, dia);

        datePickerDialog.show();
    }

    private void guardarPedido() {
        String cliente = spinnerCliente.getSelectedItem().toString();
        String evento = spinnerTipoEvento.getSelectedItem().toString();
        String repartidor = spinnerRepartidor.getSelectedItem().toString();
        String estado = spinnerEstado.getSelectedItem().toString();
        String fechaHora = editTextFechaHora.getText().toString();

        if (cliente.equals("Seleccione") || repartidor.equals("Seleccione") || fechaHora.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Aquí puedes enviar los datos a la BD o DAO
        String mensaje = "Pedido guardado:\nCliente: " + cliente +
                "\nRepartidor: " + repartidor +
                "\nEvento: " + evento +
                "\nFecha/Hora: " + fechaHora +
                "\nEstado: " + estado;

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}
