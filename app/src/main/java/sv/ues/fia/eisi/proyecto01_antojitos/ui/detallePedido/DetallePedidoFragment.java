package sv.ues.fia.eisi.proyecto01_antojitos.ui.detallePedido;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.databinding.FragmentDetallePedidoBinding;

public class DetallePedidoFragment extends Fragment {

    private FragmentDetallePedidoBinding binding;
    private DetallePedidoViewModel detallePedidoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        detallePedidoViewModel = new ViewModelProvider(this).get(DetallePedidoViewModel.class);
        binding = FragmentDetallePedidoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textDetallePedido.setText(getString(R.string.detalle_pedido_title));

        binding.btnCrear.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DetallePedidoCrearActivity.class)));

        binding.btnConsultar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DetallePedidoConsultarActivity.class)));

        binding.btnEditar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DetallePedidoEditarActivity.class)));

        binding.btnEliminar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DetallePedidoEliminarActivity.class)));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
