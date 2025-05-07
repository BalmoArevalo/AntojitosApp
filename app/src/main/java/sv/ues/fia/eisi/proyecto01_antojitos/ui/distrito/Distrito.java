package sv.ues.fia.eisi.proyecto01_antojitos.ui.distrito;

public class Distrito {
    private int idDepartamento;
    private int idMunicipio;
    private int idDistrito;
    private String nombreDistrito;
    private String codigoPostal;
    private int activoDistrito;

    // Constructor vacío
    public Distrito() {
    }

    // Constructor con todos los campos
    public Distrito(int idDepartamento, int idMunicipio, int idDistrito,
                    String nombreDistrito, String codigoPostal, int activoDistrito) {
        this.idDepartamento = idDepartamento;
        this.idMunicipio = idMunicipio;
        this.idDistrito = idDistrito;
        this.nombreDistrito = nombreDistrito;
        this.codigoPostal = codigoPostal;
        this.activoDistrito = activoDistrito;
    }

    // Getters y Setters
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

    public String getNombreDistrito() {
        return nombreDistrito;
    }

    public void setNombreDistrito(String nombreDistrito) {
        this.nombreDistrito = nombreDistrito;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public int getActivoDistrito() {
        return activoDistrito;
    }

    public void setActivoDistrito(int activoDistrito) {
        this.activoDistrito = activoDistrito;
    }

    // Método toString para representación en string del objeto
    @Override
    public String toString() {
        return "Distrito{" +
                "idDepartamento=" + idDepartamento +
                ", idMunicipio=" + idMunicipio +
                ", idDistrito=" + idDistrito +
                ", nombreDistrito='" + nombreDistrito + '\'' +
                ", codigoPostal='" + codigoPostal + '\'' +
                ", activoDistrito=" + activoDistrito +
                '}';
    }
}