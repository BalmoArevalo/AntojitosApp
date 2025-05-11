package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class RepartoPedidoConsultarActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteReparto;
    private Button btnBuscar;
    private LinearLayout layoutResultado;
    private TextView textIdPedido, textIdReparto, textHoraAsignacion, textUbicacion, textFechaEntrega;

    private RepartoPedidoDAO dao;
    private List<RepartoPedido> listaRepartos;
    private Map<String, RepartoPedido> mapRepartos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reparto_pedido_consultar);

        autoCompleteReparto = findViewById(R.id.autoCompleteReparto);
        btnBuscar = findViewById(R.id.btnBuscarReparto);
        layoutResultado = findViewById(R.id.layoutResultado);
        textIdPedido = findViewById(R.id.textIdPedido);
        textIdReparto = findViewById(R.id.textIdReparto);
        textHoraAsignacion = findViewById(R.id.textHoraAsignacion);
        textUbicacion = findViewById(R.id.textUbicacion);
        textFechaEntrega = findViewById(R.id.textFechaEntrega);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        dao = new RepartoPedidoDAO(db);

        cargarRepartos();

        btnBuscar.setOnClickListener(v -> mostrarDatos());
    }

    private void cargarRepartos() {
        listaRepartos = dao.obtenerTodos();
        mapRepartos = new HashMap<>();
        List<String> labels = new ArrayList<>();

        for (RepartoPedido r : listaRepartos) {
            String label = getString(R.string.repartopedido_item_label, r.getIdPedido(), r.getIdRepartoPedido());
            labels.add(label);
            mapRepartos.put(label, r);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, labels);
        autoCompleteReparto.setAdapter(adapter);
    }

    private void mostrarDatos() {
        String seleccionado = autoCompleteReparto.getText().toString();
        RepartoPedido r = mapRepartos.get(seleccionado);

        if (r == null) {
            Toast.makeText(this, getString(R.string.repartopedido_msj_seleccione_valido), Toast.LENGTH_SHORT).show();
            return;
        }

        layoutResultado.setVisibility(View.VISIBLE);
        textIdPedido.setText(getString(R.string.repartopedido_label_pedido) + ": " + r.getIdPedido());
        textIdReparto.setText(getString(R.string.repartopedido_label_repartidor) + ": " + r.getIdRepartoPedido());
        textHoraAsignacion.setText(getString(R.string.repartopedido_label_hora_asignacion) + ": " + r.getHoraAsignacion());
        textUbicacion.setText(getString(R.string.repartopedido_label_ubicacion) + ": " + r.getUbicacionEntrega());
        textFechaEntrega.setText(getString(R.string.repartopedido_label_fecha_entrega) + ": " +
                (r.getFechaHoraEntrega() == null || r.getFechaHoraEntrega().isEmpty()
                        ? getString(R.string.repartopedido_texto_no_entrega)
                        : r.getFechaHoraEntrega()));
    }
}
