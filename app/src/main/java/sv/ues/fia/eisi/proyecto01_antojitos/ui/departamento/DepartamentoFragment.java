package sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentDepartamentoBinding;

public class DepartamentoFragment extends Fragment {

    private FragmentDepartamentoBinding binding;
    private DepartamentoViewModel departamentoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        departamentoViewModel = new ViewModelProvider(this).get(DepartamentoViewModel.class);
        binding = FragmentDepartamentoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textDepartamento.setText(getString(R.string.departamento_title));

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DepartamentoCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DepartamentoConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DepartamentoEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DepartamentoEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
