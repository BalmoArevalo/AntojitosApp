package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

public class Direccion {

    // IDs (existentes)
    private int idCliente;
    private int idDireccion;
    private int idDepartamento;
    private int idMunicipio;
    private int idDistrito;
    private String direccionEspecifica;
    private String descripcionDireccion;
    private int activoDireccion;

    // NUEVOS CAMPOS para nombres (se llenarán desde el DAO con JOIN)
    private String nombreDepartamento;
    private String nombreMunicipio;
    private String nombreDistrito;

    // Constructor vacío
    public Direccion() { }

    // Constructor con todos los campos (IDs + Nombres + Otros)
    // Nota: Este constructor se vuelve largo. Considera usar un patrón Builder
    // o confiar más en el constructor vacío y los setters.
    public Direccion(int idCliente, int idDireccion, int idDepartamento, int idMunicipio, int idDistrito,
                     String direccionEspecifica, String descripcionDireccion, int activoDireccion,
                     String nombreDepartamento, String nombreMunicipio, String nombreDistrito) { // <-- Nuevos params
        this.idCliente = idCliente;
        this.idDireccion = idDireccion;
        this.idDepartamento = idDepartamento;
        this.idMunicipio = idMunicipio;
        this.idDistrito = idDistrito;
        this.direccionEspecifica = direccionEspecifica;
        this.descripcionDireccion = descripcionDireccion;
        this.activoDireccion = activoDireccion;
        // Asignar nuevos campos
        this.nombreDepartamento = nombreDepartamento;
        this.nombreMunicipio = nombreMunicipio;
        this.nombreDistrito = nombreDistrito;
    }

    // --- Getters y Setters existentes ---
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    // ... (otros getters/setters existentes para IDs y descripción/activo) ...
    public int getIdDireccion() { return idDireccion; }
    public void setIdDireccion(int idDireccion) { this.idDireccion = idDireccion; }
    public int getIdDepartamento() { return idDepartamento; }
    public void setIdDepartamento(int idDepartamento) { this.idDepartamento = idDepartamento; }
    public int getIdMunicipio() { return idMunicipio; }
    public void setIdMunicipio(int idMunicipio) { this.idMunicipio = idMunicipio; }
    public int getIdDistrito() { return idDistrito; }
    public void setIdDistrito(int idDistrito) { this.idDistrito = idDistrito; }
    public String getDireccionEspecifica() { return direccionEspecifica; }
    public void setDireccionEspecifica(String direccionEspecifica) { this.direccionEspecifica = direccionEspecifica; }
    public String getDescripcionDireccion() { return descripcionDireccion; }
    public void setDescripcionDireccion(String descripcionDireccion) { this.descripcionDireccion = descripcionDireccion; }
    public int getActivoDireccion() { return activoDireccion; }
    public void setActivoDireccion(int activoDireccion) { this.activoDireccion = activoDireccion; }


    // --- NUEVOS Getters y Setters para Nombres ---
    public String getNombreDepartamento() { return nombreDepartamento; }
    public void setNombreDepartamento(String nombreDepartamento) { this.nombreDepartamento = nombreDepartamento; }

    public String getNombreMunicipio() { return nombreMunicipio; }
    public void setNombreMunicipio(String nombreMunicipio) { this.nombreMunicipio = nombreMunicipio; }

    public String getNombreDistrito() { return nombreDistrito; }
    public void setNombreDistrito(String nombreDistrito) { this.nombreDistrito = nombreDistrito; }


    @Override
    public String toString() {
        return "Direccion{" +
                "idCliente=" + idCliente +
                ", idDireccion=" + idDireccion +
                ", idDepartamento=" + idDepartamento +
                ", idMunicipio=" + idMunicipio +
                ", idDistrito=" + idDistrito +
                ", direccionEspecifica='" + direccionEspecifica + '\'' +
                ", descripcionDireccion='" + descripcionDireccion + '\'' +
                ", activoDireccion=" + activoDireccion +
                ", nombreDepartamento='" + nombreDepartamento + '\'' + // <-- Añadido
                ", nombreMunicipio='" + nombreMunicipio + '\'' +   // <-- Añadido
                ", nombreDistrito='" + nombreDistrito + '\'' +     // <-- Añadido
                '}';
    }
}