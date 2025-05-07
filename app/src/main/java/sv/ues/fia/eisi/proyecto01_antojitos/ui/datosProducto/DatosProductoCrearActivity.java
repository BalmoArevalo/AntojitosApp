package sv.ues.fia.eisi.proyecto01_antojitos.ui.datosProducto;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.ProductoCrearActivity;

public class DatosProductoCrearActivity extends AppCompatActivity {
    private Spinner spinnerSucursal;
    private Spinner spinnerProducto;
    private Button btnCrearProducto;
    private EditText editPrecio;
    private EditText editStock;
    private Button btnGuardar;
    private Button btnLimpiar;
    private DBHelper dbHelper;
    private List<Integer> listaIdsSucursal;
    private List<Integer> listaIdsProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datos_producto_crear);
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.scroll_crear_datos_producto),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                }
        );

        dbHelper = new DBHelper(this);
        spinnerSucursal = findViewById(R.id.spinnerSucursal);
        spinnerProducto = findViewById(R.id.spinnerProducto);
        editPrecio = findViewById(R.id.editPrecio);
        editStock = findViewById(R.id.editStock);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnCrearProducto = findViewById(R.id.btnCrearProducto);

        // Lanzar Activity para crear Producto
        btnCrearProducto.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductoCrearActivity.class);
            startActivity(intent);
        });

        // Cargar sucursales activas
        loadSucursales();

        // Al seleccionar sucursal, cargar productos faltantes
        spinnerSucursal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int idSuc = listaIdsSucursal.get(position);
                loadProductosSinDatos(idSuc);
                editPrecio.setText("");
                editStock.setText("");
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnGuardar.setOnClickListener(v -> saveDatos());
        btnLimpiar.setOnClickListener(v -> resetForm());
    }

    private void loadSucursales() {
        listaIdsSucursal = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Cursor c = dbHelper.getReadableDatabase().rawQuery(
                "SELECT ID_SUCURSAL, NOMBRE_SUCURSAL FROM SUCURSAL WHERE ACTIVO_SUCURSAL=1", null);
        while (c.moveToNext()) {
            listaIdsSucursal.add(c.getInt(0));
            labels.add(c.getInt(0) + " - " + c.getString(1));
        }
        c.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSucursal.setAdapter(adapter);
    }

    private void loadProductosSinDatos(int idSucursal) {
        listaIdsProducto = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        String sql =
                "SELECT P.ID_PRODUCTO, P.NOMBRE_PRODUCTO " +
                        "FROM PRODUCTO P " +
                        "WHERE P.ACTIVO_PRODUCTO=1 " +
                        "AND NOT EXISTS (" +
                        "  SELECT 1 FROM DATOSPRODUCTO DP " +
                        "  WHERE DP.ID_SUCURSAL=? " +
                        "    AND DP.ID_PRODUCTO=P.ID_PRODUCTO " +
                        "    AND DP.ACTIVO_DATOSPRODUCTO=1" +
                        ")";
        Cursor c = dbHelper.getReadableDatabase().rawQuery(sql,
                new String[]{ String.valueOf(idSucursal) });
        while (c.moveToNext()) {
            listaIdsProducto.add(c.getInt(0));
            labels.add(c.getInt(0) + " - " + c.getString(1));
        }
        c.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProducto.setAdapter(adapter);
    }

    private void saveDatos() {
        int posSuc = spinnerSucursal.getSelectedItemPosition();
        int posProd = spinnerProducto.getSelectedItemPosition();
        if (posSuc < 0 || posProd < 0) {
            Toast.makeText(this, "Selecciona sucursal y producto", Toast.LENGTH_SHORT).show();
            return;
        }
        int idSuc = listaIdsSucursal.get(posSuc);
        int idProd = listaIdsProducto.get(posProd);
        double precio;
        int stock;
        try {
            precio = Double.parseDouble(editPrecio.getText().toString().trim());
            stock = Integer.parseInt(editStock.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio o stock inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        long res = new DatosProductoDAO(dbHelper.getWritableDatabase())
                .insert(new DatosProducto(idSuc, idProd, precio, stock, true));
        if (res > 0) {
            Toast.makeText(this,
                    "Datos guardados (Sucursal: " + idSuc + ", Producto: " + idProd + ")",
                    Toast.LENGTH_SHORT).show();
            resetForm();
        } else {
            Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refrescar el spinner de productos al volver de crear un producto
        int posSuc = spinnerSucursal.getSelectedItemPosition();
        if (posSuc >= 0 && listaIdsSucursal != null) {
            loadProductosSinDatos(listaIdsSucursal.get(posSuc));
        }
    }


    private void resetForm() {
        spinnerSucursal.setSelection(0);
        spinnerProducto.setAdapter(null);
        editPrecio.setText("");
        editStock.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}