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
import android.widget.EditText; // Import necesario
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
    // Nuevos componentes para abonos
    private EditText editMontoAbonar;
    private Button btnRealizarAbono;


    // --- Data ---
    private CreditoViewModel creditoViewModel;
    private List<Credito> listaCreditosParaSpinner = new ArrayList<>();
    private Credito creditoSeleccionado;
    private Calendar calendarioFechaLimite = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credito_editar); // Asegúrate que este layout tiene los campos de abono
        setTitle(getString(R.string.credito_editar_title));

        creditoViewModel = new ViewModelProvider(this).get(CreditoViewModel.class);

        inicializarVistas(); // Inicializa TODOS los componentes
        configurarListeners(); // Configura TODOS los listeners
        ocultarDetallesYDeshabilitarControles(); // Estado inicial

        // Observer para la lista FILTRADA (Correcto)
        creditoViewModel.getListaCreditosFiltradosParaUi().observe(this, creditosFiltrados -> {
            Log.d(TAG, "Observer: Lista de créditos filtrados para UI actualizada. Total: " +
                    (creditosFiltrados != null ? creditosFiltrados.size() : "null"));
            if (creditosFiltrados != null) {
                listaCreditosParaSpinner = creditosFiltrados;
                cargarSpinnerCreditosParaEdicion();
            }
        });

        // Observer para el crédito seleccionado (para actualizar UI después de abono/edición)
        creditoViewModel.getCreditoSeleccionado().observe(this, credito -> {
            // Solo actualiza la UI si el crédito observado es el que tenemos seleccionado localmente
            // o si no tenemos ninguno seleccionado (para el caso inicial o después de un pago total)
            if (credito != null && creditoSeleccionado != null && credito.getIdCredito() == creditoSeleccionado.getIdCredito()) {
                Log.d(TAG,"Observer: Crédito seleccionado actualizado. ID: " + credito.getIdCredito());
                creditoSeleccionado = credito; // Actualizar nuestra copia local
                poblarCamposConDatosDelCredito(creditoSeleccionado); // Refrescar la UI
                configurarHabilitacionControles(credito); // Re-evaluar habilitación
            } else if (credito == null && creditoSeleccionado != null) {
                // El ViewModel limpió la selección (quizás después de pago total y recarga)
                // Ocultamos los detalles locales también.
                ocultarDetallesYDeshabilitarControles();
            }
            // Si credito != null pero creditoSeleccionado == null, esperamos a onItemSelected
        });

        Log.d(TAG,"Solicitando carga inicial de créditos (con filtro para UI)...");
        creditoViewModel.cargarTodosLosCreditosYFiltrarParaUi(); // Correcto
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
        // Inicializar nuevos componentes de abono
        editMontoAbonar = findViewById(R.id.editCreditoMontoAbonar);
        btnRealizarAbono = findViewById(R.id.btnCreditoRealizarAbono);
        Log.d(TAG,"Vistas inicializadas.");
    }

    private void configurarListeners() {
        spinnerSeleccionarCredito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && (position - 1) < listaCreditosParaSpinner.size()) {
                    Credito nuevoCreditoSeleccionado = listaCreditosParaSpinner.get(position - 1);
                    // Llama a consultar para actualizar el LiveData creditoSeleccionado
                    // La UI se actualizará cuando el observer de creditoSeleccionado reciba el valor
                    creditoViewModel.consultarCreditoPorId(nuevoCreditoSeleccionado.getIdCredito());
                    scrollViewDetalles.setVisibility(View.VISIBLE); // Mostrar detalles inmediatamente
                } else {
                    ocultarDetallesYDeshabilitarControles();
                    creditoViewModel.limpiarCreditoSeleccionado(); // Limpiar en ViewModel también
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                ocultarDetallesYDeshabilitarControles();
                creditoViewModel.limpiarCreditoSeleccionado();
            }
        });

        editFechaLimite.setOnClickListener(v -> {
            if (editFechaLimite.isEnabled()) mostrarDialogoDatePicker();
        });

        btnActualizarFecha.setOnClickListener(v -> intentarActualizacionFechaLimite());
        // Listener para el nuevo botón de abono
        btnRealizarAbono.setOnClickListener(v -> intentarRealizarAbono());

        Log.d(TAG,"Listeners configurados.");
    }

    // Método separado para habilitar/deshabilitar basado en el estado del crédito
    private void configurarHabilitacionControles(Credito credito) {
        boolean esActivo = credito != null && getString(R.string.credito_estado_activo).equalsIgnoreCase(credito.getEstadoCredito());

        editFechaLimite.setEnabled(esActivo);
        btnActualizarFecha.setEnabled(esActivo);
        editMontoAbonar.setEnabled(esActivo);
        btnRealizarAbono.setEnabled(esActivo);

        // Limpiar campo de abono si el crédito ya no está activo o no hay crédito
        if (!esActivo && editMontoAbonar != null) {
            editMontoAbonar.setText("");
        }
    }


    // --- Métodos de Carga y UI ---

    private void cargarSpinnerCreditosParaEdicion() {
        // ... (Método con el workaround, sin cambios respecto a la versión anterior) ...
        List<String> descripciones = new ArrayList<>();
        descripciones.add(getString(R.string.placeholder_seleccione));

        for (Credito c : listaCreditosParaSpinner) {
            try {
                String saldoFormateado = String.format(Locale.US, "%.2f", c.getSaldoPendiente());
                String descripcion = String.format(Locale.getDefault(),
                        "Crédito #%1$d (Fact: #%2$d) Saldo: $%3$s",
                        c.getIdCredito(), c.getIdFactura(), saldoFormateado);
                descripciones.add(descripcion);
            } catch (Exception e) {
                Log.e(TAG, "Error formateando descripción para spinner - Crédito ID: " + c.getIdCredito(), e);
                descripciones.add("Error - Crédito #" + c.getIdCredito());
            }
        }
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

        // Poblar TextViews existentes
        tvIdCredito.setText(String.valueOf(credito.getIdCredito()));
        tvIdFactura.setText(String.valueOf(credito.getIdFactura()));
        tvMontoAutorizado.setText(String.format(Locale.US, "$%.2f", credito.getMontoAutorizadoCredito()));
        tvMontoPagado.setText(String.format(Locale.US, "$%.2f", credito.getMontoPagado()));
        tvSaldoPendiente.setText(String.format(Locale.US, "$%.2f", credito.getSaldoPendiente()));
        tvEstadoCredito.setText(credito.getEstadoCredito());
        editFechaLimite.setText(credito.getFechaLimitePago());

        // Configurar calendario para el DatePicker
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date fecha = sdf.parse(credito.getFechaLimitePago());
            if (fecha != null) calendarioFechaLimite.setTime(fecha);
        } catch (ParseException e) {
            Log.e(TAG, "Error parseando fecha límite al poblar campos: " + credito.getFechaLimitePago(), e);
            calendarioFechaLimite.setTime(new Date());
        }

        // Limpiar campo de abono cada vez que se pueblan los datos
        editMontoAbonar.setText("");
    }

    private void ocultarDetallesYDeshabilitarControles(){
        if (scrollViewDetalles != null) scrollViewDetalles.setVisibility(View.GONE);
        // Deshabilitar controles existentes
        if (editFechaLimite != null) editFechaLimite.setEnabled(false);
        if (btnActualizarFecha != null) btnActualizarFecha.setEnabled(false);
        // Deshabilitar nuevos controles de abono
        if (editMontoAbonar != null) {
            editMontoAbonar.setText(""); // Limpiar campo
            editMontoAbonar.setEnabled(false);
        }
        if (btnRealizarAbono != null) btnRealizarAbono.setEnabled(false);

        creditoSeleccionado = null; // Limpiar selección local
        Log.d(TAG,"Detalles de edición/abono de crédito ocultados y campos deshabilitados.");
    }

    private void mostrarDialogoDatePicker() {
        // ... (sin cambios) ...
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
        // ... (sin cambios) ...
        String formato = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
        editFechaLimite.setText(sdf.format(calendarioFechaLimite.getTime()));
    }

    // --- Métodos de Acción ---

    private void intentarActualizacionFechaLimite() {
        // ... (lógica sin cambios, solo asegurar llamada correcta a cargar...) ...
        if (creditoSeleccionado == null) {
            Toast.makeText(this, R.string.credito_editar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!getString(R.string.credito_estado_activo).equalsIgnoreCase(creditoSeleccionado.getEstadoCredito())) {
            Toast.makeText(this, R.string.credito_editar_no_editable_estado, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, String.format(getString(R.string.credito_editar_toast_exito), creditoSeleccionado.getIdCredito()), Toast.LENGTH_SHORT).show();
            // La UI se refresca mediante el observer de creditoSeleccionado
        } else {
            Toast.makeText(this, R.string.credito_editar_toast_error, Toast.LENGTH_SHORT).show();
        }
    }

    // --- Nuevo Método para Manejar el Botón de Abono ---
    private void intentarRealizarAbono() {
        if (creditoSeleccionado == null) {
            Toast.makeText(this, R.string.credito_editar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!getString(R.string.credito_estado_activo).equalsIgnoreCase(creditoSeleccionado.getEstadoCredito())) {
            Toast.makeText(this, R.string.credito_editar_no_editable_estado, Toast.LENGTH_SHORT).show();
            return;
        }

        String montoStr = editMontoAbonar.getText().toString().trim();
        if (montoStr.isEmpty()) {
            editMontoAbonar.setError("Ingrese un monto"); // Mejor feedback que Toast
            // Toast.makeText(this, R.string.credito_editar_toast_monto_invalido, Toast.LENGTH_SHORT).show();
            return;
        }

        double montoAbono;
        try {
            montoAbono = Double.parseDouble(montoStr);
        } catch (NumberFormatException e) {
            editMontoAbonar.setError("Monto numérico inválido");
            // Toast.makeText(this, R.string.credito_editar_toast_monto_invalido, Toast.LENGTH_SHORT).show();
            return;
        }

        if (montoAbono <= 0) {
            editMontoAbonar.setError("El monto debe ser positivo");
            // Toast.makeText(this, R.string.credito_editar_toast_monto_invalido, Toast.LENGTH_SHORT).show();
            return;
        }

        double saldoActual = creditoSeleccionado.getSaldoPendiente();
        double tolerancia = 0.001;
        if (montoAbono > saldoActual + tolerancia) {
            editMontoAbonar.setError("El monto excede el saldo pendiente de $" + String.format(Locale.US,"%.2f", saldoActual));
            // Toast.makeText(this, R.string.credito_editar_toast_monto_excede_saldo, Toast.LENGTH_LONG).show();
            return;
        }

        Log.i(TAG, "Intentando realizar abono de " + montoAbono + " para Crédito ID: " + creditoSeleccionado.getIdCredito());

        boolean exito = creditoViewModel.realizarAbono(creditoSeleccionado.getIdCredito(), montoAbono);

        if (exito) {
            Toast.makeText(this, R.string.credito_editar_toast_abono_exito, Toast.LENGTH_SHORT).show();
            editMontoAbonar.setText(""); // Limpiar campo

            // El observer de creditoSeleccionado se encargará de actualizar la UI.
            // Verificamos si el estado cambió a Pagado para mostrar mensaje extra.
            Credito creditoActualizado = creditoViewModel.getCreditoSeleccionado().getValue();
            if (creditoActualizado != null && getString(R.string.credito_estado_pagado).equalsIgnoreCase(creditoActualizado.getEstadoCredito())) {
                Toast.makeText(this, R.string.credito_editar_toast_abono_credito_pagado, Toast.LENGTH_LONG).show();
                // El ViewModel llamó a cargarTodosLosCreditosYFiltrarParaUi,
                // lo que quitará este crédito del spinner a través del observer de la lista.
                // Resetear selección y ocultar detalles.
                ocultarDetallesYDeshabilitarControles();
                if(spinnerSeleccionarCredito.getAdapter() != null && spinnerSeleccionarCredito.getAdapter().getCount() > 0){
                    spinnerSeleccionarCredito.setSelection(0);
                }
            }
        } else {
            Toast.makeText(this, R.string.credito_editar_toast_abono_error, Toast.LENGTH_SHORT).show();
            // Podría ser útil recargar explícitamente si el ViewModel no lo hizo por alguna razón
            // creditoViewModel.cargarTodosLosCreditosYFiltrarParaUi();
        }
    }
}