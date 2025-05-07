package sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito.Distrito;

public class DistritoCrearActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerDepartamento;
    private AutoCompleteTextView spinnerMunicipio;
    private TextInputEditText txtIdDistrito;
    private TextInputEditText txtNombreDistrito;
    private TextInputEditText txtCodigoPostal;
    private MaterialButton btnGuardar;

    private DistritoViewModel distritoViewModel;
    private Map<String, Integer> departamentosMap;
    private Map<String, Integer> municipiosMap;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distrito_crear);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        inicializarVistas();
        inicializarViewModel();
        cargarDepartamentos();

        // Listener para el spinner de departamentos
        spinnerDepartamento.setOnItemClickListener((parent, view, position, id) -> {
            String departamentoSeleccionado = parent.getItemAtPosition(position).toString();
            Integer idDepartamento = departamentosMap.get(departamentoSeleccionado);
            if (idDepartamento != null) {
                cargarMunicipios(idDepartamento);
                spinnerMunicipio.setText(""); // Limpiar selección de municipio
            }
        });

        btnGuardar.setOnClickListener(v -> validarYGuardar());
    }

    private void inicializarVistas() {
        spinnerDepartamento = findViewById(R.id.spinnerDepartamento);
        spinnerMunicipio = findViewById(R.id.spinnerMunicipio);
        txtNombreDistrito = findViewById(R.id.txtNombreDistrito);
        txtCodigoPostal = findViewById(R.id.txtCodigoPostal);
        btnGuardar = findViewById(R.id.btnGuardar);

        departamentosMap = new HashMap<>();
        municipiosMap = new HashMap<>();
    }

    private void inicializarViewModel() {
        distritoViewModel = new ViewModelProvider(this).get(DistritoViewModel.class);

        distritoViewModel.getMensajeError().observe(this, mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            }
        });

        distritoViewModel.getOperacionExitosa().observe(this, exitoso -> {
            if (exitoso != null && exitoso) {
                Toast.makeText(this, "Distrito creado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            }
        });
    }

    private void cargarDepartamentos() {
        try {
            String sql = "SELECT ID_DEPARTAMENTO, NOMBRE_DEPARTAMENTO FROM DEPARTAMENTO " +
                    "WHERE ACTIVO_DEPARTAMENTO = 1 ORDER BY NOMBRE_DEPARTAMENTO";

            List<String> nombresDepartamentos = new ArrayList<>();

            try (Cursor cursor = db.rawQuery(sql, null)) {
                while (cursor.moveToNext()) {
                    int idDepartamento = cursor.getInt(cursor.getColumnIndexOrThrow("ID_DEPARTAMENTO"));
                    String nombreDepartamento = cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_DEPARTAMENTO"));
                    nombresDepartamentos.add(nombreDepartamento);
                    departamentosMap.put(nombreDepartamento, idDepartamento);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    nombresDepartamentos
            );

            spinnerDepartamento.setAdapter(adapter);

        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar departamentos: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarMunicipios(int idDepartamento) {
        try {
            String sql = "SELECT ID_MUNICIPIO, NOMBRE_MUNICIPIO FROM MUNICIPIO " +
                    "WHERE ID_DEPARTAMENTO = ? AND ACTIVO_MUNICIPIO = 1 " +
                    "ORDER BY NOMBRE_MUNICIPIO";

            List<String> nombresMunicipios = new ArrayList<>();
            municipiosMap.clear();

            try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idDepartamento)})) {
                while (cursor.moveToNext()) {
                    int idMunicipio = cursor.getInt(cursor.getColumnIndexOrThrow("ID_MUNICIPIO"));
                    String nombreMunicipio = cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_MUNICIPIO"));
                    nombresMunicipios.add(nombreMunicipio);
                    municipiosMap.put(nombreMunicipio, idMunicipio);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    nombresMunicipios
            );

            spinnerMunicipio.setAdapter(adapter);

        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar municipios: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void validarYGuardar() {
        try {
            // Validar departamento seleccionado
            String departamentoSeleccionado = spinnerDepartamento.getText().toString();
            if (departamentoSeleccionado.isEmpty() || !departamentosMap.containsKey(departamentoSeleccionado)) {
                Toast.makeText(this, "Debe seleccionar un departamento", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar municipio seleccionado
            String municipioSeleccionado = spinnerMunicipio.getText().toString();
            if (municipioSeleccionado.isEmpty() || !municipiosMap.containsKey(municipioSeleccionado)) {
                Toast.makeText(this, "Debe seleccionar un municipio", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar nombre del distrito
            String nombreDistrito = txtNombreDistrito.getText().toString().trim();
            if (TextUtils.isEmpty(nombreDistrito)) {
                Toast.makeText(this, "Debe ingresar el nombre del distrito", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar código postal
            String codigoPostal = txtCodigoPostal.getText().toString().trim();
            if (TextUtils.isEmpty(codigoPostal)) {
                Toast.makeText(this, "Debe ingresar el código postal", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener el siguiente ID disponible para el distrito
            int idDepartamento = departamentosMap.get(departamentoSeleccionado);
            int idMunicipio = municipiosMap.get(municipioSeleccionado);
            int nuevoIdDistrito = obtenerSiguienteIdDistrito(idDepartamento, idMunicipio);

            // Crear objeto Distrito
            Distrito distrito = new Distrito();
            distrito.setIdDepartamento(idDepartamento);
            distrito.setIdMunicipio(idMunicipio);
            distrito.setIdDistrito(nuevoIdDistrito);
            distrito.setNombreDistrito(nombreDistrito);
            distrito.setCodigoPostal(codigoPostal);
            distrito.setActivoDistrito(1);

            // Guardar distrito
            distritoViewModel.agregarDistrito(distrito);

        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar el distrito: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private int obtenerSiguienteIdDistrito(int idDepartamento, int idMunicipio) {
        int siguienteId = 1;
        String sql = "SELECT MAX(ID_DISTRITO) as max_id FROM DISTRITO " +
                "WHERE ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ?";

        try (Cursor cursor = db.rawQuery(sql,
                new String[]{String.valueOf(idDepartamento), String.valueOf(idMunicipio)})) {
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                siguienteId = cursor.getInt(0) + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Si hay error, retornamos 1 como ID por defecto
        }

        return siguienteId;
    }

    private void limpiarCampos() {
        spinnerDepartamento.setText("");
        spinnerMunicipio.setText("");
        txtNombreDistrito.setText("");
        txtCodigoPostal.setText("");
        spinnerDepartamento.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}