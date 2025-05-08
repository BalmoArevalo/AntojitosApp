package sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento;

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
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentDepartamentoBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.util.PermUIUtils;

public class DepartamentoFragment extends Fragment {

    private FragmentDepartamentoBinding binding;
    private DepartamentoViewModel departamentoViewModel;

    private static final Map<Integer, String> BTN_TO_PERM = Map.of(
            R.id.btnCrear,     "departamento_crear",
            R.id.btnConsultar, "departamento_consultar",
            R.id.btnEditar,    "departamento_editar",
            R.id.btnEliminar,  "departamento_eliminar"
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        departamentoViewModel = new ViewModelProvider(this).get(DepartamentoViewModel.class);
        binding = FragmentDepartamentoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textDepartamento.setText(getString(R.string.departamento_title));

        /* 1) Aplicar permisos → esconde los botones no autorizados */
        /* ─── Permisos: oculta/mostrar botones ─── */
        AuthRepository auth = new AuthRepository(requireContext());
        PermUIUtils.aplicarPermisosBotones(root, BTN_TO_PERM, auth);

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DepartamentoCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DepartamentoConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DepartamentoEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DepartamentoEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
