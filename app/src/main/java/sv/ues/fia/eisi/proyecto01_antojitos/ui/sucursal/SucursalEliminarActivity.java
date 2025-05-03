package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
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
    private List<Sucursal> sucursales = new ArrayList<>();
    private int idSucursalSeleccionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_sucursal_eliminar);

            spinnerSucursal = findViewById(R.id.spinnerSucursal);
            tvResultado = findViewById(R.id.tvResultado);
            btnBuscar = findViewById(R.id.btnBuscarSucursal);
            btnEliminar = findViewById(R.id.btnEliminarSucursal);
            btnLimpiar = findViewById(R.id.btnLimpiarCampos);

            dbHelper = new DBHelper(this);
            cargarSpinnerSucursales();

            btnBuscar.setOnClickListener(v -> {
                try {
                    mostrarDetalles();
                } catch (Exception e) {
                    Toast.makeText(this, "Error al mostrar detalles: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            btnEliminar.setOnClickListener(v -> {
                try {
                    eliminarSucursal();
                } catch (Exception e) {
                    Toast.makeText(this, "Error al eliminar sucursal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            btnLimpiar.setOnClickListener(v -> {
                try {
                    limpiarCampos();
                } catch (Exception e) {
                    Toast.makeText(this, "Error al limpiar campos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error en onCreate: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void cargarSpinnerSucursales() {
        try {
            sucursales.clear();
            List<String> items = new ArrayList<>();
            items.add("Seleccione...");
            sucursales.add(null);  // posición 0 vacía

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            SucursalDAO dao = new SucursalDAO(db);
            List<Sucursal> lista = dao.obtenerTodas();
            db.close();

            for (Sucursal s : lista) {
                sucursales.add(s);
                items.add(s.getIdSucursal() + " - " + s.getNombreSucursal());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSucursal.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar spinner: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarDetalles() {
        int pos = spinnerSucursal.getSelectedItemPosition();
        if (pos <= 0 || sucursales.get(pos) == null) {
            Toast.makeText(this, "Selecciona una sucursal válida", Toast.LENGTH_SHORT).show();
            return;
        }

        Sucursal s = sucursales.get(pos);
        idSucursalSeleccionada = s.getIdSucursal();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Consultar nombres relacionados
        String nombreDepartamento = consultarNombre(db, "DEPARTAMENTO", "NOMBRE_DEPARTAMENTO", "ID_DEPARTAMENTO", s.getIdDepartamento());
        String nombreMunicipio = consultarNombreDoble(db, "MUNICIPIO", "NOMBRE_MUNICIPIO", "ID_DEPARTAMENTO", s.getIdDepartamento(), "ID_MUNICIPIO", s.getIdMunicipio());
        String nombreDistrito = consultarNombreTriple(db, "DISTRITO", "NOMBRE_DISTRITO",
                "ID_DEPARTAMENTO", s.getIdDepartamento(),
                "ID_MUNICIPIO", s.getIdMunicipio(),
                "ID_DISTRITO", s.getIdDistrito());

        db.close();

        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(s.getIdSucursal()).append("\n");
        sb.append("Nombre: ").append(s.getNombreSucursal()).append("\n");
        sb.append("Teléfono: ").append(s.getTelefonoSucursal()).append("\n");
        sb.append("Dirección: ").append(s.getDireccionSucursal()).append("\n");
        sb.append("Departamento: ").append(s.getIdDepartamento()).append(" - ").append(nombreDepartamento).append("\n");
        sb.append("Municipio: ").append(s.getIdMunicipio()).append(" - ").append(nombreMunicipio).append("\n");
        sb.append("Distrito: ").append(s.getIdDistrito()).append(" - ").append(nombreDistrito).append("\n");
        sb.append("Hora Apertura: ").append(s.getHorarioApertura()).append("\n");
        sb.append("Hora Cierre: ").append(s.getHorarioCierre()).append("\n");

        tvResultado.setText(sb.toString());
    }

    private String consultarNombre(SQLiteDatabase db, String tabla, String campoNombre, String campoId, int id) {
        Cursor c = db.rawQuery("SELECT " + campoNombre + " FROM " + tabla + " WHERE " + campoId + " = ?",
                new String[]{String.valueOf(id)});
        String nombre = "Desconocido";
        if (c.moveToFirst()) {
            nombre = c.getString(0);
        }
        c.close();
        return nombre;
    }

    private String consultarNombreDoble(SQLiteDatabase db, String tabla, String campoNombre,
                                        String campoId1, int id1, String campoId2, int id2) {
        Cursor c = db.rawQuery("SELECT " + campoNombre + " FROM " + tabla + " WHERE " + campoId1 + " = ? AND " + campoId2 + " = ?",
                new String[]{String.valueOf(id1), String.valueOf(id2)});
        String nombre = "Desconocido";
        if (c.moveToFirst()) {
            nombre = c.getString(0);
        }
        c.close();
        return nombre;
    }

    private String consultarNombreTriple(SQLiteDatabase db, String tabla, String campoNombre,
                                         String campoId1, int id1, String campoId2, int id2, String campoId3, int id3) {
        Cursor c = db.rawQuery("SELECT " + campoNombre + " FROM " + tabla +
                        " WHERE " + campoId1 + " = ? AND " + campoId2 + " = ? AND " + campoId3 + " = ?",
                new String[]{String.valueOf(id1), String.valueOf(id2), String.valueOf(id3)});
        String nombre = "Desconocido";
        if (c.moveToFirst()) {
            nombre = c.getString(0);
        }
        c.close();
        return nombre;
    }


    private void eliminarSucursal() {
        if (idSucursalSeleccionada == -1) {
            Toast.makeText(this, "Busca una sucursal primero", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SucursalDAO dao = new SucursalDAO(db);
        int filas = dao.eliminar(idSucursalSeleccionada);
        db.close();

        if (filas > 0) {
            Toast.makeText(this, "Sucursal eliminada correctamente", Toast.LENGTH_LONG).show();
            cargarSpinnerSucursales();
            limpiarCampos();
        } else {
            Toast.makeText(this, "Error al eliminar sucursal", Toast.LENGTH_LONG).show();
        }
    }

    private void limpiarCampos() {
        spinnerSucursal.setSelection(0);
        tvResultado.setText("Aquí se mostrará la información de la sucursal seleccionada.");
        idSucursalSeleccionada = -1;
    }
}
