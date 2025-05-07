package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity; // Para setResult
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
// import android.view.View; // No se usa directamente el View para el listener
// import android.widget.AdapterView; // No se usa directamente
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
// import android.widget.DatePicker; // No se usa directamente
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class FacturaCrearActivity extends AppCompatActivity {

    private FacturaViewModel facturaViewModel;
    private static final String TAG = "FacturaCrearActivity";

    private Spinner spinnerPedido;
    private EditText editFechaEmision;
    private EditText editMontoTotal;
    private Spinner spinnerTipoPago;
    private CheckBox checkPagado;
    private Button btnGuardarFactura;

    private List<Integer> pedidoIds = new ArrayList<>();
    private List<String> tiposPago = Arrays.asList("Efectivo", "Tarjeta", "Bitcoin", "Transferencia", "Otro");

    private Calendar calendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura_crear);

        facturaViewModel = new ViewModelProvider(this).get(FacturaViewModel.class);

        spinnerPedido = findViewById(R.id.spinnerPedido);
        editFechaEmision = findViewById(R.id.editFechaEmision);
        editMontoTotal = findViewById(R.id.editMontoTotal);
        spinnerTipoPago = findViewById(R.id.spinnerTipoPago);
        checkPagado = findViewById(R.id.checkPagado);
        btnGuardarFactura = findViewById(R.id.btnGuardarFactura);

        cargarSpinnerPedidosSinFactura(); // Modificado para cargar solo pedidos elegibles
        cargarSpinnerTipoPago();

        editFechaEmision.setOnClickListener(v -> mostrarDatePickerDialog());
        btnGuardarFactura.setOnClickListener(v -> guardarFactura());
    }

    private void cargarSpinnerPedidosSinFactura() {
        pedidoIds.clear();
        List<String> pedidoDescripciones = new ArrayList<>();

        pedidoDescripciones.add("Seleccione un Pedido (sin factura)...");
        pedidoIds.add(-1); // ID inválido para el placeholder

        DBHelper localDbHelper = new DBHelper(this);
        SQLiteDatabase db = null;
        Cursor cursor = null;

        // Consulta para obtener IDs de pedido que NO están ya en la tabla FACTURA
        String query = "SELECT P.ID_PEDIDO FROM PEDIDO P " +
                "WHERE NOT EXISTS (SELECT 1 FROM FACTURA F WHERE F.ID_PEDIDO = P.ID_PEDIDO) " +
                "ORDER BY P.ID_PEDIDO ASC";
        try {
            db = localDbHelper.getReadableDatabase();
            cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                pedidoIds.add(id);
                pedidoDescripciones.add("Pedido #" + id);
            }
            Log.d(TAG, "Pedidos sin factura cargados en spinner: " + (pedidoDescripciones.size() -1) );
            if (pedidoDescripciones.size() <= 1) { // Solo el placeholder
                Toast.makeText(this, "No hay pedidos disponibles para facturar.", Toast.LENGTH_LONG).show();
                // Podrías deshabilitar el botón de guardar o toda la actividad
                btnGuardarFactura.setEnabled(false);
            } else {
                btnGuardarFactura.setEnabled(true);
            }

        } catch (SQLiteException e) {
            Log.e(TAG, "Error al cargar pedidos sin factura", e);
            Toast.makeText(this, "Error al cargar pedidos disponibles", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, pedidoDescripciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPedido.setAdapter(adapter);
    }

    private void cargarSpinnerTipoPago() {
        List<String> opcionesConPlaceholder = new ArrayList<>();
        opcionesConPlaceholder.add("Seleccione Tipo de Pago...");
        opcionesConPlaceholder.addAll(tiposPago);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opcionesConPlaceholder);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoPago.setAdapter(adapter);
    }

    private void mostrarDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendario.set(Calendar.YEAR, year);
            calendario.set(Calendar.MONTH, month);
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarEditTextFecha();
        };

        new DatePickerDialog(this, dateSetListener,
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void actualizarEditTextFecha() {
        String formato = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
        editFechaEmision.setText(sdf.format(calendario.getTime()));
    }

    private void guardarFactura() {
        int pedidoPos = spinnerPedido.getSelectedItemPosition();
        if (pedidoPos <= 0) { // Posición 0 es el placeholder
            Toast.makeText(this, "Seleccione un pedido válido.", Toast.LENGTH_SHORT).show();
            return;
        }
        int idPedidoSeleccionado = pedidoIds.get(pedidoPos);

        String fechaEmision = editFechaEmision.getText().toString().trim();
        if (fechaEmision.isEmpty()) {
            Toast.makeText(this, "Ingrese la fecha de emisión.", Toast.LENGTH_SHORT).show();
            return;
        }

        String montoStr = editMontoTotal.getText().toString().trim();
        double montoTotal;
        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Ingrese el monto total.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            montoTotal = Double.parseDouble(montoStr);
            if (montoTotal <= 0) { // El trigger de BD también valida esto, pero es bueno validar en UI
                Toast.makeText(this, "El monto total debe ser mayor a cero.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese un monto total válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        int tipoPagoPos = spinnerTipoPago.getSelectedItemPosition();
        if (tipoPagoPos <= 0) { // Posición 0 es el placeholder
            Toast.makeText(this, "Seleccione un tipo de pago.", Toast.LENGTH_SHORT).show();
            return;
        }
        String tipoPagoSeleccionado = tiposPago.get(tipoPagoPos - 1); // -1 por el placeholder

        int pagado = checkPagado.isChecked() ? 1 : 0;

        Factura nuevaFactura = new Factura();
        nuevaFactura.setIdPedido(idPedidoSeleccionado); // Crucial para la relación 1:1
        // ID_FACTURA es AUTOINCREMENT, no se setea aquí.
        nuevaFactura.setFechaEmision(fechaEmision);
        nuevaFactura.setMontoTotal(montoTotal);
        nuevaFactura.setTipoPago(tipoPagoSeleccionado);
        nuevaFactura.setPagado(pagado);

        // Insertar usando ViewModel. El ViewModel ahora devuelve el ID de la nueva factura.
        long nuevoIdFactura = facturaViewModel.insertarFactura(nuevaFactura);

        if (nuevoIdFactura != -1) {
            Toast.makeText(this, "Factura creada con ID: " + nuevoIdFactura + " para el pedido " + idPedidoSeleccionado, Toast.LENGTH_LONG).show();
            setResult(Activity.RESULT_OK); // Notificar éxito si esta actividad fue llamada con startActivityForResult
            finish(); // Cerrar la actividad si fue exitoso
        } else {
            Toast.makeText(this, "Error al crear la factura. Es posible que el pedido ya tenga una factura asociada o haya otro error.", Toast.LENGTH_LONG).show();
            // La restricción UNIQUE en FACTURA.ID_PEDIDO previene duplicados.
        }
    }
}