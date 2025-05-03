package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucursal_crear);

        // Inicializar views
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

        cargarSpinnerDepartamento();

        spDepartamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                if (pos > 0) {
                    int depId = departamentoIds.get(pos - 1);
                    cargarSpinnerMunicipio(depId);
                    spMunicipio.setEnabled(true);
                } else {
                    spMunicipio.setEnabled(false);
                    spDistrito.setEnabled(false);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        spMunicipio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                if (pos > 0) {
                    int depId = departamentoIds.get(spDepartamento.getSelectedItemPosition() - 1);
                    int munId = municipioIds.get(pos - 1);
                    cargarSpinnerDistrito(depId, munId);
                    spDistrito.setEnabled(true);
                } else {
                    spDistrito.setEnabled(false);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        etHoraApertura.setOnClickListener(v -> mostrarTimePicker(etHoraApertura));
        etHoraCierre.setOnClickListener(v -> mostrarTimePicker(etHoraCierre));

        btnGuardar.setOnClickListener(v -> guardarSucursal());
        btnLimpiar.setOnClickListener(v -> limpiarCampos());
    }

    private void mostrarTimePicker(EditText campoHora) {
        final Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minuto = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String horaFormateada = String.format("%02d:%02d", hourOfDay, minute);
                    campoHora.setText(horaFormateada);
                },
                hora, minuto, true);
        tpd.show();
    }

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
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartamento.setAdapter(adapter);
    }

    private void cargarSpinnerMunicipio(int idDepartamento) {
        municipioIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione...");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT ID_MUNICIPIO, NOMBRE_MUNICIPIO FROM MUNICIPIO WHERE ID_DEPARTAMENTO = ?",
                new String[]{String.valueOf(idDepartamento)});
        while (c.moveToNext()) {
            municipioIds.add(c.getInt(0));
            nombres.add(c.getString(1));
        }
        c.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMunicipio.setAdapter(adapter);
    }

    private void cargarSpinnerDistrito(int idDepartamento, int idMunicipio) {
        distritoIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione...");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT ID_DISTRITO, NOMBRE_DISTRITO FROM DISTRITO " +
                        "WHERE ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ?",
                new String[]{String.valueOf(idDepartamento), String.valueOf(idMunicipio)});
        while (c.moveToNext()) {
            distritoIds.add(c.getInt(0));
            nombres.add(c.getString(1));
        }
        c.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistrito.setAdapter(adapter);
    }

    private void guardarSucursal() {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String horaApertura = etHoraApertura.getText().toString();
        String horaCierre = etHoraCierre.getText().toString();

        if (nombre.isEmpty() || telefono.isEmpty() || direccion.isEmpty()
                || spDepartamento.getSelectedItemPosition() == 0
                || spMunicipio.getSelectedItemPosition() == 0
                || spDistrito.getSelectedItemPosition() == 0
                || horaApertura.isEmpty() || horaCierre.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int idDepto = departamentoIds.get(spDepartamento.getSelectedItemPosition() - 1);
        int idMun = municipioIds.get(spMunicipio.getSelectedItemPosition() - 1);
        int idDist = distritoIds.get(spDistrito.getSelectedItemPosition() - 1);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ID_DEPARTAMENTO", idDepto);
        values.put("ID_MUNICIPIO", idMun);
        values.put("ID_DISTRITO", idDist);
        values.put("ID_USUARIO", 999); // ID de usuario temporal o real si lo tienes
        values.put("NOMBRE_SUCURSAL", nombre);
        values.put("DIRECCION_SUCURSAL", direccion);
        values.put("TELEFONO_SUCURSAL", telefono);
        values.put("HORARIO_APERTURA_SUCURSAL", horaApertura);
        values.put("HORARIO_CIERRE_SUCURSAL", horaCierre);

        long newId = db.insert("SUCURSAL", null, values);
        db.close();

        if (newId != -1) {
            Toast.makeText(this, "Sucursal guardada con ID " + newId, Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar la sucursal", Toast.LENGTH_LONG).show();
        }
    }



    private void limpiarCampos() {
        etNombre.setText("");
        etTelefono.setText("");
        etDireccion.setText("");
        etHoraApertura.setText("");
        etHoraCierre.setText("");
        spDepartamento.setSelection(0);
        spMunicipio.setSelection(0);
        spMunicipio.setEnabled(false);
        spDistrito.setSelection(0);
        spDistrito.setEnabled(false);
    }
}
