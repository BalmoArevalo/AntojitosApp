package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DireccionEliminarActivity extends AppCompatActivity {

    private TextView tvDirEsp, tvDesc;
    private Spinner spinnerCliente, spinnerDepartamento, spinnerMunicipio, spinnerDistrito;
    private Button btnEliminar;

    private List<Integer> clienteIds      = new ArrayList<>();
    private List<Integer> departamentoIds = new ArrayList<>();
    private List<Integer> municipioIds    = new ArrayList<>();
    private List<Integer> distritoIds     = new ArrayList<>();

    private DBHelper dbHelper;
    private boolean toastEjemploMostrado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_eliminar);

        dbHelper       = new DBHelper(this);
        tvDirEsp       = findViewById(R.id.tvDirEsp);
        tvDesc         = findViewById(R.id.tvDesc);
        spinnerCliente      = findViewById(R.id.spinnerCliente);
        spinnerDepartamento = findViewById(R.id.spinnerDepartamento);
        spinnerMunicipio    = findViewById(R.id.spinnerMunicipio);
        spinnerDistrito     = findViewById(R.id.spinnerDistrito);
        btnEliminar    = findViewById(R.id.btnEliminar);

        // Poblar spinners (reales o de ejemplo)
        cargarDatosSpinner(spinnerCliente,     "CLIENTE",     "ID_CLIENTE",     "NOMBRE_CLIENTE || ' ' || APELLIDO_CLIIENTE", clienteIds);
        cargarDatosSpinner(spinnerDepartamento,"DEPARTAMENTO","ID_DEPARTAMENTO","NOMBRE_DEPARTAMENTO", departamentoIds);
        cargarDatosSpinner(spinnerMunicipio,   "MUNICIPIO",   "ID_MUNICIPIO",   "NOMBRE_MUNICIPIO", municipioIds);
        cargarDatosSpinner(spinnerDistrito,    "DISTRITO",    "ID_DISTRITO",    "NOMBRE_DISTRITO", distritoIds);

        // Deshabilitamos inputs para sólo mostrar
        tvDirEsp.setEnabled(false);
        tvDesc.setEnabled(false);
        spinnerCliente.setEnabled(false);
        spinnerDepartamento.setEnabled(false);
        spinnerMunicipio.setEnabled(false);
        spinnerDistrito.setEnabled(false);

        // Cargar valores de ejemplo
        tvDirEsp.setText("Col. Los Próceres, Calle 5 #123");
        tvDesc.setText("Frente a la escuela");

        spinnerCliente.setSelection(1);
        spinnerDepartamento.setSelection(0);
        spinnerMunicipio.setSelection(2);
        spinnerDistrito.setSelection(1);

        btnEliminar.setOnClickListener(v -> {
            // Aquí podrías llamar a dao.eliminar(idCliente, idDireccion)
            Toast.makeText(this,
                    "Dirección eliminada correctamente (ejemplo)",
                    Toast.LENGTH_SHORT).show();
            finish();
        });
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
                    "SELECT " + campoId + ", " + campoNom + " FROM " + tabla, null);

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
            if (!toastEjemploMostrado) {
                Toast.makeText(this,
                        "Base de datos aún no creada, funcionando con datos de ejemplo",
                        Toast.LENGTH_LONG).show();
                toastEjemploMostrado = true;
            }
            // Ejemplos
            if (spinner.getId() == R.id.spinnerCliente) {
                nombres.add("Juan Pérez");
                nombres.add("María López");
                nombres.add("Carlos Martínez");
                idList.add(1);
                idList.add(2);
                idList.add(3);
            } else if (spinner.getId() == R.id.spinnerDepartamento) {
                nombres.add("San Salvador");
                nombres.add("La Libertad");
                nombres.add("Santa Ana");
                idList.add(1);
                idList.add(2);
                idList.add(3);
            } else if (spinner.getId() == R.id.spinnerMunicipio) {
                nombres.add("San Miguel");
                nombres.add("Santa Tecla");
                nombres.add("Sonsonate");
                idList.add(1);
                idList.add(2);
                idList.add(3);
            } else {
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
}
