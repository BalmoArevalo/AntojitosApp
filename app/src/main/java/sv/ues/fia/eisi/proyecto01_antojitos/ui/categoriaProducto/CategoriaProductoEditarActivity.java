package sv.ues.fia.eisi.proyecto01_antojitos.ui.categoriaProducto;

import android.annotation.SuppressLint;
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

public class CategoriaProductoEditarActivity extends AppCompatActivity {

    private Spinner spinnerCategoria;
    private EditText editTextNombre, editTextDescripcion, editTextHoraDesde, editTextHoraHasta;
    private Switch switchActivo;
    private TextView textViewDisponible;
    private Button btnActualizar;

    private SQLiteDatabase db;
    private CategoriaProductoDAO dao;
    private List<CategoriaProducto> listaCategorias;
    private Map<String, CategoriaProducto> mapCategorias;

    private CategoriaProducto seleccionada;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_producto_editar);

        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        editTextHoraDesde = findViewById(R.id.editTextHoraDesde);
        editTextHoraHasta = findViewById(R.id.editTextHoraHasta);
        switchActivo = findViewById(R.id.switchActivo);
        textViewDisponible = findViewById(R.id.textViewDisponible);
        btnActualizar = findViewById(R.id.btnActualizarCategoria);

        editTextHoraDesde.setOnClickListener(v -> mostrarTimePicker(editTextHoraDesde));
        editTextHoraHasta.setOnClickListener(v -> mostrarTimePicker(editTextHoraHasta));

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        dao = new CategoriaProductoDAO(db);

        cargarSpinner();

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position == 0) {
                    limpiarCampos();
                    btnActualizar.setEnabled(false);
                    seleccionada = null;
                    return;
                }

                String label = parent.getItemAtPosition(position).toString();
                seleccionada = mapCategorias.get(label);
                if (seleccionada != null) {
                    mostrarDatosCategoria(seleccionada);
                    btnActualizar.setEnabled(true);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnActualizar.setOnClickListener(v -> actualizarCategoria());
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
        String desde = editTextHoraDesde.getText().toString().trim();
        String hasta = editTextHoraHasta.getText().toString().trim();

        if (desde.isEmpty() || hasta.isEmpty()) {
            textViewDisponible.setText("Disponible ahora: -");
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date horaDesde = sdf.parse(desde);
            Date horaHasta = sdf.parse(hasta);
            Date ahora = sdf.parse(sdf.format(new Date()));

            boolean disponible = ahora.equals(horaDesde) || ahora.equals(horaHasta)
                    || (ahora.after(horaDesde) && ahora.before(horaHasta));

            textViewDisponible.setText("Disponible ahora: " + (disponible ? "Sí" : "No"));

        } catch (ParseException e) {
            textViewDisponible.setText("Error al calcular disponibilidad");
        }
    }

    private void cargarSpinner() {
        listaCategorias = dao.obtenerTodos(false); // todas, activas e inactivas
        actualizarDisponibilidadSegunHora(listaCategorias);

        mapCategorias = new HashMap<>();
        List<String> items = new ArrayList<>();
        items.add("Seleccione");

        for (CategoriaProducto c : listaCategorias) {
            String label = c.getIdCategoriaProducto() + " - " + c.getNombreCategoria();
            if (c.getActivoCategoriaProducto() == 0) label += " (Inactiva)";
            items.add(label);
            mapCategorias.put(label, c);
        }

        spinnerCategoria.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void mostrarDatosCategoria(CategoriaProducto c) {
        editTextNombre.setText(c.getNombreCategoria());
        editTextDescripcion.setText(c.getDescripcionCategoria());
        editTextHoraDesde.setText(c.getHoraDisponibleDesde());
        editTextHoraHasta.setText(c.getHoraDisponibleHasta());
        switchActivo.setChecked(c.getActivoCategoriaProducto() == 1);
        verificarDisponibilidad();
    }

    private void actualizarCategoria() {
        if (seleccionada == null) return;

        seleccionada.setNombreCategoria(editTextNombre.getText().toString().trim());
        seleccionada.setDescripcionCategoria(editTextDescripcion.getText().toString().trim());
        seleccionada.setHoraDisponibleDesde(editTextHoraDesde.getText().toString().trim());
        seleccionada.setHoraDisponibleHasta(editTextHoraHasta.getText().toString().trim());
        seleccionada.setActivoCategoriaProducto(switchActivo.isChecked() ? 1 : 0);

        boolean ok = dao.actualizar(seleccionada);
        if (ok) {
            Toast.makeText(this, "Categoría actualizada correctamente", Toast.LENGTH_SHORT).show();
            cargarSpinner();
            spinnerCategoria.setSelection(0);
        } else {
            Toast.makeText(this, "Error al actualizar categoría", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarDisponibilidadSegunHora(List<CategoriaProducto> lista) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String ahoraStr = sdf.format(new Date());

        try {
            Date ahora = sdf.parse(ahoraStr);

            for (CategoriaProducto c : lista) {
                Date desde = sdf.parse(c.getHoraDisponibleDesde());
                Date hasta = sdf.parse(c.getHoraDisponibleHasta());

                boolean disponible = ahora.equals(desde) || ahora.equals(hasta)
                        || (ahora.after(desde) && ahora.before(hasta));

                int nuevoEstado = disponible ? 1 : 0;

                if (c.getDisponibleCategoria() != nuevoEstado) {
                    c.setDisponibleCategoria(nuevoEstado);
                    dao.actualizar(c);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void limpiarCampos() {
        editTextNombre.setText("");
        editTextDescripcion.setText("");
        editTextHoraDesde.setText("");
        editTextHoraHasta.setText("");
        switchActivo.setChecked(false);
        textViewDisponible.setText("Disponible ahora: -");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) db.close();
    }
}
