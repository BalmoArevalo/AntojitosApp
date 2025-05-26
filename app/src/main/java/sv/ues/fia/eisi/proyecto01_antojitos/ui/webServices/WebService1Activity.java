package sv.ues.fia.eisi.proyecto01_antojitos.ui.webServices;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.network.helpers.ProductoHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.network.ApiConfig;

public class WebService1Activity extends AppCompatActivity {

    Spinner spinnerCategoria;
    EditText etNombreProducto, etDescripcionProducto;
    CheckBox cbActivo;
    Button btnCrearProducto;

    ArrayList<String> categorias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service1);

        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        etNombreProducto = findViewById(R.id.etNombreProducto);
        etDescripcionProducto = findViewById(R.id.etDescripcionProducto);
        cbActivo = findViewById(R.id.cbActivo);
        btnCrearProducto = findViewById(R.id.btnCrearProducto);

        cargarCategoriasDesdeServidor();

        btnCrearProducto.setOnClickListener(v -> {
            String seleccionado = spinnerCategoria.getSelectedItem().toString();
            int idCategoria = Integer.parseInt(seleccionado.split(" - ")[0]);
            String nombre = etNombreProducto.getText().toString().trim();
            String descripcion = etDescripcionProducto.getText().toString().trim();
            int activo = cbActivo.isChecked() ? 1 : 0;

            if (nombre.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            ProductoHelper.crearProducto(this, idCategoria, nombre, descripcion, activo);
        });
    }

    private void cargarCategoriasDesdeServidor() {
        String url = ApiConfig.getBaseUrl() + "/listar_categorias.php"; // Debes tener este endpoint en PHP

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String nombre = obj.getString("nombre");
                            categorias.add(id + " - " + nombre);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    spinnerCategoria.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias));
                },
                error -> Toast.makeText(this, "Error al cargar categorías: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}
