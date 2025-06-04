package sv.ues.fia.eisi.proyecto01_antojitos.network.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.network.ApiConfig;
import sv.ues.fia.eisi.proyecto01_antojitos.network.VolleySingleton;

public class RepartidorHelper {

    /**
     * Crear repartidor con ubicación (departamento, municipio, distrito)
     */
    public static void crearRepartidorConUbicacion(Context context,
                                                   int idDepartamento,
                                                   int idMunicipio,
                                                   int idDistrito,
                                                   String nombre,
                                                   String apellido,
                                                   String telefono,
                                                   String tipoVehiculo,
                                                   int disponible,
                                                   int activo) {

        String url = ApiConfig.getBaseUrl() + "/crear_repartidor.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.optBoolean("success", false);
                        String message = json.optString("message", "Sin mensaje");

                        Toast.makeText(context, (success ? "✅ " : "❌ ") + message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "❌ Error al procesar respuesta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("RepartidorHelper", "Parse error", e);
                    }
                },
                error -> {
                    Toast.makeText(context, "❌ Error de red: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("RepartidorHelper", "Volley error", error);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_departamento", String.valueOf(idDepartamento));
                params.put("id_municipio", String.valueOf(idMunicipio));
                params.put("id_distrito", String.valueOf(idDistrito));
                params.put("nombre", nombre != null ? nombre : "");
                params.put("apellido", apellido != null ? apellido : "");
                params.put("telefono", telefono != null ? telefono : "");
                params.put("tipo_vehiculo", tipoVehiculo != null ? tipoVehiculo : "");
                params.put("disponible", String.valueOf(disponible));
                params.put("activo", String.valueOf(activo));
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     * Eliminar repartidor por ID
     */
    public static void eliminarRepartidor(Context context, int idRepartidor) {
        String url = ApiConfig.getBaseUrl() + "/eliminar_repartidor.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.optBoolean("success", false);
                        String message = json.optString("message", "Sin mensaje");

                        Toast.makeText(context, (success ? "✅ " : "❌ ") + message, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "❌ Error al procesar respuesta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("RepartidorHelper", "Parse error", e);
                    }
                },
                error -> {
                    Toast.makeText(context, "❌ Error de red: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("RepartidorHelper", "Volley error", error);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_repartidor", String.valueOf(idRepartidor));
                return parametros;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
}
