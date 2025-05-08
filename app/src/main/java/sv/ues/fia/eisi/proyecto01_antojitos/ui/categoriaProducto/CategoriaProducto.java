package sv.ues.fia.eisi.proyecto01_antojitos.ui.categoriaProducto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CategoriaProducto {
    private int idCategoriaProducto;
    private String nombreCategoria;
    private String descripcionCategoria;
    private int disponibleCategoria;
    private String horaDisponibleDesde;
    private String horaDisponibleHasta;
    private int activoCategoriaProducto;

    // Constructor vacío
    public CategoriaProducto() {}

    // Constructor con parámetros (sin id)
    public CategoriaProducto(String nombreCategoria, String descripcionCategoria, int disponibleCategoria,
                             String horaDisponibleDesde, String horaDisponibleHasta, int activoCategoriaProducto) {
        this.nombreCategoria = nombreCategoria;
        this.descripcionCategoria = descripcionCategoria;
        this.disponibleCategoria = disponibleCategoria;
        this.horaDisponibleDesde = horaDisponibleDesde;
        this.horaDisponibleHasta = horaDisponibleHasta;
        this.activoCategoriaProducto = activoCategoriaProducto;
    }

    public boolean estaDisponibleAhora() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date desde = sdf.parse(horaDisponibleDesde);
            Date hasta = sdf.parse(horaDisponibleHasta);
            Date ahora = sdf.parse(sdf.format(new Date())); // solo la hora actual

            // Verifica si "ahora" está dentro del rango
            return (ahora.equals(desde) || ahora.equals(hasta) || (ahora.after(desde) && ahora.before(hasta)));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Getters y setters
    public int getIdCategoriaProducto() { return idCategoriaProducto; }
    public void setIdCategoriaProducto(int idCategoriaProducto) { this.idCategoriaProducto = idCategoriaProducto; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    public String getDescripcionCategoria() { return descripcionCategoria; }
    public void setDescripcionCategoria(String descripcionCategoria) { this.descripcionCategoria = descripcionCategoria; }

    public int getDisponibleCategoria() { return disponibleCategoria; }
    public void setDisponibleCategoria(int disponibleCategoria) { this.disponibleCategoria = disponibleCategoria; }

    public String getHoraDisponibleDesde() { return horaDisponibleDesde; }
    public void setHoraDisponibleDesde(String horaDisponibleDesde) { this.horaDisponibleDesde = horaDisponibleDesde; }

    public String getHoraDisponibleHasta() { return horaDisponibleHasta; }
    public void setHoraDisponibleHasta(String horaDisponibleHasta) { this.horaDisponibleHasta = horaDisponibleHasta; }

    public int getActivoCategoriaProducto() { return activoCategoriaProducto; }
    public void setActivoCategoriaProducto(int activoCategoriaProducto) { this.activoCategoriaProducto = activoCategoriaProducto; }
}
