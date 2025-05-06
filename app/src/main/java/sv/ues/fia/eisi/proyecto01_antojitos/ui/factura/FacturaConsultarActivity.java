package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer; // Importar Observer
import androidx.lifecycle.ViewModelProvider;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout; // Para el contenedor de resultados
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class FacturaConsultarActivity extends AppCompatActivity {

    // ViewModel
    private FacturaViewModel facturaViewModel;

    // Componentes UI Entrada
    private Spinner spinnerConsultaPedido;
    private EditText editConsultaFacturaId;
    private Button btnConsultarFactura;

    // Componentes UI Resultados
    private LinearLayout layoutResultados; // Contenedor para mostrar/ocultar
    private TextView tvResultadoFecha;
    private TextView tvResultadoMonto;
    private TextView tvResultadoTipoPago;
    private TextView tvResultadoPagado;

    // Datos para Spinner
    private List<Integer> consultaPedidoIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura_consultar); // Crear este layout

        // Inicializar ViewModel
        facturaViewModel = new ViewModelProvider(this).get(FacturaViewModel.class);

        // Inicializar Vistas de Entrada
        spinnerConsultaPedido = findViewById(R.id.spinnerConsultaPedido);
        editConsultaFacturaId = findViewById(R.id.editConsultaFacturaId);
        btnConsultarFactura = findViewById(R.id.btnConsultarFactura);

        // Inicializar Vistas de Resultados
        layoutResultados = findViewById(R.id.layoutResultados);
        tvResultadoFecha = findViewById(R.id.tvResultadoFecha);
        tvResultadoMonto = findViewById(R.id.tvResultadoMonto);
        tvResultadoTipoPago = findViewById(R.id.tvResultadoTipoPago);
        tvResultadoPagado = findViewById(R.id.tvResultadoPagado);

        // Cargar Spinner de Pedidos
        cargarSpinnerConsultaPedidos();

        // Ocultar resultados inicialmente
        ocultarResultados();

        // Configurar Listener del botón
        btnConsultarFactura.setOnClickListener(v -> realizarConsulta());

        // --- Observar cambios en la factura seleccionada desde el ViewModel ---
        facturaViewModel.getFacturaSeleccionada().observe(this, new Observer<Factura>() {
            @Override
            public void onChanged(Factura factura) {
                if (factura != null) {
                    // Factura encontrada, mostrar datos
                    mostrarResultados(factura);
                } else {
                    // Factura no encontrada (o error en la consulta)
                    // El ViewModel debería haber limpiado el LiveData o puesto null
                    Toast.makeText(FacturaConsultarActivity.this, "Factura no encontrada", Toast.LENGTH_SHORT).show();
                    ocultarResultados();
                }
            }
        });
    }

    private void cargarSpinnerConsultaPedidos() {
        consultaPedidoIds.clear();
        List<String> pedidoDescripciones = new ArrayList<>();
        pedidoDescripciones.add("Seleccione un Pedido...");
        consultaPedidoIds.add(-1); // Placeholder ID

        DBHelper dbHelper = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT ID_PEDIDO FROM PEDIDO ORDER BY ID_PEDIDO ASC", null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                consultaPedidoIds.add(id);
                pedidoDescripciones.add("Pedido #" + id);
            }
        } catch (SQLiteException e) {
            Log.e("FacturaConsultar", "Error al cargar pedidos", e);
            Toast.makeText(this, "Error al cargar pedidos", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, pedidoDescripciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConsultaPedido.setAdapter(adapter);
    }

    private void realizarConsulta() {
        // Validar entrada
        int pedidoPos = spinnerConsultaPedido.getSelectedItemPosition();
        if (pedidoPos <= 0 || consultaPedidoIds.get(pedidoPos) == -1) {
            Toast.makeText(this, "Seleccione un pedido válido", Toast.LENGTH_SHORT).show();
            ocultarResultados(); // Ocultar si la selección cambia a inválida
            return;
        }
        int selectedPedidoId = consultaPedidoIds.get(pedidoPos);

        String facturaIdStr = editConsultaFacturaId.getText().toString().trim();
        if (facturaIdStr.isEmpty()) {
            Toast.makeText(this, "Ingrese el ID de la factura a consultar", Toast.LENGTH_SHORT).show();
            ocultarResultados();
            return;
        }

        int inputFacturaId;
        try {
            inputFacturaId = Integer.parseInt(facturaIdStr);
            if (inputFacturaId <= 0) {
                Toast.makeText(this, "Ingrese un ID de factura válido (mayor a 0)", Toast.LENGTH_SHORT).show();
                ocultarResultados();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese un ID de factura numérico válido", Toast.LENGTH_SHORT).show();
            ocultarResultados();
            return;
        }

        // Llamar al ViewModel para que realice la consulta.
        // El Observer se encargará de actualizar la UI.
        facturaViewModel.consultarFacturaPorId(selectedPedidoId, inputFacturaId);
    }

    private void mostrarResultados(Factura factura) {
        if (layoutResultados != null) layoutResultados.setVisibility(View.VISIBLE);
        if (tvResultadoFecha != null) tvResultadoFecha.setText(factura.getFechaEmision());
        if (tvResultadoMonto != null) tvResultadoMonto.setText(String.format(Locale.getDefault(), "$%.2f", factura.getMontoTotal())); // Formatear moneda
        if (tvResultadoTipoPago != null) tvResultadoTipoPago.setText(factura.getTipoPago());
        if (tvResultadoPagado != null) tvResultadoPagado.setText(factura.getPagado() == 1 ? "Pagado" : "Pendiente");
    }

    private void ocultarResultados() {
        if (layoutResultados != null) layoutResultados.setVisibility(View.GONE);
        // Limpiar campos por si acaso
        if (tvResultadoFecha != null) tvResultadoFecha.setText("");
        if (tvResultadoMonto != null) tvResultadoMonto.setText("");
        if (tvResultadoTipoPago != null) tvResultadoTipoPago.setText("");
        if (tvResultadoPagado != null) tvResultadoPagado.setText("");

        // Podrías también limpiar el LiveData en el ViewModel si fuera necesario
        // facturaViewModel.limpiarFacturaSeleccionada(); // (Necesitarías crear este método)
    }
}