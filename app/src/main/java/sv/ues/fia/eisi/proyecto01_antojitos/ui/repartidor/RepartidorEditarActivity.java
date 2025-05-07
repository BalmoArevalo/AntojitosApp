package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class RepartidorEditarActivity extends AppCompatActivity {

    private Spinner spinnerRepartidor;
    private Button btnBuscar;

    private EditText etNombreRepartidor;
    private EditText etApellidoRepartidor;
    private EditText etTelefonoRepartidor;
    private Spinner spTipoVehiculo;
    private Spinner spDepartamento;
    private Spinner spMunicipio;
    private Spinner spDistrito;
    private Switch switchActivoRepartidor;
    private Button btnActualizarRepartidor;
    private Button btnLimpiarCampos;

    private DBHelper dbHelper;
    private RepartidorDAO dao;

    private List<Integer> repartidorIds = new ArrayList<>();
    private List<Integer> departamentoIds = new ArrayList<>();
    private List<Integer> municipioIds = new ArrayList<>();
    private List<Integer> distritoIds = new ArrayList<>();

    private int idRepartidorSeleccionado = -1;
    private int disponibleActual = 1;  // almacenará el valor original de 'disponible'

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repartidor_editar);

        // Inicializar vistas
        spinnerRepartidor       = findViewById(R.id.spinnerRepartidor);
        btnBuscar               = findViewById(R.id.btnBuscar);
        etNombreRepartidor      = findViewById(R.id.editNombreRepartidor);
        etApellidoRepartidor    = findViewById(R.id.editApellidoRepartidor);
        etTelefonoRepartidor    = findViewById(R.id.editTelefonoRepartidor);
        spTipoVehiculo          = findViewById(R.id.spinnerTipoVehiculo);
        spDepartamento          = findViewById(R.id.spinnerDepartamento);
        spMunicipio             = findViewById(R.id.spinnerMunicipio);
        spDistrito              = findViewById(R.id.spinnerDistrito);
        switchActivoRepartidor  = findViewById(R.id.switchActivoRepartidor);
        btnActualizarRepartidor = findViewById(R.id.btnActualizarRepartidor);
        btnLimpiarCampos        = findViewById(R.id.btnLimpiarCampos);

        dbHelper = new DBHelper(this);
        dao = new RepartidorDAO(dbHelper.getWritableDatabase());

        // Spinner repartidores (activos e inactivos)
        cargarSpinnerRepartidor();

        // Spinner tipo de vehículo
        String[] tipos = {"Moto", "Bicicleta", "Auto", "Otro"};
        spTipoVehiculo.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, tipos));

        // Spinner departamentos
        cargarSpinnerDepartamento();
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

        // Listeners
        btnBuscar.setOnClickListener(v -> buscarRepartidor());
        btnActualizarRepartidor.setOnClickListener(v -> actualizarRepartidor());
        btnLimpiarCampos.setOnClickListener(v -> limpiarCampos());

        setFormularioEnabled(false);
    }

    private void setFormularioEnabled(boolean enabled) {
        etNombreRepartidor.setEnabled(enabled);
        etApellidoRepartidor.setEnabled(enabled);
        etTelefonoRepartidor.setEnabled(enabled);
        spTipoVehiculo.setEnabled(enabled);
        spDepartamento.setEnabled(enabled);
        spMunicipio.setEnabled(enabled && spMunicipio.isEnabled());
        spDistrito.setEnabled(enabled && spDistrito.isEnabled());
        switchActivoRepartidor.setEnabled(enabled);
        btnActualizarRepartidor.setEnabled(enabled);
        btnLimpiarCampos.setEnabled(enabled);
    }

    private void cargarSpinnerRepartidor() {
        repartidorIds.clear();
        List<String> items = new ArrayList<>();
        items.add("Seleccione...");
        repartidorIds.add(-1);

        List<Repartidor> list = dao.obtenerTodos();
        for (Repartidor r : list) {
            repartidorIds.add(r.getIdRepartidor());
            String estado = r.getActivoRepartidor() == 1 ? "(Activo)" : "(Inactivo)";
            items.add(r.getIdRepartidor() + " - " + r.getNombreRepartidor() + " " + r.getApellidoRepartidor() + " " + estado);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepartidor.setAdapter(adapter);
    }

    private void cargarSpinnerDepartamento() {
        departamentoIds.clear();
        List<String> names = new ArrayList<>(); names.add("Seleccione...");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT ID_DEPARTAMENTO, NOMBRE_DEPARTAMENTO FROM DEPARTAMENTO", null);
        while (c.moveToNext()) {
            departamentoIds.add(c.getInt(0));
            names.add(c.getString(1));
        }
        c.close();
        spDepartamento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names));
    }

    private void cargarSpinnerMunicipio(int idDep) {
        municipioIds.clear();
        List<String> names = new ArrayList<>(); names.add("Seleccione...");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT ID_MUNICIPIO, NOMBRE_MUNICIPIO FROM MUNICIPIO WHERE ID_DEPARTAMENTO = ?",
                new String[]{String.valueOf(idDep)});
        while (c.moveToNext()) {
            municipioIds.add(c.getInt(0));
            names.add(c.getString(1));
        }
        c.close();
        spMunicipio.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names));
    }

    private void cargarSpinnerDistrito(int idDep, int idMun) {
        distritoIds.clear();
        List<String> names = new ArrayList<>(); names.add("Seleccione...");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT ID_DISTRITO, NOMBRE_DISTRITO FROM DISTRITO WHERE ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ?",
                new String[]{String.valueOf(idDep), String.valueOf(idMun)});
        while (c.moveToNext()) {
            distritoIds.add(c.getInt(0));
            names.add(c.getString(1));
        }
        c.close();
        spDistrito.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names));
    }

    private void buscarRepartidor() {
        int pos = spinnerRepartidor.getSelectedItemPosition();
        if (pos <= 0) {
            Toast.makeText(this, "Selecciona un repartidor válido", Toast.LENGTH_SHORT).show();
            return;
        }
        idRepartidorSeleccionado = repartidorIds.get(pos);
        Repartidor r = dao.obtenerPorId(idRepartidorSeleccionado);
        if (r != null) {
            etNombreRepartidor.setText(r.getNombreRepartidor());
            etApellidoRepartidor.setText(r.getApellidoRepartidor());
            etTelefonoRepartidor.setText(r.getTelefonoRepartidor());
            disponibleActual = r.getDisponible();
            // Tipo vehículo
            ArrayAdapter adapter = (ArrayAdapter) spTipoVehiculo.getAdapter();
            int tipoPos = adapter.getPosition(r.getTipoVehiculo());
            if (tipoPos >= 0) spTipoVehiculo.setSelection(tipoPos);
            // Departamento
            int depPos = departamentoIds.indexOf(r.getIdDepartamento());
            if (depPos >= 0) {
                spDepartamento.setSelection(depPos + 1);
                cargarSpinnerMunicipio(r.getIdDepartamento());
            }
            // Municipio
            int munPos = municipioIds.indexOf(r.getIdMunicipio());
            if (munPos >= 0) spMunicipio.setSelection(munPos + 1);
            // Distrito
            cargarSpinnerDistrito(r.getIdDepartamento(), r.getIdMunicipio());
            int distPos = distritoIds.indexOf(r.getIdDistrito());
            if (distPos >= 0) spDistrito.setSelection(distPos + 1);
            // Activo
            switchActivoRepartidor.setChecked(r.getActivoRepartidor() == 1);
            setFormularioEnabled(true);
        } else {
            Toast.makeText(this, "Repartidor no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarRepartidor() {
        if (idRepartidorSeleccionado < 0) {
            Toast.makeText(this, "Busca primero un repartidor", Toast.LENGTH_SHORT).show();
            return;
        }
        String nombre = etNombreRepartidor.getText().toString().trim();
        String apellido = etApellidoRepartidor.getText().toString().trim();
        String telefono = etTelefonoRepartidor.getText().toString().trim();
        int tipoPos = spTipoVehiculo.getSelectedItemPosition();
        int depPos = spDepartamento.getSelectedItemPosition();
        int munPos = spMunicipio.getSelectedItemPosition();
        int distPos = spDistrito.getSelectedItemPosition();
        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty()
                || tipoPos < 0 || depPos == 0 || munPos == 0 || distPos == 0) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        Repartidor r = new Repartidor(
                idRepartidorSeleccionado,
                departamentoIds.get(depPos - 1),
                municipioIds.get(munPos - 1),
                distritoIds.get(distPos - 1),
                spTipoVehiculo.getSelectedItem().toString(),
                disponibleActual,
                telefono,
                nombre,
                apellido,
                switchActivoRepartidor.isChecked() ? 1 : 0
        );
        dao.actualizar(r);
        Toast.makeText(this, "Repartidor actualizado correctamente", Toast.LENGTH_LONG).show();
        finish();
    }

    private void limpiarCampos() {
        spinnerRepartidor.setSelection(0);
        etNombreRepartidor.setText("");
        etApellidoRepartidor.setText("");
        etTelefonoRepartidor.setText("");
        spTipoVehiculo.setSelection(0);
        spDepartamento.setSelection(0);
        spMunicipio.setSelection(0);
        spMunicipio.setEnabled(false);
        spDistrito.setSelection(0);
        spDistrito.setEnabled(false);
        switchActivoRepartidor.setChecked(true);
        setFormularioEnabled(false);
        idRepartidorSeleccionado = -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}