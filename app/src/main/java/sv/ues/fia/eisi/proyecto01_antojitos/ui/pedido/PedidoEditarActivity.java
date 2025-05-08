package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
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

    private Spinner spinnerPedidoBuscar, spinnerCliente, spinnerRepartidor, spinnerEvento, spinnerEstado, spinnerSucursal;
    private EditText editTextFecha;
    private Button btnActualizar;
    private Switch switchActivo;

    private PedidoDAO pedidoDAO;
    private ClienteDAO clienteDAO;
    private RepartidorDAO repartidorDAO;
    private TipoEventoDAO tipoEventoDAO;
    private SucursalDAO sucursalDAO;

    private List<Pedido> pedidos;
    private List<Cliente> clientes;
    private List<Repartidor> repartidores;
    private List<TipoEvento> eventos;
    private List<Sucursal> sucursales;

    private Map<String, Pedido> pedidosMap = new HashMap<>();
    private Pedido pedidoActual;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_editar);

        // UI
        spinnerPedidoBuscar = findViewById(R.id.spinnerPedidoBuscar);
        spinnerCliente = findViewById(R.id.spinnerCliente);
        spinnerRepartidor = findViewById(R.id.spinnerRepartidor);
        spinnerEvento = findViewById(R.id.spinnerEvento);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        spinnerSucursal = findViewById(R.id.spinnerSucursal);
        editTextFecha = findViewById(R.id.editTextFecha);
        btnActualizar = findViewById(R.id.btnActualizar);
        switchActivo = findViewById(R.id.switchActivo);

        calendar = Calendar.getInstance();

        // DB
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        pedidoDAO = new PedidoDAO(db);
        clienteDAO = new ClienteDAO(db);
        repartidorDAO = new RepartidorDAO(db);
        tipoEventoDAO = new TipoEventoDAO(db);
        sucursalDAO = new SucursalDAO(db);

        cargarSpinners();
        cargarPedidosEnSpinner();

        spinnerPedidoBuscar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    pedidoActual = null;
                    btnActualizar.setEnabled(false);
                    return;
                }

                String label = parent.getItemAtPosition(position).toString();
                pedidoActual = pedidosMap.get(label);
                if (pedidoActual != null) {
                    mostrarDatosPedido(pedidoActual);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        editTextFecha.setOnClickListener(v -> mostrarDateTimePicker());
        btnActualizar.setOnClickListener(v -> actualizarPedido());
    }

    private void cargarSpinners() {
        clientes = clienteDAO.obtenerTodos();
        repartidores = repartidorDAO.obtenerTodos();
        eventos = tipoEventoDAO.obtenerTodos();
        sucursales = sucursalDAO.obtenerTodos();

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

        List<String> sucursalItems = new ArrayList<>();
        sucursalItems.add("Seleccione");
        for (Sucursal s : sucursales) {
            sucursalItems.add(s.getIdSucursal() + " - " + s.getNombreSucursal());
        }

        spinnerCliente.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, clienteItems));
        spinnerRepartidor.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, repartidorItems));
        spinnerEvento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, eventoItems));
        spinnerEstado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados));
        spinnerSucursal.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sucursalItems));
    }

    private void cargarPedidosEnSpinner() {
        pedidos = pedidoDAO.obtenerTodosIncluyendoInactivos(); // usar metodo que obtenga todos
        List<String> items = new ArrayList<>();
        items.add("Seleccione");

        for (Pedido p : pedidos) {
            String label = "Pedido " + p.getIdPedido();
            if (p.getActivoPedido() == 0) {
                label += " (inactivo)";
            }
            items.add(label);
            pedidosMap.put(label, p);
        }

        spinnerPedidoBuscar.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void mostrarDatosPedido(Pedido pedido) {
        spinnerCliente.setSelection(obtenerIndicePorId(spinnerCliente, pedido.getIdCliente()));
        spinnerRepartidor.setSelection(obtenerIndicePorId(spinnerRepartidor, pedido.getIdRepartidor()));
        spinnerEvento.setSelection(obtenerIndicePorId(spinnerEvento, pedido.getIdTipoEvento()));
        spinnerEstado.setSelection(obtenerIndiceTexto(spinnerEstado, pedido.getEstadoPedido()));
        editTextFecha.setText(pedido.getFechaHoraPedido());
        spinnerSucursal.setSelection(obtenerIndicePorId(spinnerSucursal, pedido.getIdSucursal()));
        switchActivo.setChecked(pedido.getActivoPedido() == 1);
        btnActualizar.setEnabled(true);
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
            cargarPedidosEnSpinner(); // refrescar spinner
            spinnerPedidoBuscar.setSelection(0); // opcional: volver a "Seleccione"
            btnActualizar.setEnabled(false);
            limpiarCampos(); // si quieres limpiar los campos
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

    private void limpiarCampos() {
        spinnerCliente.setSelection(0);
        spinnerRepartidor.setSelection(0);
        spinnerEvento.setSelection(0);
        spinnerEstado.setSelection(0);
        spinnerSucursal.setSelection(0);
        editTextFecha.setText("");
        switchActivo.setChecked(false);
    }
}
