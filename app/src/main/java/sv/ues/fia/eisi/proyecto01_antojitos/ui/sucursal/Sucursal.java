package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Modelo de Sucursal para uso con SQLite en Android.
 */
public class Sucursal {

    private int idSucursal;
    private int idDepartamento;
    private int idMunicipio;
    private int idDistrito;
    private String nombreSucursal;
    private String direccionSucursal;
    private String telefonoSucursal;
    private String horarioApertura;     // HORARIO_APERTURA_SUCURSAL
    private String horarioCierre;       // HORARIO_CIERRE_SUCURSAL
    private int activoSucursal;         // ACTIVO_SUCURSAL (1 = activo, 0 = inactivo)

    public Sucursal() {
    }

    /**
     * Constructor completo incluyendo estado activo.
     */
    public Sucursal(int idSucursal,
                    int idDepartamento,
                    int idMunicipio,
                    int idDistrito,
                    String nombreSucursal,
                    String direccionSucursal,
                    String telefonoSucursal,
                    String horarioApertura,
                    String horarioCierre,
                    int activoSucursal) {
        this.idSucursal = idSucursal;
        this.idDepartamento = idDepartamento;
        this.idMunicipio = idMunicipio;
        this.idDistrito = idDistrito;
        this.nombreSucursal = nombreSucursal;
        this.direccionSucursal = direccionSucursal;
        this.telefonoSucursal = telefonoSucursal;
        this.horarioApertura = horarioApertura;
        this.horarioCierre = horarioCierre;
        this.activoSucursal = activoSucursal;
    }

    /**
     * Constructor para inserciones (sin id, activo por defecto a 1).
     */
    public Sucursal(int idDepartamento,
                    int idMunicipio,
                    int idDistrito,
                    String nombreSucursal,
                    String direccionSucursal,
                    String telefonoSucursal,
                    String horarioApertura,
                    String horarioCierre) {
        this(0, idDepartamento, idMunicipio, idDistrito,
                nombreSucursal, direccionSucursal,
                telefonoSucursal, horarioApertura, horarioCierre,
                1);
    }

    // Getters y setters

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(int idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public int getIdDistrito() {
        return idDistrito;
    }

    public void setIdDistrito(int idDistrito) {
        this.idDistrito = idDistrito;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public String getDireccionSucursal() {
        return direccionSucursal;
    }

    public void setDireccionSucursal(String direccionSucursal) {
        this.direccionSucursal = direccionSucursal;
    }

    public String getTelefonoSucursal() {
        return telefonoSucursal;
    }

    public void setTelefonoSucursal(String telefonoSucursal) {
        this.telefonoSucursal = telefonoSucursal;
    }

    public String getHorarioApertura() {
        return horarioApertura;
    }

    public void setHorarioApertura(String horarioApertura) {
        this.horarioApertura = horarioApertura;
    }

    public String getHorarioCierre() {
        return horarioCierre;
    }

    public void setHorarioCierre(String horarioCierre) {
        this.horarioCierre = horarioCierre;
    }

    public int getActivoSucursal() {
        return activoSucursal;
    }

    public void setActivoSucursal(int activoSucursal) {
        this.activoSucursal = activoSucursal;
    }

    /**
     * Convierte la entidad a ContentValues para inserción/actualización en SQLite.
     */
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        // No incluir ID_SUCURSAL si es cero (auto-incremental)
        if (idSucursal > 0) {
            values.put("ID_SUCURSAL", idSucursal);
        }
        values.put("ID_DEPARTAMENTO", idDepartamento);
        values.put("ID_MUNICIPIO", idMunicipio);
        values.put("ID_DISTRITO", idDistrito);
        values.put("NOMBRE_SUCURSAL", nombreSucursal);
        values.put("DIRECCION_SUCURSAL", direccionSucursal);
        values.put("TELEFONO_SUCURSAL", telefonoSucursal);
        values.put("HORARIO_APERTURA_SUCURSAL", horarioApertura);
        values.put("HORARIO_CIERRE_SUCURSAL", horarioCierre);
        values.put("ACTIVO_SUCURSAL", activoSucursal);
        return values;
    }

    /**
     * Crea una instancia de Sucursal a partir de un Cursor de consulta.
     */
    public static Sucursal fromCursor(Cursor cursor) {
        int idxId = cursor.getColumnIndex("ID_SUCURSAL");
        int idxDept = cursor.getColumnIndex("ID_DEPARTAMENTO");
        int idxMun = cursor.getColumnIndex("ID_MUNICIPIO");
        int idxDist = cursor.getColumnIndex("ID_DISTRITO");
        int idxNombre = cursor.getColumnIndex("NOMBRE_SUCURSAL");
        int idxDir = cursor.getColumnIndex("DIRECCION_SUCURSAL");
        int idxTel = cursor.getColumnIndex("TELEFONO_SUCURSAL");
        int idxApert = cursor.getColumnIndex("HORARIO_APERTURA_SUCURSAL");
        int idxCierre = cursor.getColumnIndex("HORARIO_CIERRE_SUCURSAL");
        int idxAct = cursor.getColumnIndex("ACTIVO_SUCURSAL");

        Sucursal sucursal = new Sucursal();
        if (idxId != -1) {
            sucursal.setIdSucursal(cursor.getInt(idxId));
        }
        sucursal.setIdDepartamento(cursor.getInt(idxDept));
        sucursal.setIdMunicipio(cursor.getInt(idxMun));
        sucursal.setIdDistrito(cursor.getInt(idxDist));
        sucursal.setNombreSucursal(cursor.getString(idxNombre));
        sucursal.setDireccionSucursal(cursor.getString(idxDir));
        sucursal.setTelefonoSucursal(cursor.getString(idxTel));
        sucursal.setHorarioApertura(cursor.getString(idxApert));
        sucursal.setHorarioCierre(cursor.getString(idxCierre));
        sucursal.setActivoSucursal(cursor.getInt(idxAct));
        return sucursal;
    }

    @Override
    public String toString() {
        return "Sucursal{" +
                "idSucursal=" + idSucursal +
                ", nombre='" + nombreSucursal + '\'' +
                ", depto=" + idDepartamento +
                ", municipio=" + idMunicipio +
                ", distrito=" + idDistrito +
                '}';
    }
}
