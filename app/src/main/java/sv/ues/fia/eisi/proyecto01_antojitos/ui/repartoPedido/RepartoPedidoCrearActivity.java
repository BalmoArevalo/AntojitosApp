package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido;

import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido.Pedido;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido.PedidoDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor.Repartidor;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor.RepartidorDAO;

import java.text.SimpleDateFormat;
import java.util.*;

public class RepartoPedidoCrearActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompletePedido, autoCompleteRepartidor;
    private EditText editTextHoraAsignacion, editTextUbicacion, editTextFechaEntrega;
    private Button btnGuardar;
    private SQLiteDatabase db;
    private RepartoPedidoDAO dao;
    private Calendar calendar = Calendar.getInstance();
    private List<Pedido> listaPedidos;
    private List<Repartidor> listaRepartidores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reparto_pedido_crear);

        autoCompletePedido = findViewById(R.id.autoCompletePedido);
        autoCompleteRepartidor = findViewById(R.id.autoCompleteRepartidor);
        editTextHoraAsignacion = findViewById(R.id.editTextHoraAsignacion);
        editTextUbicacion = findViewById(R.id.editTextUbicacion);
        editTextFechaEntrega = findViewById(R.id.editTextFechaEntrega);
        btnGuardar = findViewById(R.id.btnGuardarReparto);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        dao = new RepartoPedidoDAO(db);

        PedidoDAO pedidoDAO = new PedidoDAO(db);
        RepartidorDAO repartidorDAO = new RepartidorDAO(db);
        listaPedidos = pedidoDAO.obtenerTodos();
        listaRepartidores = repartidorDAO.obtenerActivos();

        ArrayAdapter<String> adapterPedidos = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                obtenerDescripcionesPedidos(listaPedidos)
        );
        autoCompletePedido.setAdapter(adapterPedidos);

        ArrayAdapter<String> adapterRepartidores = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                obtenerDescripcionesRepartidores(listaRepartidores)
        );
        autoCompleteRepartidor.setAdapter(adapterRepartidores);

        editTextHoraAsignacion.setOnClickListener(v -> mostrarTimePicker(editTextHoraAsignacion));
        editTextFechaEntrega.setOnClickListener(v -> mostrarDateTimePicker());

        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void guardar() {
        String horaAsignacion = editTextHoraAsignacion.getText().toString().trim();
        String ubicacion = editTextUbicacion.getText().toString().trim();
        String fechaEntrega = editTextFechaEntrega.getText().toString().trim();

        int indexPedido = autoCompletePedido.getListSelection();
        int indexRepartidor = autoCompleteRepartidor.getListSelection();

        if (indexPedido < 0 || indexPedido >= listaPedidos.size()
                || indexRepartidor < 0 || indexRepartidor >= listaRepartidores.size()
                || horaAsignacion.isEmpty() || ubicacion.isEmpty()) {
            Toast.makeText(this, getString(R.string.repartopedido_toast_incompleto), Toast.LENGTH_SHORT).show();
            return;
        }

        int idPedido = listaPedidos.get(indexPedido).getIdPedido();
        int idRepartidor = listaRepartidores.get(indexRepartidor).getIdRepartidor();

        RepartoPedido r = new RepartoPedido();
        r.setIdPedido(idPedido);
        r.setIdRepartoPedido(idRepartidor);
        r.setHoraAsignacion(horaAsignacion);
        r.setUbicacionEntrega(ubicacion);
        r.setFechaHoraEntrega(fechaEntrega.isEmpty() ? null : fechaEntrega);

        long result = dao.insertar(r);
        if (result != -1) {
            Toast.makeText(this, getString(R.string.repartopedido_toast_insertado_ok), Toast.LENGTH_SHORT).show();
            limpiar();
        } else {
            Toast.makeText(this, getString(R.string.repartopedido_toast_insertado_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiar() {
        autoCompletePedido.setText("");
        autoCompleteRepartidor.setText("");
        editTextHoraAsignacion.setText("");
        editTextUbicacion.setText("");
        editTextFechaEntrega.setText("");
        autoCompletePedido.requestFocus();
    }

    private void mostrarTimePicker(EditText target) {
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        new TimePickerDialog(this, (view, hour, min) -> {
            target.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, min));
        }, h, m, true).show();
    }

    private void mostrarDateTimePicker() {
        new android.app.DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            new TimePickerDialog(this, (view1, hour, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                editTextFechaEntrega.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private List<String> obtenerDescripcionesPedidos(List<Pedido> pedidos) {
        List<String> descripciones = new ArrayList<>();
        for (Pedido p : pedidos) {
            descripciones.add("Pedido " + p.getIdPedido());
        }
        return descripciones;
    }

    private List<String> obtenerDescripcionesRepartidores(List<Repartidor> repartidores) {
        List<String> descripciones = new ArrayList<>();
        for (Repartidor r : repartidores) {
            descripciones.add("Repartidor " + r.getIdRepartidor() + ": " + r.getNombreRepartidor() + " " + r.getApellidoRepartidor());
        }
        return descripciones;
    }
}