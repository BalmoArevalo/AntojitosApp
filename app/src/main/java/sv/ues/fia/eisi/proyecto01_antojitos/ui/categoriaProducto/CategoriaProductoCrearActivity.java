package sv.ues.fia.eisi.proyecto01_antojitos.ui.categoriaProducto;

import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class CategoriaProductoCrearActivity extends AppCompatActivity {

    private EditText editTextNombre, editTextDescripcion, editTextDesde, editTextHasta;
    private TextView textViewDisponible;
    private Button btnGuardar;

    private SQLiteDatabase db;
    private CategoriaProductoDAO categoriaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_producto_crear);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        editTextDesde = findViewById(R.id.editTextDesde);
        editTextHasta = findViewById(R.id.editTextHasta);
        textViewDisponible = findViewById(R.id.textViewDisponible);
        btnGuardar = findViewById(R.id.btnGuardarCategoria);

        editTextDesde.setOnClickListener(v -> mostrarTimePicker(editTextDesde));
        editTextHasta.setOnClickListener(v -> mostrarTimePicker(editTextHasta));

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        categoriaDAO = new CategoriaProductoDAO(db);

        btnGuardar.setOnClickListener(v -> guardarCategoria());
    }

    private void mostrarTimePicker(EditText target) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
            target.setText(timeFormatted);
            verificarDisponibilidad();
        }, hour, minute, true);
        timePicker.show();
    }

    private void verificarDisponibilidad() {
        String desde = editTextDesde.getText().toString().trim();
        String hasta = editTextHasta.getText().toString().trim();

        if (desde.isEmpty() || hasta.isEmpty()) {
            textViewDisponible.setText(getString(R.string.categoria_producto_disponible_default));
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date horaDesde = sdf.parse(desde);
            Date horaHasta = sdf.parse(hasta);
            Date ahora = sdf.parse(sdf.format(new Date()));

            boolean disponible = ahora.equals(horaDesde) || ahora.equals(horaHasta)
                    || (ahora.after(horaDesde) && ahora.before(horaHasta));

            String disponibleStr = disponible ? getString(R.string.respuesta_si) : getString(R.string.respuesta_no);
            textViewDisponible.setText(getString(R.string.categoria_producto_disponible_label, disponibleStr));

        } catch (ParseException e) {
            textViewDisponible.setText(getString(R.string.categoria_producto_disponible_error));
        }
    }

    private void guardarCategoria() {
        String nombre = editTextNombre.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String desde = editTextDesde.getText().toString().trim();
        String hasta = editTextHasta.getText().toString().trim();

        if (nombre.isEmpty() || descripcion.isEmpty() || desde.isEmpty() || hasta.isEmpty()) {
            Toast.makeText(this, getString(R.string.categoria_producto_toast_campos_obligatorios), Toast.LENGTH_SHORT).show();
            return;
        }

        CategoriaProducto categoria = new CategoriaProducto(
                nombre,
                descripcion,
                0, // disponible ignorado
                desde,
                hasta,
                1
        );

        boolean ok = categoriaDAO.insertar(categoria);
        if (ok) {
            Toast.makeText(this, getString(R.string.categoria_producto_toast_guardado_ok), Toast.LENGTH_LONG).show();
            limpiarCampos();
        } else {
            Toast.makeText(this, getString(R.string.categoria_producto_toast_guardado_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        editTextNombre.setText("");
        editTextDescripcion.setText("");
        editTextDesde.setText("");
        editTextHasta.setText("");
        textViewDisponible.setText(getString(R.string.categoria_producto_disponible_default));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) db.close();
    }
}