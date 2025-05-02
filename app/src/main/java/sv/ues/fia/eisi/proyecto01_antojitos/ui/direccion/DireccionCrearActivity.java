package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DireccionCrearActivity extends AppCompatActivity {

    private EditText etCalle, etNumero;
    private Spinner spinnerCliente, spinnerDepartamento, spinnerMunicipio, spinnerDistrito;
    private Button btnGuardar;

    // Listas paralelas para convertir posición de Spinner → ID real
    private List<Integer> clienteIds      = new ArrayList<>();
    private List<Integer> departamentoIds = new ArrayList<>();
    private List<Integer> municipioIds    = new ArrayList<>();
    private List<Integer> distritoIds     = new ArrayList<>();

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_crear);

        dbHelper = new DBHelper(this);

        etCalle      = findViewById(R.id.etCalle);
        etNumero     = findViewById(R.id.etNumero);
        spinnerCliente      = findViewById(R.id.spinnerCliente);
        spinnerDepartamento = findViewById(R.id.spinnerDepartamento);
        spinnerMunicipio    = findViewById(R.id.spinnerMunicipio);
        spinnerDistrito     = findViewById(R.id.spinnerDistrito);
        btnGuardar   = findViewById(R.id.btnGuardar);

        // Carga inicial de datos en cada Spinner
        cargarDatosSpinner(spinnerCliente,     "CLIENTE",      "ID_CLIENTE",     "NOMBRE_CLIENTE || ' ' || APELLIDO_CLIIENTE", clienteIds);
        cargarDatosSpinner(spinnerDepartamento,"DEPARTAMENTO", "ID_DEPARTAMENTO","NOMBRE_DEPARTAMENTO", departamentoIds);
        cargarDatosSpinner(spinnerMunicipio,   "MUNICIPIO",    "ID_MUNICIPIO",   "NOMBRE_MUNICIPIO", municipioIds);
        cargarDatosSpinner(spinnerDistrito,    "DISTRITO",     "ID_DISTRITO",    "NOMBRE_DISTRITO", distritoIds);

        btnGuardar.setOnClickListener(v -> guardarDireccion());
    }

    private void cargarDatosSpinner(Spinner spinner,
                                    String tabla,
                                    String campoId,
                                    String campoNom,
                                    List<Integer> idList) {
        idList.clear();
        List<String> nombres = new ArrayList<>();

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT " + campoId + ", " + campoNom +
                            " FROM " + tabla, null);

            if (cursor.moveToFirst()) {
                do {
                    idList.add(cursor.getInt(0));
                    nombres.add(cursor.getString(1));
                } while (cursor.moveToNext());
            } else {
                nombres.add("No existen datos");
                idList.add(-1);
            }

        } catch (SQLiteException ex) {
            // Tabla no existe aún: datos de ejemplo
            nombres.clear();
            idList.clear();
            nombres.add("Ejemplo A");
            nombres.add("Ejemplo B");
            idList.add(1);
            idList.add(2);
        } finally {
            if (cursor != null) cursor.close();
            if (db     != null) db.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nombres
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void guardarDireccion() {
        String calle  = etCalle.getText().toString().trim();
        String numero = etNumero.getText().toString().trim();

        int idCliente      = clienteIds.size() > spinnerCliente.getSelectedItemPosition()
                ? clienteIds.get(spinnerCliente.getSelectedItemPosition()) : -1;
        int idDepartamento = departamentoIds.size() > spinnerDepartamento.getSelectedItemPosition()
                ? departamentoIds.get(spinnerDepartamento.getSelectedItemPosition()) : -1;
        int idMunicipio    = municipioIds.size() > spinnerMunicipio.getSelectedItemPosition()
                ? municipioIds.get(spinnerMunicipio.getSelectedItemPosition()) : -1;
        int idDistrito     = distritoIds.size() > spinnerDistrito.getSelectedItemPosition()
                ? distritoIds.get(spinnerDistrito.getSelectedItemPosition()) : -1;

        if (calle.isEmpty() || numero.isEmpty()) {
            Toast.makeText(this, "Calle y Número son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        String direccionEspecifica = calle + " #" + numero;
        String msg = "ClienteID=" + idCliente +
                "\nDeptoID=" + idDepartamento +
                "\nMunID=" + idMunicipio +
                "\nDistID=" + idDistrito +
                "\nDirEsp=" + direccionEspecifica;
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
