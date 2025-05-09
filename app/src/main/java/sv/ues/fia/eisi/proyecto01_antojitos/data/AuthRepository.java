package sv.ues.fia.eisi.proyecto01_antojitos.data;

import android.content.Context;

import java.util.Collections;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.permisos.AccesoUsuarioDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.db.usuario.Usuario;
import sv.ues.fia.eisi.proyecto01_antojitos.db.usuario.UsuarioDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.util.SessionManager;

/**
 * Repositorio de autenticación y permisos.
 *
 * · login/logout   → gestiona SessionManager
 * · getIdUsuario…  → expone el id ('SU','CL','RP','SC') a la UI
 * · getPermisos…   → devuelve la lista de IDs de opción CRUD
 *
 * Si en el futuro migras a Retrofit + API, solo toca esta clase.
 */
public class AuthRepository {

    private final UsuarioDAO       usuarioDAO;
    private final AccesoUsuarioDAO accesoDAO;
    private final Context          ctx;

    public AuthRepository(Context ctx) {
        this.ctx        = ctx.getApplicationContext();
        this.usuarioDAO = new UsuarioDAO(ctx);
        this.accesoDAO  = new AccesoUsuarioDAO(ctx);
    }

    /* ====================  Sesión  ==================== */

    /** Intenta iniciar sesión y guarda el id en SharedPreferences. */
    public boolean login(String nomUsuario, String clave) {
        Usuario u = usuarioDAO.validarLogin(nomUsuario, clave);
        if (u == null) return false;

        SessionManager.login(ctx, u.getIdUsuario());
        return true;
    }

    public void logout()           { SessionManager.logout(ctx); }
    public boolean isLoggedIn()    { return SessionManager.isLoggedIn(ctx); }

    /** Id del usuario activo o null. */
    public String getCurrentUserId()   { return SessionManager.getIdUsuario(ctx); }
    /** Alias legible para la UI. */
    public String getIdUsuarioActual() { return getCurrentUserId(); }

    /* ====================  Permisos  ==================== */

    /** Lista de IDs de opción para el usuario logueado. */
    public List<String> getPermisosActuales() {
        String idUsuario = getCurrentUserId();
        if (idUsuario == null) return Collections.emptyList();
        return accesoDAO.obtenerOpcionesPorUsuario(idUsuario);
    }

    /** true si posee idOpcion o 'todo_admin'. */
    public boolean tienePermiso(String idOpcion) {
        String idUsuario = getCurrentUserId();
        return idUsuario != null && accesoDAO.tieneAcceso(idUsuario, idOpcion);
    }
}
