package sv.ues.fia.eisi.proyecto01_antojitos;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.data.AuthRepository;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.ActivityMainBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.login.LoginActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.util.MenuPermUtils;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    // ------------------------------------------------------------------
    // Ciclo de vida
    // ------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* 1)  Verificar sesión */
        AuthRepository auth = new AuthRepository(this);
        if (!auth.isLoggedIn()) {
            goToLogin();   // sale si no hay sesión
            return;
        }

        /* 2)  ViewBinding + Toolbar */
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        /* 3)  FAB (placeholder) */
        binding.appBarMain.fab.setOnClickListener(view ->
                Snackbar.make(view, "Acción aún no definida", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab).show());

        /* 4)  Drawer + Navigation */
        DrawerLayout drawer         = binding.drawerLayout;
        NavigationView navigation   = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_cliente,
                R.id.nav_sucursal,
                R.id.nav_repartidor,
                R.id.nav_producto,
                R.id.nav_categoria_producto,
                R.id.nav_pedido,
                R.id.nav_factura,
                R.id.nav_credito,
                R.id.nav_detalle_pedido,
                R.id.nav_tipo_evento,
                R.id.nav_direccion,
                R.id.nav_departamento,
                R.id.nav_municipio,
                R.id.nav_distrito,
                R.id.nav_datos_producto,
                R.id.nav_web_services
        ).setOpenableLayout(drawer).build();

        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment_content_main);

        NavigationUI.setupActionBarWithNavController(
                this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigation, navController);

        /* 5)  Mostrar/ocultar ítems según permisos */
        aplicarPermisosDrawer(navigation.getMenu(),
                auth.getPermisosActuales(),
                MenuPermUtils.MENU_TO_PERM);
    }

    // ------------------------------------------------------------------
    //  Menú overflow
    // ------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);   // incluye action_logout
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            new AuthRepository(this).logout();
            goToLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ------------------------------------------------------------------
    //  Navegación Up
    // ------------------------------------------------------------------
    @Override
    public boolean onSupportNavigateUp() {
        NavController nav = Navigation.findNavController(
                this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(nav, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // ------------------------------------------------------------------
    //  Helpers privados
    // ------------------------------------------------------------------
    private void goToLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    /** Muestra u oculta cada item del drawer según la lista de permisos. */
    private void aplicarPermisosDrawer(Menu menu,
                                       List<String> permisosUsuario,
                                       Map<Integer, String> mapMenuPerm) {

        boolean esAdmin = permisosUsuario.contains("todo_admin");

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item     = menu.getItem(i);
            String permisoReq = mapMenuPerm.get(item.getItemId());

            boolean visible =
                    permisoReq == null              // sin mapeo => visible siempre
                            || esAdmin
                            || permisosUsuario.contains(permisoReq);

            item.setVisible(visible);
        }
    }
}
