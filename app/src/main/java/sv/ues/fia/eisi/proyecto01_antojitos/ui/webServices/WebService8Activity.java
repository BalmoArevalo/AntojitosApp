package sv.ues.fia.eisi.proyecto01_antojitos.ui.webServices;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;
import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.network.helpers.CategoriaProductoHelper;

public class WebService8Activity extends AppCompatActivity implements CategoriaProductoHelper.ListarCategoriasCallback {

    private ScrollView scrollViewCategorias;
    private TextView tvCategoriasContent; // TextView para mostrar la lista
    private ProgressBar progressBar;
    private TextView tvMensajeFondo; // TextView para mensajes de "cargando", "vacío" o "error"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service8);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        scrollViewCategorias = findViewById(R.id.scrollViewCategorias);
        tvCategoriasContent = findViewById(R.id.tvCategoriasContent);
        progressBar = findViewById(R.id.progressBarListarCategorias);
        tvMensajeFondo = findViewById(R.id.tvMensajeFondoCategorias); // Este es el TextView que estaba como tvMensajeCategorias

        cargarCategorias();
    }

    private void cargarCategorias() {
        progressBar.setVisibility(View.VISIBLE);
        tvMensajeFondo.setText("Cargando categorías...");
        tvMensajeFondo.setVisibility(View.VISIBLE);
        scrollViewCategorias.setVisibility(View.GONE); // Ocultar el contenido mientras carga
        tvCategoriasContent.setText(""); // Limpiar contenido anterior

        CategoriaProductoHelper.obtenerCategorias(this, this);
    }

    @Override
    public void onSuccess(List<Map<String, String>> categorias) {
        progressBar.setVisibility(View.GONE);
        if (categorias != null && !categorias.isEmpty()) {
            tvMensajeFondo.setVisibility(View.GONE);
            scrollViewCategorias.setVisibility(View.VISIBLE);

            StringBuilder sb = new StringBuilder();
            for (Map<String, String> categoria : categorias) {
                sb.append("ID: ").append(categoria.get("id_categoriaproducto")).append("\n");
                sb.append("Name: ").append(categoria.get("nombre_categoria")).append("\n");
                sb.append("Descripcion: ").append(categoria.get("descripcion_categoria")).append("\n");
                sb.append("Available ahora: ").append(categoria.get("disponible_ahora")).append("\n");
                String horario = "Horario: " + categoria.get("hora_disponible_desde") + " - " + categoria.get("hora_disponible_hasta");
                sb.append(horario).append("\n");
                sb.append("--------------------\n\n");
            }
            tvCategoriasContent.setText(sb.toString());
        } else {
            tvMensajeFondo.setText("No se encontraron categorías.");
            tvMensajeFondo.setVisibility(View.VISIBLE);
            scrollViewCategorias.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        tvMensajeFondo.setText("Error al cargar: " + errorMessage);
        tvMensajeFondo.setVisibility(View.VISIBLE);
        scrollViewCategorias.setVisibility(View.GONE);
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}