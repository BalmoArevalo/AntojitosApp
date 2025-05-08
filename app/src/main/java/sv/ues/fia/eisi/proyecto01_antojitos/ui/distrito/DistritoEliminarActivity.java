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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito.Distrito;


public class DistritoEliminarActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerDistrito;
    private TextView tvDepartamento;
    private TextView tvMunicipio;
    private TextView tvCodigoPostal;
    private TextView tvEstado;
    private MaterialButton btnDesactivar;
    private LinearLayout layoutDetalles;

    private DistritoViewModel distritoViewModel;
    private Map<String, Distrito> distritosMap;
    private SQLiteDatabase db;
    private Distrito distritoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distrito_eliminar);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        inicializarVistas();
        inicializarViewModel();
        cargarDistritos();

        spinnerDistrito.setOnItemClickListener((parent, view, position, id) -> {
            String distritoSeleccionadoNombre = parent.getItemAtPosition(position).toString();
            distritoSeleccionado = distritosMap.get(distritoSeleccionadoNombre);
            if (distritoSeleccionado != null) {
                mostrarDetallesDistrito();
            }
        });

        btnDesactivar.setOnClickListener(v -> confirmarDesactivacion());
    }

    private void inicializarVistas() {
        spinnerDistrito = findViewById(R.id.spinnerDistrito);
        tvDepartamento = findViewById(R.id.tvDepartamento);
        tvMunicipio = findViewById(R.id.tvMunicipio);
        tvCodigoPostal = findViewById(R.id.tvCodigoPostal);
        btnDesactivar = findViewById(R.id.btnDesactivar);
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

        distritoViewModel.getOperacionExitosa().observe(this, exitoso -> {
            if (exitoso != null && exitoso) {
                Toast.makeText(this, "Distrito desactivado exitosamente", Toast.LENGTH_SHORT).show();
                cargarDistritos();
                limpiarSeleccion();
            }
        });
    }

    private void cargarDistritos() {
        try {
            String sql = "SELECT d.*, dep.NOMBRE_DEPARTAMENTO, m.NOMBRE_MUNICIPIO " +
                    "FROM DISTRITO d " +
                    "JOIN DEPARTAMENTO dep ON d.ID_DEPARTAMENTO = dep.ID_DEPARTAMENTO " +
                    "JOIN MUNICIPIO m ON d.ID_DEPARTAMENTO = m.ID_DEPARTAMENTO " +
                    "AND d.ID_MUNICIPIO = m.ID_MUNICIPIO " +
                    "WHERE d.ACTIVO_DISTRITO = 1 " +  // Solo distritos activos
                    "ORDER BY d.NOMBRE_DISTRITO";

            List<String> nombresDistritos = new ArrayList<>();
            distritosMap.clear();

            try (Cursor cursor = db.rawQuery(sql, null)) {
                while (cursor.moveToNext()) {
                    Distrito distrito = new Distrito();
                    distrito.setIdDepartamento(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DEPARTAMENTO")));
                    distrito.setIdMunicipio(cursor.getInt(cursor.getColumnIndexOrThrow("ID_MUNICIPIO")));
                    distrito.setIdDistrito(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DISTRITO")));
                    distrito.setNombreDistrito(cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_DISTRITO")));
                    distrito.setCodigoPostal(cursor.getString(cursor.getColumnIndexOrThrow("CODIGO_POSTAL")));
                    distrito.setActivoDistrito(cursor.getInt(cursor.getColumnIndexOrThrow("ACTIVO_DISTRITO")));

                    nombresDistritos.add(distrito.getNombreDistrito() + "-" + distrito.getCodigoPostal());
                    distritosMap.put(nombresDistritos.get(nombresDistritos.size() - 1), distrito);
                }
            }

            if (nombresDistritos.isEmpty()) {
                Toast.makeText(this, "No hay distritos activos disponibles", Toast.LENGTH_SHORT).show();
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

    private void mostrarDetallesDistrito() {
        try {
            String sql = "SELECT d.CODIGO_POSTAL, " +
                    "dep.NOMBRE_DEPARTAMENTO, " +
                    "m.NOMBRE_MUNICIPIO " +
                    "FROM DISTRITO d " +
                    "JOIN DEPARTAMENTO dep ON d.ID_DEPARTAMENTO = dep.ID_DEPARTAMENTO " +
                    "JOIN MUNICIPIO m ON d.ID_DEPARTAMENTO = m.ID_DEPARTAMENTO " +
                    "AND d.ID_MUNICIPIO = m.ID_MUNICIPIO " +
                    "WHERE d.ID_DEPARTAMENTO = ? " +
                    "AND d.ID_MUNICIPIO = ? " +
                    "AND d.ID_DISTRITO = ?";

            String[] selectionArgs = {
                    String.valueOf(distritoSeleccionado.getIdDepartamento()),
                    String.valueOf(distritoSeleccionado.getIdMunicipio()),
                    String.valueOf(distritoSeleccionado.getIdDistrito())
            };

            try (Cursor cursor = db.rawQuery(sql, selectionArgs)) {
                if (cursor.moveToFirst()) {
                    String nombreDepartamento = cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_DEPARTAMENTO"));
                    String nombreMunicipio = cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_MUNICIPIO"));
                    String codigoPostal = cursor.getString(cursor.getColumnIndexOrThrow("CODIGO_POSTAL"));

                    tvDepartamento.setText("Departamento: " + nombreDepartamento);
                    tvMunicipio.setText("Municipio: " + nombreMunicipio);
                    tvCodigoPostal.setText("Código Postal: " + codigoPostal);

                    layoutDetalles.setVisibility(View.VISIBLE);
                    btnDesactivar.setEnabled(true);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar los detalles del distrito: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            layoutDetalles.setVisibility(View.GONE);
            btnDesactivar.setEnabled(false);
        }
    }

    private void confirmarDesactivacion() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Desactivación")
                .setMessage("¿Está seguro que desea desactivar el distrito " +
                        distritoSeleccionado.getNombreDistrito() + "?")
                .setPositiveButton("Sí", (dialog, which) -> desactivarDistrito())
                .setNegativeButton("No", null)
                .show();
    }

    private void desactivarDistrito() {
        if (distritoSeleccionado != null) {
            distritoSeleccionado.setActivoDistrito(0);
            distritoViewModel.desactivarDistrito(distritoSeleccionado);
        }
    }

    private void limpiarSeleccion() {
        spinnerDistrito.setText("");
        layoutDetalles.setVisibility(View.GONE);
        distritoSeleccionado = null;
        btnDesactivar.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}