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

public class DetallePedidoEditarActivity extends AppCompatActivity {

    private EditText editTextBuscarIdDetalle, editTextCantidad;
    private Spinner spinnerPedido, spinnerProducto;
    private TextView textViewSubtotal;
    private Button btnBuscarDetalle, btnActualizar;

    private SQLiteDatabase db;
    private DetallePedidoDAO detallePedidoDAO;
    private PedidoDAO pedidoDAO;
    private ProductoDAO productoDAO;

    private Map<String, Pedido> pedidosMap = new HashMap<>();
    private Map<String, Producto> productosMap = new HashMap<>();
    private DetallePedido detalleActual;
    private double precioActual = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido_editar);

        // UI
        editTextBuscarIdDetalle = findViewById(R.id.editTextBuscarIdDetalle);
        editTextCantidad = findViewById(R.id.editTextCantidad);
        spinnerPedido = findViewById(R.id.spinnerPedido);
        spinnerProducto = findViewById(R.id.spinnerProducto);
        textViewSubtotal = findViewById(R.id.textViewSubtotal);
        btnBuscarDetalle = findViewById(R.id.btnBuscarDetalle);
        btnActualizar = findViewById(R.id.btnActualizarDetalle);

        // DB
        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        detallePedidoDAO = new DetallePedidoDAO(db);
        pedidoDAO = new PedidoDAO(db);
        productoDAO = new ProductoDAO(db);

        cargarPedidos();

        // Listeners
        spinnerProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    precioActual = 0;
                    actualizarSubtotal();
                    return;
                }
                int idSucursal = obtenerSucursalDelPedido();
                int idProducto = productosMap.get(spinnerProducto.getSelectedItem().toString()).getIdProducto();
                precioActual = productoDAO.obtenerPrecioSucursal(idProducto, idSucursal);
                actualizarSubtotal();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        editTextCantidad.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualizarSubtotal();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        btnBuscarDetalle.setOnClickListener(v -> buscarDetalle());
        btnActualizar.setOnClickListener(v -> actualizarDetalle());
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

        spinnerPedido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    spinnerProducto.setAdapter(new ArrayAdapter<>(DetallePedidoEditarActivity.this, android.R.layout.simple_spinner_dropdown_item, Collections.singletonList("Seleccione")));
                    return;
                }
                Pedido p = pedidosMap.get(parent.getItemAtPosition(position).toString());
                cargarProductosPorSucursal(p.getIdSucursal());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarProductosPorSucursal(int idSucursal) {
        productosMap.clear();
        List<Producto> productos = productoDAO.obtenerPorSucursal(idSucursal);
        List<String> items = new ArrayList<>();
        items.add("Seleccione");
        for (Producto p : productos) {
            String label = p.getIdProducto() + " - " + p.getNombreProducto();
            items.add(label);
            productosMap.put(label, p);
        }
        spinnerProducto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void buscarDetalle() {
        String idStr = editTextBuscarIdDetalle.getText().toString().trim();
        if (idStr.isEmpty()) {
            Toast.makeText(this, "Ingrese un ID válido", Toast.LENGTH_SHORT).show();
            return;
        }

        int idDetalle = Integer.parseInt(idStr);
        detalleActual = detallePedidoDAO.consultarPorId(idDetalle);
        if (detalleActual == null) {
            Toast.makeText(this, "Detalle no encontrado", Toast.LENGTH_SHORT).show();
            btnActualizar.setEnabled(false);
            return;
        }

        setearValoresEnFormulario(detalleActual);
        btnActualizar.setEnabled(true);
    }

    private void setearValoresEnFormulario(DetallePedido d) {
        seleccionarItemPorId(spinnerPedido, d.getIdPedido(), pedidosMap);
        cargarProductosPorSucursal(obtenerSucursalDelPedido());
        spinnerProducto.post(() -> seleccionarItemPorId(spinnerProducto, d.getIdProducto(), productosMap));

        editTextCantidad.setText(String.valueOf(d.getCantidad()));
        precioActual = productoDAO.obtenerPrecioSucursal(d.getIdProducto(), obtenerSucursalDelPedido());
        actualizarSubtotal();
    }

    private void seleccionarItemPorId(Spinner spinner, int id, Map<String, ?> map) {
        for (int i = 0; i < spinner.getCount(); i++) {
            String label = spinner.getItemAtPosition(i).toString();
            if (label.startsWith(id + " -")) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private int obtenerSucursalDelPedido() {
        int pos = spinnerPedido.getSelectedItemPosition();
        if (pos <= 0) return -1;
        return pedidosMap.get(spinnerPedido.getSelectedItem().toString()).getIdSucursal();
    }

    private void actualizarSubtotal() {
        String cantidadStr = editTextCantidad.getText().toString().trim();
        int cantidad = (cantidadStr.isEmpty()) ? 0 : Integer.parseInt(cantidadStr);
        double subtotal = cantidad * precioActual;
        textViewSubtotal.setText(String.format(Locale.getDefault(), "$ %.2f", subtotal));
    }

    private void actualizarDetalle() {
        if (detalleActual == null) return;

        String pedidoKey = spinnerPedido.getSelectedItem().toString();
        String productoKey = spinnerProducto.getSelectedItem().toString();

        Pedido pedidoSeleccionado = pedidosMap.get(pedidoKey);
        Producto productoSeleccionado = productosMap.get(productoKey);

        if (pedidoSeleccionado == null || productoSeleccionado == null) {
            Toast.makeText(this, "Debe seleccionar un pedido y producto válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        String cantidadStr = editTextCantidad.getText().toString().trim();
        if (cantidadStr.isEmpty()) {
            Toast.makeText(this, "Ingrese una cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);
        int idPedido = pedidoSeleccionado.getIdPedido();
        int idProducto = productoSeleccionado.getIdProducto();
        double subtotal = cantidad * precioActual;

        // Actualizar objeto
        detalleActual.setIdPedido(idPedido);
        detalleActual.setIdProducto(idProducto);
        detalleActual.setCantidad(cantidad);
        detalleActual.setSubtotal(subtotal);

        // Persistir
        int filas = detallePedidoDAO.actualizar(detalleActual);
        if (filas > 0) {
            Toast.makeText(this, "Detalle actualizado correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al actualizar el detalle", Toast.LENGTH_SHORT).show();
        }
    }

}
