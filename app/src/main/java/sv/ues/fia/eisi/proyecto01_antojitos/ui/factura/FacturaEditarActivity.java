package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors; // Solo si usas API 24+ para filtrar LiveData

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaViewModel;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.Factura;

public class FacturaEditarActivity extends AppCompatActivity {

    private static final String TAG = "FacturaEditarActivity";
    private static final String ESTADO_ANULADA = "Anulada";

    // --- UI Components ---
    private Spinner spinnerSeleccionarFactura;
    private LinearLayout layoutCamposEditables;
    private TextView tvIdFactura, tvIdPedido, tvMontoTotal, tvEstadoFactura, tvEsCredito;
    private EditText editFechaEmision;
    private Spinner spinnerTipoPago;
    private Button btnActualizarFactura;
    private Button btnReactivarFactura;

    // --- Data ---
    private FacturaViewModel facturaViewModel;
    private List<Factura> listaDeTodasLasFacturas = new ArrayList<>();
    private Factura facturaSeleccionadaActual;
    private List<String> tiposPagoEditables = Arrays.asList("Contado", "Transferencia", "Cheque", "Otro", "Bitcoin", "Tarjeta", "Efectivo"); // Tipos de pago permitidos
    private Calendar calendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura_editar);
        setTitle(getString(R.string.factura_editar_title));

        facturaViewModel = new ViewModelProvider(this).get(FacturaViewModel.class);

        // Inicializar Vistas
        spinnerSeleccionarFactura = findViewById(R.id.spinnerSeleccionarFacturaEditar);
        layoutCamposEditables = findViewById(R.id.layoutFacturaEditarCampos);
        tvIdFactura = findViewById(R.id.tvFacturaEditarIdFactura);
        tvIdPedido = findViewById(R.id.tvFacturaEditarIdPedido);
        editFechaEmision = findViewById(R.id.editFacturaEditarFecha);
        tvMontoTotal = findViewById(R.id.tvFacturaEditarMontoTotal);
        spinnerTipoPago = findViewById(R.id.spinnerFacturaEditarTipoPago);
        tvEstadoFactura = findViewById(R.id.tvFacturaEditarEstado);
        tvEsCredito = findViewById(R.id.tvFacturaEditarEsCredito);
        btnActualizarFactura = findViewById(R.id.btnActualizarFacturaEditar);
        btnReactivarFactura = findViewById(R.id.btnReactivarFactura);

        // Configurar estado inicial
        ocultarYDeshabilitarTodo();
        cargarSpinnerTipoPagoEditables();

        // Configurar DatePicker para Fecha de Emisión
        editFechaEmision.setOnClickListener(v -> {
            if(editFechaEmision.isEnabled()) mostrarDatePickerDialog();
        });

        // Listeners para los botones
        btnActualizarFactura.setOnClickListener(v -> intentarActualizarFactura());
        btnReactivarFactura.setOnClickListener(v -> intentarReactivarFactura());

        // Observador para la lista de todas las facturas (para el spinner de selección)
        facturaViewModel.getListaFacturas().observe(this, facturas -> {
            Log.d(TAG, "LiveData listaFacturas actualizado. Facturas: " + (facturas != null ? facturas.size() : "null"));
            if (facturas != null) {
                listaDeTodasLasFacturas = facturas; // Guardar lista completa
                cargarSpinnerFacturas(); // Poblar spinner cuando lleguen los datos
            }
        });

        // Solicitar la carga inicial de todas las facturas
        Log.d(TAG, "Solicitando carga de todas las facturas...");
        facturaViewModel.cargarTodasLasFacturas();

        // Listener para el spinner de selección de factura
        spinnerSeleccionarFactura.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && (position - 1) < listaDeTodasLasFacturas.size()) {
                    // Selección válida
                    facturaSeleccionadaActual = listaDeTodasLasFacturas.get(position - 1);
                    Log.d(TAG, "Factura seleccionada ID: " + facturaSeleccionadaActual.getIdFactura() + ", Estado: " + facturaSeleccionadaActual.getEstadoFactura());
                    poblarCampos(facturaSeleccionadaActual); // Mostrar datos
                    layoutCamposEditables.setVisibility(View.VISIBLE); // Mostrar sección de campos

                    // Lógica Condicional para Botones y Edición
                    if (ESTADO_ANULADA.equalsIgnoreCase(facturaSeleccionadaActual.getEstadoFactura())) {
                        habilitarCamposParaEdicion(false); // Deshabilitar campos
                        btnActualizarFactura.setVisibility(View.GONE); // Ocultar Actualizar
                        btnReactivarFactura.setVisibility(View.VISIBLE); // Mostrar Reactivar
                        btnReactivarFactura.setEnabled(true); // Habilitar Reactivar
                    } else {
                        // No está Anulada: Habilitar edición, mostrar Actualizar, ocultar Reactivar
                        habilitarCamposParaEdicion(true); // Habilitar Fecha y Tipo Pago
                        btnActualizarFactura.setVisibility(View.VISIBLE); // Mostrar Actualizar
                        btnActualizarFactura.setEnabled(true); // Habilitar Actualizar
                        btnReactivarFactura.setVisibility(View.GONE); // Ocultar Reactivar
                        // Considera añadir lógica extra aquí si no se puede editar si está "Pagada" o "En Crédito"
                        // if ("Pagada".equalsIgnoreCase(facturaSeleccionadaActual.getEstadoFactura()) || ...) {
                        //     habilitarCamposParaEdicion(false);
                        //     btnActualizarFactura.setEnabled(false);
                        // }
                    }
                } else {
                    // Placeholder seleccionado
                    ocultarYDeshabilitarTodo();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                ocultarYDeshabilitarTodo();
            }
        });
    }

    // Carga el spinner con todas las facturas existentes mostrando estado
    private void cargarSpinnerFacturas() {
        List<String> descripcionesFacturas = new ArrayList<>();
        descripcionesFacturas.add(getString(R.string.placeholder_seleccione));

        for (Factura f : listaDeTodasLasFacturas) {
            descripcionesFacturas.add(String.format(Locale.getDefault(), "Factura #%d (Pedido #%d) - %s",
                    f.getIdFactura(), f.getIdPedido(), f.getEstadoFactura()));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, descripcionesFacturas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeleccionarFactura.setAdapter(adapter);

        if (descripcionesFacturas.size() <= 1) {
            Toast.makeText(this, "No hay facturas existentes.", Toast.LENGTH_LONG).show();
            spinnerSeleccionarFactura.setEnabled(false);
        } else {
            spinnerSeleccionarFactura.setEnabled(true);
        }
    }

    // Carga el spinner de Tipos de Pago editables
    private void cargarSpinnerTipoPagoEditables() {
        List<String> opcionesConPlaceholder = new ArrayList<>();
        opcionesConPlaceholder.add(getString(R.string.placeholder_seleccione));
        opcionesConPlaceholder.addAll(tiposPagoEditables);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opcionesConPlaceholder);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoPago.setAdapter(adapter);
    }

    // Rellena los campos de la UI con los datos de la factura seleccionada
    private void poblarCampos(Factura factura) {
        if (factura == null) return;
        Log.d(TAG, "Poblando campos para Factura ID: " + factura.getIdFactura());

        // Campos de solo lectura
        tvIdFactura.setText(String.valueOf(factura.getIdFactura()));
        tvIdPedido.setText(String.valueOf(factura.getIdPedido()));
        tvMontoTotal.setText(String.format(Locale.US, "$%.2f", factura.getMontoTotal()));
        tvEstadoFactura.setText(factura.getEstadoFactura());
        tvEsCredito.setText(factura.getEsCredito() == 1 ? getString(R.string.factura_consultar_s_valor_si) : getString(R.string.factura_consultar_s_valor_no));

        // Campos editables (establecer valor inicial)
        editFechaEmision.setText(factura.getFechaEmision());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date fecha = sdf.parse(factura.getFechaEmision());
            if (fecha != null) calendario.setTime(fecha);
        } catch (ParseException e) {
            Log.e(TAG, "Error parseando fecha al poblar campos: " + factura.getFechaEmision(), e);
        }

        // Seleccionar tipo de pago en el spinner editable
        int spinnerPosition = 0;
        String tipoPagoFactura = factura.getTipoPago();
        if (tipoPagoFactura != null) {
            for (int i = 0; i < tiposPagoEditables.size(); i++) {
                if (tiposPagoEditables.get(i).equalsIgnoreCase(tipoPagoFactura)) {
                    spinnerPosition = i + 1; // +1 por el placeholder
                    break;
                }
            }
        }
        spinnerTipoPago.setSelection(spinnerPosition);
    }

    // DatePicker (sin cambios)
    private void mostrarDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendario.set(Calendar.YEAR, year);
            calendario.set(Calendar.MONTH, month);
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarEditTextFecha();
        };

        new DatePickerDialog(this, dateSetListener,
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void actualizarEditTextFecha() {
        String formato = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
        editFechaEmision.setText(sdf.format(calendario.getTime()));
    }

    // Habilita/Deshabilita los campos que SÍ son editables
    private void habilitarCamposParaEdicion(boolean habilitar) {
        editFechaEmision.setEnabled(habilitar);
        spinnerTipoPago.setEnabled(habilitar);
    }

    // Resetea la UI a su estado inicial
    private void ocultarYDeshabilitarTodo() {
        layoutCamposEditables.setVisibility(View.GONE);
        btnActualizarFactura.setVisibility(View.GONE);
        btnReactivarFactura.setVisibility(View.GONE);
        btnActualizarFactura.setEnabled(false);
        btnReactivarFactura.setEnabled(false);
        facturaSeleccionadaActual = null;
        Log.d(TAG,"UI reseteada / campos ocultados.");
    }


    // Lógica para el botón "Actualizar Factura"
    private void intentarActualizarFactura() {
        if (facturaSeleccionadaActual == null) {
            Toast.makeText(this, R.string.factura_editar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }
        // No permitir actualizar si está anulada
        if (ESTADO_ANULADA.equalsIgnoreCase(facturaSeleccionadaActual.getEstadoFactura())) {
            Toast.makeText(this,"La factura está anulada, no se puede editar.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Validación de campos editables ---
        String fechaEmision = editFechaEmision.getText().toString().trim();
        if (fechaEmision.isEmpty()) {
            Toast.makeText(this, R.string.factura_editar_toast_fecha_requerida, Toast.LENGTH_SHORT).show();
            return;
        }

        int tipoPagoPos = spinnerTipoPago.getSelectedItemPosition();
        if (tipoPagoPos <= 0) {
            Toast.makeText(this, R.string.factura_editar_toast_tipo_pago_requerido, Toast.LENGTH_SHORT).show();
            return;
        }
        String tipoPagoSeleccionado = tiposPagoEditables.get(tipoPagoPos - 1);

        // --- Crear Objeto Actualizado ---
        Factura facturaActualizada = facturaSeleccionadaActual; // Usar el objeto cargado como base
        facturaActualizada.setFechaEmision(fechaEmision);       // Establecer nuevo valor
        facturaActualizada.setTipoPago(tipoPagoSeleccionado);  // Establecer nuevo valor
        // Los demás campos (ID_Factura, ID_Pedido, Monto, Estado, EsCredito) NO se modifican aquí

        Log.d(TAG, "Objeto Factura a actualizar: " + facturaActualizada.toString());

        // --- Actualizar usando ViewModel ---
        boolean exito = facturaViewModel.actualizarFactura(facturaActualizada);

        // --- Feedback Final ---
        if (exito) {
            Toast.makeText(this,
                    String.format(getString(R.string.factura_editar_toast_exito), facturaActualizada.getIdFactura()),
                    Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish(); // Cerrar al éxito
        } else {
            Toast.makeText(this, R.string.factura_editar_toast_error, Toast.LENGTH_LONG).show();
        }
    }

    // Lógica para el botón "Reactivar Factura"
    private void intentarReactivarFactura() {
        if (facturaSeleccionadaActual == null) {
            Toast.makeText(this, R.string.factura_editar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }
        // Solo reactivar si está anulada
        if (!ESTADO_ANULADA.equalsIgnoreCase(facturaSeleccionadaActual.getEstadoFactura())) {
            Toast.makeText(this, "Esta factura no está anulada.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "Solicitando reactivación para Factura ID: " + facturaSeleccionadaActual.getIdFactura());

        Factura facturaParaReactivar = facturaSeleccionadaActual;
        // Cambiar estado a "Pendiente" (o el estado por defecto definido)
        facturaParaReactivar.setEstadoFactura(getString(R.string.factura_editar_estado_destino_reactivada));

        boolean exito = facturaViewModel.actualizarFactura(facturaParaReactivar);

        if (exito) {
            Toast.makeText(this,
                    String.format(getString(R.string.factura_editar_toast_reactivada), facturaSeleccionadaActual.getIdFactura()),
                    Toast.LENGTH_SHORT).show();
            // Refrescar lista del spinner y ocultar/resetear detalles
            facturaViewModel.cargarTodasLasFacturas(); // Para que el observer actualice el spinner
            ocultarYDeshabilitarTodo();
            spinnerSeleccionarFactura.setSelection(0); // Volver al placeholder
            // finish(); // Opcional: cerrar actividad
        } else {
            Toast.makeText(this, R.string.factura_editar_toast_error_reactivar, Toast.LENGTH_SHORT).show();
        }
    }
}