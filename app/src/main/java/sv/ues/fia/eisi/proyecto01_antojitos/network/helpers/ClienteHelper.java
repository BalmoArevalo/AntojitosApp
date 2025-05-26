package sv.ues.fia.eisi.proyecto01_antojitos.network.helpers;

import android.content.Context;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.network.ApiConfig;
import sv.ues.fia.eisi.proyecto01_antojitos.network.VolleySingleton;

public class ClienteHelper {

    // CREAR CLIENTE (Ya lo tenías)
    public static void crearCliente(Context context, String telefono, String nombre, String apellido, int activo, Runnable onSuccessCallback, View anchorView) {
        String url = ApiConfig.getBaseUrl() + "crear_cliente.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            Snackbar.make(anchorView, "✅ " + json.getString("message"), Snackbar.LENGTH_LONG).show();
                            if (onSuccessCallback != null) onSuccessCallback.run();
                        } else {
                            Snackbar.make(anchorView, "❌ " + json.getString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Snackbar.make(anchorView, "❌ Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                },
                error -> Snackbar.make(anchorView, "❌ Error de red: " + error.getMessage(), Snackbar.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("telefonoCliente", telefono);
                params.put("nombreCliente", nombre);
                params.put("apellidoCliente", apellido);
                params.put("activoCliente", String.valueOf(activo));
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    // CONSULTAR CLIENTES
    public interface ClienteResponse {
        void onResponse(JSONArray clientes);
        void onError(String error);
    }

    public static void consultarClientes(Context context, ClienteResponse listener) {
        String url = ApiConfig.getBaseUrl() + "consultar_clientes.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                listener::onResponse,
                error -> {
                    if (listener != null) {
                        listener.onError(error.getMessage());
                    }
                    Snackbar.make(((android.app.Activity) context).findViewById(android.R.id.content),
                            "❌ Error al consultar: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                });

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}
