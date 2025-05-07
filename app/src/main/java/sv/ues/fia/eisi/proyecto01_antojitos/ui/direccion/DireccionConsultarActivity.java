package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

// ... (imports sin cambios) ...
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DireccionConsultarActivity extends AppCompatActivity {

    private static final String TAG = "DireccionConsultarAct";

    private Spinner spinnerCliente;
    private Button btnCargar;
    private TextView tvResultado;
    private DBHelper dbHelper;
    private List<Integer> clienteIds = new ArrayList<>();
    // NO necesitamos el ViewModel si usamos DBHelper/DAO directamente como en el original
    // private DireccionViewModel direccionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_consultar);
        setTitle(getString(R.string.direccion_consultar_title));

        dbHelper       = new DBHelper(this);
        spinnerCliente = findViewById(R.id.spinnerConsultaDireccionCliente);
        btnCargar      = findViewById(R.id.btnCargarDireccionesCliente);
        tvResultado    = findViewById(R.id.tvConsultaDireccionResultado);

        cargarSpinnerClientes();

        btnCargar.setOnClickListener(v -> mostrarDirecciones());
    }

    // cargarSpinnerClientes (sin cambios respecto a la versión anterior,
    //                     pero asegúrate que el typo APELLIDO_CLIENTE está corregido)
    private void cargarSpinnerClientes() {
        clienteIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add(getString(R.string.placeholder_seleccione));
        clienteIds.add(-1);

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbHelper.getReadableDatabase();
            c = db.rawQuery(
                    "SELECT ID_CLIENTE, NOMBRE_CLIENTE || ' ' || APELLIDO_CLIENTE FROM CLIENTE ORDER BY NOMBRE_CLIENTE ASC, APELLIDO_CLIENTE ASC", // Corregido
                    null
            );
            while (c.moveToNext()) {
                clienteIds.add(c.getInt(0));
                nombres.add(c.getString(1));
            }
        } catch (SQLiteException ex) {
            Log.e(TAG, "Error cargando spinner clientes", ex);
            Toast.makeText(this, "Error al cargar clientes", Toast.LENGTH_SHORT).show();
        } finally {
            if (c != null) c.close();
            // No cerrar db aquí
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCliente.setAdapter(adapter);
    }

    // --- MÉTODO MODIFICADO ---
    private void mostrarDirecciones() {
        int pos = spinnerCliente.getSelectedItemPosition();
        if (pos <= 0) {
            Toast.makeText(this, R.string.direccion_consultar_seleccione_cliente, Toast.LENGTH_SHORT).show();
            tvResultado.setText("");
            return;
        }
        int idCli = clienteIds.get(pos);
        Log.d(TAG, "Mostrando direcciones para Cliente ID: " + idCli);

        SQLiteDatabase db = null;
        List<Direccion> lista = new ArrayList<>();

        try {
            db = dbHelper.getReadableDatabase();
            // *** USA EL DAO MODIFICADO QUE OBTIENE LOS NOMBRES ***
            DireccionDAO dao = new DireccionDAO(db);
            lista = dao.obtenerPorCliente(idCli);
            Log.d(TAG, "Direcciones encontradas para cliente " + idCli + ": " + lista.size());
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener direcciones para cliente " + idCli, e);
            Toast.makeText(this,"Error al consultar direcciones", Toast.LENGTH_SHORT).show();
        } finally {
            // No cerrar db aquí
        }

        if (lista.isEmpty()) {
            tvResultado.setText(getString(R.string.direccion_consultar_resultado_vacio));
        } else {
            StringBuilder sb = new StringBuilder();
            String estadoStr;
            String formatoConDesc = getString(R.string.direccion_consultar_formato_direccion);
            String formatoSinDesc = getString(R.string.direccion_consultar_formato_direccion_sin_desc);

            for (Direccion d : lista) {
                // Determinar estado
                estadoStr = (d.getActivoDireccion() == 1) ?
                        getString(R.string.direccion_consultar_estado_activa) :
                        getString(R.string.direccion_consultar_estado_inactiva);

                // Obtener nombres de ubicación (ahora están en el objeto Direccion)
                String depto = d.getNombreDepartamento() != null ? d.getNombreDepartamento() : "N/A";
                String mun = d.getNombreMunicipio() != null ? d.getNombreMunicipio() : "N/A";
                String dist = d.getNombreDistrito() != null ? d.getNombreDistrito() : "N/A";
                String descripcion = d.getDescripcionDireccion();

                // Formatear y añadir al StringBuilder
                if (descripcion != null && !descripcion.isEmpty()) {
                    sb.append(String.format(formatoConDesc,
                            d.getIdDireccion(),       // %1$d
                            estadoStr,                // %2$s
                            d.getDireccionEspecifica(),// %3$s
                            depto,                    // %4$s
                            mun,                      // %5$s
                            dist,                     // %6$s
                            descripcion));            // %7$s
                } else {
                    sb.append(String.format(formatoSinDesc,
                            d.getIdDireccion(),       // %1$d
                            estadoStr,                // %2$s
                            d.getDireccionEspecifica(),// %3$s
                            depto,                    // %4$s
                            mun,                      // %5$s
                            dist));                   // %6$s
                }
                sb.append("\n"); // Añadir separación extra
            }
            tvResultado.setText(sb.toString().trim());
        }
    }
}