package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class CreditoEliminarActivity extends AppCompatActivity {

    private static final String TAG = "CreditoEliminarActivity"; // Nombre de clase completo

    // UI Components
    private Spinner spinnerSeleccionarCredito;
    private ScrollView scrollViewDetalles;
    private TextView tvIdCredito, tvIdFactura, tvMontoAutorizado, tvMontoPagado,
            tvSaldoPendiente, tvFechaLimite, tvEstadoCredito;
    private Button btnConfirmarCancelacion;

    // Data
    private CreditoViewModel creditoViewModel;
    private List<Credito> listaCreditosParaSpinner = new ArrayList<>();
    private Credito creditoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credito_eliminar);
        setTitle(getString(R.string.credito_cancelar_title));

        creditoViewModel = new ViewModelProvider(this).get(CreditoViewModel.class);

        inicializarVistas();
        configurarListeners();
        ocultarDetallesYDeshabilitarControles();

        creditoViewModel.getListaCreditosFiltradosParaUi().observe(this, creditosFiltrados -> {
            Log.d(TAG, "Observer: Lista de créditos filtrados para UI actualizada. Total: " +
                    (creditosFiltrados != null ? creditosFiltrados.size() : "null"));
            if (creditosFiltrados != null) {
                listaCreditosParaSpinner = creditosFiltrados;
                cargarSpinnerCreditosParaCancelacion(); // Llamada al método corregido abajo
            }
        });

        Log.d(TAG,"Solicitando carga inicial de créditos (con filtro para UI)...");
        creditoViewModel.cargarTodosLosCreditosYFiltrarParaUi();

        btnConfirmarCancelacion.setOnClickListener(v -> mostrarDialogoDeConfirmacion());
    }

    private void inicializarVistas() {
        spinnerSeleccionarCredito = findViewById(R.id.spinnerSeleccionarCreditoCancelar);
        scrollViewDetalles = findViewById(R.id.scrollViewCreditoCancelarDetalles);
        tvIdCredito = findViewById(R.id.tvCancelarCreditoIdCredito);
        tvIdFactura = findViewById(R.id.tvCancelarCreditoIdFactura);
        tvMontoAutorizado = findViewById(R.id.tvCancelarCreditoMontoAutorizado);
        tvMontoPagado = findViewById(R.id.tvCancelarCreditoMontoPagado);
        tvSaldoPendiente = findViewById(R.id.tvCancelarCreditoSaldoPendiente);
        tvFechaLimite = findViewById(R.id.tvCancelarCreditoFechaLimite);
        tvEstadoCredito = findViewById(R.id.tvCancelarCreditoEstadoCredito);
        btnConfirmarCancelacion = findViewById(R.id.btnCreditoCancelarConfirmar);
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

                    boolean esCancelablePorEstado = getString(R.string.credito_estado_activo).equalsIgnoreCase(creditoSeleccionado.getEstadoCredito()) &&
                            creditoSeleccionado.getMontoPagado() == 0;
                    btnConfirmarCancelacion.setEnabled(esCancelablePorEstado);

                    if (!esCancelablePorEstado && creditoSeleccionado != null) {
                        Toast.makeText(CreditoEliminarActivity.this, R.string.credito_cancelar_toast_no_cancelable, Toast.LENGTH_LONG).show();
                    }

                } else {
                    ocultarDetallesYDeshabilitarControles();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                ocultarDetallesYDeshabilitarControles();
            }
        });
        Log.d(TAG,"Listeners configurados.");
    }

    // ----- MÉTODO CORREGIDO -----
    private void cargarSpinnerCreditosParaCancelacion() {
        List<String> descripciones = new ArrayList<>();
        descripciones.add(getString(R.string.placeholder_seleccione));

        // *** INICIO DEL WORKAROUND (Igual que en Editar) ***
        for (Credito c : listaCreditosParaSpinner) {
            try {
                // 1. Formatear el saldo (double) por separado, forzando Locale US
                String saldoFormateado = String.format(Locale.US, "%.2f", c.getSaldoPendiente());

                // 2. Construir el string final usando %s para el saldo ya formateado
                //    (Se usa un formato directo aquí en lugar de R.string.credito_spinner_item_format)
                String descripcion = String.format(Locale.getDefault(),
                        "Crédito #%1$d (Fact: #%2$d) Saldo: $%3$s", // Formato con %s para saldo
                        c.getIdCredito(),       // Argumento 1 (int)
                        c.getIdFactura(),       // Argumento 2 (int)
                        saldoFormateado);       // Argumento 3 (String)
                descripciones.add(descripcion);

            } catch (Exception e) {
                Log.e(TAG, "Error formateando descripción para spinner - Crédito ID: " + c.getIdCredito(), e);
                descripciones.add("Error - Crédito #" + c.getIdCredito());
            }
        }
        // *** FIN DEL WORKAROUND ***

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, descripciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeleccionarCredito.setAdapter(adapter);

        if (descripciones.size() <= 1) {
            Toast.makeText(this, R.string.credito_cancelar_no_creditos_cancelables, Toast.LENGTH_LONG).show();
            spinnerSeleccionarCredito.setEnabled(false);
            ocultarDetallesYDeshabilitarControles();
        } else {
            spinnerSeleccionarCredito.setEnabled(true);
        }
    }
    // ----- FIN MÉTODO CORREGIDO -----

    private void poblarCamposConDatosDelCredito(Credito credito) {
        if (credito == null) {
            ocultarDetallesYDeshabilitarControles();
            return;
        }
        Log.d(TAG,"Mostrando detalles para cancelar Crédito ID: " + credito.getIdCredito());

        tvIdCredito.setText(String.valueOf(credito.getIdCredito()));
        tvIdFactura.setText(String.valueOf(credito.getIdFactura()));
        tvMontoAutorizado.setText(String.format(Locale.US, "$%.2f", credito.getMontoAutorizadoCredito()));
        tvMontoPagado.setText(String.format(Locale.US, "$%.2f", credito.getMontoPagado()));
        tvSaldoPendiente.setText(String.format(Locale.US, "$%.2f", credito.getSaldoPendiente()));
        tvFechaLimite.setText(credito.getFechaLimitePago());
        tvEstadoCredito.setText(credito.getEstadoCredito());
    }

    private void ocultarDetallesYDeshabilitarControles(){
        if (scrollViewDetalles != null) scrollViewDetalles.setVisibility(View.GONE);
        if (btnConfirmarCancelacion != null) btnConfirmarCancelacion.setEnabled(false);
        creditoSeleccionado = null;
        Log.d(TAG,"Detalles de cancelación de crédito ocultados y botón deshabilitado.");
    }

    private void mostrarDialogoDeConfirmacion() {
        if (creditoSeleccionado == null) {
            Toast.makeText(this, R.string.credito_cancelar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.credito_cancelar_title)
                .setMessage(String.format(getString(R.string.credito_cancelar_confirmacion_mensaje), creditoSeleccionado.getIdCredito()))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    procederConCancelacion();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void procederConCancelacion() {
        if (creditoSeleccionado == null) { // Doble chequeo
            Toast.makeText(this, R.string.credito_cancelar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "Confirmada cancelación para Crédito ID: " + creditoSeleccionado.getIdCredito());

        boolean exito = creditoViewModel.cancelarCredito(creditoSeleccionado.getIdCredito());

        if (exito) {
            Toast.makeText(this,
                    String.format(getString(R.string.credito_cancelar_toast_exito), creditoSeleccionado.getIdCredito()),
                    Toast.LENGTH_SHORT).show();

            ocultarDetallesYDeshabilitarControles();
            if(spinnerSeleccionarCredito.getAdapter() != null && spinnerSeleccionarCredito.getAdapter().getCount() > 0){
                spinnerSeleccionarCredito.setSelection(0);
            }
        } else {
            Toast.makeText(this, R.string.credito_cancelar_toast_error, Toast.LENGTH_SHORT).show();
            creditoViewModel.cargarTodosLosCreditosYFiltrarParaUi();
        }
    }
}