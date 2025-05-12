package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class RepartoPedidoEliminarActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteEliminar;
    private Button btnCargar, btnEliminar;
    private TextView tvPreview;

    private RepartoPedidoDAO dao;
    private Map<String, RepartoPedido> mapRepartos;
    private RepartoPedido seleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reparto_pedido_eliminar);

        autoCompleteEliminar = findViewById(R.id.autoCompleteEliminar);
        btnCargar = findViewById(R.id.btnCargar);
        btnEliminar = findViewById(R.id.btnEliminar);
        tvPreview = findViewById(R.id.tvPreview);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        dao = new RepartoPedidoDAO(db);

        cargarOpciones();

        btnCargar.setOnClickListener(v -> mostrarDatos());
        btnEliminar.setOnClickListener(v -> confirmarEliminar());
    }

    private void cargarOpciones() {
        List<RepartoPedido> lista = dao.obtenerTodos();
        mapRepartos = new HashMap<>();
        List<String> opciones = new ArrayList<>();

        for (RepartoPedido r : lista) {
            String label = getString(R.string.repartopedido_item_label, r.getIdPedido(), r.getIdRepartoPedido());
            mapRepartos.put(label, r);
            opciones.add(label);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, opciones);
        autoCompleteEliminar.setAdapter(adapter);
    }

    private void mostrarDatos() {
        String seleccion = autoCompleteEliminar.getText().toString().trim();
        seleccionado = null;

        for (Map.Entry<String, RepartoPedido> entry : mapRepartos.entrySet()) {
            if (entry.getKey().equals(seleccion)) {
                seleccionado = entry.getValue();
                break;
            }
        }

        if (seleccionado == null) {
            Toast.makeText(this, getString(R.string.repartopedido_toast_seleccione_valido), Toast.LENGTH_SHORT).show();
            tvPreview.setText("");
            return;
        }

        String datos = getString(R.string.repartopedido_label_pedido) + ": " + seleccionado.getIdPedido() + "\n"
                + getString(R.string.repartopedido_label_repartidor) + ": " + seleccionado.getIdRepartoPedido() + "\n"
                + getString(R.string.repartopedido_label_fecha_asignacion) + ": " + seleccionado.getFechaHoraAsignacion() + "\n"
                + getString(R.string.repartopedido_label_ubicacion) + ": " + seleccionado.getUbicacionEntrega() + "\n"
                + getString(R.string.repartopedido_label_fecha_entrega) + ": "
                + (seleccionado.getFechaHoraEntrega() == null || seleccionado.getFechaHoraEntrega().isEmpty()
                ? getString(R.string.repartopedido_texto_no_entrega)
                : seleccionado.getFechaHoraEntrega());

        tvPreview.setText(datos);
    }


    private void confirmarEliminar() {
        if (seleccionado == null) {
            Toast.makeText(this, getString(R.string.repartopedido_toast_seleccione_valido), Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.repartopedido_confirm_titulo))
                .setMessage(getString(R.string.repartopedido_confirm_mensaje))
                .setPositiveButton(getString(R.string.repartopedido_confirm_si), (dialog, which) -> eliminar())
                .setNegativeButton(getString(R.string.repartopedido_confirm_no), null)
                .show();
    }

    private void eliminar() {
        int result = dao.eliminar(seleccionado.getIdPedido(), seleccionado.getIdRepartoPedido());
        if (result > 0) {
            Toast.makeText(this, getString(R.string.repartopedido_toast_eliminado_ok), Toast.LENGTH_SHORT).show();
            autoCompleteEliminar.setText("");
            tvPreview.setText("");
            cargarOpciones();
        } else {
            Toast.makeText(this, getString(R.string.repartopedido_toast_eliminado_error), Toast.LENGTH_SHORT).show();
        }
    }
}