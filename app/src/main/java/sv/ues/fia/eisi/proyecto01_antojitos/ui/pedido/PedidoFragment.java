package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentPedidoBinding;

public class PedidoFragment extends Fragment {

    private FragmentPedidoBinding binding;
    private PedidoViewModel pedidoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        pedidoViewModel = new ViewModelProvider(this).get(PedidoViewModel.class);
        binding = FragmentPedidoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textPedido.setText(getString(R.string.pedido_title));

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), PedidoCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), PedidoConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), PedidoEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), PedidoEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
