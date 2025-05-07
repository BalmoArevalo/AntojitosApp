package sv.ues.fia.eisi.proyecto01_antojitos.ui.producto;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.Producto;

public class ProductoEliminarActivity extends AppCompatActivity {

    private ProductoViewModel productoViewModel;
    private AutoCompleteTextView spinnerProducto;
    private MaterialCardView cardConfirmacion;
    private TextView tvNombreProducto;
    private TextView tvCategoriaProducto;

    private TextView tvDescripcionProducto;
    private MaterialButton btnCancelar;
    private MaterialButton btnConfirmarEliminar;
    private List<Producto> listaProductos;
    private Producto productoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_eliminar);

        // Inicializar ViewModel
        productoViewModel = new ViewModelProvider(this).get(ProductoViewModel.class);

        // Inicializar vistas
        inicializarVistas();

        // Configurar observers
        configurarObservers();

        // Cargar productos
        productoViewModel.cargarProductos();
    }

    private void inicializarVistas() {
        spinnerProducto = findViewById(R.id.spinnerProducto);
        cardConfirmacion = findViewById(R.id.cardConfirmacion);
        tvNombreProducto = findViewById(R.id.tvNombreProducto);
        tvCategoriaProducto = findViewById(R.id.tvCategoriaProducto);
        tvDescripcionProducto = findViewById(R.id.tvDescripcionProducto);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnConfirmarEliminar = findViewById(R.id.btnConfirmarEliminar);

        // Configurar listeners
        spinnerProducto.setOnItemClickListener((parent, view, position, id) -> {
            if (listaProductos != null && position < listaProductos.size()) {
                productoSeleccionado = listaProductos.get(position);
                mostrarConfirmacion(productoSeleccionado);
            }
        });

        btnCancelar.setOnClickListener(v -> {
            cardConfirmacion.setVisibility(View.GONE);
            spinnerProducto.setText("", false);
            productoSeleccionado = null;
        });

        btnConfirmarEliminar.setOnClickListener(v -> {
            if (productoSeleccionado != null) {
                productoViewModel.eliminarProducto(productoSeleccionado.getIdProducto());
            }
        });
    }

    private void configurarObservers() {
        productoViewModel.getProductos().observe(this, productos -> {
            if (productos != null && !productos.isEmpty()) {
                listaProductos = productos;
                configurarSpinnerProductos(productos);
            } else {
                Toast.makeText(this, "No hay productos disponibles", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        productoViewModel.getMensajeError().observe(this, mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            }
        });

        productoViewModel.getOperacionExitosa().observe(this, exitoso -> {
            if (exitoso != null && exitoso) {
                Toast.makeText(this, "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void configurarSpinnerProductos(List<Producto> productos) {
        List<String> nombresProductos = new ArrayList<>();
        for (Producto producto : productos) {
            nombresProductos.add(producto.getNombreProducto());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                nombresProductos
        );

        spinnerProducto.setAdapter(adapter);
    }

    private void mostrarConfirmacion(Producto producto) {
        tvNombreProducto.setText("Nombre: " + producto.getNombreProducto());
        tvCategoriaProducto.setText("ID Categoría: " + producto.getIdCategoriaProducto());

        // Manejo de la descripción, considerando que podría ser null
        String descripcion = producto.getDescripcionProducto();
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            tvDescripcionProducto.setText("Descripción: " + descripcion);
            tvDescripcionProducto.setVisibility(View.VISIBLE);
        } else {
            tvDescripcionProducto.setText("Descripción: Sin descripción");
            tvDescripcionProducto.setVisibility(View.VISIBLE);
        }

        cardConfirmacion.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpieza de recursos si es necesario
    }
}