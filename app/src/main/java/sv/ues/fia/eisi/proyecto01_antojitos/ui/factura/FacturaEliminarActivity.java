package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity; // Importar Activity si usas setResult
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors; // Para filtrar fácil (API 24+)

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper; // Puede quitarse si solo usas ViewModel
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.Factura;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaViewModel;

// *** NOMBRE DE CLASE ACTUALIZADO ***
public class FacturaEliminarActivity extends AppCompatActivity {

    private static final String TAG = "FacturaEliminarAct"; // *** TAG ACTUALIZADO ***
    private static final String ESTADO_ANULADA = "Anulada"; // Define el estado final

    // UI Components
    private Spinner spinnerSeleccionarFactura;
    private LinearLayout layoutDetalles;
    private TextView tvFacturaId, tvPedidoId, tvFacturaFecha, tvFacturaMonto, tvFacturaTipoPago, tvFacturaEstado, tvFacturaEsCredito;
    private Button btnConfirmarAnulacion; // El botón para confirmar la acción

    // Data
    private FacturaViewModel facturaViewModel;
    private List<Factura> listaFacturasAnulables = new ArrayList<>();
    private Factura facturaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // *** USAR EL NOMBRE DE LAYOUT CORRECTO ***
        setContentView(R.layout.activity_factura_eliminar);
        setTitle(getString(R.string.factura_eliminar_title)); // *** USAR STRING CORRECTO ***

        facturaViewModel = new ViewModelProvider(this).get(FacturaViewModel.class);

        // Inicializar Vistas (usando IDs del nuevo XML)
        spinnerSeleccionarFactura = findViewById(R.id.spinnerSeleccionarFacturaEliminar);
        layoutDetalles = findViewById(R.id.layoutDetallesFacturaEliminar);
        tvFacturaId = findViewById(R.id.tvEliminarFacturaId);
        tvPedidoId = findViewById(R.id.tvEliminarPedidoId);
        tvFacturaFecha = findViewById(R.id.tvEliminarFacturaFecha);
        tvFacturaMonto = findViewById(R.id.tvEliminarFacturaMonto);
        tvFacturaTipoPago = findViewById(R.id.tvEliminarFacturaTipoPago);
        tvFacturaEstado = findViewById(R.id.tvEliminarFacturaEstado);
        tvFacturaEsCredito = findViewById(R.id.tvEliminarFacturaEsCredito);
        btnConfirmarAnulacion = findViewById(R.id.btnConfirmarAnulacionFactura);

        // Estado inicial
        ocultarDetallesYDeshabilitarBoton();
        configurarListenerSpinner();

