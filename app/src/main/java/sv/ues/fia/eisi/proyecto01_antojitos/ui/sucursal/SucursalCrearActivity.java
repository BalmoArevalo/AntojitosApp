package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.app.TimePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class SucursalCrearActivity extends AppCompatActivity {

    private EditText etNombre, etTelefono, etDireccion, etHoraApertura, etHoraCierre;
    private Spinner spDepartamento, spMunicipio, spDistrito;
    private Button btnGuardar, btnLimpiar;

    private List<Integer> departamentoIds = new ArrayList<>();
    private List<Integer> municipioIds = new ArrayList<>();
    private List<Integer> distritoIds = new ArrayList<>();

    private DBHelper dbHelper;
    private SucursalDAO dao;
    private SucursalViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucursal_crear);

        etNombre = findViewById(R.id.editNombreSucursal);
        etTelefono = findViewById(R.id.editTelefonoSucursal);
        etDireccion = findViewById(R.id.editDireccionSucursal);
        etHoraApertura = findViewById(R.id.editHorarioApertura);
        etHoraCierre = findViewById(R.id.editHorarioCierre);
        spDepartamento = findViewById(R.id.spinnerDepartamento);
        spMunicipio = findViewById(R.id.spinnerMunicipio);
        spDistrito = findViewById(R.id.spinnerDistrito);
        btnGuardar = findViewById(R.id.btnGuardarSucursal);
        btnLimpiar = findViewById(R.id.btnLimpiarCampos);

        dbHelper = new DBHelper(this);
        dao = new SucursalDAO(dbHelper.getWritableDatabase());
        viewModel = new ViewModelProvider(this).get(SucursalViewModel.class);

        // Observa resultado de inserción
        viewModel.getIdInsertado().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long newId) {
                if (newId != null && newId > 0) {
                    Toast.makeText(SucursalCrearActivity.this,
                            "Sucursal guardada con ID " + newId,
                            Toast.LENGTH_LONG).show();
                    finish();
                } else if (newId != null && newId == -1) {
                    Toast.makeText(SucursalCrearActivity.this,
                            "Error al guardar la sucursal",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        cargarSpinnerDepartamento();
        configurarSpinners();

        etHoraApertura.setOnClickListener(v -> mostrarTimePicker(etHoraApertura));
        etHoraCierre.setOnClickListener(v -> mostrarTimePicker(etHoraCierre));

        btnGuardar.setOnClickListener(v -> guardarSucursal());
        btnLimpiar.setOnClickListener(v -> limpiarCampos());
    }

    private void configurarSpinners() {
        spDepartamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos > 0) {
                    int depId = departamentoIds.get(pos - 1);
                    cargarSpinnerMunicipio(depId);
                    spMunicipio.setEnabled(true);
                } else {
                    spMunicipio.setEnabled(false);
                    spDistrito.setEnabled(false);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spMunicipio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos > 0) {
                    int depId = departamentoIds.get(spDepartamento.getSelectedItemPosition() - 1);
                    int munId = municipioIds.get(pos - 1);
                    cargarSpinnerDistrito(depId, munId);
                    spDistrito.setEnabled(true);
                } else {
                    spDistrito.setEnabled(false);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * Carga departamentos desde la base de datos.
     */
    private void cargarSpinnerDepartamento() {
        departamentoIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione...");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT ID_DEPARTAMENTO, NOMBRE_DEPARTAMENTO FROM DEPARTAMENTO", null);
        while (c.moveToNext()) {
            departamentoIds.add(c.getInt(0));
            nombres.add(c.getString(1));
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartamento.setAdapter(adapter);
    }

    /**
     * Carga municipios según departamento seleccionado.
     */
    private void cargarSpinnerMunicipio(int idDepartamento) {
        municipioIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione...");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT ID_MUNICIPIO, NOMBRE_MUNICIPIO FROM MUNICIPIO WHERE ID_DEPARTAMENTO = ?",
                new String[]{String.valueOf(idDepartamento)});
        while (c.moveToNext()) {
            municipioIds.add(c.getInt(0));
            nombres.add(c.getString(1));
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMunicipio.setAdapter(adapter);
    }

    /**
     * Carga distritos según departamento y municipio.
     */
    private void cargarSpinnerDistrito(int idDepartamento, int idMunicipio) {
        distritoIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione...");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT ID_DISTRITO, NOMBRE_DISTRITO FROM DISTRITO WHERE ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ?",
                new String[]{String.valueOf(idDepartamento), String.valueOf(idMunicipio)});
        while (c.moveToNext()) {
            distritoIds.add(c.getInt(0));
            nombres.add(c.getString(1));
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistrito.setAdapter(adapter);
    }

    /**
     * TimePicker para seleccionar hora.
     */
    private void mostrarTimePicker(EditText campoHora) {
        Calendar c = Calendar.getInstance();
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);
        new TimePickerDialog(this,
                (view, hourOfDay, minute) -> campoHora.setText(String.format("%02d:%02d", hourOfDay, minute)),
                h, m, true).show();
    }

    /**
     * Valida y guarda la sucursal.
     */
    private void guardarSucursal() {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String horaA = etHoraApertura.getText().toString();
        String horaC = etHoraCierre.getText().toString();

        if (nombre.isEmpty() || telefono.isEmpty() || direccion.isEmpty()
                || spDepartamento.getSelectedItemPosition() == 0
                || spMunicipio.getSelectedItemPosition() == 0
                || spDistrito.getSelectedItemPosition() == 0
                || horaA.isEmpty() || horaC.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Sucursal s = new Sucursal(
                departamentoIds.get(spDepartamento.getSelectedItemPosition() - 1),
                municipioIds.get(spMunicipio.getSelectedItemPosition() - 1),
                distritoIds.get(spDistrito.getSelectedItemPosition() - 1),
                nombre, direccion, telefono, horaA, horaC
        );
        viewModel.insertarSucursal(s);
    }

    /**
     * Limpia el formulario.
     */
    private void limpiarCampos() {
        etNombre.setText("");
        etTelefono.setText("");
        etDireccion.setText("");
        etHoraApertura.setText("");
        etHoraCierre.setText("");
        spDepartamento.setSelection(0);
        spMunicipio.setSelection(0); spMunicipio.setEnabled(false);
        spDistrito.setSelection(0); spDistrito.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}