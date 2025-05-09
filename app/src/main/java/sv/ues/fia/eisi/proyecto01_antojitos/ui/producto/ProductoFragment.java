package sv.ues.fia.eisi.proyecto01_antojitos.ui.producto;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.data.AuthRepository;
import sv.ues.fia.eisi.proyecto01_antojitos.util.PermUIUtils;

public class ProductoFragment extends Fragment {

    /** id-botón → id-permiso */
    private static final Map<Integer, String> BTN_TO_PERM = Map.of(
            R.id.btnCrearProducto,     "producto_crear",
            R.id.btnConsultarProducto, "producto_consultar",
            R.id.btnEditarProducto,    "producto_editar",
            R.id.btnEliminarProducto,  "producto_eliminar"
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_producto, container, false);

        /* ---------- aplicar permisos ---------- */
        AuthRepository auth = new AuthRepository(requireContext());
        PermUIUtils.aplicarPermisosBotones(root, BTN_TO_PERM, auth);

        /* ---------- listeners ---------- */
        root.findViewById(R.id.btnCrearProducto).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ProductoCrearActivity.class)));

        root.findViewById(R.id.btnConsultarProducto).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ProductoConsultarActivity.class)));

        root.findViewById(R.id.btnEditarProducto).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ProductoEditarActivity.class)));

        root.findViewById(R.id.btnEliminarProducto).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ProductoEliminarActivity.class)));

        return root;
    }
}
