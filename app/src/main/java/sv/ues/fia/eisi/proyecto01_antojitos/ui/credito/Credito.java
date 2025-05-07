package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito; // Asegúrate que la ruta del paquete sea correcta

public class Credito {

    private int idCredito;         // PK Autoincremental: ID_CREDITO
    private int idFactura;         // FK a FACTURA (UNIQUE): ID_FACTURA
    private double montoAutorizadoCredito; // MONTO_AUTORIZADO_CREDITO
    private double montoPagado;           // MONTO_PAGADO
    private double saldoPendiente;        // SALDO_PENDIENTE
    private String fechaLimitePago;       // FECHA_LIMITE_PAGO (Formato YYYY-MM-DD)
    private String estadoCredito;         // ESTADO_CREDITO (Ej: "Activo", "Pagado", "Vencido")

    // Constructor vacío (importante para algunas librerías y frameworks)
    public Credito() {
    }

    // Constructor con todos los campos (útil para crear instancias rápidamente)
    public Credito(int idCredito, int idFactura, double montoAutorizadoCredito,
                   double montoPagado, double saldoPendiente, String fechaLimitePago,
                   String estadoCredito) {
        this.idCredito = idCredito;
        this.idFactura = idFactura;
        this.montoAutorizadoCredito = montoAutorizadoCredito;
        this.montoPagado = montoPagado;
        this.saldoPendiente = saldoPendiente;
        this.fechaLimitePago = fechaLimitePago;
        this.estadoCredito = estadoCredito;
    }

    // --- Getters y Setters para todos los campos ---

    public int getIdCredito() {
        return idCredito;
    }

    public void setIdCredito(int idCredito) {
        this.idCredito = idCredito;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public double getMontoAutorizadoCredito() {
        return montoAutorizadoCredito;
    }

    public void setMontoAutorizadoCredito(double montoAutorizadoCredito) {
        this.montoAutorizadoCredito = montoAutorizadoCredito;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(double montoPagado) {
        // Podrías añadir validación aquí si quieres (ej. no negativo)
        this.montoPagado = montoPagado;
    }

    public double getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(double saldoPendiente) {
        // Podrías añadir validación aquí (ej. no negativo)
        this.saldoPendiente = saldoPendiente;
    }

    public String getFechaLimitePago() {
        return fechaLimitePago;
    }

    public void setFechaLimitePago(String fechaLimitePago) {
        this.fechaLimitePago = fechaLimitePago;
    }

    public String getEstadoCredito() {
        return estadoCredito;
    }

    public void setEstadoCredito(String estadoCredito) {
        // Podrías validar los estados permitidos si lo deseas
        this.estadoCredito = estadoCredito;
    }

    // (Opcional pero recomendado) Método toString() para fácil depuración
    @Override
    public String toString() {
        return "Credito{" +
                "idCredito=" + idCredito +
                ", idFactura=" + idFactura +
                ", montoAutorizadoCredito=" + montoAutorizadoCredito +
                ", montoPagado=" + montoPagado +
                ", saldoPendiente=" + saldoPendiente +
                ", fechaLimitePago='" + fechaLimitePago + '\'' +
                ", estadoCredito='" + estadoCredito + '\'' +
                '}';
    }
}