package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.util.Log; // Para logs

import java.util.Collections;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;

public class FacturaViewModel extends AndroidViewModel {

    private static final String TAG = "FacturaViewModel";

    private final MutableLiveData<List<Factura>> listaFacturas = new MutableLiveData<>();
    private final MutableLiveData<Factura> facturaSeleccionada = new MutableLiveData<>();
    private DBHelper dbHelper;

    public FacturaViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DBHelper(application);
    }

    public LiveData<List<Factura>> getListaFacturas() {
        return listaFacturas;
    }

    public LiveData<Factura> getFacturaSeleccionada() {
        return facturaSeleccionada;
    }

    public void cargarTodasLasFacturas() {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getReadableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            List<Factura> facturas = dao.obtenerTodas(); // Asume que FacturaDAO.obtenerTodas() está adaptado
            listaFacturas.postValue(facturas);
            Log.d(TAG, "Cargadas " + (facturas != null ? facturas.size() : 0) + " facturas en total.");
        } catch (Exception e) {
            Log.e(TAG, "Error cargando todas las facturas", e);
            listaFacturas.postValue(Collections.emptyList());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * Consulta una factura específica por su ID_FACTURA (PK).
     * Actualiza el LiveData facturaSeleccionada.
     * @param idFactura El ID de la factura a consultar.
     */
    public void consultarFacturaPorId(int idFactura) {
        SQLiteDatabase db = null;
        Factura factura = null;
        try {
            db = dbHelper.getReadableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            // !!! CAMBIO: FacturaDAO.consultarPorId ahora solo necesita idFactura
            factura = dao.consultarPorId(idFactura);
            facturaSeleccionada.postValue(factura);
            if (factura != null) {
                Log.d(TAG, "Factura consultada por ID: " + idFactura + ", Pedido ID: " + factura.getIdPedido());
            } else {
                Log.d(TAG, "Factura con ID: " + idFactura + " no encontrada.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error consultando factura por ID: " + idFactura, e);
            facturaSeleccionada.postValue(null);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * Consulta la factura asociada a un ID_PEDIDO específico (relación 1:1).
     * Actualiza el LiveData facturaSeleccionada.
     * @param idPedido El ID del pedido cuya factura se quiere consultar.
     */
    public void consultarFacturaDePedido(int idPedido) {
        SQLiteDatabase db = null;
        Factura factura = null;
        try {
            db = dbHelper.getReadableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            // !!! CAMBIO: Se necesita un método en FacturaDAO para buscar por ID_PEDIDO
            // (ej: dao.consultarPorIdPedido(idPedido))
            factura = dao.consultarPorIdPedido(idPedido); // Asume que este método existe en FacturaDAO
            facturaSeleccionada.postValue(factura);
            if (factura != null) {
                Log.d(TAG, "Factura consultada para Pedido ID: " + idPedido + ", Factura ID: " + factura.getIdFactura());
            } else {
                Log.d(TAG, "No se encontró factura para Pedido ID: " + idPedido);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error consultando factura de pedido: " + idPedido, e);
            facturaSeleccionada.postValue(null);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }


    /**
     * Inserta una nueva factura. ID_FACTURA es autoincremental.
     * @param factura La factura a insertar (debe tener ID_PEDIDO seteado).
     * @return El ID de la factura insertada, o -1 si ocurrió un error.
     */
    public long insertarFactura(Factura factura) {
        SQLiteDatabase db = null;
        long nuevoIdFactura = -1;
        try {
            db = dbHelper.getWritableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            // !!! CAMBIO: ID_FACTURA es AUTOINCREMENT. El DAO.insertar debe devolver el nuevo ID.
            // No se necesita getNextIdFactura().
            nuevoIdFactura = dao.insertar(factura); // Asume que dao.insertar devuelve el ID (long)
            if (nuevoIdFactura != -1) {
                factura.setIdFactura((int) nuevoIdFactura); // Actualizar el objeto factura con el ID generado
                Log.d(TAG, "Factura insertada con ID: " + nuevoIdFactura + " para Pedido ID: " + factura.getIdPedido());
                // Opcional: recargar lista o factura seleccionada si es necesario
                // cargarTodasLasFacturas();
                // facturaSeleccionada.postValue(factura);
            } else {
                Log.e(TAG, "Error al insertar factura para Pedido ID: " + factura.getIdPedido());
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción al insertar factura", e);
            nuevoIdFactura = -1;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return nuevoIdFactura;
    }

    /**
     * Actualiza una factura existente.
     * @param factura La factura con los datos actualizados (debe tener ID_FACTURA).
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarFactura(Factura factura) {
        SQLiteDatabase db = null;
        boolean exito = false;
        try {
            db = dbHelper.getWritableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            // !!! CAMBIO: FacturaDAO.actualizar ahora usa ID_FACTURA como clave para el WHERE.
            int filasAfectadas = dao.actualizar(factura);
            exito = (filasAfectadas > 0);
            if (exito) {
                Log.d(TAG, "Factura actualizada con ID: " + factura.getIdFactura());
                // Opcional: recargar datos
                // consultarFacturaPorId(factura.getIdFactura());
            } else {
                Log.w(TAG, "No se actualizó la factura con ID: " + factura.getIdFactura() + " (quizás no existía o no hubo cambios)");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error actualizando factura ID: " + factura.getIdFactura(), e);
            exito = false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return exito;
    }

    /**
     * Elimina una factura por su ID_FACTURA.
     * @param idFactura ID de la factura a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminarFactura(int idFactura) {
        SQLiteDatabase db = null;
        boolean exito = false;
        try {
            db = dbHelper.getWritableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            // !!! CAMBIO: FacturaDAO.eliminar ahora solo necesita idFactura.
            int filasAfectadas = dao.eliminar(idFactura);
            exito = (filasAfectadas > 0);
            if (exito) {
                Log.d(TAG, "Factura eliminada con ID: " + idFactura);
                // Opcional: limpiar facturaSeleccionada si era esta
                // if (facturaSeleccionada.getValue() != null && facturaSeleccionada.getValue().getIdFactura() == idFactura) {
                //     facturaSeleccionada.postValue(null);
                // }
                // cargarTodasLasFacturas();
            } else {
                Log.w(TAG, "No se eliminó la factura con ID: " + idFactura + " (quizás no existía)");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error eliminando factura ID: " + idFactura, e);
            exito = false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return exito;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "FacturaViewModel cleared");
        // dbHelper.close(); // SQLiteOpenHelper se maneja por el sistema si es una instancia compartida.
        // Si dbHelper es una instancia única para este ViewModel, se podría cerrar aquí,
        // pero generalmente se obtiene una instancia por operación o se usa una singleton.
    }
}