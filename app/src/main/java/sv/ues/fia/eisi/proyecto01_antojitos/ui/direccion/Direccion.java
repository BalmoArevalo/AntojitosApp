package sv.ues.fia.eisi.proyecto01_antojitos.ui.direccion;

public class Direccion {

    private int idCliente;
    private int idDireccion;
    private int idDepartamento;
    private int idMunicipio;
    private int idDistrito;
    private String direccionEspecifica;
    private String descripcionDireccion;
    private int activoDireccion; // NUEVO CAMPO para ACTIVO_DIRECCION (0=inactivo, 1=activo)

    // Constructor vacío
    public Direccion() { }

    // Constructor con todos los campos (INCLUYENDO el nuevo campo activoDireccion)
    public Direccion(int idCliente, int idDireccion, int idDepartamento,
                     int idMunicipio, int idDistrito,
                     String direccionEspecifica, String descripcionDireccion,
                     int activoDireccion) { // <-- Nuevo parámetro
        this.idCliente = idCliente;
        this.idDireccion = idDireccion;
        this.idDepartamento = idDepartamento;
        this.idMunicipio = idMunicipio;
        this.idDistrito = idDistrito;
        this.direccionEspecifica = direccionEspecifica;
        this.descripcionDireccion = descripcionDireccion;
        this.activoDireccion = activoDireccion; // <-- Asignar nuevo campo
    }

    // --- Getters y Setters existentes ---
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

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

    // --- NUEVO Getter y Setter para activoDireccion ---
    public int getActivoDireccion() {
        return activoDireccion;
    }
    public void setActivoDireccion(int activoDireccion) {
        // Podrías añadir validación si quieres asegurar que solo sea 0 o 1
        this.activoDireccion = activoDireccion;
    }

    // (Opcional) Podrías añadir el nuevo campo a un método toString() para depuración
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
                ", activoDireccion=" + activoDireccion + // <-- Añadido a toString
                '}';
    }
}