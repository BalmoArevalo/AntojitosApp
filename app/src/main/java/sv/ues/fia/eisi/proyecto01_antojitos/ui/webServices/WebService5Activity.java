package sv.ues.fia.eisi.proyecto01_antojitos.ui.webServices;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.network.helpers.SucursalHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.network.helpers.CatalogoHelper;

import android.widget.CheckBox;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;

import java.util.ArrayList;

public class WebService5Activity extends AppCompatActivity {

    AutoCompleteTextView spinnerDepartamento, spinnerMunicipio, spinnerDistrito;
    TextInputEditText etNombreSucursal, etDireccionSucursal, etTelefonoSucursal;
    TimePicker timePickerApertura, timePickerCierre;
    CheckBox cbActivoSucursal;
    MaterialButton btnCrearSucursal;

    // Listas auxiliares para mapear nombre <-> ID
    private ArrayList<CatalogoHelper.Departamento> listaDepartamentos = new ArrayList<>();
    private ArrayList<CatalogoHelper.Municipio> listaMunicipios = new ArrayList<>();
    private ArrayList<CatalogoHelper.Distrito> listaDistritos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service5);

        spinnerDepartamento = findViewById(R.id.spinnerDepartamento);
        spinnerMunicipio = findViewById(R.id.spinnerMunicipio);
        spinnerDistrito = findViewById(R.id.spinnerDistrito);
        etNombreSucursal = findViewById(R.id.editTextNombreSucursal);
        etDireccionSucursal = findViewById(R.id.editTextDireccionSucursal);
        etTelefonoSucursal = findViewById(R.id.editTextTelefonoSucursal);
        timePickerApertura = findViewById(R.id.timePickerApertura);
        timePickerCierre = findViewById(R.id.timePickerCierre);
        btnCrearSucursal = findViewById(R.id.btnGuardarSucursal);

        // Configuración de los TimePicker en modo 24 horas
        timePickerApertura.setIs24HourView(true);
        timePickerCierre.setIs24HourView(true);

        // Cargar departamentos al iniciar
        CatalogoHelper.cargarDepartamentos(this, spinnerDepartamento, listaDepartamentos, null);

        // Cuando el usuario selecciona un departamento, cargar municipios
        spinnerDepartamento.setOnItemClickListener((parent, view, position, id) -> {
            CatalogoHelper.Departamento dpto = listaDepartamentos.get(position);
            CatalogoHelper.cargarMunicipios(this, dpto.id, spinnerMunicipio, listaMunicipios, null);
            spinnerMunicipio.setText(""); // Limpiar selección previa
            spinnerDistrito.setText("");  // Limpiar selección previa
            listaDistritos.clear();       // Limpiar lista previa
        });

        // Cuando el usuario selecciona un municipio, cargar distritos
        spinnerMunicipio.setOnItemClickListener((parent, view, position, id) -> {
            CatalogoHelper.Municipio mun = listaMunicipios.get(position);
            CatalogoHelper.Departamento dpto = getDepartamentoSeleccionado();
            if (dpto != null && mun != null) {
                CatalogoHelper.cargarDistritos(this, dpto.id, mun.id, spinnerDistrito, listaDistritos, null);
            }
            spinnerDistrito.setText("");  // Limpiar selección previa
        });

        // Botón de crear sucursal
        btnCrearSucursal.setOnClickListener(v -> {
            String nombre = etNombreSucursal.getText().toString().trim();
            String direccion = etDireccionSucursal.getText().toString().trim();
            String telefono = etTelefonoSucursal.getText().toString().trim();

            // Obtener horario desde los pickers
            String horarioApertura = String.format("%02d:%02d", timePickerApertura.getHour(), timePickerApertura.getMinute());
            String horarioCierre = String.format("%02d:%02d", timePickerCierre.getHour(), timePickerCierre.getMinute());

            int activo = 1;

            CatalogoHelper.Departamento dpto = getDepartamentoSeleccionado();
            CatalogoHelper.Municipio mun = getMunicipioSeleccionado();
            CatalogoHelper.Distrito dist = getDistritoSeleccionado();

            // Validar que todos los campos estén llenos y que se haya seleccionado un valor válido en cada spinner
            if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty() ||
                    dpto == null || mun == null || dist == null) {
                Toast.makeText(this, "⚠️ Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Llamar al helper, usando los IDs seleccionados
            SucursalHelper.crearSucursal(
                    this,
                    dpto.id,
                    mun.id,
                    dist.id,
                    nombre,
                    direccion,
                    telefono,
                    horarioApertura,
                    horarioCierre,
                    activo,
                    () -> {
                        etNombreSucursal.setText("");
                        etDireccionSucursal.setText("");
                        etTelefonoSucursal.setText("");
                        spinnerDepartamento.setText("");
                        spinnerMunicipio.setText("");
                        spinnerDistrito.setText("");
                        timePickerApertura.setHour(8);
                        timePickerApertura.setMinute(0);
                        timePickerCierre.setHour(17);
                        timePickerCierre.setMinute(0);
                        if (cbActivoSucursal != null) cbActivoSucursal.setChecked(true);
                        etNombreSucursal.requestFocus();
                    },
                    btnCrearSucursal
            );
        });
    }

    // Métodos para obtener el objeto seleccionado según el texto del spinner
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