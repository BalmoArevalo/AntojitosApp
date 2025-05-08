package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider; // Importar ViewModelProvider

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion.Direccion;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion.DireccionDAO;
// Importar ViewModel
import sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion.DireccionViewModel;


public class DireccionEliminarActivity extends AppCompatActivity {

    private static final String TAG = "DireccionEliminarAct";

    // --- Componentes UI ---
    private Spinner spinnerCliente;
    private Spinner spinnerDireccion;
    private Button btnDesactivar; // Cambiado nombre
    private LinearLayout layoutDetalles;
    private TextView tvDirEspecifica, tvUbicacion, tvDescripcion, tvEstado;

    // --- Datos ---
    private DBHelper dbHelper; // Mantener para cargar spinners si se prefiere
    private DireccionViewModel direccionViewModel; // << USAR VIEWMODEL para lógica
    private List<Integer> clienteIds = new ArrayList<>();
    // Almacena las direcciones ACTIVAS cargadas para el cliente
    private List<Direccion> direccionesActivasCliente = new ArrayList<>();
    private Direccion direccionSeleccionada; // Para guardar la selección actual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_eliminar);
        setTitle(getString(R.string.direccion_eliminar_title));

        // Inicializar ViewModel
        direccionViewModel = new ViewModelProvider(this).get(DireccionViewModel.class);
        dbHelper = new DBHelper(this); // Se sigue usando para cargar spinners

        // Inicializar Vistas
        spinnerCliente = findViewById(R.id.spinnerEliminarDireccionCliente);
        spinnerDireccion = findViewById(R.id.spinnerEliminarDireccionSeleccion);
        btnDesactivar = findViewById(R.id.btnDesactivarDireccion); // Nuevo ID/Nombre
        layoutDetalles = findViewById(R.id.layoutDetallesDireccionEliminar);
        tvDirEspecifica = findViewById(R.id.tvEliminarDirEspecifica);
        tvUbicacion = findViewById(R.id.tvEliminarDirUbicacion);
        tvDescripcion = findViewById(R.id.tvEliminarDirDescripcion);
        tvEstado = findViewById(R.id.tvEliminarDirEstado);

        // Estado inicial
        ocultarDetallesYDeshabilitarBoton();
        cargarSpinnerClientes();
        configurarListenersSpinners();

