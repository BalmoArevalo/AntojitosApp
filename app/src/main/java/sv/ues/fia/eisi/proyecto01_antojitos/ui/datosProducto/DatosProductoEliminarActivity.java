package sv.ues.fia.eisi.proyecto01_antojitos.ui.datosProducto;

import android.database.Cursor;
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

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DatosProductoEliminarActivity extends AppCompatActivity {

    private Spinner spinnerDatosProducto;
    private TextView tvResultadoDatosProducto;
    private Button btnEliminar;
    private Button btnLimpiar;
    private DBHelper dbHelper;
    private List<int[]> listaClaves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datos_producto_eliminar);
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.scroll_eliminar_datos_producto),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                }
        );

        dbHelper = new DBHelper(this);
        spinnerDatosProducto = findViewById(R.id.spinnerDatosProducto);
        tvResultadoDatosProducto = findViewById(R.id.tvResultadoDatosProducto);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnLimpiar = findViewById(R.id.btnLimpiar);

        loadRegistros();

        spinnerDatosProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    tvResultadoDatosProducto.setText("");
                    btnEliminar.setEnabled(false);
                } else {
                    showDetalles(position - 1);
                    btnEliminar.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tvResultadoDatosProducto.setText("");
                btnEliminar.setEnabled(false);
            }
        });

        btnEliminar.setOnClickListener(v -> eliminarRegistro());
        btnLimpiar.setOnClickListener(v -> {
            spinnerDatosProducto.setSelection(0);
            tvResultadoDatosProducto.setText("");
            btnEliminar.setEnabled(false);
        });
    }

    private void loadRegistros() {
        listaClaves = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        labels.add("Selecciona un registro activo");

        String sql = "SELECT dp.ID_SUCURSAL, dp.ID_PRODUCTO, s.NOMBRE_SUCURSAL, p.NOMBRE_PRODUCTO " +
                "FROM DATOSPRODUCTO dp " +
                "JOIN SUCURSAL s ON dp.ID_SUCURSAL = s.ID_SUCURSAL " +
                "JOIN PRODUCTO p ON dp.ID_PRODUCTO = p.ID_PRODUCTO " +
                "WHERE dp.ACTIVO_DATOSPRODUCTO = 1";

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int idSuc = cursor.getInt(0);
            int idProd = cursor.getInt(1);
            String sucName = cursor.getString(2);
            String prodName = cursor.getString(3);
            listaClaves.add(new int[]{idSuc, idProd});
            labels.add(idSuc + " - " + sucName + ": " + idProd + " - " + prodName);
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDatosProducto.setAdapter(adapter);
        spinnerDatosProducto.setSelection(0);
        btnEliminar.setEnabled(false);
    }

    private void showDetalles(int index) {
        int[] clave = listaClaves.get(index);
        int idSuc = clave[0];
        int idProd = clave[1];
        DatosProducto dp = new DatosProductoDAO(
                dbHelper.getReadableDatabase()).find(idSuc, idProd);
        if (dp != null) {
            String info = "Sucursal: " + idSuc + " - " + getSucursalName(idSuc)
                    + "\nProducto: " + idProd + " - " + getProductoName(idProd)
                    + "\nPrecio: " + dp.getPrecioSucursalProducto()
                    + "\nStock: " + dp.getStock();
            tvResultadoDatosProducto.setText(info);
        } else {
            tvResultadoDatosProducto.setText("Registro no válido o ya inactivo.");
        }
    }

    private String getSucursalName(int idSucursal) {
        Cursor c = dbHelper.getReadableDatabase().rawQuery(
                "SELECT NOMBRE_SUCURSAL FROM SUCURSAL WHERE ID_SUCURSAL = ?",
                new String[]{String.valueOf(idSucursal)});
        String name = "";
        if (c.moveToFirst()) name = c.getString(0);
        c.close();
        return name;
    }

    private String getProductoName(int idProducto) {
        Cursor c = dbHelper.getReadableDatabase().rawQuery(
                "SELECT NOMBRE_PRODUCTO FROM PRODUCTO WHERE ID_PRODUCTO = ?",
                new String[]{String.valueOf(idProducto)});
        String name = "";
        if (c.moveToFirst()) name = c.getString(0);
        c.close();
        return name;
    }

    private void eliminarRegistro() {
        int position = spinnerDatosProducto.getSelectedItemPosition();
        if (position <= 0) return;
        int[] clave = listaClaves.get(position - 1);
        int rows = new DatosProductoDAO(
                dbHelper.getWritableDatabase()).delete(clave[0], clave[1]);
        if (rows > 0) {
            Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show();
            loadRegistros();
        } else {
            Toast.makeText(this, "Error al eliminar registro", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
