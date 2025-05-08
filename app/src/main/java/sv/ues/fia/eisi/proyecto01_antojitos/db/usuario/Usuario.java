package sv.ues.fia.eisi.proyecto01_antojitos.db.usuario;

import android.content.ContentValues;

/**
 * Modelo POJO para la tabla USUARIO.
 */
public class Usuario {

    private String idUsuario;   // PK: “SU”, “CL”, …
    private String nomUsuario;  // nombre para mostrar / login
    private String clave;       // contraseña en texto plano (demo)

    public Usuario() {}

    public Usuario(String idUsuario, String nomUsuario, String clave) {
        this.idUsuario = idUsuario;
        this.nomUsuario = nomUsuario;
        this.clave = clave;
    }

    /* ===== Getters & Setters ===== */

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getNomUsuario() { return nomUsuario; }
    public void setNomUsuario(String nomUsuario) { this.nomUsuario = nomUsuario; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    /* ===== Helper para inserts/updates ===== */
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("ID_USUARIO", idUsuario);
        cv.put("NOM_USUARIO", nomUsuario);
        cv.put("CLAVE", clave);
        return cv;
    }
}
