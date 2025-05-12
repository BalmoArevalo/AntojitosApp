package sv.ues.fia.eisi.proyecto01_antojitos.ui.tipoEvento; // Ajusta el paquete si es necesario

public class TipoEvento {

    private int idTipoEvento;
    private String nombreTipoEvento;
    private String descripcionTipoEvento;
    private double montoMinimo;
    private double montoMaximo;
    private int activoTipoEvento; // 1 para activo, 0 para inactivo

    // Constructor vacío
    public TipoEvento() {
    }

    // Constructor con todos los campos
    public TipoEvento(int idTipoEvento, String nombreTipoEvento, String descripcionTipoEvento,
                      double montoMinimo, double montoMaximo, int activoTipoEvento) {
        this.idTipoEvento = idTipoEvento;
        this.nombreTipoEvento = nombreTipoEvento;
        this.descripcionTipoEvento = descripcionTipoEvento;
        this.montoMinimo = montoMinimo;
        this.montoMaximo = montoMaximo;
        this.activoTipoEvento = activoTipoEvento;
    }

    // Getters y Setters
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

    public int getActivoTipoEvento() {
        return activoTipoEvento;
    }

    public void setActivoTipoEvento(int activoTipoEvento) {
        this.activoTipoEvento = activoTipoEvento;
    }

    // Método toString() para facilitar la depuración y visualización (opcional en Spinners)
    @Override
    public String toString() {
        // Podrías retornar solo nombreTipoEvento si lo vas a usar directamente en un ArrayAdapter para Spinner
        // return nombreTipoEvento;
        return "TipoEvento{" +
                "idTipoEvento=" + idTipoEvento +
                ", nombreTipoEvento='" + nombreTipoEvento + '\'' +
                ", descripcionTipoEvento='" + descripcionTipoEvento + '\'' +
                ", montoMinimo=" + montoMinimo +
                ", montoMaximo=" + montoMaximo +
                ", activoTipoEvento=" + activoTipoEvento +
                '}';
    }
}