package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class SucursalEliminarActivity extends AppCompatActivity {

    private Spinner spinnerSucursal;
    private TextView tvResultado;
    private Button btnBuscar, btnEliminar, btnLimpiar;

    private DBHelper dbHelper;
    private SucursalDAO dao;
    private List<Sucursal> sucursales = new ArrayList<>();
    private int idSucursalSeleccionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucursal_eliminar);

        spinnerSucursal = findViewById(R.id.spinnerSucursal);
        tvResultado = findViewById(R.id.tvResultado);
        btnBuscar = findViewById(R.id.btnBuscarSucursal);
        btnEliminar = findViewById(R.id.btnEliminarSucursal);
        btnLimpiar = findViewById(R.id.btnLimpiarCampos);

        dbHelper = new DBHelper(this);
        dao = new SucursalDAO(dbHelper.getWritableDatabase());

        cargarSpinnerSucursales();

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDetalles();
            }
        });
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarSucursal();
            }
        });
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarCampos();
            }
        });
    }

    /**
     * Carga solo sucursales activas en el spinner para posibilitar soft delete.
     */
    private void cargarSpinnerSucursales() {
        sucursales.clear();
        List<String> items = new ArrayList<>();
        items.add("Seleccione...");
        sucursales.add(null);

        // Obtener solo activas
        List<Sucursal> listaActivas = dao.obtenerActivos();
        for (Sucursal s : listaActivas) {
            sucursales.add(s);
            items.add(s.getIdSucursal() + " - " + s.getNombreSucursal());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSucursal.setAdapter(adapter);
    }

    /**
     * Muestra detalles de la sucursal seleccionada.
     */
    private void mostrarDetalles() {
        int pos = spinnerSucursal.getSelectedItemPosition();
        if (pos <= 0 || sucursales.get(pos) == null) {
            Toast.makeText(this, "Selecciona una sucursal válida", Toast.LENGTH_SHORT).show();
            return;
        }
        Sucursal s = sucursales.get(pos);
        idSucursalSeleccionada = s.getIdSucursal();

        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(s.getIdSucursal()).append("\n");
        sb.append("Nombre: ").append(s.getNombreSucursal()).append("\n");
        sb.append("Teléfono: ").append(s.getTelefonoSucursal()).append("\n");
        sb.append("Dirección: ").append(s.getDireccionSucursal()).append("\n");
        sb.append("Departamento ID: ").append(s.getIdDepartamento()).append("\n");
        sb.append("Municipio ID: ").append(s.getIdMunicipio()).append("\n");
        sb.append("Distrito ID: ").append(s.getIdDistrito()).append("\n");
        sb.append("Horario Apertura: ").append(s.getHorarioApertura()).append("\n");
        sb.append("Horario Cierre: ").append(s.getHorarioCierre()).append("\n");
        sb.append("Activo: ").append(s.getActivoSucursal() == 1 ? "Sí" : "No");

        tvResultado.setText(sb.toString());
    }

    /**
     * Realiza soft delete marcando la sucursal como inactiva.
     */
    private void eliminarSucursal() {
        if (idSucursalSeleccionada == -1) {
            Toast.makeText(this, "Busca primero una sucursal", Toast.LENGTH_SHORT).show();
            return;
        }

        int filas = dao.eliminar(idSucursalSeleccionada);
        if (filas > 0) {
            Toast.makeText(this, "Sucursal desactivada correctamente", Toast.LENGTH_LONG).show();
            cargarSpinnerSucursales();
            limpiarCampos();
        } else {
            Toast.makeText(this, "Error al desactivar sucursal", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Limpia el formulario.
     */
    private void limpiarCampos() {
        spinnerSucursal.setSelection(0);
        tvResultado.setText("Aquí se mostrará la información de la sucursal seleccionada.");
        idSucursalSeleccionada = -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
