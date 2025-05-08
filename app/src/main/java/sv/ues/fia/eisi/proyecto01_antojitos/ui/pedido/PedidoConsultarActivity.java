package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.*;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente.*;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento.*;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor.*;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal.*;

public class PedidoConsultarActivity extends AppCompatActivity {

    private Spinner spinnerPedidos;
    private Button btnBuscar;
    private TextView tvResultado;

    private PedidoDAO pedidoDAO;
    private ClienteDAO clienteDAO;
    private RepartidorDAO repartidorDAO;
    private TipoEventoDAO tipoEventoDAO;
    private SucursalDAO sucursalDAO;

    private List<Pedido> listaPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_consultar);

        spinnerPedidos = findViewById(R.id.spinnerPedidos);
        btnBuscar = findViewById(R.id.btnBuscarPedido);
        tvResultado = findViewById(R.id.tvResultadoPedido);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        pedidoDAO = new PedidoDAO(db);
        clienteDAO = new ClienteDAO(db);
        repartidorDAO = new RepartidorDAO(db);
        tipoEventoDAO = new TipoEventoDAO(db);
        sucursalDAO = new SucursalDAO(db);

        cargarPedidosEnSpinner();

        btnBuscar.setOnClickListener(v -> buscarPedidoSeleccionado());
    }

    private void cargarPedidosEnSpinner() {
        listaPedidos = pedidoDAO.obtenerTodos();

        List<String> opciones = new ArrayList<>();
        for (Pedido p : listaPedidos) {
            opciones.add("ID: " + p.getIdPedido() + " — " + p.getEstadoPedido());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPedidos.setAdapter(adapter);
    }

    private void buscarPedidoSeleccionado() {
        int posicion = spinnerPedidos.getSelectedItemPosition();
        if (posicion == AdapterView.INVALID_POSITION || listaPedidos.isEmpty()) {
            Toast.makeText(this, getString(R.string.pedido_toast_seleccion_invalida), Toast.LENGTH_SHORT).show();
            return;
        }

        Pedido pedido = listaPedidos.get(posicion);
        Cliente cliente = clienteDAO.consultarPorId(pedido.getIdCliente());
        Repartidor repartidor = repartidorDAO.obtenerPorId(pedido.getIdRepartidor());
        TipoEvento tipoEvento = tipoEventoDAO.consultarPorId(pedido.getIdTipoEvento());
        Sucursal sucursal = sucursalDAO.obtenerPorId(pedido.getIdSucursal());

        String nombreSucursal = (sucursal != null) ? sucursal.getNombreSucursal() : getString(R.string.pedido_nombre_sucursal_desconocida);
        String estadoActivo = (pedido.getActivoPedido() == 1) ? getString(R.string.pedido_estado_activo) : getString(R.string.pedido_estado_inactivo);
        String nombreCliente = (cliente != null) ? cliente.getNombreCliente() : getString(R.string.pedido_nombre_cliente_desconocido);
        String nombreRepartidor = (repartidor != null) ? repartidor.getNombreRepartidor() : getString(R.string.pedido_nombre_repartidor_desconocido);
        String nombreTipoEvento = (tipoEvento != null) ? tipoEvento.getNombreTipoEvento() : getString(R.string.pedido_nombre_tipo_evento_ninguno);

        String resultado = getString(
                R.string.pedido_resultado_formato,
                pedido.getIdPedido(),
                pedido.getIdCliente(), nombreCliente,
                pedido.getIdRepartidor(), nombreRepartidor,
                pedido.getIdTipoEvento(), nombreTipoEvento,
                pedido.getFechaHoraPedido(),
                pedido.getEstadoPedido(),
                pedido.getIdSucursal(), nombreSucursal,
                estadoActivo
        );

        tvResultado.setText(resultado);
    }
}
