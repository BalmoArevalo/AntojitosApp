package sv.ues.fia.eisi.proyecto01_antojitos.ui.detallePedido;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.Producto;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.ProductoDAO;

public class DetallePedidoEliminarActivity extends AppCompatActivity {

    private EditText editTextIdEliminar;
    private TextView textViewDetalle;
    private Button btnBuscar, btnEliminar;

    private SQLiteDatabase db;
    private DetallePedidoDAO detallePedidoDAO;
    private ProductoDAO productoDAO;
    private DetallePedido detalleEncontrado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido_eliminar);

        editTextIdEliminar = findViewById(R.id.editTextIdEliminar);
        textViewDetalle = findViewById(R.id.textViewDetalle);
        btnBuscar = findViewById(R.id.btnBuscarDetalle);
        btnEliminar = findViewById(R.id.btnEliminarDetalle);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        detallePedidoDAO = new DetallePedidoDAO(db);
        productoDAO = new ProductoDAO(db);

        btnEliminar.setEnabled(false);

        btnBuscar.setOnClickListener(v -> buscarDetalle());
        btnEliminar.setOnClickListener(v -> eliminarDetalle());
    }

    private void buscarDetalle() {
        String input = editTextIdEliminar.getText().toString().trim();
        if (input.isEmpty()) {
            Toast.makeText(this, "Ingrese un ID válido", Toast.LENGTH_SHORT).show();
            return;
        }

        int idDetalle = Integer.parseInt(input);
        detalleEncontrado = detallePedidoDAO.consultarPorId(idDetalle);

        if (detalleEncontrado != null) {
            Producto p = productoDAO.obtenerPorId(detalleEncontrado.getIdProducto());
            String nombreProd = (p != null) ? p.getNombreProducto() : "Desconocido";

            String info = "Detalle encontrado:\n"
                    + "ID Detalle: " + detalleEncontrado.getIdDetallePedido() + "\n"
                    + "ID Pedido: " + detalleEncontrado.getIdPedido() + "\n"
                    + "Producto: " + nombreProd + "\n"
                    + "Cantidad: " + detalleEncontrado.getCantidad() + "\n"
                    + "Subtotal: $" + String.format("%.2f", detalleEncontrado.getSubtotal());

            textViewDetalle.setText(info);
            btnEliminar.setEnabled(true);
        } else {
            textViewDetalle.setText("No se encontró el detalle.");
            btnEliminar.setEnabled(false);
        }
    }

    private void eliminarDetalle() {
        if (detalleEncontrado == null) return;

        int filas = detallePedidoDAO.eliminar(detalleEncontrado.getIdDetallePedido());
        if (filas > 0) {
            Toast.makeText(this, "Detalle eliminado correctamente", Toast.LENGTH_SHORT).show();
            textViewDetalle.setText("");
            editTextIdEliminar.setText("");
            btnEliminar.setEnabled(false);
        } else {
            Toast.makeText(this, "Error al eliminar el detalle", Toast.LENGTH_SHORT).show();
        }
    }
}
