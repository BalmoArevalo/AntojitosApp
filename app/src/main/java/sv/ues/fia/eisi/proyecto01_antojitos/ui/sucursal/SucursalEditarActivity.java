package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.app.TimePickerDialog;
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

public class SucursalEditarActivity extends AppCompatActivity {

    private Spinner spSucursal, spDepartamento, spMunicipio, spDistrito;
    private EditText etNombre, etTelefono, etDireccion, etHoraApertura, etHoraCierre;
    private Switch switchActivo;
    private Button btnBuscar, btnActualizar, btnLimpiar;

    private DBHelper dbHelper;
    private SucursalDAO dao;

    private List<Integer> sucursalIds = new ArrayList<>();
    private List<Integer> departamentoIds = new ArrayList<>();
    private List<Integer> municipioIds = new ArrayList<>();
    private List<Integer> distritoIds = new ArrayList<>();

    private int idSucursalSeleccionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucursal_editar);

        // Inicializar vistas
        spSucursal = findViewById(R.id.spinnerSucursal);
        spDepartamento = findViewById(R.id.spinnerDepartamento);
        spMunicipio = findViewById(R.id.spinnerMunicipio);
        spDistrito = findViewById(R.id.spinnerDistrito);
        etNombre = findViewById(R.id.editNombreSucursal);
        etTelefono = findViewById(R.id.editTelefonoSucursal);
        etDireccion = findViewById(R.id.editDireccionSucursal);
        etHoraApertura = findViewById(R.id.editHorarioApertura);
        etHoraCierre = findViewById(R.id.editHorarioCierre);
        switchActivo = findViewById(R.id.switchActivoSucursal);
        btnBuscar = findViewById(R.id.btnBuscarSucursal);
        btnActualizar = findViewById(R.id.btnActualizarSucursal);
        btnLimpiar = findViewById(R.id.btnLimpiarCampos);

        dbHelper = new DBHelper(this);
        dao = new SucursalDAO(dbHelper.getWritableDatabase());

        cargarSpinnerSucursales();
        cargarSpinnerDepartamento();

        setFormularioEnabled(false);

        spDepartamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos > 0) {
                    int depId = departamentoIds.get(pos - 1);
                    cargarSpinnerMunicipio(depId);
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
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        etHoraApertura.setOnClickListener(v -> mostrarTimePicker(etHoraApertura));
        etHoraCierre.setOnClickListener(v -> mostrarTimePicker(etHoraCierre));

        btnBuscar.setOnClickListener(v -> buscarSucursal());
        btnActualizar.setOnClickListener(v -> actualizarSucursal());
        btnLimpiar.setOnClickListener(v -> limpiarCampos());
    }

    private void setFormularioEnabled(boolean enabled) {
        etNombre.setEnabled(enabled);
        etTelefono.setEnabled(enabled);
        etDireccion.setEnabled(enabled);
        etHoraApertura.setEnabled(enabled);
        etHoraCierre.setEnabled(enabled);
        spDepartamento.setEnabled(enabled);
        spMunicipio.setEnabled(enabled);
        spDistrito.setEnabled(enabled);
        switchActivo.setEnabled(enabled);
        btnActualizar.setEnabled(enabled);
        btnLimpiar.setEnabled(enabled);
    }

    private void cargarSpinnerSucursales() {
        sucursalIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add(getString(R.string.spinner_placeholder));
        sucursalIds.add(-1);

        List<Sucursal> lista = dao.obtenerTodos();
        for (Sucursal s : lista) {
            sucursalIds.add(s.getIdSucursal());
            String estado = s.getActivoSucursal() == 1
                    ? getString(R.string.estado_activo)
                    : getString(R.string.estado_inactivo);
            nombres.add(s.getIdSucursal() + " - " + s.getNombreSucursal() + " " + estado);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSucursal.setAdapter(adapter);
    }

    private void cargarSpinnerDepartamento() {
        departamentoIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add(getString(R.string.spinner_placeholder));

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT ID_DEPARTAMENTO, NOMBRE_DEPARTAMENTO FROM DEPARTAMENTO", null);
        while (c.moveToNext()) {
            departamentoIds.add(c.getInt(0));
            nombres.add(c.getInt(0) + " - " + c.getString(1));
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartamento.setAdapter(adapter);
    }

    private void cargarSpinnerMunicipio(int idDepartamento) {
        municipioIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add(getString(R.string.spinner_placeholder));

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT ID_MUNICIPIO, NOMBRE_MUNICIPIO FROM MUNICIPIO WHERE ID_DEPARTAMENTO = ?",
                new String[]{String.valueOf(idDepartamento)});
        while (c.moveToNext()) {
            municipioIds.add(c.getInt(0));
            nombres.add(c.getInt(0) + " - " + c.getString(1));
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMunicipio.setAdapter(adapter);
    }

    private void cargarSpinnerDistrito(int idDepartamento, int idMunicipio) {
        distritoIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add(getString(R.string.spinner_placeholder));

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT ID_DISTRITO, NOMBRE_DISTRITO FROM DISTRITO WHERE ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ?",
                new String[]{String.valueOf(idDepartamento), String.valueOf(idMunicipio)});
        while (c.moveToNext()) {
            distritoIds.add(c.getInt(0));
            nombres.add(c.getInt(0) + " - " + c.getString(1));
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistrito.setAdapter(adapter);
    }

    private void buscarSucursal() {
        int pos = spSucursal.getSelectedItemPosition();
        if (pos <= 0) {
            Toast.makeText(this, getString(R.string.sucursal_editar_toast_seleccione), Toast.LENGTH_SHORT).show();
            return;
        }

        idSucursalSeleccionada = sucursalIds.get(pos);
        Sucursal s = dao.obtenerPorId(idSucursalSeleccionada);
        if (s != null) {
            etNombre.setText(s.getNombreSucursal());
            etTelefono.setText(s.getTelefonoSucursal());
            etDireccion.setText(s.getDireccionSucursal());
            etHoraApertura.setText(s.getHorarioApertura());
            etHoraCierre.setText(s.getHorarioCierre());
            switchActivo.setChecked(s.getActivoSucursal() == 1);

            int posDep = departamentoIds.indexOf(s.getIdDepartamento());
            if (posDep >= 0) {
                spDepartamento.setSelection(posDep + 1);
                cargarSpinnerMunicipio(s.getIdDepartamento());
            }

            int posMun = municipioIds.indexOf(s.getIdMunicipio());
            if (posMun >= 0) spMunicipio.setSelection(posMun + 1);

            cargarSpinnerDistrito(s.getIdDepartamento(), s.getIdMunicipio());

            int posDist = distritoIds.indexOf(s.getIdDistrito());
            if (posDist >= 0) spDistrito.setSelection(posDist + 1);

            setFormularioEnabled(true);
        } else {
            Toast.makeText(this, getString(R.string.sucursal_editar_toast_no_encontrada), Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarSucursal() {
        if (idSucursalSeleccionada == -1) {
            Toast.makeText(this, getString(R.string.sucursal_editar_toast_no_busqueda), Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String horaA = etHoraApertura.getText().toString();
        String horaC = etHoraCierre.getText().toString();

        if (nombre.isEmpty() || telefono.isEmpty() || direccion.isEmpty() ||
                spDepartamento.getSelectedItemPosition() == 0 ||
                spMunicipio.getSelectedItemPosition() == 0 ||
                spDistrito.getSelectedItemPosition() == 0 ||
                horaA.isEmpty() || horaC.isEmpty()) {
            Toast.makeText(this, getString(R.string.sucursal_editar_toast_campos), Toast.LENGTH_SHORT).show();
            return;
        }

        int activo = switchActivo.isChecked() ? 1 : 0;
        Sucursal s = new Sucursal(
                idSucursalSeleccionada,
                departamentoIds.get(spDepartamento.getSelectedItemPosition() - 1),
                municipioIds.get(spMunicipio.getSelectedItemPosition() - 1),
                distritoIds.get(spDistrito.getSelectedItemPosition() - 1),
                nombre, direccion, telefono, horaA, horaC, activo
        );

        dao.actualizar(s);
        Toast.makeText(this, getString(R.string.sucursal_editar_toast_actualizado), Toast.LENGTH_LONG).show();
        finish();
    }

    private void mostrarTimePicker(EditText campoHora) {
        Calendar c = Calendar.getInstance();
        int h = c.get(Calendar.HOUR_OF_DAY), m = c.get(Calendar.MINUTE);
        new TimePickerDialog(this,
                (view, hourOfDay, minute) -> campoHora.setText(String.format("%02d:%02d", hourOfDay, minute)),
                h, m, true).show();
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
        switchActivo.setChecked(true);
        setFormularioEnabled(false);
        idSucursalSeleccionada = -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}