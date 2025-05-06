package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections; // Para devolver lista vacía segura
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper; // Asegúrate que la ruta sea correcta

public class FacturaViewModel extends AndroidViewModel {

    // LiveData para la lista de facturas (observable por la UI)
    private final MutableLiveData<List<Factura>> listaFacturas = new MutableLiveData<>();
    // LiveData para una factura individual (útil para vistas de detalle)
    private final MutableLiveData<Factura> facturaSeleccionada = new MutableLiveData<>();

    // DBHelper y DAO (se instancian cuando se necesitan)
    private DBHelper dbHelper;

    // Constructor
    public FacturaViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DBHelper(application); // Crear instancia del DBHelper
    }

    // --- Métodos para exponer LiveData a la UI ---

    /**
     * Obtiene la lista observable de todas las facturas.
     * La UI observará este LiveData para actualizarse automáticamente.
     */
    public LiveData<List<Factura>> getListaFacturas() {
        return listaFacturas;
    }

    /**
     * Obtiene la factura seleccionada observable.
     * Útil si tienes una vista que muestra detalles de una factura específica.
     */
    public LiveData<Factura> getFacturaSeleccionada() {
        return facturaSeleccionada;
    }


    // --- Métodos para cargar datos desde el DAO ---

    /**
     * Carga todas las facturas desde la base de datos y actualiza el LiveData.
     * Se debería llamar desde la UI (Fragment/Activity) cuando necesite los datos.
     */
    public void cargarTodasLasFacturas() {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getReadableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            List<Factura> facturas = dao.obtenerTodas();
            listaFacturas.postValue(facturas); // Usar postValue si se llama desde un hilo secundario, setValue si es desde el principal
        } catch (Exception e) {
            // Manejar error, quizás posteando una lista vacía o un estado de error
            System.err.println("Error cargando todas las facturas: " + e.getMessage());
            listaFacturas.postValue(Collections.emptyList()); // Informar lista vacía en caso de error
        } finally {
            if (db != null && db.isOpen()) {
                db.close(); // Cerrar la base de datos
            }
        }
    }

    /**
     * Carga todas las facturas de un pedido específico y actualiza el LiveData listaFacturas.
     * @param idPedido El ID del pedido cuyas facturas se quieren cargar.
     */
    public void cargarFacturasPorPedido(int idPedido) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getReadableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            List<Factura> facturas = dao.obtenerPorPedido(idPedido);
            listaFacturas.postValue(facturas);
        } catch (Exception e) {
            System.err.println("Error cargando facturas por pedido: " + e.getMessage());
            listaFacturas.postValue(Collections.emptyList());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }


    /**
     * Consulta una factura específica por su ID compuesto y actualiza el LiveData facturaSeleccionada.
     * No actualiza la lista principal, sólo el LiveData de la factura individual.
     * @param idPedido ID del Pedido.
     * @param idFactura ID de la Factura.
     */
    public void consultarFacturaPorId(int idPedido, int idFactura) {
        SQLiteDatabase db = null;
        Factura factura = null;
        try {
            db = dbHelper.getReadableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            factura = dao.consultarPorId(idPedido, idFactura);
            facturaSeleccionada.postValue(factura); // Postear la factura encontrada (o null si no se encontró)
        } catch (Exception e) {
            System.err.println("Error consultando factura por ID: " + e.getMessage());
            facturaSeleccionada.postValue(null); // Postear null en caso de error
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        // Nota: Este método actualiza 'facturaSeleccionada', no retorna el objeto directamente.
        // Si necesitas el objeto de forma síncrona (menos común en ViewModel), tendrías que cambiar la firma.
    }

    // --- Métodos para operaciones CRUD (Insertar, Actualizar, Eliminar) ---
    // Estos métodos generalmente se llaman desde la Activity/Fragment después de la interacción del usuario.
    // Podrían retornar un boolean indicando éxito/fallo, o usar otro LiveData para comunicar el resultado.

    /**
     * Inserta una nueva factura.
     * @param factura La factura a insertar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean insertarFactura(Factura factura) {
        SQLiteDatabase db = null;
        boolean exito = false;
        try {
            db = dbHelper.getWritableDatabase(); // Necesitamos escribir
            FacturaDAO dao = new FacturaDAO(db);
            // Obtenemos el siguiente ID *antes* de insertar
            int nextId = dao.getNextIdFactura(factura.getIdPedido());
            factura.setIdFactura(nextId); // Establecemos el ID calculado
            long resultado = dao.insertar(factura);
            exito = (resultado != -1); // Inserción exitosa si el resultado no es -1
        } catch (Exception e) {
            System.err.println("Error insertando factura: " + e.getMessage());
            exito = false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        // Opcional: Si la inserción fue exitosa, podrías recargar la lista
        // if (exito) { cargarTodasLasFacturas(); // O cargarFacturasPorPedido si es relevante }
        return exito;
    }

    /**
     * Actualiza una factura existente.
     * @param factura La factura con los datos actualizados.
     * @return true si la actualización fue exitosa (al menos una fila afectada), false en caso contrario.
     */
    public boolean actualizarFactura(Factura factura) {
        SQLiteDatabase db = null;
        boolean exito = false;
        try {
            db = dbHelper.getWritableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            int filasAfectadas = dao.actualizar(factura);
            exito = (filasAfectadas > 0);
        } catch (Exception e) {
            System.err.println("Error actualizando factura: " + e.getMessage());
            exito = false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        // Opcional: Si la actualización fue exitosa, recargar datos
        // if (exito) { ... }
        return exito;
    }

    /**
     * Elimina una factura.
     * @param idPedido ID del pedido de la factura a eliminar.
     * @param idFactura ID de la factura a eliminar.
     * @return true si la eliminación fue exitosa (al menos una fila afectada), false en caso contrario.
     */
    public boolean eliminarFactura(int idPedido, int idFactura) {
        SQLiteDatabase db = null;
        boolean exito = false;
        try {
            db = dbHelper.getWritableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            int filasAfectadas = dao.eliminar(idPedido, idFactura);
            exito = (filasAfectadas > 0);
        } catch (Exception e) {
            // Podría fallar por restricciones de FK si hay créditos asociados y no hay ON DELETE CASCADE
            System.err.println("Error eliminando factura: " + e.getMessage());
            exito = false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        // Opcional: Si la eliminación fue exitosa, recargar datos
        // if (exito) { ... }
        return exito;
    }

    // El método onCleared() es llamado cuando el ViewModel ya no se necesita.
    // Es un buen lugar para limpiar recursos si fuera necesario, aunque DBHelper
    // maneja el ciclo de vida de la base de datos correctamente.
    @Override
    protected void onCleared() {
        super.onCleared();
        // dbHelper.close(); // SQLiteOpenHelper no necesita cerrarse explícitamente aquí.
        System.out.println("FacturaViewModel cleared");
    }
}