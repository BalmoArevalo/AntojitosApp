package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class MunicipioConsultarActivity extends AppCompatActivity {

    private Spinner spinnerMunicipio;
    private Button btnConsultar;
    private TextView tvResultado;
    private DBHelper dbHelper;

    private List<String> municipioIds = new ArrayList<>();  // formato: "ID_DEP|ID_MUN"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipio_consultar);

        spinnerMunicipio = findViewById(R.id.spinnerMunicipio);
        btnConsultar = findViewById(R.id.btnConsultarMunicipio);
        tvResultado = findViewById(R.id.tvResultado);

        dbHelper = new DBHelper(this);
        cargarSpinnerMunicipios();

        btnConsultar.setOnClickListener(v -> mostrarDetalleMunicipio());
    }

    private void cargarSpinnerMunicipios() {
        municipioIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione...");
        municipioIds.add("-1|-1");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT m.ID_DEPARTAMENTO, d.NOMBRE_DEPARTAMENTO, m.ID_MUNICIPIO, m.NOMBRE_MUNICIPIO " +
                "FROM MUNICIPIO m JOIN DEPARTAMENTO d ON m.ID_DEPARTAMENTO = d.ID_DEPARTAMENTO " +
                "WHERE m.ACTIVO_MUNICIPIO = 1", null);

        while (cursor.moveToNext()) {
            int idDep = cursor.getInt(0);
            String nombreDep = cursor.getString(1);
            int idMun = cursor.getInt(2);
            String nombreMun = cursor.getString(3);
            municipioIds.add(idDep + "|" + idMun);
            nombres.add(idMun + " - " + nombreMun + " (" + nombreDep + ")");
        }

        cursor.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMunicipio.setAdapter(adapter);
    }

    private void mostrarDetalleMunicipio() {
        int pos = spinnerMunicipio.getSelectedItemPosition();
        String[] ids = municipioIds.get(pos).split("\\|");

        int idDep = Integer.parseInt(ids[0]);
        int idMun = Integer.parseInt(ids[1]);

        if (idDep == -1 || idMun == -1) {
            Toast.makeText(this, "Selecciona un municipio válido", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT m.ID_DEPARTAMENTO, d.NOMBRE_DEPARTAMENTO, " +
                        "m.ID_MUNICIPIO, m.NOMBRE_MUNICIPIO, m.ACTIVO_MUNICIPIO " +
                        "FROM MUNICIPIO m JOIN DEPARTAMENTO d ON m.ID_DEPARTAMENTO = d.ID_DEPARTAMENTO " +
                        "WHERE m.ID_DEPARTAMENTO = ? AND m.ID_MUNICIPIO = ?",
                new String[]{String.valueOf(idDep), String.valueOf(idMun)});

        if (c.moveToFirst()) {
            int activo = c.getInt(4);
            String detalle = "ID Departamento: " + c.getInt(0) + " - " + c.getString(1) + "\n" +
                    "ID Municipio: " + c.getInt(2) + " - " + c.getString(3) + "\n" +
                    "Estado: " + (activo == 1 ? "Activo" : "Inactivo");
            tvResultado.setText(detalle);
        } else {
            tvResultado.setText("Municipio no encontrado.");
        }

        c.close();
        db.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
