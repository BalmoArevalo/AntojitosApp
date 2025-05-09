package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito;

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
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentCreditoBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.util.PermUIUtils;

public class CreditoFragment extends Fragment {

    private FragmentCreditoBinding binding;
    private CreditoViewModel creditoViewModel;

    private static final Map<Integer, String> BTN_TO_PERM = Map.of(
            R.id.btnCrear,     "credito_crear",
            R.id.btnConsultar, "credito_consultar",
            R.id.btnEditar,    "credito_editar",
            R.id.btnEliminar,  "credito_eliminar"
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        creditoViewModel = new ViewModelProvider(this).get(CreditoViewModel.class);
        binding = FragmentCreditoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /* 1) Aplicar permisos → esconde los botones no autorizados */
        /* ─── Permisos: oculta/mostrar botones ─── */
        AuthRepository auth = new AuthRepository(requireContext());
        PermUIUtils.aplicarPermisosBotones(root, BTN_TO_PERM, auth);

        // Texto del título desde strings.xml
        binding.textCredito.setText(getString(R.string.credito_title));

        // Acciones CRUD
        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreditoCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreditoConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreditoEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreditoEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
