package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor;

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
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentRepartidorBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.util.PermUIUtils;

public class RepartidorFragment extends Fragment {

    private FragmentRepartidorBinding binding;
    private RepartidorViewModel repartidorViewModel;

    private static final Map<Integer, String> BTN_TO_PERM = Map.of(
            R.id.btnCrear,     "repartidor_crear",
            R.id.btnConsultar, "repartidor_consultar",
            R.id.btnEditar,    "repartidor_editar",
            R.id.btnEliminar,  "repartidor_eliminar"
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        repartidorViewModel = new ViewModelProvider(this).get(RepartidorViewModel.class);
        binding = FragmentRepartidorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textRepartidor.setText(getString(R.string.repartidor_title));

        /* 1) Aplicar permisos → esconde los botones no autorizados */
        /* ─── Permisos: oculta/mostrar botones ─── */
        AuthRepository auth = new AuthRepository(requireContext());
        PermUIUtils.aplicarPermisosBotones(root, BTN_TO_PERM, auth);

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RepartidorCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RepartidorConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RepartidorEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RepartidorEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
