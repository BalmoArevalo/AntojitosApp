package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido;

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
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentRepartoPedidoBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido.RepartoPedidoCrearActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido.RepartoPedidoConsultarActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido.RepartoPedidoEditarActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido.RepartoPedidoEliminarActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.util.PermUIUtils;

public class RepartoPedidoFragment extends Fragment {

    private FragmentRepartoPedidoBinding binding;
    private RepartoPedidoViewModel repartoPedidoViewModel;

    private static final Map<Integer, String> BTN_TO_PERM = Map.of(
            R.id.btnCrear,     "repartopedido_crear",
            R.id.btnConsultar, "repartopedido_consultar",
            R.id.btnEditar,    "repartopedido_editar",
            R.id.btnEliminar,  "repartopedido_eliminar"
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        repartoPedidoViewModel = new ViewModelProvider(this).get(RepartoPedidoViewModel.class);
        binding = FragmentRepartoPedidoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Establecer título
        binding.textRepartoPedido.setText(getString(R.string.reparto_pedido_title));

        // Aplicar permisos (ocultar/mostrar botones según rol)
        AuthRepository auth = new AuthRepository(requireContext());
        PermUIUtils.aplicarPermisosBotones(root, BTN_TO_PERM, auth);

        // Set OnClickListeners
        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RepartoPedidoCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RepartoPedidoConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RepartoPedidoEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RepartoPedidoEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
