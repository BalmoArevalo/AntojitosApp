package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentTipoEventoBinding;

public class TipoEventoFragment extends Fragment {

    private FragmentTipoEventoBinding binding;
    private TipoEventoViewModel tipoEventoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        tipoEventoViewModel = new ViewModelProvider(this).get(TipoEventoViewModel.class);
        binding = FragmentTipoEventoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textTipoEvento.setText(getString(R.string.tipo_evento_title));

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TipoEventoCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TipoEventoConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TipoEventoEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TipoEventoEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
