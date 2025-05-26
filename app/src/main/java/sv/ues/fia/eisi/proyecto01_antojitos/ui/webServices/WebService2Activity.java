package sv.ues.fia.eisi.proyecto01_antojitos.ui.webServices;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.network.helpers.ProductoHelper;

public class WebService2Activity extends AppCompatActivity {
    Spinner spinnerCategoriaProductoWS;
    Button btnCargarProductosWS;
    TextView tvResultadoProductosWS;
    ArrayList<String> listaCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service2);

        // Enlazar componentes con sufijo WS
        spinnerCategoriaProductoWS = findViewById(R.id.spinnerCategoriaProductoWS);
        btnCargarProductosWS = findViewById(R.id.btnCargarProductosWS);
        tvResultadoProductosWS = findViewById(R.id.tvResultadoProductosWS);

        // Inicializar lista de categorías y cargar en spinner
        listaCategorias = new ArrayList<>();
        ProductoHelper.cargarCategorias(this, spinnerCategoriaProductoWS, listaCategorias);

        // Acción del botón: cargar productos por categoría seleccionada
        btnCargarProductosWS.setOnClickListener(v -> {
            if (spinnerCategoriaProductoWS.getSelectedItem() != null) {
                String seleccionado = spinnerCategoriaProductoWS.getSelectedItem().toString();
                int idCategoria = Integer.parseInt(seleccionado.split(" - ")[0]);
                ProductoHelper.cargarProductosPorCategoria(this, idCategoria, tvResultadoProductosWS);
            } else {
                tvResultadoProductosWS.setText("Seleccione una categoría.");
            }
        });
    }
}