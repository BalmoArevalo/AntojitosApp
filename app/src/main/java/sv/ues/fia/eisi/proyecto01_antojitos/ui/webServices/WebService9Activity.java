package sv.ues.fia.eisi.proyecto01_antojitos.ui.webServices;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.network.ApiConfig;
import sv.ues.fia.eisi.proyecto01_antojitos.network.VolleySingleton;
import sv.ues.fia.eisi.proyecto01_antojitos.network.helpers.RepartidorHelper;

public class WebService9Activity extends AppCompatActivity {

    Spinner spinnerDepartamento, spinnerMunicipio, spinnerDistrito;
    EditText etNombre, etApellido, etTelefono, etTipoVehiculo;
    CheckBox cbDisponible, cbActivo;
    Button btnCrearRepartidor;

    ArrayList<Departamento> listaDepartamentos = new ArrayList<>();
    ArrayList<Municipio> listaMunicipios = new ArrayList<>();
    ArrayList<Distrito> listaDistritos = new ArrayList<>();

    Departamento dptoSeleccionado;
    Municipio municipioSeleccionado;
    Distrito distritoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service9);

        spinnerDepartamento = findViewById(R.id.spinnerDepartamento);
        spinnerMunicipio = findViewById(R.id.spinnerMunicipio);
        spinnerDistrito = findViewById(R.id.spinnerDistrito);
        etNombre = findViewById(R.id.txtNombre);
        etApellido = findViewById(R.id.txtApellido);
        etTelefono = findViewById(R.id.txtTelefono);
        etTipoVehiculo = findViewById(R.id.txtTipoVehiculo);
        cbDisponible = findViewById(R.id.chkDisponible);
        cbActivo = findViewById(R.id.chkActivo);
        btnCrearRepartidor = findViewById(R.id.btnGuardar);

        cargarDepartamentos();

        spinnerDepartamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dptoSeleccionado = listaDepartamentos.get(position);
                cargarMunicipios(dptoSeleccionado.id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerMunicipio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                municipioSeleccionado = listaMunicipios.get(position);
                cargarDistritos(dptoSeleccionado.id, municipioSeleccionado.id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerDistrito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                distritoSeleccionado = listaDistritos.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnCrearRepartidor.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String apellido = etApellido.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String tipoVehiculo = etTipoVehiculo.getText().toString().trim();
            int disponible = cbDisponible.isChecked() ? 1 : 0;
            int activo = cbActivo.isChecked() ? 1 : 0;

            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || tipoVehiculo.isEmpty()
                    || dptoSeleccionado == null || municipioSeleccionado == null || distritoSeleccionado == null) {
                Toast.makeText(this, "Todos los campos y ubicaciones son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            RepartidorHelper.crearRepartidorConUbicacion(
                    this,
                    dptoSeleccionado.id,
                    municipioSeleccionado.id,
                    distritoSeleccionado.id,
                    nombre,
                    apellido,
                    telefono,
                    tipoVehiculo,
                    disponible,
                    activo
            );
        });
    }

    private void cargarDepartamentos() {
        String url = ApiConfig.getBaseUrl() + "/consultar_departamentos.php";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        listaDepartamentos.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            listaDepartamentos.add(new Departamento(obj.getInt("ID_DEPARTAMENTO"), obj.getString("NOMBRE_DEPARTAMENTO")));
                        }
                        ArrayAdapter<Departamento> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaDepartamentos);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerDepartamento.setAdapter(adapter);
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al cargar departamentos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de red al cargar departamentos", Toast.LENGTH_SHORT).show());
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void cargarMunicipios(int idDepartamento) {
        String url = ApiConfig.getBaseUrl() + "/consultar_municipios.php?departamentoId=" + idDepartamento;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        listaMunicipios.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            listaMunicipios.add(new Municipio(obj.getInt("ID_MUNICIPIO"), obj.getString("NOMBRE_MUNICIPIO")));
                        }
                        ArrayAdapter<Municipio> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaMunicipios);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerMunicipio.setAdapter(adapter);
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al cargar municipios", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de red al cargar municipios", Toast.LENGTH_SHORT).show());
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void cargarDistritos(int idDepartamento, int idMunicipio) {
        String url = ApiConfig.getBaseUrl() + "/consultar_distritos.php?departamentoId=" + idDepartamento + "&municipioId=" + idMunicipio;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        listaDistritos.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            listaDistritos.add(new Distrito(obj.getInt("ID_DISTRITO"), obj.getString("NOMBRE_DISTRITO")));
                        }
                        ArrayAdapter<Distrito> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaDistritos);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerDistrito.setAdapter(adapter);
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al cargar distritos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de red al cargar distritos", Toast.LENGTH_SHORT).show());
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    public static class Departamento {
        public int id;
        public String nombre;
        public Departamento(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override
        public String toString() { return nombre; }
    }

    public static class Municipio {
        public int id;
        public String nombre;
        public Municipio(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override
        public String toString() { return nombre; }
    }

    public static class Distrito {
        public int id;
        public String nombre;
        public Distrito(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override
        public String toString() { return nombre; }
    }
}