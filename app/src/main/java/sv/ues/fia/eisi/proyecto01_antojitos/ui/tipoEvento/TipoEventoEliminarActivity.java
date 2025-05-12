package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class TipoEventoEliminarActivity extends AppCompatActivity {

    private static final String TAG = "TipoEventoEliminarAct";

    private TipoEventoViewModel tipoEventoViewModel;
    private Spinner spinnerTipoEventoParaDesactivar;
    private LinearLayout layoutDetalles;
    private TextView tvNombre, tvDescripcion, tvMontos, tvEstadoActual;
    private Button btnConfirmarDesactivacion;

    private ArrayAdapter<String> spinnerAdapter;
    private List<TipoEvento> listaTiposEventoSpinner = new ArrayList<>();
    private TipoEvento tipoEventoSeleccionadoParaDesactivar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_evento_eliminar);

        setTitle(getString(R.string.tipo_evento_desactivar_title)); // Cambiado a "Desactivar"

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        spinnerTipoEventoParaDesactivar = findViewById(R.id.spinnerTipoEventoParaDesactivar);
        layoutDetalles = findViewById(R.id.layoutDetallesTipoEventoDesactivar);
        tvNombre = findViewById(R.id.tvNombreTipoEventoDesactivar);
        tvDescripcion = findViewById(R.id.tvDescripcionTipoEventoDesactivar);
        tvMontos = findViewById(R.id.tvMontosTipoEventoDesactivar);
        tvEstadoActual = findViewById(R.id.tvEstadoActualTipoEventoDesactivar);
        btnConfirmarDesactivacion = findViewById(R.id.btnConfirmarDesactivacion);

        List<String> spinnerNombresInicial = new ArrayList<>();
        spinnerNombresInicial.add(getString(R.string.tipo_evento_desactivar_placeholder_seleccione));
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerNombresInicial);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoEventoParaDesactivar.setAdapter(spinnerAdapter);

        tipoEventoViewModel = new ViewModelProvider(this).get(TipoEventoViewModel.class);

        setupListeners();
        setupObservers();

        // Cargar tipos de evento (preferiblemente solo activos para desactivar)
        tipoEventoViewModel.cargarTiposEventoActivos();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupListeners() {
        spinnerTipoEventoParaDesactivar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position <= listaTiposEventoSpinner.size()) {
                    tipoEventoSeleccionadoParaDesactivar = listaTiposEventoSpinner.get(position - 1);
                    poblarDetalles(tipoEventoSeleccionadoParaDesactivar);
                    layoutDetalles.setVisibility(View.VISIBLE);
                    // Habilitar botón solo si el tipo de evento está actualmente activo
                    btnConfirmarDesactivacion.setEnabled(tipoEventoSeleccionadoParaDesactivar.getActivoTipoEvento() == 1);
                } else {
                    tipoEventoSeleccionadoParaDesactivar = null;
                    layoutDetalles.setVisibility(View.GONE);
                    btnConfirmarDesactivacion.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tipoEventoSeleccionadoParaDesactivar = null;
                layoutDetalles.setVisibility(View.GONE);
                btnConfirmarDesactivacion.setEnabled(false);
            }
        });

        btnConfirmarDesactivacion.setOnClickListener(v -> confirmarYDesactivar());
    }

    private void setupObservers() {
        // Observador para poblar el spinner (solo con activos, idealmente)
        tipoEventoViewModel.getListaTiposEventoActivos().observe(this, tiposEvento -> {
            if (tiposEvento != null) {
                listaTiposEventoSpinner = tiposEvento;
                actualizarSpinnerPoblacion(tiposEvento);
            }
            // Si la lista de activos está vacía, podría ser útil informar al usuario
            if (tiposEvento == null || tiposEvento.isEmpty()) {
                Toast.makeText(this, "No hay tipos de evento activos para desactivar.", Toast.LENGTH_SHORT).show();
                // Limpiar spinner por si acaso
                spinnerAdapter.clear();
                spinnerAdapter.add(getString(R.string.tipo_evento_desactivar_placeholder_seleccione));
                spinnerAdapter.notifyDataSetChanged();
            }
        });

        tipoEventoViewModel.getOperacionExitosa().observe(this, exitosa -> {
            if (exitosa != null && exitosa) {
                // Mostrar mensaje de éxito y finalizar
                String nombreDesactivado = tipoEventoSeleccionadoParaDesactivar != null ?
                        tipoEventoSeleccionadoParaDesactivar.getNombreTipoEvento() : "";
                // Usamos StringBuilder para construir el mensaje
                StringBuilder sbExito = new StringBuilder();
                sbExito.append(getString(R.string.tipo_evento_desactivar_toast_exito, nombreDesactivado));
                Toast.makeText(this, sbExito.toString(), Toast.LENGTH_LONG).show();

                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        tipoEventoViewModel.getMensajeError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarSpinnerPoblacion(List<TipoEvento> tiposEvento) {
        spinnerAdapter.clear();
        spinnerAdapter.add(getString(R.string.tipo_evento_desactivar_placeholder_seleccione));
        List<String> nombres = new ArrayList<>();
        for (TipoEvento te : tiposEvento) {
            // Usamos StringBuilder para construir el string para el spinner
            StringBuilder sbSpinnerItem = new StringBuilder();
            sbSpinnerItem.append(te.getIdTipoEvento());
            sbSpinnerItem.append(" - ");
            sbSpinnerItem.append(te.getNombreTipoEvento());
            nombres.add(sbSpinnerItem.toString());
        }
        spinnerAdapter.addAll(nombres);
        spinnerAdapter.notifyDataSetChanged();
        layoutDetalles.setVisibility(View.GONE);
        btnConfirmarDesactivacion.setEnabled(false);
    }

    private void poblarDetalles(TipoEvento tipoEvento) {
        if (tipoEvento == null) {
            layoutDetalles.setVisibility(View.GONE);
            return;
        }
        tvNombre.setText(tipoEvento.getNombreTipoEvento());
        tvDescripcion.setText(tipoEvento.getDescripcionTipoEvento() == null ? "" : tipoEvento.getDescripcionTipoEvento());

        // Usamos StringBuilder para construir los montos
        StringBuilder sbMontos = new StringBuilder();
        sbMontos.append("$").append(tipoEvento.getMontoMinimo());
        sbMontos.append(" - $").append(tipoEvento.getMontoMaximo());
        tvMontos.setText(sbMontos.toString());

        if (tipoEvento.getActivoTipoEvento() == 1) {
            tvEstadoActual.setText(R.string.tipo_evento_estado_actual_activo_val);
            tvEstadoActual.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvEstadoActual.setText(R.string.tipo_evento_estado_actual_inactivo_val);
            tvEstadoActual.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void confirmarYDesactivar() {
        if (tipoEventoSeleccionadoParaDesactivar == null) {
            Toast.makeText(this, R.string.tipo_evento_desactivar_toast_seleccione_primero, Toast.LENGTH_SHORT).show();
            return;
        }

        if (tipoEventoSeleccionadoParaDesactivar.getActivoTipoEvento() == 0) {
            Toast.makeText(this, R.string.tipo_evento_desactivar_toast_ya_inactivo, Toast.LENGTH_SHORT).show();
            return;
        }

        // Usamos StringBuilder para construir el mensaje del diálogo
        StringBuilder sbDialogMessage = new StringBuilder();
        sbDialogMessage.append(getString(R.string.tipo_evento_confirm_desactivar_dialog_message,
                tipoEventoSeleccionadoParaDesactivar.getNombreTipoEvento()));

        new AlertDialog.Builder(this)
                .setTitle(R.string.tipo_evento_confirm_desactivar_dialog_title)
                .setMessage(sbDialogMessage.toString())
                .setPositiveButton(R.string.dialog_btn_confirmar, (dialog, which) -> {
                    Log.d(TAG, "Confirmada desactivación para TipoEvento ID: " + tipoEventoSeleccionadoParaDesactivar.getIdTipoEvento());
                    tipoEventoViewModel.desactivarTipoEvento(tipoEventoSeleccionadoParaDesactivar.getIdTipoEvento());
                })
                .setNegativeButton(R.string.dialog_btn_cancelar, null)
                .setIcon(android.R.drawable.ic_dialog_alert) // Icono de alerta
                .show();
    }
}