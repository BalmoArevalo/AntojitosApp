package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer; // Necesario para observar LiveData
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
// No se necesita CheckBox aquí si ESTADO y ES_CREDITO no son editables
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

import sv.ues.fia.eisi.proyecto01_antojitos.R;
// Importar ViewModel y POJO
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaViewModel;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.Factura;

public class FacturaEditarActivity extends AppCompatActivity {

    private static final String TAG = "FacturaEditarActivity";

    // --- UI Components ---
    private Spinner spinnerSeleccionarFactura;
    private LinearLayout layoutCamposEditables;
    private TextView tvIdFactura, tvIdPedido, tvMontoTotal, tvEstadoFactura, tvEsCredito;
    private EditText editFechaEmision; // Editable
    private Spinner spinnerTipoPago;   // Editable
    private Button btnActualizarFactura;

    // --- Data ---
    private FacturaViewModel facturaViewModel;
    private List<Factura> listaDeTodasLasFacturas = new ArrayList<>(); // Para el spinner
    private Factura facturaSeleccionadaActual; // La factura que se está editando
    // Tipos de pago editables (podrían excluir 'Crédito' si esa lógica es separada)
    private List<String> tiposPagoEditables = Arrays.asList("Contado", "Transferencia", "Cheque", "Otro", "Bitcoin", "Tarjeta", "Efectivo");
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
        btnActualizarFactura = findViewById(R.id.btnActualizarFacturaEditar); // Asegurar ID correcto

        // Configurar estado inicial
        layoutCamposEditables.setVisibility(View.GONE);
        btnActualizarFactura.setEnabled(false);

        cargarSpinnerTipoPagoEditables(); // Cargar tipos de pago editables

        // Configurar DatePicker para Fecha de Emisión
        editFechaEmision.setOnClickListener(v -> {
            if(editFechaEmision.isEnabled()) mostrarDatePickerDialog();
        });

        // Listener para el botón Actualizar
        btnActualizarFactura.setOnClickListener(v -> intentarActualizarFactura());

        // Observador para la lista de todas las facturas (para el spinner de selección)
        facturaViewModel.getListaFacturas().observe(this, facturas -> {
            Log.d(TAG, "LiveData listaFacturas actualizado. Facturas: " + (facturas != null ? facturas.size() : "null"));
            if (facturas != null) {
                listaDeTodasLasFacturas = facturas;
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
                    // Selección válida, no es el placeholder
                    facturaSeleccionadaActual = listaDeTodasLasFacturas.get(position - 1);
                    Log.d(TAG, "Factura seleccionada ID: " + facturaSeleccionadaActual.getIdFactura());
                    poblarCampos(facturaSeleccionadaActual);
                    layoutCamposEditables.setVisibility(View.VISIBLE);
                    btnActualizarFactura.setEnabled(true);
                } else {
                    // Placeholder seleccionado
                    facturaSeleccionadaActual = null;
                    layoutCamposEditables.setVisibility(View.GONE);
                    btnActualizarFactura.setEnabled(false);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                facturaSeleccionadaActual = null;
                layoutCamposEditables.setVisibility(View.GONE);
                btnActualizarFactura.setEnabled(false);
            }
        });
    }

    // Carga el spinner con todas las facturas existentes
    private void cargarSpinnerFacturas() {
        List<String> descripcionesFacturas = new ArrayList<>();
        descripcionesFacturas.add(getString(R.string.placeholder_seleccione)); // Placeholder

        for (Factura f : listaDeTodasLasFacturas) {
            // Descripción: "Factura #ID (Pedido #ID) - Estado"
            descripcionesFacturas.add(String.format(Locale.getDefault(), "Factura #%d (Pedido #%d) - %s",
                    f.getIdFactura(), f.getIdPedido(), f.getEstadoFactura()));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, descripcionesFacturas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeleccionarFactura.setAdapter(adapter);

        if (descripcionesFacturas.size() <= 1) {
            Toast.makeText(this, "No hay facturas existentes para editar.", Toast.LENGTH_LONG).show();
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

        // Campos editables
        editFechaEmision.setText(factura.getFechaEmision());
        // Establecer fecha en el calendario para el DatePicker
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date fecha = sdf.parse(factura.getFechaEmision());
            if (fecha != null) calendario.setTime(fecha);
        } catch (ParseException e) {
            Log.e(TAG, "Error parseando fecha al poblar campos: " + factura.getFechaEmision(), e);
        }

        // Seleccionar tipo de pago en el spinner
        int spinnerPosition = 0;
        String tipoPagoFactura = factura.getTipoPago();
        if (tipoPagoFactura != null) {
            // Buscar en la lista de tipos de pago EDITABLES
            for (int i = 0; i < tiposPagoEditables.size(); i++) {
                if (tiposPagoEditables.get(i).equalsIgnoreCase(tipoPagoFactura)) {
                    spinnerPosition = i + 1; // +1 por el placeholder "Seleccione..."
                    break;
                }
            }
        }
        spinnerTipoPago.setSelection(spinnerPosition);

        // Habilitar/Deshabilitar edición basada en lógica (ej. no editar si está pagada?)
        // Por ahora, habilitamos los campos editables designados
        editFechaEmision.setEnabled(true);
        spinnerTipoPago.setEnabled(true);
        // Considera deshabilitar tipo de pago si la factura está pagada,
        // dependiendo de si tienes el trigger que lo impide en la BD.
        // spinnerTipoPago.setEnabled(!factura.getEstadoFactura().equalsIgnoreCase("Pagada"));
    }


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


    private void intentarActualizarFactura() {
        if (facturaSeleccionadaActual == null) {
            Toast.makeText(this, R.string.factura_editar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Validación ---
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
        String tipoPagoSeleccionado = tiposPagoEditables.get(tipoPagoPos - 1); // -1 por placeholder

        // --- Crear Objeto Actualizado ---
        // Tomar los datos NO editables del objeto original cargado
        Factura facturaActualizada = new Factura();
        facturaActualizada.setIdFactura(facturaSeleccionadaActual.getIdFactura());
        facturaActualizada.setIdPedido(facturaSeleccionadaActual.getIdPedido());
        facturaActualizada.setMontoTotal(facturaSeleccionadaActual.getMontoTotal()); // No editable aquí
        facturaActualizada.setEstadoFactura(facturaSeleccionadaActual.getEstadoFactura()); // No editable aquí
        facturaActualizada.setEsCredito(facturaSeleccionadaActual.getEsCredito());       // No editable aquí

        // Tomar los datos editables de la UI
        facturaActualizada.setFechaEmision(fechaEmision);
        facturaActualizada.setTipoPago(tipoPagoSeleccionado);

        Log.d(TAG, "Objeto Factura a actualizar: " + facturaActualizada.toString());

        // --- Actualizar usando ViewModel ---
        boolean exito = facturaViewModel.actualizarFactura(facturaActualizada);

        // --- Feedback Final ---
        if (exito) {
            Toast.makeText(this,
                    String.format(getString(R.string.factura_editar_toast_exito), facturaActualizada.getIdFactura()),
                    Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, R.string.factura_editar_toast_error, Toast.LENGTH_LONG).show();
        }
    }
}