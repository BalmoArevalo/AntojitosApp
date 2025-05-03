package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DireccionEliminarActivity extends AppCompatActivity {

    private Spinner spinnerCliente;
    private Spinner spinnerDireccion;
    private Button btnEliminar;
    private DBHelper dbHelper;

    private List<Integer> clienteIds = new ArrayList<>();
    private List<Integer> direccionIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_eliminar);

        dbHelper = new DBHelper(this);
        spinnerCliente = findViewById(R.id.spinnerCliente);
        spinnerDireccion = findViewById(R.id.spinnerDireccion);
        btnEliminar = findViewById(R.id.btnEliminarDireccion);

        cargarSpinnerClientes();

        spinnerCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int idCli = clienteIds.get(pos);
                cargarSpinnerDirecciones(idCli);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int posCli = spinnerCliente.getSelectedItemPosition();
                int idCli = clienteIds.get(posCli);
                int posDir = spinnerDireccion.getSelectedItemPosition();
                int idDir = direccionIds.get(posDir);
                if (idCli < 0 || idDir < 0) {
                    Toast.makeText(DireccionEliminarActivity.this,
                            "Selecciona cliente y dirección válidos", Toast.LENGTH_SHORT).show();
                    return;
                }
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                DireccionDAO dao = new DireccionDAO(db);
                int rows = dao.eliminar(idCli, idDir);
                db.close();
                if (rows > 0) {
                    Toast.makeText(DireccionEliminarActivity.this,
                            "Dirección eliminada", Toast.LENGTH_SHORT).show();
                    cargarSpinnerDirecciones(idCli);
                } else {
                    Toast.makeText(DireccionEliminarActivity.this,
                            "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cargarSpinnerClientes() {
        clienteIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione..."); clienteIds.add(-1);

        SQLiteDatabase db = null; Cursor c = null;
        try {
            db = dbHelper.getReadableDatabase();
            c = db.rawQuery(
                    "SELECT ID_CLIENTE, NOMBRE_CLIENTE || ' ' || APELLIDO_CLIIENTE FROM CLIENTE",
                    null);
            while (c.moveToNext()) {
                clienteIds.add(c.getInt(0));
                nombres.add(c.getString(1));
            }
        } catch (SQLiteException ex) {
            nombres.add("Sin datos"); clienteIds.add(-1);
        } finally {
            if (c!=null) c.close();
            if (db!=null) db.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCliente.setAdapter(adapter);
    }

    private void cargarSpinnerDirecciones(int idCliente) {
        direccionIds.clear();
        List<String> items = new ArrayList<>();
        items.add("Seleccione..."); direccionIds.add(-1);
        if (idCliente >= 0) {
            SQLiteDatabase db = null; Cursor c = null;
            try {
                db = dbHelper.getReadableDatabase();
                c = db.rawQuery(
                        "SELECT ID_DIRECCION, DIRECCION_ESPECIFICA FROM DIRECCION WHERE ID_CLIENTE = ?",
                        new String[]{ String.valueOf(idCliente) }
                );
                while (c.moveToNext()) {
                    direccionIds.add(c.getInt(0));
                    items.add(c.getString(1));
                }
            } catch (SQLiteException ex) {
                items.add("Sin direcciones"); direccionIds.add(-1);
            } finally {
                if (c!=null) c.close();
                if (db!=null) db.close();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDireccion.setAdapter(adapter);
    }
}
