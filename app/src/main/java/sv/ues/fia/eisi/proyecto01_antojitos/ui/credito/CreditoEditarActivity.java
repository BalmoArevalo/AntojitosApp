package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class CreditoEditarActivity extends AppCompatActivity {

    private static final String TAG = "CreditoEditarActivity";

    // --- UI Components ---
    private Spinner spinnerSeleccionarCredito;
    private ScrollView scrollViewDetalles;
    private TextView tvIdCredito, tvIdFactura, tvMontoAutorizado, tvMontoPagado,
            tvSaldoPendiente, tvEstadoCredito;
    private EditText editFechaLimite;
    private Button btnActualizarFecha;

    // --- Data ---
    private CreditoViewModel creditoViewModel;
    private List<Credito> listaCreditosParaSpinner = new ArrayList<>();
    private Credito creditoSeleccionado;
    private Calendar calendarioFechaLimite = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credito_editar);
        setTitle(getString(R.string.credito_editar_title));

        creditoViewModel = new ViewModelProvider(this).get(CreditoViewModel.class);

        inicializarVistas();
        configurarListeners();
        ocultarDetallesYDeshabilitarControles();

        creditoViewModel.getListaCreditosFiltradosParaUi().observe(this, creditosFiltrados -> {
            Log.d(TAG, "Observer: Lista de créditos filtrados para UI actualizada. Total: " +
                    (creditosFiltrados != null ? creditosFiltrados.size() : "null"));
            if (creditosFiltrados != null) {
                listaCreditosParaSpinner = creditosFiltrados;
                cargarSpinnerCreditosParaEdicion(); // Llamada al método modificado
            }
        });

        Log.d(TAG,"Solicitando carga inicial de créditos (con filtro para UI)...");
        creditoViewModel.cargarTodosLosCreditosYFiltrarParaUi();
    }

    // --- Métodos de Inicialización y Configuración ---

    private void inicializarVistas() {
        spinnerSeleccionarCredito = findViewById(R.id.spinnerSeleccionarCreditoEditar);
        scrollViewDetalles = findViewById(R.id.scrollViewCreditoEditarDetalles);
        tvIdCredito = findViewById(R.id.tvCreditoEditarIdCredito);
        tvIdFactura = findViewById(R.id.tvCreditoEditarIdFactura);
        tvMontoAutorizado = findViewById(R.id.tvCreditoEditarMontoAutorizado);
        tvMontoPagado = findViewById(R.id.tvCreditoEditarMontoPagado);
        tvSaldoPendiente = findViewById(R.id.tvCreditoEditarSaldoPendiente);
        tvEstadoCredito = findViewById(R.id.tvCreditoEditarEstadoCredito);
        editFechaLimite = findViewById(R.id.editCreditoEditarFechaLimite);
        btnActualizarFecha = findViewById(R.id.btnCreditoEditarActualizarFecha);
        Log.d(TAG,"Vistas inicializadas.");
    }

    private void configurarListeners() {
        spinnerSeleccionarCredito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && (position - 1) < listaCreditosParaSpinner.size()) {
                    creditoSeleccionado = listaCreditosParaSpinner.get(position - 1);
                    poblarCamposConDatosDelCredito(creditoSeleccionado);
                    scrollViewDetalles.setVisibility(View.VISIBLE);
                    if (creditoSeleccionado != null && getString(R.string.credito_estado_activo).equalsIgnoreCase(creditoSeleccionado.getEstadoCredito())) {
                        editFechaLimite.setEnabled(true);
                        btnActualizarFecha.setEnabled(true);
                    } else {
                        editFechaLimite.setEnabled(false);
                        btnActualizarFecha.setEnabled(false);
                        if (creditoSeleccionado != null) {
                            Toast.makeText(CreditoEditarActivity.this, R.string.credito_editar_no_editable_estado, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    ocultarDetallesYDeshabilitarControles();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                ocultarDetallesYDeshabilitarControles();
            }
        });

        editFechaLimite.setOnClickListener(v -> {
            if (editFechaLimite.isEnabled()) mostrarDialogoDatePicker();
        });

        btnActualizarFecha.setOnClickListener(v -> intentarActualizacionFechaLimite());
        Log.d(TAG,"Listeners configurados.");
    }

    // --- Métodos de Carga y UI ---

    private void cargarSpinnerCreditosParaEdicion() {
        List<String> descripciones = new ArrayList<>();
        descripciones.add(getString(R.string.placeholder_seleccione));

        // *** INICIO DEL WORKAROUND ***
        for (Credito c : listaCreditosParaSpinner) {
            try {
                // 1. Formatear el saldo (double) por separado, forzando Locale US para el punto decimal
                String saldoFormateado = String.format(Locale.US, "%.2f", c.getSaldoPendiente());

                // 2. Construir el string final usando %s para el saldo ya formateado
                String descripcion = String.format(Locale.getDefault(), // Usar Locale por defecto para el texto general
                        "Crédito #%1$d (Fact: #%2$d) Saldo: $%3$s", // Usar %1$d, %2$d, %3$s
                        c.getIdCredito(),       // Argumento 1 (int)
                        c.getIdFactura(),       // Argumento 2 (int)
                        saldoFormateado);       // Argumento 3 (String)
                descripciones.add(descripcion);

            } catch (Exception e) {
                // Capturar cualquier excepción durante el formateo de este item específico
                Log.e(TAG, "Error formateando descripción para spinner - Crédito ID: " + c.getIdCredito(), e);
                // Añadir un mensaje de error al spinner para este item
                descripciones.add("Error - Crédito #" + c.getIdCredito());
            }
        }
        // *** FIN DEL WORKAROUND ***

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, descripciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeleccionarCredito.setAdapter(adapter);

        if (descripciones.size() <= 1) {
            Toast.makeText(this, R.string.credito_editar_no_creditos_editables, Toast.LENGTH_LONG).show();
            spinnerSeleccionarCredito.setEnabled(false);
            ocultarDetallesYDeshabilitarControles();
        } else {
            spinnerSeleccionarCredito.setEnabled(true);
        }
    }


    private void poblarCamposConDatosDelCredito(Credito credito) {
        if (credito == null) {
            ocultarDetallesYDeshabilitarControles();
            return;
        }
        Log.d(TAG,"Poblando campos para editar Crédito ID: " + credito.getIdCredito());

        tvIdCredito.setText(String.valueOf(credito.getIdCredito()));
        tvIdFactura.setText(String.valueOf(credito.getIdFactura()));
        tvMontoAutorizado.setText(String.format(Locale.US, "$%.2f", credito.getMontoAutorizadoCredito()));
        tvMontoPagado.setText(String.format(Locale.US, "$%.2f", credito.getMontoPagado()));
        tvSaldoPendiente.setText(String.format(Locale.US, "$%.2f", credito.getSaldoPendiente()));
        tvEstadoCredito.setText(credito.getEstadoCredito());
        editFechaLimite.setText(credito.getFechaLimitePago());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date fecha = sdf.parse(credito.getFechaLimitePago());
            if (fecha != null) calendarioFechaLimite.setTime(fecha);
        } catch (ParseException e) {
            Log.e(TAG, "Error parseando fecha límite al poblar campos: " + credito.getFechaLimitePago(), e);
            calendarioFechaLimite.setTime(new Date());
        }
    }

    private void ocultarDetallesYDeshabilitarControles(){
        if (scrollViewDetalles != null) scrollViewDetalles.setVisibility(View.GONE);
        if (editFechaLimite != null) editFechaLimite.setEnabled(false);
        if (btnActualizarFecha != null) btnActualizarFecha.setEnabled(false);
        creditoSeleccionado = null;
        Log.d(TAG,"Detalles de edición de crédito ocultados y campos deshabilitados.");
    }

    private void mostrarDialogoDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendarioFechaLimite.set(Calendar.YEAR, year);
            calendarioFechaLimite.set(Calendar.MONTH, month);
            calendarioFechaLimite.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarCampoTextoFechaLimite();
        };
        new DatePickerDialog(this, dateSetListener,
                calendarioFechaLimite.get(Calendar.YEAR),
                calendarioFechaLimite.get(Calendar.MONTH),
                calendarioFechaLimite.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void actualizarCampoTextoFechaLimite() {
        String formato = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
        editFechaLimite.setText(sdf.format(calendarioFechaLimite.getTime()));
    }

    // --- Método de Acción ---

    private void intentarActualizacionFechaLimite() {
        if (creditoSeleccionado == null) {
            Toast.makeText(this, R.string.credito_editar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!getString(R.string.credito_estado_activo).equalsIgnoreCase(creditoSeleccionado.getEstadoCredito())) {
            Toast.makeText(this, R.string.credito_editar_no_editable_estado, Toast.LENGTH_SHORT).show();
            creditoViewModel.cargarTodosLosCreditosYFiltrarParaUi();
            ocultarDetallesYDeshabilitarControles();
            if(spinnerSeleccionarCredito.getAdapter() != null && spinnerSeleccionarCredito.getAdapter().getCount() > 0){
                spinnerSeleccionarCredito.setSelection(0);
            }
            return;
        }

        String nuevaFechaLimiteStr = editFechaLimite.getText().toString().trim();
        if (nuevaFechaLimiteStr.isEmpty()) {
            Toast.makeText(this, R.string.credito_editar_toast_ingrese_fecha, Toast.LENGTH_SHORT).show();
            return;
        }

        if (nuevaFechaLimiteStr.equals(creditoSeleccionado.getFechaLimitePago())) {
            Toast.makeText(this, R.string.credito_editar_toast_fecha_no_cambiada, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "Intentando actualizar fecha límite para Crédito ID: " + creditoSeleccionado.getIdCredito() + " a " + nuevaFechaLimiteStr);

        boolean exito = creditoViewModel.actualizarFechaLimite(creditoSeleccionado.getIdCredito(), nuevaFechaLimiteStr);

        if (exito) {
            Toast.makeText(this,
                    String.format(getString(R.string.credito_editar_toast_exito), creditoSeleccionado.getIdCredito()),
                    Toast.LENGTH_SHORT).show();

            ocultarDetallesYDeshabilitarControles();
            if(spinnerSeleccionarCredito.getAdapter() != null && spinnerSeleccionarCredito.getAdapter().getCount() > 0){
                spinnerSeleccionarCredito.setSelection(0);
            }
        } else {
            Toast.makeText(this, R.string.credito_editar_toast_error, Toast.LENGTH_SHORT).show();
            creditoViewModel.cargarTodosLosCreditosYFiltrarParaUi();
        }
    }
}