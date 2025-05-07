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

public class DetallePedidoEliminarActivity extends AppCompatActivity {

    private Spinner spinnerPedido, spinnerDetalle;
    private TextView textViewDetalle;
    private Button btnEliminar;

    private SQLiteDatabase db;
    private DetallePedidoDAO detallePedidoDAO;
    private ProductoDAO productoDAO;
    private PedidoDAO pedidoDAO;

    private Map<String, Pedido> pedidosMap = new HashMap<>();
    private Map<String, DetallePedido> detallesMap = new HashMap<>();
    private DetallePedido detalleSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido_eliminar);

        spinnerPedido = findViewById(R.id.spinnerPedido);
        spinnerDetalle = findViewById(R.id.spinnerDetalle);
        textViewDetalle = findViewById(R.id.textViewDetalle);
        btnEliminar = findViewById(R.id.btnEliminarDetalle);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        detallePedidoDAO = new DetallePedidoDAO(db);
        productoDAO = new ProductoDAO(db);
        pedidoDAO = new PedidoDAO(db);

        cargarPedidos();

        spinnerPedido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos > 0) {
                    Pedido p = pedidosMap.get(parent.getItemAtPosition(pos).toString());
                    cargarDetalles(p.getIdPedido());
                } else {
                    limpiarDetalles();
                }
                btnEliminar.setEnabled(false);
                textViewDetalle.setText("");
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerDetalle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos <= 0) {
                    detalleSeleccionado = null;
                    textViewDetalle.setText("");
                    btnEliminar.setEnabled(false);
                    return;
                }

                detalleSeleccionado = detallesMap.get(parent.getItemAtPosition(pos).toString());
                if (detalleSeleccionado != null) {
                    Producto p = productoDAO.obtenerProductoPorId(detalleSeleccionado.getIdProducto());
                    String nombreProd = (p != null) ? p.getNombreProducto() : "Desconocido";

                    String info = "ID Detalle: " + detalleSeleccionado.getIdDetallePedido() + "\n"
                            + "Producto: " + nombreProd + "\n"
                            + "Cantidad: " + detalleSeleccionado.getCantidad() + "\n"
                            + "Subtotal: $" + String.format(Locale.getDefault(), "%.2f", detalleSeleccionado.getSubtotal());

                    textViewDetalle.setText(info);
                    btnEliminar.setEnabled(true);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnEliminar.setOnClickListener(v -> eliminarDetalle());
    }

    private void cargarPedidos() {
        List<Pedido> pedidos = pedidoDAO.obtenerTodos();
        List<String> items = new ArrayList<>();
        items.add("Seleccione");
        for (Pedido p : pedidos) {
            String label = "Pedido " + p.getIdPedido();
            items.add(label);
            pedidosMap.put(label, p);
        }
        spinnerPedido.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void cargarDetalles(int idPedido) {
        detallesMap.clear();
        List<DetallePedido> lista = detallePedidoDAO.obtenerPorPedido(idPedido);
        List<String> items = new ArrayList<>();
        items.add("Seleccione");
        for (DetallePedido d : lista) {
            String label = "Detalle " + d.getIdDetallePedido() + " - Prod " + d.getIdProducto();
            items.add(label);
            detallesMap.put(label, d);
        }
        spinnerDetalle.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void limpiarDetalles() {
        spinnerDetalle.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Collections.singletonList("Seleccione")));
        textViewDetalle.setText("");
        detalleSeleccionado = null;
        btnEliminar.setEnabled(false);
    }

    private void eliminarDetalle() {
        if (detalleSeleccionado == null) return;

        // Mostrar diálogo de confirmación
        new android.app.AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este detalle del pedido?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    int idPedido = detalleSeleccionado.getIdPedido();

                    int filas = detallePedidoDAO.eliminar(detalleSeleccionado.getIdDetallePedido());
                    if (filas > 0) {
                        Toast.makeText(this, "Detalle eliminado correctamente", Toast.LENGTH_SHORT).show();
                        limpiarDetalles();
                        cargarDetalles(idPedido);
                    } else {
                        Toast.makeText(this, "Error al eliminar el detalle", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
