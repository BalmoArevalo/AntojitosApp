package sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.data.AuthRepository;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentClienteBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.util.PermUIUtils;

public class ClienteFragment extends Fragment {

    private FragmentClienteBinding binding;

    /** idBotón → idPermiso  */
    private static final Map<Integer, String> BTN_TO_PERM = Map.of(
            R.id.btnCrearCliente,     "cliente_crear",
            R.id.btnConsultarCliente, "cliente_consultar",
            R.id.btnEditarCliente,    "cliente_editar",
            R.id.btnEliminarCliente,  "cliente_eliminar"
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ClienteViewModel vm =
                new ViewModelProvider(this).get(ClienteViewModel.class);

        binding = FragmentClienteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /* ─── Permisos: oculta/mostrar botones ─── */
        AuthRepository auth = new AuthRepository(requireContext());
        PermUIUtils.aplicarPermisosBotones(root, BTN_TO_PERM, auth);

        /* ─── Listeners  (usa binding.*) ─── */
        binding.btnCrearCliente.setOnClickListener(v ->
                startActivity(new Intent(requireContext(),
                        ClienteCrearActivity.class)));

        binding.btnConsultarCliente.setOnClickListener(v ->
                startActivity(new Intent(requireContext(),
                        ClienteConsultarActivity.class)));

        binding.btnEditarCliente.setOnClickListener(v ->
                startActivity(new Intent(requireContext(),
                        ClienteEditarActivity.class)));

        binding.btnEliminarCliente.setOnClickListener(v ->
                startActivity(new Intent(requireContext(),
                        ClienteEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
