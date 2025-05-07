package sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito.Distrito;
public class DistritoEditarActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerDistrito;
    private AutoCompleteTextView spinnerDepartamento;
    private AutoCompleteTextView spinnerMunicipio;
    private TextInputEditText txtNombreDistrito;
    private TextInputEditText txtCodigoPostal;
    private SwitchMaterial switchActivo;
    private MaterialButton btnGuardar;
    private LinearLayout layoutDetalles;

    private DistritoViewModel distritoViewModel;
    private Map<String, Distrito> distritosMap;
    private Map<String, Integer> departamentosMap;
    private Map<String, Integer> municipiosMap;
    private SQLiteDatabase db;
    private Distrito distritoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distrito_editar);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        inicializarVistas();
        inicializarViewModel();
        cargarDistritos();

        // Configurar listeners
        spinnerDistrito.setOnItemClickListener((parent, view, position, id) -> {
            String distritoSeleccionadoNombre = parent.getItemAtPosition(position).toString();
            distritoSeleccionado = distritosMap.get(distritoSeleccionadoNombre);
            if (distritoSeleccionado != null) {
                cargarDetallesDistrito();
            }
        });

        spinnerDepartamento.setOnItemClickListener((parent, view, position, id) -> {
            String departamentoSeleccionado = parent.getItemAtPosition(position).toString();
            Integer idDepartamento = departamentosMap.get(departamentoSeleccionado);
            if (idDepartamento != null) {
                cargarMunicipios(idDepartamento);
                spinnerMunicipio.setText("");
            }
        });

        btnGuardar.setOnClickListener(v -> validarYGuardar());
    }

    private void inicializarVistas() {
        spinnerDistrito = findViewById(R.id.spinnerDistrito);
        spinnerDepartamento = findViewById(R.id.spinnerDepartamento);
        spinnerMunicipio = findViewById(R.id.spinnerMunicipio);
        txtNombreDistrito = findViewById(R.id.txtNombreDistrito);
        txtCodigoPostal = findViewById(R.id.txtCodigoPostal);
        switchActivo = findViewById(R.id.switchActivo);
        btnGuardar = findViewById(R.id.btnGuardar);
        layoutDetalles = findViewById(R.id.layoutDetalles);

        distritosMap = new HashMap<>();
        departamentosMap = new HashMap<>();
        municipiosMap = new HashMap<>();
    }

    private void cargarDistritos() {
        try {
            String sql = "SELECT d.*, dep.NOMBRE_DEPARTAMENTO, m.NOMBRE_MUNICIPIO " +
                    "FROM DISTRITO d " +
                    "JOIN DEPARTAMENTO dep ON d.ID_DEPARTAMENTO = dep.ID_DEPARTAMENTO " +
                    "JOIN MUNICIPIO m ON d.ID_DEPARTAMENTO = m.ID_DEPARTAMENTO " +
                    "AND d.ID_MUNICIPIO = m.ID_MUNICIPIO " +
                    "ORDER BY d.NOMBRE_DISTRITO";

            List<String> nombresDistritos = new ArrayList<>();
            try (Cursor cursor = db.rawQuery(sql, null)) {
                while (cursor.moveToNext()) {
                    Distrito distrito = new Distrito();
                    distrito.setIdDepartamento(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DEPARTAMENTO")));
                    distrito.setIdMunicipio(cursor.getInt(cursor.getColumnIndexOrThrow("ID_MUNICIPIO")));
                    distrito.setIdDistrito(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DISTRITO")));
                    distrito.setNombreDistrito(cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_DISTRITO")));
                    distrito.setCodigoPostal(cursor.getString(cursor.getColumnIndexOrThrow("CODIGO_POSTAL")));
                    distrito.setActivoDistrito(cursor.getInt(cursor.getColumnIndexOrThrow("ACTIVO_DISTRITO")));

                    String nombreMostrar = distrito.getNombreDistrito() +
                            (distrito.getActivoDistrito() == 1 ? "(Activo)" : " (Inactivo)");
                    nombresDistritos.add(nombreMostrar);
                    distritosMap.put(nombreMostrar, distrito);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    nombresDistritos
            );
            spinnerDistrito.setAdapter(adapter);

        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar los distritos: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarDetallesDistrito() {
        try {
            cargarDepartamentos();

            // Establecer valores actuales
            String nombreDepartamento = obtenerNombreDepartamento(distritoSeleccionado.getIdDepartamento());
            spinnerDepartamento.setText(nombreDepartamento, false);

            cargarMunicipios(distritoSeleccionado.getIdDepartamento());
            String nombreMunicipio = obtenerNombreMunicipio(distritoSeleccionado.getIdDepartamento(),
                    distritoSeleccionado.getIdMunicipio());
            spinnerMunicipio.setText(nombreMunicipio, false);

            txtNombreDistrito.setText(distritoSeleccionado.getNombreDistrito());
            txtCodigoPostal.setText(distritoSeleccionado.getCodigoPostal());
            switchActivo.setChecked(distritoSeleccionado.getActivoDistrito() == 1);

            layoutDetalles.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar detalles: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarDepartamentos() {
        try {
            String sql = "SELECT ID_DEPARTAMENTO, NOMBRE_DEPARTAMENTO FROM DEPARTAMENTO " +
                    "WHERE ACTIVO_DEPARTAMENTO = 1 ORDER BY NOMBRE_DEPARTAMENTO";

            List<String> nombresDepartamentos = new ArrayList<>();
            departamentosMap.clear();

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

    private String obtenerNombreDepartamento(int idDepartamento) {
        String sql = "SELECT NOMBRE_DEPARTAMENTO FROM DEPARTAMENTO WHERE ID_DEPARTAMENTO = ?";
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idDepartamento)})) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        }
        return "";
    }

    private String obtenerNombreMunicipio(int idDepartamento, int idMunicipio) {
        String sql = "SELECT NOMBRE_MUNICIPIO FROM MUNICIPIO " +
                "WHERE ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ?";
        try (Cursor cursor = db.rawQuery(sql,
                new String[]{String.valueOf(idDepartamento), String.valueOf(idMunicipio)})) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        }
        return "";
    }

    private void validarYGuardar() {
        try {
            if (distritoSeleccionado == null) {
                Toast.makeText(this, "Debe seleccionar un distrito", Toast.LENGTH_SHORT).show();
                return;
            }

            String departamentoSeleccionado = spinnerDepartamento.getText().toString();
            if (departamentoSeleccionado.isEmpty() || !departamentosMap.containsKey(departamentoSeleccionado)) {
                Toast.makeText(this, "Debe seleccionar un departamento", Toast.LENGTH_SHORT).show();
                return;
            }

            String municipioSeleccionado = spinnerMunicipio.getText().toString();
            if (municipioSeleccionado.isEmpty() || !municipiosMap.containsKey(municipioSeleccionado)) {
                Toast.makeText(this, "Debe seleccionar un municipio", Toast.LENGTH_SHORT).show();
                return;
            }

            String nombreDistrito = txtNombreDistrito.getText().toString().trim();
            if (TextUtils.isEmpty(nombreDistrito)) {
                Toast.makeText(this, "Debe ingresar el nombre del distrito", Toast.LENGTH_SHORT).show();
                return;
            }

            String codigoPostal = txtCodigoPostal.getText().toString().trim();
            if (TextUtils.isEmpty(codigoPostal)) {
                Toast.makeText(this, "Debe ingresar el código postal", Toast.LENGTH_SHORT).show();
                return;
            }

            // Actualizar objeto Distrito
            distritoSeleccionado.setIdDepartamento(departamentosMap.get(departamentoSeleccionado));
            distritoSeleccionado.setIdMunicipio(municipiosMap.get(municipioSeleccionado));
            distritoSeleccionado.setNombreDistrito(nombreDistrito);
            distritoSeleccionado.setCodigoPostal(codigoPostal);
            distritoSeleccionado.setActivoDistrito(switchActivo.isChecked() ? 1 : 0);

            // Guardar cambios
            distritoViewModel.actualizarDistrito(distritoSeleccionado);

        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar cambios: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void inicializarViewModel() {
        distritoViewModel = new ViewModelProvider(this).get(DistritoViewModel.class);

        // Observer para mensajes de error
        distritoViewModel.getMensajeError().observe(this, mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            }
        });

        // Observer para operaciones exitosas
        distritoViewModel.getOperacionExitosa().observe(this, exitoso -> {
            if (exitoso != null && exitoso) {
                Toast.makeText(this, "Cambios guardados exitosamente", Toast.LENGTH_SHORT).show();
                // Recargar la lista de distritos para reflejar los cambios
                cargarDistritos();
                // Limpiar la selección actual
                spinnerDistrito.setText("");
                layoutDetalles.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}