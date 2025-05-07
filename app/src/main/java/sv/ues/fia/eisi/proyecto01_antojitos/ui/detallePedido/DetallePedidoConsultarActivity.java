package sv.ues.fia.eisi.proyecto01_antojitos.ui.detallePedido;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.Producto;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.ProductoDAO;

public class DetallePedidoConsultarActivity extends AppCompatActivity {

    private EditText editTextIdPedido;
    private Button btnBuscar;
    private TextView textViewResultado;

    private SQLiteDatabase db;
    private DetallePedidoDAO detallePedidoDAO;
    private ProductoDAO productoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido_consultar);

        editTextIdPedido = findViewById(R.id.editTextIdPedido);
        btnBuscar = findViewById(R.id.btnBuscarDetalles);
        textViewResultado = findViewById(R.id.textViewResultadoDetalles);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();
        detallePedidoDAO = new DetallePedidoDAO(db);
        productoDAO = new ProductoDAO(db);

        btnBuscar.setOnClickListener(v -> consultarDetalles());
    }

    private void consultarDetalles() {
        String input = editTextIdPedido.getText().toString().trim();
        if (input.isEmpty()) {
            Toast.makeText(this, "Ingrese un ID de pedido válido", Toast.LENGTH_SHORT).show();
            return;
        }

        int idPedido = Integer.parseInt(input);
        List<DetallePedido> lista = detallePedidoDAO.obtenerPorPedido(idPedido);

        if (lista.isEmpty()) {
            textViewResultado.setText("No hay productos asociados a este pedido.");
            return;
        }

        StringBuilder resultado = new StringBuilder("Detalles del pedido " + idPedido + ":\n");
        for (DetallePedido d : lista) {
            Producto producto = productoDAO.obtenerPorId(d.getIdProducto());
            String nombreProducto = (producto != null) ? producto.getNombreProducto() : "Desconocido";

            resultado.append("\n• Producto: ").append(nombreProducto)
                    .append("\n  Cantidad: ").append(d.getCantidad())
                    .append("\n  Subtotal: $").append(String.format("%.2f", d.getSubtotal()))
                    .append("\n");
        }

        textViewResultado.setText(resultado.toString());
    }
}
