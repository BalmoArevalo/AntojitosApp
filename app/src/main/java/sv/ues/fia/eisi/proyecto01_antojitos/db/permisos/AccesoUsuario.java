package sv.ues.fia.eisi.proyecto01_antojitos.db.permisos;

import android.content.ContentValues;

/** POJO para la tabla ACCESOUSUARIO (PK compuesta). */
public class AccesoUsuario {

    private String idOpcion;   // FK → OPCIONCRUD.ID_OPCION
    private String idUsuario;  // FK → USUARIO.ID_USUARIO

    public AccesoUsuario() {}

    public AccesoUsuario(String idOpcion, String idUsuario) {
        this.idOpcion  = idOpcion;
        this.idUsuario = idUsuario;
    }

    /* getters / setters */
    public String getIdOpcion()  { return idOpcion;  }
    public void   setIdOpcion(String idOpcion)   { this.idOpcion = idOpcion; }

    public String getIdUsuario() { return idUsuario; }
    public void   setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    /** Convierte el objeto a ContentValues listo para insert/update. */
    public ContentValues toCV() {
        ContentValues cv = new ContentValues();
        cv.put("ID_OPCION",  idOpcion);
        cv.put("ID_USUARIO", idUsuario);
        return cv;
    }
}
