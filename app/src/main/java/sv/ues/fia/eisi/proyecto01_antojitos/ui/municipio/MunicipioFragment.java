package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentMunicipioBinding;

public class MunicipioFragment extends Fragment {

    private FragmentMunicipioBinding binding;
    private MunicipioViewModel municipioViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        municipioViewModel = new ViewModelProvider(this).get(MunicipioViewModel.class);
        binding = FragmentMunicipioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textMunicipio.setText(getString(R.string.municipio_title));

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MunicipioCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MunicipioConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MunicipioEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MunicipioEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
