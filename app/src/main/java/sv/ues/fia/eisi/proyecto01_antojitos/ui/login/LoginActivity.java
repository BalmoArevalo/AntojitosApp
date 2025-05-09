package sv.ues.fia.eisi.proyecto01_antojitos.ui.login;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import sv.ues.fia.eisi.proyecto01_antojitos.MainActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.data.AuthRepository;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.ActivityLoginBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.db.seeders.SeguridadSeeder;

/**
 * Login minimal: valida contra la tabla USUARIO
 * y usa AuthRepository para guardar sesión (SharedPrefs).
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* 1) ¿Ya hay sesión? → directo al Main */
        if (new AuthRepository(this).isLoggedIn()) {
            goToMain();
            return;
        }

        /* 2) UI */
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnEntrar.setOnClickListener(v -> attemptLogin());
        binding.btnCrearUsuarios.setOnClickListener(v -> poblarSeguridad());
    }

    /* --------------------------------------------------------------------- */
    /*  LOGIN                                                                */
    /* --------------------------------------------------------------------- */
    private void attemptLogin() {
        String usuario = binding.etUsuario.getText().toString().trim();
        String clave   = binding.etClave.getText().toString().trim();

        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(clave)) {
            Toast.makeText(this, "Ingresa usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthRepository auth = new AuthRepository(this);
        if (auth.login(usuario, clave)) {
            goToMain();
        } else {
            Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    /* --------------------------------------------------------------------- */
    /*  CREAR USUARIOS Y PERMISOS                                            */
    /* --------------------------------------------------------------------- */
    private void poblarSeguridad() {
        // Borra el archivo: obliga a ejecutar onCreate() desde cero
        deleteDatabase(DBHelper.DB_NAME);

        // Se recrean TODAS las tablas y luego poblas seguridad
        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        SeguridadSeeder.poblar(db);

        Toast.makeText(this,
                "Base recreada y usuarios/permisos insertados", Toast.LENGTH_LONG).show();
    }
}
