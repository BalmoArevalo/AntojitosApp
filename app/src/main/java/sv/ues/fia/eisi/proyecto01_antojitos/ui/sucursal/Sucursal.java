package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

public class Sucursal {

    private int idSucursal;
    private int idDepartamento;
    private int idMunicipio;
    private int idDistrito;
    private String nombreSucursal;
    private String direccionSucursal;
    private String telefonoSucursal;
    private String horarioApertura;     // corresponde a HORARIO_APERTURA_SUCURSAL
    private String horarioCierre;       // corresponde a HORARIO_CIERRE_SUCURSAL
    private int activoSucursal;         // corresponde a ACTIVO_SUCURSAL (1 = activo, 0 = inactivo)

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
     * Constructor sin id (para inserciones) y con activo por defecto a 1.
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
}
