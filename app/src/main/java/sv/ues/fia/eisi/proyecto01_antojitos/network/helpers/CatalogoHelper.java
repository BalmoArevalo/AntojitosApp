package sv.ues.fia.eisi.proyecto01_antojitos.network.helpers;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import sv.ues.fia.eisi.proyecto01_antojitos.network.ApiConfig;
import sv.ues.fia.eisi.proyecto01_antojitos.network.VolleySingleton;

public class CatalogoHelper {

    // Clase interna para Departamento
    public static class Departamento {
        public int id;
        public String nombre;

        public Departamento(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() { return nombre; }
    }
    // Clase interna para Municipio
    public static class Municipio {
        public int id;
        public String nombre;

        public Municipio(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() { return nombre; }
    }
    // Clase interna para Distrito
    public static class Distrito {
        public int id;
        public String nombre;

        public Distrito(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() { return nombre; }
    }

    // Consulta departamentos y llena spinner
    public static void cargarDepartamentos(Context context, AutoCompleteTextView spinner, ArrayList<Departamento> listaDepartamentos, Runnable onLoaded) {
        String url = ApiConfig.getBaseUrl() + "consultar_departamentos.php";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    listaDepartamentos.clear();
                    ArrayList<String> nombres = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("ID_DEPARTAMENTO");
                            String nombre = obj.getString("NOMBRE_DEPARTAMENTO");
                            listaDepartamentos.add(new Departamento(id, nombre));
                            nombres.add(nombre);
                        } catch (Exception ignored) {}
                    }
                    spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, nombres));
                    if (onLoaded != null) onLoaded.run();
                },
                error -> Toast.makeText(context, "Error cargando departamentos", Toast.LENGTH_SHORT).show()
        );
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // Consulta municipios y llena spinner
    public static void cargarMunicipios(Context context, int idDepartamento, AutoCompleteTextView spinner, ArrayList<Municipio> listaMunicipios, Runnable onLoaded) {
        String url = ApiConfig.getBaseUrl() + "consultar_municipios.php?departamentoId=" + idDepartamento;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    listaMunicipios.clear();
                    ArrayList<String> nombres = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("ID_MUNICIPIO");
                            String nombre = obj.getString("NOMBRE_MUNICIPIO");
                            listaMunicipios.add(new Municipio(id, nombre));
                            nombres.add(nombre);
                        } catch (Exception ignored) {}
                    }
                    spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, nombres));
                    if (onLoaded != null) onLoaded.run();
                },
                error -> Toast.makeText(context, "Error cargando municipios", Toast.LENGTH_SHORT).show()
        );
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // Consulta distritos y llena spinner
    public static void cargarDistritos(Context context, int idDepartamento, int idMunicipio, AutoCompleteTextView spinner, ArrayList<Distrito> listaDistritos, Runnable onLoaded) {
        String url = ApiConfig.getBaseUrl() + "consultar_distritos.php?departamentoId=" + idDepartamento + "&municipioId=" + idMunicipio;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    listaDistritos.clear();
                    ArrayList<String> nombres = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("ID_DISTRITO");
                            String nombre = obj.getString("NOMBRE_DISTRITO");
                            listaDistritos.add(new Distrito(id, nombre));
                            nombres.add(nombre);
                        } catch (Exception ignored) {}
                    }
                    spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, nombres));
                    if (onLoaded != null) onLoaded.run();
                },
                error -> Toast.makeText(context, "Error cargando distritos", Toast.LENGTH_SHORT).show()
        );
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}