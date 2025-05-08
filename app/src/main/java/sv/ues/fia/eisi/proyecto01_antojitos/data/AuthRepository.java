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
 *  • login/logout → maneja SessionManager
 *  • exponer usuario y lista de permisos a la UI
 *
 *  **Futuro-proof**: si migras a Retrofit + API, cambia aquí y
 *  la UI seguirá igual.
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

    /* ==================== Sesión ==================== */

    /**
     * Intenta iniciar sesión.
     * @return true si las credenciales son válidas.
     */
    public boolean login(String nomUsuario, String clave) {
        Usuario u = usuarioDAO.validarLogin(nomUsuario, clave);
        if (u == null) return false;

        // Guarda usuario en SharedPreferences
        SessionManager.login(ctx, u.getIdUsuario());
        return true;
    }

    /** Borra la sesión actual. */
    public void logout() {
        SessionManager.logout(ctx);
    }

    /** ¿Hay sesión activa? */
    public boolean isLoggedIn() {
        return SessionManager.isLoggedIn(ctx);
    }

    /** ID del usuario activo o null. */
    public String getCurrentUserId() {
        return SessionManager.getIdUsuario(ctx);
    }

    /* ==================== Permisos ==================== */

    /**
     * Devuelve la lista de IDs de opción (permisos) para el usuario logueado.
     * Si el usuario tiene 'todo_admin', se devuelve una lista con ese único comodín.
     */
    public List<String> getPermisosActuales() {
        String idUsuario = getCurrentUserId();
        if (idUsuario == null) return Collections.emptyList();
        return accesoDAO.obtenerOpcionesPorUsuario(idUsuario);
    }

    /**
     * Atajo: ¿el usuario activo tiene permiso explícito o 'todo_admin'?
     */
    public boolean tienePermiso(String idOpcion) {
        String idUsuario = getCurrentUserId();
        return idUsuario != null && accesoDAO.tieneAcceso(idUsuario, idOpcion);
    }
}
