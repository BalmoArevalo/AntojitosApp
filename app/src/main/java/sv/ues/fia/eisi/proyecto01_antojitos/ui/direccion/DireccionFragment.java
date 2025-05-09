package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

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
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentDireccionBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.util.PermUIUtils;

public class DireccionFragment extends Fragment {

    private FragmentDireccionBinding binding;
    private DireccionViewModel direccionViewModel;

    private static final Map<Integer, String> BTN_TO_PERM = Map.of(
            R.id.btnCrear,     "direccion_crear",
            R.id.btnConsultar, "direccion_consultar",
            R.id.btnEditar,    "direccion_editar",
            R.id.btnEliminar,  "direccion_eliminar"
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        direccionViewModel = new ViewModelProvider(this).get(DireccionViewModel.class);
        binding = FragmentDireccionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textDireccion.setText(getString(R.string.direccion_title));

        /* 1) Aplicar permisos → esconde los botones no autorizados */
        /* ─── Permisos: oculta/mostrar botones ─── */
        AuthRepository auth = new AuthRepository(requireContext());
        PermUIUtils.aplicarPermisosBotones(root, BTN_TO_PERM, auth);

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DireccionCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DireccionConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DireccionEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DireccionEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
