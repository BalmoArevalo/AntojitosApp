package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito; // Asegúrate que el paquete sea el correcto

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider; // Importar aunque usemos DAOs directos, podría necesitarse luego

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout; // Import necesario si se usa el layout interno directamente
import android.widget.ScrollView; // Import necesario
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors; // Si usas API 24+ para filtrar

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
// Importar todos los POJOs y DAOs necesarios
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.Factura;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaViewModel; // Importar aunque no se use activamente aquí
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.Credito;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.CreditoDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.CreditoViewModel; // Importar

public class CreditoConsultarActivity extends AppCompatActivity {

    private static final String TAG = "CreditoConsultarAct";

    // UI Components
    private Spinner spinnerFacturaConCredito;
    private ScrollView scrollView; // *** CORREGIDO: Nombre de variable consistente ***
    // TextViews para Crédito
    private TextView tvIdCredito, tvIdFactura, tvMontoAutorizado, tvMontoPagado,
            tvSaldoPendiente, tvFechaLimite, tvEstadoCredito;

    // Data and Helpers
    private DBHelper dbHelper;
    private FacturaViewModel facturaViewModel; // Para cargar las facturas elegibles
    private CreditoViewModel creditoViewModel; // Para consultar el crédito asociado
    private List<Factura> listaFacturasConCredito = new ArrayList<>();
    private int idFacturaSeleccionada = -1;
    private SQLiteDatabase dbRead; // Declaración verificada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credito_consultar); // Layout que definimos antes
        setTitle(getString(R.string.credito_consultar_title));

        // Inicializar ViewModels y Helper/DB
        facturaViewModel = new ViewModelProvider(this).get(FacturaViewModel.class);
        creditoViewModel = new ViewModelProvider(this).get(CreditoViewModel.class);
        dbHelper = new DBHelper(this);
        dbRead = dbHelper.getReadableDatabase(); // Inicialización verificada

        // Inicializar Vistas
        spinnerFacturaConCredito = findViewById(R.id.spinnerSeleccionarFacturaConCredito);
        // *** CORREGIDO: Usar ID correcto y nombre de variable correcto ***
        scrollView = findViewById(R.id.scrollViewCreditoConsultar);

        // TextViews Crédito
        tvIdCredito = findViewById(R.id.tvConsultaCreditoIdCredito);
        tvIdFactura = findViewById(R.id.tvConsultaCreditoIdFactura);
        tvMontoAutorizado = findViewById(R.id.tvConsultaCreditoMontoAutorizado);
        tvMontoPagado = findViewById(R.id.tvConsultaCreditoMontoPagado);
        tvSaldoPendiente = findViewById(R.id.tvConsultaCreditoSaldoPendiente);
        tvFechaLimite = findViewById(R.id.tvConsultaCreditoFechaLimite);
        tvEstadoCredito = findViewById(R.id.tvConsultaCreditoEstado);

        // Estado inicial
        ocultarDetalles();
        configurarListenerSpinner();

