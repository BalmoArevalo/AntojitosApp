package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento;

public class TipoEvento {
    private int idTipoEvento;
    private String nombreTipoEvento;
    private String descripcionTipoEvento;
    private double montoMinimo;
    private double montoMaximo;

    public TipoEvento() {}

    public int getIdTipoEvento() {
        return idTipoEvento;
    }

    public void setIdTipoEvento(int idTipoEvento) {
        this.idTipoEvento = idTipoEvento;
    }

    public String getNombreTipoEvento() {
        return nombreTipoEvento;
    }

    public void setNombreTipoEvento(String nombreTipoEvento) {
        this.nombreTipoEvento = nombreTipoEvento;
    }

    public String getDescripcionTipoEvento() {
        return descripcionTipoEvento;
    }

    public void setDescripcionTipoEvento(String descripcionTipoEvento) {
        this.descripcionTipoEvento = descripcionTipoEvento;
    }

    public double getMontoMinimo() {
        return montoMinimo;
    }

    public void setMontoMinimo(double montoMinimo) {
        this.montoMinimo = montoMinimo;
    }

    public double getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(double montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    @Override
    public String toString() {
        return nombreTipoEvento;
    }
}