        // Observador para cargar el spinner
        facturaViewModel.getListaFacturas().observe(this, facturas -> {
            Log.d(TAG, "LiveData listaFacturas actualizado en Eliminar/Anular. Facturas: " + (facturas != null ? facturas.size() : "null"));
            if (facturas != null) {
                // Filtrar para mostrar solo las NO anuladas
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    listaFacturasAnulables = facturas.stream()
                            .filter(f -> !ESTADO_ANULADA.equalsIgnoreCase(f.getEstadoFactura()))
                            .collect(Collectors.toList());
                } else {
                    listaFacturasAnulables = new ArrayList<>();
                    for(Factura f : facturas) {
                        if (!ESTADO_ANULADA.equalsIgnoreCase(f.getEstadoFactura())) {
                            listaFacturasAnulables.add(f);
                        }
                    }
                }
                cargarSpinnerFacturasAnulables();
            }
        });

        // Cargar todas las facturas al inicio
        facturaViewModel.cargarTodasLasFacturas();

        btnConfirmarAnulacion.setOnClickListener(v -> intentarAnularFactura());
    }

    private void configurarListenerSpinner() {
        spinnerSeleccionarFactura.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && (position - 1) < listaFacturasAnulables.size()) {
                    facturaSeleccionada = listaFacturasAnulables.get(position - 1);
                    mostrarDetalles(facturaSeleccionada);
                    layoutDetalles.setVisibility(View.VISIBLE);
                    // Habilitar botón solo si la factura seleccionada NO es a crédito
                    // y no está ya anulada (aunque el spinner ya las filtra)
                    boolean puedeAnular = facturaSeleccionada.getEsCredito() == 0 &&
                            !ESTADO_ANULADA.equalsIgnoreCase(facturaSeleccionada.getEstadoFactura());
                    btnConfirmarAnulacion.setEnabled(puedeAnular);

                    // Mostrar advertencia si es a crédito y por eso no se puede anular
                    if (facturaSeleccionada.getEsCredito() == 1) {
                        Toast.makeText(FacturaEliminarActivity.this, R.string.factura_eliminar_toast_es_credito, Toast.LENGTH_LONG).show();
                    }
                    // Mostrar advertencia si ya está anulada (aunque no debería aparecer en spinner)
                    else if (!puedeAnular && ESTADO_ANULADA.equalsIgnoreCase(facturaSeleccionada.getEstadoFactura())) {
                        Toast.makeText(FacturaEliminarActivity.this, R.string.factura_eliminar_toast_ya_anulada, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ocultarDetallesYDeshabilitarBoton();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                ocultarDetallesYDeshabilitarBoton();
            }
        });
    }

    private void cargarSpinnerFacturasAnulables() {
        List<String> descripcionesFacturas = new ArrayList<>();
        descripcionesFacturas.add(getString(R.string.placeholder_seleccione)); // Placeholder

        for (Factura f : listaFacturasAnulables) {
            descripcionesFacturas.add(String.format(Locale.getDefault(), "Factura #%d (Pedido #%d) - %s",
                    f.getIdFactura(), f.getIdPedido(), f.getEstadoFactura()));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, descripcionesFacturas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeleccionarFactura.setAdapter(adapter);

        if (descripcionesFacturas.size() <= 1) {
            Toast.makeText(this, R.string.factura_eliminar_no_facturas_disponibles, Toast.LENGTH_LONG).show();
            spinnerSeleccionarFactura.setEnabled(false);
        } else {
            spinnerSeleccionarFactura.setEnabled(true);
        }
    }

    private void mostrarDetalles(Factura f) {
        if (f == null) return;
        Log.d(TAG,"Mostrando detalles para anular Factura ID: " + f.getIdFactura());
        // Poblar los TextViews (usando strings estandarizados)
        tvFacturaId.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.factura_eliminar_label_id_factura), f.getIdFactura()));
        tvPedidoId.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.factura_eliminar_label_id_pedido), f.getIdPedido()));
        tvFacturaFecha.setText(String.format("%s %s", getString(R.string.factura_eliminar_label_fecha), f.getFechaEmision()));
        tvFacturaMonto.setText(String.format(Locale.US, "%s $%.2f", getString(R.string.factura_eliminar_label_monto), f.getMontoTotal()));
        tvFacturaTipoPago.setText(String.format("%s %s", getString(R.string.factura_eliminar_label_tipo_pago), f.getTipoPago()));
        tvFacturaEstado.setText(String.format("%s %s", getString(R.string.factura_eliminar_label_estado), f.getEstadoFactura()));
        tvFacturaEsCredito.setText(String.format("%s %s", getString(R.string.factura_eliminar_label_es_credito),
                f.getEsCredito() == 1 ? getString(R.string.factura_consultar_s_valor_si) : getString(R.string.factura_consultar_s_valor_no))); // Reutiliza si/no
    }

    private void ocultarDetallesYDeshabilitarBoton(){
        layoutDetalles.setVisibility(View.GONE);
        btnConfirmarAnulacion.setEnabled(false);
        facturaSeleccionada = null;
        Log.d(TAG,"Detalles ocultados y botón desactivado.");
    }

    private void intentarAnularFactura() {
        if (facturaSeleccionada == null) {
            Toast.makeText(this, R.string.factura_eliminar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar si es a crédito
        if (facturaSeleccionada.getEsCredito() == 1) {
            Toast.makeText(this, R.string.factura_eliminar_toast_es_credito, Toast.LENGTH_LONG).show();
            return;
        }
        // Validar si ya está anulada
        if (ESTADO_ANULADA.equalsIgnoreCase(facturaSeleccionada.getEstadoFactura())) {
            Toast.makeText(this, R.string.factura_eliminar_toast_ya_anulada, Toast.LENGTH_LONG).show();
            return;
        }

        Log.i(TAG, "Solicitando anulación para Factura ID: " + facturaSeleccionada.getIdFactura());

        // Crear copia o modificar estado del objeto seleccionado
        Factura facturaParaActualizar = facturaSeleccionada;
        facturaParaActualizar.setEstadoFactura(ESTADO_ANULADA); // Cambiar estado a "Anulada"

        // Llamar al ViewModel para actualizar (que usará dao.actualizar)
        boolean exito = facturaViewModel.actualizarFactura(facturaParaActualizar);

        if (exito) {
            Toast.makeText(this,
                    String.format(getString(R.string.factura_eliminar_toast_exito), facturaSeleccionada.getIdFactura()),
                    Toast.LENGTH_SHORT).show();
            // Refrescar lista del spinner y ocultar detalles
            facturaViewModel.cargarTodasLasFacturas(); // Volver a cargar para actualizar lista del observer
            ocultarDetallesYDeshabilitarBoton();
            // finish(); // Opcional: cerrar actividad
        } else {
            Toast.makeText(this, R.string.factura_eliminar_toast_error, Toast.LENGTH_SHORT).show();
            // Quizás volver a cargar el estado original si falló
            // facturaViewModel.consultarFacturaPorId(facturaSeleccionada.getIdFactura());
        }
    }
}