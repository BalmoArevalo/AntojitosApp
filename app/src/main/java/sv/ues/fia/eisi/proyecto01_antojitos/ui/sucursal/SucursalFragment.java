package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentSucursalBinding;


public class SucursalFragment extends Fragment {

    private FragmentSucursalBinding binding;
    private SucursalViewModel sucursalViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        sucursalViewModel = new ViewModelProvider(this).get(SucursalViewModel.class);
        binding = FragmentSucursalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textSucursal.setText(getString(R.string.sucursal_title));

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), SucursalCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), SucursalConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), SucursalEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), SucursalEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}