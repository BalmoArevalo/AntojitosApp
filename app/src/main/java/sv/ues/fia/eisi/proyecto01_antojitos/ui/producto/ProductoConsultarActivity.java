package sv.ues.fia.eisi.proyecto01_antojitos.ui.producto;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.Producto;

public class ProductoConsultarActivity extends AppCompatActivity {

    private ProductoViewModel productoViewModel;
    private AutoCompleteTextView spinnerProducto;
    private MaterialCardView cardResultados;
    private TextView tvIdProducto;
    private TextView tvCategoriaProducto;
    private TextView tvNombreProducto;
    private TextView tvDescripcionProducto;
    private List<Producto> listaProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_consultar);

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
        cardResultados = findViewById(R.id.cardResultados);
        tvIdProducto = findViewById(R.id.tvIdProducto);
        tvCategoriaProducto = findViewById(R.id.tvCategoriaProducto);
        tvNombreProducto = findViewById(R.id.tvNombreProducto);
        tvDescripcionProducto = findViewById(R.id.tvDescripcionProducto);

        // Configurar evento de selección del spinner
        spinnerProducto.setOnItemClickListener((parent, view, position, id) -> {
            if (listaProductos != null && position < listaProductos.size()) {
                mostrarDatosProducto(listaProductos.get(position));
                cardResultados.setVisibility(View.VISIBLE);
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
            }
        });

        productoViewModel.getMensajeError().observe(this, mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
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

    private void mostrarDatosProducto(Producto producto) {
        if (producto != null) {
            tvIdProducto.setText(String.valueOf(producto.getIdProducto()));
            tvCategoriaProducto.setText(String.valueOf(producto.getIdCategoriaProducto()));
            tvNombreProducto.setText(producto.getNombreProducto());
            tvDescripcionProducto.setText(producto.getDescripcionProducto() != null ?
                    producto.getDescripcionProducto() : "Sin descripción");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpieza de recursos si es necesario
    }
}