package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentCreditoBinding;

public class CreditoFragment extends Fragment {

    private FragmentCreditoBinding binding;
    private CreditoViewModel creditoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        creditoViewModel = new ViewModelProvider(this).get(CreditoViewModel.class);
        binding = FragmentCreditoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Texto del título desde strings.xml
        binding.textCredito.setText(getString(R.string.credito_title));

        // Acciones CRUD
        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreditoCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreditoConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreditoEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreditoEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
