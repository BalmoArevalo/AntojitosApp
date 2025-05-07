package sv.ues.fia.eisi.proyecto01_antojitos.ui.producto;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class ProductoFragment extends Fragment {

    private ProductoViewModel productoViewModel;
    private MaterialButton btnCrearProducto;
    private MaterialButton btnConsultarProducto;
    private MaterialButton btnEditarProducto;
    private MaterialButton btnEliminarProducto;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_producto, container, false);

        productoViewModel = new ViewModelProvider(this).get(ProductoViewModel.class);

        // Inicializar vistas
        inicializarVistas(root);

        // Configurar listeners
        configurarListeners();

        return root;
    }

    private void inicializarVistas(View view) {
        btnCrearProducto = view.findViewById(R.id.btnCrearProducto);
        btnConsultarProducto = view.findViewById(R.id.btnConsultarProducto);
        btnEditarProducto = view.findViewById(R.id.btnEditarProducto);
        btnEliminarProducto = view.findViewById(R.id.btnEliminarProducto);
    }

    private void configurarListeners() {
        btnCrearProducto.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProductoCrearActivity.class);
            startActivity(intent);
        });

        btnConsultarProducto.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProductoConsultarActivity.class);
            startActivity(intent);
        });

        btnEditarProducto.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProductoEditarActivity.class);
            startActivity(intent);
        });

        btnEliminarProducto.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProductoEliminarActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}