        btnDesactivar.setOnClickListener(v -> intentarDesactivarDireccion());
    }

    private void configurarListenersSpinners() {
        // Listener Cliente -> Carga Direcciones Activas
        spinnerCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ocultarDetallesYDeshabilitarBoton();
                if (pos > 0) {
                    int idCli = clienteIds.get(pos);
                    cargarSpinnerDireccionesActivas(idCli); // Cargar solo activas
                    spinnerDireccion.setEnabled(true);
                } else {
                    spinnerDireccion.setAdapter(null);
                    spinnerDireccion.setEnabled(false);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                ocultarDetallesYDeshabilitarBoton();
                spinnerDireccion.setEnabled(false);
            }
        });

        // Listener Direccion -> Muestra Detalles
        spinnerDireccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos > 0 && (pos - 1) < direccionesActivasCliente.size()) {
                    direccionSeleccionada = direccionesActivasCliente.get(pos - 1);
                    mostrarDetalles(direccionSeleccionada);
                    layoutDetalles.setVisibility(View.VISIBLE);
                    // Habilitar botón solo si la dirección está activa (aunque el spinner solo carga activas)
                    btnDesactivar.setEnabled(direccionSeleccionada.getActivoDireccion() == 1);
                } else {
                    ocultarDetallesYDeshabilitarBoton();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                ocultarDetallesYDeshabilitarBoton();
            }
        });
    }

    // Carga Clientes (igual que antes, corregir typo)
    private void cargarSpinnerClientes() {
        clienteIds.clear();
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.placeholder_seleccione));
        clienteIds.add(-1);
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor c = db.rawQuery("SELECT ID_CLIENTE, NOMBRE_CLIENTE || ' ' || APELLIDO_CLIENTE FROM CLIENTE ORDER BY NOMBRE_CLIENTE ASC, APELLIDO_CLIENTE ASC", null)) { // Corregido APELLIDO_CLIENTE
            while (c.moveToNext()) {
                clienteIds.add(c.getInt(0));
                items.add(c.getString(1));
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error cargando clientes", e);
            Toast.makeText(this, String.format(getString(R.string.direccion_crear_toast_error_carga),"Clientes"), Toast.LENGTH_SHORT).show();
        }
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCliente.setAdapter(a);
    }

    // Carga Direcciones ACTIVAS para el cliente
    private void cargarSpinnerDireccionesActivas(int idCliente) {
        direccionesActivasCliente.clear();
        List<String> itemsParaSpinner = new ArrayList<>();
        itemsParaSpinner.add(getString(R.string.placeholder_seleccione));

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getReadableDatabase();
            DireccionDAO dao = new DireccionDAO(db);
            List<Direccion> todas = dao.obtenerPorCliente(idCliente); // Obtener todas

            // Filtrar solo las activas para el spinner y almacenar los objetos
            if (!todas.isEmpty()) {
                for(Direccion d : todas){
                    if (d.getActivoDireccion() == 1) { // Filtrar aquí
                        direccionesActivasCliente.add(d); // Guardar objeto activo
                        String descSpinner = "ID:" + d.getIdDireccion() + " - " + d.getDireccionEspecifica().substring(0, Math.min(d.getDireccionEspecifica().length(), 25)) + "...";
                        itemsParaSpinner.add(descSpinner);
                    }
                }
            }
            Log.d(TAG,"Direcciones activas cargadas para cliente " + idCliente + ": " + direccionesActivasCliente.size());

        } catch (Exception e) {
            Log.e(TAG, "Error cargando direcciones activas para cliente " + idCliente, e);
            Toast.makeText(this, String.format(getString(R.string.direccion_crear_toast_error_carga),"Direcciones"), Toast.LENGTH_SHORT).show();
        } finally {
            // No cerrar db
        }

        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsParaSpinner);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDireccion.setAdapter(a);
        spinnerDireccion.setEnabled(itemsParaSpinner.size() > 1);
        if (itemsParaSpinner.size() <= 1) {
            ocultarDetallesYDeshabilitarBoton();
        }
    }

    // Muestra los detalles de la dirección seleccionada
    private void mostrarDetalles(Direccion d) {
        if (d == null) return;
        Log.d(TAG,"Mostrando detalles para Direccion ID: " + d.getIdDireccion());
        tvDirEspecifica.setText(String.format("Dirección: %s", d.getDireccionEspecifica()));
        // Usar los nombres de ubicación que ahora vienen en el objeto Direccion gracias al DAO modificado
        String ubicacion = String.format(Locale.getDefault(), "Ubicación: %s, %s, %s",
                d.getNombreDepartamento() != null ? d.getNombreDepartamento() : "N/A",
                d.getNombreMunicipio() != null ? d.getNombreMunicipio() : "N/A",
                d.getNombreDistrito() != null ? d.getNombreDistrito() : "N/A"
        );
        tvUbicacion.setText(ubicacion);
        tvDescripcion.setText(String.format("Descripción: %s", d.getDescripcionDireccion() != null ? d.getDescripcionDireccion() : "-"));
        // Mostrar estado actual
        if (d.getActivoDireccion() == 1) {
            tvEstado.setText(String.format("Estado Actual: %s", getString(R.string.direccion_eliminar_estado_actual_activa)));
        } else {
            // Aunque el spinner solo carga activas, por si acaso
            tvEstado.setText(String.format("Estado Actual: %s", getString(R.string.direccion_eliminar_estado_actual_inactiva)));
        }
    }

    // Oculta detalles y deshabilita botón
    private void ocultarDetallesYDeshabilitarBoton(){
        layoutDetalles.setVisibility(View.GONE);
        btnDesactivar.setEnabled(false);
        direccionSeleccionada = null; // Limpiar selección
        // Limpiar textos
        tvDirEspecifica.setText("");
        tvUbicacion.setText("");
        tvDescripcion.setText("");
        tvEstado.setText("");
        Log.d(TAG,"Detalles ocultados y botón desactivado.");
    }

    // Lógica para DESACTIVAR la dirección
    private void intentarDesactivarDireccion() {
        if (direccionSeleccionada == null) {
            Toast.makeText(this, R.string.direccion_eliminar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }

        // Doble chequeo por si acaso la dirección ya está inactiva
        if (direccionSeleccionada.getActivoDireccion() == 0) {
            Toast.makeText(this, "Esta dirección ya está inactiva.", Toast.LENGTH_SHORT).show();
            return;
        }

        int idCliente = direccionSeleccionada.getIdCliente();
        int idDireccion = direccionSeleccionada.getIdDireccion();

        Log.i(TAG, "Solicitando desactivación para Cliente: " + idCliente + ", Dirección: " + idDireccion);
        // Llamar al método del ViewModel para desactivar (Soft Delete)
        boolean exito = direccionViewModel.desactivarDireccion(idCliente, idDireccion);

        if (exito) {
            Toast.makeText(this, R.string.direccion_eliminar_toast_exito, Toast.LENGTH_SHORT).show();
            // Opcional: Recargar el spinner de direcciones para que ya no aparezca la desactivada
            cargarSpinnerDireccionesActivas(idCliente); // Recargar spinner del cliente actual
            ocultarDetallesYDeshabilitarBoton(); // Ocultar/limpiar detalles
            // finish(); // O cerrar la actividad
        } else {
            Toast.makeText(this, R.string.direccion_eliminar_toast_error, Toast.LENGTH_SHORT).show();
        }
    }
}