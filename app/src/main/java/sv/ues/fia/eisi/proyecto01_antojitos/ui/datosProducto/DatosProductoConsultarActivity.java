package sv.ues.fia.eisi.proyecto01_antojitos.ui.datosProducto;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.ProductoDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.datosProducto.DatosProducto;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.datosProducto.DatosProductoDAO;

public class DatosProductoConsultarActivity extends AppCompatActivity {
    private Spinner spinnerSucursal;
    private Spinner spinnerProducto;
    private Button btnMostrarDetalles;
    private TextView tvResultado;
    private DBHelper dbHelper;

    private List<Integer> listaIdsSucursal;
    private List<Integer> listaIdsProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datos_producto_consultar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scroll_consultar_datos_producto), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);
        spinnerSucursal = findViewById(R.id.spinnerSucursal);
        spinnerProducto = findViewById(R.id.spinnerProducto);
        btnMostrarDetalles = findViewById(R.id.btnMostrarDetalles);
        tvResultado = findViewById(R.id.tvResultadoDatosProducto);

        cargarSucursales();

        spinnerSucursal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int idSuc = listaIdsSucursal.get(position);
                cargarProductos(idSuc);
                tvResultado.setText("");
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnMostrarDetalles.setOnClickListener(v -> mostrarDetalles());
    }

    private void cargarSucursales() {
        listaIdsSucursal = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Cursor c = dbHelper.getReadableDatabase().rawQuery(
                "SELECT ID_SUCURSAL, NOMBRE_SUCURSAL FROM SUCURSAL WHERE ACTIVO_SUCURSAL=1", null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String nombre = c.getString(1);
            listaIdsSucursal.add(id);
            labels.add(id + " - " + nombre);
        }
        c.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSucursal.setAdapter(adapter);
    }

    private void cargarProductos(int idSucursal) {
        listaIdsProducto = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Cursor c = dbHelper.getReadableDatabase().rawQuery(
                "SELECT P.ID_PRODUCTO, P.NOMBRE_PRODUCTO " +
                        "FROM PRODUCTO P INNER JOIN DATOSPRODUCTO DP " +
                        "ON P.ID_PRODUCTO = DP.ID_PRODUCTO " +
                        "WHERE DP.ID_SUCURSAL=? AND DP.ACTIVO_DATOSPRODUCTO=1 AND DP.STOCK>0",
                new String[]{String.valueOf(idSucursal)});
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String nombre = c.getString(1);
            listaIdsProducto.add(id);
            labels.add(id + " - " + nombre);
        }
        c.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProducto.setAdapter(adapter);
    }

    private void mostrarDetalles() {
        int posSuc = spinnerSucursal.getSelectedItemPosition();
        int posProd = spinnerProducto.getSelectedItemPosition();
        if (posSuc < 0 || posProd < 0) {
            Toast.makeText(this, getString(R.string.datos_producto_consultar_toast_seleccion_invalida), Toast.LENGTH_SHORT).show();
            return;
        }
        int idSuc = listaIdsSucursal.get(posSuc);
        int idProd = listaIdsProducto.get(posProd);
        DatosProductoDAO dpDao = new DatosProductoDAO(dbHelper.getReadableDatabase());
        DatosProducto dp = dpDao.find(idSuc, idProd);
        if (dp != null) {
            String info = getString(R.string.datos_producto_consultar_resultado,
                    idProd, dp.getPrecioSucursalProducto(), dp.getStock());
            tvResultado.setText(info);
        } else {
            tvResultado.setText(getString(R.string.datos_producto_consultar_no_encontrado));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}