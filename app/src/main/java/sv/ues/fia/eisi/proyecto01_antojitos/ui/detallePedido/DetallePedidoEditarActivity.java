package sv.ues.fia.eisi.proyecto01_antojitos.ui.detallePedido;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

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

public class DetallePedidoEditarActivity extends AppCompatActivity {

    private Spinner spinnerPedido, spinnerDetalle, spinnerProducto;
    private EditText editTextCantidad;
    private TextView textViewSubtotal;
    private Button btnActualizar;

    private SQLiteDatabase db;
    private DetallePedidoDAO detallePedidoDAO;
    private PedidoDAO pedidoDAO;
    private ProductoDAO productoDAO;
    private CategoriaProductoDAO categoriaDAO;

    private List<CategoriaProducto> listaCategorias;
    private Map<String, Pedido> pedidosMap = new HashMap<>();
    private Map<String, Producto> productosMap = new HashMap<>();
    private Map<String, DetallePedido> detallesMap = new HashMap<>();

    private DetallePedido detalleActual;
    private double precioActual = 0.0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido_editar);

        spinnerPedido = findViewById(R.id.spinnerPedido);
        spinnerDetalle = findViewById(R.id.spinnerDetalle);
        spinnerProducto = findViewById(R.id.spinnerProducto);
        editTextCantidad = findViewById(R.id.editTextCantidad);
        textViewSubtotal = findViewById(R.id.textViewSubtotal);
        btnActualizar = findViewById(R.id.btnActualizarDetalle);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        detallePedidoDAO = new DetallePedidoDAO(db);
        pedidoDAO = new PedidoDAO(db);
        productoDAO = new ProductoDAO(db);
        categoriaDAO = new CategoriaProductoDAO(db);

        // Actualizar disponibilidad al iniciar
        listaCategorias = categoriaDAO.obtenerTodos(false);
        actualizarDisponibilidadSegunHora(listaCategorias);

        cargarPedidos();

        spinnerPedido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    limpiarDetalles();
                    limpiarProductos();
                    return;
                }
                Pedido pedido = pedidosMap.get(parent.getItemAtPosition(pos).toString());
                cargarDetallesPedido(pedido.getIdPedido());
                cargarProductosPorSucursal(pedido.getIdSucursal());
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerDetalle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    detalleActual = null;
                    btnActualizar.setEnabled(false);
                    return;
                }

                detalleActual = detallesMap.get(parent.getItemAtPosition(pos).toString());
                if (detalleActual != null) {
                    selectProducto(detalleActual.getIdProducto());
                    editTextCantidad.setText(String.valueOf(detalleActual.getCantidad()));
                    actualizarSubtotal();
                    btnActualizar.setEnabled(true);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos <= 0 || detalleActual == null) {
                    precioActual = 0;
                    actualizarSubtotal();
                    return;
                }

                Producto prod = productosMap.get(parent.getItemAtPosition(pos).toString());
                if (prod != null) {
                    int idSucursal = pedidosMap.get(spinnerPedido.getSelectedItem().toString()).getIdSucursal();
                    precioActual = productoDAO.obtenerPrecioProductoEnSucursal(prod.getIdProducto(), idSucursal);
                    actualizarSubtotal();
                }
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

        btnActualizar.setOnClickListener(v -> actualizarDetalle());
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

    private void cargarDetallesPedido(int idPedido) {
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

    private void cargarProductosPorSucursal(int idSucursal) {
        productosMap.clear();
        List<Producto> productos = productoDAO.obtenerProductosPorSucursal(idSucursal);
        List<String> items = new ArrayList<>();

        for (Producto p : productos) {
            CategoriaProducto cat = categoriaDAO.obtenerPorId(p.getIdCategoriaProducto());

            boolean disponible = cat != null && cat.getDisponibleCategoria() == 1;

            // Asegura que el producto actual del detalle aparezca aunque esté inactivo
            if (detalleActual != null && p.getIdProducto() == detalleActual.getIdProducto()) {
                disponible = true;
            }

            if (disponible) {
                String label = p.getIdProducto() + " - " + p.getNombreProducto();
                items.add(label);
                productosMap.put(label, p);
            }
        }

        if (items.isEmpty()) {
            items.add("No hay productos disponibles en este momento");
            spinnerProducto.setEnabled(false);
        } else {
            items.add(0, "Seleccione");
            spinnerProducto.setEnabled(true);
        }

        spinnerProducto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void selectProducto(int idProducto) {
        for (int i = 1; i < spinnerProducto.getCount(); i++) {
            String label = spinnerProducto.getItemAtPosition(i).toString();
            if (label.startsWith(idProducto + " -")) {
                spinnerProducto.setSelection(i);
                return;
            }
        }
    }

    private void actualizarSubtotal() {
        String cantidadStr = editTextCantidad.getText().toString().trim();
        int cantidad = (cantidadStr.isEmpty()) ? 0 : Integer.parseInt(cantidadStr);
        double subtotal = cantidad * precioActual;
        textViewSubtotal.setText(String.format(Locale.getDefault(), "$ %.2f", subtotal));
    }

    private void actualizarDetalle() {
        if (detalleActual == null) return;

        String productoKey = spinnerProducto.getSelectedItem().toString();
        Producto productoSeleccionado = productosMap.get(productoKey);

        if (productoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un producto válido", Toast.LENGTH_SHORT).show();
            return;
        }

        String cantidadStr = editTextCantidad.getText().toString().trim();
        if (cantidadStr.isEmpty()) {
            Toast.makeText(this, "Ingrese una cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        int nuevaCantidad = Integer.parseInt(cantidadStr);
        int antiguaCantidad = detalleActual.getCantidad();
        int diferencia = nuevaCantidad - antiguaCantidad;

        int idSucursal = pedidosMap.get(spinnerPedido.getSelectedItem().toString()).getIdSucursal();
        DatosProductoDAO datosProductoDAO = new DatosProductoDAO(db);
        DatosProducto dp = datosProductoDAO.find(idSucursal, productoSeleccionado.getIdProducto());

        if (dp == null) {
            Toast.makeText(this, "No hay datos del producto en esta sucursal", Toast.LENGTH_SHORT).show();
            return;
        }

        if (diferencia > 0 && diferencia > dp.getStock()) {
            Toast.makeText(this, "Stock insuficiente. Solo quedan " + dp.getStock(), Toast.LENGTH_LONG).show();
            return;
        }

        if (diferencia != 0) {
            dp.setStock(dp.getStock() - diferencia);
            int actualizado = datosProductoDAO.update(dp);
            if (actualizado <= 0) {
                Toast.makeText(this, "Error al actualizar stock", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        double subtotal = nuevaCantidad * precioActual;

        detalleActual.setIdProducto(productoSeleccionado.getIdProducto());
        detalleActual.setCantidad(nuevaCantidad);
        detalleActual.setSubtotal(subtotal);

        int filas = detallePedidoDAO.actualizar(detalleActual);
        if (filas > 0) {
            Toast.makeText(this, "Detalle actualizado correctamente", Toast.LENGTH_SHORT).show();
            resetearFormulario();
        } else {
            dp.setStock(dp.getStock() + diferencia);
            datosProductoDAO.update(dp);
            Toast.makeText(this, "Error al actualizar el detalle", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarDetalles() {
        spinnerDetalle.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Collections.singletonList("Seleccione")));
    }

    private void limpiarProductos() {
        spinnerProducto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Collections.singletonList("Seleccione")));
        spinnerProducto.setEnabled(true);
    }

    private void resetearFormulario() {
        spinnerPedido.setSelection(0);
        limpiarDetalles();
        limpiarProductos();
        editTextCantidad.setText("");
        textViewSubtotal.setText("$ 0.00");
        btnActualizar.setEnabled(false);
        detalleActual = null;
    }
}
