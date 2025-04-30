package sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentClienteBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.R;

public class ClienteFragment extends Fragment {

    private FragmentClienteBinding binding;
    private ClienteViewModel clienteViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        binding = FragmentClienteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Recomendación: mostrar título desde strings.xml
        binding.textCliente.setText(getString(R.string.cliente_title));

        // Eventos de los botones
        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ClienteCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ClienteConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ClienteEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ClienteEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
