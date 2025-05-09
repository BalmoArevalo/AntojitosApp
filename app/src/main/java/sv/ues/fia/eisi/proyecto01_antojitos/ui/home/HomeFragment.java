package sv.ues.fia.eisi.proyecto01_antojitos.ui.home;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.data.AuthRepository;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentHomeBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.db.seeders.DatosInicialesSeeder;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.datosProducto.DatosProductoDAO;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    /* --------------------------------------------------------------- */
    /*  Ciclo de vida                                                  */
    /* --------------------------------------------------------------- */
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState)
    {
        HomeViewModel vm = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        /* 1) Mensaje de bienvenida */
        AuthRepository auth = new AuthRepository(requireContext());
        String idUser = auth.getIdUsuarioActual();        // 'SU', 'CL', 'RP', 'SC'
        String rol    = mapearRol(idUser);               // texto legible
        vm.setText("Bienvenido " + rol);
        vm.getText().observe(getViewLifecycleOwner(), binding.textHome::setText);

        /* 2) Botón Crear BD – solo para superusuario */
        Button btnCrear = binding.getRoot().findViewById(R.id.btnCrearBase);
        boolean esSuper = "SU".equals(idUser) || auth.tienePermiso("todo_admin");
        btnCrear.setVisibility(esSuper ? View.VISIBLE : View.GONE);

        if (esSuper) {
            btnCrear.setOnClickListener(v -> poblarNegocio());
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /* --------------------------------------------------------------- */
    /*  Helpers privados                                               */
    /* --------------------------------------------------------------- */
    private void poblarNegocio() {
        try (SQLiteDatabase db = new DBHelper(getContext()).getWritableDatabase()) {

            /* Limpia únicamente tablas de negocio (mismo orden que onUpgrade) */
            db.beginTransaction();
            try {
                DatosInicialesSeeder.poblar(db);          // repoblar catálogos
                db.setTransactionSuccessful();
            } finally { db.endTransaction(); }

            Toast.makeText(getContext(),
                    "Catálogos de negocio recreados y poblados",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    /** Convierte el id del usuario en un nombre legible. */
    private String mapearRol(String id) {
        if (id == null) return "";
        switch (id) {
            case "SU": return "superusuario";
            case "CL": return "cliente";
            case "RP": return "repartidor";
            case "SC": return "sucursal";
            default:   return id;
        }
    }
}
