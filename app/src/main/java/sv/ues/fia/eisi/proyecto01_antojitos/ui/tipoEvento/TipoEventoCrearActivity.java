package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale; // Para el String.format del Toast, si se mantiene

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class TipoEventoCrearActivity extends AppCompatActivity {

    private static final String TAG = "TipoEventoCrearAct";

    private TipoEventoViewModel tipoEventoViewModel;
    private TextInputEditText etNombre, etDescripcion, etMontoMinimo, etMontoMaximo;
    private MaterialCheckBox cbActivo;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_evento_crear);

        setTitle(getString(R.string.tipo_evento_crear_title));

        etNombre = findViewById(R.id.editTextNombreTipoEvento);
        etDescripcion = findViewById(R.id.editTextDescripcionTipoEvento);
        etMontoMinimo = findViewById(R.id.editTextMontoMinimo);
        etMontoMaximo = findViewById(R.id.editTextMontoMaximo);
        cbActivo = findViewById(R.id.checkboxActivoTipoEvento);
        btnGuardar = findViewById(R.id.btnGuardarTipoEvento);

        tipoEventoViewModel = new ViewModelProvider(this).get(TipoEventoViewModel.class);

        setupObservers();

        btnGuardar.setOnClickListener(v -> guardarTipoEvento());
    }

    private void setupObservers() {
        tipoEventoViewModel.getOperacionExitosa().observe(this, exitosa -> {
            if (exitosa != null && exitosa) {
                // Para obtener el ID del último insertado, necesitaríamos que el ViewModel lo exponga
                // o que el DAO.insertar devuelva el objeto completo o el ID.
                // Por ahora, un mensaje genérico o podríamos modificar el ViewModel.
                // Asumiendo que el ViewModel NO expone el ID directamente aquí.
                Toast.makeText(this, "Tipo de Evento guardado exitosamente.", Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_OK); // Indicar éxito a la actividad anterior
                finish();
            }
        });

        tipoEventoViewModel.getMensajeError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

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

    private void guardarTipoEvento() {
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

        TipoEvento nuevoTipoEvento = new TipoEvento();
        nuevoTipoEvento.setNombreTipoEvento(nombre);
        nuevoTipoEvento.setDescripcionTipoEvento(descripcion.isEmpty() ? null : descripcion); // Guardar null si está vacío
        nuevoTipoEvento.setMontoMinimo(montoMinHolder.value);
        nuevoTipoEvento.setMontoMaximo(montoMaxHolder.value);
        nuevoTipoEvento.setActivoTipoEvento(activo ? 1 : 0);

        Log.d(TAG, "Intentando guardar TipoEvento: " + nombre);
        tipoEventoViewModel.insertarTipoEvento(nuevoTipoEvento);
    }

    // Clase helper para pasar doubles por referencia desde el método de validación
    private static class DoubleHolder {
        double value;
    }
}