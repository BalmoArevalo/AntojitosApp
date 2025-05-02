package sv.ues.fia.eisi.proyecto01_antojitos.ui.pedido;

public class Pedido {

    private int idPedido;
    private int idCliente;
    private int facIdPedido;
    private int idFactura;
    private int idRepartidor;
    private int repIdPedido;
    private int idRepartoPedido;
    private int idTipoEvento;
    private String fechaHoraPedido;
    private String estadoPedido;

    // Getters y setters

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getFacIdPedido() { return facIdPedido; }
    public void setFacIdPedido(int facIdPedido) { this.facIdPedido = facIdPedido; }

    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }

    public int getIdRepartidor() { return idRepartidor; }
    public void setIdRepartidor(int idRepartidor) { this.idRepartidor = idRepartidor; }

    public int getRepIdPedido() { return repIdPedido; }
    public void setRepIdPedido(int repIdPedido) { this.repIdPedido = repIdPedido; }

    public int getIdRepartoPedido() { return idRepartoPedido; }
    public void setIdRepartoPedido(int idRepartoPedido) { this.idRepartoPedido = idRepartoPedido; }

    public int getIdTipoEvento() { return idTipoEvento; }
    public void setIdTipoEvento(int idTipoEvento) { this.idTipoEvento = idTipoEvento; }

    public String getFechaHoraPedido() { return fechaHoraPedido; }
    public void setFechaHoraPedido(String fechaHoraPedido) { this.fechaHoraPedido = fechaHoraPedido; }

    public String getEstadoPedido() { return estadoPedido; }
    public void setEstadoPedido(String estadoPedido) { this.estadoPedido = estadoPedido; }
}
