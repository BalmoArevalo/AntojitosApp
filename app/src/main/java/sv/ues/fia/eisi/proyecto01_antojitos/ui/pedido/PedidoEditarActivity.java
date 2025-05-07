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

public class PedidoEditarActivity extends AppCompatActivity {

    private EditText editTextIdBuscar, editTextFecha;
    private Spinner spinnerCliente, spinnerRepartidor, spinnerEvento, spinnerEstado, spinnerSucursal;
    private Button btnBuscar, btnActualizar;

    private Switch switchActivo;
    private SucursalDAO sucursalDAO;

    private PedidoDAO pedidoDAO;
    private ClienteDAO clienteDAO;
    private RepartidorDAO repartidorDAO;
    private TipoEventoDAO tipoEventoDAO;
    private List<Sucursal> sucursales;

    private List<Cliente> clientes;
    private List<Repartidor> repartidores;
    private List<TipoEvento> eventos;

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
        spinnerSucursal = findViewById(R.id.spinnerSucursal);
        btnActualizar = findViewById(R.id.btnActualizar);
        switchActivo = findViewById(R.id.switchActivo);

        calendar = Calendar.getInstance();

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        pedidoDAO = new PedidoDAO(db);
        clienteDAO = new ClienteDAO(db);
        repartidorDAO = new RepartidorDAO(db);
        tipoEventoDAO = new TipoEventoDAO(db);
        sucursalDAO = new SucursalDAO(db);

        cargarSpinners();

        editTextFecha.setOnClickListener(v -> mostrarDateTimePicker());
        btnBuscar.setOnClickListener(v -> buscarPedido());
        btnActualizar.setOnClickListener(v -> actualizarPedido());
    }

    private void cargarSpinners() {
        clientes = clienteDAO.obtenerTodos();
        repartidores = repartidorDAO.obtenerTodos();
        eventos = tipoEventoDAO.obtenerTodos();

        List<String> clienteItems = new ArrayList<>();
        clienteItems.add("Seleccione");
        for (Cliente c : clientes) {
            clienteItems.add(c.getIdCliente() + " - " + c.getNombreCliente());
        }

        List<String> repartidorItems = new ArrayList<>();
        repartidorItems.add("Seleccione");
        for (Repartidor r : repartidores) {
            repartidorItems.add(r.getIdRepartidor() + " - " + r.getNombreRepartidor());
        }

        List<String> eventoItems = new ArrayList<>();
        eventoItems.add("Ninguno");
        for (TipoEvento e : eventos) {
            eventoItems.add(e.getIdTipoEvento() + " - " + e.getNombreTipoEvento());
        }

        List<String> estados = Arrays.asList("Pendiente", "Despachado", "Entregado", "Cancelado");

        spinnerCliente.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, clienteItems));
        spinnerRepartidor.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, repartidorItems));
        spinnerEvento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, eventoItems));
        spinnerEstado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados));

        sucursales = sucursalDAO.obtenerTodos();
        List<String> sucursalItems = new ArrayList<>();
        sucursalItems.add("Seleccione");
        for (Sucursal s : sucursales) {
            sucursalItems.add(s.getIdSucursal() + " - " + s.getNombreSucursal());
        }
        spinnerSucursal.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sucursalItems));

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
            spinnerCliente.setSelection(obtenerIndicePorId(spinnerCliente, pedidoActual.getIdCliente()));
            spinnerRepartidor.setSelection(obtenerIndicePorId(spinnerRepartidor, pedidoActual.getIdRepartidor()));
            spinnerEvento.setSelection(obtenerIndicePorId(spinnerEvento, pedidoActual.getIdTipoEvento()));
            spinnerEstado.setSelection(obtenerIndiceTexto(spinnerEstado, pedidoActual.getEstadoPedido()));
            editTextFecha.setText(pedidoActual.getFechaHoraPedido());
            spinnerSucursal.setSelection(obtenerIndicePorId(spinnerSucursal, pedidoActual.getIdSucursal()));
            switchActivo.setChecked(pedidoActual.getActivoPedido() == 1);
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
        pedidoActual.setIdSucursal(extraerId(spinnerSucursal));
        pedidoActual.setActivoPedido(switchActivo.isChecked() ? 1 : 0);

        int filas = pedidoDAO.actualizar(pedidoActual);
        if (filas > 0) {
            Toast.makeText(this, "Pedido actualizado correctamente", Toast.LENGTH_SHORT).show();
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
