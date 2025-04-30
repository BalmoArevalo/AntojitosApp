package sv.ues.fia.eisi.proyecto01_antojitos.ui.producto;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentProductoBinding;

public class ProductoFragment extends Fragment {

    private FragmentProductoBinding binding;
    private ProductoViewModel productoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        productoViewModel = new ViewModelProvider(this).get(ProductoViewModel.class);
        binding = FragmentProductoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textProducto.setText(getString(R.string.producto_title));

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ProductoCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ProductoConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ProductoEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ProductoEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
