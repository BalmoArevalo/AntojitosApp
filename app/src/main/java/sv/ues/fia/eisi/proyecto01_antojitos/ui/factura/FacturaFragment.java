package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentFacturaBinding;

public class FacturaFragment extends Fragment {

    private FragmentFacturaBinding binding;
    private FacturaViewModel facturaViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        facturaViewModel = new ViewModelProvider(this).get(FacturaViewModel.class);
        binding = FragmentFacturaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textFactura.setText(getString(R.string.factura_title));

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), FacturaCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), FacturaConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), FacturaEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), FacturaEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
