package sv.ues.fia.eisi.proyecto01_antojitos.ui.categoriaProducto;


public class CategoriaProducto {
    private int idCategoriaProducto;
    private String nombreCategoria;
    private String descripcionCategoria;
    private int disponibleCategoria;
    private String horaDisponibleDesde;
    private String horaDisponibleHasta;
    private int activoCategoriaProducto;

    // Constructor vacío
    public CategoriaProducto() {
    }

    // Constructor con parámetros
    public CategoriaProducto(int idCategoriaProducto, String nombreCategoria,
                             String descripcionCategoria, int disponibleCategoria,
                             String horaDisponibleDesde, String horaDisponibleHasta,
                             int activoCategoriaProducto) {
        this.idCategoriaProducto = idCategoriaProducto;
        this.nombreCategoria = nombreCategoria;
        this.descripcionCategoria = descripcionCategoria;
        this.disponibleCategoria = disponibleCategoria;
        this.horaDisponibleDesde = horaDisponibleDesde;
        this.horaDisponibleHasta = horaDisponibleHasta;
        this.activoCategoriaProducto = activoCategoriaProducto;
    }

    // Getters y Setters
    public int getIdCategoriaProducto() {
        return idCategoriaProducto;
    }

    public void setIdCategoriaProducto(int idCategoriaProducto) {
        this.idCategoriaProducto = idCategoriaProducto;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getDescripcionCategoria() {
        return descripcionCategoria;
    }

    public void setDescripcionCategoria(String descripcionCategoria) {
        this.descripcionCategoria = descripcionCategoria;
    }

    public int getDisponibleCategoria() {
        return disponibleCategoria;
    }

    public void setDisponibleCategoria(int disponibleCategoria) {
        this.disponibleCategoria = disponibleCategoria;
    }

    public String getHoraDisponibleDesde() {
        return horaDisponibleDesde;
    }

    public void setHoraDisponibleDesde(String horaDisponibleDesde) {
        this.horaDisponibleDesde = horaDisponibleDesde;
    }

    public String getHoraDisponibleHasta() {
        return horaDisponibleHasta;
    }

    public void setHoraDisponibleHasta(String horaDisponibleHasta) {
        this.horaDisponibleHasta = horaDisponibleHasta;
    }

    public int getActivoCategoriaProducto() {
        return activoCategoriaProducto;
    }

    public void setActivoCategoriaProducto(int activoCategoriaProducto) {
        this.activoCategoriaProducto = activoCategoriaProducto;
    }
}