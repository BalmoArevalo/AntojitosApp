package sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentDistritoBinding;

public class DistritoFragment extends Fragment {

    private FragmentDistritoBinding binding;
    private DistritoViewModel distritoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        distritoViewModel = new ViewModelProvider(this).get(DistritoViewModel.class);
        binding = FragmentDistritoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textDistrito.setText(getString(R.string.distrito_title));

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DistritoCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DistritoConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DistritoEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DistritoEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
