package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

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

public class SucursalConsultarActivity extends AppCompatActivity {

    private Spinner spinnerSucursal;
    private Button btnConsultar;
    private TextView tvResultado;
    private DBHelper dbHelper;
    private SucursalDAO dao;

    // Lista de IDs de sucursales a mostrar en el spinner
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

    /**
     * Carga todas las sucursales (activas e inactivas) en el spinner,
     * mostrando su estado.
     */
    private void cargarSpinnerSucursales() {
        sucursalIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione...");
        sucursalIds.add(-1);

        List<Sucursal> lista = dao.obtenerTodos();
        for (Sucursal s : lista) {
            sucursalIds.add(s.getIdSucursal());
            String estado = s.getActivoSucursal() == 1 ? "(Activo)" : "(Inactivo)";
            nombres.add(s.getIdSucursal() + " - " + s.getNombreSucursal() + " " + estado);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSucursal.setAdapter(adapter);
    }

    /**
     * Muestra todos los detalles de la sucursal seleccionada, incluyendo estado.
     */
    private void mostrarDetalleSucursal() {
        int pos = spinnerSucursal.getSelectedItemPosition();
        int idSucursal = sucursalIds.get(pos);

        if (idSucursal < 0) {
            Toast.makeText(this, "Selecciona una sucursal válida", Toast.LENGTH_SHORT).show();
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
                    "WHERE s.ID_SUCURSAL = ?";
            c = db.rawQuery(query, new String[]{String.valueOf(idSucursal)});

            if (c.moveToFirst()) {
                int activo = c.getInt(12);
                String detalle = "ID Sucursal: " + c.getInt(0) + "\n" +
                        "Departamento " + c.getInt(1) + " - " + c.getString(2) + "\n" +
                        "Municipio " + c.getInt(3) + " - " + c.getString(4) + "\n" +
                        "Distrito " + c.getInt(5) + " - " + c.getString(6) + "\n" +
                        "Nombre: " + c.getString(7) + "\n" +
                        "Dirección: " + c.getString(8) + "\n" +
                        "Teléfono: " + c.getString(9) + "\n" +
                        "Horario Apertura: " + c.getString(10) + "\n" +
                        "Horario Cierre: " + c.getString(11) + "\n" +
                        "Estado: " + (activo == 1 ? "Activo" : "Inactivo");
                tvResultado.setText(detalle);
            } else {
                tvResultado.setText("Sucursal no encontrada.");
            }
        } catch (Exception ex) {
            tvResultado.setText("Error al consultar: " + ex.getMessage());
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