        // Observador para la lista de facturas (para filtrar las de crédito)
        facturaViewModel.getListaFacturas().observe(this, facturas -> {
            Log.d(TAG, "Observer Facturas: Recibidas " + (facturas != null ? facturas.size() : 0) + " facturas.");
            if (facturas != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    listaFacturasConCredito = facturas.stream()
                            .filter(f -> f.getEsCredito() == 1)
                            .collect(Collectors.toList());
                } else {
                    listaFacturasConCredito = new ArrayList<>();
                    for (Factura f : facturas) { if (f.getEsCredito() == 1) { listaFacturasConCredito.add(f); } }
                }
                Log.d(TAG,"Facturas CON crédito filtradas: " + listaFacturasConCredito.size());
                cargarSpinnerFacturasConCredito();
            }
        });

        // Observador para el crédito consultado
        creditoViewModel.getCreditoSeleccionado().observe(this, credito -> {
            if (credito != null && credito.getIdFactura() == idFacturaSeleccionada) {
                Log.d(TAG,"Observer Crédito: Recibido crédito para Factura ID " + idFacturaSeleccionada);
                mostrarDetallesCredito(credito);
            } else if (idFacturaSeleccionada != -1) {
                Log.w(TAG,"Observer Crédito: No se encontró crédito para Factura ID " + idFacturaSeleccionada);
                Toast.makeText(this, R.string.credito_consultar_toast_no_credito, Toast.LENGTH_SHORT).show();
                ocultarDetalles();
            }
            // Si credito es null y idFacturaSeleccionada es -1 (placeholder), no hacer nada.
        });

        // Cargar las facturas al inicio
        Log.d(TAG,"Solicitando carga inicial de todas las facturas...");
        facturaViewModel.cargarTodasLasFacturas();
    }

    private void configurarListenerSpinner() {
        spinnerFacturaConCredito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && (position - 1) < listaFacturasConCredito.size()) {
                    Factura facturaSel = listaFacturasConCredito.get(position - 1);
                    idFacturaSeleccionada = facturaSel.getIdFactura();
                    Log.d(TAG, "Factura con crédito seleccionada: ID=" + idFacturaSeleccionada);
                    // Llamar al ViewModel para que consulte el crédito
                    creditoViewModel.consultarCreditoPorIdFactura(idFacturaSeleccionada);
                } else {
                    idFacturaSeleccionada = -1;
                    ocultarDetalles();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                idFacturaSeleccionada = -1;
                ocultarDetalles();
            }
        });
    }

    private void cargarSpinnerFacturasConCredito() {
        // ... (Código sin cambios) ...
        List<String> descripciones = new ArrayList<>();
        descripciones.add(getString(R.string.placeholder_seleccione));

        for (Factura f : listaFacturasConCredito) {
            descripciones.add(String.format(Locale.getDefault(), "Factura #%d (Pedido #%d)",
                    f.getIdFactura(), f.getIdPedido()));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, descripciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFacturaConCredito.setAdapter(adapter);

        if (descripciones.size() <= 1) {
            Toast.makeText(this, R.string.credito_consultar_no_facturas_con_credito, Toast.LENGTH_LONG).show();
            spinnerFacturaConCredito.setEnabled(false);
        } else {
            spinnerFacturaConCredito.setEnabled(true);
        }
        ocultarDetalles();
    }

    private void mostrarDetallesCredito(Credito c) {
        if (c == null) {
            ocultarDetalles();
            return;
        }
        Log.d(TAG,"Mostrando detalles para Crédito ID: " + c.getIdCredito());

        // Poblar TextViews con datos del objeto Credito 'c'
        tvIdCredito.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.credito_consultar_label_id_credito), c.getIdCredito()));
        tvIdFactura.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.credito_consultar_label_id_factura), c.getIdFactura()));
        tvMontoAutorizado.setText(String.format(Locale.US, "%s $%.2f", getString(R.string.credito_consultar_label_monto_autorizado), c.getMontoAutorizadoCredito()));
        tvMontoPagado.setText(String.format(Locale.US, "%s $%.2f", getString(R.string.credito_consultar_label_monto_pagado), c.getMontoPagado()));
        tvSaldoPendiente.setText(String.format(Locale.US, "%s $%.2f", getString(R.string.credito_consultar_label_saldo_pendiente), c.getSaldoPendiente()));
        tvFechaLimite.setText(String.format("%s %s", getString(R.string.credito_consultar_label_fecha_limite), c.getFechaLimitePago()));
        tvEstadoCredito.setText(String.format("%s %s", getString(R.string.credito_consultar_label_estado_credito), c.getEstadoCredito()));

        scrollView.setVisibility(View.VISIBLE); // *** CORREGIDO: Usar nombre de variable correcto ***
    }

    private void ocultarDetalles(){
        // *** CORREGIDO: Usar nombre de variable correcto ***
        if (scrollView != null) { // Añadir chequeo por si acaso
            scrollView.setVisibility(View.GONE);
        }
        // Limpiar textos
        tvIdCredito.setText(""); tvIdFactura.setText(""); tvMontoAutorizado.setText("");
        tvMontoPagado.setText(""); tvSaldoPendiente.setText(""); tvFechaLimite.setText("");
        tvEstadoCredito.setText("");
        Log.d(TAG,"Detalles de crédito ocultados.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cerrar la conexión de lectura si se mantuvo abierta y no es null
        // La declaración e inicialización de dbRead parecen correctas,
        // si sigue dando error aquí, revisa typos o considera Invalidate Caches.
        if (dbRead != null && dbRead.isOpen()) {
            dbRead.close();
            Log.d(TAG,"Base de datos de lectura cerrada.");
        }
    }
}