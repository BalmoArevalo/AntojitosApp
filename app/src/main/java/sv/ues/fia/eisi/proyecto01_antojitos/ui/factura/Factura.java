package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

public class Factura {

    private int idPedido;
    private int idFactura;
    private String fechaEmision; // Considera usar Date o LocalDate si manejas fechas complejas
    private double montoTotal;
    private String tipoPago;
    private int pagado; // 1 para pagado, 0 para no pagado

    // Constructor vacío (necesario para algunas librerías o frameworks)
    public Factura() { }

    // Constructor con todos los campos
    public Factura(int idPedido, int idFactura, String fechaEmision, double montoTotal, String tipoPago, int pagado) {
        this.idPedido = idPedido;
        this.idFactura = idFactura;
        this.fechaEmision = fechaEmision;
        this.montoTotal = montoTotal;
        this.tipoPago = tipoPago;
        this.pagado = pagado;
    }

    // Getters y Setters
    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public int getPagado() {
        return pagado;
    }

    public void setPagado(int pagado) {
        this.pagado = pagado;
    }

    // (Opcional) Metodo toString para fácil depuración
    @Override
    public String toString() {
        return "Factura{" +
                "idPedido=" + idPedido +
                ", idFactura=" + idFactura +
                ", fechaEmision='" + fechaEmision + '\'' +
                ", montoTotal=" + montoTotal +
                ", tipoPago='" + tipoPago + '\'' +
                ", pagado=" + pagado +
                '}';
    }
}