package sv.ues.fia.eisi.proyecto01_antojitos.network.helpers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest; // Para la solicitud GET que espera un objeto JSON
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;  // Para manejar el array de categorías
import org.json.JSONObject;

import java.util.ArrayList; // Para la lista
import java.util.HashMap;
import java.util.Iterator;  // Para iterar sobre las claves del JSONObject
import java.util.List;    // Para la lista
import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.network.ApiConfig;
import sv.ues.fia.eisi.proyecto01_antojitos.network.VolleySingleton;

public class CategoriaProductoHelper {

    // Callback para la creación (existente)
    public interface CategoriaProductoCallback {
        void onSuccess(String message, String idCategoriaProducto);
        void onError(String errorMessage);
    }

    // --- NUEVA INTERFAZ DE CALLBACK PARA LISTAR ---
    // Devolverá una lista de Mapas, donde cada Mapa representa una categoría
    public interface ListarCategoriasCallback {
        void onSuccess(List<Map<String, String>> categorias);
        void onError(String errorMessage);
    }
    // ----------------------------------------------


    // Método para crear categoría (existente)
    public static void crearCategoriaProducto(
            Context context,
            String nombreCategoria,
            String descripcionCategoria,
            int disponibleCategoria,
            String horaDesde,
            String horaHasta,
            int activoCategoriaProducto,
            final CategoriaProductoCallback callback
    ) {
        String url = ApiConfig.getBaseUrl() + "crear_categoria_producto.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.optBoolean("success", false);
                        String message = jsonResponse.optString("message", "Respuesta desconocida del servidor.");

                        if (success) {
                            String idCategoria = jsonResponse.optString("id_categoriaproducto", null);
                            if (callback != null) {
                                callback.onSuccess(message, idCategoria);
                            }
                        } else {
                            if (callback != null) {
                                callback.onError(message);
                            }
                        }
                    } catch (Exception e) {
                        if (callback != null) {
                            callback.onError("Error al parsear la respuesta del servidor: " + e.getMessage());
                        }
                    }
                },
                error -> {
                    String errorMessage = "Error al crear categoría: ";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String body = new String(error.networkResponse.data, "UTF-8");
                            JSONObject errorJson = new JSONObject(body);
                            errorMessage += errorJson.optString("message", body);
                        } catch (Exception e) {
                            errorMessage += error.getMessage() != null ? error.getMessage() : "Error desconocido.";
                        }
                    } else {
                        errorMessage += error.getMessage() != null ? error.getMessage() : "Error de red desconocido.";
                    }
                    if (callback != null) {
                        callback.onError(errorMessage);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombreCategoria", nombreCategoria);
                params.put("descripcionCategoria", descripcionCategoria);
                params.put("disponibleCategoria", String.valueOf(disponibleCategoria));
                params.put("horaDesde", horaDesde);
                params.put("horaHasta", horaHasta);
                params.put("activoCategoriaProducto", String.valueOf(activoCategoriaProducto));
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void crearCategoriaProducto(
            Context context,
            String nombreCategoria,
            String descripcionCategoria,
            int disponibleCategoria,
            String horaDesde,
            String horaHasta,
            final CategoriaProductoCallback callback
    ) {
        crearCategoriaProducto(context, nombreCategoria, descripcionCategoria, disponibleCategoria, horaDesde, horaHasta, 1, callback);
    }


    // --- NUEVO MÉTODO PARA LISTAR CATEGORÍAS ---
    public static void obtenerCategorias(Context context, final ListarCategoriasCallback callback) {
        String url = ApiConfig.getBaseUrl() + "listar_categoria_producto.php";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> { // La respuesta raíz es un JSONObject
                    try {
                        boolean success = response.optBoolean("success", false);
                        if (success) {
                            JSONArray categoriasJsonArray = response.getJSONArray("categorias");
                            List<Map<String, String>> listaCategorias = new ArrayList<>();

                            for (int i = 0; i < categoriasJsonArray.length(); i++) {
                                JSONObject categoriaJson = categoriasJsonArray.getJSONObject(i);
                                Map<String, String> categoriaMap = new HashMap<>();

                                // Iterar sobre todas las claves del JSONObject y añadirlas al Map
                                Iterator<String> keys = categoriaJson.keys();
                                while(keys.hasNext()) {
                                    String key = keys.next();
                                    // Convertir todos los valores a String para el Map<String, String>
                                    categoriaMap.put(key, categoriaJson.getString(key));
                                }
                                listaCategorias.add(categoriaMap);
                            }
                            if (callback != null) {
                                callback.onSuccess(listaCategorias);
                            }
                        } else {
                            String message = response.optString("message", "No se pudieron obtener las categorías.");
                            if (callback != null) {
                                callback.onError(message);
                            }
                        }
                    } catch (Exception e) {
                        if (callback != null) {
                            callback.onError("Error al parsear la lista de categorías: " + e.getMessage());
                        }
                    }
                },
                error -> {
                    String errorMessage = "Error al obtener categorías: ";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String body = new String(error.networkResponse.data, "UTF-8");
                            JSONObject errorJson = new JSONObject(body);
                            errorMessage += errorJson.optString("message", body);
                        } catch (Exception e) {
                            // Si el cuerpo del error no es JSON o falla el parseo, usar el mensaje de error de Volley
                            errorMessage += error.getMessage() != null ? error.getMessage() : "Error desconocido.";
                        }
                    } else {
                        errorMessage += error.getMessage() != null ? error.getMessage() : "Error de red desconocido.";
                    }
                    if (callback != null) {
                        callback.onError(errorMessage);
                    }
                }
        );
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
    // -----------------------------------------
}