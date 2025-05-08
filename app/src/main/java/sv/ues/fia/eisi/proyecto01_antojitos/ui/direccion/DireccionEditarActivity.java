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
// import java.util.Objects; // No se necesita

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion.Direccion;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion.DireccionDAO;

public class DireccionEditarActivity extends AppCompatActivity {

    private static final String TAG = "DireccionEditarActivity";

    // --- Componentes UI ---
    private Spinner spCliente, spDireccion, spDepto, spMun, spDist;
    private EditText etDirEsp, etDesc;
    private CheckBox checkActivo;
    private Button btnActualizar;
    private LinearLayout layoutCamposEditables;

    // --- Datos ---
    private DBHelper dbHelper;
    private List<Integer> clienteIds = new ArrayList<>();
    private List<Direccion> direccionesCliente = new ArrayList<>();
    private Direccion direccionSeleccionadaActual;
    private List<Integer> deptoIds = new ArrayList<>();
    private List<Integer> munIds = new ArrayList<>();
    private List<Integer> distIds = new ArrayList<>();

    // Flag para evitar que los listeners se disparen durante la carga inicial
    private boolean isInitialLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_editar);
        setTitle(getString(R.string.direccion_editar_title));

        dbHelper = new DBHelper(this);

        // Inicializar Vistas
        spCliente    = findViewById(R.id.spinnerEditarDireccionCliente);
        spDireccion  = findViewById(R.id.spinnerEditarDireccionSeleccion);
        layoutCamposEditables = findViewById(R.id.layoutEditarDireccionCampos);
        spDepto      = findViewById(R.id.spinnerEditarDireccionDepartamento);
        spMun        = findViewById(R.id.spinnerEditarDireccionMunicipio);
        spDist       = findViewById(R.id.spinnerEditarDireccionDistrito);
        etDirEsp     = findViewById(R.id.editEditarDireccionEspecifica);
        etDesc       = findViewById(R.id.editEditarDireccionDescripcion);
        checkActivo  = findViewById(R.id.checkEditarDireccionActivo);
        btnActualizar= findViewById(R.id.btnActualizarDireccion);

        // Estado inicial
        layoutCamposEditables.setVisibility(View.GONE);
        btnActualizar.setEnabled(false);

        cargarSpinnerClientes();
        configurarListenersSpinners();

        btnActualizar.setOnClickListener(v -> actualizarDireccion());
    }

    private void configurarListenersSpinners(){
        // Listener para Cliente -> Carga Direcciones
        spCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ocultarYLimpiarCamposEditables();
                if (pos > 0) {
                    int idCli = clienteIds.get(pos); // ID real está en la lista
                    cargarSpinnerDirecciones(idCli);
                    // Habilitar spDireccion se hace dentro de cargarSpinnerDirecciones
                } else {
                    spDireccion.setAdapter(null);
                    spDireccion.setEnabled(false);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                ocultarYLimpiarCamposEditables();
                spDireccion.setEnabled(false);
            }
        });

        // Listener para Direccion -> Carga Datos en campos editables
        spDireccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    ocultarYLimpiarCamposEditables();
                } else {
                    if (pos - 1 < direccionesCliente.size()) {
                        isInitialLoad = true; // Marcar inicio de carga automática
                        direccionSeleccionadaActual = direccionesCliente.get(pos - 1);
                        cargarDatosDireccionUI(direccionSeleccionadaActual);
                        layoutCamposEditables.setVisibility(View.VISIBLE);
                        btnActualizar.setEnabled(true);
                        // Desmarcar al final de la carga en cargarDatosDireccionUI
                    } else {
                        Log.e(TAG, "Índice fuera de rango para direccionesCliente al seleccionar dirección.");
                        ocultarYLimpiarCamposEditables();
                    }
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                ocultarYLimpiarCamposEditables();
            }
        });

        // Listeners para Spinners de Ubicación (Depto -> Mun -> Dist)
        spDepto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                // Evitar recarga si es parte de la carga inicial de datos
                if (isInitialLoad) return;

                int selDept = -1;
                if(pos >= 0 && pos < deptoIds.size()) selDept = deptoIds.get(pos);

                if (selDept >= 0) {
                    spMun.setEnabled(true);
                    cargarSpinnerFiltrado("MUNICIPIO", "ID_MUNICIPIO", "NOMBRE_MUNICIPIO",
                            "ID_DEPARTAMENTO=" + selDept, spMun, munIds);
                    spDist.setAdapter(null); // Limpiar distrito al cambiar depto
                    spDist.setEnabled(false);
                } else {
                    spMun.setSelection(0);
                    spDist.setSelection(0);
                    spMun.setEnabled(false);
                    spDist.setEnabled(false);
                    spMun.setAdapter(null);
                    spDist.setAdapter(null);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> p) { /* Limpiar/deshabilitar */ }
        });

        spMun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                // Evitar recarga si es parte de la carga inicial de datos
                if (isInitialLoad) return;

                int posDepto = spDepto.getSelectedItemPosition();
                int selMun = -1;
                if (pos >= 0 && pos < munIds.size()) selMun = munIds.get(pos);

                if (posDepto > 0 && selMun >= 0) {
                    spDist.setEnabled(true);
                    int selDepId = deptoIds.get(posDepto);
                    String where = "ID_DEPARTAMENTO=" + selDepId + " AND ID_MUNICIPIO=" + selMun;
                    cargarSpinnerFiltrado("DISTRITO", "ID_DISTRITO", "NOMBRE_DISTRITO",
                            where, spDist, distIds);
                } else {
                    spDist.setSelection(0);
                    spDist.setEnabled(false);
                    spDist.setAdapter(null);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> p) { /* Limpiar/deshabilitar */ }
        });
    }

    // Carga el spinner de clientes
    private void cargarSpinnerClientes() {
        clienteIds.clear();
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.placeholder_seleccione));
        clienteIds.add(-1);

        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor c = db.rawQuery("SELECT ID_CLIENTE, NOMBRE_CLIENTE || ' ' || APELLIDO_CLIENTE FROM CLIENTE ORDER BY NOMBRE_CLIENTE ASC, APELLIDO_CLIENTE ASC", null)) { // Corregido typo
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
        spCliente.setAdapter(a);
    }

    // Carga el spinner de direcciones
    private void cargarSpinnerDirecciones(int idCliente) {
        direccionesCliente.clear();
        List<String> itemsParaSpinner = new ArrayList<>();
        itemsParaSpinner.add(getString(R.string.placeholder_seleccione));

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getReadableDatabase();
            DireccionDAO dao = new DireccionDAO(db);
            direccionesCliente = dao.obtenerPorCliente(idCliente);

            if (!direccionesCliente.isEmpty()) {
                for(Direccion d : direccionesCliente){
                    String estado = (d.getActivoDireccion() == 1) ? "" : " (Inactiva)";
                    String descSpinner = "ID:" + d.getIdDireccion() + " - " + d.getDireccionEspecifica().substring(0, Math.min(d.getDireccionEspecifica().length(), 25)) + "..." + estado;
                    itemsParaSpinner.add(descSpinner);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error cargando direcciones para cliente " + idCliente, e);
            Toast.makeText(this, String.format(getString(R.string.direccion_crear_toast_error_carga),"Direcciones"), Toast.LENGTH_SHORT).show();
        } finally {
            // No cerrar DB
        }

        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsParaSpinner);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDireccion.setAdapter(a);
        spDireccion.setEnabled(itemsParaSpinner.size() > 1);
        // Limpiar campos si no hay direcciones
        if (itemsParaSpinner.size() <= 1) {
            ocultarYLimpiarCamposEditables();
        }
    }

    // *** MÉTODO MODIFICADO ***
    // Carga los datos de la Dirección en los campos de la UI y SELECCIONA los spinners
    private void cargarDatosDireccionUI(Direccion d) {
        if (d == null) {
            Toast.makeText(this, R.string.direccion_editar_toast_error_carga, Toast.LENGTH_SHORT).show();
            ocultarYLimpiarCamposEditables();
            return;
        }
        Log.d(TAG, "Cargando UI para Direccion ID: " + d.getIdDireccion());

        // Poblar campos de texto y checkbox
        etDirEsp.setText(d.getDireccionEspecifica());
        etDesc.setText(d.getDescripcionDireccion());
        checkActivo.setChecked(d.getActivoDireccion() == 1);

        // 1. Cargar TODOS los Departamentos y seleccionar el correcto
        cargarSpinnerConPlaceholder("DEPARTAMENTO", "ID_DEPARTAMENTO", "NOMBRE_DEPARTAMENTO", spDepto, deptoIds);
        seleccionarItemEnSpinner(spDepto, deptoIds, d.getIdDepartamento());
        spDepto.setEnabled(true);

        // 2. Cargar Municipios FILTRADOS por el Depto de la dirección y seleccionar el correcto
        cargarSpinnerFiltrado("MUNICIPIO", "ID_MUNICIPIO", "NOMBRE_MUNICIPIO",
                "ID_DEPARTAMENTO=" + d.getIdDepartamento(), spMun, munIds);
        seleccionarItemEnSpinner(spMun, munIds, d.getIdMunicipio());
        // Habilitar si hay opciones cargadas
        spMun.setEnabled(spMun.getAdapter() != null && spMun.getAdapter().getCount() > 1);

        // 3. Cargar Distritos FILTRADOS por Depto y Mun de la dirección y seleccionar el correcto
        cargarSpinnerFiltrado("DISTRITO", "ID_DISTRITO", "NOMBRE_DISTRITO",
                "ID_DEPARTAMENTO=" + d.getIdDepartamento() + " AND ID_MUNICIPIO=" + d.getIdMunicipio(),
                spDist, distIds);
        seleccionarItemEnSpinner(spDist, distIds, d.getIdDistrito());
        // Habilitar si hay opciones cargadas
        spDist.setEnabled(spDist.getAdapter() != null && spDist.getAdapter().getCount() > 1);

        // Marcar que la carga inicial ha terminado para que los listeners funcionen normalmente
        isInitialLoad = false;
        Log.d(TAG, "Carga de datos UI completada.");
    }

    // Método helper para seleccionar ítem en Spinner por ID
    private void seleccionarItemEnSpinner(Spinner spinner, List<Integer> idList, int idASeleccionar) {
        // Asegurarse que la lista de IDs no sea null y el spinner tenga adaptador
        if (idList == null || spinner.getAdapter() == null) return;

        for (int i = 0; i < idList.size(); i++) {
            // Comparamos el ID en la lista (que corresponde a la posición i del adaptador)
            if (idList.get(i) == idASeleccionar) {
                // Seleccionar la posición correspondiente en el adaptador
                if (i < spinner.getAdapter().getCount()){ // Chequeo extra
                    Log.d(TAG, "Seleccionando posición " + i + " para ID " + idASeleccionar + " en spinner: " + getResources().getResourceEntryName(spinner.getId()));
                    spinner.setSelection(i);
                    return; // Salir una vez encontrado
                }
            }
        }
        Log.w(TAG, "No se encontró ID " + idASeleccionar + " en lista para spinner: " + getResources().getResourceEntryName(spinner.getId()) + ". Seleccionando placeholder.");
        spinner.setSelection(0); // Seleccionar placeholder si no se encuentra
    }

    // Limpia campos y oculta/deshabilita
    private void ocultarYLimpiarCamposEditables(){
        layoutCamposEditables.setVisibility(View.GONE);
        btnActualizar.setEnabled(false);
        direccionSeleccionadaActual = null;

        etDirEsp.setText("");
        etDesc.setText("");
        checkActivo.setChecked(false);
        // Limpiar y deshabilitar spinners de ubicación
        cargarSpinnerPlaceholder(spDepto, deptoIds);
        cargarSpinnerPlaceholder(spMun, munIds);
        cargarSpinnerPlaceholder(spDist, distIds);
        Log.d(TAG, "Campos editables limpiados y ocultados.");
    }

    // Actualiza la dirección
    private void actualizarDireccion() {
        Log.d(TAG, "Intentando actualizar dirección...");
        if (direccionSeleccionadaActual == null) {
            Toast.makeText(this, R.string.direccion_editar_toast_no_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Validación ---
        String dirEsp = etDirEsp.getText().toString().trim();
        if (dirEsp.isEmpty()) {
            Toast.makeText(this, R.string.direccion_editar_toast_especifica_requerida, Toast.LENGTH_SHORT).show();
            etDirEsp.requestFocus();
            return;
        }
        int posDep = spDepto.getSelectedItemPosition();
        int posMun = spMun.getSelectedItemPosition();
        int posDist = spDist.getSelectedItemPosition();

        if (posDep <= 0 || (spMun.isEnabled() && posMun <= 0) || (spDist.isEnabled() && posDist <= 0)) {
            // La validación es un poco más compleja: Mun y Dist deben tener selección si están habilitados
            Toast.makeText(this, R.string.direccion_editar_toast_completar_campos, Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Crear Objeto Actualizado ---
        Direccion direccionActualizada = new Direccion();
        direccionActualizada.setIdCliente(direccionSeleccionadaActual.getIdCliente());
        direccionActualizada.setIdDireccion(direccionSeleccionadaActual.getIdDireccion());
        direccionActualizada.setIdDepartamento(deptoIds.get(posDep));
        direccionActualizada.setIdMunicipio(munIds.get(posMun));
        direccionActualizada.setIdDistrito(distIds.get(posDist));
        direccionActualizada.setDireccionEspecifica(dirEsp);
        direccionActualizada.setDescripcionDireccion(etDesc.getText().toString().trim());
        direccionActualizada.setActivoDireccion(checkActivo.isChecked() ? 1 : 0);

        Log.d(TAG, "Objeto Dirección a actualizar: " + direccionActualizada.toString());

        // --- Actualizar en BD ---
        SQLiteDatabase db = null;
        int rows = 0;
        try {
            db = dbHelper.getWritableDatabase();
            DireccionDAO dao = new DireccionDAO(db);
            rows = dao.actualizar(direccionActualizada);
        } catch(SQLiteException e){
            Log.e(TAG, "Error de BD al actualizar dirección", e);
            Toast.makeText(this, R.string.direccion_editar_toast_error, Toast.LENGTH_SHORT).show();
        } finally {
            // no cerrar db
        }

        // --- Feedback Final ---
        if (rows > 0) {
            Toast.makeText(this, R.string.direccion_editar_toast_exito, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.e(TAG,"No se actualizaron filas.");
            if (!isFinishing()) {
                Toast.makeText(this, R.string.direccion_editar_toast_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // --- Métodos de Carga de Spinners (MODIFICADOS para no seleccionar placeholder) ---
    private void cargarSpinnerConPlaceholder(String tabla, String idCol, String nomCol, Spinner sp, List<Integer> ids) {
        ids.clear();
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.placeholder_seleccione));
        ids.add(-1); // ID inválido para placeholder
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor c = db.rawQuery("SELECT " + idCol + ", " + nomCol + " FROM " + tabla + " ORDER BY " + nomCol + " ASC", null)) {
            while (c.moveToNext()) {
                ids.add(c.getInt(0));
                items.add(c.getString(1));
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error cargando spinner " + tabla, e);
            Toast.makeText(this, String.format(getString(R.string.direccion_crear_toast_error_carga), tabla), Toast.LENGTH_SHORT).show();
        }
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(a);
        // NO establecer selección aquí, dejar que cargarDatosDireccionUI lo haga
        // sp.setSelection(0);
    }

    private void cargarSpinnerFiltrado(String tabla, String idCol, String nomCol,
                                       String where, Spinner sp, List<Integer> ids) {
        ids.clear();
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.placeholder_seleccione));
        ids.add(-1);
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor c = db.rawQuery("SELECT " + idCol + ", " + nomCol + " FROM " + tabla + " WHERE " + where + " ORDER BY " + nomCol + " ASC", null)) {
            while (c.moveToNext()) {
                ids.add(c.getInt(0));
                items.add(c.getString(1));
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error cargando spinner filtrado " + tabla, e);
            Toast.makeText(this, String.format(getString(R.string.direccion_crear_toast_error_carga), tabla), Toast.LENGTH_SHORT).show();
        }
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(a);
        // NO establecer selección aquí
        // sp.setSelection(0);
        // La habilitación se maneja en cargarDatosDireccionUI
        // sp.setEnabled(a.getCount() > 1);
        if(a.getCount() <= 1) {
            Log.w(TAG,"No se encontraron datos filtrados para " + tabla + " con WHERE " + where);
            // Si no hay datos, el spinner se deshabilitará en cargarDatosDireccionUI
            // o en el listener del spinner padre.
        }
    }

    // Método para limpiar un spinner (se usa en ocultarYLimpiarCamposEditables)
    private void cargarSpinnerPlaceholder(Spinner sp, List<Integer> ids) {
        if (ids!= null) ids.clear();
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.placeholder_seleccione));
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(a);
        sp.setEnabled(false);
    }
}