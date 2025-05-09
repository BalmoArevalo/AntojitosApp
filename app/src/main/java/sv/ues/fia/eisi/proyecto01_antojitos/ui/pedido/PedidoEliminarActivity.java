package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.*;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente.*;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento.*;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor.*;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal.*;

public class PedidoEliminarActivity extends AppCompatActivity {

    private Spinner spinnerEliminarPedidos;
    private TextView textViewResultado;
    private Button btnBuscar, btnEliminar;

    private PedidoDAO pedidoDAO;
    private ClienteDAO clienteDAO;
    private RepartidorDAO repartidorDAO;
    private TipoEventoDAO tipoEventoDAO;
    private SucursalDAO sucursalDAO;

    private Pedido pedidoSeleccionado;
    private List<Pedido> listaPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_eliminar);

        spinnerEliminarPedidos = findViewById(R.id.spinnerEliminarPedidos);
        textViewResultado = findViewById(R.id.textViewResultadoEliminar);
        btnBuscar = findViewById(R.id.btnBuscarEliminar);
        btnEliminar = findViewById(R.id.btnEliminar);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        pedidoDAO = new PedidoDAO(db);
        clienteDAO = new ClienteDAO(db);
        repartidorDAO = new RepartidorDAO(db);
        tipoEventoDAO = new TipoEventoDAO(db);
        sucursalDAO = new SucursalDAO(db);

        cargarPedidosEnSpinner();

        btnEliminar.setEnabled(false);

        btnBuscar.setOnClickListener(v -> buscarPedido());
        btnEliminar.setOnClickListener(v -> eliminarPedido());
    }

    private void cargarPedidosEnSpinner() {
        listaPedidos = pedidoDAO.obtenerTodos();

        List<String> opciones = new ArrayList<>();
        for (Pedido p : listaPedidos) {
            opciones.add("ID: " + p.getIdPedido() + " — " + p.getEstadoPedido());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEliminarPedidos.setAdapter(adapter);
    }

    private void buscarPedido() {
        int posicion = spinnerEliminarPedidos.getSelectedItemPosition();
        if (posicion == AdapterView.INVALID_POSITION || listaPedidos.isEmpty()) {
            Toast.makeText(this, getString(R.string.pedido_eliminar_toast_seleccion_invalida), Toast.LENGTH_SHORT).show();
            return;
        }

        pedidoSeleccionado = listaPedidos.get(posicion);

        Cliente cliente = clienteDAO.consultarPorId(pedidoSeleccionado.getIdCliente());
        Repartidor repartidor = repartidorDAO.obtenerPorId(pedidoSeleccionado.getIdRepartidor());
        TipoEvento tipoEvento = tipoEventoDAO.consultarPorId(pedidoSeleccionado.getIdTipoEvento());
        Sucursal sucursal = sucursalDAO.obtenerPorId(pedidoSeleccionado.getIdSucursal());

        String nombreSucursal = (sucursal != null) ? sucursal.getNombreSucursal() : getString(R.string.pedido_eliminar_nombre_sucursal_desconocida);
        String estadoActivo = (pedidoSeleccionado.getActivoPedido() == 1) ? getString(R.string.pedido_eliminar_estado_activo) : getString(R.string.pedido_eliminar_estado_inactivo);
        String nombreCliente = (cliente != null) ? cliente.getNombreCliente() : getString(R.string.pedido_eliminar_nombre_cliente_desconocido);
        String nombreRepartidor = (repartidor != null) ? repartidor.getNombreRepartidor() : getString(R.string.pedido_eliminar_nombre_repartidor_desconocido);
        String nombreTipoEvento = (tipoEvento != null) ? tipoEvento.getNombreTipoEvento() : getString(R.string.pedido_eliminar_nombre_evento_ninguno);

        String info = getString(R.string.pedido_eliminar_resultado,
                pedidoSeleccionado.getIdPedido(),
                pedidoSeleccionado.getIdCliente(), nombreCliente,
                pedidoSeleccionado.getIdRepartidor(), nombreRepartidor,
                pedidoSeleccionado.getIdTipoEvento(), nombreTipoEvento,
                pedidoSeleccionado.getIdSucursal(), nombreSucursal,
                pedidoSeleccionado.getFechaHoraPedido(),
                pedidoSeleccionado.getEstadoPedido(),
                estadoActivo
        );

        textViewResultado.setText(info);
        btnEliminar.setEnabled(true);
    }

    private void eliminarPedido() {
        if (pedidoSeleccionado != null) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.pedido_eliminar_dialog_titulo))
                    .setMessage(getString(R.string.pedido_eliminar_dialog_mensaje, pedidoSeleccionado.getIdPedido()))
                    .setPositiveButton(getString(R.string.pedido_eliminar_dialog_confirmar), (dialog, which) -> {
                        int resultado = pedidoDAO.eliminar(pedidoSeleccionado.getIdPedido());

                        switch (resultado) {
                            case 2:
                                Toast.makeText(this, getString(R.string.pedido_eliminar_resultado_ok), Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(this, getString(R.string.pedido_eliminar_resultado_desactivado), Toast.LENGTH_LONG).show();
                                break;
                            case 0:
                                Toast.makeText(this, getString(R.string.pedido_eliminar_resultado_asociado), Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(this, getString(R.string.pedido_eliminar_resultado_error), Toast.LENGTH_SHORT).show();
                        }

                        textViewResultado.setText("");
                        btnEliminar.setEnabled(false);
                        cargarPedidosEnSpinner();
                        pedidoSeleccionado = null;
                    })
                    .setNegativeButton(getString(R.string.pedido_eliminar_dialog_cancelar), null)
                    .show();
        }
    }
}