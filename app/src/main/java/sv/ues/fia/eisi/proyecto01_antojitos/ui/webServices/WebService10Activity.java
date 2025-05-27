package sv.ues.fia.eisi.proyecto01_antojitos.ui.webServices;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.network.helpers.RepartidorHelper;

public class WebService10Activity extends AppCompatActivity {

    EditText etIdRepartidor;
    Button btnEliminarRepartidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service10);

        etIdRepartidor = findViewById(R.id.etIdRepartidorEliminar);
        btnEliminarRepartidor = findViewById(R.id.btnEliminarRepartidor);

        btnEliminarRepartidor.setOnClickListener(v -> {
            String id = etIdRepartidor.getText().toString().trim();

            if (id.isEmpty()) {
                Toast.makeText(this, "Debe ingresar el ID del repartidor", Toast.LENGTH_SHORT).show();
                return;
            }

            int idRepartidor;
            try {
                idRepartidor = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            RepartidorHelper.eliminarRepartidor(this, idRepartidor);
        });
    }
}
