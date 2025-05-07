package sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente;

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
import sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente.Cliente;

public class ClienteEditarActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerCliente;
    private TextInputEditText txtNombresCliente;
    private TextInputEditText txtApellidosCliente;
    private TextInputEditText txtTelefonoCliente;
    private SwitchMaterial switchEstadoCliente;
    private MaterialButton btnGuardarCambios;

    private Map<String, Cliente> clientesMap;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_editar);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        inicializarVistas();
        cargarClientes();
    }

    private void inicializarVistas() {
        spinnerCliente = findViewById(R.id.spinnerCliente);
        txtNombresCliente = findViewById(R.id.txtNombresCliente);
        txtApellidosCliente = findViewById(R.id.txtApellidosCliente);
        txtTelefonoCliente = findViewById(R.id.txtTelefonoCliente);
        switchEstadoCliente = findViewById(R.id.switchEstadoCliente);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        clientesMap = new HashMap<>();

        spinnerCliente.setOnItemClickListener((parent, view, position, id) -> {
            String seleccion = parent.getItemAtPosition(position).toString();
            Cliente cliente = clientesMap.get(seleccion);
            if (cliente != null) {
                cargarDatosCliente(cliente);
            }
        });

        btnGuardarCambios.setOnClickListener(v -> validarYGuardarCambios());
    }

    private void cargarClientes() {
        List<String> nombresClientes = new ArrayList<>();

        String sql = "SELECT ID_CLIENTE, NOMBRE_CLIENTE, APELLIDO_CLIENTE, " +
                "TELEFONO_CLIENTE, ACTIVO_CLIENTE " +
                "FROM CLIENTE ORDER BY NOMBRE_CLIENTE, APELLIDO_CLIENTE";

        try (Cursor cursor = db.rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                Cliente cliente = new Cliente();
                cliente.setIdCliente(cursor.getInt(cursor.getColumnIndexOrThrow("ID_CLIENTE")));
                cliente.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE_CLIENTE")));
                cliente.setApellidoCliente(cursor.getString(cursor.getColumnIndexOrThrow("APELLIDO_CLIENTE")));
                cliente.setTelefonoCliente(cursor.getString(cursor.getColumnIndexOrThrow("TELEFONO_CLIENTE")));
                cliente.setActivoCliente(cursor.getInt(cursor.getColumnIndexOrThrow("ACTIVO_CLIENTE")));

                // Crear nombre completo con indicador de estado
                String nombreCompleto = cliente.getNombreCliente() + " " +
                        cliente.getApellidoCliente() +
                        (cliente.getActivoCliente() == 1 ? " ✓" : " ❌");

                nombresClientes.add(nombreCompleto);
                clientesMap.put(nombreCompleto, cliente);
            }

            if (nombresClientes.isEmpty()) {
                Toast.makeText(this, "No hay clientes disponibles", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    nombresClientes
            );

            spinnerCliente.setAdapter(adapter);

        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar los clientes: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void cargarDatosCliente(Cliente cliente) {
        txtNombresCliente.setText(cliente.getNombreCliente());
        txtApellidosCliente.setText(cliente.getApellidoCliente());
        txtTelefonoCliente.setText(cliente.getTelefonoCliente());
        switchEstadoCliente.setChecked(cliente.getActivoCliente() == 1);
    }

    private void validarYGuardarCambios() {
        String clienteSeleccionado = spinnerCliente.getText().toString();
        String nombres = txtNombresCliente.getText().toString().trim();
        String apellidos = txtApellidosCliente.getText().toString().trim();
        String telefono = txtTelefonoCliente.getText().toString().trim();

        if (!clientesMap.containsKey(clienteSeleccionado)) {
            Toast.makeText(this, "Debe seleccionar un cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nombres)) {
            Toast.makeText(this, "Debe ingresar los nombres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(apellidos)) {
            Toast.makeText(this, "Debe ingresar los apellidos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(telefono)) {
            Toast.makeText(this, "Debe ingresar el teléfono", Toast.LENGTH_SHORT).show();
            return;
        }

        Cliente cliente = clientesMap.get(clienteSeleccionado);
        if (cliente != null) {
            actualizarCliente(cliente.getIdCliente(), nombres, apellidos, telefono,
                    switchEstadoCliente.isChecked() ? 1 : 0);
        }
    }

    private void actualizarCliente(int idCliente, String nombres, String apellidos,
                                   String telefono, int activo) {
        try {
            String sql = "UPDATE CLIENTE SET NOMBRE_CLIENTE = ?, " +
                    "APELLIDO_CLIENTE = ?, TELEFONO_CLIENTE = ?, " +
                    "ACTIVO_CLIENTE = ? WHERE ID_CLIENTE = ?";

            Object[] args = {nombres, apellidos, telefono, activo, idCliente};

            db.execSQL(sql, args);

            Toast.makeText(this, "Cliente actualizado exitosamente", Toast.LENGTH_SHORT).show();

            // Recargar la lista de clientes y limpiar campos
            cargarClientes();
            limpiarCampos();

        } catch (Exception e) {
            Toast.makeText(this, "Error al actualizar el cliente: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        spinnerCliente.setText("", false);
        txtNombresCliente.setText("");
        txtApellidosCliente.setText("");
        txtTelefonoCliente.setText("");
        switchEstadoCliente.setChecked(true); // Por defecto activo
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}