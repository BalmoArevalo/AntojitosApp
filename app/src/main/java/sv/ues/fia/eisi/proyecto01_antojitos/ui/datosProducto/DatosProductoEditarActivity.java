package sv.ues.fia.eisi.proyecto01_antojitos.ui.datosProducto;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
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

public class DatosProductoEditarActivity extends AppCompatActivity {

    private Spinner spinnerSucursal, spinnerProducto;
    private Button btnBuscar, btnActualizar, btnLimpiar;
    private EditText editPrecio, editStock;
    private Switch switchDisponible;

    private DBHelper dbHelper;
    private List<Integer> listaIdsSucursal, listaIdsProducto;
    private DatosProducto datosActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datos_producto_editar);
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.scroll_editar_datos_producto),
                (v, insets) -> {
                    Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
                    return insets;
                });

        // Inicialización
        dbHelper = new DBHelper(this);
        spinnerSucursal    = findViewById(R.id.spinnerSucursal);
        spinnerProducto    = findViewById(R.id.spinnerProducto);
        editPrecio         = findViewById(R.id.editPrecio);
        editStock          = findViewById(R.id.editStock);
        switchDisponible   = findViewById(R.id.switchDisponible);
        btnBuscar          = findViewById(R.id.btnBuscar);
        btnActualizar      = findViewById(R.id.btnActualizar);
        btnLimpiar         = findViewById(R.id.btnLimpiar);

        disableForm();

        loadSucursales();
        spinnerSucursal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadProductosConDatos(listaIdsSucursal.get(position));
                clearFields();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnBuscar.setOnClickListener(v -> buscarDatos());
        btnActualizar.setOnClickListener(v -> actualizarDatos());
        btnLimpiar.setOnClickListener(v -> clearFields());
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
        spinnerSucursal.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels));
    }

    private void loadProductosConDatos(int idSucursal) {
        listaIdsProducto = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Cursor c = dbHelper.getReadableDatabase().rawQuery(
                "SELECT DP.ID_PRODUCTO, P.NOMBRE_PRODUCTO FROM DATOSPRODUCTO DP " +
                        "JOIN PRODUCTO P ON DP.ID_PRODUCTO=P.ID_PRODUCTO " +
                        "WHERE DP.ID_SUCURSAL=? AND DP.ACTIVO_DATOSPRODUCTO=1",
                new String[]{ String.valueOf(idSucursal) });
        while (c.moveToNext()) {
            listaIdsProducto.add(c.getInt(0));
            labels.add(c.getInt(0) + " - " + c.getString(1));
        }
        c.close();
        spinnerProducto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels));
    }

    private void buscarDatos() {
        int posSuc = spinnerSucursal.getSelectedItemPosition();
        int posProd = spinnerProducto.getSelectedItemPosition();
        if (posSuc < 0 || posProd < 0) {
            Toast.makeText(this, "Selecciona sucursal y producto", Toast.LENGTH_SHORT).show();
            return;
        }

        int idSuc = listaIdsSucursal.get(posSuc);
        int idProd = listaIdsProducto.get(posProd);
        datosActual = new DatosProductoDAO(dbHelper.getReadableDatabase()).find(idSuc, idProd);

        if (datosActual == null) {
            Toast.makeText(this, "No se encontraron datos existentes", Toast.LENGTH_SHORT).show();
            return;
        }

        editPrecio.setText(String.valueOf(datosActual.getPrecioSucursalProducto()));
        editStock.setText(String.valueOf(datosActual.getStock()));
        switchDisponible.setChecked(datosActual.isActivo());

        enableForm();
    }

    private void actualizarDatos() {
        if (datosActual == null) {
            Toast.makeText(this, "Primero busca un registro", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double precio = Double.parseDouble(editPrecio.getText().toString().trim());
            int stock = Integer.parseInt(editStock.getText().toString().trim());
            boolean activo = switchDisponible.isChecked();

            datosActual.setPrecioSucursalProducto(precio);
            datosActual.setStock(stock);
            datosActual.setActivo(activo);

            int rows = new DatosProductoDAO(dbHelper.getWritableDatabase()).update(datosActual);
            if (rows > 0) {
                Toast.makeText(this, "Registro actualizado", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio o stock inválido", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        editPrecio.setText("");
        editStock.setText("");
        switchDisponible.setChecked(true);
        disableForm();
        datosActual = null;
    }

    private void disableForm() {
        editPrecio.setEnabled(false);
        editStock.setEnabled(false);
        switchDisponible.setEnabled(false);
        btnActualizar.setEnabled(false);
    }

    private void enableForm() {
        editPrecio.setEnabled(true);
        editStock.setEnabled(true);
        switchDisponible.setEnabled(true);
        btnActualizar.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
