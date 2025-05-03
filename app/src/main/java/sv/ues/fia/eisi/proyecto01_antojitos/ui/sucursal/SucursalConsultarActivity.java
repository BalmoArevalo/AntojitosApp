package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

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

public class SucursalConsultarActivity extends AppCompatActivity {

    private Spinner spinnerSucursal;
    private Button btnConsultar;
    private TextView tvResultado;
    private DBHelper dbHelper;

    private List<Integer> sucursalIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucursal_consultar);

        spinnerSucursal = findViewById(R.id.spinnerSucursal);
        btnConsultar = findViewById(R.id.btnConsultarSucursal);
        tvResultado = findViewById(R.id.tvResultado);
        dbHelper = new DBHelper(this);

        cargarSpinnerSucursales();

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDetalleSucursal();
            }
        });
    }

    private void cargarSpinnerSucursales() {
        sucursalIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione..."); sucursalIds.add(-1);

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbHelper.getReadableDatabase();
            c = db.rawQuery("SELECT ID_SUCURSAL, NOMBRE_SUCURSAL FROM SUCURSAL", null);
            while (c.moveToNext()) {
                sucursalIds.add(c.getInt(0));
                nombres.add("ID " + c.getInt(0) + " - " + c.getString(1));
            }
        } catch (SQLiteException ex) {
            nombres.add("Error al cargar"); sucursalIds.add(-1);
        } finally {
            if (c != null) c.close();
            if (db != null) db.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSucursal.setAdapter(adapter);
    }

    private void mostrarDetalleSucursal() {
        int pos = spinnerSucursal.getSelectedItemPosition();
        int idSucursal = sucursalIds.get(pos);

        if (idSucursal < 0) {
            Toast.makeText(this, "Selecciona una sucursal válida", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT s.ID_SUCURSAL, s.ID_DEPARTAMENTO, d.NOMBRE_DEPARTAMENTO, " +
                "s.ID_MUNICIPIO, m.NOMBRE_MUNICIPIO, s.ID_DISTRITO, dt.NOMBRE_DISTRITO, " +
                "s.NOMBRE_SUCURSAL, s.DIRECCION_SUCURSAL, s.TELEFONO_SUCURSAL, " +
                "s.HORARIO_APERTURA_SUCURSAL, s.HORARIO_CIERRE_SUCURSAL " +
                "FROM SUCURSAL s " +
                "JOIN DEPARTAMENTO d ON s.ID_DEPARTAMENTO = d.ID_DEPARTAMENTO " +
                "JOIN MUNICIPIO m ON s.ID_DEPARTAMENTO = m.ID_DEPARTAMENTO AND s.ID_MUNICIPIO = m.ID_MUNICIPIO " +
                "JOIN DISTRITO dt ON s.ID_DEPARTAMENTO = dt.ID_DEPARTAMENTO AND s.ID_MUNICIPIO = dt.ID_MUNICIPIO AND s.ID_DISTRITO = dt.ID_DISTRITO " +
                "WHERE s.ID_SUCURSAL = ?";

        Cursor c = db.rawQuery(query, new String[]{String.valueOf(idSucursal)});

        if (c.moveToFirst()) {
            String detalle = "ID Sucursal: " + c.getInt(0) + "\n"
                    + "Departamento " + c.getInt(1) + " - " + c.getString(2) + "\n"
                    + "Municipio " + c.getInt(3) + " - " + c.getString(4) + "\n"
                    + "Distrito " + c.getInt(5) + " - " + c.getString(6) + "\n"
                    + "Nombre: " + c.getString(7) + "\n"
                    + "Dirección: " + c.getString(8) + "\n"
                    + "Teléfono: " + c.getString(9) + "\n"
                    + "Horario Apertura: " + c.getString(10) + "\n"
                    + "Horario Cierre: " + c.getString(11);

            tvResultado.setText(detalle);
        } else {
            tvResultado.setText("Sucursal no encontrada.");
        }

        c.close();
        db.close();
    }

}
