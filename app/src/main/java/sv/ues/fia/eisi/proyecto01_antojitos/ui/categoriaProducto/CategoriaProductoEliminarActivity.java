package sv.ues.fia.eisi.proyecto01_antojitos.ui.categoriaProducto;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class CategoriaProductoEliminarActivity extends AppCompatActivity {

    private Spinner spinnerCategoriaEliminar;
    private Button btnEliminarCategoria;

    private SQLiteDatabase db;
    private CategoriaProductoDAO dao;
    private List<CategoriaProducto> listaCategorias;
    private Map<String, CategoriaProducto> mapCategorias;
    private CategoriaProducto seleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_producto_eliminar);

        spinnerCategoriaEliminar = findViewById(R.id.spinnerCategoriaEliminar);
        btnEliminarCategoria = findViewById(R.id.btnEliminarCategoria);
        TextView textDetalleCategoria = findViewById(R.id.textDetalleCategoria);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        dao = new CategoriaProductoDAO(db);

        cargarSpinner();

        spinnerCategoriaEliminar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position == 0) {
                    seleccionada = null;
                    textDetalleCategoria.setText("");
                } else {
                    String label = parent.getItemAtPosition(position).toString();
                    seleccionada = mapCategorias.get(label);

                    if (seleccionada != null) {
                        String disponibleStr = (seleccionada.getDisponibleCategoria() == 1) ?
                                getString(R.string.respuesta_si) : getString(R.string.respuesta_no);

                        String detalle = getString(R.string.categoria_producto_eliminar_detalle_formato,
                                seleccionada.getIdCategoriaProducto(),
                                seleccionada.getNombreCategoria(),
                                seleccionada.getDescripcionCategoria(),
                                seleccionada.getHoraDisponibleDesde(),
                                seleccionada.getHoraDisponibleHasta(),
                                disponibleStr
                        );

                        textDetalleCategoria.setText(getString(R.string.categoria_producto_eliminar_detalle_titulo) + "\n\n" + detalle);
                    }
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnEliminarCategoria.setOnClickListener(v -> eliminarCategoria());
    }

    private void cargarSpinner() {
        listaCategorias = dao.obtenerTodos(true); // solo activas
        actualizarDisponibilidadSegunHora(listaCategorias);

        mapCategorias = new HashMap<>();
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.categoria_producto_eliminar_spinner_default));

        for (CategoriaProducto c : listaCategorias) {
            String label = c.getIdCategoriaProducto() + " - " + c.getNombreCategoria();
            items.add(label);
            mapCategorias.put(label, c);
        }

        spinnerCategoriaEliminar.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void eliminarCategoria() {
        if (seleccionada == null) {
            Toast.makeText(this, getString(R.string.categoria_producto_eliminar_toast_seleccionar), Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = dao.eliminar(seleccionada.getIdCategoriaProducto());
        if (ok) {
            Toast.makeText(this, getString(R.string.categoria_producto_eliminar_toast_ok), Toast.LENGTH_SHORT).show();
            cargarSpinner(); // refrescar
            spinnerCategoriaEliminar.setSelection(0);
        } else {
            Toast.makeText(this, getString(R.string.categoria_producto_eliminar_toast_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarDisponibilidadSegunHora(List<CategoriaProducto> lista) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String ahoraStr = sdf.format(new Date());

        try {
            Date ahora = sdf.parse(ahoraStr);

            for (CategoriaProducto c : lista) {
                Date desde = sdf.parse(c.getHoraDisponibleDesde());
                Date hasta = sdf.parse(c.getHoraDisponibleHasta());

                boolean disponible = ahora.equals(desde) || ahora.equals(hasta)
                        || (ahora.after(desde) && ahora.before(hasta));

                int nuevoEstado = disponible ? 1 : 0;

                if (c.getDisponibleCategoria() != nuevoEstado) {
                    c.setDisponibleCategoria(nuevoEstado);
                    dao.actualizar(c);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) db.close();
    }
}