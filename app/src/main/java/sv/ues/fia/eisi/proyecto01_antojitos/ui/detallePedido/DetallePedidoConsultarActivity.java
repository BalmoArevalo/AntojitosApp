package sv.ues.fia.eisi.proyecto01_antojitos.ui.detallePedido;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido.Pedido;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido.PedidoDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.Producto;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.ProductoDAO;

public class DetallePedidoConsultarActivity extends AppCompatActivity {

    private Spinner spinnerPedido;
    private TextView textViewResultado;

    private SQLiteDatabase db;
    private DetallePedidoDAO detallePedidoDAO;
    private ProductoDAO productoDAO;
    private PedidoDAO pedidoDAO;

    private Map<String, Pedido> pedidosMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido_consultar);

        spinnerPedido = findViewById(R.id.spinnerPedido);
        textViewResultado = findViewById(R.id.textViewResultadoDetalles);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();
        detallePedidoDAO = new DetallePedidoDAO(db);
        productoDAO = new ProductoDAO(db);
        pedidoDAO = new PedidoDAO(db);

        cargarPedidos();

        spinnerPedido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos > 0) {
                    Pedido p = pedidosMap.get(parent.getItemAtPosition(pos).toString());
                    mostrarDetalles(p.getIdPedido());
                } else {
                    textViewResultado.setText("");
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarPedidos() {
        List<Pedido> pedidos = pedidoDAO.obtenerTodos();
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.detalle_pedido_consultar_seleccione));
        for (Pedido p : pedidos) {
            String label = getString(R.string.detalle_pedido_consultar_prefijo_pedido) + " " + p.getIdPedido();
            items.add(label);
            pedidosMap.put(label, p);
        }
        spinnerPedido.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void mostrarDetalles(int idPedido) {
        List<DetallePedido> lista = detallePedidoDAO.obtenerPorPedido(idPedido);

        if (lista.isEmpty()) {
            textViewResultado.setText(getString(R.string.detalle_pedido_consultar_sin_productos));
            return;
        }

        StringBuilder resultado = new StringBuilder(getString(R.string.detalle_pedido_consultar_encabezado, idPedido));
        for (DetallePedido d : lista) {
            Producto producto = productoDAO.obtenerProductoPorId(d.getIdProducto());
            String nombreProducto = (producto != null) ? producto.getNombreProducto() : getString(R.string.detalle_pedido_consultar_desconocido);

            resultado.append(getString(
                    R.string.detalle_pedido_consultar_item_producto,
                    nombreProducto,
                    d.getCantidad(),
                    d.getSubtotal()
            ));
        }

        textViewResultado.setText(resultado.toString());
    }
}