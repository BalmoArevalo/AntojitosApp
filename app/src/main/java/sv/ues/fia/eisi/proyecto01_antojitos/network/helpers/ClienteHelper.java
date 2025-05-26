package sv.ues.fia.eisi.proyecto01_antojitos.network.helpers;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.network.ApiConfig;
import sv.ues.fia.eisi.proyecto01_antojitos.network.VolleySingleton;

public class ClienteHelper {

    public static void registrarCliente(Context context, String nombre, String apellido, String telefono) {
        String url = ApiConfig.getBaseUrl() + "crear_cliente.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(context, "Cliente creado: " + response, Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", nombre);
                params.put("apellido", apellido);
                params.put("telefono", telefono);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}