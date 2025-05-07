package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class FacturaEditarActivity extends AppCompatActivity {

    private static final String TAG = "FacturaEditarActivity";

    private FacturaViewModel facturaViewModel;

    private Spinner spinnerSeleccionarFactura;
    private LinearLayout layoutCamposEditables;
    private TextView tvEditarIdFactura;
    private TextView tvEditarIdPedido;
    private EditText editEditarFechaEmision;
    private EditText editEditarMontoTotal;
    private Spinner spinnerEditarTipoPago;
    private CheckBox checkEditarPagado;
    private Button btnActualizarFactura;

    private List<String> tiposPago = Arrays.asList("Efectivo", "Tarjeta", "Bitcoin", "Transferencia", "Otro");
    private Calendar calendario = Calendar.getInstance();
    private List<Factura> listaDeTodasLasFacturas = new ArrayList<>(); // Para mapear selección del spinner a objeto Factura
    private Factura facturaSeleccionadaParaEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura_editar);

        facturaViewModel = new ViewModelProvider(this).get(FacturaViewModel.class);

        spinnerSeleccionarFactura = findViewById(R.id.spinnerSeleccionarFactura);
        layoutCamposEditables = findViewById(R.id.layoutCamposEditables);
        tvEditarIdFactura = findViewById(R.id.tvEditarIdFactura);
        tvEditarIdPedido = findViewById(R.id.tvEditarIdPedido);
        editEditarFechaEmision = findViewById(R.id.editEditarFechaEmision);
        editEditarMontoTotal = findViewById(R.id.editEditarMontoTotal);
        spinnerEditarTipoPago = findViewById(R.id.spinnerEditarTipoPago);
        checkEditarPagado = findViewById(R.id.checkEditarPagado);
        btnActualizarFactura = findViewById(R.id.btnActualizarFactura);

        cargarSpinnerTipoPago();
        editEditarFechaEmision.setOnClickListener(v -> mostrarDatePickerDialog());
        btnActualizarFactura.setOnClickListener(v -> intentarActualizarFactura());

        // Observador para la lista de todas las facturas
        facturaViewModel.getListaFacturas().observe(this, facturas -> {
            listaDeTodasLasFacturas = facturas;
            cargarSpinnerFacturas();
        });

        // Cargar todas las facturas para el spinner principal
        facturaViewModel.cargarTodasLasFacturas();

        spinnerSeleccionarFactura.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Posición 0 es el placeholder "Seleccione..."
                    // Restar 1 a la posición para coincidir con el índice de listaDeTodasLasFacturas
                    facturaSeleccionadaParaEditar = listaDeTodasLasFacturas.get(position - 1);
                    poblarCampos(facturaSeleccionadaParaEditar);
                    layoutCamposEditables.setVisibility(View.VISIBLE);
                    btnActualizarFactura.setEnabled(true);
                } else {
                    facturaSeleccionadaParaEditar = null;
                    layoutCamposEditables.setVisibility(View.GONE);
                    btnActualizarFactura.setEnabled(false);
                    limpiarCamposEditables();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                facturaSeleccionadaParaEditar = null;
                layoutCamposEditables.setVisibility(View.GONE);
                btnActualizarFactura.setEnabled(false);
                limpiarCamposEditables();
            }
        });

        // Inicialmente los campos están ocultos y el botón deshabilitado
        layoutCamposEditables.setVisibility(View.GONE);
        btnActualizarFactura.setEnabled(false);
    }

    private void cargarSpinnerFacturas() {
        List<String> descripcionesFacturas = new ArrayList<>();
        descripcionesFacturas.add("Seleccione una factura para editar..."); // Placeholder

        if (listaDeTodasLasFacturas != null) {
            for (Factura f : listaDeTodasLasFacturas) {
                descripcionesFacturas.add("Factura #" + f.getIdFactura() + " (Pedido #" + f.getIdPedido() + ")");
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, descripcionesFacturas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeleccionarFactura.setAdapter(adapter);

        if (descripcionesFacturas.size() <= 1) {
            Toast.makeText(this, "No hay facturas existentes para editar.", Toast.LENGTH_LONG).show();
        }
    }


    private void cargarSpinnerTipoPago() {
        List<String> opcionesConPlaceholder = new ArrayList<>();
        opcionesConPlaceholder.add("Seleccione Tipo de Pago...");
        opcionesConPlaceholder.addAll(tiposPago);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opcionesConPlaceholder);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditarTipoPago.setAdapter(adapter);
    }

    private void poblarCampos(Factura factura) {
        if (factura == null) {
            Log.e(TAG, "poblarCampos: la factura es null");
            limpiarCamposEditables();
            layoutCamposEditables.setVisibility(View.GONE);
            btnActualizarFactura.setEnabled(false);
            return;
        }
        Log.d(TAG, "Poblando campos para factura ID: " + factura.getIdFactura());
        tvEditarIdFactura.setText(String.valueOf(factura.getIdFactura()));
        tvEditarIdPedido.setText(String.valueOf(factura.getIdPedido()));
        editEditarFechaEmision.setText(factura.getFechaEmision());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date fecha = sdf.parse(factura.getFechaEmision());
            if (fecha != null) calendario.setTime(fecha);
        } catch (ParseException e) {
            Log.e(TAG, "Error al parsear fecha para el calendario: " + factura.getFechaEmision(), e);
        }

        editEditarMontoTotal.setText(String.format(Locale.US, "%.2f", factura.getMontoTotal()));
        checkEditarPagado.setChecked(factura.getPagado() == 1);

        int spinnerPosition = 0;
        String tipoPagoFactura = factura.getTipoPago();
        if (tipoPagoFactura != null) {
            for (int i = 0; i < tiposPago.size(); i++) {
                if (tiposPago.get(i).equalsIgnoreCase(tipoPagoFactura)) {
                    spinnerPosition = i + 1; // +1 por el placeholder
                    break;
                }
            }
        }
        spinnerEditarTipoPago.setSelection(spinnerPosition);
    }

    private void limpiarCamposEditables() {
        tvEditarIdFactura.setText("");
        tvEditarIdPedido.setText("");
        editEditarFechaEmision.setText("");
        editEditarMontoTotal.setText("");
        spinnerEditarTipoPago.setSelection(0); // Seleccionar placeholder
        checkEditarPagado.setChecked(false);
    }


    private void mostrarDatePickerDialog() {
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
        String formato = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
        editEditarFechaEmision.setText(sdf.format(calendario.getTime()));
    }

    private void intentarActualizarFactura() {
        if (facturaSeleccionadaParaEditar == null) {
            Toast.makeText(this, "No se ha seleccionado ninguna factura para actualizar.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaEmision = editEditarFechaEmision.getText().toString().trim();
        if (fechaEmision.isEmpty()) {
            Toast.makeText(this, "La fecha de emisión no puede estar vacía.", Toast.LENGTH_SHORT).show();
            return;
        }

        String montoStr = editEditarMontoTotal.getText().toString().trim();
        double montoTotal;
        if (montoStr.isEmpty()) {
            Toast.makeText(this, "El monto total no puede estar vacío.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            montoTotal = Double.parseDouble(montoStr);
            if (montoTotal <= 0) {
                Toast.makeText(this, "El monto total debe ser mayor a cero.", Toast.LENGTH_SHORT).show();
                // Considerar si el trigger de BD maneja esto y si se debe retornar aquí
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Formato de monto total inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        int tipoPagoPos = spinnerEditarTipoPago.getSelectedItemPosition();
        if (tipoPagoPos <= 0) {
            Toast.makeText(this, "Seleccione un tipo de pago.", Toast.LENGTH_SHORT).show();
            return;
        }
        String tipoPagoSeleccionado = tiposPago.get(tipoPagoPos - 1);

        if (facturaSeleccionadaParaEditar.getPagado() == 1 && !tipoPagoSeleccionado.equals(facturaSeleccionadaParaEditar.getTipoPago())) {
            // Lógica para advertir o prevenir si el trigger de BD está activo y podría fallar
            Log.w(TAG, "Intentando cambiar tipo de pago en factura ya pagada. El trigger de BD podría impedirlo.");
        }

        int pagado = checkEditarPagado.isChecked() ? 1 : 0;

        Factura facturaActualizada = new Factura();
        facturaActualizada.setIdFactura(facturaSeleccionadaParaEditar.getIdFactura());
        facturaActualizada.setIdPedido(facturaSeleccionadaParaEditar.getIdPedido()); // ID Pedido NO se edita
        facturaActualizada.setFechaEmision(fechaEmision);
        facturaActualizada.setMontoTotal(montoTotal);
        facturaActualizada.setTipoPago(tipoPagoSeleccionado);
        facturaActualizada.setPagado(pagado);

        boolean exito = facturaViewModel.actualizarFactura(facturaActualizada);

        if (exito) {
            Toast.makeText(this, "Factura ID: " + facturaActualizada.getIdFactura() + " actualizada exitosamente.", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            // Refrescar la lista en el spinner o recargar la actividad
            // Para simplificar, solo finalizamos. La lista se recargará si el usuario vuelve a entrar.
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar la factura. Verifique los datos o los logs.", Toast.LENGTH_LONG).show();
        }
    }
}