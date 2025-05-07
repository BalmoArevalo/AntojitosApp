package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

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
import java.util.Locale; // Importar Locale

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DireccionConsultarActivity extends AppCompatActivity {

    private static final String TAG = "DireccionConsultarAct";

    private Spinner spinnerCliente;
    private Button btnCargar;
    private TextView tvResultado;
    private DBHelper dbHelper;
    private List<Integer> clienteIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_consultar);
        setTitle(getString(R.string.direccion_consultar_title)); // Establecer título

        dbHelper       = new DBHelper(this);
        // Usar los nuevos IDs del layout XML
        spinnerCliente = findViewById(R.id.spinnerConsultaDireccionCliente);
        btnCargar      = findViewById(R.id.btnCargarDireccionesCliente);
        tvResultado    = findViewById(R.id.tvConsultaDireccionResultado);

        cargarSpinnerClientes();

        btnCargar.setOnClickListener(v -> mostrarDirecciones());
    }

    private void cargarSpinnerClientes() {
        clienteIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add(getString(R.string.placeholder_seleccione)); // Usar string resource
        clienteIds.add(-1);

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbHelper.getReadableDatabase();
            // Corregir typo APELLIDO_CLIENTE
            c = db.rawQuery(
                    "SELECT ID_CLIENTE, NOMBRE_CLIENTE || ' ' || APELLIDO_CLIENTE FROM CLIENTE ORDER BY NOMBRE_CLIENTE ASC, APELLIDO_CLIENTE ASC",
                    null
            );
            while (c.moveToNext()) {
                clienteIds.add(c.getInt(0));
                nombres.add(c.getString(1));
            }
        } catch (SQLiteException ex) {
            Log.e(TAG, "Error cargando spinner clientes", ex);
            // Podrías añadir un item indicando el error si quieres
            // nombres.add("Error al cargar"); clienteIds.add(-1);
            Toast.makeText(this, "Error al cargar clientes", Toast.LENGTH_SHORT).show(); // Toast genérico
        } finally {
            if (c != null) c.close();
            // No cerrar DB aquí
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCliente.setAdapter(adapter);
    }

    private void mostrarDirecciones() {
        int pos = spinnerCliente.getSelectedItemPosition();
        if (pos <= 0) { // Posición 0 es el placeholder
            Toast.makeText(this, R.string.direccion_consultar_seleccione_cliente, Toast.LENGTH_SHORT).show();
            tvResultado.setText(""); // Limpiar resultados si no hay cliente seleccionado
            return;
        }
        int idCli = clienteIds.get(pos);
        Log.d(TAG, "Mostrando direcciones para Cliente ID: " + idCli);

        SQLiteDatabase db = null;
        List<Direccion> lista = new ArrayList<>(); // Inicializar lista

        try {
            db = dbHelper.getReadableDatabase();
            DireccionDAO dao = new DireccionDAO(db); // DAO actualizado obtiene ACTIVO_DIRECCION
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
                // Obtener el estado ACTIVO/INACTIVO
                if (d.getActivoDireccion() == 1) {
                    estadoStr = getString(R.string.direccion_consultar_estado_activa);
                } else {
                    estadoStr = getString(R.string.direccion_consultar_estado_inactiva);
                }

                // Construir el string usando String.format y los recursos
                String descripcion = d.getDescripcionDireccion();
                if (descripcion != null && !descripcion.isEmpty()) {
                    sb.append(String.format(formatoConDesc,
                            d.getIdDireccion(),
                            estadoStr, // Incluir estado
                            d.getDireccionEspecifica(),
                            descripcion));
                } else {
                    sb.append(String.format(formatoSinDesc,
                            d.getIdDireccion(),
                            estadoStr, // Incluir estado
                            d.getDireccionEspecifica()));
                }
                sb.append("\n"); // Añadir una línea extra de separación
            }
            tvResultado.setText(sb.toString().trim());
        }
    }
}