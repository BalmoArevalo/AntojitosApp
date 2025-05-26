package sv.ues.fia.eisi.proyecto01_antojitos.ui.webServices;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.data.AuthRepository;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentWebServicesBinding;
import sv.ues.fia.eisi.proyecto01_antojitos.util.PermUIUtils;


public class WebServicesFragment extends Fragment {
    private FragmentWebServicesBinding binding;

    private static final Map<Integer, String> BTN_TO_PERM = Map.of(
            R.id.btnWS1, "ws_1",
            R.id.btnWS2, "ws_2",
            R.id.btnWS3, "ws_3",
            R.id.btnWS4, "ws_4",
            R.id.btnWS5, "ws_5",
            R.id.btnWS6, "ws_6",
            R.id.btnWS7, "ws_7",
            R.id.btnWS8, "ws_8",
            R.id.btnWS9, "ws_9",
            R.id.btnWS10, "ws_10"
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWebServicesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //binding.textWs.setText(getString(R.string.ws_title)); // Define ws_title en strings.xml

        // Aplicar permisos
        AuthRepository auth = new AuthRepository(requireContext());
        PermUIUtils.aplicarPermisosBotones(root, BTN_TO_PERM, auth);

        // Asignar eventos para abrir actividades
        binding.btnWS1.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), WebService1Activity.class)));

        binding.btnWS2.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), WebService2Activity.class)));

        binding.btnWS3.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), WebService3Activity.class)));

        binding.btnWS4.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), WebService4Activity.class)));

        binding.btnWS5.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), WebService5Activity.class)));

        binding.btnWS6.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), WebService6Activity.class)));

        binding.btnWS7.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), WebService7Activity.class)));

        binding.btnWS8.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), WebService8Activity.class)));

        binding.btnWS9.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), WebService9Activity.class)));

        binding.btnWS10.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), WebService10Activity.class)));

        return root;
    }
}