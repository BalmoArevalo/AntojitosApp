package sv.ues.fia.eisi.proyecto01_antojitos.ui.webServices;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import sv.ues.fia.eisi.proyecto01_antojitos.R;

import sv.ues.fia.eisi.proyecto01_antojitos.network.helpers.CatalogoHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.network.helpers.RepartidorHelper;

public class WebService9Activity extends AppCompatActivity {

    AutoCompleteTextView spinnerDepartamento, spinnerMunicipio, spinnerDistrito;
    EditText etNombre, etApellido, etTelefono, etTipoVehiculo;
    CheckBox cbDisponible, cbActivo;
    Button btnCrearRepartidor;

    private final ArrayList<CatalogoHelper.Departamento> listaDepartamentos = new ArrayList<>();
    private final ArrayList<CatalogoHelper.Municipio> listaMunicipios = new ArrayList<>();
    private final ArrayList<CatalogoHelper.Distrito> listaDistritos = new ArrayList<>();

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

        CatalogoHelper.cargarDepartamentos(this, spinnerDepartamento, listaDepartamentos, null);

        spinnerDepartamento.setOnItemClickListener((parent, view, position, id) -> {
            CatalogoHelper.Departamento dpto = listaDepartamentos.get(position);
            CatalogoHelper.cargarMunicipios(this, dpto.id, spinnerMunicipio, listaMunicipios, null);
            spinnerMunicipio.setText("");
            spinnerDistrito.setText("");
            listaDistritos.clear();
        });

        spinnerMunicipio.setOnItemClickListener((parent, view, position, id) -> {
            CatalogoHelper.Municipio mun = listaMunicipios.get(position);
            CatalogoHelper.Departamento dpto = getDepartamentoSeleccionado();
            if (dpto != null && mun != null) {
                CatalogoHelper.cargarDistritos(this, dpto.id, mun.id, spinnerDistrito, listaDistritos, null);
            }
            spinnerDistrito.setText("");
        });

        btnCrearRepartidor.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String apellido = etApellido.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String tipoVehiculo = etTipoVehiculo.getText().toString().trim();
            int disponible = cbDisponible.isChecked() ? 1 : 0;
            int activo = cbActivo.isChecked() ? 1 : 0;

            CatalogoHelper.Departamento dpto = getDepartamentoSeleccionado();
            CatalogoHelper.Municipio mun = getMunicipioSeleccionado();
            CatalogoHelper.Distrito dist = getDistritoSeleccionado();

            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || tipoVehiculo.isEmpty()
                    || dpto == null || mun == null || dist == null) {
                Toast.makeText(this, "Todos los campos y ubicaciones son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            RepartidorHelper.crearRepartidorConUbicacion(
                    this,
                    dpto.id,
                    mun.id,
                    dist.id,
                    nombre,
                    apellido,
                    telefono,
                    tipoVehiculo,
                    disponible,
                    activo
            );
        });
    }

    private CatalogoHelper.Departamento getDepartamentoSeleccionado() {
        String nombre = spinnerDepartamento.getText().toString().trim();
        for (CatalogoHelper.Departamento d : listaDepartamentos)
            if (d.nombre.equals(nombre)) return d;
        return null;
    }

    private CatalogoHelper.Municipio getMunicipioSeleccionado() {
        String nombre = spinnerMunicipio.getText().toString().trim();
        for (CatalogoHelper.Municipio m : listaMunicipios)
            if (m.nombre.equals(nombre)) return m;
        return null;
    }

    private CatalogoHelper.Distrito getDistritoSeleccionado() {
        String nombre = spinnerDistrito.getText().toString().trim();
        for (CatalogoHelper.Distrito d : listaDistritos)
            if (d.nombre.equals(nombre)) return d;
        return null;
    }
}
