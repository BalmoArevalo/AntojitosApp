package sv.ues.fia.eisi.proyecto01_antojitos.util;

import android.view.View;
import java.util.Map;
import sv.ues.fia.eisi.proyecto01_antojitos.data.AuthRepository;

/**
 * Helper para ocultar o mostrar controles de UI según los permisos
 * registrados en la tabla ACCESOUSUARIO / OPCIONCRUD.
 *
 * Uso típico dentro de un Fragment:
 *
 *   AuthRepository auth = new AuthRepository(requireContext());
 *   PermUIUtils.aplicarPermisosBotones(root, MAPA_BTN_PERM, auth);
 */
public final class PermUIUtils {

    private PermUIUtils() { /* helper estático */ }

    /**
     * Recorre el mapa (idBotón → idPermiso) y decide si cada botón
     * debe mostrarse u ocultarse.
     *
     * @param root         root view inflado del fragmento/activity
     * @param btnToPerm    mapa: R.id.* → "permiso_crud"
     * @param auth         repositorio de autenticación/permisos
     */
    public static void aplicarPermisosBotones(
            View root,
            Map<Integer, String> btnToPerm,
            AuthRepository auth) {

        boolean esAdmin = auth.tienePermiso("todo_admin");

        for (Map.Entry<Integer, String> entry : btnToPerm.entrySet()) {
            int   btnId      = entry.getKey();
            String permiso   = entry.getValue();

            View btn = root.findViewById(btnId);
            if (btn == null) continue;              // por si faltó en el layout

            boolean visible = esAdmin || auth.tienePermiso(permiso);
            btn.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
}
