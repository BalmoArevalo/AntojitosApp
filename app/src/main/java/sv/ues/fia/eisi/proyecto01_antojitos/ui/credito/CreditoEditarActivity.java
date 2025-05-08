package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito; // Ajusta el paquete si es necesario

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
// Importar ViewModel y POJO
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.Credito;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.CreditoViewModel;

public class CreditoEditarActivity extends AppCompatActivity {

    private static final String TAG = "CreditoEditarActivity";

    // --- UI Components ---
    private Spinner spinnerSeleccionarCredito;
    private ScrollView scrollViewDetalles;
    // TextViews para mostrar datos
    private TextView tvIdCredito, tvIdFactura, tvMontoAutorizado, tvMontoPagado,
            tvSaldoPendiente, tvEstadoCredito;
    // EditText para el campo editable
    private EditText editFechaLimite;
    private Button btnActualizarFecha;

    // --- Data ---
    private CreditoViewModel creditoViewModel;
    private List<Credito> listaCreditosActivos = new ArrayList<>(); // Para el spinner
    private Credito creditoSeleccionado; // El crédito cargado actualmente
    private Calendar calendarioFechaLimite = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credito_editar);
        setTitle(getString(R.string.credito_editar_title));

        creditoViewModel = new ViewModelProvider(this).get(CreditoViewModel.class);

        inicializarVistas();
        configurarListeners();
        ocultarDetallesYDeshabilitar(); // Estado inicial

        // Observador para la lista de créditos ACTIVOS
        creditoViewModel.getListaCreditosActivos().observe(this, creditosActivos -> {
            Log.d(TAG, "Observer: Lista de créditos activos actualizada. Total: " + (creditosActivos != null ? creditosActivos.size() : "null"));
            if (creditosActivos != null) {
                listaCreditosActivos = creditosActivos;
                cargarSpinnerCreditosActivos();
            }
        });

        // Cargar los créditos activos al inicio
        Log.d(TAG,"Solicitando carga inicial de créditos activos...");
        creditoViewModel.cargarTodosLosCreditos(); // El ViewModel filtra los activos
    }

    // Método para centralizar inicialización de vistas
    private void inicializarVistas() {
        spinnerSeleccionarCredito = findViewById(R.id.spinnerSeleccionarCreditoEditar);
        scrollViewDetalles = findViewById(R.id.scrollViewCreditoEditar);
        tvIdCredito = findViewById(R.id.tvEditarCreditoIdCredito);
        tvIdFactura = findViewById(R.id.tvEditarCreditoIdFactura);
        tvMontoAutorizado = findViewById(R.id.tvEditarCreditoMontoAutorizado);
        tvMontoPagado = findViewById(R.id.tvEditarCreditoMontoPagado);
        tvSaldoPendiente = findViewById(R.id.tvEditarCreditoSaldoPendiente);
        tvEstadoCredito = findViewById(R.id.tvEditarCreditoEstado);
        editFechaLimite = findViewById(R.id.editCreditoEditarFechaLimite);
        btnActualizarFecha = findViewById(R.id.btnActualizarFechaCredito);
        Log.d(TAG,"Vistas inicializadas.");
    }

    // Método para configurar listeners
    private void configurarListeners() {
        spinnerSeleccionarCredito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && (position - 1) < listaCreditosActivos.size()) {
                    creditoSeleccionado = listaCreditosActivos.get(position - 1);
                    poblarCampos(creditoSeleccionado);
                    scrollViewDetalles.setVisibility(View.VISIBLE);
                    editFechaLimite.setEnabled(true);
                    btnActualizarFecha.setEnabled(true);
                } else {
                    ocultarDetallesYDeshabilitar();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                ocultarDetallesYDeshabilitar();
            }
        });

        editFechaLimite.setOnClickListener(v -> {
            if (editFechaLimite.isEnabled()) mostrarDatePickerDialog();
        });

        btnActualizarFecha.setOnClickListener(v -> intentarActualizarFechaLimite());
        Log.d(TAG,"Listeners configurados.");
    }

    // Carga el spinner con créditos activos
    private void cargarSpinnerCreditosActivos() {
        List<String> descripciones = new ArrayList<>();
        descripciones.add(getString(R.string.placeholder_seleccione));

        for (Credito c : listaCreditosActivos) {
            descripciones.add(String.format(Locale.getDefault(), "Crédito #%d (Fact: #%d) Saldo: $%.2f",
                    c.getIdCredito(), c.getIdFactura(), c.getSaldoPendiente()));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, descripciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeleccionarCredito.setAdapter(adapter);

        if (descripciones.size() <= 1) {
            Toast.makeText(this, R.string.credito_editar_no_creditos_activos, Toast.LENGTH_LONG).show();
            spinnerSeleccionarCredito.setEnabled(false);
        } else {
            spinnerSeleccionarCredito.setEnabled(true);
        }
        ocultarDetallesYDeshabilitar(); // Resetear al recargar spinner
    }

    // Rellena la UI con los datos del crédito seleccionado
    private void poblarCampos(Credito c) {
        if (c == null) return;
        Log.d(TAG,"Poblando campos para editar Crédito ID: " + c.getIdCredito());

        // Campos de solo lectura
        tvIdCredito.setText(String.valueOf(c.getIdCredito()));
        tvIdFactura.setText(String.valueOf(c.getIdFactura()));
        tvMontoAutorizado.setText(String.format(Locale.US, "$%.2f", c.getMontoAutorizadoCredito()));
        tvMontoPagado.setText(String.format(Locale.US, "$%.2f", c.getMontoPagado()));
        tvSaldoPendiente.setText(String.format(Locale.US, "$%.2f", c.getSaldoPendiente()));
        tvEstadoCredito.setText(c.getEstadoCredito());

        // Campo editable: Fecha Límite
        editFechaLimite.setText(c.getFechaLimitePago());
        // Configurar calendario para el DatePicker
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date fecha = sdf.parse(c.getFechaLimitePago());
            if (fecha != null) calendarioFechaLimite.setTime(fecha);
        } catch (ParseException e) {
            Log.e(TAG, "Error parseando fecha límite al poblar: " + c.getFechaLimitePago(), e);
        }
    }

    // Oculta la sección de detalles y deshabilita controles de edición/guardado
    private void ocultarDetallesYDeshabilitar(){
        if (scrollViewDetalles != null) scrollViewDetalles.setVisibility(View.GONE);
        if (editFechaLimite != null) editFechaLimite.setEnabled(false);
        if (btnActualizarFecha != null) btnActualizarFecha.setEnabled(false);
        creditoSeleccionado = null;
        Log.d(TAG,"Detalles de edición de crédito ocultados y campos deshabilitados.");
    }

    // Muestra el DatePickerDialog para la fecha límite
    private void mostrarDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendarioFechaLimite.set(Calendar.YEAR, year);
            calendarioFechaLimite.set(Calendar.MONTH, month);
            calendarioFechaLimite.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarEditTextFechaLimite();
        };
        new DatePickerDialog(this, dateSetListener,
                calendarioFechaLimite.get(Calendar.YEAR),
                calendarioFechaLimite.get(Calendar.MONTH),
                calendarioFechaLimite.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Actualiza el texto del EditText de fecha límite
    private void actualizarEditTextFechaLimite() {
        String formato = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
        editFechaLimite.setText(sdf.format(calendarioFechaLimite.getTime()));
    }

    // Intenta actualizar la fecha límite del crédito seleccionado
    private void intentarActualizarFechaLimite() {
        if (creditoSeleccionado == null) {
            Toast.makeText(this, R.string.credito_editar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }

        String nuevaFechaLimiteStr = editFechaLimite.getText().toString().trim();
        if (nuevaFechaLimiteStr.isEmpty()) {
            Toast.makeText(this, R.string.credito_editar_toast_ingrese_fecha, Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que la fecha no sea la misma (opcional, para evitar llamadas innecesarias)
        if (nuevaFechaLimiteStr.equals(creditoSeleccionado.getFechaLimitePago())) {
            Toast.makeText(this, "La fecha límite no ha cambiado.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "Intentando actualizar fecha límite para Crédito ID: " + creditoSeleccionado.getIdCredito() + " a " + nuevaFechaLimiteStr);

        // Llamar al ViewModel para actualizar
        boolean exito = creditoViewModel.actualizarFechaLimite(creditoSeleccionado.getIdCredito(), nuevaFechaLimiteStr);

        if (exito) {
            Toast.makeText(this,
                    String.format(getString(R.string.credito_editar_toast_exito), creditoSeleccionado.getIdCredito()),
                    Toast.LENGTH_SHORT).show();
            // Refrescar datos y UI
            creditoViewModel.cargarTodosLosCreditos(); // Recarga para actualizar LiveData y Spinner
            ocultarDetallesYDeshabilitar();
            spinnerSeleccionarCredito.setSelection(0);
            // setResult(Activity.RESULT_OK); // Si se necesita devolver resultado
            // finish(); // Opcional: Cerrar
        } else {
            Toast.makeText(this, R.string.credito_editar_toast_error, Toast.LENGTH_SHORT).show();
        }
    }
}