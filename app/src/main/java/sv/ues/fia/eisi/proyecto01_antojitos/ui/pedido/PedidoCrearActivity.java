package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.*;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente.*;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento.*;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor.*;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class PedidoCrearActivity extends AppCompatActivity {

    private Spinner spinnerCliente, spinnerTipoEvento, spinnerRepartidor, spinnerEstado, spinnerSucursal;
    private EditText editTextFechaHora;
    private Button btnGuardar;
    private Calendar calendario;

    private PedidoDAO pedidoDAO;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_crear);

        // UI
        spinnerCliente = findViewById(R.id.spinnerCliente);
        spinnerTipoEvento = findViewById(R.id.spinnerTipoEvento);
        spinnerRepartidor = findViewById(R.id.spinnerRepartidor);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        spinnerSucursal = findViewById(R.id.spinnerSucursal);
        editTextFechaHora = findViewById(R.id.editTextFechaHora);
        btnGuardar = findViewById(R.id.btnGuardarPedido);

        calendario = Calendar.getInstance();

        // Inicializar DB y DAO
        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        pedidoDAO = new PedidoDAO(db);

        // Cargar datos desde la base
        cargarSpinnersDesdeBD();

        // Fecha y hora
        editTextFechaHora.setOnClickListener(v -> mostrarDateTimePicker());

        // Guardar pedido
        btnGuardar.setOnClickListener(v -> guardarPedido());
    }

    private void cargarSpinnersDesdeBD() {
        // Clientes
        ClienteDAO clienteDAO = new ClienteDAO(db);
        List<Cliente> listaClientes = clienteDAO.obtenerTodos();
        List<String> clientes = new ArrayList<>();
        clientes.add("Seleccione");
        for (Cliente c : listaClientes) {
            clientes.add(c.getIdCliente() + " - " + c.getNombreCliente());
        }
        spinnerCliente.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, clientes));

        // Tipo de Evento
        TipoEventoDAO tipoEventoDAO = new TipoEventoDAO(db);
        List<TipoEvento> listaEventos = tipoEventoDAO.obtenerTodos();
        List<String> eventos = new ArrayList<>();
        eventos.add("Ninguno");
        for (TipoEvento t : listaEventos) {
            eventos.add(t.getIdTipoEvento() + " - " + t.getNombreTipoEvento());
        }
        spinnerTipoEvento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, eventos));

        // Repartidores
        RepartidorDAO repartidorDAO = new RepartidorDAO(db);
        List<Repartidor> listaRepartidores = repartidorDAO.obtenerTodos();
        List<String> repartidores = new ArrayList<>();
        repartidores.add("Seleccione");
        for (Repartidor r : listaRepartidores) {
            repartidores.add(r.getIdRepartidor() + " - " + r.getNombreRepartidor() + " " + r.getApellidoRepartidor());
        }
        spinnerRepartidor.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, repartidores));

        // Sucursales
        SucursalDAO sucursalDAO = new SucursalDAO(db);
        List<Sucursal> listaSucursales = sucursalDAO.obtenerTodos();
        List<String> sucursales = new ArrayList<>();
        sucursales.add("Seleccione");
        for (Sucursal s : listaSucursales) {
            sucursales.add(s.getIdSucursal() + " - " + s.getNombreSucursal());
        }
        spinnerSucursal.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sucursales));

        // Estados
        List<String> estados = Arrays.asList("Pendiente", "Despachado", "Entregado", "Cancelado");
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
        pedido.setIdSucursal(extraerId(spinnerSucursal));
        pedido.setActivoPedido(1); // Siempre activo al crearse


        if (spinnerTipoEvento.getSelectedItemPosition() != 0) {
            pedido.setIdTipoEvento(extraerId(spinnerTipoEvento));
        } else {
            pedido.setIdTipoEvento(0); // Opcional
        }

        if (spinnerSucursal.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecciona una sucursal", Toast.LENGTH_SHORT).show();
            return;
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
        spinnerSucursal.setSelection(0);
        editTextFechaHora.setText("");
    }
}
