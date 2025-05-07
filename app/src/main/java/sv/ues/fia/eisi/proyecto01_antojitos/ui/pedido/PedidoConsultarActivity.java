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

public class PedidoConsultarActivity extends AppCompatActivity {

    private EditText editTextIdPedido;
    private Button btnBuscar;
    private TextView tvResultado;

    private PedidoDAO pedidoDAO;
    private ClienteDAO clienteDAO;
    private RepartidorDAO repartidorDAO;
    private TipoEventoDAO tipoEventoDAO;
    private SucursalDAO sucursalDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_consultar);

        editTextIdPedido = findViewById(R.id.editTextIdPedido);
        btnBuscar = findViewById(R.id.btnBuscarPedido);
        tvResultado = findViewById(R.id.tvResultadoPedido);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        pedidoDAO = new PedidoDAO(db);
        clienteDAO = new ClienteDAO(db);
        repartidorDAO = new RepartidorDAO(db);
        tipoEventoDAO = new TipoEventoDAO(db);
        sucursalDAO = new SucursalDAO(db);

        btnBuscar.setOnClickListener(v -> buscarPedido());
    }

    private void buscarPedido() {
        String input = editTextIdPedido.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(this, "Ingrese un ID válido", Toast.LENGTH_SHORT).show();
            return;
        }

        int idPedido = Integer.parseInt(input);
        Pedido pedido = pedidoDAO.consultarPorId(idPedido);

        if (pedido != null) {
            Cliente cliente = clienteDAO.consultarPorId(pedido.getIdCliente());
            Repartidor repartidor = repartidorDAO.obtenerPorId(pedido.getIdRepartidor());
            TipoEvento tipoEvento = tipoEventoDAO.consultarPorId(pedido.getIdTipoEvento());
            Sucursal sucursal = sucursalDAO.obtenerPorId(pedido.getIdSucursal());

            String nombreSucursal = (sucursal != null) ? sucursal.getNombreSucursal() : "Desconocida";
            String estadoActivo = (pedido.getActivoPedido() == 1) ? "Activo" : "Inactivo";
            String nombreCliente = (cliente != null) ? cliente.getNombreCliente() : "Desconocido";
            String nombreRepartidor = (repartidor != null) ? repartidor.getNombreRepartidor() : "Desconocido";
            String nombreTipoEvento = (tipoEvento != null) ? tipoEvento.getNombreTipoEvento() : "Ninguno";

            String resultado = "ID Pedido: " + pedido.getIdPedido() +
                    "\nID Cliente: " + pedido.getIdCliente() + " — " + nombreCliente +
                    "\nID Repartidor: " + pedido.getIdRepartidor() + " — " + nombreRepartidor +
                    "\nID Tipo Evento: " + pedido.getIdTipoEvento() + " — " + nombreTipoEvento +
                    "\nFecha/Hora: " + pedido.getFechaHoraPedido() +
                    "\nEstado: " + pedido.getEstadoPedido() +
                    "\nSucursal: " + pedido.getIdSucursal() + " — " + nombreSucursal +
                    "\nEstado del Pedido: " + estadoActivo;


            tvResultado.setText(resultado);
        } else {
            tvResultado.setText("No se encontró un pedido con ese ID.");
        }
    }
}
