package sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentClienteBinding;

public class ClienteFragment extends Fragment {

    private FragmentClienteBinding binding;
    private Button btnCrearCliente, btnConsultarCliente,
            btnEditarCliente, btnEliminarCliente;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ClienteViewModel clienteViewModel =
                new ViewModelProvider(this).get(ClienteViewModel.class);

        binding = FragmentClienteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializar los botones
        btnCrearCliente = root.findViewById(R.id.btnCrearCliente);
        btnConsultarCliente = root.findViewById(R.id.btnConsultarCliente);
        btnEditarCliente = root.findViewById(R.id.btnEditarCliente);
        btnEliminarCliente = root.findViewById(R.id.btnEliminarCliente);

        // Configurar listeners para los botones
        btnCrearCliente.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ClienteCrearActivity.class);
            startActivity(intent);
        });

        btnConsultarCliente.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ClienteConsultarActivity.class);
            startActivity(intent);
        });

        btnEditarCliente.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ClienteEditarActivity.class);
            startActivity(intent);
        });

        btnEliminarCliente.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ClienteEliminarActivity.class);
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}