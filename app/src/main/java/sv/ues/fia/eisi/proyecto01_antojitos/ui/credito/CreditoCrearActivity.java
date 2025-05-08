package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito; // Ajusta el paquete

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase; // Necesario para transacción
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper; // Importar si necesitas DB directo
// Importar ViewModels, DAOs y POJOs
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.Factura;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaViewModel;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.Credito;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.CreditoViewModel;
// Quitar DAOs si usas solo ViewModels
// import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaDAO;
// import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.CreditoDAO;


public class CreditoCrearActivity extends AppCompatActivity {

    private static final String TAG = "CreditoCrearActivity";
    private static final String ESTADO_FACTURA_ELEGIBLE = "Pendiente"; // Estado requerido para activar crédito
    private static final String ESTADO_FACTURA_NUEVO = "En Crédito";
    private static final String ESTADO_CREDITO_NUEVO = "Activo";
    private static final String TIPO_PAGO_CREDITO = "Crédito";


    // UI
    private Spinner spinnerFacturaElegible;
    private LinearLayout layoutCamposCredito;
    private TextView tvFacturaId, tvPedidoId, tvMontoAutorizar;
    private EditText editFechaLimite;
    private Button btnConfirmar;

    // Data
    private FacturaViewModel facturaViewModel;
    private CreditoViewModel creditoViewModel;
    private List<Factura> listaFacturasElegibles = new ArrayList<>();
    private Factura facturaSeleccionada;
    private Calendar calendarioLimite = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credito_crear);
        setTitle(getString(R.string.credito_crear_title));

        // ViewModels
        facturaViewModel = new ViewModelProvider(this).get(FacturaViewModel.class);
        creditoViewModel = new ViewModelProvider(this).get(CreditoViewModel.class);

        // Vistas UI
        spinnerFacturaElegible = findViewById(R.id.spinnerSeleccionarFacturaParaCredito);
        layoutCamposCredito = findViewById(R.id.layoutCreditoCrearCampos);
        tvFacturaId = findViewById(R.id.tvCreditoCrearIdFactura);
        tvPedidoId = findViewById(R.id.tvCreditoCrearIdPedido);
        tvMontoAutorizar = findViewById(R.id.tvCreditoCrearMonto);
        editFechaLimite = findViewById(R.id.editCreditoFechaLimite);
        btnConfirmar = findViewById(R.id.btnConfirmarCrearCredito);

        // Estado inicial
        layoutCamposCredito.setVisibility(View.GONE);
        btnConfirmar.setEnabled(false);

        configurarListeners();
        observarFacturas(); // Configurar observador

        // Cargar facturas elegibles
        facturaViewModel.cargarTodasLasFacturas();
    }

    private void configurarListeners() {
        // Listener para seleccionar factura
        spinnerFacturaElegible.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && (position - 1) < listaFacturasElegibles.size()) {
                    facturaSeleccionada = listaFacturasElegibles.get(position - 1);
                    mostrarDetallesFacturaSeleccionada(facturaSeleccionada);
                    layoutCamposCredito.setVisibility(View.VISIBLE);
                    btnConfirmar.setEnabled(true);
                } else {
                    facturaSeleccionada = null;
                    layoutCamposCredito.setVisibility(View.GONE);
                    btnConfirmar.setEnabled(false);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                facturaSeleccionada = null;
                layoutCamposCredito.setVisibility(View.GONE);
                btnConfirmar.setEnabled(false);
            }
        });

        // Listener para fecha límite
        editFechaLimite.setOnClickListener(v -> mostrarDatePickerDialog());

        // Listener para botón confirmar
        btnConfirmar.setOnClickListener(v -> intentarCrearCredito());
    }

    // Observa la lista de facturas y filtra las elegibles para el spinner
    private void observarFacturas() {
        facturaViewModel.getListaFacturas().observe(this, facturas -> {
            Log.d(TAG, "Observer: Lista de facturas actualizada. Total: " + (facturas != null ? facturas.size() : "null"));
            if (facturas != null) {
                // Filtrar facturas: ES_CREDITO = 0 Y ESTADO_FACTURA = "Pendiente" (o el estado que definas como elegible)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    listaFacturasElegibles = facturas.stream()
                            .filter(f -> f.getEsCredito() == 0 && ESTADO_FACTURA_ELEGIBLE.equalsIgnoreCase(f.getEstadoFactura()))
                            .collect(Collectors.toList());
                } else {
                    listaFacturasElegibles = new ArrayList<>();
                    for(Factura f: facturas){
                        if(f.getEsCredito() == 0 && ESTADO_FACTURA_ELEGIBLE.equalsIgnoreCase(f.getEstadoFactura())){
                            listaFacturasElegibles.add(f);
                        }
                    }
                }
                cargarSpinnerFacturasElegibles(); // Poblar spinner con las filtradas
            }
        });
    }


    // Carga el spinner con facturas elegibles
    private void cargarSpinnerFacturasElegibles() {
        List<String> descripciones = new ArrayList<>();
        descripciones.add(getString(R.string.placeholder_seleccione));

        for (Factura f : listaFacturasElegibles) {
            descripciones.add(String.format(Locale.getDefault(), "Factura #%d (Pedido #%d) - Monto: $%.2f",
                    f.getIdFactura(), f.getIdPedido(), f.getMontoTotal()));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, descripciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFacturaElegible.setAdapter(adapter);

        if (descripciones.size() <= 1) {
            Toast.makeText(this, R.string.credito_crear_no_facturas_disponibles, Toast.LENGTH_LONG).show();
            spinnerFacturaElegible.setEnabled(false);
        } else {
            spinnerFacturaElegible.setEnabled(true);
        }
    }

    // Muestra detalles de la factura seleccionada
    private void mostrarDetallesFacturaSeleccionada(Factura f) {
        tvFacturaId.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.credito_crear_label_id_factura), f.getIdFactura()));
        tvPedidoId.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.credito_crear_label_id_pedido), f.getIdPedido()));
        tvMontoAutorizar.setText(String.format(Locale.US, "%s $%.2f", getString(R.string.credito_crear_label_monto_autorizar), f.getMontoTotal()));
    }

    private void mostrarDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendarioLimite.set(Calendar.YEAR, year);
            calendarioLimite.set(Calendar.MONTH, month);
            calendarioLimite.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarEditTextFecha(editFechaLimite, calendarioLimite);
        };
        // Usar fecha actual como mínima? O la fecha de emisión de la factura? Por ahora, default.
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener,
                calendarioLimite.get(Calendar.YEAR),
                calendarioLimite.get(Calendar.MONTH),
                calendarioLimite.get(Calendar.DAY_OF_MONTH));
        // Opcional: Poner fecha mínima (ej. fecha de emisión de la factura)
        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void actualizarEditTextFecha(EditText editText, Calendar calendario) {
        String formato = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
        editText.setText(sdf.format(calendario.getTime()));
    }


    // Lógica para crear el crédito y actualizar la factura
    private void intentarCrearCredito() {
        if (facturaSeleccionada == null) {
            Toast.makeText(this, R.string.credito_crear_toast_seleccione_factura, Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaLimitePago = editFechaLimite.getText().toString().trim();
        if (fechaLimitePago.isEmpty()) {
            Toast.makeText(this, R.string.credito_crear_toast_ingrese_fecha_limite, Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar elegibilidad de la factura (doble chequeo)
        if (facturaSeleccionada.getEsCredito() != 0 || !ESTADO_FACTURA_ELEGIBLE.equalsIgnoreCase(facturaSeleccionada.getEstadoFactura())) {
            Toast.makeText(this, R.string.credito_crear_toast_factura_no_valida, Toast.LENGTH_LONG).show();
            // Recargar lista por si acaso cambió mientras estaba en esta pantalla
            facturaViewModel.cargarTodasLasFacturas();
            return;
        }


        Log.i(TAG, "Intentando activar crédito para Factura ID: " + facturaSeleccionada.getIdFactura());

        // --- Preparar datos ---
        // 1. Datos para actualizar Factura
        Factura facturaActualizada = facturaSeleccionada; // Tomar la seleccionada como base
        facturaActualizada.setEsCredito(1); // Marcar como crédito
        facturaActualizada.setEstadoFactura(getString(R.string.factura_estado_en_credito)); // Cambiar estado
        facturaActualizada.setTipoPago(getString(R.string.factura_tipo_pago_credito)); // Cambiar tipo pago

        // 2. Datos para crear Crédito
        Credito nuevoCredito = new Credito();
        nuevoCredito.setIdFactura(facturaActualizada.getIdFactura());
        nuevoCredito.setMontoAutorizadoCredito(facturaActualizada.getMontoTotal());
        nuevoCredito.setMontoPagado(0.0); // Pago inicial es cero
        nuevoCredito.setSaldoPendiente(facturaActualizada.getMontoTotal()); // Saldo inicial
        nuevoCredito.setFechaLimitePago(fechaLimitePago);
        nuevoCredito.setEstadoCredito(getString(R.string.credito_crear_estado_credito_inicial)); // "Activo"

        // --- Ejecutar operaciones (Idealmente en transacción) ---
        // Simplificación: secuencial por ahora
        Log.d(TAG, "Actualizando factura...");
        boolean facturaActualizadaOk = facturaViewModel.actualizarFactura(facturaActualizada);

        long nuevoCreditoId = -1;
        if (facturaActualizadaOk) {
            Log.d(TAG, "Factura actualizada. Insertando crédito...");
            nuevoCreditoId = creditoViewModel.insertarCredito(nuevoCredito);
        } else {
            Log.e(TAG, "Falló la actualización de la factura. No se creará el crédito.");
        }

        // --- Feedback Final ---
        if (facturaActualizadaOk && nuevoCreditoId != -1) {
            Toast.makeText(this,
                    String.format(getString(R.string.credito_crear_toast_exito), nuevoCreditoId, facturaActualizada.getIdFactura()),
                    Toast.LENGTH_LONG).show();
            setResult(Activity.RESULT_OK);
            finish(); // Éxito completo
        } else {
            Toast.makeText(this, R.string.credito_crear_toast_error, Toast.LENGTH_LONG).show();
            // Aquí sería ideal hacer rollback si estuviéramos en una transacción.
            // Si la factura se actualizó pero el crédito falló, hay inconsistencia.
            // Intentar revertir el cambio en factura podría ser una opción, o dejarlo así y loguear.
            Log.e(TAG, "Error en el proceso de creación de crédito. FacturaActualizadaOk=" + facturaActualizadaOk + ", nuevoCreditoId=" + nuevoCreditoId);
        }
    }
}