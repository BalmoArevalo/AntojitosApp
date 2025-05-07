package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DireccionCrearActivity extends AppCompatActivity {

    private static final String TAG = "DireccionCrearActivity";

    private EditText etDirEsp, etDesc;
    private Spinner spCliente, spDepto, spMun, spDist;
    private Button btnGuardar;
    private DBHelper dbHelper;

    private List<Integer> clienteIds = new ArrayList<>();
    private List<Integer> deptoIds   = new ArrayList<>();
    private List<Integer> munIds     = new ArrayList<>();
    private List<Integer> distIds    = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_crear);

        // Usar nuevo nombre de string para el título
        setTitle(getString(R.string.direccion_crear_title));

        Log.d(TAG, "onCreate: Inicializando vistas y helper.");
        dbHelper = new DBHelper(this);
        etDirEsp  = findViewById(R.id.editDireccionEspecifica);
        etDesc    = findViewById(R.id.editDescripcionDireccion);
        spCliente = findViewById(R.id.spinnerCliente);
        spDepto   = findViewById(R.id.spinnerDepartamento);
        spMun     = findViewById(R.id.spinnerMunicipio);
        spDist    = findViewById(R.id.spinnerDistrito);
        btnGuardar= findViewById(R.id.btnGuardar);

        Log.d(TAG, "Cargando spinners...");
        cargarSpinnerConPlaceholder(
                "CLIENTE", "ID_CLIENTE", "NOMBRE_CLIENTE || ' ' || APELLIDO_CLIENTE",
                spCliente, clienteIds);

        cargarSpinnerConPlaceholder(
                "DEPARTAMENTO", "ID_DEPARTAMENTO", "NOMBRE_DEPARTAMENTO",
                spDepto, deptoIds);

        // --- Configurar Listeners (sin cambios en referencias a strings) ---
        spDepto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int selDept = -1;
                if (pos >= 0 && pos < deptoIds.size()){
                    selDept = deptoIds.get(pos);
                }
                Log.d(TAG, "Departamento seleccionado: Pos=" + pos + ", ID=" + selDept);
                if (selDept >= 0) {
                    spMun.setEnabled(true);
                    cargarSpinnerFiltrado(
                            "MUNICIPIO", "ID_MUNICIPIO", "NOMBRE_MUNICIPIO",
                            "ID_DEPARTAMENTO = " + selDept,
                            spMun, munIds);
                    spDist.setSelection(0);
                    spDist.setEnabled(false);
                    if(spMun.getAdapter() == null || spMun.getAdapter().getCount() <= 1){
                        spDist.setAdapter(null);
                    }
                } else {
                    spMun.setSelection(0);
                    spDist.setSelection(0);
                    spMun.setEnabled(false);
                    spDist.setEnabled(false);
                    spMun.setAdapter(null);
                    spDist.setAdapter(null);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                spMun.setEnabled(false);
                spDist.setEnabled(false);
                spMun.setAdapter(null);
                spDist.setAdapter(null);
            }
        });

        spMun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int posDepto = spDepto.getSelectedItemPosition();
                int selMun = -1;
                if (pos >= 0 && pos < munIds.size()){
                    selMun = munIds.get(pos);
                }
                Log.d(TAG, "Municipio seleccionado: Pos=" + pos + ", ID=" + selMun);
                if (posDepto > 0 && selMun >= 0) {
                    spDist.setEnabled(true);
                    String where = "ID_DEPARTAMENTO = " + deptoIds.get(posDepto)
                            + " AND ID_MUNICIPIO = " + selMun;
                    cargarSpinnerFiltrado(
                            "DISTRITO", "ID_DISTRITO", "NOMBRE_DISTRITO",
                            where, spDist, distIds);
                } else {
                    spDist.setSelection(0);
                    spDist.setEnabled(false);
                    spDist.setAdapter(null);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                spDist.setEnabled(false);
                spDist.setAdapter(null);
            }
        });

        spMun.setEnabled(false);
        spDist.setEnabled(false);

        btnGuardar.setOnClickListener(v -> guardar());
        Log.d(TAG, "Inicialización completa.");
    }

    private void cargarSpinnerConPlaceholder(
            String tabla, String campoId, String campoNom,
            Spinner spinner, List<Integer> idList) {
        Log.d(TAG, "Cargando Spinner para tabla: " + tabla);
        idList.clear();
        List<String> nombres = new ArrayList<>();
        // Usar el string resource para el placeholder
        nombres.add(getString(R.string.placeholder_seleccione));
        idList.add(-1);

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT " + campoId + ", " + campoNom + " FROM " + tabla + " ORDER BY " + campoNom + " ASC";
            Log.d(TAG, "Ejecutando query: " + query);
            c = db.rawQuery(query, null);
            while (c.moveToNext()) {
                idList.add(c.getInt(0));
                nombres.add(c.getString(1));
            }
            Log.d(TAG, "Cargados " + (nombres.size()-1) + " items para " + tabla);
        } catch (SQLiteException ex) {
            Log.e(TAG, "Error al cargar spinner " + tabla, ex);
            // Usar el string resource para el Toast de error de carga
            Toast.makeText(this, String.format(getString(R.string.direccion_crear_toast_error_carga), tabla), Toast.LENGTH_SHORT).show();
        } finally {
            if (c != null) c.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void cargarSpinnerFiltrado(
            String tabla, String campoId, String campoNom,
            String whereClause, Spinner spinner, List<Integer> idList) {
        Log.d(TAG, "Cargando Spinner Filtrado para tabla: " + tabla + " con WHERE: " + whereClause);
        idList.clear();
        List<String> nombres = new ArrayList<>();
        // Usar el string resource para el placeholder
        nombres.add(getString(R.string.placeholder_seleccione));
        idList.add(-1);

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT " + campoId + ", " + campoNom
                    + " FROM " + tabla
                    + " WHERE " + whereClause
                    + " ORDER BY " + campoNom + " ASC";
            Log.d(TAG, "Ejecutando query filtrada: " + query);
            c = db.rawQuery(query, null);
            while (c.moveToNext()) {
                idList.add(c.getInt(0));
                nombres.add(c.getString(1));
            }
            Log.d(TAG, "Cargados " + (nombres.size()-1) + " items filtrados para " + tabla);
        } catch (SQLiteException ex) {
            Log.e(TAG, "Error al cargar spinner filtrado " + tabla, ex);
            // Usar el string resource para el Toast de error de carga
            Toast.makeText(this, String.format(getString(R.string.direccion_crear_toast_error_carga), tabla), Toast.LENGTH_SHORT).show();
        } finally {
            if (c != null) c.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setEnabled(adapter.getCount() > 1);
        if(adapter.getCount() <= 1) {
            if(spinner == spMun) {
                spDist.setAdapter(null);
                spDist.setEnabled(false);
            }
            Log.w(TAG,"No se encontraron datos filtrados para " + tabla + " con WHERE " + whereClause);
        }
    }

    private void guardar() {
        Log.d(TAG, "Intentando guardar dirección...");

        // --- Validación (Usando nuevos nombres de strings para Toasts) ---
        String dirEsp = etDirEsp.getText().toString().trim();
        if (dirEsp.isEmpty()) {
            Toast.makeText(this, R.string.direccion_crear_toast_error_especifica, Toast.LENGTH_SHORT).show();
            etDirEsp.requestFocus();
            return;
        }

        int posCli = spCliente.getSelectedItemPosition();
        if (posCli <= 0) {
            Toast.makeText(this, R.string.direccion_crear_toast_error_cliente, Toast.LENGTH_SHORT).show();
            return;
        }
        int idCliente = clienteIds.get(posCli);

        int posDep = spDepto.getSelectedItemPosition();
        if (posDep <= 0) {
            Toast.makeText(this, R.string.direccion_crear_toast_error_departamento, Toast.LENGTH_SHORT).show();
            return;
        }
        int idDep = deptoIds.get(posDep);

        int posMun = spMun.getSelectedItemPosition();
        if (posMun <= 0 || !spMun.isEnabled()) {
            Toast.makeText(this, R.string.direccion_crear_toast_error_municipio, Toast.LENGTH_SHORT).show();
            return;
        }
        int idMun = munIds.get(posMun);

        int posDist = spDist.getSelectedItemPosition();
        if (posDist <= 0 || !spDist.isEnabled()) {
            Toast.makeText(this, R.string.direccion_crear_toast_error_distrito, Toast.LENGTH_SHORT).show();
            return;
        }
        int idDist = distIds.get(posDist);

        String desc = etDesc.getText().toString().trim();

        Log.d(TAG, "Datos validados: Cliente=" + idCliente + ", Depto=" + idDep + ", Mun=" + idMun + ", Dist=" + idDist);

        // --- Creación e Inserción ---
        SQLiteDatabase db = null;
        long res = -1;
        int nextId = -1;

        try {
            db = dbHelper.getWritableDatabase();
            DireccionDAO dao = new DireccionDAO(db);
            nextId = dao.getNextIdDireccion(idCliente);

            Direccion dir = new Direccion();
            dir.setIdCliente(idCliente);
            dir.setIdDireccion(nextId);
            dir.setIdDepartamento(idDep);
            dir.setIdMunicipio(idMun);
            dir.setIdDistrito(idDist);
            dir.setDireccionEspecifica(dirEsp);
            dir.setDescripcionDireccion(desc);
            dir.setActivoDireccion(1); // Asegurarse que se setea como activo

            Log.d(TAG, "Objeto Dirección a insertar: " + dir.toString());
            res = dao.insertar(dir);

        } catch (SQLiteException e) {
            Log.e(TAG, "Error de base de datos al guardar dirección", e);
            // Usar el string resource para el Toast de error al guardar
            Toast.makeText(this, getString(R.string.direccion_crear_toast_error_guardar), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error inesperado al guardar dirección", e);
            Toast.makeText(this, getString(R.string.direccion_crear_toast_error_guardar), Toast.LENGTH_LONG).show();
        }
        finally {
            // No cerrar DB
        }

        // --- Feedback Final (Usando nuevo nombre de string para Toast) ---
        if (res != -1 && nextId != -1) {
            Toast.makeText(this,
                    String.format(getString(R.string.direccion_crear_toast_exito), nextId),
                    Toast.LENGTH_LONG).show();
            finish();
        } else {
            Log.e(TAG, "La inserción devolvió -1 o nextId fue -1 (error).");
        }
    }
}