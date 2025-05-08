package sv.ues.fia.eisi.proyecto01_antojitos.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Encargado de gestionar la sesión del usuario con SharedPreferences.
 *
 * Guarda:
 *  • ID_USUARIO  → clave primaria del usuario logueado
 *  • PERMISOS    → lista opcional, separada por comas (para acceso rápido)
 *
 * Si sólo quieres persistir el ID, ignora los métodos de permisos.
 */
public final class SessionManager {

    private static final String PREFS_NAME    = "antojitos_prefs";
    private static final String KEY_USER_ID   = "id_usuario";
    private static final String KEY_PERMISOS  = "permisos";      // comma-separated

    private SessionManager() { /* utility */ }

    /* ================= Autenticación ================= */

    /** Guarda el ID del usuario y (opcional) su lista de permisos. */
    public static void login(Context ctx, String idUsuario) {
        SharedPreferences.Editor ed = prefs(ctx).edit();
        ed.putString(KEY_USER_ID, idUsuario);
        ed.apply();
    }

    /** Borra toda la información de sesión. */
    public static void logout(Context ctx) {
        prefs(ctx).edit().clear().apply();
    }

    /** ¿Hay usuario guardado en preferencias? */
    public static boolean isLoggedIn(Context ctx) {
        return prefs(ctx).contains(KEY_USER_ID);
    }

    /** Devuelve el ID del usuario o null. */
    public static String getIdUsuario(Context ctx) {
        return prefs(ctx).getString(KEY_USER_ID, null);
    }

    /* ================= Permisos (opcional) ================= */

    /** Guarda la lista de permisos como «perm1,perm2,perm3». */
    public static void setPermisos(Context ctx, List<String> permisos) {
        Set<String> unique = new HashSet<>(permisos);      // evita repetidos
        prefs(ctx).edit()
                .putString(KEY_PERMISOS, String.join(",", unique))
                .apply();
    }

    /** Devuelve la lista de permisos almacenada, o lista vacía. */
    public static List<String> getPermisos(Context ctx) {
        String joined = prefs(ctx).getString(KEY_PERMISOS, "");
        if (joined.isEmpty()) return Collections.emptyList();
        return Arrays.asList(joined.split(","));
    }

    /* ================= Utils ================= */

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
