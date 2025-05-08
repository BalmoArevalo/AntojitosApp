package sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito.Distrito;

public class DistritoConsultarActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerDistrito;
    private TextInputEditText txtDepartamento;
    private TextInputEditText txtMunicipio;
    private TextInputEditText txtCodigoPostal;
    private TextView tvEstado;
    private LinearLayout layoutDetalles;

    private DistritoViewModel distritoViewModel;
    private Map<String, Distrito> distritosMap;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distrito_consultar);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();

        inicializarVistas();
        inicializarViewModel();
        cargarDistritos();

        spinnerDistrito.setOnItemClickListener((parent, view, position, id) -> {
            String distritoSeleccionado = parent.getItemAtPosition(position).toString();
            Distrito distrito = distritosMap.get(distritoSeleccionado);
            if (distrito != null) {
                mostrarDetallesDistrito(distrito);
            }
        });
    }

    private void inicializarVistas() {
        spinnerDistrito = findViewById(R.id.spinnerDistrito);
        txtDepartamento = findViewById(R.id.txtDepartamento);
        txtMunicipio = findViewById(R.id.txtMunicipio);
        txtCodigoPostal = findViewById(R.id.txtCodigoPostal);
        layoutDetalles = findViewById(R.id.layoutDetalles);

        distritosMap = new HashMap<>();
    }

    private void inicializarViewModel() {
        distritoViewModel = new ViewModelProvider(this).get(DistritoViewModel.class);

        distritoViewModel.getMensajeError().observe(this, mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            }
        });

        distritoViewModel.getDistritos().observe(this, this::actualizarListaDistritos);
    }

    private void cargarDistritos() {
        try {
            // Modificar la consulta SQL para obtener solo distritos activos
            String sql = "SELECT d.ID_DEPARTAMENTO, d.ID_MUNICIPIO, d.ID_DISTRITO, " +
                    "d.NOMBRE_DISTRITO, d.CODIGO_POSTAL, d.ACTIVO_DISTRITO, " +
                    "dep.NOMBRE_DEPARTAMENTO, m.NOMBRE_MUNICIPIO " +
                    "FROM DISTRITO d " +
                    "JOIN DEPARTAMENTO dep ON d.ID_DEPARTAMENTO = dep.ID_DEPARTAMENTO " +
                    "JOIN MUNICIPIO m ON d.ID_DEPARTAMENTO = m.ID_DEPARTAMENTO " +
                    "AND d.ID_MUNICIPIO = m.ID_MUNICIPIO " +
                    "WHERE d.ACTIVO_DISTRITO = 1 " + // Filtrar solo activos
                    "AND dep.ACTIVO_DEPARTAMENTO = 1 " + // Asegurar departamento activo
                    "AND m.ACTIVO_MUNICIPIO = 1 " + // Asegurar municipio activo
                    "ORDER BY d.NOMBRE_DISTRITO";

            try (Cursor cursor = db.rawQuery(sql, null)) {
                List<String> nombresDistritos = new ArrayList<>();
                distritosMap.clear();

                while (cursor.moveToNext()) {
                    Distrito distrito = new Distrito();
                    distrito.setIdDepartamento(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DEPARTAMENTO")));
                    distrito.setIdMunicipio(cursor.getInt(cursor.getColumnIndexOrThrow("ID_MUNICIPIO")));
                    distrito.setIdDistrito(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DISTRITO")));
                    distrito.setNombreDistrito(cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_DISTRITO")));
                    distrito.setCodigoPostal(cursor.getString(cursor.getColumnIndexOrThrow("CODIGO_POSTAL")));
                    distrito.setActivoDistrito(1); // Siempre será 1 por la consulta

                    nombresDistritos.add(distrito.getNombreDistrito() + "-" + distrito.getCodigoPostal());
                    distritosMap.put(nombresDistritos.get(nombresDistritos.size() - 1), distrito);
                }

                if (nombresDistritos.isEmpty()) {
                    Toast.makeText(this, "No hay distritos activos disponibles",
                            Toast.LENGTH_SHORT).show();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        nombresDistritos
                );

                spinnerDistrito.setAdapter(adapter);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar los distritos: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDetallesDistrito(Distrito distrito) {
        try {
            // Obtener nombres de departamento y municipio
            String sqlDepartamento = "SELECT NOMBRE_DEPARTAMENTO FROM DEPARTAMENTO " +
                    "WHERE ID_DEPARTAMENTO = ? AND ACTIVO_DEPARTAMENTO = 1";
            String sqlMunicipio = "SELECT NOMBRE_MUNICIPIO FROM MUNICIPIO " +
                    "WHERE ID_DEPARTAMENTO = ? AND ID_MUNICIPIO = ? " +
                    "AND ACTIVO_MUNICIPIO = 1";

            String nombreDepartamento = "";
            String nombreMunicipio = "";

            try (Cursor cursorDep = db.rawQuery(sqlDepartamento,
                    new String[]{String.valueOf(distrito.getIdDepartamento())})) {
                if (cursorDep.moveToFirst()) {
                    nombreDepartamento = cursorDep.getString(0);
                }
            }

            try (Cursor cursorMun = db.rawQuery(sqlMunicipio,
                    new String[]{String.valueOf(distrito.getIdDepartamento()),
                            String.valueOf(distrito.getIdMunicipio())})) {
                if (cursorMun.moveToFirst()) {
                    nombreMunicipio = cursorMun.getString(0);
                }
            }

            txtDepartamento.setText(nombreDepartamento);
            txtMunicipio.setText(nombreMunicipio);
            txtCodigoPostal.setText(distrito.getCodigoPostal());
            layoutDetalles.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Toast.makeText(this, "Error al mostrar detalles: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarListaDistritos(List<Distrito> distritos) {
        List<String> nombresDistritos = new ArrayList<>();
        distritosMap.clear();

        for (Distrito distrito : distritos) {
            if (distrito.getActivoDistrito() == 1) {
                nombresDistritos.add(distrito.getNombreDistrito());
                distritosMap.put(distrito.getNombreDistrito(), distrito);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                nombresDistritos
        );

        spinnerDistrito.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}