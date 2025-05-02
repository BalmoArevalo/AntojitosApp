package sv.ues.fia.eisi.proyecto01_antojitos.ui.home;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentHomeBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.db.BaseDatosSeeder;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Button btnCrearBase = root.findViewById(R.id.btnCrearBase);
        btnCrearBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DBHelper dbHelper = new DBHelper(getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    // Forzar recreación segura
                    dbHelper.onUpgrade(db, DBHelper.DB_VERSION, DBHelper.DB_VERSION);

                    // Reabrir base para que esté limpia
                    db = dbHelper.getWritableDatabase();

                    // Poblar base
                    BaseDatosSeeder.insertarDatosIniciales(db);

                    Toast.makeText(getContext(), "Base de datos recreada y poblada", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace(); // Para Logcat
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}