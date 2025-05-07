package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartidor;

public class Repartidor {
    private int idRepartidor;
    private int idDepartamento;
    private int idMunicipio;
    private int idDistrito;
    private String tipoVehiculo;
    private int disponible;
    private String telefonoRepartidor;
    private String nombreRepartidor;
    private String apellidoRepartidor;
    private int activoRepartidor;

    public Repartidor() {
    }

    /**
     * Constructor para inserción (sin ID).
     */
    public Repartidor(int idDepartamento,
                      int idMunicipio,
                      int idDistrito,
                      String tipoVehiculo,
                      int disponible,
                      String telefonoRepartidor,
                      String nombreRepartidor,
                      String apellidoRepartidor,
                      int activoRepartidor) {
        this.idDepartamento = idDepartamento;
        this.idMunicipio = idMunicipio;
        this.idDistrito = idDistrito;
        this.tipoVehiculo = tipoVehiculo;
        this.disponible = disponible;
        this.telefonoRepartidor = telefonoRepartidor;
        this.nombreRepartidor = nombreRepartidor;
        this.apellidoRepartidor = apellidoRepartidor;
        this.activoRepartidor = activoRepartidor;
    }

    /**
     * Constructor completo (incluye ID).
     */
    public Repartidor(int idRepartidor,
                      int idDepartamento,
                      int idMunicipio,
                      int idDistrito,
                      String tipoVehiculo,
                      int disponible,
                      String telefonoRepartidor,
                      String nombreRepartidor,
                      String apellidoRepartidor,
                      int activoRepartidor) {
        this.idRepartidor = idRepartidor;
        this.idDepartamento = idDepartamento;
        this.idMunicipio = idMunicipio;
        this.idDistrito = idDistrito;
        this.tipoVehiculo = tipoVehiculo;
        this.disponible = disponible;
        this.telefonoRepartidor = telefonoRepartidor;
        this.nombreRepartidor = nombreRepartidor;
        this.apellidoRepartidor = apellidoRepartidor;
        this.activoRepartidor = activoRepartidor;
    }

    // Getters y Setters
    public int getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(int idRepartidor) {
        this.idRepartidor = idRepartidor;
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

    public String getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(String tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public int getDisponible() {
        return disponible;
    }

    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }

    public String getTelefonoRepartidor() {
        return telefonoRepartidor;
    }

    public void setTelefonoRepartidor(String telefonoRepartidor) {
        this.telefonoRepartidor = telefonoRepartidor;
    }

    public String getNombreRepartidor() {
        return nombreRepartidor;
    }

    public void setNombreRepartidor(String nombreRepartidor) {
        this.nombreRepartidor = nombreRepartidor;
    }

    public String getApellidoRepartidor() {
        return apellidoRepartidor;
    }

    public void setApellidoRepartidor(String apellidoRepartidor) {
        this.apellidoRepartidor = apellidoRepartidor;
    }

    public int getActivoRepartidor() {
        return activoRepartidor;
    }

    public void setActivoRepartidor(int activoRepartidor) {
        this.activoRepartidor = activoRepartidor;
    }
}
