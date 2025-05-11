package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class TipoEventoConsultarActivity extends AppCompatActivity {

    private Spinner spinnerTiposEvento;
    private Button btnBuscar;
    private TextView tvResultado;

    private TipoEventoViewModel viewModel;
    private List<TipoEvento> listaTipoEventos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_evento_consultar);

        spinnerTiposEvento = findViewById(R.id.spinnerIdTipoEventoBuscar);
        btnBuscar = findViewById(R.id.btnBuscarTipoEvento);
        tvResultado = findViewById(R.id.tvResultadoTipoEvento);

        viewModel = new ViewModelProvider(this).get(TipoEventoViewModel.class);

        // Observar los cambios en los datos
        viewModel.getListaTipoEventos().observe(this, tipoEventos -> {
            listaTipoEventos = tipoEventos;

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    obtenerItemsSpinner(tipoEventos)
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTiposEvento.setAdapter(adapter);
        });

        viewModel.cargarTipoEventos(); // Inicia la carga desde BD

        btnBuscar.setOnClickListener(v -> mostrarDetallesSeleccionado());
    }

    private List<String> obtenerItemsSpinner(List<TipoEvento> lista) {
        List<String> items = new ArrayList<>();
        for (TipoEvento tipo : lista) {
            items.add(tipo.getIdTipoEvento() + " - " + tipo.getNombreTipoEvento());
        }
        return items;
    }

    private void mostrarDetallesSeleccionado() {
        int pos = spinnerTiposEvento.getSelectedItemPosition();
        if (pos >= 0 && pos < listaTipoEventos.size()) {
            TipoEvento tipo = listaTipoEventos.get(pos);
            String detalles = "ID: " + tipo.getIdTipoEvento() + "\n" +
                    "Nombre: " + tipo.getNombreTipoEvento() + "\n" +
                    "Descripción: " + tipo.getDescripcionTipoEvento() + "\n" +
                    "Monto Mínimo: $" + tipo.getMontoMinimo() + "\n" +
                    "Monto Máximo: $" + tipo.getMontoMaximo();
            tvResultado.setText(detalles);
        } else {
            tvResultado.setText("No se encontró el Tipo de Evento.");
        }
    }
}
