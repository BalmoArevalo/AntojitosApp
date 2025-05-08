package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider; // Aunque no usemos ViewModel activamente aquí, lo mantenemos si se inicializó

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
// Importar DAOs y POJOs necesarios
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.Factura;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido.Pedido;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido.PedidoDAO; // Asegúrate que este DAO existe y tiene consultarPorId


public class FacturaConsultarActivity extends AppCompatActivity {

    private static final String TAG = "FacturaConsultarAct";

    // UI Components
    private Spinner spinnerPedidoFactura;
    private ScrollView scrollViewResultados;
    // TextViews para Factura
    private TextView tvFacturaId, tvFacturaFecha, tvFacturaMonto, tvFacturaTipoPago, tvFacturaEstado, tvFacturaEsCredito;
    // TextViews para Pedido
    private TextView tvPedidoId, tvPedidoClienteId, tvPedidoSucursalId, tvPedidoRepartidorId, tvPedidoFechaHora, tvPedidoEstado;

    // Data and Helpers
    private DBHelper dbHelper;
    private FacturaDAO facturaDAO;
    private PedidoDAO pedidoDAO; // Necesitamos este DAO
    private List<Integer> pedidoConFacturaIds = new ArrayList<>();
    private SQLiteDatabase dbRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura_consultar); // Usar el layout simplificado
        setTitle(getString(R.string.factura_consultar_s_title)); // Usar string estandarizado

        // Inicializar Helper/DB y DAOs
        dbHelper = new DBHelper(this);
        dbRead = dbHelper.getReadableDatabase();
        facturaDAO = new FacturaDAO(dbRead);
        pedidoDAO = new PedidoDAO(dbRead); // Instanciar PedidoDAO

        // Inicializar Vistas
        spinnerPedidoFactura = findViewById(R.id.spinnerConsultaFacturaPedido);
        scrollViewResultados = findViewById(R.id.scrollViewConsultaFactura);

        // TextViews Factura
        tvFacturaId = findViewById(R.id.tvConsultaFacturaId);
        tvFacturaFecha = findViewById(R.id.tvConsultaFacturaFecha);
        tvFacturaMonto = findViewById(R.id.tvConsultaFacturaMonto);
        tvFacturaTipoPago = findViewById(R.id.tvConsultaFacturaTipoPago);
        tvFacturaEstado = findViewById(R.id.tvConsultaFacturaEstado);
        tvFacturaEsCredito = findViewById(R.id.tvConsultaFacturaEsCredito);

        // TextViews Pedido (Usando IDs del layout simplificado)
        tvPedidoId = findViewById(R.id.tvConsultaPedidoId);
        tvPedidoClienteId = findViewById(R.id.tvConsultaPedidoClienteId);
        tvPedidoSucursalId = findViewById(R.id.tvConsultaPedidoSucursalId);
        tvPedidoRepartidorId = findViewById(R.id.tvConsultaPedidoRepartidorId);
        tvPedidoFechaHora = findViewById(R.id.tvConsultaPedidoFechaHora);
        tvPedidoEstado = findViewById(R.id.tvConsultaPedidoEstado);

        // Estado inicial
        ocultarTodosLosDetalles();
        cargarPedidosConFactura();
        configurarListenerSpinner();
    }

    private void cargarPedidosConFactura() {
        pedidoConFacturaIds.clear();
        List<String> items = new ArrayList<>();
        // Usar string resource estandarizado
        items.add(getString(R.string.placeholder_seleccione));
        pedidoConFacturaIds.add(-1);

        String query = "SELECT P.ID_PEDIDO FROM PEDIDO P INNER JOIN FACTURA F ON P.ID_PEDIDO = F.ID_PEDIDO ORDER BY P.ID_PEDIDO ASC";
        Cursor cursor = null;
        try {
            cursor = dbRead.rawQuery(query, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                pedidoConFacturaIds.add(id);
                items.add("Pedido #" + id);
            }
            Log.d(TAG, "Pedidos CON factura cargados: " + (items.size() - 1));
        } catch (SQLiteException e) {
            Log.e(TAG, "Error cargando pedidos con factura", e);
            // Usar string resource estandarizado
            Toast.makeText(this, String.format(getString(R.string.direccion_crear_toast_error_carga), "Pedidos con Factura"), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPedidoFactura.setAdapter(adapter);
        spinnerPedidoFactura.setEnabled(items.size() > 1);
    }


    private void configurarListenerSpinner() {
        spinnerPedidoFactura.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    int selectedPedidoId = pedidoConFacturaIds.get(position);
                    Log.d(TAG, "Pedido seleccionado: " + selectedPedidoId + ". Cargando detalles...");
                    cargarYMostrarDatos(selectedPedidoId);
                } else {
                    ocultarTodosLosDetalles();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                ocultarTodosLosDetalles();
            }
        });
    }

    // Carga y muestra datos de Factura y Pedido (SIN detalles)
    private void cargarYMostrarDatos(int pedidoId) {
        Factura factura = facturaDAO.consultarPorIdPedido(pedidoId);
        Pedido pedido = pedidoDAO.consultarPorId(pedidoId); // Usar PedidoDAO

        // Validar que ambos objetos se cargaron
        if (factura == null) {
            Toast.makeText(this, R.string.factura_consultar_s_toast_no_factura, Toast.LENGTH_SHORT).show();
            ocultarTodosLosDetalles();
            return;
        }
        if (pedido == null) {
            Toast.makeText(this, R.string.factura_consultar_s_toast_no_pedido, Toast.LENGTH_SHORT).show();
            ocultarTodosLosDetalles();
            return;
        }

        // Mostrar detalles
        mostrarDetallesFactura(factura);
        mostrarDetallesPedido(pedido);
        scrollViewResultados.setVisibility(View.VISIBLE); // Mostrar contenedor
    }


    private void mostrarDetallesFactura(Factura f) {
        tvFacturaId.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.factura_consultar_s_label_id_factura), f.getIdFactura()));
        tvFacturaFecha.setText(String.format("%s %s", getString(R.string.factura_consultar_s_label_fecha_emision), f.getFechaEmision()));
        tvFacturaMonto.setText(String.format(Locale.US, "%s $%.2f", getString(R.string.factura_consultar_s_label_monto_total), f.getMontoTotal()));
        tvFacturaTipoPago.setText(String.format("%s %s", getString(R.string.factura_consultar_s_label_tipo_pago), f.getTipoPago()));
        tvFacturaEstado.setText(String.format("%s %s", getString(R.string.factura_consultar_s_label_estado_factura), f.getEstadoFactura()));
        tvFacturaEsCredito.setText(String.format("%s %s", getString(R.string.factura_consultar_s_label_es_credito),
                f.getEsCredito() == 1 ? getString(R.string.factura_consultar_s_valor_si) : getString(R.string.factura_consultar_s_valor_no)));
    }

    private void mostrarDetallesPedido(Pedido p) {
        // Muestra IDs según layout simplificado y strings
        tvPedidoId.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.factura_consultar_s_label_id_pedido), p.getIdPedido()));
        tvPedidoClienteId.setText(String.format(Locale.getDefault(), "%s %s", getString(R.string.factura_consultar_s_label_id_cliente), p.getIdCliente() > 0 ? p.getIdCliente() : getString(R.string.factura_consultar_s_valor_no_definido)));
        tvPedidoSucursalId.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.factura_consultar_s_label_id_sucursal), p.getIdSucursal()));
        tvPedidoRepartidorId.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.factura_consultar_s_label_id_repartidor), p.getIdRepartidor()));
        tvPedidoFechaHora.setText(String.format("%s %s", getString(R.string.factura_consultar_s_label_fecha_hora_pedido), p.getFechaHoraPedido()));
        tvPedidoEstado.setText(String.format("%s %s", getString(R.string.factura_consultar_s_label_estado_pedido), p.getEstadoPedido()));
    }

    private void ocultarTodosLosDetalles(){
        scrollViewResultados.setVisibility(View.GONE);
        // Limpiar todos los TextViews
        tvFacturaId.setText(""); tvFacturaFecha.setText(""); tvFacturaMonto.setText("");
        tvFacturaTipoPago.setText(""); tvFacturaEstado.setText(""); tvFacturaEsCredito.setText("");
        tvPedidoId.setText(""); tvPedidoClienteId.setText(""); tvPedidoSucursalId.setText("");
        tvPedidoRepartidorId.setText(""); tvPedidoFechaHora.setText(""); tvPedidoEstado.setText("");
        Log.d(TAG, "Detalles ocultados.");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cerrar la conexión de lectura si se mantuvo abierta
        if (dbRead != null && dbRead.isOpen()) {
            dbRead.close();
            Log.d(TAG,"Base de datos de lectura cerrada.");
        }
        // No es necesario cerrar dbHelper explícitamente aquí si la app lo gestiona globalmente.
    }
}