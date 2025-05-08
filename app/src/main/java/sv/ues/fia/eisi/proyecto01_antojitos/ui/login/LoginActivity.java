package sv.ues.fia.eisi.proyecto01_antojitos.ui.login;

import android.content.Intent;
import android.database.Cursor;
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

/**
 * Login ultra–minimal: valida usuario/clave en la tabla USUARIO,
 * guarda el usuario en memoria (static) y lanza el MainActivity.
 *
 * Futuro-proof:
 *  • Cambia la consulta SQL por un DAO si migras a Room.
 *  • Sustituye el campo estático por SessionManager para
 *    persistir la sesión en SharedPreferences.
 */
public class LoginActivity extends AppCompatActivity {

    /** Usuario actualmente logueado (memoria RAM, no persiste tras cerrar app). */
    public static @Nullable String currentUser = null;

    private ActivityLoginBinding binding;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Si ya hay sesión, salta el login
        if (currentUser != null) {
            goToMain();
            return;
        }

        binding  = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);

        binding.btnEntrar.setOnClickListener(v -> attemptLogin());
        binding.btnCrearBD.setOnClickListener(v -> recrearYpoblarBD());
    }

    private void attemptLogin() {
        String usuario = binding.etUsuario.getText().toString().trim();
        String clave   = binding.etClave.getText().toString().trim();

        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(clave)) {
            Toast.makeText(this, "Ingresa usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthRepository auth = new AuthRepository(this);
        if (auth.login(usuario, clave)) {          // ← guarda sesión en SharedPrefs
            goToMain();
        } else {
            Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
        }
    }

    /** Consulta directa a la BD: existe registro con ese usuario + clave */
    private boolean loginOK(String user, String pass) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM USUARIO WHERE NOM_USUARIO = ? AND CLAVE = ?",
                new String[]{user, pass}
        );
        boolean ok = c.moveToFirst();
        c.close();
        return ok;
    }

    /** Limpia el back-stack y abre el MainActivity. */
    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        // No llamamos finish(); el flag CLEAR_TASK ya elimina Login del stack.
    }

    private void recrearYpoblarBD() {
        // 1) Elimina la BD para forzar un onCreate limpio
        deleteDatabase(DBHelper.DB_NAME);

        // 2) Vuelve a crearla (se ejecutará DBHelper.onCreate → Seeder)
        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();

        // 3) Mensaje de éxito
        Toast.makeText(this, "Base de datos recreada y poblada", Toast.LENGTH_SHORT).show();
    }
}
