package sv.ues.fia.eisi.proyecto01_antojitos.ui.webServices;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.network.helpers.ClienteHelper;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.network.ApiConfig;

public class WebService4Activity extends AppCompatActivity {

    AutoCompleteTextView autoCompleteClientes;
    TextView tvDetalleCliente;

    ArrayList<String> nombres = new ArrayList<>();
    HashMap<String, JSONObject> mapaClientes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service4);

        autoCompleteClientes = findViewById(R.id.autoCompleteClientes);
        tvDetalleCliente = findViewById(R.id.tvDetalleCliente);

        cargarClientes();
    }

    private void cargarClientes() {
        ClienteHelper.consultarClientes(this, new ClienteHelper.ClienteResponse() {
            @Override
            public void onResponse(JSONArray response) {
                nombres.clear();
                mapaClientes.clear();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject cliente = response.getJSONObject(i);
                        String nombreCompleto = cliente.getString("NOMBRE_CLIENTE") + " " + cliente.getString("APELLIDO_CLIENTE");
                        nombres.add(nombreCompleto);
                        mapaClientes.put(nombreCompleto, cliente);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(WebService4Activity.this, android.R.layout.simple_dropdown_item_1line, nombres);
                autoCompleteClientes.setAdapter(adapter);

                autoCompleteClientes.setOnItemClickListener((parent, view, position, id) -> {
                    String seleccionado = parent.getItemAtPosition(position).toString();
                    mostrarDetalle(mapaClientes.get(seleccionado));
                });
            }

            @Override
            public void onError(String error) {
                tvDetalleCliente.setText("Error: " + error);
            }
        });
    }


    private void mostrarDetalle(JSONObject cliente) {
        try {
            String detalle = "ID: " + cliente.getInt("ID_CLIENTE") + "\n"
                    + "Teléfono: " + cliente.getString("TELEFONO_CLIENTE") + "\n"
                    + "Activo: " + (cliente.getInt("ACTIVO_CLIENTE") == 1 ? "Sí" : "No");
            tvDetalleCliente.setText(detalle);
        } catch (Exception e) {
            tvDetalleCliente.setText("Error al mostrar detalles.");
        }
    }
}
