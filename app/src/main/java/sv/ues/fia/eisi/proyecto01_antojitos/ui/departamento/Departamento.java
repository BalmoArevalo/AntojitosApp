package sv.ues.fia.eisi.proyecto01_antojitos.ui.departamento;

public class Departamento {

        private int idDepartamento;
        private String nombreDepartamento;

        private int activoDepartamento;

        public int getActivoDepartamento() { return activoDepartamento; }
        public void setActivoDepartamento(int activo) { this.activoDepartamento = activo; }


    // Constructor vacío
        public Departamento() {}

        // Constructor con parámetros
        public Departamento(int idDepartamento, String nombreDepartamento) {
            this.idDepartamento = idDepartamento;
            this.nombreDepartamento = nombreDepartamento;
        }

        // Getters y Setters
        public int getIdDepartamento() {
            return idDepartamento;
        }

        public void setIdDepartamento(int idDepartamento) {
            this.idDepartamento = idDepartamento;
        }

        public String getNombreDepartamento() {
            return nombreDepartamento;
        }

        public void setNombreDepartamento(String nombreDepartamento) {
            this.nombreDepartamento = nombreDepartamento;
        }
}
