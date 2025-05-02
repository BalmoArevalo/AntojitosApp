package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

public class Direccion {

    private int idCliente;
    private int idDireccion;
    private int idDepartamento;
    private int idMunicipio;
    private int idDistrito;
    private String direccionEspecifica;
    private String descripcionDireccion;

    public Direccion() { }

    public Direccion(int idCliente, int idDireccion, int idDepartamento,
                     int idMunicipio, int idDistrito,
                     String direccionEspecifica, String descripcionDireccion) {
        this.idCliente = idCliente;
        this.idDireccion = idDireccion;
        this.idDepartamento = idDepartamento;
        this.idMunicipio = idMunicipio;
        this.idDistrito = idDistrito;
        this.direccionEspecifica = direccionEspecifica;
        this.descripcionDireccion = descripcionDireccion;
    }

    public int getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdDireccion() {
        return idDireccion;
    }
    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
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

    public String getDireccionEspecifica() {
        return direccionEspecifica;
    }
    public void setDireccionEspecifica(String direccionEspecifica) {
        this.direccionEspecifica = direccionEspecifica;
    }

    public String getDescripcionDireccion() {
        return descripcionDireccion;
    }
    public void setDescripcionDireccion(String descripcionDireccion) {
        this.descripcionDireccion = descripcionDireccion;
    }
}
