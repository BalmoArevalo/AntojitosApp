package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider; // Usaremos ViewModel para insertar

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log; // Para logs de depuración
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays; // Para lista de tipos de pago
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper; // Necesario para cargar pedidos

public class FacturaCrearActivity extends AppCompatActivity {

    // ViewModel para operaciones de datos
    private FacturaViewModel facturaViewModel;

    // Componentes UI
    private Spinner spinnerPedido;
    private EditText editFechaEmision;
    private EditText editMontoTotal;
    private Spinner spinnerTipoPago;
    private CheckBox checkPagado;
    private Button btnGuardarFactura;

    // Listas para IDs de Spinners
    private List<Integer> pedidoIds = new ArrayList<>();
    private List<String> tiposPago = Arrays.asList("Efectivo", "Tarjeta", "Bitcoin", "Transferencia", "Otro"); // Opciones predefinidas

    // Calendario para DatePicker
    private Calendar calendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura_crear); // Asegúrate de crear este layout

        // Inicializar ViewModel
        facturaViewModel = new ViewModelProvider(this).get(FacturaViewModel.class);

        // Inicializar Vistas
        spinnerPedido = findViewById(R.id.spinnerPedido);
        editFechaEmision = findViewById(R.id.editFechaEmision);
        editMontoTotal = findViewById(R.id.editMontoTotal);
        spinnerTipoPago = findViewById(R.id.spinnerTipoPago);
        checkPagado = findViewById(R.id.checkPagado);
        btnGuardarFactura = findViewById(R.id.btnGuardarFactura);

        // Cargar datos en Spinners
        cargarSpinnerPedidos();
        cargarSpinnerTipoPago();

        // Configurar DatePicker para Fecha de Emisión
        editFechaEmision.setOnClickListener(v -> mostrarDatePickerDialog());

        // Configurar botón Guardar
        btnGuardarFactura.setOnClickListener(v -> guardarFactura());
    }

    private void cargarSpinnerPedidos() {
        pedidoIds.clear();
        List<String> pedidoDescripciones = new ArrayList<>();

        // Añadir opción por defecto
        pedidoDescripciones.add("Seleccione un Pedido...");
        pedidoIds.add(-1); // ID inválido para la opción por defecto

        DBHelper dbHelper = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getReadableDatabase();
            // Consulta simple para obtener IDs de pedido. Podrías añadir más info si quieres.
            cursor = db.rawQuery("SELECT ID_PEDIDO FROM PEDIDO ORDER BY ID_PEDIDO ASC", null);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                pedidoIds.add(id);
                // Crear una descripción simple
                pedidoDescripciones.add("Pedido #" + id);
            }
        } catch (SQLiteException e) {
            Log.e("FacturaCrear", "Error al cargar pedidos", e);
            Toast.makeText(this, "Error al cargar pedidos", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
            // DBHelper no necesita cerrarse explícitamente aquí
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, pedidoDescripciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPedido.setAdapter(adapter);
    }

    private void cargarSpinnerTipoPago() {
        // Añadir opción por defecto si se desea
        List<String> opcionesConPlaceholder = new ArrayList<>();
        opcionesConPlaceholder.add("Seleccione Tipo de Pago...");
        opcionesConPlaceholder.addAll(tiposPago);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opcionesConPlaceholder); // Usar la lista con placeholder
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

        new DatePickerDialog(FacturaCrearActivity.this, dateSetListener,
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void actualizarEditTextFecha() {
        // Formato deseado: YYYY-MM-DD (común para SQLite)
        String formato = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
        editFechaEmision.setText(sdf.format(calendario.getTime()));
    }


    private void guardarFactura() {
        // 1. Obtener datos y Validar
        int pedidoPos = spinnerPedido.getSelectedItemPosition();
        if (pedidoPos <= 0 || pedidoIds.get(pedidoPos) == -1) { // Posición 0 es el placeholder
            Toast.makeText(this, "Seleccione un pedido válido", Toast.LENGTH_SHORT).show();
            return;
        }
        int idPedidoSeleccionado = pedidoIds.get(pedidoPos);

        String fechaEmision = editFechaEmision.getText().toString().trim();
        if (fechaEmision.isEmpty()) {
            Toast.makeText(this, "Ingrese la fecha de emisión", Toast.LENGTH_SHORT).show();
            return;
        }

        String montoStr = editMontoTotal.getText().toString().trim();
        double montoTotal;
        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Ingrese el monto total", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            montoTotal = Double.parseDouble(montoStr);
            if (montoTotal < 0) {
                Toast.makeText(this, "El monto total no puede ser negativo", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese un monto total válido", Toast.LENGTH_SHORT).show();
            return;
        }

        int tipoPagoPos = spinnerTipoPago.getSelectedItemPosition();
        // Si el primer item es placeholder "Seleccione...", la posición 0 es inválida
        if (tipoPagoPos <= 0) {
            Toast.makeText(this, "Seleccione un tipo de pago", Toast.LENGTH_SHORT).show();
            return;
        }
        // Ajustar índice si se usó placeholder: tiposPago.get(tipoPagoPos - 1)
        String tipoPagoSeleccionado = tiposPago.get(tipoPagoPos - 1);


        int pagado = checkPagado.isChecked() ? 1 : 0; // 1 si está marcado, 0 si no

        // 2. Crear objeto Factura (ID_FACTURA se asignará en el ViewModel/DAO)
        Factura nuevaFactura = new Factura();
        nuevaFactura.setIdPedido(idPedidoSeleccionado);
        // El ID_FACTURA real lo calculará getNextIdFactura dentro de insertarFactura del ViewModel
        nuevaFactura.setFechaEmision(fechaEmision);
        nuevaFactura.setMontoTotal(montoTotal);
        nuevaFactura.setTipoPago(tipoPagoSeleccionado);
        nuevaFactura.setPagado(pagado);


        // 3. Insertar usando ViewModel
        boolean exito = facturaViewModel.insertarFactura(nuevaFactura);

        // 4. Mostrar Feedback y Cerrar
        if (exito) {
            // Recuperar el ID asignado sería ideal, pero requiere modificar DAO/ViewModel.
            // Mostramos un mensaje genérico por ahora.
            Toast.makeText(this, "Factura creada exitosamente para el pedido " + idPedidoSeleccionado, Toast.LENGTH_LONG).show();
            finish(); // Cerrar la actividad si fue exitoso
        } else {
            Toast.makeText(this, "Error al crear la factura", Toast.LENGTH_LONG).show();
        }
    }
}