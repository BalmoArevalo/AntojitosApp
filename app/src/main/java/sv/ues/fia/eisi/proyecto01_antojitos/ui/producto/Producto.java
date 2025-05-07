package sv.ues.fia.eisi.proyecto01_antojitos.ui.producto;

public class Producto {
    private int idProducto;
    private int idCategoriaProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private int activoProducto;

    // Constructor vacío
    public Producto() {
    }

    // Constructor con parámetros
    public Producto(int idProducto, int idCategoriaProducto, String nombreProducto,
                    String descripcionProducto, int activoProducto) {
        this.idProducto = idProducto;
        this.idCategoriaProducto = idCategoriaProducto;
        this.nombreProducto = nombreProducto;
        this.descripcionProducto = descripcionProducto;
        this.activoProducto = activoProducto;
    }

    // Getters y Setters
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdCategoriaProducto() {
        return idCategoriaProducto;
    }

    public void setIdCategoriaProducto(int idCategoriaProducto) {
        this.idCategoriaProducto = idCategoriaProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public int getActivoProducto() {
        return activoProducto;
    }

    public void setActivoProducto(int activoProducto) {
        this.activoProducto = activoProducto;
    }
}