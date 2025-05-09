package sv.ues.fia.eisi.proyecto01_antojitos.ui.detallePedido;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido.Pedido;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido.PedidoDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.Producto;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.ProductoDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.datosProducto.*;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.categoriaProducto.*;

public class DetallePedidoCrearActivity extends AppCompatActivity {

    private Spinner spinnerPedido, spinnerProducto;
    private EditText editTextCantidad;
    private TextView textViewPrecioUnitario, textViewSubtotal;
    private Button btnGuardar;

    private SQLiteDatabase db;
    private DetallePedidoDAO detallePedidoDAO;
    private PedidoDAO pedidoDAO;
    private ProductoDAO productoDAO;
    private CategoriaProductoDAO categoriaDAO;

    private List<CategoriaProducto> listaCategorias;
    private Map<String, Pedido> pedidosMap = new HashMap<>();
    private Map<String, Producto> productosMap = new HashMap<>();
    private double precioActual = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido_crear);

        spinnerPedido = findViewById(R.id.spinnerPedido);
        spinnerProducto = findViewById(R.id.spinnerProducto);
        editTextCantidad = findViewById(R.id.editTextCantidad);
        textViewPrecioUnitario = findViewById(R.id.textViewPrecioUnitario);
        textViewSubtotal = findViewById(R.id.textViewSubtotal);
        btnGuardar = findViewById(R.id.btnGuardarDetalle);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        detallePedidoDAO = new DetallePedidoDAO(db);
        pedidoDAO = new PedidoDAO(db);
        productoDAO = new ProductoDAO(db);
        categoriaDAO = new CategoriaProductoDAO(db);

        listaCategorias = categoriaDAO.obtenerTodos(false);
        actualizarDisponibilidadSegunHora(listaCategorias);

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
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    textViewPrecioUnitario.setText(getString(R.string.detalle_pedido_vacio));
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
        items.add(getString(R.string.detalle_pedido_seleccione));
        for (Pedido p : pedidos) {
            String label = getString(R.string.detalle_pedido_prefijo_pedido) + " " + p.getIdPedido();
            items.add(label);
            pedidosMap.put(label, p);
        }
        spinnerPedido.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void cargarProductosPorSucursal(int idSucursal) {
        productosMap.clear();
        List<Producto> productos = productoDAO.obtenerProductosPorSucursal(idSucursal);
        List<String> items = new ArrayList<>();

        for (Producto p : productos) {
            CategoriaProducto cat = categoriaDAO.obtenerPorId(p.getIdCategoriaProducto());
            if (cat != null && cat.getDisponibleCategoria() == 1) {
                String label = p.getIdProducto() + " - " + p.getNombreProducto();
                items.add(label);
                productosMap.put(label, p);
            }
        }

        if (items.isEmpty()) {
            items.add(getString(R.string.detalle_pedido_no_productos));
            spinnerProducto.setEnabled(false);
        } else {
            items.add(0, getString(R.string.detalle_pedido_seleccione));
            spinnerProducto.setEnabled(true);
        }

        spinnerProducto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void actualizarDisponibilidadSegunHora(List<CategoriaProducto> lista) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String ahoraStr = sdf.format(new Date());

        try {
            Date ahora = sdf.parse(ahoraStr);

            for (CategoriaProducto c : lista) {
                Date desde = sdf.parse(c.getHoraDisponibleDesde());
                Date hasta = sdf.parse(c.getHoraDisponibleHasta());

                boolean disponible = ahora.equals(desde) || ahora.equals(hasta)
                        || (ahora.after(desde) && ahora.before(hasta));

                int nuevoEstado = disponible ? 1 : 0;

                if (c.getDisponibleCategoria() != nuevoEstado) {
                    c.setDisponibleCategoria(nuevoEstado);
                    categoriaDAO.actualizar(c);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void limpiarProductos() {
        productosMap.clear();
        spinnerProducto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                Collections.singletonList(getString(R.string.detalle_pedido_seleccione))));
        textViewPrecioUnitario.setText(getString(R.string.detalle_pedido_vacio));
        textViewSubtotal.setText(getString(R.string.detalle_pedido_vacio));
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
            Toast.makeText(this, getString(R.string.detalle_pedido_toast_seleccion_invalida), Toast.LENGTH_SHORT).show();
            return;
        }

        String cantidadStr = editTextCantidad.getText().toString().trim();
        if (cantidadStr.isEmpty()) {
            Toast.makeText(this, getString(R.string.detalle_pedido_toast_cantidad_invalida), Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);
        int idSucursal = pedidoSeleccionado.getIdSucursal();

        DatosProductoDAO datosProductoDAO = new DatosProductoDAO(db);
        DatosProducto dp = datosProductoDAO.find(idSucursal, productoSeleccionado.getIdProducto());

        if (dp == null) {
            Toast.makeText(this, getString(R.string.detalle_pedido_toast_no_datos_producto), Toast.LENGTH_SHORT).show();
            return;
        }

        if (cantidad > dp.getStock()) {
            Toast.makeText(this, getString(R.string.detalle_pedido_toast_stock_insuficiente, dp.getStock()), Toast.LENGTH_LONG).show();
            return;
        }

        double subtotal = cantidad * precioActual;

        DetallePedido detalle = new DetallePedido();
        detalle.setIdPedido(pedidoSeleccionado.getIdPedido());
        detalle.setIdProducto(productoSeleccionado.getIdProducto());
        detalle.setCantidad(cantidad);
        detalle.setSubtotal(subtotal);

        dp.setStock(dp.getStock() - cantidad);
        int actualizado = datosProductoDAO.update(dp);
        if (actualizado <= 0) {
            Toast.makeText(this, getString(R.string.detalle_pedido_toast_error_stock), Toast.LENGTH_SHORT).show();
            return;
        }

        long idInsertado = detallePedidoDAO.insertar(detalle);
        if (idInsertado > 0) {
            Toast.makeText(this, getString(R.string.detalle_pedido_toast_insertado_ok, idInsertado), Toast.LENGTH_LONG).show();
            resetearFormulario();
        } else {
            dp.setStock(dp.getStock() + cantidad);
            datosProductoDAO.update(dp);
            Toast.makeText(this, getString(R.string.detalle_pedido_toast_insertado_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void resetearFormulario() {
        spinnerPedido.setSelection(0);
        limpiarProductos();
        editTextCantidad.setText("");
        textViewSubtotal.setText(getString(R.string.detalle_pedido_vacio));
    }
}