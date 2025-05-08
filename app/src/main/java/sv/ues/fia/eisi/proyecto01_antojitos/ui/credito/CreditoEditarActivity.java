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
    private EditText editMontoAbonar;
    private Button btnRealizarAbono;

    // --- Data ---
    private CreditoViewModel creditoViewModel;
    private List<Credito> listaCreditosParaSpinner = new ArrayList<>();
    private Credito creditoSeleccionado; // Variable local para el crédito actualmente mostrado/editado
    private Calendar calendarioFechaLimite = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credito_editar);
        setTitle(getString(R.string.credito_editar_title));

        creditoViewModel = new ViewModelProvider(this).get(CreditoViewModel.class);

        inicializarVistas();
        configurarListeners();
        ocultarDetallesYDeshabilitarControles(); // Estado inicial UI

        // Observer para la lista de créditos (para el Spinner)
        creditoViewModel.getListaCreditosFiltradosParaUi().observe(this, creditosFiltrados -> {
            Log.d(TAG, "Observer: Lista de créditos para UI actualizada. Total: " +
                    (creditosFiltrados != null ? creditosFiltrados.size() : "null"));
            if (creditosFiltrados != null) {
                listaCreditosParaSpinner = creditosFiltrados;
                cargarSpinnerCreditosParaEdicion();
            }
        });

        // --- OBSERVER CORREGIDO ---
        // Observer para el crédito individual que ha sido seleccionado o modificado
        creditoViewModel.getCreditoSeleccionado().observe(this, creditoRecibidoDelViewModel -> {
            if (creditoRecibidoDelViewModel != null) {
                // Un crédito válido ha sido provisto por el ViewModel.
                Log.d(TAG,"Observer: Crédito seleccionado/actualizado desde ViewModel. ID: " + creditoRecibidoDelViewModel.getIdCredito());
                this.creditoSeleccionado = creditoRecibidoDelViewModel; // Actualizar nuestra referencia local.
                poblarCamposConDatosDelCredito(this.creditoSeleccionado); // Llenar la UI con los datos.
                configurarHabilitacionControles(this.creditoSeleccionado); // Habilitar/deshabilitar controles según el estado del crédito.
                scrollViewDetalles.setVisibility(View.VISIBLE); // Asegurar que la sección de detalles sea visible.
            } else {
                // El ViewModel indica que no hay crédito seleccionado (o fue limpiado, o la consulta falló).
                Log.d(TAG,"Observer: ViewModel limpió la selección de crédito, la consulta falló, o el crédito ya no es editable.");
                ocultarDetallesYDeshabilitarControles(); // Ocultar la sección de detalles y deshabilitar todo.
                this.creditoSeleccionado = null; // Limpiar nuestra referencia local.
            }
        });
        // --- FIN OBSERVER CORREGIDO ---

        Log.d(TAG,"Solicitando carga inicial de créditos (con filtro para UI)...");
        creditoViewModel.cargarTodosLosCreditosYFiltrarParaUi();
    }

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
        editMontoAbonar = findViewById(R.id.editCreditoMontoAbonar);
        btnRealizarAbono = findViewById(R.id.btnCreditoRealizarAbono);
        Log.d(TAG,"Vistas inicializadas.");
    }

    private void configurarListeners() {
        spinnerSeleccionarCredito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && (position - 1) < listaCreditosParaSpinner.size()) {
                    Credito creditoDeLista = listaCreditosParaSpinner.get(position - 1);
                    // Indicar al ViewModel que este es el crédito que queremos ver/editar.
                    // El observer de `getCreditoSeleccionado()` se encargará de actualizar la UI.
                    creditoViewModel.consultarCreditoPorId(creditoDeLista.getIdCredito());
                } else {
                    // Se seleccionó el placeholder "Seleccione..." o la lista está vacía.
                    // Indicar al ViewModel que limpie la selección actual.
                    creditoViewModel.limpiarCreditoSeleccionado();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                creditoViewModel.limpiarCreditoSeleccionado();
            }
        });

        editFechaLimite.setOnClickListener(v -> {
            if (editFechaLimite.isEnabled()) mostrarDialogoDatePicker();
        });

        btnActualizarFecha.setOnClickListener(v -> intentarActualizacionFechaLimite());
        btnRealizarAbono.setOnClickListener(v -> intentarRealizarAbono());

        Log.d(TAG,"Listeners configurados.");
    }

    private void configurarHabilitacionControles(Credito credito) {
        // Se asume que 'credito' no es null porque el observer ya lo manejó si lo fuera.
        boolean esActivoYEditable = getString(R.string.credito_estado_activo).equalsIgnoreCase(credito.getEstadoCredito());
        Log.d(TAG, "configurarHabilitacionControles: Crédito ID " + credito.getIdCredito() + ", Estado: " + credito.getEstadoCredito() + ", esActivoYEditable: " + esActivoYEditable);

        editFechaLimite.setEnabled(esActivoYEditable);
        btnActualizarFecha.setEnabled(esActivoYEditable);
        editMontoAbonar.setEnabled(esActivoYEditable);
        btnRealizarAbono.setEnabled(esActivoYEditable);

        if (!esActivoYEditable && editMontoAbonar != null) {
            editMontoAbonar.setText("");
            editMontoAbonar.setError(null);
        }
    }

    private void cargarSpinnerCreditosParaEdicion() {
        List<String> descripciones = new ArrayList<>();
        descripciones.add(getString(R.string.placeholder_seleccione));

        for (Credito c : listaCreditosParaSpinner) {
            try {
                // Usar el strings.xml para el formato si es posible, o mantener el formato directo.
                // El strings.xml credito_spinner_item_format usa $%.2f, Locale.US es buena idea para asegurar el punto decimal.
                String saldoFormateado = String.format(Locale.US, "%.2f", c.getSaldoPendiente());
                // String descripcion = getString(R.string.credito_spinner_item_format, c.getIdCredito(), c.getIdFactura(), c.getSaldoPendiente()); // Si el formato es %.2f
                // Si el formato en strings.xml es %s para el saldo ya formateado:
                String descripcion = String.format(Locale.getDefault(), "Crédito #%1$d (Fact: #%2$d) Saldo: $%3$s", c.getIdCredito(), c.getIdFactura(), saldoFormateado);


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
            // El observer de creditoSeleccionado (al recibir null si se limpia la seleccion)
            // se encargará de ocultar los detalles si es necesario.
        } else {
            spinnerSeleccionarCredito.setEnabled(true);
        }
    }

    private void poblarCamposConDatosDelCredito(Credito credito) {
        // Se asume que 'credito' no es null aquí porque el observer ya lo verificó y lo pasó.
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
            Log.e(TAG, "Error parseando fecha límite '" + credito.getFechaLimitePago() + "' al poblar campos: ", e);
            calendarioFechaLimite.setTime(new Date()); // Fallback a fecha actual
        }

        editMontoAbonar.setText(""); // Siempre limpiar campo de abono al mostrar/refrescar un crédito
        editMontoAbonar.setError(null); // Limpiar errores previos del campo de abono
    }

    private void ocultarDetallesYDeshabilitarControles(){
        if (scrollViewDetalles != null) scrollViewDetalles.setVisibility(View.GONE);

        if (editFechaLimite != null) editFechaLimite.setEnabled(false);
        if (btnActualizarFecha != null) btnActualizarFecha.setEnabled(false);

        if (editMontoAbonar != null) {
            editMontoAbonar.setText("");
            editMontoAbonar.setError(null);
            editMontoAbonar.setEnabled(false);
        }
        if (btnRealizarAbono != null) btnRealizarAbono.setEnabled(false);

        // this.creditoSeleccionado = null; // La variable local 'this.creditoSeleccionado' se actualiza en el observer.
        Log.d(TAG,"Detalles de edición/abono de crédito ocultados y campos deshabilitados.");
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

    private void intentarActualizacionFechaLimite() {
        if (this.creditoSeleccionado == null) {
            Toast.makeText(this, R.string.credito_editar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!getString(R.string.credito_estado_activo).equalsIgnoreCase(this.creditoSeleccionado.getEstadoCredito())) {
            Toast.makeText(this, R.string.credito_editar_no_editable_estado, Toast.LENGTH_SHORT).show();
            return;
        }
        String nuevaFechaLimiteStr = editFechaLimite.getText().toString().trim();
        if (nuevaFechaLimiteStr.isEmpty()) {
            Toast.makeText(this, R.string.credito_editar_toast_ingrese_fecha, Toast.LENGTH_SHORT).show();
            return;
        }
        if (nuevaFechaLimiteStr.equals(this.creditoSeleccionado.getFechaLimitePago())) {
            Toast.makeText(this, R.string.credito_editar_toast_fecha_no_cambiada, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, "Intentando actualizar fecha límite para Crédito ID: " + this.creditoSeleccionado.getIdCredito() + " a " + nuevaFechaLimiteStr);
        boolean exito = creditoViewModel.actualizarFechaLimite(this.creditoSeleccionado.getIdCredito(), nuevaFechaLimiteStr);

        if (exito) {
            Toast.makeText(this, String.format(getString(R.string.credito_editar_toast_exito), this.creditoSeleccionado.getIdCredito()), Toast.LENGTH_SHORT).show();
            // La UI se refrescará a través del observer de `creditoViewModel.getCreditoSeleccionado()`
        } else {
            Toast.makeText(this, R.string.credito_editar_toast_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void intentarRealizarAbono() {
        if (this.creditoSeleccionado == null) {
            Toast.makeText(this, R.string.credito_editar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!getString(R.string.credito_estado_activo).equalsIgnoreCase(this.creditoSeleccionado.getEstadoCredito())) {
            Toast.makeText(this, R.string.credito_editar_no_editable_estado, Toast.LENGTH_SHORT).show();
            return;
        }

        String montoStr = editMontoAbonar.getText().toString().trim();
        if (montoStr.isEmpty()) {
            // Usando el string que sí existe y es genérico.
            editMontoAbonar.setError(getString(R.string.credito_editar_toast_monto_invalido));
            return;
        }

        double montoAbono;
        try {
            montoAbono = Double.parseDouble(montoStr);
        } catch (NumberFormatException e) {
            editMontoAbonar.setError(getString(R.string.credito_editar_toast_monto_invalido)); // Reutilizando
            return;
        }

        if (montoAbono <= 0) {
            editMontoAbonar.setError(getString(R.string.credito_editar_toast_monto_invalido)); // Reutilizando
            return;
        }

        double saldoActual = this.creditoSeleccionado.getSaldoPendiente();
        double tolerancia = 0.001;
        if (montoAbono > saldoActual + tolerancia) {
            // Formateando el saldo para el mensaje de error
            String saldoFormateado = String.format(Locale.US,"%.2f", saldoActual);
            editMontoAbonar.setError(getString(R.string.credito_editar_toast_monto_excede_saldo) + " ($" + saldoFormateado + ")");
            return;
        }

        Log.i(TAG, "Intentando realizar abono de " + montoAbono + " para Crédito ID: " + this.creditoSeleccionado.getIdCredito());
        boolean exito = creditoViewModel.realizarAbono(this.creditoSeleccionado.getIdCredito(), montoAbono);

        if (exito) {
            Toast.makeText(this, R.string.credito_editar_toast_abono_exito, Toast.LENGTH_SHORT).show();
            editMontoAbonar.setText("");
            editMontoAbonar.setError(null);

            // No es necesario verificar aquí si el crédito se pagó y luego resetear el spinner manualmente.
            // El ViewModel, al realizar el abono, llama a `consultarCreditoPorId` y
            // `cargarTodosLosCreditosYFiltrarParaUi`.
            // 1. `consultarCreditoPorId` actualiza `creditoSeleccionado` LiveData: El observer
            //    en esta Activity recibirá el crédito (posiblemente con estado "Pagado") y
            //    llamará a poblarCampos/configurarHabilitacion (deshabilitando si está "Pagado").
            // 2. `cargarTodosLosCreditosYFiltrarParaUi` actualiza `listaCreditosFiltradosParaUi` LiveData:
            //    El observer de esta lista repoblará el spinner. Si el crédito se pagó, ya no estará "Activo"
            //    y por lo tanto no aparecerá en el spinner. Si el spinner se repuebla y el crédito
            //    seleccionado ya no está, el `onItemSelected` podría llamarse con posición 0, lo que
            //    llevaría a `limpiarCreditoSeleccionado()` en el ViewModel, y el observer de `creditoSeleccionado`
            //    recibiría null, ocultando los detalles. Este flujo debería ser automático.

        } else {
            Toast.makeText(this, R.string.credito_editar_toast_abono_error, Toast.LENGTH_SHORT).show();
        }
    }
}