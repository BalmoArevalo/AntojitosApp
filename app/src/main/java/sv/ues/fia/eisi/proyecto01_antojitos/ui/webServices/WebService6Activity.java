package sv.ues.fia.eisi.proyecto01_antojitos.ui.webServices;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.network.helpers.SucursalHelper;

public class WebService6Activity extends AppCompatActivity {

    private AutoCompleteTextView spinnerSucursales;
    private MaterialCardView cardResultadosSucursal;
    // TextViews para mostrar información
    private android.widget.TextView tvIdSucursal, tvNombreSucursal, tvDepartamentoSucursal, tvMunicipioSucursal,
            tvDistritoSucursal, tvDireccionSucursal, tvTelefonoSucursal, tvHorarioSucursal;

    // Lista para mapear nombre a objeto Sucursal
    private ArrayList<Sucursal> listaSucursales = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service6);

        spinnerSucursales = findViewById(R.id.spinnerSucursales);
        cardResultadosSucursal = findViewById(R.id.cardResultadosSucursal);
        tvIdSucursal = findViewById(R.id.tvIdSucursal);
        tvNombreSucursal = findViewById(R.id.tvNombreSucursal);
        tvDepartamentoSucursal = findViewById(R.id.tvDepartamentoSucursal);
        tvMunicipioSucursal = findViewById(R.id.tvMunicipioSucursal);
        tvDistritoSucursal = findViewById(R.id.tvDistritoSucursal);
        tvDireccionSucursal = findViewById(R.id.tvDireccionSucursal);
        tvTelefonoSucursal = findViewById(R.id.tvTelefonoSucursal);
        tvHorarioSucursal = findViewById(R.id.tvHorarioSucursal);

        cargarSucursales();
    }

    private void cargarSucursales() {
        SucursalHelper.consultarSucursales(this, new SucursalHelper.SucursalResponse() {
            @Override
            public void onResponse(JSONArray sucursales) {
                listaSucursales.clear();
                ArrayList<String> nombres = new ArrayList<>();
                for (int i = 0; i < sucursales.length(); i++) {
                    try {
                        JSONObject obj = sucursales.getJSONObject(i);
                        Sucursal s = new Sucursal(
                                obj.optInt("ID_SUCURSAL"),
                                obj.optString("NOMBRE_SUCURSAL"),
                                obj.optString("DIRECCION_SUCURSAL"),
                                obj.optString("TELEFONO_SUCURSAL"),
                                obj.optString("HORARIO_APERTURA_SUCURSAL"),
                                obj.optString("HORARIO_CIERRE_SUCURSAL"),
                                obj.optString("NOMBRE_DEPARTAMENTO"),
                                obj.optString("NOMBRE_MUNICIPIO"),
                                obj.optString("NOMBRE_DISTRITO")
                        );
                        listaSucursales.add(s);
                        nombres.add(s.nombre);
                    } catch (Exception ignored) {}
                }
                spinnerSucursales.setAdapter(new ArrayAdapter<>(WebService6Activity.this,
                        android.R.layout.simple_dropdown_item_1line, nombres));
                spinnerSucursales.setText("", false);
                cardResultadosSucursal.setVisibility(View.GONE);

                spinnerSucursales.setOnItemClickListener((parent, view, position, id) -> mostrarSucursalSeleccionada(position));
            }

            @Override
            public void onError(String error) {
                Toast.makeText(WebService6Activity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarSucursalSeleccionada(int position) {
        if (position < 0 || position >= listaSucursales.size()) return;
        Sucursal s = listaSucursales.get(position);

        tvIdSucursal.setText(String.valueOf(s.id));
        tvNombreSucursal.setText(s.nombre);
        tvDepartamentoSucursal.setText(s.departamento);
        tvMunicipioSucursal.setText(s.municipio);
        tvDistritoSucursal.setText(s.distrito);
        tvDireccionSucursal.setText(s.direccion);
        tvTelefonoSucursal.setText(s.telefono);
        tvHorarioSucursal.setText(String.format("%s - %s", s.horarioApertura, s.horarioCierre));

        cardResultadosSucursal.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Horario apertura: " + s.horarioApertura + " cierre: " + s.horarioCierre, Toast.LENGTH_SHORT).show();
    }

    // Sucursal POJO simple
    private static class Sucursal {
        int id;
        String nombre, direccion, telefono, horarioApertura, horarioCierre, departamento, municipio, distrito;

        Sucursal(int id, String nombre, String direccion, String telefono,
                 String horarioApertura, String horarioCierre,
                 String departamento, String municipio, String distrito) {
            this.id = id;
            this.nombre = nombre;
            this.direccion = direccion;
            this.telefono = telefono;
            this.horarioApertura = horarioApertura;
            this.horarioCierre = horarioCierre;
            this.departamento = departamento;
            this.municipio = municipio;
            this.distrito = distrito;
        }
    }
}