package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento;

import android.app.Activity;
// No se necesita Intent para pasar ID ahora
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView; // Si necesitas mostrar el ID
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class TipoEventoEditarActivity extends AppCompatActivity {

    private static final String TAG = "TipoEventoEditarAct";

    private TipoEventoViewModel tipoEventoViewModel;
    private Spinner spinnerTipoEventoParaEditar;
    private TextInputEditText etNombre, etDescripcion, etMontoMinimo, etMontoMaximo;
    private TextInputLayout tilNombre, tilDescripcion, tilMontoMinimo, tilMontoMaximo; // Para habilitar/deshabilitar
    private MaterialCheckBox cbActivo;
    private Button btnActualizar;
    // private TextView tvIdTipoEventoEditarHidden; // Opcional si necesitas el ID visible

    private ArrayAdapter<String> spinnerAdapter;
    private List<TipoEvento> listaTiposEventoSpinner = new ArrayList<>();
    private TipoEvento tipoEventoSeleccionadoParaEditar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_evento_editar);

        setTitle(getString(R.string.tipo_evento_editar_title));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        spinnerTipoEventoParaEditar = findViewById(R.id.spinnerTipoEventoParaEditar);
        // tvIdTipoEventoEditarHidden = findViewById(R.id.textViewIdTipoEventoEditarHidden); // Si lo usas
        etNombre = findViewById(R.id.editTextNombreTipoEventoEditar);
        etDescripcion = findViewById(R.id.editTextDescripcionTipoEventoEditar);
        etMontoMinimo = findViewById(R.id.editTextMontoMinimoEditar);
        etMontoMaximo = findViewById(R.id.editTextMontoMaximoEditar);
        cbActivo = findViewById(R.id.checkboxActivoTipoEventoEditar);
        btnActualizar = findViewById(R.id.btnActualizarTipoEvento);

        tilNombre = findViewById(R.id.tilNombreTipoEventoEditar);
        tilDescripcion = findViewById(R.id.tilDescripcionTipoEventoEditar);
        tilMontoMinimo = findViewById(R.id.tilMontoMinimoEditar);
        tilMontoMaximo = findViewById(R.id.tilMontoMaximoEditar);


        List<String> spinnerNombresInicial = new ArrayList<>();
        spinnerNombresInicial.add(getString(R.string.tipo_evento_editar_placeholder_seleccione));
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerNombresInicial);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoEventoParaEditar.setAdapter(spinnerAdapter);

        tipoEventoViewModel = new ViewModelProvider(this).get(TipoEventoViewModel.class);

        setCamposEditables(false); // Deshabilitar campos hasta que se seleccione un item
        setupListeners();
        setupObservers();

        // Cargar todos los tipos de evento (o solo activos) en el Spinner al iniciar
        tipoEventoViewModel.cargarTodosTiposEvento(); // O cargarTiposEventoActivos()
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCamposEditables(boolean habilitar) {
        tilNombre.setEnabled(habilitar);
        tilDescripcion.setEnabled(habilitar);
        tilMontoMinimo.setEnabled(habilitar);
        tilMontoMaximo.setEnabled(habilitar);
        cbActivo.setEnabled(habilitar);
        btnActualizar.setEnabled(habilitar);

        if (!habilitar) {
            etNombre.setText("");
            etDescripcion.setText("");
            etMontoMinimo.setText("");
            etMontoMaximo.setText("");
            cbActivo.setChecked(false);
            // tvIdTipoEventoEditarHidden.setText(""); // Si lo usas
        }
    }

    private void setupListeners() {
        spinnerTipoEventoParaEditar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position <= listaTiposEventoSpinner.size()) {
                    tipoEventoSeleccionadoParaEditar = listaTiposEventoSpinner.get(position - 1);
                    poblarCampos(tipoEventoSeleccionadoParaEditar);
                    setCamposEditables(true);
                    String infoCargada = String.format(Locale.getDefault(),
                            getString(R.string.tipo_evento_editar_info_cargada),
                            tipoEventoSeleccionadoParaEditar.getNombreTipoEvento());
                    // Opcional: Mostrar un Toast o un TextView con esta info
                    // Toast.makeText(TipoEventoEditarActivity.this, infoCargada, Toast.LENGTH_SHORT).show();
                } else {
                    tipoEventoSeleccionadoParaEditar = null;
                    setCamposEditables(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tipoEventoSeleccionadoParaEditar = null;
                setCamposEditables(false);
            }
        });

        btnActualizar.setOnClickListener(v -> actualizarTipoEvento());
    }

    private void setupObservers() {
        // Observador para poblar el spinner
        tipoEventoViewModel.getListaTodosTiposEvento().observe(this, tiposEvento -> {
            // Podrías también observar getListaTiposEventoActivos() si prefieres solo editar activos
            if (tiposEvento != null) {
                listaTiposEventoSpinner = tiposEvento;
                actualizarSpinnerPoblacion(tiposEvento);
            }
        });

        tipoEventoViewModel.getOperacionExitosa().observe(this, exitosa -> {
            if (exitosa != null && exitosa) {
                Toast.makeText(this, R.string.tipo_evento_editar_toast_exito, Toast.LENGTH_LONG).show();
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
        spinnerAdapter.add(getString(R.string.tipo_evento_editar_placeholder_seleccione));
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
        setCamposEditables(false); // Deshabilitar campos hasta nueva selección
    }


    private void poblarCampos(TipoEvento tipoEvento) {
        if (tipoEvento == null) {
            setCamposEditables(false);
            return;
        }
        // if (tvIdTipoEventoEditarHidden != null) tvIdTipoEventoEditarHidden.setText(String.valueOf(tipoEvento.getIdTipoEvento()));
        etNombre.setText(tipoEvento.getNombreTipoEvento());
        etDescripcion.setText(tipoEvento.getDescripcionTipoEvento());
        etMontoMinimo.setText(String.valueOf(tipoEvento.getMontoMinimo()));
        etMontoMaximo.setText(String.valueOf(tipoEvento.getMontoMaximo()));
        cbActivo.setChecked(tipoEvento.getActivoTipoEvento() == 1);
    }

    // Reutilizar la clase DoubleHolder y el método validarCampos de TipoEventoCrearActivity
    private static class DoubleHolder { double value; }

    private boolean validarCampos(String nombre, String strMontoMin, String strMontoMax,
                                  DoubleHolder montoMinHolder, DoubleHolder montoMaxHolder) {
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError(getString(R.string.tipo_evento_crear_toast_nombre_requerido));
            etNombre.requestFocus();
            Toast.makeText(this, R.string.tipo_evento_crear_toast_nombre_requerido, Toast.LENGTH_SHORT).show();
            return false;
        }
        etNombre.setError(null);

        if (TextUtils.isEmpty(strMontoMin)) {
            etMontoMinimo.setError(getString(R.string.tipo_evento_crear_toast_monto_min_requerido));
            etMontoMinimo.requestFocus();
            Toast.makeText(this, R.string.tipo_evento_crear_toast_monto_min_requerido, Toast.LENGTH_SHORT).show();
            return false;
        }
        etMontoMinimo.setError(null);

        if (TextUtils.isEmpty(strMontoMax)) {
            etMontoMaximo.setError(getString(R.string.tipo_evento_crear_toast_monto_max_requerido));
            etMontoMaximo.requestFocus();
            Toast.makeText(this, R.string.tipo_evento_crear_toast_monto_max_requerido, Toast.LENGTH_SHORT).show();
            return false;
        }
        etMontoMaximo.setError(null);

        try {
            montoMinHolder.value = Double.parseDouble(strMontoMin);
            if (montoMinHolder.value <= 0) {
                etMontoMinimo.setError(getString(R.string.tipo_evento_crear_toast_monto_invalido));
                etMontoMinimo.requestFocus();
                Toast.makeText(this, R.string.tipo_evento_crear_toast_monto_invalido, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            etMontoMinimo.setError(getString(R.string.tipo_evento_crear_toast_monto_invalido));
            etMontoMinimo.requestFocus();
            Toast.makeText(this, R.string.tipo_evento_crear_toast_monto_invalido, Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            montoMaxHolder.value = Double.parseDouble(strMontoMax);
            if (montoMaxHolder.value <= 0) {
                etMontoMaximo.setError(getString(R.string.tipo_evento_crear_toast_monto_invalido));
                etMontoMaximo.requestFocus();
                Toast.makeText(this, R.string.tipo_evento_crear_toast_monto_invalido, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            etMontoMaximo.setError(getString(R.string.tipo_evento_crear_toast_monto_invalido));
            etMontoMaximo.requestFocus();
            Toast.makeText(this, R.string.tipo_evento_crear_toast_monto_invalido, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (montoMaxHolder.value < montoMinHolder.value) {
            etMontoMaximo.setError(getString(R.string.tipo_evento_crear_toast_monto_max_menor_min));
            etMontoMaximo.requestFocus();
            Toast.makeText(this, R.string.tipo_evento_crear_toast_monto_max_menor_min, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void actualizarTipoEvento() {
        if (tipoEventoSeleccionadoParaEditar == null) {
            Toast.makeText(this, R.string.tipo_evento_editar_toast_seleccione_primero, Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String strMontoMin = etMontoMinimo.getText().toString().trim();
        String strMontoMax = etMontoMaximo.getText().toString().trim();
        boolean activo = cbActivo.isChecked();

        DoubleHolder montoMinHolder = new DoubleHolder();
        DoubleHolder montoMaxHolder = new DoubleHolder();

        if (!validarCampos(nombre, strMontoMin, strMontoMax, montoMinHolder, montoMaxHolder)) {
            return;
        }

        TipoEvento tipoEventoActualizado = new TipoEvento();
        tipoEventoActualizado.setIdTipoEvento(tipoEventoSeleccionadoParaEditar.getIdTipoEvento()); // ID original
        tipoEventoActualizado.setNombreTipoEvento(nombre);
        tipoEventoActualizado.setDescripcionTipoEvento(descripcion.isEmpty() ? null : descripcion);
        tipoEventoActualizado.setMontoMinimo(montoMinHolder.value);
        tipoEventoActualizado.setMontoMaximo(montoMaxHolder.value);
        tipoEventoActualizado.setActivoTipoEvento(activo ? 1 : 0);

        Log.d(TAG, "Intentando actualizar TipoEvento ID: " + tipoEventoActualizado.getIdTipoEvento());
        tipoEventoViewModel.actualizarTipoEvento(tipoEventoActualizado);
    }
}