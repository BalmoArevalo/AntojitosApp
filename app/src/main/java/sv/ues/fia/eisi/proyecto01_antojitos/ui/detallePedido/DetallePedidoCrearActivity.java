package sv.ues.fia.eisi.proyecto01_antojitos.ui.detallePedido;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import java.util.*;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido.Pedido;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido.PedidoDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.Producto;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.ProductoDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.datosProducto.*;

public class DetallePedidoCrearActivity extends AppCompatActivity {

    private Spinner spinnerPedido, spinnerProducto;
    private EditText editTextCantidad;
    private TextView textViewPrecioUnitario, textViewSubtotal;
    private Button btnGuardar;

    private SQLiteDatabase db;
    private DetallePedidoDAO detallePedidoDAO;
    private PedidoDAO pedidoDAO;
    private ProductoDAO productoDAO;

    private Map<String, Pedido> pedidosMap = new HashMap<>();
    private Map<String, Producto> productosMap = new HashMap<>();
    private double precioActual = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido_crear);

        // UI
        spinnerPedido = findViewById(R.id.spinnerPedido);
        spinnerProducto = findViewById(R.id.spinnerProducto);
        editTextCantidad = findViewById(R.id.editTextCantidad);
        textViewPrecioUnitario = findViewById(R.id.textViewPrecioUnitario);
        textViewSubtotal = findViewById(R.id.textViewSubtotal);
        btnGuardar = findViewById(R.id.btnGuardarDetalle);

        // BD y DAOs
        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        detallePedidoDAO = new DetallePedidoDAO(db);
        pedidoDAO = new PedidoDAO(db);
        productoDAO = new ProductoDAO(db);

        cargarPedidos();

        spinnerPedido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    limpiarProductos();
                    return;
                }
                String key = parent.getItemAtPosition(position).toString();
                Pedido pedido = pedidosMap.get(key);
                cargarProductosPorSucursal(pedido.getIdSucursal());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });

        spinnerProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    textViewPrecioUnitario.setText("--");
                    precioActual = 0;
                    actualizarSubtotal();
                    return;
                }
                String key = parent.getItemAtPosition(pos).toString();
                Producto prod = productosMap.get(key);
                precioActual = productoDAO.obtenerPrecioProductoEnSucursal(prod.getIdProducto(), obtenerSucursalDelPedido());
                textViewPrecioUnitario.setText(String.format(Locale.getDefault(), "$ %.2f", precioActual));
                actualizarSubtotal();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        editTextCantidad.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualizarSubtotal();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnGuardar.setOnClickListener(v -> guardarDetalle());
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

    private void cargarProductosPorSucursal(int idSucursal) {
        productosMap.clear();
        List<Producto> productos = productoDAO.obtenerProductosPorSucursal(idSucursal);
        List<String> items = new ArrayList<>();
        items.add("Seleccione");
        for (Producto p : productos) {
            String label = p.getIdProducto() + " - " + p.getNombreProducto();
            items.add(label);
            productosMap.put(label, p);
        }
        spinnerProducto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void limpiarProductos() {
        productosMap.clear();
        spinnerProducto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Collections.singletonList("Seleccione")));
        textViewPrecioUnitario.setText("--");
        textViewSubtotal.setText("--");
        precioActual = 0;
    }

    private void actualizarSubtotal() {
        String cantidadStr = editTextCantidad.getText().toString().trim();
        int cantidad = (cantidadStr.isEmpty()) ? 0 : Integer.parseInt(cantidadStr);
        double subtotal = cantidad * precioActual;
        textViewSubtotal.setText(String.format(Locale.getDefault(), "$ %.2f", subtotal));
    }

    private int obtenerSucursalDelPedido() {
        int pos = spinnerPedido.getSelectedItemPosition();
        if (pos <= 0) return -1;
        Pedido pedido = pedidosMap.get(spinnerPedido.getSelectedItem().toString());
        return pedido.getIdSucursal();
    }

    private void guardarDetalle() {
        String pedidoKey = spinnerPedido.getSelectedItem().toString();
        String productoKey = spinnerProducto.getSelectedItem().toString();

        Pedido pedidoSeleccionado = pedidosMap.get(pedidoKey);
        Producto productoSeleccionado = productosMap.get(productoKey);

        if (pedidoSeleccionado == null || productoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un pedido y un producto válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        String cantidadStr = editTextCantidad.getText().toString().trim();
        if (cantidadStr.isEmpty()) {
            Toast.makeText(this, "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);
        int idSucursal = pedidoSeleccionado.getIdSucursal();

        // Validar stock
        DatosProductoDAO datosProductoDAO = new DatosProductoDAO(db);
        DatosProducto dp = datosProductoDAO.find(idSucursal, productoSeleccionado.getIdProducto());

        if (dp == null) {
            Toast.makeText(this, "No hay datos del producto en esta sucursal", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cantidad > dp.getStock()) {
            Toast.makeText(this, "Stock insuficiente. Solo quedan " + dp.getStock(), Toast.LENGTH_LONG).show();
            return;
        }

        double subtotal = cantidad * precioActual;

        // Preparar objeto
        DetallePedido detalle = new DetallePedido();
        detalle.setIdPedido(pedidoSeleccionado.getIdPedido());
        detalle.setIdProducto(productoSeleccionado.getIdProducto());
        detalle.setCantidad(cantidad);
        detalle.setSubtotal(subtotal);

        // Actualizar stock primero
        dp.setStock(dp.getStock() - cantidad);
        int actualizado = datosProductoDAO.update(dp);
        if (actualizado <= 0) {
            Toast.makeText(this, "Error al actualizar el stock", Toast.LENGTH_SHORT).show();
            return;
        }

        long idInsertado = detallePedidoDAO.insertar(detalle);
        if (idInsertado > 0) {
            Toast.makeText(this, "Detalle insertado (ID: " + idInsertado + ")", Toast.LENGTH_LONG).show();
            resetearFormulario();
        } else {
            // Revertir el stock si falla
            dp.setStock(dp.getStock() + cantidad);
            datosProductoDAO.update(dp);
            Toast.makeText(this, "Error al insertar detalle", Toast.LENGTH_SHORT).show();
        }
    }
    private void resetearFormulario() {
        spinnerPedido.setSelection(0);
        limpiarProductos();
        editTextCantidad.setText("");
        textViewSubtotal.setText("--");
    }

}
