package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
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

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DireccionCrearActivity extends AppCompatActivity {

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

        dbHelper = new DBHelper(this);
        etDirEsp  = findViewById(R.id.editDireccionEspecifica);
        etDesc    = findViewById(R.id.editDescripcionDireccion);
        spCliente = findViewById(R.id.spinnerCliente);
        spDepto   = findViewById(R.id.spinnerDepartamento);
        spMun     = findViewById(R.id.spinnerMunicipio);
        spDist    = findViewById(R.id.spinnerDistrito);
        btnGuardar= findViewById(R.id.btnGuardar);

        cargarSpinnerConPlaceholder(
                "CLIENTE", "ID_CLIENTE", "NOMBRE_CLIENTE || ' ' || APELLIDO_CLIIENTE",
                spCliente, clienteIds);

        cargarSpinnerConPlaceholder(
                "DEPARTAMENTO", "ID_DEPARTAMENTO", "NOMBRE_DEPARTAMENTO",
                spDepto, deptoIds);
        spDepto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int selDept = deptoIds.get(pos);
                if (selDept >= 0) {
                    spMun.setEnabled(true);
                    cargarSpinnerFiltrado(
                            "MUNICIPIO", "ID_MUNICIPIO", "NOMBRE_MUNICIPIO",
                            "ID_DEPARTAMENTO = " + selDept,
                            spMun, munIds);
                } else {
                    spMun.setEnabled(false);
                    spDist.setEnabled(false);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spMun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int selMun = munIds.get(pos);
                if (selMun >= 0) {
                    spDist.setEnabled(true);
                    String where = "ID_DEPARTAMENTO = " + deptoIds.get(spDepto.getSelectedItemPosition())
                            + " AND ID_MUNICIPIO = " + selMun;
                    cargarSpinnerFiltrado(
                            "DISTRITO", "ID_DISTRITO", "NOMBRE_DISTRITO",
                            where, spDist, distIds);
                } else {
                    spDist.setEnabled(false);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void cargarSpinnerConPlaceholder(
            String tabla, String campoId, String campoNom,
            Spinner spinner, List<Integer> idList) {

        idList.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione...");
        idList.add(-1);

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbHelper.getReadableDatabase();
            c = db.rawQuery(
                    "SELECT " + campoId + ", " + campoNom + " FROM " + tabla,
                    null);
            while (c.moveToNext()) {
                idList.add(c.getInt(0));
                nombres.add(c.getString(1));
            }
        } catch (SQLiteException ex) {
            Toast.makeText(this,
                    "BD no creada, usando datos de ejemplo", Toast.LENGTH_SHORT).show();
        } finally {
            if (c != null) c.close();
            if (db!=null) db.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void cargarSpinnerFiltrado(
            String tabla, String campoId, String campoNom,
            String whereClause, Spinner spinner, List<Integer> idList) {

        idList.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione...");
        idList.add(-1);

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbHelper.getReadableDatabase();
            c = db.rawQuery(
                    "SELECT " + campoId + ", " + campoNom
                            + " FROM " + tabla
                            + " WHERE " + whereClause,
                    null);
            while (c.moveToNext()) {
                idList.add(c.getInt(0));
                nombres.add(c.getString(1));
            }
        } catch (SQLiteException ex) {
            Toast.makeText(this,
                    "BD no creada, usando datos de ejemplo", Toast.LENGTH_SHORT).show();
        } finally {
            if (c != null) c.close();
            if (db!=null) db.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void guardar() {
        String dirEsp = etDirEsp.getText().toString().trim();
        if (dirEsp.isEmpty()) {
            Toast.makeText(this,
                    "La dirección específica es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }
        int posCli = spCliente.getSelectedItemPosition();
        int idCliente = clienteIds.get(posCli);
        if (idCliente < 0) {
            Toast.makeText(this,
                    "Selecciona un cliente", Toast.LENGTH_SHORT).show();
            return;
        }
        int posDep = spDepto.getSelectedItemPosition();
        int idDep = deptoIds.get(posDep);
        if (idDep < 0) {
            Toast.makeText(this,
                    "Selecciona un departamento", Toast.LENGTH_SHORT).show();
            return;
        }
        int posMun = spMun.getSelectedItemPosition();
        int idMun = munIds.get(posMun);
        if (idMun < 0) {
            Toast.makeText(this,
                    "Selecciona un municipio", Toast.LENGTH_SHORT).show();
            return;
        }
        int posDist = spDist.getSelectedItemPosition();
        int idDist = distIds.get(posDist);
        if (idDist < 0) {
            Toast.makeText(this,
                    "Selecciona un distrito", Toast.LENGTH_SHORT).show();
            return;
        }

        // Uso del DAO para siguiente ID y guardar
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DireccionDAO dao = new DireccionDAO(db);
        int nextId = dao.getNextIdDireccion(idCliente);
        Direccion dir = new Direccion();
        dir.setIdCliente(idCliente);
        dir.setIdDireccion(nextId);
        dir.setIdDepartamento(idDep);
        dir.setIdMunicipio(idMun);
        dir.setIdDistrito(idDist);
        dir.setDireccionEspecifica(dirEsp);
        dir.setDescripcionDireccion(etDesc.getText().toString().trim());
        long res = dao.insertar(dir);
        db.close();

        if (res != -1) {
            Toast.makeText(this,
                    "Dirección guardada con ID " + nextId,
                    Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this,
                    "Error al guardar en la base de datos",
                    Toast.LENGTH_LONG).show();
        }
    }
}