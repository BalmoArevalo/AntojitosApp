package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;

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
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentMunicipioBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.util.PermUIUtils;

public class MunicipioFragment extends Fragment {

    private FragmentMunicipioBinding binding;
    private MunicipioViewModel municipioViewModel;

    private static final Map<Integer, String> BTN_TO_PERM = Map.of(
            R.id.btnCrear,     "municipio_crear",
            R.id.btnConsultar, "municipio_consultar",
            R.id.btnEditar,    "municipio_editar",
            R.id.btnEliminar,  "municipio_eliminar"
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        municipioViewModel = new ViewModelProvider(this).get(MunicipioViewModel.class);
        binding = FragmentMunicipioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textMunicipio.setText(getString(R.string.municipio_title));

        /* 1) Aplicar permisos → esconde los botones no autorizados */
        /* ─── Permisos: oculta/mostrar botones ─── */
        AuthRepository auth = new AuthRepository(requireContext());
        PermUIUtils.aplicarPermisosBotones(root, BTN_TO_PERM, auth);

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MunicipioCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MunicipioConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MunicipioEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MunicipioEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
