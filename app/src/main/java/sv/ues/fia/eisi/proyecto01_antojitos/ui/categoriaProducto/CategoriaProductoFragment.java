package sv.ues.fia.eisi.proyecto01_antojitos.ui.categoriaProducto;

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
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentCategoriaProductoBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.util.PermUIUtils;

/**
 * Fragment para las operaciones CRUD de Categoría de Producto,
 * con visibilidad de botones controlada por permisos.
 */
public class CategoriaProductoFragment extends Fragment {

    private FragmentCategoriaProductoBinding binding;

    /** botón-id → id de permiso que lo habilita */
    private static final Map<Integer, String> BTN_TO_PERM = Map.of(
            R.id.btnCrear,     "categoriaproducto_crear",
            R.id.btnConsultar, "categoriaproducto_consultar",
            R.id.btnEditar,    "categoriaproducto_editar",
            R.id.btnEliminar,  "categoriaproducto_eliminar"
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        new ViewModelProvider(this).get(CategoriaProductoViewModel.class); // (no usamos LiveData aún)

        binding = FragmentCategoriaProductoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /* 1) Aplicar permisos → esconde los botones no autorizados */
        /* ─── Permisos: oculta/mostrar botones ─── */
        AuthRepository auth = new AuthRepository(requireContext());
        PermUIUtils.aplicarPermisosBotones(root, BTN_TO_PERM, auth);

        /* 2) Listeners SOLO para los botones visibles
               (los ocultos no recibirán clicks) */
        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CategoriaProductoCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CategoriaProductoConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CategoriaProductoEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CategoriaProductoEliminarActivity.class)));

        /* 3) Título opcional */
        binding.textCategoriaProducto.setText(R.string.categoria_producto_title);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
