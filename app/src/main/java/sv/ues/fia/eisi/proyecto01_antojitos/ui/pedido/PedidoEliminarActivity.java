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
        listaPedidos = pedidoDAO.obtenerTodos(); // Solo activos si ya aplicaste el filtro

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
            Toast.makeText(this, "Seleccione un pedido válido", Toast.LENGTH_SHORT).show();
            return;
        }

        pedidoSeleccionado = listaPedidos.get(posicion);

        Cliente cliente = clienteDAO.consultarPorId(pedidoSeleccionado.getIdCliente());
        Repartidor repartidor = repartidorDAO.obtenerPorId(pedidoSeleccionado.getIdRepartidor());
        TipoEvento tipoEvento = tipoEventoDAO.consultarPorId(pedidoSeleccionado.getIdTipoEvento());
        Sucursal sucursal = sucursalDAO.obtenerPorId(pedidoSeleccionado.getIdSucursal());

        String nombreSucursal = (sucursal != null) ? sucursal.getNombreSucursal() : "Desconocida";
        String estadoActivo = (pedidoSeleccionado.getActivoPedido() == 1) ? "Activo" : "Inactivo";
        String nombreCliente = (cliente != null) ? cliente.getNombreCliente() : "Desconocido";
        String nombreRepartidor = (repartidor != null) ? repartidor.getNombreRepartidor() : "Desconocido";
        String nombreTipoEvento = (tipoEvento != null) ? tipoEvento.getNombreTipoEvento() : "Ninguno";

        String info = "Pedido encontrado:\n" +
                "ID Pedido: " + pedidoSeleccionado.getIdPedido() + "\n" +
                "ID Cliente: " + pedidoSeleccionado.getIdCliente() + " — " + nombreCliente + "\n" +
                "ID Repartidor: " + pedidoSeleccionado.getIdRepartidor() + " — " + nombreRepartidor + "\n" +
                "ID Tipo Evento: " + pedidoSeleccionado.getIdTipoEvento() + " — " + nombreTipoEvento + "\n" +
                "ID Sucursal: " + pedidoSeleccionado.getIdSucursal() + " — " + nombreSucursal + "\n" +
                "Fecha/Hora: " + pedidoSeleccionado.getFechaHoraPedido() + "\n" +
                "Estado: " + pedidoSeleccionado.getEstadoPedido() + "\n" +
                "Activo: " + estadoActivo;

        textViewResultado.setText(info);
        btnEliminar.setEnabled(true);
    }

    private void eliminarPedido() {
        if (pedidoSeleccionado != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro de que deseas eliminar el pedido ID " + pedidoSeleccionado.getIdPedido() + "? Esta acción no se puede deshacer.")
                    .setPositiveButton("Sí, eliminar", (dialog, which) -> {
                        int resultado = pedidoDAO.eliminar(pedidoSeleccionado.getIdPedido());

                        switch (resultado) {
                            case 2:
                                Toast.makeText(this, "✅ Pedido eliminado correctamente", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(this, "⚠️ Pedido desactivado (asociado a detalle o reparto)", Toast.LENGTH_LONG).show();
                                break;
                            case 0:
                                Toast.makeText(this, "❌ No se puede eliminar: está asociado a una factura", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(this, "❌ Error al intentar eliminar el pedido", Toast.LENGTH_SHORT).show();
                        }

                        // Limpiar y actualizar
                        textViewResultado.setText("");
                        btnEliminar.setEnabled(false);
                        cargarPedidosEnSpinner();
                        pedidoSeleccionado = null;
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        }
    }
}

