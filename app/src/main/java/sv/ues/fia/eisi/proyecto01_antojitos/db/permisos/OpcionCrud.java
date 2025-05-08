package sv.ues.fia.eisi.proyecto01_antojitos.db.permisos;

import android.content.ContentValues;

/** POJO para la tabla OPCIONCRUD. */
public class OpcionCrud {

    private String  idOpcion;   // “cliente_crear”, “producto_consultar”, etc.
    private String  desOpcion;  // Texto legible: “Crear Cliente”
    private int     numCrud;    // 1-Crear, 2-Consultar, 3-Editar, 4-Eliminar, 0-Wildcard

    public OpcionCrud() {}

    public OpcionCrud(String idOpcion, String desOpcion, int numCrud) {
        this.idOpcion  = idOpcion;
        this.desOpcion = desOpcion;
        this.numCrud   = numCrud;
    }

    /* ===== getters / setters ===== */
    public String getIdOpcion()           { return idOpcion;  }
    public void   setIdOpcion(String v)   { idOpcion = v;     }

    public String getDesOpcion()          { return desOpcion; }
    public void   setDesOpcion(String v)  { desOpcion = v;    }

    public int    getNumCrud()            { return numCrud;   }
    public void   setNumCrud(int v)       { numCrud = v;      }

    /** Convierte la entidad en ContentValues para insert/update. */
    public ContentValues toCV() {
        ContentValues cv = new ContentValues();
        cv.put("ID_OPCION",  idOpcion);
        cv.put("DES_OPCION", desOpcion);
        cv.put("NUM_CRUD",   numCrud);
        return cv;
    }
}
