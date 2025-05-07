package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Modelo de Repartidor acorde a la tabla REPARTIDOR en SQLite.
 */
public class Repartidor {

    private int idRepartidor;
    private Integer idDepartamento;  // Puede ser null si no aplica
    private Integer idMunicipio;     // Puede ser null si no aplica
    private Integer idDistrito;      // FK compuesta a DISTRITO
    private String tipoVehiculo;
    private int disponible;          // 1 = disponible, 0 = no disponible
    private String telefonoRepartidor;
    private String nombreRepartidor;
    private String apellidoRepartidor;
    private int activoRepartidor;    // 1 = activo, 0 = inactivo

    /**
     * Constructor vacío.
     */
    public Repartidor() { }

    /**
     * Constructor completo (incluye ID y estado activo).
     */
    public Repartidor(int idRepartidor,
                      Integer idDepartamento,
                      Integer idMunicipio,
                      Integer idDistrito,
                      String tipoVehiculo,
                      int disponible,
                      String telefonoRepartidor,
                      String nombreRepartidor,
                      String apellidoRepartidor,
                      int activoRepartidor) {
        this.idRepartidor     = idRepartidor;
        this.idDepartamento   = idDepartamento;
        this.idMunicipio      = idMunicipio;
        this.idDistrito       = idDistrito;
        this.tipoVehiculo     = tipoVehiculo;
        this.disponible       = disponible;
        this.telefonoRepartidor = telefonoRepartidor;
        this.nombreRepartidor   = nombreRepartidor;
        this.apellidoRepartidor = apellidoRepartidor;
        this.activoRepartidor   = activoRepartidor;
    }

    /**
     * Constructor para inserción (sin ID, activo por defecto a 1).
     */
    public Repartidor(Integer idDepartamento,
                      Integer idMunicipio,
                      Integer idDistrito,
                      String tipoVehiculo,
                      int disponible,
                      String telefonoRepartidor,
                      String nombreRepartidor,
                      String apellidoRepartidor) {
        this(0,
                idDepartamento,
                idMunicipio,
                idDistrito,
                tipoVehiculo,
                disponible,
                telefonoRepartidor,
                nombreRepartidor,
                apellidoRepartidor,
                1);
    }

    // Getters y setters

    public int getIdRepartidor() { return idRepartidor; }
    public void setIdRepartidor(int idRepartidor) { this.idRepartidor = idRepartidor; }

    public Integer getIdDepartamento() { return idDepartamento; }
    public void setIdDepartamento(Integer idDepartamento) { this.idDepartamento = idDepartamento; }

    public Integer getIdMunicipio() { return idMunicipio; }
    public void setIdMunicipio(Integer idMunicipio) { this.idMunicipio = idMunicipio; }

    public Integer getIdDistrito() { return idDistrito; }
    public void setIdDistrito(Integer idDistrito) { this.idDistrito = idDistrito; }

    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }

    public int getDisponible() { return disponible; }
    public void setDisponible(int disponible) { this.disponible = disponible; }

    public String getTelefonoRepartidor() { return telefonoRepartidor; }
    public void setTelefonoRepartidor(String telefonoRepartidor) { this.telefonoRepartidor = telefonoRepartidor; }

    public String getNombreRepartidor() { return nombreRepartidor; }
    public void setNombreRepartidor(String nombreRepartidor) { this.nombreRepartidor = nombreRepartidor; }

    public String getApellidoRepartidor() { return apellidoRepartidor; }
    public void setApellidoRepartidor(String apellidoRepartidor) { this.apellidoRepartidor = apellidoRepartidor; }

    public int getActivoRepartidor() { return activoRepartidor; }
    public void setActivoRepartidor(int activoRepartidor) { this.activoRepartidor = activoRepartidor; }

    /**
     * Convierte la entidad a ContentValues para inserciones/actualizaciones.
     */
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        // ID autogenerado si idRepartidor == 0
        if (idRepartidor > 0) {
            values.put("ID_REPARTIDOR", idRepartidor);
        }
        values.put("ID_DEPARTAMENTO", idDepartamento);
        values.put("ID_MUNICIPIO", idMunicipio);
        values.put("ID_DISTRITO", idDistrito);
        values.put("TIPO_VEHICULO", tipoVehiculo);
        values.put("DISPONIBLE", disponible);
        values.put("TELEFONO_REPARTIDOR", telefonoRepartidor);
        values.put("NOMBRE_REPARTIDOR", nombreRepartidor);
        values.put("APELLIDO_REPARTIDOR", apellidoRepartidor);
        values.put("ACTIVO_REPARTIDOR", activoRepartidor);
        return values;
    }

    /**
     * Crea una instancia de Repartidor a partir de un Cursor.
     */
    public static Repartidor fromCursor(Cursor c) {
        Repartidor r = new Repartidor();
        r.setIdRepartidor(c.getInt(c.getColumnIndexOrThrow("ID_REPARTIDOR")));
        // Puede devolver null si la columna es null
        int idxDept = c.getColumnIndexOrThrow("ID_DEPARTAMENTO");
        r.setIdDepartamento(c.isNull(idxDept) ? null : c.getInt(idxDept));
        int idxMun = c.getColumnIndexOrThrow("ID_MUNICIPIO");
        r.setIdMunicipio(c.isNull(idxMun) ? null : c.getInt(idxMun));
        int idxDist = c.getColumnIndexOrThrow("ID_DISTRITO");
        r.setIdDistrito(c.isNull(idxDist) ? null : c.getInt(idxDist));
        r.setTipoVehiculo(c.getString(c.getColumnIndexOrThrow("TIPO_VEHICULO")));
        r.setDisponible(c.getInt(c.getColumnIndexOrThrow("DISPONIBLE")));
        r.setTelefonoRepartidor(c.getString(c.getColumnIndexOrThrow("TELEFONO_REPARTIDOR")));
        r.setNombreRepartidor(c.getString(c.getColumnIndexOrThrow("NOMBRE_REPARTIDOR")));
        r.setApellidoRepartidor(c.getString(c.getColumnIndexOrThrow("APELLIDO_REPARTIDOR")));
        r.setActivoRepartidor(c.getInt(c.getColumnIndexOrThrow("ACTIVO_REPARTIDOR")));
        return r;
    }

    @Override
    public String toString() {
        return "Repartidor{" +
                "id=" + idRepartidor +
                ", nombre='" + nombreRepartidor + '\'' +
                ", vehiculo='" + tipoVehiculo + '\'' +
                '}';
    }
}