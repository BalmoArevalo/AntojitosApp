package sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito;

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

public class DistritoFragment extends Fragment {

    /** botón-id → permiso */
    private static final Map<Integer, String> BTN_TO_PERM = Map.of(
            R.id.btnCrearDistrito,     "distrito_crear",
            R.id.btnConsultarDistrito, "distrito_consultar",
            R.id.btnEditarDistrito,    "distrito_editar",
            R.id.btnEliminarDistrito,  "distrito_eliminar"
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_distrito, container, false);

        /* ---------- aplicar permisos ---------- */
        AuthRepository auth = new AuthRepository(requireContext());
        PermUIUtils.aplicarPermisosBotones(root, BTN_TO_PERM, auth);

        /* ---------- listeners ---------- */
        root.findViewById(R.id.btnCrearDistrito).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DistritoCrearActivity.class)));

        root.findViewById(R.id.btnConsultarDistrito).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DistritoConsultarActivity.class)));

        root.findViewById(R.id.btnEditarDistrito).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DistritoEditarActivity.class)));

        root.findViewById(R.id.btnEliminarDistrito).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DistritoEliminarActivity.class)));

        return root;
    }
}
