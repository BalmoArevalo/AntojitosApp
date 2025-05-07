package sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class DistritoFragment extends Fragment {

    private Button btnCrearDistrito;
    private Button btnConsultarDistrito;
    private Button btnEditarDistrito;
    private Button btnEliminarDistrito;

    public DistritoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_distrito, container, false);

        // Inicializar los botones
        btnCrearDistrito = view.findViewById(R.id.btnCrearDistrito);
        btnConsultarDistrito = view.findViewById(R.id.btnConsultarDistrito);
        btnEditarDistrito = view.findViewById(R.id.btnEditarDistrito);
        btnEliminarDistrito = view.findViewById(R.id.btnEliminarDistrito);

        // Configurar listeners para los botones
        btnCrearDistrito.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DistritoCrearActivity.class);
            startActivity(intent);
        });

        btnConsultarDistrito.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DistritoConsultarActivity.class);
            startActivity(intent);
        });

        btnEditarDistrito.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DistritoEditarActivity.class);
            startActivity(intent);
        });

        btnEliminarDistrito.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DistritoEliminarActivity.class);
            startActivity(intent);
        });

        return view;
    }
}