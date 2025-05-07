package sv.ues.fia.eisi.proyecto01_antojitos.ui.detallePedido;

public class DetallePedido {
    private int idDetallePedido;
    private int idProducto;
    private int idPedido;
    private int cantidad;
    private double subtotal;

    // Getters y Setters
    public int getIdDetallePedido() { return idDetallePedido; }
    public void setIdDetallePedido(int idDetallePedido) { this.idDetallePedido = idDetallePedido; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
