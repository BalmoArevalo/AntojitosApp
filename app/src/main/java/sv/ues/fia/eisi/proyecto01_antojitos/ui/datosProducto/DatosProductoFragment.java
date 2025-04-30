package sv.ues.fia.eisi.proyecto01_antojitos.ui.datosProducto;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentDatosProductoBinding;

public class DatosProductoFragment extends Fragment {

    private FragmentDatosProductoBinding binding;
    private DatosProductoViewModel datosProductoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        datosProductoViewModel = new ViewModelProvider(this).get(DatosProductoViewModel.class);
        binding = FragmentDatosProductoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textDatosProducto.setText(getString(R.string.datos_producto_title));

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DatosProductoCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DatosProductoConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DatosProductoEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DatosProductoEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
