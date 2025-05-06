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

public class RepartidorCrearActivity extends AppCompatActivity {

    private EditText etNombreRepartidor, etApellidoRepartidor, etTelefonoRepartidor;
    private Spinner spinnerTipoVehiculo, spinnerDepartamento, spinnerMunicipio, spinnerDistrito;
    private Switch switchDisponibleRepartidor;
    private Button btnGuardarRepartidor, btnLimpiarCampos;

    private DBHelper dbHelper;
    private RepartidorDAO dao;

    private List<Integer> departamentoIds = new ArrayList<>();
    private List<Integer> municipioIds   = new ArrayList<>();
    private List<Integer> distritoIds    = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repartidor_crear);

        // Asociar vistas
        etNombreRepartidor       = findViewById(R.id.editNombreRepartidor);
        etApellidoRepartidor     = findViewById(R.id.editApellidoRepartidor);
        etTelefonoRepartidor     = findViewById(R.id.editTelefonoRepartidor);
        spinnerTipoVehiculo      = findViewById(R.id.spinnerTipoVehiculo);
        switchDisponibleRepartidor = findViewById(R.id.switchDisponibleRepartidor);
        spinnerDepartamento      = findViewById(R.id.spinnerDepartamento);
        spinnerMunicipio         = findViewById(R.id.spinnerMunicipio);
        spinnerDistrito          = findViewById(R.id.spinnerDistrito);
        btnGuardarRepartidor     = findViewById(R.id.btnGuardarRepartidor);
        btnLimpiarCampos         = findViewById(R.id.btnLimpiarCampos);

        // Inicializar DB y DAO
        dbHelper = new DBHelper(this);
        dao      = new RepartidorDAO(dbHelper.getWritableDatabase());

        // Spinner Tipo de Vehículo
        String[] tipos = {"Moto", "Bicicleta", "Auto", "Otro"};
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, tipos);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoVehiculo.setAdapter(tipoAdapter);

        // Cargar departamentos
        cargarSpinnerDepartamento();

        // Cascada Spinner Departamento → Municipio → Distrito
        spinnerDepartamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos > 0) {
                    int depId = departamentoIds.get(pos - 1);
                    cargarSpinnerMunicipio(depId);
                    spinnerMunicipio.setEnabled(true);
                } else {
                    spinnerMunicipio.setEnabled(false);
                    spinnerDistrito.setEnabled(false);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinnerMunicipio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos > 0) {
                    int depId = departamentoIds.get(spinnerDepartamento.getSelectedItemPosition() - 1);
                    int munId = municipioIds.get(pos - 1);
                    cargarSpinnerDistrito(depId, munId);
                    spinnerDistrito.setEnabled(true);
                } else {
                    spinnerDistrito.setEnabled(false);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Botones
        btnGuardarRepartidor.setOnClickListener(v -> guardarRepartidor());
        btnLimpiarCampos.setOnClickListener(v -> limpiarCampos());
    }

    private void cargarSpinnerDepartamento() {
        departamentoIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione...");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT ID_DEPARTAMENTO, NOMBRE_DEPARTAMENTO FROM DEPARTAMENTO ORDER BY NOMBRE_DEPARTAMENTO",
                null);
        while (c.moveToNext()) {
            departamentoIds.add(c.getInt(0));
            nombres.add(c.getString(1));
        }
        c.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartamento.setAdapter(adapter);
    }

    private void cargarSpinnerMunicipio(int idDepartamento) {
        municipioIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione...");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT ID_MUNICIPIO, NOMBRE_MUNICIPIO FROM MUNICIPIO WHERE ID_DEPARTAMENTO = ? ORDER BY NOMBRE_MUNICIPIO",
                new String[]{String.valueOf(idDepartamento)});
        while (c.moveToNext()) {
            municipioIds.add(c.getInt(0));
            nombres.add(c.getString(1));
        }
        c.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMunicipio.setAdapter(adapter);
    }

    private void cargarSpinnerDistrito(int idDepartamento, int idMunicipio) {
        distritoIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione...");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT ID_DISTRITO, NOMBRE_DISTRITO FROM DISTRITO " +
                        "WHERE ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ? ORDER BY NOMBRE_DISTRITO",
                new String[]{String.valueOf(idDepartamento), String.valueOf(idMunicipio)});
        while (c.moveToNext()) {
            distritoIds.add(c.getInt(0));
            nombres.add(c.getString(1));
        }
        c.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrito.setAdapter(adapter);
    }

    private void guardarRepartidor() {
        String nombre   = etNombreRepartidor.getText().toString().trim();
        String apellido = etApellidoRepartidor.getText().toString().trim();
        String telefono = etTelefonoRepartidor.getText().toString().trim();
        boolean disponible = switchDisponibleRepartidor.isChecked();
        int posDept = spinnerDepartamento.getSelectedItemPosition();
        int posMun  = spinnerMunicipio.getSelectedItemPosition();
        int posDist = spinnerDistrito.getSelectedItemPosition();

        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty()
                || posDept == 0 || posMun == 0 || posDist == 0) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construir objeto
        Repartidor r = new Repartidor();
        r.setIdDepartamento(departamentoIds.get(posDept - 1));
        r.setIdMunicipio(municipioIds.get(posMun - 1));
        r.setIdDistrito(distritoIds.get(posDist - 1));
        r.setTipoVehiculo((String) spinnerTipoVehiculo.getSelectedItem());
        r.setDisponible(disponible ? 1 : 0);
        r.setTelefonoRepartidor(telefono);
        r.setNombreRepartidor(nombre);
        r.setApellidoRepartidor(apellido);
        r.setActivoRepartidor(1);

        long id = dao.insertar(r);
        if (id > 0) {
            Toast.makeText(this, "Repartidor creado con ID " + id, Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error al crear repartidor", Toast.LENGTH_LONG).show();
        }
    }

    private void limpiarCampos() {
        etNombreRepartidor.setText("");
        etApellidoRepartidor.setText("");
        etTelefonoRepartidor.setText("");
        spinnerTipoVehiculo.setSelection(0);
        switchDisponibleRepartidor.setChecked(true);
        spinnerDepartamento.setSelection(0);
        spinnerMunicipio.setSelection(0);
        spinnerMunicipio.setEnabled(false);
        spinnerDistrito.setSelection(0);
        spinnerDistrito.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
