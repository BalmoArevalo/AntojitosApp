package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.app.TimePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class SucursalEditarActivity extends AppCompatActivity {

    private Spinner spSucursal, spDepartamento, spMunicipio, spDistrito;
    private EditText etNombre, etTelefono, etDireccion, etHoraApertura, etHoraCierre;
    private Button btnBuscar, btnActualizar, btnLimpiar;

    private DBHelper dbHelper;

    private List<Integer> sucursalIds = new ArrayList<>();
    private List<Integer> departamentoIds = new ArrayList<>();
    private List<Integer> municipioIds = new ArrayList<>();
    private List<Integer> distritoIds = new ArrayList<>();

    private int idSucursalSeleccionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucursal_editar);

        spSucursal = findViewById(R.id.spinnerSucursal);
        spDepartamento = findViewById(R.id.spinnerDepartamento);
        spMunicipio = findViewById(R.id.spinnerMunicipio);
        spDistrito = findViewById(R.id.spinnerDistrito);

        etNombre = findViewById(R.id.editNombreSucursal);
        etTelefono = findViewById(R.id.editTelefonoSucursal);
        etDireccion = findViewById(R.id.editDireccionSucursal);
        etHoraApertura = findViewById(R.id.editHorarioApertura);
        etHoraCierre = findViewById(R.id.editHorarioCierre);

        btnBuscar = findViewById(R.id.btnBuscarSucursal);
        btnActualizar = findViewById(R.id.btnActualizarSucursal);
        btnLimpiar = findViewById(R.id.btnLimpiarCampos);

        dbHelper = new DBHelper(this);

        cargarSpinnerSucursales();
        cargarSpinnerDepartamento();

        spDepartamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                if (pos > 0) {
                    int depId = departamentoIds.get(pos - 1);
                    cargarSpinnerMunicipio(depId);
                    spMunicipio.setEnabled(true);
                } else {
                    spMunicipio.setEnabled(false);
                    spDistrito.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> p) {}
        });

        spMunicipio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                if (pos > 0) {
                    int depId = departamentoIds.get(spDepartamento.getSelectedItemPosition() - 1);
                    int munId = municipioIds.get(pos - 1);
                    cargarSpinnerDistrito(depId, munId);
                    spDistrito.setEnabled(true);
                } else {
                    spDistrito.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> p) {}
        });

        etHoraApertura.setOnClickListener(v -> mostrarTimePicker(etHoraApertura));
        etHoraCierre.setOnClickListener(v -> mostrarTimePicker(etHoraCierre));

        btnBuscar.setOnClickListener(v -> buscarSucursal());
        btnActualizar.setOnClickListener(v -> actualizarSucursal());
        btnLimpiar.setOnClickListener(v -> limpiarCampos());
    }

    private void cargarSpinnerSucursales() {
        sucursalIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione...");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT ID_SUCURSAL, NOMBRE_SUCURSAL FROM SUCURSAL", null);
        while (c.moveToNext()) {
            sucursalIds.add(c.getInt(0));
            nombres.add(c.getInt(0) + " - " + c.getString(1));
        }
        c.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSucursal.setAdapter(adapter);
    }

    private void cargarSpinnerDepartamento() {
        departamentoIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione...");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT ID_DEPARTAMENTO, NOMBRE_DEPARTAMENTO FROM DEPARTAMENTO", null);
        while (c.moveToNext()) {
            departamentoIds.add(c.getInt(0));
            nombres.add(c.getInt(0) + " - " + c.getString(1));
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
            nombres.add(c.getInt(0) + " - " + c.getString(1));
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
        Cursor c = db.rawQuery("SELECT ID_DISTRITO, NOMBRE_DISTRITO FROM DISTRITO WHERE ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ?",
                new String[]{String.valueOf(idDepartamento), String.valueOf(idMunicipio)});
        while (c.moveToNext()) {
            distritoIds.add(c.getInt(0));
            nombres.add(c.getInt(0) + " - " + c.getString(1));
        }
        c.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistrito.setAdapter(adapter);
    }

    private void buscarSucursal() {
        int pos = spSucursal.getSelectedItemPosition();
        if (pos <= 0) {
            Toast.makeText(this, "Selecciona una sucursal válida", Toast.LENGTH_SHORT).show();
            return;
        }
        idSucursalSeleccionada = sucursalIds.get(pos - 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM SUCURSAL WHERE ID_SUCURSAL = ?",
                new String[]{String.valueOf(idSucursalSeleccionada)});
        if (c.moveToFirst()) {
            etNombre.setText(c.getString(c.getColumnIndexOrThrow("NOMBRE_SUCURSAL")));
            etTelefono.setText(c.getString(c.getColumnIndexOrThrow("TELEFONO_SUCURSAL")));
            etDireccion.setText(c.getString(c.getColumnIndexOrThrow("DIRECCION_SUCURSAL")));
            etHoraApertura.setText(c.getString(c.getColumnIndexOrThrow("HORARIO_APERTURA_SUCURSAL")));
            etHoraCierre.setText(c.getString(c.getColumnIndexOrThrow("HORARIO_CIERRE_SUCURSAL")));

            int idDep = c.getInt(c.getColumnIndexOrThrow("ID_DEPARTAMENTO"));
            int idMun = c.getInt(c.getColumnIndexOrThrow("ID_MUNICIPIO"));
            int idDist = c.getInt(c.getColumnIndexOrThrow("ID_DISTRITO"));

            int posDep = departamentoIds.indexOf(idDep);
            if (posDep >= 0) spDepartamento.setSelection(posDep + 1);
            cargarSpinnerMunicipio(idDep);
            int posMun = municipioIds.indexOf(idMun);
            if (posMun >= 0) spMunicipio.setSelection(posMun + 1);
            cargarSpinnerDistrito(idDep, idMun);
            int posDist = distritoIds.indexOf(idDist);
            if (posDist >= 0) spDistrito.setSelection(posDist + 1);
        }
        c.close();
        db.close();
    }

    private void actualizarSucursal() {
        if (idSucursalSeleccionada == -1) {
            Toast.makeText(this, "Busca primero una sucursal", Toast.LENGTH_SHORT).show();
            return;
        }

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

        int idDep = departamentoIds.get(spDepartamento.getSelectedItemPosition() - 1);
        int idMun = municipioIds.get(spMunicipio.getSelectedItemPosition() - 1);
        int idDist = distritoIds.get(spDistrito.getSelectedItemPosition() - 1);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(
                "UPDATE SUCURSAL SET ID_DEPARTAMENTO=?, ID_MUNICIPIO=?, ID_DISTRITO=?, " +
                        "NOMBRE_SUCURSAL=?, DIRECCION_SUCURSAL=?, TELEFONO_SUCURSAL=?, " +
                        "HORARIO_APERTURA_SUCURSAL=?, HORARIO_CIERRE_SUCURSAL=? WHERE ID_SUCURSAL=?",
                new Object[]{idDep, idMun, idDist, nombre, direccion, telefono, horaApertura, horaCierre, idSucursalSeleccionada}
        );
        db.close();

        Toast.makeText(this, "Sucursal actualizada correctamente", Toast.LENGTH_LONG).show();
        finish();
    }

    private void mostrarTimePicker(EditText campoHora) {
        final Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minuto = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    String horaFormateada = String.format("%02d:%02d", hourOfDay, minute1);
                    campoHora.setText(horaFormateada);
                }, hora, minuto, true);
        tpd.show();
    }

    private void limpiarCampos() {
        spSucursal.setSelection(0);
        etNombre.setText("");
        etTelefono.setText("");
        etDireccion.setText("");
        etHoraApertura.setText("");
        etHoraCierre.setText("");
        spDepartamento.setSelection(0);
        spMunicipio.setSelection(0);
        spDistrito.setSelection(0);
        spMunicipio.setEnabled(false);
        spDistrito.setEnabled(false);
        idSucursalSeleccionada = -1;
    }
}
