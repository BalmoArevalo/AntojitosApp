package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class SucursalEditarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sucursal_editar);

        Toast.makeText(this, "Activity cargada", Toast.LENGTH_SHORT).show();
    }
}