package sv.ues.fia.eisi.proyecto01_antojitos.ui.municipio;


public class Municipio {
    private int idDepartamento;
    private int idMunicipio;
    private String nombreMunicipio;
    private int activoMunicipio = 1;

    public Municipio() {}

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(int idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public String getNombreMunicipio() {
        return nombreMunicipio;
    }

    public void setNombreMunicipio(String nombreMunicipio) {
        this.nombreMunicipio = nombreMunicipio;
    }

    public int getActivoMunicipio() {
        return activoMunicipio;
    }

    public void setActivoMunicipio(int activoMunicipio) {
        this.activoMunicipio = activoMunicipio;
    }

    @Override
    public String toString() {
        return nombreMunicipio;
    }
}
