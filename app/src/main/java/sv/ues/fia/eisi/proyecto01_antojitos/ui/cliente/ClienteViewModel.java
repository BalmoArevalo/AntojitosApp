package sv.ues.fia.eisi.proyecto01_antojitos.ui.cliente;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class ClienteViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Cliente>> listaClientes;
    private final MutableLiveData<Cliente> clienteSeleccionado;
    private final MutableLiveData<String> mensajeError;
    private final MutableLiveData<Boolean> operacionExitosa;
    private final ClienteDAO clienteDAO;
    private final SQLiteDatabase db;

    public ClienteViewModel(@NonNull Application application) {
        super(application);
        listaClientes = new MutableLiveData<>();
        clienteSeleccionado = new MutableLiveData<>();
        mensajeError = new MutableLiveData<>();
        operacionExitosa = new MutableLiveData<>();

        DBHelper dbHelper = new DBHelper(application);
        db = dbHelper.getWritableDatabase();
        clienteDAO = new ClienteDAO(db);

        // Cargar datos iniciales
        cargarClientes();
    }

    // Getters para LiveData
    public LiveData<List<Cliente>> getClientes() {
        return listaClientes;
    }

    public LiveData<Cliente> getClienteSeleccionado() {
        return clienteSeleccionado;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<Boolean> getOperacionExitosa() {
        return operacionExitosa;
    }

    // CREATE
    public void crearCliente(String nombre, String apellido, String telefono) {
        try {
            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setNombreCliente(nombre);
            nuevoCliente.setApellidoCliente(apellido);
            nuevoCliente.setTelefonoCliente(telefono);
            nuevoCliente.setActivoCliente(1);

            if (validarDatosCliente(nuevoCliente)) {
                long resultado = clienteDAO.insertar(nuevoCliente);
                if (resultado != -1) {
                    operacionExitosa.setValue(true);
                    cargarClientes();
                } else {
                    mensajeError.setValue("Error al crear el cliente");
                    operacionExitosa.setValue(false);
                }
            }
        } catch (Exception e) {
            mensajeError.setValue("Error: " + e.getMessage());
            operacionExitosa.setValue(false);
        }
    }

    // READ
    public void cargarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.obtenerTodos();
            listaClientes.setValue(clientes);
        } catch (Exception e) {
            mensajeError.setValue("Error al cargar clientes: " + e.getMessage());
        }
    }

    public void buscarClientePorId(int idCliente) {
        try {
            Cliente cliente = clienteDAO.consultarPorId(idCliente);
            if (cliente != null) {
                clienteSeleccionado.setValue(cliente);
                operacionExitosa.setValue(true);
            } else {
                mensajeError.setValue("Cliente no encontrado");
                operacionExitosa.setValue(false);
            }
        } catch (Exception e) {
            mensajeError.setValue("Error en la búsqueda: " + e.getMessage());
            operacionExitosa.setValue(false);
        }
    }

    // UPDATE
    public void actualizarCliente(int idCliente, String nombre, String apellido, String telefono) {
        try {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(idCliente);
            cliente.setNombreCliente(nombre);
            cliente.setApellidoCliente(apellido);
            cliente.setTelefonoCliente(telefono);

            if (validarDatosCliente(cliente)) {
                int filasAfectadas = clienteDAO.actualizar(cliente);
                if (filasAfectadas > 0) {
                    operacionExitosa.setValue(true);
                    cargarClientes();
                } else {
                    mensajeError.setValue("No se pudo actualizar el cliente");
                    operacionExitosa.setValue(false);
                }
            }
        } catch (Exception e) {
            mensajeError.setValue("Error al actualizar: " + e.getMessage());
            operacionExitosa.setValue(false);
        }
    }

    // DELETE
    public void eliminarCliente(int idCliente) {
        try {
            int filasAfectadas = clienteDAO.eliminar(idCliente);
            if (filasAfectadas > 0) {
                operacionExitosa.setValue(true);
                cargarClientes();
            } else {
                mensajeError.setValue("No se pudo eliminar el cliente");
                operacionExitosa.setValue(false);
            }
        } catch (Exception e) {
            mensajeError.setValue("Error al eliminar: " + e.getMessage());
            operacionExitosa.setValue(false);
        }
    }

    // Validaciones
    private boolean validarDatosCliente(Cliente cliente) {
        if (cliente.getNombreCliente() == null || cliente.getNombreCliente().trim().isEmpty()) {
            mensajeError.setValue("El nombre del cliente es requerido");
            return false;
        }

        if (cliente.getApellidoCliente() == null || cliente.getApellidoCliente().trim().isEmpty()) {
            mensajeError.setValue("El apellido del cliente es requerido");
            return false;
        }

        if (cliente.getTelefonoCliente() == null || cliente.getTelefonoCliente().trim().isEmpty()) {
            mensajeError.setValue("El teléfono del cliente es requerido");
            return false;
        }

        // Modificar la validación del teléfono para aceptar 8 o 9 caracteres
        String telefono = cliente.getTelefonoCliente().trim();
        if (telefono.length() != 8 && telefono.length() != 9) {
            mensajeError.setValue("El teléfono debe tener 8 o 9 caracteres");
            return false;
        }

        return true;
    }

    // Búsqueda por filtro
    public void buscarClientesPorFiltro(String filtro) {
        try {
            if (filtro != null && !filtro.trim().isEmpty()) {
                List<Cliente> clientesFiltrados = clienteDAO.buscarPorNombreOTelefono(filtro);
                listaClientes.setValue(clientesFiltrados);
            } else {
                cargarClientes();
            }
        } catch (Exception e) {
            mensajeError.setValue("Error en la búsqueda: " + e.getMessage());
        }
    }

    // Limpiar mensaje de error
    public void limpiarError() {
        mensajeError.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}