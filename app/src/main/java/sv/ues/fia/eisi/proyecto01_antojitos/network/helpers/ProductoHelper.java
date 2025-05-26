package sv.ues.fia.eisi.proyecto01_antojitos.network.helpers;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.network.ApiConfig;
import sv.ues.fia.eisi.proyecto01_antojitos.network.VolleySingleton;

public class ProductoHelper {

    public static void crearProducto(Context context, int idCategoria, String nombre, String descripcion, int activo) {
        String url = ApiConfig.getBaseUrl()+ "/crear_producto.php";

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
}
