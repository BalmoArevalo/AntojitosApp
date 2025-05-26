package sv.ues.fia.eisi.proyecto01_antojitos.network.helpers;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.network.ApiConfig;
import sv.ues.fia.eisi.proyecto01_antojitos.network.VolleySingleton;

public class ProductoHelper {

    // ----------- Crear producto ----------
    public static void crearProducto(Context context, int idCategoria, String nombre, String descripcion, int activo) {
        String url = ApiConfig.getBaseUrl() + "/crear_producto.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            Toast.makeText(context, "✅ " + json.getString("message"), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "❌ " + json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "❌ Error al procesar respuesta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(context, "❌ Error de red: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_categoria", String.valueOf(idCategoria));
                params.put("nombre", nombre);
                params.put("descripcion", descripcion);
                params.put("activo", String.valueOf(activo));
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    // ----------- Cargar categorías para Spinner ----------
    public static void cargarCategorias(Context context, Spinner spinner, ArrayList<String> categoriasList) {
        String url = ApiConfig.getBaseUrl() + "/listar_categorias.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    categoriasList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String nombre = obj.getString("nombre");
                            categoriasList.add(id + " - " + nombre);
                        } catch (Exception e) {
                            Toast.makeText(context, "Error al procesar categoría: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, categoriasList));
                },
                error -> Toast.makeText(context, "Error al cargar categorías: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // ----------- Cargar productos por categoría ----------
    public static void cargarProductosPorCategoria(Context context, int idCategoria, TextView resultadoView) {
        String url = ApiConfig.getBaseUrl() + "/listar_productos_por_categoria.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        StringBuilder resultado = new StringBuilder();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String estado = obj.getInt("activo") == 1 ? "Activa" : "Inactiva";
                            resultado.append("ID: ").append(obj.getInt("id"))
                                    .append(" [").append(estado).append("]\n")
                                    .append("Nombre: ").append(obj.getString("nombre")).append("\n")
                                    .append("Descripción: ").append(obj.getString("descripcion")).append("\n\n");
                        }
                        resultadoView.setText(resultado.toString().isEmpty() ? "Sin productos en esta categoría." : resultado.toString());
                    } catch (Exception e) {
                        resultadoView.setText("Error al procesar datos: " + e.getMessage());
                    }
                },
                error -> resultadoView.setText("Error de red: " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> datos = new HashMap<>();
                datos.put("id_categoria", String.valueOf(idCategoria));
                return datos;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}
