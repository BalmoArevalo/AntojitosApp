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

    private EditText etDireccionEspecifica, etDescripcionDireccion;
    private Spinner spinnerCliente, spinnerDepartamento, spinnerMunicipio, spinnerDistrito;
    private Button btnGuardar;

    // Listas paralelas para convertir posición de Spinner → ID real
    private List<Integer> clienteIds      = new ArrayList<>();
    private List<Integer> departamentoIds = new ArrayList<>();
    private List<Integer> municipioIds    = new ArrayList<>();
    private List<Integer> distritoIds     = new ArrayList<>();

    private DBHelper dbHelper;
    private boolean toastEjemploMostrado = false;  // Evita repetir el Toast de ejemplo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_crear);

        dbHelper = new DBHelper(this);

        etDireccionEspecifica = findViewById(R.id.editDireccionEspecifica);
        etDescripcionDireccion = findViewById(R.id.editDescripcionDireccion);

        spinnerCliente      = findViewById(R.id.spinnerCliente);
        spinnerDepartamento = findViewById(R.id.spinnerDepartamento);
        spinnerMunicipio    = findViewById(R.id.spinnerMunicipio);
        spinnerDistrito     = findViewById(R.id.spinnerDistrito);

        btnGuardar          = findViewById(R.id.btnGuardar);

        // Poblar cada Spinner, o bien con datos reales o de ejemplo
        cargarDatosSpinner(spinnerCliente,     "CLIENTE",     "ID_CLIENTE",     "NOMBRE_CLIENTE || ' ' || APELLIDO_CLIIENTE", clienteIds);
        cargarDatosSpinner(spinnerDepartamento,"DEPARTAMENTO","ID_DEPARTAMENTO","NOMBRE_DEPARTAMENTO", departamentoIds);
        cargarDatosSpinner(spinnerMunicipio,   "MUNICIPIO",   "ID_MUNICIPIO",   "NOMBRE_MUNICIPIO", municipioIds);
        cargarDatosSpinner(spinnerDistrito,    "DISTRITO",    "ID_DISTRITO",    "NOMBRE_DISTRITO", distritoIds);

        btnGuardar.setOnClickListener(v -> guardarDireccion());
    }

    /**
     * Llena un Spinner con valores de la tabla dada.
     * Si la tabla no existe, muestra un Toast informativo
     * y rellena el Spinner con datos de ejemplo (IDs + nombres).
     */
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
                    "SELECT " + campoId + ", " + campoNom + " FROM " + tabla,
                    null
            );

            if (cursor.moveToFirst()) {
                do {
                    idList.add(cursor.getInt(0));
                    nombres.add(cursor.getString(1));
                } while (cursor.moveToNext());
            } else {
                // Tabla existe pero sin datos
                nombres.add("No existen datos");
                idList.add(-1);
            }

        } catch (SQLiteException ex) {
            // La tabla no existe: uso de datos de ejemplo
            if (!toastEjemploMostrado) {
                Toast.makeText(this,
                        "Base de datos aún no creada, funcionando con datos de ejemplo",
                        Toast.LENGTH_LONG).show();
                toastEjemploMostrado = true;
            }
            // Ejemplos reales de El Salvador / nombres indicativos
            if (spinner.getId() == R.id.spinnerCliente) {
                nombres.add("Juan Pérez");
                nombres.add("María López");
                idList.add(1);
                idList.add(2);
            } else if (spinner.getId() == R.id.spinnerDepartamento) {
                nombres.add("San Salvador");
                nombres.add("La Libertad");
                nombres.add("Santa Ana");
                idList.add(1);
                idList.add(2);
                idList.add(3);
            } else if (spinner.getId() == R.id.spinnerMunicipio) {
                nombres.add("San Salvador");
                nombres.add("Santa Tecla");
                nombres.add("Sonsonate");
                idList.add(1);
                idList.add(2);
                idList.add(3);
            } else { // Distrito
                nombres.add("Centro Histórico");
                nombres.add("Colonia Escalón");
                nombres.add("Santa Elena");
                idList.add(1);
                idList.add(2);
                idList.add(3);
            }
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nombres
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Lee los valores de la UI y los muestra en un Toast
     * en lugar de insertarlos en la BD (aún no existe).
     */
    private void guardarDireccion() {
        String dirEsp = etDireccionEspecifica.getText().toString().trim();
        String desc   = etDescripcionDireccion.getText().toString().trim();

        int posCli   = spinnerCliente.getSelectedItemPosition();
        int posDep   = spinnerDepartamento.getSelectedItemPosition();
        int posMun   = spinnerMunicipio.getSelectedItemPosition();
        int posDist  = spinnerDistrito.getSelectedItemPosition();

        int idCli   = clienteIds.size()      > posCli  ? clienteIds.get(posCli)      : -1;
        int idDep   = departamentoIds.size() > posDep  ? departamentoIds.get(posDep) : -1;
        int idMun   = municipioIds.size()    > posMun  ? municipioIds.get(posMun)    : -1;
        int idDist  = distritoIds.size()     > posDist ? distritoIds.get(posDist)    : -1;

        if (dirEsp.isEmpty()) {
            Toast.makeText(this, "La dirección específica es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }

        String mensaje = "ClienteID=" + idCli +
                "\nDeptoID=" + idDep +
                "\nMunID=" + idMun +
                "\nDistID=" + idDist +
                "\nDir: " + dirEsp +
                "\nDesc: " + desc;

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}
