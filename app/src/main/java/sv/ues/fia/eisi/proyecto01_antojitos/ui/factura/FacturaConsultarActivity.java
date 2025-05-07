package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer; // No olvides este import si falta
import androidx.lifecycle.ViewModelProvider;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
// import android.view.Gravity; // No se usa directamente, se puede quitar
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class FacturaConsultarActivity extends AppCompatActivity {

    private FacturaViewModel facturaViewModel;
    private static final String TAG = "FacturaConsultarAct"; // Tag para Logs

    // Componentes UI Entrada
    private Spinner spinnerConsultaPedido;
    private Button btnConsultarFactura;

    // Componentes UI Resultados
    private ScrollView scrollViewResultados;
    private LinearLayout layoutResultadosList; // LinearLayout dentro del ScrollView

    private List<Integer> consultaPedidoIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura_consultar);

        facturaViewModel = new ViewModelProvider(this).get(FacturaViewModel.class);

        spinnerConsultaPedido = findViewById(R.id.spinnerConsultaPedido);
        btnConsultarFactura = findViewById(R.id.btnConsultarFactura);

        scrollViewResultados = findViewById(R.id.scrollViewResultados);
        layoutResultadosList = findViewById(R.id.layoutResultadosList);

        cargarSpinnerConsultaPedidos();
        ocultarYLimpiarResultados();

        btnConsultarFactura.setOnClickListener(v -> realizarConsultaFacturaDePedido());

        // Observar la FACTURA SELECCIONADA del ViewModel
        facturaViewModel.getFacturaSeleccionada().observe(this, new Observer<Factura>() {
            @Override
            public void onChanged(Factura factura) {
                ocultarYLimpiarResultados(); // Limpiar antes de mostrar nuevos resultados
                if (factura != null) {
                    // Se encontró una factura, mostrar sus detalles
                    mostrarDetallesFactura(factura);
                } else {
                    // Si la factura es null después de una consulta activa,
                    // significa que no se encontró una factura para el pedido seleccionado.
                    if (spinnerConsultaPedido.getSelectedItemPosition() > 0) { // Asegurarse que se intentó una consulta real
                        Toast.makeText(FacturaConsultarActivity.this, "No se encontró factura para este pedido.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Observer: Factura es null, no se muestra nada.");
                    }
                }
            }
        });
    }

    private void cargarSpinnerConsultaPedidos() {
        consultaPedidoIds.clear();
        List<String> pedidoDescripciones = new ArrayList<>();
        pedidoDescripciones.add("Seleccione un Pedido...");
        consultaPedidoIds.add(-1); // ID inválido para el placeholder

        DBHelper localDbHelper = new DBHelper(this);
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = localDbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT ID_PEDIDO FROM PEDIDO ORDER BY ID_PEDIDO ASC", null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                consultaPedidoIds.add(id);
                pedidoDescripciones.add("Pedido #" + id);
            }
            Log.d(TAG, "Pedidos cargados en spinner: " + pedidoDescripciones.size());
        } catch (SQLiteException e) {
            Log.e(TAG, "Error al cargar pedidos para el spinner", e);
            Toast.makeText(this, "Error al cargar pedidos", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
            // No es necesario cerrar localDbHelper aquí si getReadableDatabase/getWritableDatabase manejan el ciclo de vida de la BD.
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, pedidoDescripciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConsultaPedido.setAdapter(adapter);
    }

    private void realizarConsultaFacturaDePedido() {
        int pedidoPos = spinnerConsultaPedido.getSelectedItemPosition();
        if (pedidoPos <= 0) { // El primer item es "Seleccione un Pedido..."
            Toast.makeText(this, "Seleccione un pedido válido para consultar su factura.", Toast.LENGTH_SHORT).show();
            ocultarYLimpiarResultados();
            return;
        }
        int selectedPedidoId = consultaPedidoIds.get(pedidoPos);
        Log.d(TAG, "Realizando consulta para Pedido ID: " + selectedPedidoId);

        // Llamar al ViewModel para que cargue la factura de ESE pedido.
        // El Observer se encargará de actualizar la UI.
        facturaViewModel.consultarFacturaDePedido(selectedPedidoId);
    }

    private void mostrarDetallesFactura(Factura factura) {
        if (layoutResultadosList == null || scrollViewResultados == null || factura == null) {
            Log.w(TAG, "mostrarDetallesFactura: Componentes UI null o factura null.");
            return;
        }
        Log.d(TAG, "Mostrando detalles para Factura ID: " + factura.getIdFactura());

        // Crear un TextView para mostrar los detalles de la ÚNICA factura
        TextView tvFacturaDetalle = new TextView(this);
        String detalles = String.format(Locale.getDefault(),
                "Factura ID: %d\nPedido ID: %d\nFecha Emisión: %s\nMonto Total: $%.2f\nTipo de Pago: %s\nEstado: %s",
                factura.getIdFactura(),
                factura.getIdPedido(), // Es bueno mostrar a qué pedido pertenece
                factura.getFechaEmision(),
                factura.getMontoTotal(),
                factura.getTipoPago(),
                (factura.getPagado() == 1 ? "Pagado" : "Pendiente")
        );
        tvFacturaDetalle.setText(detalles);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16); // Margen inferior
        tvFacturaDetalle.setLayoutParams(params);
        tvFacturaDetalle.setTextSize(16);
        tvFacturaDetalle.setPadding(8, 8, 8, 8);
        // Si tienes el drawable 'borde_simple_textview.xml' y quieres usarlo:
        // tvFacturaDetalle.setBackgroundResource(R.drawable.borde_simple_textview);

        layoutResultadosList.addView(tvFacturaDetalle); // Añadir el TextView al LinearLayout
        scrollViewResultados.setVisibility(View.VISIBLE);
    }

    private void ocultarYLimpiarResultados() {
        if (layoutResultadosList != null) {
            layoutResultadosList.removeAllViews(); // Limpiar vistas anteriores
            Log.d(TAG, "Resultados limpiados.");
        }
        if (scrollViewResultados != null) {
            scrollViewResultados.setVisibility(View.GONE);
        }
    }
}