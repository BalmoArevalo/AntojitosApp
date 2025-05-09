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

public class CategoriaProductoConsultarActivity extends AppCompatActivity {

    private ListView listViewCategorias;
    private SQLiteDatabase db;
    private CategoriaProductoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_producto_consultar);

        listViewCategorias = findViewById(R.id.listViewCategorias);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();
        dao = new CategoriaProductoDAO(db);

        cargarCategorias();
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
                    dao.actualizar(c); // actualiza solo el campo disponible en la BD
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void cargarCategorias() {
        List<CategoriaProducto> lista = dao.obtenerTodos(false); // trae todas
        actualizarDisponibilidadSegunHora(lista); // actualiza basado en hora actual

        List<String> datos = new ArrayList<>();
        for (CategoriaProducto c : lista) {
            if (c.getActivoCategoriaProducto() == 1) { // solo mostrar activas
                String disponibleStr = (c.getDisponibleCategoria() == 1) ?
                        getString(R.string.respuesta_si) : getString(R.string.respuesta_no);

                String item = getString(R.string.categoria_producto_item_formato,
                        c.getIdCategoriaProducto(),
                        c.getNombreCategoria(),
                        c.getDescripcionCategoria(),
                        disponibleStr,
                        c.getHoraDisponibleDesde(),
                        c.getHoraDisponibleHasta());

                datos.add(item);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
        listViewCategorias.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) db.close();
    }
}