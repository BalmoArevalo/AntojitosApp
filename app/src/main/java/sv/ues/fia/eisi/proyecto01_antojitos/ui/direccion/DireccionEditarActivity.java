package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion.Direccion;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion.DireccionDAO;

public class DireccionEditarActivity extends AppCompatActivity {

    private Spinner spCliente, spDireccion, spDepto, spMun, spDist;
    private EditText etDirEsp, etDesc;
    private Button btnActualizar;
    private DBHelper dbHelper;

    private List<Integer> clienteIds = new ArrayList<>();
    private List<Integer> direccionIds = new ArrayList<>();
    private List<Integer> deptoIds = new ArrayList<>();
    private List<Integer> munIds = new ArrayList<>();
    private List<Integer> distIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_editar);

        dbHelper     = new DBHelper(this);
        spCliente    = findViewById(R.id.spinnerCliente);
        spDireccion  = findViewById(R.id.spinnerDireccion);
        etDirEsp     = findViewById(R.id.editDireccionEspecifica);
        etDesc       = findViewById(R.id.editDescripcionDireccion);
        spDepto      = findViewById(R.id.spinnerDepartamento);
        spMun        = findViewById(R.id.spinnerMunicipio);
        spDist       = findViewById(R.id.spinnerDistrito);
        btnActualizar= findViewById(R.id.btnActualizarDireccion);

        // Inicialmente, solo el cliente está habilitado
        spDireccion.setEnabled(false);
        spDepto.setEnabled(false);
        spMun.setEnabled(false);
        spDist.setEnabled(false);

        cargarSpinnerClientes();

        spCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    spDireccion.setEnabled(false);
                    cargarSpinnerPlaceholder(spDireccion, direccionIds);
                    return;
                }
                int idCli = clienteIds.get(pos - 1);
                cargarSpinnerDirecciones(idCli);
                spDireccion.setEnabled(true);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spDireccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    spDepto.setEnabled(false);
                    spMun.setEnabled(false);
                    spDist.setEnabled(false);
                    cargarSpinnerPlaceholder(spDepto, deptoIds);
                    cargarSpinnerPlaceholder(spMun, munIds);
                    cargarSpinnerPlaceholder(spDist, distIds);
                    return;
                }
                int idCli = clienteIds.get(spCliente.getSelectedItemPosition() - 1);
                int idDir = direccionIds.get(pos - 1);
                cargarDatosDireccion(idCli, idDir);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spDepto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                if (pos == 0) {
                    spMun.setEnabled(false);
                    cargarSpinnerPlaceholder(spMun, munIds);
                    return;
                }
                int selDept = deptoIds.get(pos - 1);
                cargarSpinnerFiltrado("MUNICIPIO", "ID_MUNICIPIO", "NOMBRE_MUNICIPIO",
                        "ID_DEPARTAMENTO=" + selDept, spMun, munIds);
                spMun.setEnabled(true);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        spMun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                if (pos == 0) {
                    spDist.setEnabled(false);
                    cargarSpinnerPlaceholder(spDist, distIds);
                    return;
                }
                int selMun = munIds.get(pos - 1);
                int selDep = deptoIds.get(spDepto.getSelectedItemPosition() - 1);
                cargarSpinnerFiltrado("DISTRITO", "ID_DISTRITO", "NOMBRE_DISTRITO",
                        "ID_DEPARTAMENTO=" + selDep + " AND ID_MUNICIPIO=" + selMun,
                        spDist, distIds);
                spDist.setEnabled(true);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        btnActualizar.setOnClickListener(v -> actualizarDireccion());
    }

    private void cargarSpinnerClientes() {
        clienteIds.clear();
        List<String> items = new ArrayList<>();
        items.add("Selecciona...");
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor c = db.rawQuery("SELECT ID_CLIENTE, NOMBRE_CLIENTE || ' ' || APELLIDO_CLIIENTE FROM CLIENTE", null)) {
            if (c.moveToFirst()) {
                do {
                    clienteIds.add(c.getInt(0));
                    items.add(c.getString(1));
                } while (c.moveToNext());
            }
        }
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCliente.setAdapter(a);
    }

    private void cargarSpinnerDirecciones(int idCliente) {
        direccionIds.clear();
        List<String> items = new ArrayList<>();
        items.add("Selecciona...");
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor c = db.rawQuery("SELECT ID_DIRECCION, DIRECCION_ESPECIFICA FROM DIRECCION WHERE ID_CLIENTE=?",
                     new String[]{String.valueOf(idCliente)})) {
            if (c.moveToFirst()) {
                do {
                    direccionIds.add(c.getInt(0));
                    items.add(c.getString(1));
                } while (c.moveToNext());
            }
        }
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDireccion.setAdapter(a);
    }

    private void cargarDatosDireccion(int idCli, int idDir) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        DireccionDAO dao = new DireccionDAO(db);
        Direccion d = dao.consultarPorId(idCli, idDir);
        db.close();
        if (d == null) return;

        etDirEsp.setText(d.getDireccionEspecifica());
        etDesc.setText(d.getDescripcionDireccion());

        cargarSpinnerConPlaceholder("DEPARTAMENTO", "ID_DEPARTAMENTO", "NOMBRE_DEPARTAMENTO", spDepto, deptoIds);
        spDepto.setEnabled(true);
        int idxDep = deptoIds.indexOf(d.getIdDepartamento());
        if (idxDep >= 0) spDepto.setSelection(idxDep + 1);

        cargarSpinnerFiltrado("MUNICIPIO", "ID_MUNICIPIO", "NOMBRE_MUNICIPIO",
                "ID_DEPARTAMENTO=" + d.getIdDepartamento(), spMun, munIds);
        spMun.setEnabled(true);
        int idxMun = munIds.indexOf(d.getIdMunicipio());
        if (idxMun >= 0) spMun.setSelection(idxMun + 1);

        cargarSpinnerFiltrado("DISTRITO", "ID_DISTRITO", "NOMBRE_DISTRITO",
                "ID_DEPARTAMENTO=" + d.getIdDepartamento() + " AND ID_MUNICIPIO=" + d.getIdMunicipio(),
                spDist, distIds);
        spDist.setEnabled(true);
        int idxDist = distIds.indexOf(d.getIdDistrito());
        if (idxDist >= 0) spDist.setSelection(idxDist + 1);
    }

    private void actualizarDireccion() {
        String dirEsp = etDirEsp.getText().toString().trim();
        if (dirEsp.isEmpty()) {
            Toast.makeText(this, "Dirección específica es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }

        int posCli = spCliente.getSelectedItemPosition();
        int posDir = spDireccion.getSelectedItemPosition();
        int posDep = spDepto.getSelectedItemPosition();
        int posMun = spMun.getSelectedItemPosition();
        int posDist = spDist.getSelectedItemPosition();

        if (posCli == 0 || posDir == 0 || posDep == 0 || posMun == 0 || posDist == 0) {
            Toast.makeText(this, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show();
            return;
        }

        Direccion d = new Direccion();
        d.setIdCliente(clienteIds.get(posCli - 1));
        d.setIdDireccion(direccionIds.get(posDir - 1));
        d.setIdDepartamento(deptoIds.get(posDep - 1));
        d.setIdMunicipio(munIds.get(posMun - 1));
        d.setIdDistrito(distIds.get(posDist - 1));
        d.setDireccionEspecifica(dirEsp);
        d.setDescripcionDireccion(etDesc.getText().toString().trim());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DireccionDAO dao = new DireccionDAO(db);
        int rows = dao.actualizar(d);
        db.close();

        if (rows > 0) {
            Toast.makeText(this, "Dirección actualizada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarSpinnerConPlaceholder(String tabla, String idCol, String nomCol, Spinner sp, List<Integer> ids) {
        ids.clear();
        List<String> items = new ArrayList<>();
        items.add("Selecciona...");
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor c = db.rawQuery("SELECT " + idCol + ", " + nomCol + " FROM " + tabla, null)) {
            if (c.moveToFirst()) {
                do {
                    ids.add(c.getInt(0));
                    items.add(c.getString(1));
                } while (c.moveToNext());
            }
        }
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(a);
    }

    private void cargarSpinnerFiltrado(String tabla, String idCol, String nomCol,
                                       String where, Spinner sp, List<Integer> ids) {
        ids.clear();
        List<String> items = new ArrayList<>();
        items.add("Selecciona...");
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor c = db.rawQuery("SELECT " + idCol + ", " + nomCol + " FROM " + tabla + " WHERE " + where, null)) {
            if (c.moveToFirst()) {
                do {
                    ids.add(c.getInt(0));
                    items.add(c.getString(1));
                } while (c.moveToNext());
            }
        }
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(a);
    }

    private void cargarSpinnerPlaceholder(Spinner sp, List<Integer> ids) {
        ids.clear();
        List<String> items = new ArrayList<>();
        items.add("Selecciona...");
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(a);
    }
}
