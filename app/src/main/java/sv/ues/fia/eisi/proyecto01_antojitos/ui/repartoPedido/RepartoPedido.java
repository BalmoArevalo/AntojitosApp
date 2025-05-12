package sv.ues.fia.eisi.proyecto01_antojitos.ui.repartoPedido;

public class RepartoPedido {

    private int idPedido;
    private int idRepartoPedido;
    private String horaAsignacion;
    private String ubicacionEntrega;
    private String fechaHoraEntrega; // Puede ser null

    // Getters y Setters
    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdRepartoPedido() {
        return idRepartoPedido;
    }

    public void setIdRepartoPedido(int idRepartoPedido) {
        this.idRepartoPedido = idRepartoPedido;
    }

    public String getHoraAsignacion() {
        return horaAsignacion;
    }

    public void setHoraAsignacion(String horaAsignacion) {
        this.horaAsignacion = horaAsignacion;
    }

    public String getUbicacionEntrega() {
        return ubicacionEntrega;
    }

    public void setUbicacionEntrega(String ubicacionEntrega) {
        this.ubicacionEntrega = ubicacionEntrega;
    }

    public String getFechaHoraEntrega() {
        return fechaHoraEntrega;
    }

    public void setFechaHoraEntrega(String fechaHoraEntrega) {
        this.fechaHoraEntrega = fechaHoraEntrega;
    }
}
