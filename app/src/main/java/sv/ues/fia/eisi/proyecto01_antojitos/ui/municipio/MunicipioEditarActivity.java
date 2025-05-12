package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.db.MunicipioDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento.Departamento;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento.DepartamentoViewModel;

public class MunicipioEditarActivity extends AppCompatActivity {

    private Spinner spinnerMunicipios, spinnerDepartamento;
    private EditText editTextNombreMunicipio;
    private Button buttonBuscar, buttonActualizar;
    private Switch switchActivo;

    private MunicipioDAO municipioDAO;
    private MunicipioViewModel municipioViewModel;
    private DepartamentoViewModel departamentoViewModel;

    private List<Municipio> listaMunicipios = new ArrayList<>();
    private List<Departamento> listaDepartamentos = new ArrayList<>();
    private Municipio municipioSeleccionado = null;

    // Almacenamos clave original
    private int idOriginalDepartamento = -1;
    private int idOriginalMunicipio = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipio_editar);

        spinnerMunicipios = findViewById(R.id.spinnerIdMunicipio);
        spinnerDepartamento = findViewById(R.id.spinnerDepartamentoEditar);
        buttonBuscar = findViewById(R.id.buttonBuscarMunicipio);
        editTextNombreMunicipio = findViewById(R.id.editTextNombreMunicipioEditar);
        buttonActualizar = findViewById(R.id.buttonActualizarMunicipio);
        switchActivo = findViewById(R.id.switchActivoMunicipio);

        municipioDAO = new MunicipioDAO(new DBHelper(this).getWritableDatabase());
        municipioViewModel = new ViewModelProvider(this).get(MunicipioViewModel.class);
        departamentoViewModel = new ViewModelProvider(this).get(DepartamentoViewModel.class);

        // Cargar departamentos primero
        departamentoViewModel.getListaDepartamentos().observe(this, departamentos -> {
            listaDepartamentos = departamentos;

            ArrayAdapter<String> adapterDeptos = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    obtenerNombresDepartamentos(departamentos)
            );
            adapterDeptos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDepartamento.setAdapter(adapterDeptos);

            // Una vez cargado departamentos, cargar municipios
            municipioViewModel.getListaMunicipios().observe(this, municipios -> {
                listaMunicipios = municipios;
                ArrayAdapter<String> adapterMun = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        obtenerListaDescriptivaMunicipios(municipios)
                );
                adapterMun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMunicipios.setAdapter(adapterMun);
            });

            municipioViewModel.cargarMunicipios();
        });

        departamentoViewModel.cargarDepartamentos();

        buttonBuscar.setOnClickListener(v -> {
            int pos = spinnerMunicipios.getSelectedItemPosition();
            if (pos >= 0 && pos < listaMunicipios.size()) {
                municipioSeleccionado = listaMunicipios.get(pos);

                // Guardar clave primaria original
                idOriginalMunicipio = municipioSeleccionado.getIdMunicipio();
                idOriginalDepartamento = municipioSeleccionado.getIdDepartamento();

                editTextNombreMunicipio.setText(municipioSeleccionado.getNombreMunicipio());
                switchActivo.setChecked(municipioSeleccionado.getActivoMunicipio() == 1);

                // Preseleccionar el departamento en el spinner
                for (int i = 0; i < listaDepartamentos.size(); i++) {
                    if (listaDepartamentos.get(i).getIdDepartamento() == municipioSeleccionado.getIdDepartamento()) {
                        spinnerDepartamento.setSelection(i);
                        break;
                    }
                }
            } else {
                Toast.makeText(this, "Selecciona un municipio válido", Toast.LENGTH_SHORT).show();
            }
        });

        buttonActualizar.setOnClickListener(v -> {
            if (municipioSeleccionado == null) {
                Toast.makeText(this, "Busca primero un municipio", Toast.LENGTH_SHORT).show();
                return;
            }

            String nuevoNombre = editTextNombreMunicipio.getText().toString().trim();
            int posDepto = spinnerDepartamento.getSelectedItemPosition();

            if (nuevoNombre.isEmpty() || posDepto < 0 || posDepto >= listaDepartamentos.size()) {
                Toast.makeText(this, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show();
                return;
            }

            int nuevoIdDep = listaDepartamentos.get(posDepto).getIdDepartamento();

            // Actualizar campos del objeto seleccionado
            municipioSeleccionado.setNombreMunicipio(nuevoNombre);
            municipioSeleccionado.setIdDepartamento(nuevoIdDep);
            municipioSeleccionado.setActivoMunicipio(switchActivo.isChecked() ? 1 : 0);

            int filas = municipioDAO.actualizarConClaveCompuesta(
                    municipioSeleccionado,
                    idOriginalDepartamento,
                    idOriginalMunicipio
            );

            if (filas > 0) {
                Toast.makeText(this, "Municipio actualizado con éxito", Toast.LENGTH_SHORT).show();
                municipioViewModel.cargarMunicipios(); // refrescar lista
            } else {
                Toast.makeText(this, "Error al actualizar municipio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> obtenerListaDescriptivaMunicipios(List<Municipio> municipios) {
        List<String> lista = new ArrayList<>();
        for (Municipio m : municipios) {
            String nombreDepto = getNombreDepartamento(m.getIdDepartamento());
            String estado = m.getActivoMunicipio() == 1 ? "Activo" : "Inactivo";
            lista.add(m.getIdMunicipio() + " - " + m.getNombreMunicipio() + " (" + nombreDepto + ") - " + estado);
        }
        return lista;
    }

    private List<String> obtenerNombresDepartamentos(List<Departamento> departamentos) {
        List<String> nombres = new ArrayList<>();
        for (Departamento d : departamentos) {
            nombres.add(d.getNombreDepartamento());
        }
        return nombres;
    }

    private String getNombreDepartamento(int idDepartamento) {
        for (Departamento d : listaDepartamentos) {
            if (d.getIdDepartamento() == idDepartamento) {
                return d.getNombreDepartamento();
            }
        }
        return "Desconocido";
    }
}
