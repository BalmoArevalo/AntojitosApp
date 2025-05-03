package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class DireccionConsultarActivity extends AppCompatActivity {

    private Spinner spinnerCliente;
    private Button btnCargar;
    private TextView tvResultado;
    private DBHelper dbHelper;
    private List<Integer> clienteIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_consultar);

        dbHelper       = new DBHelper(this);
        spinnerCliente = findViewById(R.id.spinnerCliente);
        btnCargar      = findViewById(R.id.btnCargarDirecciones);
        tvResultado    = findViewById(R.id.tvResultado);

        cargarSpinnerClientes();

        btnCargar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mostrarDirecciones();
            }
        });
    }

    private void cargarSpinnerClientes() {
        clienteIds.clear();
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione..."); clienteIds.add(-1);

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbHelper.getReadableDatabase();
            c = db.rawQuery(
                    "SELECT ID_CLIENTE, NOMBRE_CLIENTE || ' ' || APELLIDO_CLIIENTE FROM CLIENTE",
                    null
            );
            while (c.moveToNext()) {
                clienteIds.add(c.getInt(0));
                nombres.add(c.getString(1));
            }
        } catch (SQLiteException ex) {
            nombres.add("Sin datos"); clienteIds.add(-1);
        } finally {
            if (c != null) c.close();
            if (db!= null) db.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCliente.setAdapter(adapter);
    }

    private void mostrarDirecciones() {
        int pos = spinnerCliente.getSelectedItemPosition();
        int idCli = clienteIds.get(pos);
        if (idCli < 0) {
            Toast.makeText(this, "Selecciona un cliente válido", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        DireccionDAO dao = new DireccionDAO(db);
        List<Direccion> lista = dao.obtenerPorCliente(idCli);
        db.close();

        if (lista.isEmpty()) {
            tvResultado.setText("Este cliente no tiene direcciones registradas.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Direccion d : lista) {
                sb.append("ID_Dir: ").append(d.getIdDireccion())
                        .append(" → ").append(d.getDireccionEspecifica());
                if (d.getDescripcionDireccion() != null && !d.getDescripcionDireccion().isEmpty()) {
                    sb.append(" (").append(d.getDescripcionDireccion()).append(")");
                }
                sb.append("\n\n");
            }
            tvResultado.setText(sb.toString().trim());
        }
    }
}
