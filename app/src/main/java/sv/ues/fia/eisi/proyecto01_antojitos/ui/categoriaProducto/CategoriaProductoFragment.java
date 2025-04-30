package sv.ues.fia.eisi.proyecto01_antojitos.ui.categoriaProducto;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentCategoriaProductoBinding;

public class CategoriaProductoFragment extends Fragment {

    private FragmentCategoriaProductoBinding binding;
    private CategoriaProductoViewModel categoriaProductoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        categoriaProductoViewModel = new ViewModelProvider(this).get(CategoriaProductoViewModel.class);
        binding = FragmentCategoriaProductoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Opción: mostrar título desde strings.xml
        binding.textCategoriaProducto.setText(getString(R.string.categoria_producto_title));

        // Eventos de los botones
        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CategoriaProductoCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CategoriaProductoConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CategoriaProductoEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CategoriaProductoEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
