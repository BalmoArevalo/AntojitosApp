package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito; // Ajusta el paquete

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
// Importar ViewModel y POJO
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.Credito;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.CreditoViewModel;
// Reutilizar strings genéricos
import static sv.ues.fia.eisi.proyecto01_antojitos.R.string.factura_consultar_s_valor_no_definido;
import static sv.ues.fia.eisi.proyecto01_antojitos.R.string.placeholder_seleccione;

public class CreditoEliminarActivity extends AppCompatActivity {

    private static final String TAG = "CreditoEliminarAct"; // Usar nombre de la clase
    private static final String ESTADO_CREDITO_ACTIVO = "Activo"; // Estado elegible para cancelar

    // UI Components
    private Spinner spinnerSeleccionarCredito;
    private ScrollView scrollViewDetalles;
    // TextViews para mostrar datos
    private TextView tvIdCredito, tvIdFactura, tvMontoAutorizado, tvMontoPagado,
            tvSaldoPendiente, tvFechaLimite, tvEstadoCredito;
    private Button btnConfirmarCancelacion;

    // Data
    private CreditoViewModel creditoViewModel;
    private List<Credito> listaCreditosActivos = new ArrayList<>();
    private Credito creditoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credito_eliminar); // Usar layout correspondiente
        setTitle(getString(R.string.credito_cancelar_title)); // Título indica acción

        creditoViewModel = new ViewModelProvider(this).get(CreditoViewModel.class);

        // Inicializar Vistas
        spinnerSeleccionarCredito = findViewById(R.id.spinnerSeleccionarCreditoCancelar);
        scrollViewDetalles = findViewById(R.id.scrollViewCreditoCancelar);
        tvIdCredito = findViewById(R.id.tvCancelarCreditoIdCredito);
        tvIdFactura = findViewById(R.id.tvCancelarCreditoIdFactura);
        tvMontoAutorizado = findViewById(R.id.tvCancelarCreditoMontoAutorizado);
        tvMontoPagado = findViewById(R.id.tvCancelarCreditoMontoPagado);
        tvSaldoPendiente = findViewById(R.id.tvCancelarCreditoSaldoPendiente);
        tvFechaLimite = findViewById(R.id.tvCancelarCreditoFechaLimite);
        tvEstadoCredito = findViewById(R.id.tvCancelarCreditoEstado);
        btnConfirmarCancelacion = findViewById(R.id.btnConfirmarCancelacionCredito);

        // Estado inicial
        ocultarDetallesYDeshabilitar();
        configurarListenerSpinner();

        // Observador para la lista de créditos ACTIVOS (para el spinner)
        creditoViewModel.getListaCreditosActivos().observe(this, creditosActivos -> {
            Log.d(TAG, "Observer: Lista créditos activos actualizada. Total: " + (creditosActivos != null ? creditosActivos.size() : "null"));
            if (creditosActivos != null) {
                listaCreditosActivos = creditosActivos;
                cargarSpinnerCreditosActivos();
            }
        });

        // Cargar créditos activos al inicio
        Log.d(TAG,"Solicitando carga inicial de créditos activos...");
        creditoViewModel.cargarTodosLosCreditos(); // ViewModel filtra internamente

        btnConfirmarCancelacion.setOnClickListener(v -> intentarCancelarCredito());
    }

    private void configurarListenerSpinner() {
        spinnerSeleccionarCredito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && (position - 1) < listaCreditosActivos.size()) {
                    creditoSeleccionado = listaCreditosActivos.get(position - 1);
                    mostrarDetalles(creditoSeleccionado);
                    scrollViewDetalles.setVisibility(View.VISIBLE);
                    // Habilitar botón solo si el crédito seleccionado está realmente activo
                    btnConfirmarCancelacion.setEnabled(ESTADO_CREDITO_ACTIVO.equalsIgnoreCase(creditoSeleccionado.getEstadoCredito()));
                    if (!ESTADO_CREDITO_ACTIVO.equalsIgnoreCase(creditoSeleccionado.getEstadoCredito())) {
                        Toast.makeText(CreditoEliminarActivity.this, R.string.credito_cancelar_toast_no_cancelable, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ocultarDetallesYDeshabilitar();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                ocultarDetallesYDeshabilitar();
            }
        });
    }

    private void cargarSpinnerCreditosActivos() {
        List<String> descripciones = new ArrayList<>();
        descripciones.add(getString(placeholder_seleccione)); // Reutilizar string genérico

        for (Credito c : listaCreditosActivos) {
            descripciones.add(String.format(Locale.getDefault(), "Crédito #%d (Fact: #%d) Saldo: $%.2f",
                    c.getIdCredito(), c.getIdFactura(), c.getSaldoPendiente()));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, descripciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeleccionarCredito.setAdapter(adapter);

        if (descripciones.size() <= 1) {
            Toast.makeText(this, R.string.credito_cancelar_no_creditos_activos, Toast.LENGTH_LONG).show();
            spinnerSeleccionarCredito.setEnabled(false);
        } else {
            spinnerSeleccionarCredito.setEnabled(true);
        }
        ocultarDetallesYDeshabilitar(); // Resetear al recargar
    }

    private void mostrarDetalles(Credito c) {
        if (c == null) return;
        Log.d(TAG,"Mostrando detalles para cancelar Crédito ID: " + c.getIdCredito());
        // Poblar los TextViews usando los strings correspondientes
        tvIdCredito.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.credito_cancelar_label_id_credito), c.getIdCredito()));
        tvIdFactura.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.credito_cancelar_label_id_factura), c.getIdFactura()));
        tvMontoAutorizado.setText(String.format(Locale.US, "%s $%.2f", getString(R.string.credito_cancelar_label_monto_autorizado), c.getMontoAutorizadoCredito()));
        tvMontoPagado.setText(String.format(Locale.US, "%s $%.2f", getString(R.string.credito_cancelar_label_monto_pagado), c.getMontoPagado()));
        tvSaldoPendiente.setText(String.format(Locale.US, "%s $%.2f", getString(R.string.credito_cancelar_label_saldo_pendiente), c.getSaldoPendiente()));
        tvFechaLimite.setText(String.format("%s %s", getString(R.string.credito_cancelar_label_fecha_limite_editable), c.getFechaLimitePago())); // Reusar label editable
        tvEstadoCredito.setText(String.format("%s %s", getString(R.string.credito_cancelar_label_estado_credito), c.getEstadoCredito()));
    }

    private void ocultarDetallesYDeshabilitar(){
        if (scrollViewDetalles != null) scrollViewDetalles.setVisibility(View.GONE);
        if (btnConfirmarCancelacion != null) btnConfirmarCancelacion.setEnabled(false);
        creditoSeleccionado = null;
        Log.d(TAG,"Detalles de cancelación de crédito ocultados y botón deshabilitado.");
    }

    // Lógica del botón "Confirmar Cancelación"
    private void intentarCancelarCredito() {
        if (creditoSeleccionado == null) {
            Toast.makeText(this, R.string.credito_cancelar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que esté activo (aunque el spinner ya filtra, es buena práctica)
        if (!ESTADO_CREDITO_ACTIVO.equalsIgnoreCase(creditoSeleccionado.getEstadoCredito())) {
            Toast.makeText(this, R.string.credito_cancelar_toast_no_cancelable, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "Solicitando cancelación para Crédito ID: " + creditoSeleccionado.getIdCredito());

        // Llamar al método del ViewModel que ahora también actualiza Factura
        boolean exito = creditoViewModel.cancelarCredito(creditoSeleccionado.getIdCredito());

        if (exito) {
            Toast.makeText(this,
                    String.format(getString(R.string.credito_cancelar_toast_exito), creditoSeleccionado.getIdCredito()),
                    Toast.LENGTH_SHORT).show();
            // Refrescar datos y UI
            creditoViewModel.cargarTodosLosCreditos(); // Para actualizar LiveData y el spinner
            ocultarDetallesYDeshabilitar();
            spinnerSeleccionarCredito.setSelection(0); // Volver al placeholder
            // finish(); // Opcional: cerrar
        } else {
            Toast.makeText(this, R.string.credito_cancelar_toast_error, Toast.LENGTH_SHORT).show();
        }
    }
}