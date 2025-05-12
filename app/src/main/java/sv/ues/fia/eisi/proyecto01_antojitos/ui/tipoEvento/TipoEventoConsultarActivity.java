package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class TipoEventoConsultarActivity extends AppCompatActivity {

    private static final String TAG = "TipoEventoConsultarAct";
    private TipoEventoViewModel tipoEventoViewModel;

    private Spinner spinnerConsultaTipoEvento;
    // El botón "Cargar Todos" se eliminará o su función cambiará
    // private Button btnCargarTodos;
    private Button btnCargarActivos;
    private TextView tvResultado;
    private ProgressBar progressBar;
    private FloatingActionButton fabAgregar;

    private ArrayAdapter<String> spinnerAdapter;
    private List<TipoEvento> listaTiposEventoActual = new ArrayList<>();

    private static final int REQUEST_CODE_OPERACION_TIPO_EVENTO = 1;
    private boolean isLoadingForSpinner = false; // Bandera para controlar la carga inicial del spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_evento_consultar);

        setTitle(getString(R.string.tipo_evento_consultar_title));

        Locale defaultLocaleOnCreate = Locale.getDefault();
        Log.d(TAG, "onCreate - Default Locale: " + defaultLocaleOnCreate.toString());
        Log.d(TAG, "onCreate - Default Locale Language: " + defaultLocaleOnCreate.getLanguage());
        Log.d(TAG, "onCreate - Default Locale Country: " + defaultLocaleOnCreate.getCountry());

        spinnerConsultaTipoEvento = findViewById(R.id.spinnerConsultaTipoEvento);
        // btnCargarTodos = findViewById(R.id.btnCargarTodosTiposEvento); // Se puede eliminar del layout o reutilizar
        btnCargarActivos = findViewById(R.id.btnCargarTiposEventoActivos);
        tvResultado = findViewById(R.id.tvConsultaTipoEventoResultado);
        progressBar = findViewById(R.id.progressBarTipoEventoConsultar);
        fabAgregar = findViewById(R.id.fabAgregarTipoEventoConsultar);

        List<String> spinnerNombres = new ArrayList<>();
        spinnerNombres.add(getString(R.string.tipo_evento_consultar_placeholder_seleccione));
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerNombres);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConsultaTipoEvento.setAdapter(spinnerAdapter);

        tipoEventoViewModel = new ViewModelProvider(this).get(TipoEventoViewModel.class);

        setupListeners();
        setupObservers();

        // Cargar todos los tipos de evento en el Spinner al iniciar
        cargarDatosParaSpinner();
    }

    private void setupListeners() {
        // Si eliminas el botón btnCargarTodos del layout, elimina este listener también
        // btnCargarTodos.setOnClickListener(v -> cargarDatosTodos());
        btnCargarActivos.setOnClickListener(v -> cargarDatosActivosFiltrados());

        spinnerConsultaTipoEvento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isLoadingForSpinner) { // Evitar acción mientras el spinner se está poblando inicialmente
                    return;
                }
                if (position > 0 && position <= listaTiposEventoActual.size()) {
                    TipoEvento seleccionado = listaTiposEventoActual.get(position - 1);
                    mostrarDetalleUnico(seleccionado);
                } else if (position == 0 && !listaTiposEventoActual.isEmpty()) {
                    // Si se selecciona el placeholder, mostrar la lista actual completa en el TextView
                    mostrarListaEnTextView(listaTiposEventoActual);
                } else if (position == 0 && listaTiposEventoActual.isEmpty()){
                    // Si se selecciona el placeholder y no hay nada cargado
                    tvResultado.setText(getString(R.string.tipo_evento_consultar_vacio));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(TipoEventoConsultarActivity.this, TipoEventoCrearActivity.class);
            startActivityForResult(intent, REQUEST_CODE_OPERACION_TIPO_EVENTO);
        });
    }

    private void setupObservers() {
        // Observador para la carga inicial del Spinner (todos los tipos)
        // y también si el usuario presiona un botón "Cargar Todos" (si lo mantienes)
        tipoEventoViewModel.getListaTodosTiposEvento().observe(this, tiposEvento -> {
            progressBar.setVisibility(View.GONE);
            if (isLoadingForSpinner) { // Solo actualizar spinner si esta era la carga inicial
                listaTiposEventoActual = tiposEvento != null ? tiposEvento : new ArrayList<>();
                actualizarSpinner(listaTiposEventoActual);
                // Mostrar la lista completa en el TextView por defecto después de cargar el spinner
                mostrarListaEnTextView(listaTiposEventoActual);
                isLoadingForSpinner = false; // Resetear bandera
            } else {
                // Si no era para el spinner inicial, podría ser una recarga general
                // o el botón "Cargar Todos" si lo dejas.
                listaTiposEventoActual = tiposEvento != null ? tiposEvento : new ArrayList<>();
                actualizarSpinner(listaTiposEventoActual); // Actualizar spinner también
                mostrarListaEnTextView(listaTiposEventoActual);
            }
        });

        // Observador para cuando se cargan solo los activos (botón "Cargar Activos")
        tipoEventoViewModel.getListaTiposEventoActivos().observe(this, tiposEvento -> {
            progressBar.setVisibility(View.GONE);
            if (!isLoadingForSpinner) { // No interferir con la carga inicial del spinner
                listaTiposEventoActual = tiposEvento != null ? tiposEvento : new ArrayList<>();
                actualizarSpinner(listaTiposEventoActual);
                mostrarListaEnTextView(listaTiposEventoActual);
                // Si quieres que el spinner se quede en el placeholder después de filtrar
                spinnerConsultaTipoEvento.setSelection(0);
            }
        });

        tipoEventoViewModel.getMensajeError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                tvResultado.setText(error);
                Log.e(TAG, "Error del ViewModel: " + error);
                isLoadingForSpinner = false; // Asegurarse de resetear en caso de error
            }
        });

        tipoEventoViewModel.getTipoEventoSeleccionado().observe(this, tipoEvento -> {
            progressBar.setVisibility(View.GONE);
            if (tipoEvento != null && !isLoadingForSpinner) { // No interferir con la carga inicial
                mostrarDetalleUnico(tipoEvento);
            }
        });
    }

    private void cargarDatosParaSpinner() {
        Log.d(TAG, "Cargando datos iniciales para el Spinner (todos).");
        progressBar.setVisibility(View.VISIBLE);
        tvResultado.setText("");
        isLoadingForSpinner = true; // Indicar que estamos cargando para el spinner
        tipoEventoViewModel.cargarTodosTiposEvento();
    }

    // Este método ahora es llamado solo por el botón "Cargar Activos"
    private void cargarDatosActivosFiltrados() {
        Log.d(TAG, "Botón Cargar Activos presionado.");
        progressBar.setVisibility(View.VISIBLE);
        tvResultado.setText("");
        isLoadingForSpinner = false; // No es la carga inicial del spinner
        tipoEventoViewModel.cargarTiposEventoActivos();
    }

    // Si mantienes un botón "Cargar Todos" (además de la carga inicial), este sería su método
    /*
    private void cargarDatosTodosGeneral() {
        Log.d(TAG, "Botón Cargar Todos presionado.");
        progressBar.setVisibility(View.VISIBLE);
        tvResultado.setText("");
        isLoadingForSpinner = false; // No es la carga inicial del spinner
        tipoEventoViewModel.cargarTodosTiposEvento();
    }
    */


    private void actualizarSpinner(List<TipoEvento> tiposEvento) {
        List<String> nombres = new ArrayList<>();
        // El placeholder ya está en el adapter, no es necesario añadirlo aquí si no limpias y re-pueblas el adapter completo.
        // O, si prefieres limpiar y añadir:
        spinnerAdapter.clear();
        nombres.add(getString(R.string.tipo_evento_consultar_placeholder_seleccione));

        if (tiposEvento != null) {
            for (TipoEvento te : tiposEvento) {
                // Usamos StringBuilder para construir el string para el spinner
                StringBuilder sbSpinnerItem = new StringBuilder();
                sbSpinnerItem.append(te.getIdTipoEvento());
                sbSpinnerItem.append(" - ");
                sbSpinnerItem.append(te.getNombreTipoEvento());
                nombres.add(sbSpinnerItem.toString());
            }
        }
        spinnerAdapter.addAll(nombres);
        spinnerAdapter.notifyDataSetChanged();
        // No resetear la selección aquí si la carga fue por el botón "Cargar Activos",
        // a menos que sea el comportamiento deseado.
        // Si es la carga inicial, setSelection(0) es correcto.
        if (isLoadingForSpinner) { // Solo para la carga inicial o si explícitamente se quiere resetear
            spinnerConsultaTipoEvento.setSelection(0);
        }
    }

    private void mostrarListaEnTextView(List<TipoEvento> tiposEvento) {
        if (tiposEvento == null || tiposEvento.isEmpty()) {
            tvResultado.setText(getString(R.string.tipo_evento_consultar_vacio));
            Log.d(TAG, "Mostrando mensaje de lista vacía.");
            return;
        }

        Log.d(TAG, "mostrarListaEnTextView - Current Default Locale: " + Locale.getDefault().toString());

        StringBuilder sb = new StringBuilder();
        for (TipoEvento te : tiposEvento) {
            Object min = te.getMontoMinimo();
            Object max = te.getMontoMaximo();
            Log.d(TAG, "Procesando TipoEvento ID: " + te.getIdTipoEvento());
            Log.d(TAG, "  MontoMinimo - Valor: " + te.getMontoMinimo() + ", Tipo: " + (min != null ? min.getClass().getName() : "null"));
            Log.d(TAG, "  MontoMaximo - Valor: " + te.getMontoMaximo() + ", Tipo: " + (max != null ? max.getClass().getName() : "null"));

            String estadoStr = (te.getActivoTipoEvento() == 1) ? getString(R.string.tipo_evento_estado_activo) : getString(R.string.tipo_evento_estado_inactivo);

            // Construcción manual del string
            sb.append("ID: ").append(te.getIdTipoEvento()).append(" (").append(estadoStr).append(")\n");
            sb.append("Nombre: ").append(te.getNombreTipoEvento()).append("\n");
            String descripcion = te.getDescripcionTipoEvento();
            sb.append("Descripción: ").append(descripcion == null ? "" : descripcion).append("\n");
            sb.append("Mín: $").append(te.getMontoMinimo());
            sb.append(" - Máx: $").append(te.getMontoMaximo());
            sb.append("\n\n");
        }
        tvResultado.setText(sb.toString());
        Log.d(TAG, "Mostrando " + tiposEvento.size() + " tipos de evento en TextView (datos crudos).");
    }

    private void mostrarDetalleUnico(TipoEvento te) {
        if (te == null) {
            tvResultado.setText(getString(R.string.tipo_evento_consultar_vacio));
            return;
        }
        Log.d(TAG, "mostrarDetalleUnico - Current Default Locale: " + Locale.getDefault().toString());

        Object min = te.getMontoMinimo();
        Object max = te.getMontoMaximo();
        Log.d(TAG, "Detalle Unico TipoEvento ID: " + te.getIdTipoEvento());
        Log.d(TAG, "  MontoMinimo - Valor: " + te.getMontoMinimo() + ", Tipo: " + (min != null ? min.getClass().getName() : "null"));
        Log.d(TAG, "  MontoMaximo - Valor: " + te.getMontoMaximo() + ", Tipo: " + (max != null ? max.getClass().getName() : "null"));

        String estadoStr = (te.getActivoTipoEvento() == 1) ? getString(R.string.tipo_evento_estado_activo) : getString(R.string.tipo_evento_estado_inactivo);

        StringBuilder detalleSb = new StringBuilder();
        detalleSb.append("ID: ").append(te.getIdTipoEvento()).append(" (").append(estadoStr).append(")\n");
        detalleSb.append("Nombre: ").append(te.getNombreTipoEvento()).append("\n");
        String descripcion = te.getDescripcionTipoEvento();
        detalleSb.append("Descripción: ").append(descripcion == null ? "" : descripcion).append("\n");
        detalleSb.append("Mín: $").append(te.getMontoMinimo());
        detalleSb.append(" - Máx: $").append(te.getMontoMaximo());

        tvResultado.setText(detalleSb.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPERACION_TIPO_EVENTO && resultCode == RESULT_OK) {
            Log.d(TAG, "Operación de TipoEvento (Crear/Editar) resultó OK. Recargando datos para spinner...");
            cargarDatosParaSpinner(); // Recargar la lista completa en el spinner
        }
    }
}