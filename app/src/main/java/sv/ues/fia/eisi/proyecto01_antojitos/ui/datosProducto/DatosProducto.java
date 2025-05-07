package sv.ues.fia.eisi.proyecto01_antojitos.ui.datosProducto;

public class DatosProducto {
    private int idSucursal;
    private int idProducto;
    private double precioSucursalProducto;
    private int stock;
    private boolean activo;

    public DatosProducto(int idSucursal, int idProducto, double precioSucursalProducto, int stock, boolean activo) {
        this.idSucursal = idSucursal;
        this.idProducto = idProducto;
        this.precioSucursalProducto = precioSucursalProducto;
        this.stock = stock;
        this.activo = activo;
    }

    // Getters y Setters
    public int getIdSucursal() { return idSucursal; }
    public void setIdSucursal(int idSucursal) { this.idSucursal = idSucursal; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public double getPrecioSucursalProducto() { return precioSucursalProducto; }
    public void setPrecioSucursalProducto(double precioSucursalProducto) { this.precioSucursalProducto = precioSucursalProducto; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
