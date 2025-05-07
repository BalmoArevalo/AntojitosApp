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
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.categoriaProducto.CategoriaProducto;

public class ProductoCrearActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerCategoria;
    private TextInputEditText txtNombreProducto;
    private TextInputEditText txtDescripcionProducto;
    private MaterialButton btnGuardarProducto;
    private Map<String, Integer> categoriasMap;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_crear);

        // Inicializar base de datos
        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        // Inicializar vistas y cargar datos
        inicializarVistas();
        cargarCategorias();
    }

    private void inicializarVistas() {
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        txtNombreProducto = findViewById(R.id.txtNombreProducto);
        txtDescripcionProducto = findViewById(R.id.txtDescripcionProducto);
        btnGuardarProducto = findViewById(R.id.btnGuardarProducto);
        categoriasMap = new HashMap<>();

        btnGuardarProducto.setOnClickListener(v -> validarYGuardarProducto());
    }

    private void cargarCategorias() {
        List<CategoriaProducto> categorias = new ArrayList<>();
        List<String> nombresCategorias = new ArrayList<>();

        // Consulta SQL para obtener categorías activas
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

            if (nombresCategorias.isEmpty()) {
                Toast.makeText(this, "No hay categorías disponibles", Toast.LENGTH_SHORT).show();
                finish();
                return;
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
            finish();
        }
    }

    private void validarYGuardarProducto() {
        String nombreCategoria = spinnerCategoria.getText().toString();
        String nombreProducto = txtNombreProducto.getText().toString().trim();
        String descripcion = txtDescripcionProducto.getText().toString().trim();

        if (TextUtils.isEmpty(nombreCategoria)) {
            Toast.makeText(this, "Debe seleccionar una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nombreProducto)) {
            Toast.makeText(this, "Debe ingresar el nombre del producto", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer idCategoria = categoriasMap.get(nombreCategoria);
        if (idCategoria == null) {
            Toast.makeText(this, "Categoría no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        guardarProducto(nombreProducto, idCategoria, descripcion);
    }

    private void guardarProducto(String nombre, int idCategoria, String descripcion) {
        try {
            String sql = "INSERT INTO PRODUCTO (ID_CATEGORIAPRODUCTO, NOMBRE_PRODUCTO, " +
                    "DESCRIPCION_PRODUCTO, ACTIVO_PRODUCTO) VALUES (?, ?, ?, 1)";

            Object[] args = {idCategoria, nombre, descripcion};

            db.execSQL(sql, args);

            Toast.makeText(this, "Producto guardado exitosamente", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar el producto: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}