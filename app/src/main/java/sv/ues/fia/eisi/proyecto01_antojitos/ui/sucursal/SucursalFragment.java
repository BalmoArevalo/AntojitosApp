package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.data.AuthRepository;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentSucursalBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.util.PermUIUtils;


public class SucursalFragment extends Fragment {

    private FragmentSucursalBinding binding;
    private SucursalViewModel sucursalViewModel;

    private static final Map<Integer, String> BTN_TO_PERM = Map.of(
            R.id.btnCrear,     "sucursal_crear",
            R.id.btnConsultar, "sucursal_consultar",
            R.id.btnEditar,    "sucursal_editar",
            R.id.btnEliminar,  "sucursal_eliminar"
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        sucursalViewModel = new ViewModelProvider(this).get(SucursalViewModel.class);
        binding = FragmentSucursalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textSucursal.setText(getString(R.string.sucursal_title));

        /* 1) Aplicar permisos → esconde los botones no autorizados */
        /* ─── Permisos: oculta/mostrar botones ─── */
        AuthRepository auth = new AuthRepository(requireContext());
        PermUIUtils.aplicarPermisosBotones(root, BTN_TO_PERM, auth);

        binding.btnCrear.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), SucursalCrearActivity.class);
                    startActivity(intent);
                });

        binding.btnConsultar.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), SucursalConsultarActivity.class);
                    startActivity(intent);
                });

        binding.btnEditar.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), SucursalEditarActivity.class);
                    startActivity(intent);
                });

        binding.btnEliminar.setOnClickListener(v ->{
                    Intent intent = new Intent(getActivity(), SucursalEliminarActivity.class);
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