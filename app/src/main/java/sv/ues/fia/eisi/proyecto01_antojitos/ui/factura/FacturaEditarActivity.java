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
    // Constantes de Estado (Leer desde Strings idealmente)
    private static final String ESTADO_ANULADA = "Anulada"; // Considerar R.string.factura_estado_anulada
    private static final String ESTADO_PAGADA = "Pagada";   // Considerar R.string.factura_estado_pagada
    private static final String ESTADO_EN_CREDITO = "En Crédito"; // Considerar R.string.factura_estado_en_credito
    private static final String ESTADO_PENDIENTE = "Pendiente"; // Considerar R.string.factura_estado_pendiente

    // --- UI Components ---
    private Spinner spinnerSeleccionarFactura;
    private LinearLayout layoutCamposEditables;
    private TextView tvIdFactura, tvIdPedido, tvMontoTotal, tvEstadoFactura, tvEsCredito;
    private EditText editFechaEmision;
    private Spinner spinnerTipoPago;
    private Button btnActualizarFactura;
    private Button btnReactivarFactura;
    private Button btnMarcarComoPagadaFactura; // Nuevo Botón

    // --- Data ---
    private FacturaViewModel facturaViewModel;
    private List<Factura> listaDeTodasLasFacturas = new ArrayList<>();
    private Factura facturaSeleccionadaActual;
    // Lista de tipos de pago que se permiten seleccionar al editar
    private List<String> tiposPagoEditables = Arrays.asList("Contado", "Transferencia", "Cheque", "Otro", "Bitcoin", "Tarjeta", "Efectivo"); //Considerar leer de R.array
    private Calendar calendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura_editar);
        setTitle(getString(R.string.factura_editar_title));

        facturaViewModel = new ViewModelProvider(this).get(FacturaViewModel.class);

        inicializarVistas();
        ocultarYDeshabilitarTodo(); // Estado inicial UI
        cargarSpinnerTipoPagoEditables();
        configurarListeners();

        // Observador para la lista de todas las facturas
        facturaViewModel.getListaFacturas().observe(this, facturas -> {
            Log.d(TAG, "LiveData listaFacturas actualizado. Facturas: " + (facturas != null ? facturas.size() : "null"));
            if (facturas != null) {
                listaDeTodasLasFacturas = facturas;
                cargarSpinnerFacturas();
            }
        });

        // Carga inicial
        Log.d(TAG, "Solicitando carga de todas las facturas...");
        facturaViewModel.cargarTodasLasFacturas();
    }

    private void inicializarVistas() {
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
        btnMarcarComoPagadaFactura = findViewById(R.id.btnMarcarComoPagadaFactura); // Inicializar nuevo botón
        Log.d(TAG,"Vistas inicializadas.");
    }

    private void configurarListeners() {
        // Listener para el spinner de selección de factura
        spinnerSeleccionarFactura.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ocultarYDeshabilitarTodo(); // Resetear UI al cambiar selección
                if (position > 0 && (position - 1) < listaDeTodasLasFacturas.size()) {
                    facturaSeleccionadaActual = listaDeTodasLasFacturas.get(position - 1);
                    Log.d(TAG, "Factura seleccionada ID: " + facturaSeleccionadaActual.getIdFactura() + ", Estado: " + facturaSeleccionadaActual.getEstadoFactura() + ", EsCredito: " + facturaSeleccionadaActual.getEsCredito());
                    poblarCampos(facturaSeleccionadaActual);
                    layoutCamposEditables.setVisibility(View.VISIBLE);
                    configurarVisibilidadBotonesYEdicion(); // Decide qué mostrar/habilitar
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                ocultarYDeshabilitarTodo();
            }
        });

        // Listener para Fecha Emisión
        editFechaEmision.setOnClickListener(v -> {
            if(editFechaEmision.isEnabled()) mostrarDatePickerDialog();
        });

        // Listeners para los botones de acción
        btnActualizarFactura.setOnClickListener(v -> intentarActualizarFactura());
        btnReactivarFactura.setOnClickListener(v -> intentarReactivarFactura());
        btnMarcarComoPagadaFactura.setOnClickListener(v -> intentarMarcarComoPagada()); // Listener nuevo botón

        Log.d(TAG,"Listeners configurados.");
    }

    // Carga el spinner con todas las facturas existentes mostrando estado
    private void cargarSpinnerFacturas() {
        List<String> descripcionesFacturas = new ArrayList<>();
        descripcionesFacturas.add(getString(R.string.placeholder_seleccione)); // Usar string resource

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
        tvEsCredito.setText(factura.getEsCredito() == 1 ? getString(R.string.respuesta_si) : getString(R.string.respuesta_no)); // Usar strings genéricos

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

    // Muestra DatePicker
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
    // Actualiza EditText de Fecha
    private void actualizarEditTextFecha() {
        String formato = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
        editFechaEmision.setText(sdf.format(calendario.getTime()));
    }

    // Habilita/Deshabilita los campos que SÍ son editables
    private void habilitarCamposParaEdicion(boolean habilitar) {
        editFechaEmision.setEnabled(habilitar);
        spinnerTipoPago.setEnabled(habilitar);
        Log.d(TAG, "Campos editables habilitados: " + habilitar);
    }

    // Configura visibilidad de botones y campos según estado de factura
    private void configurarVisibilidadBotonesYEdicion() {
        if (facturaSeleccionadaActual == null) {
            ocultarYDeshabilitarTodo();
            return;
        }

        String estado = facturaSeleccionadaActual.getEstadoFactura();
        boolean esCredito = facturaSeleccionadaActual.getEsCredito() == 1;

        // Resetear visibilidad y estado de todos los botones de acción primero
        btnActualizarFactura.setVisibility(View.GONE);
        btnActualizarFactura.setEnabled(false);
        btnReactivarFactura.setVisibility(View.GONE);
        btnReactivarFactura.setEnabled(false);
        btnMarcarComoPagadaFactura.setVisibility(View.GONE);
        btnMarcarComoPagadaFactura.setEnabled(false);

        if (ESTADO_ANULADA.equalsIgnoreCase(estado)) {
            habilitarCamposParaEdicion(false);
            btnReactivarFactura.setVisibility(View.VISIBLE);
            btnReactivarFactura.setEnabled(true);
        } else if (ESTADO_PENDIENTE.equalsIgnoreCase(estado)) {
            habilitarCamposParaEdicion(true); // Fecha y tipo de pago son editables
            btnActualizarFactura.setVisibility(View.VISIBLE);
            btnActualizarFactura.setEnabled(true);

            if (!esCredito) { // Si está PENDIENTE y NO ES CRÉDITO, se puede pagar
                btnMarcarComoPagadaFactura.setVisibility(View.VISIBLE);
                btnMarcarComoPagadaFactura.setEnabled(true);
            }
        } else if (ESTADO_EN_CREDITO.equalsIgnoreCase(estado)) {
            // Si está "En Crédito", solo se permite editar Fecha/TipoPago.
            // No se puede pagar directamente aquí (se maneja en módulo de créditos).
            habilitarCamposParaEdicion(true);
            btnActualizarFactura.setVisibility(View.VISIBLE);
            btnActualizarFactura.setEnabled(true);
        } else if (ESTADO_PAGADA.equalsIgnoreCase(estado)) {
            // Si está Pagada, no se puede editar, ni reactivar, ni pagar de nuevo.
            habilitarCamposParaEdicion(false);
        } else {
            // Estado desconocido, deshabilitar todo y ocultar campos
            ocultarYDeshabilitarTodo();
            Log.w(TAG, "Estado de factura desconocido: " + estado);
        }
    }


    // Resetea la UI
    private void ocultarYDeshabilitarTodo() {
        if (layoutCamposEditables != null) layoutCamposEditables.setVisibility(View.GONE);
        if (btnActualizarFactura != null) {
            btnActualizarFactura.setVisibility(View.GONE);
            btnActualizarFactura.setEnabled(false);
        }
        if (btnReactivarFactura != null) {
            btnReactivarFactura.setVisibility(View.GONE);
            btnReactivarFactura.setEnabled(false);
        }
        if (btnMarcarComoPagadaFactura != null) { // Incluir nuevo botón
            btnMarcarComoPagadaFactura.setVisibility(View.GONE);
            btnMarcarComoPagadaFactura.setEnabled(false);
        }
        facturaSeleccionadaActual = null;
        Log.d(TAG,"UI reseteada / campos ocultados.");
    }


    // Lógica para el botón "Actualizar Factura"
    private void intentarActualizarFactura() {
        if (facturaSeleccionadaActual == null) {
            Toast.makeText(this, R.string.factura_editar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }
        // Validar que no esté Anulada o Pagada antes de intentar actualizar
        // (Aunque configurarVisibilidadBotonesYEdicion ya debería prevenir esto)
        if (ESTADO_ANULADA.equalsIgnoreCase(facturaSeleccionadaActual.getEstadoFactura()) ||
                ESTADO_PAGADA.equalsIgnoreCase(facturaSeleccionadaActual.getEstadoFactura())) {
            Toast.makeText(this,"La factura está " + facturaSeleccionadaActual.getEstadoFactura() + ", no se puede editar.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Validación de campos editables ---
        String fechaEmision = editFechaEmision.getText().toString().trim();
        if (fechaEmision.isEmpty()) {
            Toast.makeText(this, R.string.factura_editar_toast_fecha_requerida, Toast.LENGTH_SHORT).show();
            return;
        }
        int tipoPagoPos = spinnerTipoPago.getSelectedItemPosition();
        if (tipoPagoPos <= 0) { // El índice 0 es el placeholder "Seleccione"
            Toast.makeText(this, R.string.factura_editar_toast_tipo_pago_requerido, Toast.LENGTH_SHORT).show();
            return;
        }
        String tipoPagoSeleccionado = tiposPagoEditables.get(tipoPagoPos - 1); // -1 por el placeholder

        // --- Crear Objeto Actualizado ---
        // Es importante crear una nueva instancia o clonar para evitar modificar directamente
        // el objeto en la lista que podría estar siendo usado por el adapter del spinner.
        // Sin embargo, si el objeto facturaSeleccionadaActual se obtiene fresco del DAO cada vez
        // o si el ViewModel maneja la inmutabilidad, podría ser menos problemático.
        // Por seguridad y buenas prácticas con LiveData, es mejor enviar un objeto "limpio"
        // o asegurar que el ViewModel maneje la actualización de forma correcta.
        // En este caso, estamos modificando el objeto que ya tenemos y lo pasamos.
        Factura facturaActualizada = facturaSeleccionadaActual; // Se modifica la instancia actual
        facturaActualizada.setFechaEmision(fechaEmision);
        facturaActualizada.setTipoPago(tipoPagoSeleccionado);
        // Los demás campos (ID, PedidoID, Monto, Estado, EsCredito) se mantienen.

        Log.d(TAG, "Objeto Factura a actualizar: " + facturaActualizada.toString());

        // --- Actualizar usando ViewModel ---
        boolean exito = facturaViewModel.actualizarFactura(facturaActualizada);

        // --- Feedback Final ---
        if (exito) {
            Toast.makeText(this,
                    String.format(getString(R.string.factura_editar_toast_exito), facturaActualizada.getIdFactura()),
                    Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK); // Informar éxito a actividad anterior si es necesario
            // Refrescar datos y resetear UI
            facturaViewModel.cargarTodasLasFacturas();
            ocultarYDeshabilitarTodo();
            spinnerSeleccionarFactura.setSelection(0); // Resetear spinner
            // Opcional: finish();
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
        // Cambiar estado a "Pendiente"
        facturaParaReactivar.setEstadoFactura(getString(R.string.factura_editar_estado_destino_reactivada)); //Debería ser ESTADO_PENDIENTE

        boolean exito = facturaViewModel.actualizarFactura(facturaParaReactivar);

        if (exito) {
            Toast.makeText(this,
                    String.format(getString(R.string.factura_editar_toast_reactivada), facturaSeleccionadaActual.getIdFactura()),
                    Toast.LENGTH_SHORT).show();
            // Refrescar lista del spinner y ocultar/resetear detalles
            facturaViewModel.cargarTodasLasFacturas();
            ocultarYDeshabilitarTodo();
            spinnerSeleccionarFactura.setSelection(0);
            // Opcional: finish();
        } else {
            Toast.makeText(this, R.string.factura_editar_toast_error_reactivar, Toast.LENGTH_SHORT).show();
        }
    }

    // NUEVO MÉTODO: Lógica para el botón "Marcar como Pagada"
    private void intentarMarcarComoPagada() {
        if (facturaSeleccionadaActual == null) {
            Toast.makeText(this, R.string.factura_editar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }

        // Doble validación (aunque configurarVisibilidadBotonesYEdicion ya debería manejarlo)
        if (facturaSeleccionadaActual.getEsCredito() == 1 ||
                !ESTADO_PENDIENTE.equalsIgnoreCase(facturaSeleccionadaActual.getEstadoFactura())) {
            Toast.makeText(this, R.string.factura_editar_toast_no_pagable, Toast.LENGTH_LONG).show();
            Log.w(TAG, "Intento de marcar como pagada una factura no elegible. ID: " + facturaSeleccionadaActual.getIdFactura() +
                    ", EsCredito: " + facturaSeleccionadaActual.getEsCredito() +
                    ", Estado: " + facturaSeleccionadaActual.getEstadoFactura());
            return;
        }

        Log.i(TAG, "Intentando marcar como pagada Factura ID: " + facturaSeleccionadaActual.getIdFactura());

        boolean exito = facturaViewModel.marcarComoPagada(facturaSeleccionadaActual.getIdFactura());

        if (exito) {
            Toast.makeText(this,
                    String.format(getString(R.string.factura_editar_toast_pagada_exito), facturaSeleccionadaActual.getIdFactura()),
                    Toast.LENGTH_SHORT).show();

            // La factura se habrá actualizado. Refrescar la lista del spinner y resetear la UI.
            facturaViewModel.cargarTodasLasFacturas(); // Para actualizar el spinner
            ocultarYDeshabilitarTodo(); // Oculta los detalles y deshabilita botones
            spinnerSeleccionarFactura.setSelection(0); // Resetea la selección del spinner

            // Opcionalmente, podrías querer finalizar la actividad si la acción es terminal.
            // setResult(Activity.RESULT_OK);
            // finish();
        } else {
            Toast.makeText(this, R.string.factura_editar_toast_pagada_error, Toast.LENGTH_SHORT).show();
        }
    }
}