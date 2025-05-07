package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

// Considera importar clases de Fecha si manejarás fechas de forma más compleja
// import java.util.Date;

public class Factura {

    // Columnas de la tabla FACTURA según DBHelper v4
    private int idFactura;       // PK Autoincremental
    private int idPedido;        // FK a Pedido (UNIQUE para 1:1)
    private String fechaEmision; // Formato YYYY-MM-DD generalmente
    private double montoTotal;
    private String tipoPago;     // Ej: "Contado", "Crédito"
    private String estadoFactura;// Ej: "Pendiente", "Pagada", "En Crédito", "Anulada"
    private int esCredito;       // 0 = No es crédito, 1 = Sí es crédito

    // Constructor vacío (necesario para algunas operaciones)
    public Factura() { }

    // Constructor con todos los campos (opcional pero útil)
    public Factura(int idFactura, int idPedido, String fechaEmision, double montoTotal,
                   String tipoPago, String estadoFactura, int esCredito) {
        this.idFactura = idFactura;
        this.idPedido = idPedido;
        this.fechaEmision = fechaEmision;
        this.montoTotal = montoTotal;
        this.tipoPago = tipoPago;
        this.estadoFactura = estadoFactura;
        this.esCredito = esCredito;
    }

    // Getters y Setters para todos los campos

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
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

    public String getEstadoFactura() {
        return estadoFactura;
    }

    public void setEstadoFactura(String estadoFactura) {
        this.estadoFactura = estadoFactura;
    }

    public int getEsCredito() {
        return esCredito;
    }

    public void setEsCredito(int esCredito) {
        // Podrías añadir validación para que solo sea 0 o 1 si quieres
        this.esCredito = esCredito;
    }

    // (Opcional) Método toString() para depuración
    @Override
    public String toString() {
        return "Factura{" +
                "idFactura=" + idFactura +
                ", idPedido=" + idPedido +
                ", fechaEmision='" + fechaEmision + '\'' +
                ", montoTotal=" + montoTotal +
                ", tipoPago='" + tipoPago + '\'' +
                ", estadoFactura='" + estadoFactura + '\'' +
                ", esCredito=" + esCredito +
                '}';
    }
}