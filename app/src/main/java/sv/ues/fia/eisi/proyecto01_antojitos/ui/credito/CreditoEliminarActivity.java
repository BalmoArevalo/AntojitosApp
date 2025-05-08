package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

// Ya no se necesita DialogInterface si no se usa directamente, pero AlertDialog lo usa.
// import android.content.DialogInterface;
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

    private static final String TAG = CreditoEliminarActivity.class.getSimpleName();

    // UI Components
    private Spinner spinnerSeleccionarCredito;
    private ScrollView scrollViewDetalles;
    private TextView tvIdCredito, tvIdFactura, tvMontoAutorizado, tvMontoPagado,
            tvSaldoPendiente, tvFechaLimite, tvEstadoCredito;
    private Button btnConfirmarAccion; // Renombrado para ser más genérico, antes btnConfirmarCancelacion

    // Data
    private CreditoViewModel creditoViewModel;
    private List<Credito> listaCreditosParaSpinner = new ArrayList<>();
    private Credito creditoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credito_eliminar);
        // Considera cambiar R.string.credito_cancelar_title a algo como R.string.credito_eliminar_title
        setTitle(getString(R.string.credito_cancelar_title)); // UI Text: "Eliminar Crédito"

        creditoViewModel = new ViewModelProvider(this).get(CreditoViewModel.class);

        inicializarVistas();
        configurarListeners();
        ocultarDetallesYDeshabilitarControles();

        creditoViewModel.getListaCreditosFiltradosParaUi().observe(this, creditosFiltrados -> {
            Log.d(TAG, "Observer: Lista de créditos para UI actualizada. Total: " +
                    (creditosFiltrados != null ? creditosFiltrados.size() : "null"));
            if (creditosFiltrados != null) {
                listaCreditosParaSpinner = creditosFiltrados;
                // El nombre del método podría cambiar para ser más genérico si la UI lo es
                cargarSpinnerCreditosParaAccion();
            }
        });

        Log.d(TAG,"Solicitando carga inicial de créditos (con filtro para UI)...");
        creditoViewModel.cargarTodosLosCreditosYFiltrarParaUi();

        btnConfirmarAccion.setOnClickListener(v -> mostrarDialogoDeConfirmacion());
    }

    private void inicializarVistas() {
        spinnerSeleccionarCredito = findViewById(R.id.spinnerSeleccionarCreditoCancelar); // ID del layout
        scrollViewDetalles = findViewById(R.id.scrollViewCreditoCancelarDetalles); // ID del layout
        tvIdCredito = findViewById(R.id.tvCancelarCreditoIdCredito); // ID del layout
        tvIdFactura = findViewById(R.id.tvCancelarCreditoIdFactura); // ID del layout
        tvMontoAutorizado = findViewById(R.id.tvCancelarCreditoMontoAutorizado); // ID del layout
        tvMontoPagado = findViewById(R.id.tvCancelarCreditoMontoPagado); // ID del layout
        tvSaldoPendiente = findViewById(R.id.tvCancelarCreditoSaldoPendiente); // ID del layout
        tvFechaLimite = findViewById(R.id.tvCancelarCreditoFechaLimite); // ID del layout
        tvEstadoCredito = findViewById(R.id.tvCancelarCreditoEstadoCredito); // ID del layout
        // El ID del botón en el layout es R.id.btnCreditoCancelarConfirmar
        btnConfirmarAccion = findViewById(R.id.btnCreditoCancelarConfirmar);
        // Podrías cambiar el texto del botón dinámicamente si es necesario
        // btnConfirmarAccion.setText(getString(R.string.boton_eliminar));
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

                    // La lógica para habilitar el botón sigue siendo la misma:
                    // El crédito debe estar en un estado que permita la acción (ej. "Activo")
                    // Y el monto pagado debe ser CERO para permitir la eliminación física.
                    boolean esEstadoValidoParaAccion = getString(R.string.credito_estado_activo) // Asume que "Activo" es el estado elegible
                            .equalsIgnoreCase(creditoSeleccionado.getEstadoCredito());
                    boolean noTienePagos = creditoSeleccionado.getMontoPagado() == 0;

                    boolean esAccionPermitida = esEstadoValidoParaAccion && noTienePagos;
                    btnConfirmarAccion.setEnabled(esAccionPermitida);

                    if (!esAccionPermitida && creditoSeleccionado != null) {
                        // Considera un string más específico si la acción es "eliminar"
                        // ej. R.string.credito_eliminar_toast_no_eliminable
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

    // Podrías renombrar este método a cargarSpinnerCreditosParaAccion o similar
    private void cargarSpinnerCreditosParaAccion() {
        List<String> descripciones = new ArrayList<>();
        descripciones.add(getString(R.string.placeholder_seleccione)); // UI Text: "Seleccione..."

        for (Credito c : listaCreditosParaSpinner) {
            try {
                String saldoFormateado = String.format(Locale.US, "%.2f", c.getSaldoPendiente());
                String descripcion = String.format(Locale.getDefault(),
                        "Crédito #%1$d (Fact: #%2$d) Saldo: $%3$s", // Formato de item en spinner
                        c.getIdCredito(),
                        c.getIdFactura(),
                        saldoFormateado);
                descripciones.add(descripcion);
            } catch (Exception e) {
                Log.e(TAG, "Error formateando descripción para spinner - Crédito ID: " + c.getIdCredito(), e);
                descripciones.add("Error - Crédito #" + c.getIdCredito()); // Fallback
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, descripciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeleccionarCredito.setAdapter(adapter);

        if (descripciones.size() <= 1) { // Solo el placeholder "Seleccione"
            // Considera un string R.string.credito_eliminar_no_creditos_eliminables
            Toast.makeText(this, R.string.credito_cancelar_no_creditos_cancelables, Toast.LENGTH_LONG).show();
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
        Log.d(TAG,"Mostrando detalles para Crédito ID: " + credito.getIdCredito());

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
        if (btnConfirmarAccion != null) btnConfirmarAccion.setEnabled(false);
        creditoSeleccionado = null;
        Log.d(TAG,"Detalles de crédito ocultados y botón de acción deshabilitado.");
    }

    private void mostrarDialogoDeConfirmacion() {
        if (creditoSeleccionado == null) {
            // Considera R.string.credito_eliminar_toast_no_seleccion
            Toast.makeText(this, R.string.credito_cancelar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }

        // El título y mensaje del diálogo deberían reflejar la acción de "Eliminar"
        new AlertDialog.Builder(this)
                // Usa un R.string.credito_eliminar_title para "Eliminar Crédito"
                .setTitle(getString(R.string.credito_cancelar_title))
                // Usa un R.string.credito_eliminar_confirmacion_mensaje para "¿Está seguro de ELIMINAR el crédito #%d?"
                .setMessage(String.format(getString(R.string.credito_cancelar_confirmacion_mensaje), creditoSeleccionado.getIdCredito()))
                .setIcon(android.R.drawable.ic_dialog_alert) // Icono de advertencia estándar
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    procederConAccionPrincipal(); // Renombrado para claridad
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    // Renombrado de procederConCancelacion a procederConAccionPrincipal
    private void procederConAccionPrincipal() {
        if (creditoSeleccionado == null) { // Doble chequeo
            Toast.makeText(this, R.string.credito_cancelar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "Confirmada ELIMINACIÓN FÍSICA para Crédito ID: " + creditoSeleccionado.getIdCredito());

        // *** CAMBIO PRINCIPAL AQUÍ: Llamar a eliminarCredito en lugar de cancelarCredito ***
        boolean exito = creditoViewModel.eliminarCredito(creditoSeleccionado.getIdCredito());

        if (exito) {
            Toast.makeText(this,
                    // Considera R.string.credito_eliminar_toast_exito para "Crédito #%d eliminado exitosamente."
                    String.format(getString(R.string.credito_cancelar_toast_exito), creditoSeleccionado.getIdCredito()),
                    Toast.LENGTH_SHORT).show();

            ocultarDetallesYDeshabilitarControles();
            if(spinnerSeleccionarCredito.getAdapter() != null && spinnerSeleccionarCredito.getAdapter().getCount() > 0){
                spinnerSeleccionarCredito.setSelection(0); // Volver a "Seleccione"
            }
            // La lista en el spinner se actualizará automáticamente porque el ViewModel
            // llama a cargarTodosLosCreditosYFiltrarParaUi(), lo que dispara el observer.
        } else {
            // Considera R.string.credito_eliminar_toast_error
            // El ViewModel ya logueó la causa específica del fallo (ej. tenía pagos, error de BD).
            Toast.makeText(this, R.string.credito_cancelar_toast_error, Toast.LENGTH_SHORT).show();
            // Recargar la lista para asegurar que la UI refleje el estado más actual,
            // especialmente si el fallo no fue una validación previa en la UI.
            creditoViewModel.cargarTodosLosCreditosYFiltrarParaUi();
        }
    }
}