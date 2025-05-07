package sv.ues.fia.eisi.proyecto01_antojitos.ui.producto;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.Producto;

public class ProductoEditarActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerProducto;
    private AutoCompleteTextView spinnerCategoria;
    private TextInputEditText txtNombreProducto;
    private TextInputEditText txtDescripcionProducto;
    private SwitchMaterial switchEstadoProducto;
    private MaterialButton btnGuardarCambios;

    private Map<String, Producto> productosMap;
    private Map<String, Integer> categoriasMap;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_editar);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        inicializarVistas();
        cargarProductos();
        cargarCategorias();
    }

    private void inicializarVistas() {
        spinnerProducto = findViewById(R.id.spinnerProducto);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        txtNombreProducto = findViewById(R.id.txtNombreProducto);
        txtDescripcionProducto = findViewById(R.id.txtDescripcionProducto);
        switchEstadoProducto = findViewById(R.id.switchEstadoProducto);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        productosMap = new HashMap<>();
        categoriasMap = new HashMap<>();

        spinnerProducto.setOnItemClickListener((parent, view, position, id) -> {
            String seleccion = parent.getItemAtPosition(position).toString();
            Producto producto = productosMap.get(seleccion);
            if (producto != null) {
                cargarDatosProducto(producto);
            }
        });

        btnGuardarCambios.setOnClickListener(v -> validarYGuardarCambios());
    }

    private void cargarProductos() {
        List<String> nombresProductos = new ArrayList<>();

        String sql = "SELECT p.ID_PRODUCTO, p.NOMBRE_PRODUCTO, p.DESCRIPCION_PRODUCTO, " +
                "p.ID_CATEGORIAPRODUCTO, p.ACTIVO_PRODUCTO " +
                "FROM PRODUCTO p ORDER BY p.NOMBRE_PRODUCTO";

        try (Cursor cursor = db.rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                Producto producto = new Producto();
                producto.setIdProducto(cursor.getInt(cursor.getColumnIndexOrThrow("ID_PRODUCTO")));
                producto.setNombreProducto(cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_PRODUCTO")));
                producto.setDescripcionProducto(cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPCION_PRODUCTO")));
                producto.setIdCategoriaProducto(cursor.getInt(cursor.getColumnIndexOrThrow("ID_CATEGORIAPRODUCTO")));
                producto.setActivoProducto(cursor.getInt(cursor.getColumnIndexOrThrow("ACTIVO_PRODUCTO")));

                // Crear una cadena que indique el estado del producto
                String nombreMostrado = producto.getNombreProducto() +
                        (producto.getActivoProducto() == 1 ? " ✓" : " ❌");

                nombresProductos.add(nombreMostrado);
                productosMap.put(nombreMostrado, producto);
            }

            if (nombresProductos.isEmpty()) {
                Toast.makeText(this, "No hay productos disponibles", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    nombresProductos
            );

            spinnerProducto.setAdapter(adapter);

        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar los productos: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void cargarCategorias() {
        List<String> nombresCategorias = new ArrayList<>();

        String sql = "SELECT ID_CATEGORIAPRODUCTO, NOMBRE_CATEGORIA " +
                "FROM CATEGORIAPRODUCTO " +
                "WHERE ACTIVO_CATEGORIAPRODUCTO = 1 " +
                "ORDER BY NOMBRE_CATEGORIA";

        try (Cursor cursor = db.rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_CATEGORIAPRODUCTO"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_CATEGORIA"));

                nombresCategorias.add(nombre);
                categoriasMap.put(nombre, id);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    nombresCategorias
            );

            spinnerCategoria.setAdapter(adapter);

        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar las categorías: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarDatosProducto(Producto producto) {
        // Encontrar y seleccionar la categoría correcta
        for (Map.Entry<String, Integer> entry : categoriasMap.entrySet()) {
            if (entry.getValue() == producto.getIdCategoriaProducto()) {
                spinnerCategoria.setText(entry.getKey(), false);
                break;
            }
        }

        txtNombreProducto.setText(producto.getNombreProducto());
        txtDescripcionProducto.setText(producto.getDescripcionProducto());
        switchEstadoProducto.setChecked(producto.getActivoProducto() == 1);
    }

    private void validarYGuardarCambios() {
        String productoSeleccionado = spinnerProducto.getText().toString();
        String categoriaSeleccionada = spinnerCategoria.getText().toString();
        String nombreProducto = txtNombreProducto.getText().toString().trim();
        String descripcion = txtDescripcionProducto.getText().toString().trim();

        if (!productosMap.containsKey(productoSeleccionado)) {
            Toast.makeText(this, "Debe seleccionar un producto", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(categoriaSeleccionada)) {
            Toast.makeText(this, "Debe seleccionar una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nombreProducto)) {
            Toast.makeText(this, "Debe ingresar el nombre del producto", Toast.LENGTH_SHORT).show();
            return;
        }

        Producto producto = productosMap.get(productoSeleccionado);
        Integer idCategoria = categoriasMap.get(categoriaSeleccionada);

        if (producto != null && idCategoria != null) {
            actualizarProducto(producto.getIdProducto(), idCategoria, nombreProducto,
                    descripcion, switchEstadoProducto.isChecked() ? 1 : 0);
        }
    }

    private void actualizarProducto(int idProducto, int idCategoria, String nombre,
                                    String descripcion, int activo) {
        try {
            String sql = "UPDATE PRODUCTO SET ID_CATEGORIAPRODUCTO = ?, " +
                    "NOMBRE_PRODUCTO = ?, DESCRIPCION_PRODUCTO = ?, " +
                    "ACTIVO_PRODUCTO = ? WHERE ID_PRODUCTO = ?";

            Object[] args = {idCategoria, nombre, descripcion, activo, idProducto};

            db.execSQL(sql, args);

            Toast.makeText(this, "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show();

            // Recargar la lista de productos para mostrar los cambios
            cargarProductos();

            // Limpiar los campos
            limpiarCampos();

        } catch (Exception e) {
            Toast.makeText(this, "Error al actualizar el producto: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Nuevo método para limpiar los campos
    private void limpiarCampos() {
        spinnerProducto.setText("", false);
        spinnerCategoria.setText("", false);
        txtNombreProducto.setText("");
        txtDescripcionProducto.setText("");
        switchEstadoProducto.setChecked(true); // Por defecto activo
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}