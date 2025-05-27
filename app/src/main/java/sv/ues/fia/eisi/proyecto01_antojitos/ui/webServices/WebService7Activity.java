package sv.ues.fia.eisi.proyecto01_antojitos.ui.webServices;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
// import android.view.View; // No se usa explícitamente
import android.widget.Button;
import android.widget.TextView;
// import android.widget.TimePicker; // No se usa explícitamente
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.network.helpers.CategoriaProductoHelper; // Importa la clase Helper

// 1. Implementar la interfaz de Callback
public class WebService7Activity extends AppCompatActivity implements CategoriaProductoHelper.CategoriaProductoCallback {

    private TextInputLayout tilNombreCategoria, tilDescripcionCategoria, tilHoraDisponibleDesde, tilHoraDisponibleHasta;
    private TextInputEditText etNombreCategoria, etDescripcionCategoria, etHoraDisponibleDesde, etHoraDisponibleHasta;
    private TextView tvDisponibleStatus;
    private Button btnGuardarCategoria;

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service7);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("WS7 - Crear Categoría Producto");
        }

        // ... (inicialización de vistas igual que antes)
        tilNombreCategoria = findViewById(R.id.tilNombreCategoria);
        etNombreCategoria = findViewById(R.id.etNombreCategoria);
        tilDescripcionCategoria = findViewById(R.id.tilDescripcionCategoria);
        etDescripcionCategoria = findViewById(R.id.etDescripcionCategoria);
        tilHoraDisponibleDesde = findViewById(R.id.tilHoraDisponibleDesde);
        etHoraDisponibleDesde = findViewById(R.id.etHoraDisponibleDesde);
        tilHoraDisponibleHasta = findViewById(R.id.tilHoraDisponibleHasta);
        etHoraDisponibleHasta = findViewById(R.id.etHoraDisponibleHasta);
        tvDisponibleStatus = findViewById(R.id.tvDisponibleStatus);
        btnGuardarCategoria = findViewById(R.id.btnGuardarCategoria);


        TextWatcher timeTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                actualizarEstadoDisponibleUI();
            }
        };

        etHoraDisponibleDesde.addTextChangedListener(timeTextWatcher);
        etHoraDisponibleHasta.addTextChangedListener(timeTextWatcher);

        etHoraDisponibleDesde.setOnClickListener(v -> showTimePickerDialog(etHoraDisponibleDesde));
        etHoraDisponibleHasta.setOnClickListener(v -> showTimePickerDialog(etHoraDisponibleHasta));

        btnGuardarCategoria.setOnClickListener(v -> guardarCategoriaProducto());
        actualizarEstadoDisponibleUI();
    }

    private void showTimePickerDialog(final TextInputEditText timeEditText) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        String existingTime = timeEditText.getText().toString();
        if (isValidTimeFormat(existingTime)) {
            try {
                Calendar existingCal = Calendar.getInstance();
                existingCal.setTime(timeFormat.parse(existingTime));
                currentHour = existingCal.get(Calendar.HOUR_OF_DAY);
                currentMinute = existingCal.get(Calendar.MINUTE);
            } catch (ParseException e) {
                // Si hay error, usar la hora actual
            }
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    timeEditText.setText(formattedTime);
                }, currentHour, currentMinute, true);
        timePickerDialog.show();
    }

    private boolean isValidTimeFormat(String time) {
        if (time == null || TextUtils.isEmpty(time)) return false;
        if (!time.matches("^([01]\\d|2[0-3]):([0-5]\\d)$")) return false;
        try {
            timeFormat.setLenient(false);
            timeFormat.parse(time);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private int calcularDisponibilidadNumerica(String horaDesdeStr, String horaHastaStr) {
        if (!isValidTimeFormat(horaDesdeStr) || !isValidTimeFormat(horaHastaStr)) {
            return 0;
        }
        try {
            Calendar ahoraCal = Calendar.getInstance();

            Calendar desdeCal = Calendar.getInstance();
            desdeCal.setTime(timeFormat.parse(horaDesdeStr));
            desdeCal.set(Calendar.YEAR, ahoraCal.get(Calendar.YEAR));
            desdeCal.set(Calendar.MONTH, ahoraCal.get(Calendar.MONTH));
            desdeCal.set(Calendar.DAY_OF_MONTH, ahoraCal.get(Calendar.DAY_OF_MONTH));

            Calendar hastaCal = Calendar.getInstance();
            hastaCal.setTime(timeFormat.parse(horaHastaStr));
            hastaCal.set(Calendar.YEAR, ahoraCal.get(Calendar.YEAR));
            hastaCal.set(Calendar.MONTH, ahoraCal.get(Calendar.MONTH));
            hastaCal.set(Calendar.DAY_OF_MONTH, ahoraCal.get(Calendar.DAY_OF_MONTH));

            if (hastaCal.before(desdeCal) || hastaCal.equals(desdeCal)) {
                if (ahoraCal.compareTo(desdeCal) >= 0 || ahoraCal.compareTo(hastaCal) < 0) {
                    return 1;
                }
            } else {
                if (ahoraCal.compareTo(desdeCal) >= 0 && ahoraCal.compareTo(hastaCal) < 0) {
                    return 1;
                }
            }
            return 0;
        } catch (ParseException e) {
            return 0;
        }
    }

    private void actualizarEstadoDisponibleUI() {
        String horaDesdeStr = etHoraDisponibleDesde.getText().toString().trim();
        String horaHastaStr = etHoraDisponibleHasta.getText().toString().trim();

        if (TextUtils.isEmpty(horaDesdeStr) || TextUtils.isEmpty(horaHastaStr) || !isValidTimeFormat(horaDesdeStr) || !isValidTimeFormat(horaHastaStr)) {
            tvDisponibleStatus.setText("Disponible ahora: (Horas inválidas)");
            tilHoraDisponibleDesde.setError(isValidTimeFormat(horaDesdeStr) || TextUtils.isEmpty(horaDesdeStr) ? null : "Formato HH:MM");
            tilHoraDisponibleHasta.setError(isValidTimeFormat(horaHastaStr) || TextUtils.isEmpty(horaHastaStr) ? null : "Formato HH:MM");
            return;
        }
        tilHoraDisponibleDesde.setError(null);
        tilHoraDisponibleHasta.setError(null);

        int disponibilidadNum = calcularDisponibilidadNumerica(horaDesdeStr, horaHastaStr);
        tvDisponibleStatus.setText("Disponible ahora: " + (disponibilidadNum == 1 ? "Sí" : "No"));
    }


    private void guardarCategoriaProducto() {
        // ... (validaciones igual que antes) ...
        String nombre = etNombreCategoria.getText().toString().trim();
        String descripcion = etDescripcionCategoria.getText().toString().trim();
        String horaDesde = etHoraDisponibleDesde.getText().toString().trim();
        String horaHasta = etHoraDisponibleHasta.getText().toString().trim();

        boolean esValido = true;

        if (TextUtils.isEmpty(nombre)) {
            tilNombreCategoria.setError("El nombre es requerido");
            esValido = false;
        } else {
            tilNombreCategoria.setError(null);
        }

        if (TextUtils.isEmpty(descripcion)) {
            tilDescripcionCategoria.setError("La descripción es requerida");
            esValido = false;
        } else {
            tilDescripcionCategoria.setError(null);
        }

        if (TextUtils.isEmpty(horaDesde)) {
            tilHoraDisponibleDesde.setError("La hora desde es requerida");
            esValido = false;
        } else if (!isValidTimeFormat(horaDesde)) {
            tilHoraDisponibleDesde.setError("Formato de hora inválido (HH:MM)");
            esValido = false;
        } else {
            tilHoraDisponibleDesde.setError(null);
        }

        if (TextUtils.isEmpty(horaHasta)) {
            tilHoraDisponibleHasta.setError("La hora hasta es requerida");
            esValido = false;
        } else if (!isValidTimeFormat(horaHasta)) {
            tilHoraDisponibleHasta.setError("Formato de hora inválido (HH:MM)");
            esValido = false;
        } else {
            tilHoraDisponibleHasta.setError(null);
        }


        if (!esValido) {
            Toast.makeText(this, "Por favor, corrige los errores.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Deshabilitar el botón para evitar múltiples envíos
        btnGuardarCategoria.setEnabled(false);
        Toast.makeText(this, "Guardando categoría...", Toast.LENGTH_SHORT).show();


        int disponibleParaEnviar = calcularDisponibilidadNumerica(horaDesde, horaHasta);

        // 2. Llamar al Helper CON el callback (esta instancia de la Activity)
        CategoriaProductoHelper.crearCategoriaProducto(
                this,
                nombre,
                descripcion,
                disponibleParaEnviar,
                horaDesde,
                horaHasta,
                this // 'this' porque WebService7Activity implementa CategoriaProductoCallback
        );
        // Ya no se llama a finish() aquí directamente
    }

    // 3. Implementación de los métodos del Callback
    @Override
    public void onSuccess(String message, String idCategoriaProducto) {
        Toast.makeText(this, message + (idCategoriaProducto != null ? " ID: " + idCategoriaProducto : ""), Toast.LENGTH_LONG).show();
        // Volver a habilitar el botón si fuera necesario, aunque vamos a cerrar
        // btnGuardarCategoria.setEnabled(true);
        finish(); // Cerrar la actividad y regresar en caso de éxito
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
        // Volver a habilitar el botón para que el usuario pueda intentar de nuevo
        btnGuardarCategoria.setEnabled(true);
        // No cerramos la actividad en caso de error, para que el usuario pueda corregir
    }
}