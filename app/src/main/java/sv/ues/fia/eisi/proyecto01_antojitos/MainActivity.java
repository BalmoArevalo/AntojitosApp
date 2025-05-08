package sv.ues.fia.eisi.proyecto01_antojitos;

import android.content.Intent;          // ↖︎ Necesario para los Intents
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;           // ↖︎ Para manejar options menu clicks
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import sv.ues.fia.eisi.proyecto01_antojitos.databinding.ActivityMainBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.login.LoginActivity;  // ↖︎ Para saber quién está logueado

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1) Forzar login si no hay usuario en sesión
        if (LoginActivity.currentUser == null) {
            Intent i = new Intent(this, LoginActivity.class);
            // Limpia back stack para evitar regresar con “Atrás”
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }

        // 2) Enlazar con layout usando ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar la Toolbar
        setSupportActionBar(binding.appBarMain.toolbar);

        // Botón flotante
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Acción aún no definida", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });

        // Drawer + NavController
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_cliente,
                /* … todos tus destinos … */
                R.id.nav_datos_producto
        ).setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // 3) (Opcional) Ocultar/mostrar items del drawer según usuario
        // Menu navMenu = navigationView.getMenu();
        // String u = LoginActivity.currentUser;
        // if (!u.equals("SU")) {
        //     navMenu.findItem(R.id.nav_producto).setVisible(u.equals("CL")); // p.ej.
        // }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar tu menú que ahora incluye <item android:id="@+id/action_logout" …/>
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // 4) Manejar clic en “Cerrar sesión”
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Limpiar sesión
            LoginActivity.currentUser = null;
            // Regresar al login, limpiando stack
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
