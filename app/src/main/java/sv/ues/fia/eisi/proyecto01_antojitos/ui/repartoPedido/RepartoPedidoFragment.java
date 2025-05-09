package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido; // Asegúrate que el paquete sea el correcto

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

// Importar R y la clase de Binding generada para el layout del fragment
import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentRepartoPedidoBinding; // Asume que el layout se llama fragment_reparto_pedido.xml

// Importar las Activities de RepartoPedido (Ajusta los nombres si son diferentes)
import sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido.RepartoPedidoCrearActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido.RepartoPedidoConsultarActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido.RepartoPedidoEditarActivity;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido.RepartoPedidoEliminarActivity;


public class RepartoPedidoFragment extends Fragment {

    // Variable para View Binding
    private FragmentRepartoPedidoBinding binding;
    // Variable para el ViewModel
    private RepartoPedidoViewModel repartoPedidoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inicializar ViewModel
        repartoPedidoViewModel = new ViewModelProvider(this).get(RepartoPedidoViewModel.class);

        // Inflar el layout usando View Binding
        binding = FragmentRepartoPedidoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // --- Configuración de la UI ---

        // 1. Establecer el título del Fragment
        // Asumiendo TextView con id "textRepartoPedido" en el layout y string R.string.reparto_pedido_title
        if (binding.textRepartoPedido != null) {
            binding.textRepartoPedido.setText(getString(R.string.reparto_pedido_title));
        } else {
            // Opcional: Establecer el título de la Toolbar/ActionBar si aplica
            // requireActivity().setTitle(getString(R.string.reparto_pedido_title));
        }


        // 2. Configurar los listeners para los botones CRUD
        // Asumiendo botones con IDs: btnCrear, btnConsultar, btnEditar, btnEliminar
        if (binding.btnCrear != null) {
            binding.btnCrear.setOnClickListener(v ->
                    startActivity(new Intent(getActivity(), RepartoPedidoCrearActivity.class)));
        } else {
            Log.w(TAG, "Botón btnCrear no encontrado en el layout.");
        }


        if (binding.btnConsultar != null) {
            binding.btnConsultar.setOnClickListener(v ->
                    startActivity(new Intent(getActivity(), RepartoPedidoConsultarActivity.class)));
        } else {
            Log.w(TAG, "Botón btnConsultar no encontrado en el layout.");
        }

        if (binding.btnEditar != null) {
            binding.btnEditar.setOnClickListener(v ->
                    startActivity(new Intent(getActivity(), RepartoPedidoEditarActivity.class)));
        } else Log.w(TAG, "Botón btnEditar no encontrado en el layout.");

        if (binding.btnEliminar != null) {
            binding.btnEliminar.setOnClickListener(v ->
                    startActivity(new Intent(getActivity(), RepartoPedidoEliminarActivity.class)));
        } else {
            Log.w(TAG, "Botón btnEliminar no encontrado en el layout.");
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Liberar la referencia al binding
        binding = null;
    }
}