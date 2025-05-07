package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.Factura;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaViewModel;

public class FacturaCrearActivity extends AppCompatActivity {

    private static final String TAG = "FacturaCrearActivity";
    private static final double IVA_EL_SALVADOR = 0.13;

    // --- Componentes UI ---
    private Spinner spinnerPedido;
    private TextView tvMontoTotalCalculado;
    private EditText editFechaEmision;
    private Spinner spinnerTipoPago;
    private TextView tvEstadoInicial;
    private Button btnGuardarFactura;

    // --- Datos ---
    private DBHelper dbHelper;
    private FacturaViewModel facturaViewModel;
    private List<Integer> pedidoIdsSinFactura = new ArrayList<>();
    private List<String> tiposPago = Arrays.asList("Contado", "Transferencia", "Cheque", "Otro");
    private Calendar calendario = Calendar.getInstance();
    private double montoTotalConIvaCalculado = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura_crear);
        setTitle(getString(R.string.factura_crear_title));

        facturaViewModel = new ViewModelProvider(this).get(FacturaViewModel.class);
        dbHelper = new DBHelper(this);

        // Inicializar Vistas
        spinnerPedido = findViewById(R.id.spinnerPedidoFactura);
        tvMontoTotalCalculado = findViewById(R.id.tvCalculadoMontoTotal);
        editFechaEmision = findViewById(R.id.editFechaEmisionFactura);
        spinnerTipoPago = findViewById(R.id.spinnerTipoPagoFactura);
        tvEstadoInicial = findViewById(R.id.tvEstadoFacturaInicial);
        btnGuardarFactura = findViewById(R.id.btnGuardarFactura);

        // Configurar estado inicial y campos no editables
        tvEstadoInicial.setText(getString(R.string.factura_crear_estado_default));
        tvMontoTotalCalculado.setText(getString(R.string.factura_crear_monto_placeholder));

        // *** NUEVO: Deshabilitar campos inicialmente ***
        setCamposEditablesEnabled(false);

        cargarPedidosSinFactura();
        cargarSpinnerTipoPago();

        configurarListeners();
    }

    private void configurarListeners() {
        // Listener para Spinner de Pedido -> Calcula Monto Total y Habilita/Deshabilita campos
        spinnerPedido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // No es el placeholder
                    int selectedPedidoId = pedidoIdsSinFactura.get(position);
                    // *** NUEVO: Habilitar campos al seleccionar pedido (antes de calcular monto) ***
                    setCamposEditablesEnabled(true);
                    // Calcular monto (esto habilitará el botón Guardar si es exitoso)
                    calcularYMostrarMontoTotal(selectedPedidoId);
                } else {
                    // *** NUEVO: Deshabilitar campos si se selecciona placeholder ***
                    setCamposEditablesEnabled(false);
                    tvMontoTotalCalculado.setText(getString(R.string.factura_crear_monto_placeholder));
                    montoTotalConIvaCalculado = 0.0;
                    // btnGuardarFactura ya se deshabilita en setCamposEditablesEnabled(false)
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                // *** NUEVO: Deshabilitar campos si no hay selección ***
                setCamposEditablesEnabled(false);
                tvMontoTotalCalculado.setText(getString(R.string.factura_crear_monto_placeholder));
                montoTotalConIvaCalculado = 0.0;
            }
        });

        // Listener para Fecha (se habilita/deshabilita con los otros campos)
        editFechaEmision.setOnClickListener(v -> {
            // Solo mostrar si el campo está habilitado
            if (editFechaEmision.isEnabled()) {
                mostrarDatePickerDialog();
            }
        });

        // Listener para Botón Guardar (se habilita/deshabilita con los otros campos)
        btnGuardarFactura.setOnClickListener(v -> guardarFactura());
    }

    // *** NUEVO: Método helper para habilitar/deshabilitar campos ***
    private void setCamposEditablesEnabled(boolean enabled) {
        editFechaEmision.setEnabled(enabled);
        spinnerTipoPago.setEnabled(enabled);
        btnGuardarFactura.setEnabled(enabled); // Habilitar/Deshabilitar junto con los campos

        // Resetear valores si se deshabilita
        if (!enabled) {
            editFechaEmision.setText(""); // Limpiar fecha
            spinnerTipoPago.setSelection(0); // Seleccionar placeholder
            // El monto se resetea en el listener del spinner de pedido
        }
    }


    private void cargarPedidosSinFactura() {
        // ... (código sin cambios) ...
        pedidoIdsSinFactura.clear();
        List<String> pedidoDescripciones = new ArrayList<>();
        pedidoDescripciones.add(getString(R.string.placeholder_seleccione));
        pedidoIdsSinFactura.add(-1);

        String query = "SELECT P.ID_PEDIDO FROM PEDIDO P " +
                "WHERE NOT EXISTS (SELECT 1 FROM FACTURA F WHERE F.ID_PEDIDO = P.ID_PEDIDO) " +
                "ORDER BY P.ID_PEDIDO ASC";
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, null)) {

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                pedidoIdsSinFactura.add(id);
                pedidoDescripciones.add("Pedido #" + id);
            }
            Log.d(TAG, "Pedidos sin factura cargados: " + (pedidoDescripciones.size() -1) );
            if (pedidoDescripciones.size() <= 1) {
                Toast.makeText(this, R.string.factura_crear_toast_no_pedidos_disponibles, Toast.LENGTH_LONG).show();
                spinnerPedido.setEnabled(false);
                setCamposEditablesEnabled(false); // Asegurar que todo esté deshabilitado
            } else {
                spinnerPedido.setEnabled(true);
            }

        } catch (SQLiteException e) {
            Log.e(TAG, "Error al cargar pedidos sin factura", e);
            Toast.makeText(this, String.format(getString(R.string.direccion_crear_toast_error_carga),"Pedidos disponibles"), Toast.LENGTH_SHORT).show();
            spinnerPedido.setEnabled(false);
            setCamposEditablesEnabled(false); // Asegurar que todo esté deshabilitado
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, pedidoDescripciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPedido.setAdapter(adapter);
    }

    private void cargarSpinnerTipoPago() {
        // ... (código sin cambios) ...
        List<String> opcionesConPlaceholder = new ArrayList<>();
        opcionesConPlaceholder.add(getString(R.string.placeholder_seleccione));
        opcionesConPlaceholder.addAll(tiposPago);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opcionesConPlaceholder);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoPago.setAdapter(adapter);
    }

    private void mostrarDatePickerDialog() {
        // ... (código sin cambios) ...
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
        // ... (código sin cambios) ...
        String formato = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
        editFechaEmision.setText(sdf.format(calendario.getTime()));
    }

    private void calcularYMostrarMontoTotal(int pedidoId) {
        // ... (inicio sin cambios) ...
        Log.d(TAG, "Calculando monto para Pedido ID: " + pedidoId);
        SQLiteDatabase db = null;
        double sumaSubtotales = 0.0;
        boolean errorCalculo = false;
        boolean habilitarGuardado = false; // Flag para habilitar botón guardar

        try {
            db = dbHelper.getReadableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            sumaSubtotales = dao.getSumSubtotalForPedido(pedidoId);
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener suma de subtotales", e);
            Toast.makeText(this, R.string.factura_crear_toast_error_calculo_monto, Toast.LENGTH_SHORT).show();
            errorCalculo = true;
        } finally {
            // No cerrar DB aquí
        }

        if (!errorCalculo) {
            if (sumaSubtotales > 0) {
                montoTotalConIvaCalculado = sumaSubtotales * (1 + IVA_EL_SALVADOR);
                tvMontoTotalCalculado.setText(String.format(Locale.US, "$%.2f", montoTotalConIvaCalculado));
                habilitarGuardado = true; // Habilitar si el monto es válido
                Log.d(TAG, "Monto calculado con IVA: " + montoTotalConIvaCalculado);
            } else {
                montoTotalConIvaCalculado = 0.0;
                tvMontoTotalCalculado.setText(getString(R.string.factura_crear_monto_placeholder));
                habilitarGuardado = false; // Deshabilitar si no hay detalles o monto es 0
                Toast.makeText(this, R.string.factura_crear_toast_pedido_sin_detalle, Toast.LENGTH_LONG).show();
                Log.w(TAG,"Suma de subtotales es 0 o negativa para Pedido ID: " + pedidoId);
            }
        } else {
            montoTotalConIvaCalculado = 0.0;
            tvMontoTotalCalculado.setText(getString(R.string.factura_crear_monto_placeholder));
            habilitarGuardado = false; // Deshabilitar si hubo error
        }

        // *** CAMBIO: Actualizar estado del botón Guardar basado en el cálculo ***
        btnGuardarFactura.setEnabled(habilitarGuardado);
    }

    private void guardarFactura() {
        // ... (código interno sin cambios, ya que asume que los campos están habilitados si se llega aquí) ...
        Log.d(TAG, "Intentando guardar factura...");

        // --- Validación ---
        int pedidoPos = spinnerPedido.getSelectedItemPosition();
        if (pedidoPos <= 0) {
            Toast.makeText(this, R.string.factura_crear_toast_seleccione_pedido, Toast.LENGTH_SHORT).show();
            return;
        }
        int idPedidoSeleccionado = pedidoIdsSinFactura.get(pedidoPos);

        String fechaEmision = editFechaEmision.getText().toString().trim();
        if (fechaEmision.isEmpty()) {
            Toast.makeText(this, R.string.factura_crear_toast_ingrese_fecha, Toast.LENGTH_SHORT).show();
            return;
        }

        int tipoPagoPos = spinnerTipoPago.getSelectedItemPosition();
        if (tipoPagoPos <= 0) {
            Toast.makeText(this, R.string.factura_crear_toast_seleccione_tipo_pago, Toast.LENGTH_SHORT).show();
            return;
        }
        String tipoPagoSeleccionado = tiposPago.get(tipoPagoPos - 1);

        if (montoTotalConIvaCalculado <= 0) {
            Toast.makeText(this, "No se puede crear factura con monto total inválido.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Intento de guardar factura con monto <= 0 para Pedido ID: " + idPedidoSeleccionado);
            return;
        }

        // --- Crear Objeto Factura ---
        Factura nuevaFactura = new Factura();
        nuevaFactura.setIdPedido(idPedidoSeleccionado);
        nuevaFactura.setFechaEmision(fechaEmision);
        nuevaFactura.setMontoTotal(montoTotalConIvaCalculado);
        nuevaFactura.setTipoPago(tipoPagoSeleccionado);
        nuevaFactura.setEstadoFactura(getString(R.string.factura_crear_estado_default));
        nuevaFactura.setEsCredito(0);

        Log.d(TAG, "Objeto Factura a insertar: " + nuevaFactura.toString());

        // --- Insertar usando ViewModel ---
        long nuevoIdFactura = facturaViewModel.insertarFactura(nuevaFactura);

        // --- Feedback Final ---
        if (nuevoIdFactura != -1) {
            Toast.makeText(this,
                    String.format(getString(R.string.factura_crear_toast_exito), nuevoIdFactura, idPedidoSeleccionado),
                    Toast.LENGTH_LONG).show();
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, R.string.factura_crear_toast_error_guardar, Toast.LENGTH_LONG).show();
        }
    }
}