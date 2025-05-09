package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class SucursalConsultarActivity extends AppCompatActivity {

    private Spinner spinnerSucursal;
    private Button btnConsultar;
    private TextView tvResultado;
    private DBHelper dbHelper;
    private SucursalDAO dao;

    private List<Integer> sucursalIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucursal_consultar);

        spinnerSucursal = findViewById(R.id.spinnerSucursal);
        btnConsultar = findViewById(R.id.btnConsultarSucursal);
        tvResultado = findViewById(R.id.tvResultado);

        dbHelper = new DBHelper(this);
        dao = new SucursalDAO(dbHelper.getReadableDatabase());

        cargarSpinnerSucursales();

        btnConsultar.setOnClickListener(v -> mostrarDetalleSucursal());
    }

    private void cargarSpinnerSucursales() {
        sucursalIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add(getString(R.string.spinner_placeholder));
        sucursalIds.add(-1);

        List<Sucursal> listaActivas = dao.obtenerActivos();
        for (Sucursal s : listaActivas) {
            sucursalIds.add(s.getIdSucursal());
            nombres.add(s.getIdSucursal() + " - " + s.getNombreSucursal());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSucursal.setAdapter(adapter);
    }

    private void mostrarDetalleSucursal() {
        int pos = spinnerSucursal.getSelectedItemPosition();
        int idSucursal = sucursalIds.get(pos);

        if (idSucursal < 0) {
            Toast.makeText(this, getString(R.string.sucursal_consultar_toast_seleccionar), Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = null;
        try {
            String query = "SELECT s.ID_SUCURSAL, s.ID_DEPARTAMENTO, d.NOMBRE_DEPARTAMENTO, " +
                    "s.ID_MUNICIPIO, m.NOMBRE_MUNICIPIO, s.ID_DISTRITO, dt.NOMBRE_DISTRITO, " +
                    "s.NOMBRE_SUCURSAL, s.DIRECCION_SUCURSAL, s.TELEFONO_SUCURSAL, " +
                    "s.HORARIO_APERTURA_SUCURSAL, s.HORARIO_CIERRE_SUCURSAL, s.ACTIVO_SUCURSAL " +
                    "FROM SUCURSAL s " +
                    "JOIN DEPARTAMENTO d ON s.ID_DEPARTAMENTO = d.ID_DEPARTAMENTO " +
                    "JOIN MUNICIPIO m ON s.ID_DEPARTAMENTO = m.ID_DEPARTAMENTO AND s.ID_MUNICIPIO = m.ID_MUNICIPIO " +
                    "JOIN DISTRITO dt ON s.ID_DEPARTAMENTO = dt.ID_DEPARTAMENTO " +
                    "AND s.ID_MUNICIPIO = dt.ID_MUNICIPIO AND s.ID_DISTRITO = dt.ID_DISTRITO " +
                    "WHERE s.ID_SUCURSAL = ? AND s.ACTIVO_SUCURSAL = 1";

            c = db.rawQuery(query, new String[]{String.valueOf(idSucursal)});

            if (c.moveToFirst()) {
                int activo = c.getInt(12);
                String estadoStr = (activo == 1) ? getString(R.string.estado_activo) : getString(R.string.estado_inactivo);

                String detalle = getString(R.string.sucursal_consultar_detalle_formato,
                        c.getInt(0),  // ID
                        c.getInt(1), c.getString(2), // Departamento
                        c.getInt(3), c.getString(4), // Municipio
                        c.getInt(5), c.getString(6), // Distrito
                        c.getString(7), // Nombre
                        c.getString(8), // Dirección
                        c.getString(9), // Teléfono
                        c.getString(10), // Horario apertura
                        c.getString(11), // Horario cierre
                        estadoStr);

                tvResultado.setText(detalle);
            } else {
                tvResultado.setText(getString(R.string.sucursal_consultar_msg_no_encontrada));
            }
        } catch (Exception ex) {
            tvResultado.setText(getString(R.string.sucursal_consultar_msg_error, ex.getMessage()));
        } finally {
            if (c != null) c.close();
            db.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
