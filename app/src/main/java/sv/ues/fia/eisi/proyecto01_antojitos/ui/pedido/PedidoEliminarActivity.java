package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

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

public class PedidoEliminarActivity extends AppCompatActivity {

    private EditText editTextIdEliminar;
    private TextView textViewResultado;
    private Button btnBuscar, btnEliminar;
    private PedidoDAO pedidoDAO;
    private ClienteDAO clienteDAO;
    private RepartidorDAO repartidorDAO;
    private TipoEventoDAO tipoEventoDAO;
    private SucursalDAO sucursalDAO;

    private Pedido pedidoEncontrado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_eliminar);

        // Inicializar vistas
        editTextIdEliminar = findViewById(R.id.editTextIdEliminar);
        textViewResultado = findViewById(R.id.textViewResultadoEliminar);
        btnBuscar = findViewById(R.id.btnBuscarEliminar);
        btnEliminar = findViewById(R.id.btnEliminar);

        // Inicializar BD y DAOs
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        pedidoDAO = new PedidoDAO(db);
        clienteDAO = new ClienteDAO(db);
        repartidorDAO = new RepartidorDAO(db);
        tipoEventoDAO = new TipoEventoDAO(db);
        sucursalDAO = new SucursalDAO(db);

        btnEliminar.setEnabled(false);

        btnBuscar.setOnClickListener(v -> buscarPedido());
        btnEliminar.setOnClickListener(v -> eliminarPedido());
    }

    private void buscarPedido() {
        String idStr = editTextIdEliminar.getText().toString().trim();
        if (idStr.isEmpty()) {
            Toast.makeText(this, "Ingrese un ID válido", Toast.LENGTH_SHORT).show();
            return;
        }

        int idPedido = Integer.parseInt(idStr);
        pedidoEncontrado = pedidoDAO.consultarPorId(idPedido);

        if (pedidoEncontrado != null) {
            // Buscar nombres asociados
            Cliente cliente = clienteDAO.consultarPorId(pedidoEncontrado.getIdCliente());
            Repartidor repartidor = repartidorDAO.obtenerPorId(pedidoEncontrado.getIdRepartidor());
            TipoEvento tipoEvento = tipoEventoDAO.consultarPorId(pedidoEncontrado.getIdTipoEvento());
            Sucursal sucursal = sucursalDAO.obtenerPorId(pedidoEncontrado.getIdSucursal());

            String nombreSucursal = (sucursal != null) ? sucursal.getNombreSucursal() : "Desconocida";
            String estadoActivo = (pedidoEncontrado.getActivoPedido() == 1) ? "Activo" : "Inactivo";
            String nombreCliente = (cliente != null) ? cliente.getNombreCliente() : "Desconocido";
            String nombreRepartidor = (repartidor != null) ? repartidor.getNombreRepartidor() : "Desconocido";
            String nombreTipoEvento = (tipoEvento != null) ? tipoEvento.getNombreTipoEvento() : "Ninguno";

            String info = "Pedido encontrado:\n" +
                    "ID Pedido: " + pedidoEncontrado.getIdPedido() + "\n" +
                    "ID Cliente: " + pedidoEncontrado.getIdCliente() + " — " + nombreCliente + "\n" +
                    "ID Repartidor: " + pedidoEncontrado.getIdRepartidor() + " — " + nombreRepartidor + "\n" +
                    "ID Tipo Evento: " + pedidoEncontrado.getIdTipoEvento() + " — " + nombreTipoEvento + "\n" +
                    "ID Sucursal: " + pedidoEncontrado.getIdSucursal() + " — " + nombreSucursal + "\n" +
                    "Fecha/Hora: " + pedidoEncontrado.getFechaHoraPedido() + "\n" +
                    "Estado: " + pedidoEncontrado.getEstadoPedido() + "\n" +
                    "Activo: " + estadoActivo;

            textViewResultado.setText(info);
            btnEliminar.setEnabled(true);
        } else {
            textViewResultado.setText("No se encontró el pedido.");
            btnEliminar.setEnabled(false);
        }
    }

    private void eliminarPedido() {
        if (pedidoEncontrado != null) {
            int resultado = pedidoDAO.eliminar(pedidoEncontrado.getIdPedido());

            switch (resultado) {
                case 2:
                    Toast.makeText(this, "✅ Pedido eliminado correctamente", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(this, "⚠️ Pedido no se eliminó, pero fue desactivado (está asociado a detalle o reparto)", Toast.LENGTH_LONG).show();
                    break;
                case 0:
                    Toast.makeText(this, "❌ No se puede eliminar el pedido: está asociado a una factura", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(this, "❌ Error al intentar eliminar el pedido", Toast.LENGTH_SHORT).show();
            }

            // Resetear interfaz
            textViewResultado.setText("");
            editTextIdEliminar.setText("");
            btnEliminar.setEnabled(false);
            pedidoEncontrado = null;
        }
    }
}
