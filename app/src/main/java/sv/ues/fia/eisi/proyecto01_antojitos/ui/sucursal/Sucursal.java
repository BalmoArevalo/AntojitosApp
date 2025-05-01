package sv.ues.fia.eisi.proyecto01_antojitos.ui.sucursal;

public class Sucursal {

    private int idSucursal;
    private int idDepartamento;
    private int idMunicipio;
    private int idDistrito;
    private int idUsuario;
    private String nombreSucursal;
    private String direccionSucursal;
    private String telefonoSucursal;
    private String horarioApertura;
    private String horarioCierre;

    public Sucursal() {
    }

    public Sucursal(int idSucursal, int idDepartamento, int idMunicipio, int idDistrito,
                    int idUsuario, String nombreSucursal, String direccionSucursal,
                    String telefonoSucursal, String horarioApertura, String horarioCierre) {
        this.idSucursal = idSucursal;
        this.idDepartamento = idDepartamento;
        this.idMunicipio = idMunicipio;
        this.idDistrito = idDistrito;
        this.idUsuario = idUsuario;
        this.nombreSucursal = nombreSucursal;
        this.direccionSucursal = direccionSucursal;
        this.telefonoSucursal = telefonoSucursal;
        this.horarioApertura = horarioApertura;
        this.horarioCierre = horarioCierre;
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

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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
}
