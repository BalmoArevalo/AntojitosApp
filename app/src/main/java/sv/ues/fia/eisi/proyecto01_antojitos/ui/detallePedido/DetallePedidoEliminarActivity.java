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
import sv.ues.fia.eisi.proyecto01_antojitos.ui.datosProducto.*;

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
                    String nombreProd = (p != null) ? p.getNombreProducto() : getString(R.string.detalle_pedido_consultar_desconocido);

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
        items.add(getString(R.string.detalle_pedido_seleccione));
        for (Pedido p : pedidos) {
            String label = getString(R.string.detalle_pedido_editar_prefijo_pedido) + " " + p.getIdPedido();
            items.add(label);
            pedidosMap.put(label, p);
        }
        spinnerPedido.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void cargarDetalles(int idPedido) {
        detallesMap.clear();
        List<DetallePedido> lista = detallePedidoDAO.obtenerPorPedido(idPedido);
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.detalle_pedido_seleccione));
        for (DetallePedido d : lista) {
            String label = getString(R.string.detalle_pedido_editar_prefijo_detalle) + " " + d.getIdDetallePedido() +
                    getString(R.string.detalle_pedido_editar_sep_producto) + d.getIdProducto();
            items.add(label);
            detallesMap.put(label, d);
        }
        spinnerDetalle.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void limpiarDetalles() {
        spinnerDetalle.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                Collections.singletonList(getString(R.string.detalle_pedido_seleccione))));
        textViewDetalle.setText("");
        detalleSeleccionado = null;
        btnEliminar.setEnabled(false);
    }

    private void eliminarDetalle() {
        if (detalleSeleccionado == null) return;

        new android.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.detalle_pedido_eliminar_confirmar_titulo))
                .setMessage(getString(R.string.detalle_pedido_eliminar_confirmar_mensaje))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    int idPedido = detalleSeleccionado.getIdPedido();
                    int idProducto = detalleSeleccionado.getIdProducto();
                    int cantidad = detalleSeleccionado.getCantidad();

                    Pedido pedido = pedidoDAO.consultarPorId(idPedido);
                    if (pedido == null) {
                        Toast.makeText(this, getString(R.string.detalle_pedido_eliminar_toast_error_pedido), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int idSucursal = pedido.getIdSucursal();

                    DatosProductoDAO datosProductoDAO = new DatosProductoDAO(db);
                    DatosProducto dp = datosProductoDAO.find(idSucursal, idProducto);
                    if (dp != null) {
                        dp.setStock(dp.getStock() + cantidad);
                        datosProductoDAO.update(dp);
                    }

                    int filas = detallePedidoDAO.eliminar(detalleSeleccionado.getIdDetallePedido());
                    if (filas > 0) {
                        Toast.makeText(this, getString(R.string.detalle_pedido_eliminar_toast_ok), Toast.LENGTH_SHORT).show();
                        limpiarDetalles();
                        cargarDetalles(idPedido);
                        detalleSeleccionado = null;
                    } else {
                        Toast.makeText(this, getString(R.string.detalle_pedido_eliminar_toast_error), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}