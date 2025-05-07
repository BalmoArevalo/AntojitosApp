package sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente;

public class Cliente {
    private int idCliente;
    private String telefonoCliente;
    private String nombreCliente;
    private String apellidoCliente;
    private int activoCliente;

    // Constructor por defecto
    public Cliente() {
    }

    // Constructor con parámetros
    public Cliente(int idCliente, String telefonoCliente,
                   String nombreCliente, String apellidoCliente, int activoCliente) {
        this.idCliente = idCliente;
        this.telefonoCliente = telefonoCliente;
        this.nombreCliente = nombreCliente;
        this.apellidoCliente = apellidoCliente;
        this.activoCliente = activoCliente;
    }

    // Getters y Setters
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getApellidoCliente() {
        return apellidoCliente;
    }

    public void setApellidoCliente(String apellidoCliente) {
        this.apellidoCliente = apellidoCliente;
    }

    public int getActivoCliente() {
        return activoCliente;
    }

    public void setActivoCliente(int activoCliente) {
        this.activoCliente = activoCliente;
    }

    @Override
    public String toString() {
        return nombreCliente + " " + apellidoCliente;
    }
}