package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.data.AuthRepository;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentPedidoBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.util.PermUIUtils;

public class PedidoFragment extends Fragment {

    private FragmentPedidoBinding binding;
    private PedidoViewModel pedidoViewModel;

    private static final Map<Integer, String> BTN_TO_PERM = Map.of(
            R.id.btnCrear,     "pedido_crear",
            R.id.btnConsultar, "pedido_consultar",
            R.id.btnEditar,    "pedido_editar",
            R.id.btnEliminar,  "pedido_eliminar"
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        pedidoViewModel = new ViewModelProvider(this).get(PedidoViewModel.class);
        binding = FragmentPedidoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textPedido.setText(getString(R.string.pedido_title));

        /* 1) Aplicar permisos → esconde los botones no autorizados */
        /* ─── Permisos: oculta/mostrar botones ─── */
        AuthRepository auth = new AuthRepository(requireContext());
        PermUIUtils.aplicarPermisosBotones(root, BTN_TO_PERM, auth);

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), PedidoCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), PedidoConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), PedidoEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), PedidoEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
