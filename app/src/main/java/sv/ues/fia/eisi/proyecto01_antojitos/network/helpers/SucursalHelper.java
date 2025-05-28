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

public class SucursalHelper {

    // CREAR SUCURSAL
    public static void crearSucursal(
            Context context,
            int idDepartamento,
            int idMunicipio,
            int idDistrito,
            String nombreSucursal,
            String direccionSucursal,
            String telefonoSucursal,
            String horarioApertura,
            String horarioCierre,
            int activo,
            Runnable onSuccessCallback,
            View anchorView
    ) {
        String url = ApiConfig.getBaseUrl() + "crear_sucursal.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optBoolean("success", false)) {
                            Snackbar.make(anchorView, "✅ " + json.optString("message", "Sucursal creada."), Snackbar.LENGTH_LONG).show();
                            if (onSuccessCallback != null) onSuccessCallback.run();
                        } else {
                            Snackbar.make(anchorView, "❌ " + json.optString("message", "Error al crear sucursal."), Snackbar.LENGTH_LONG).show();
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
                params.put("departamentoId", String.valueOf(idDepartamento));
                params.put("municipioId", String.valueOf(idMunicipio));
                params.put("distritoId", String.valueOf(idDistrito));
                params.put("nombreSucursal", nombreSucursal);
                params.put("direccionSucursal", direccionSucursal);
                params.put("telefonoSucursal", telefonoSucursal);
                params.put("horarioApertura", horarioApertura);
                params.put("horarioCierre", horarioCierre);
                params.put("activoSucursal", String.valueOf(activo));
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    // CONSULTAR SUCURSALES
    public interface SucursalResponse {
        void onResponse(JSONArray sucursales);
        void onError(String error);
    }

    public static void consultarSucursales(Context context, SucursalResponse listener) {
        String url = ApiConfig.getBaseUrl() + "consultar_sucursal.php";

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