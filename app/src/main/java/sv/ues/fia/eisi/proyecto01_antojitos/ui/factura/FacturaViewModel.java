package sv.ues.fia.eisi.proyecto01_antojitos.ui.factura;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.Collections;
import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
// Asegúrate de importar el POJO Factura y el DAO Factura
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.Factura;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaDAO;


public class FacturaViewModel extends AndroidViewModel {

    private static final String TAG = "FacturaViewModel";

    // LiveData para observar listas (ej. todas las facturas)
    private final MutableLiveData<List<Factura>> listaFacturas = new MutableLiveData<>();
    // LiveData para observar una factura específica (ej. resultado de consulta)
    private final MutableLiveData<Factura> facturaSeleccionada = new MutableLiveData<>();

    private DBHelper dbHelper; // Instancia del helper

    // Constructor
    public FacturaViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DBHelper(application); // Crear instancia
        Log.d(TAG, "FacturaViewModel inicializado.");
    }

    // --- Getters para LiveData (observables desde la UI) ---

    public LiveData<List<Factura>> getListaFacturas() {
        return listaFacturas;
    }

    public LiveData<Factura> getFacturaSeleccionada() {
        return facturaSeleccionada;
    }

    // --- Métodos para Cargar/Consultar Datos ---

    /** Carga todas las facturas desde la BD y actualiza LiveData listaFacturas */
    public void cargarTodasLasFacturas() {
        Log.d(TAG, "Iniciando carga de todas las facturas...");
        SQLiteDatabase db = null;
        List<Factura> facturas = Collections.emptyList();
        try {
            db = dbHelper.getReadableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            facturas = dao.obtenerTodas(); // Usa método del DAO adaptado
            listaFacturas.postValue(facturas);
        } catch (Exception e) {
            Log.e(TAG, "Error cargando todas las facturas", e);
            listaFacturas.postValue(Collections.emptyList()); // Postear lista vacía en error
        } finally {
            // No cerrar DB aquí si se reutiliza el helper
            Log.d(TAG, "Carga de todas las facturas finalizada. Encontradas: " + facturas.size());
        }
    }

    /**
     * Consulta una factura específica por su ID_FACTURA y actualiza LiveData facturaSeleccionada.
     * @param idFactura El ID de la factura a consultar.
     */
    public void consultarFacturaPorId(int idFactura) {
        Log.d(TAG, "Consultando factura por ID_Factura: " + idFactura);
        SQLiteDatabase db = null;
        Factura factura = null;
        try {
            db = dbHelper.getReadableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            factura = dao.consultarPorId(idFactura); // Llama al método DAO adaptado
            facturaSeleccionada.postValue(factura); // Actualiza LiveData (incluso si es null)
        } catch (Exception e) {
            Log.e(TAG, "Error consultando factura por ID: " + idFactura, e);
            facturaSeleccionada.postValue(null);
        } finally {
            // No cerrar DB aquí
            Log.d(TAG, "Consulta por ID_Factura " + idFactura + " finalizada. Encontrada: " + (factura != null));
        }
    }

    /**
     * Consulta la factura asociada a un ID_PEDIDO específico (relación 1:1) y actualiza LiveData facturaSeleccionada.
     * @param idPedido El ID del pedido cuya (única) factura se quiere consultar.
     */
    public void consultarFacturaDePedido(int idPedido) {
        Log.d(TAG, "Consultando factura por ID_Pedido: " + idPedido);
        SQLiteDatabase db = null;
        Factura factura = null;
        try {
            db = dbHelper.getReadableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            factura = dao.consultarPorIdPedido(idPedido); // Usa el nuevo método del DAO
            facturaSeleccionada.postValue(factura); // Actualiza LiveData
        } catch (Exception e) {
            Log.e(TAG, "Error consultando factura de pedido: " + idPedido, e);
            facturaSeleccionada.postValue(null);
        } finally {
            // No cerrar DB aquí
            Log.d(TAG, "Consulta por ID_Pedido " + idPedido + " finalizada. Encontrada: " + (factura != null));
        }
    }


    // --- Métodos para Operaciones CRUD ---

    /**
     * Inserta una nueva factura usando el DAO adaptado.
     * @param factura La factura a insertar (con ID_PEDIDO y otros campos seteados).
     * @return El ID de la factura insertada (generado por AUTOINCREMENT), o -1 si ocurrió un error.
     */
    public long insertarFactura(Factura factura) {
        Log.d(TAG, "Intentando insertar factura para Pedido ID: " + factura.getIdPedido());
        SQLiteDatabase db = null;
        long nuevoIdFactura = -1;
        try {
            db = dbHelper.getWritableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            nuevoIdFactura = dao.insertar(factura); // DAO devuelve el nuevo ID_FACTURA
            if (nuevoIdFactura != -1) {
                Log.i(TAG, "Factura insertada con ID: " + nuevoIdFactura);
                // Opcional: podrías querer recargar alguna lista aquí
                // cargarTodasLasFacturas();
            } else {
                Log.e(TAG, "DAO devolvió -1 al insertar factura para Pedido ID: " + factura.getIdPedido());
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción al insertar factura", e);
            nuevoIdFactura = -1;
        } finally {
            // No cerrar DB aquí
        }
        return nuevoIdFactura;
    }

    /**
     * Actualiza una factura existente usando el DAO adaptado.
     * @param factura La factura con los datos actualizados (debe tener ID_FACTURA).
     * @return true si la actualización afectó al menos una fila, false en caso contrario.
     */
    public boolean actualizarFactura(Factura factura) {
        Log.d(TAG, "Intentando actualizar factura ID: " + factura.getIdFactura());
        SQLiteDatabase db = null;
        boolean exito = false;
        try {
            db = dbHelper.getWritableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            int filasAfectadas = dao.actualizar(factura); // DAO actualizado usa ID_FACTURA en WHERE
            exito = (filasAfectadas > 0);
            if (exito) {
                Log.i(TAG, "Factura actualizada exitosamente ID: " + factura.getIdFactura());
                // Opcional: Refrescar la factura seleccionada o la lista
                // consultarFacturaPorId(factura.getIdFactura());
                // cargarTodasLasFacturas();
            } else {
                Log.w(TAG, "Factura ID: " + factura.getIdFactura() + " no actualizada (filas afectadas=0)");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error actualizando factura ID: " + factura.getIdFactura(), e);
            exito = false;
        } finally {
            // No cerrar DB aquí
        }
        return exito;
    }

    /**
     * Elimina una factura por su ID_FACTURA usando el DAO adaptado.
     * @param idFactura ID de la factura a eliminar.
     * @return true si la eliminación afectó al menos una fila, false en caso contrario.
     */
    public boolean eliminarFactura(int idFactura) {
        Log.d(TAG, "Intentando eliminar factura ID: " + idFactura);
        SQLiteDatabase db = null;
        boolean exito = false;
        try {
            db = dbHelper.getWritableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            int filasAfectadas = dao.eliminar(idFactura); // DAO actualizado usa ID_FACTURA en WHERE
            exito = (filasAfectadas > 0);
            if (exito) {
                Log.i(TAG, "Factura eliminada exitosamente ID: " + idFactura);
                // Opcional: Limpiar selección, recargar lista
                // if (facturaSeleccionada.getValue() != null && facturaSeleccionada.getValue().getIdFactura() == idFactura) {
                //     facturaSeleccionada.postValue(null);
                // }
                // cargarTodasLasFacturas();
            } else {
                Log.w(TAG, "Factura ID: " + idFactura + " no eliminada (filas afectadas=0)");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error eliminando factura ID: " + idFactura, e);
            exito = false;
        } finally {
            // No cerrar DB aquí
        }
        return exito;
    }

    // Limpia el LiveData de la factura seleccionada
    public void limpiarFacturaSeleccionada() {
        facturaSeleccionada.postValue(null);
        Log.d(TAG,"LiveData facturaSeleccionada limpiado.");
    }

    /**
     * Marca una factura como pagada (cambia ESTADO_FACTURA a "Pagada").
     * Solo debería llamarse para facturas no a crédito y pendientes.
     * @param idFactura El ID de la factura a marcar como pagada.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean marcarComoPagada(int idFactura) {
        Log.i(TAG, "Intentando marcar como pagada Factura ID: " + idFactura);
        SQLiteDatabase db = null;
        boolean exito = false;
        String estadoPagada = "Pagada"; // O usa R.string.factura_estado_pagada

        try {
            db = dbHelper.getWritableDatabase();
            FacturaDAO dao = new FacturaDAO(db);
            Factura factura = dao.consultarPorId(idFactura); // Obtener factura actual

            if (factura != null) {
                // Validación (importante hacerla aquí o en la Activity)
                if (factura.getEsCredito() == 1) {
                    Log.w(TAG,"Intento de marcar como pagada una factura a crédito (ID: " + idFactura + "). Usar lógica de crédito.");
                    // Considera lanzar una excepción o devolver un código de error específico
                    return false;
                }
                if (!"Pendiente".equalsIgnoreCase(factura.getEstadoFactura())) { // O el estado que consideres pagable
                    Log.w(TAG,"Intento de marcar como pagada una factura que no está pendiente (ID: " + idFactura + ", Estado: " + factura.getEstadoFactura() + ").");
                    // Podrías permitir pagar si está "En Crédito" si se salda el crédito? No, esa es otra lógica.
                    // Si ya está "Pagada", considerar éxito silencioso?
                    if (estadoPagada.equalsIgnoreCase(factura.getEstadoFactura())) return true;
                    return false; // No está en estado pagable
                }

                // Actualizar estado
                factura.setEstadoFactura(estadoPagada);
                // Podrías querer actualizar TIPO_PAGO si se pagó diferente? No en esta lógica simple.

                int filasAfectadas = dao.actualizar(factura);
                exito = (filasAfectadas > 0);

            } else {
                Log.w(TAG, "No se encontró factura con ID " + idFactura + " para marcar como pagada.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al marcar factura como pagada ID " + idFactura, e);
            exito = false;
        } finally {
            // No cerrar db si helper es compartido
        }
        if (exito) {
            Log.i(TAG, "Factura ID: " + idFactura + " marcada como Pagada.");
            // Recargar datos para reflejar cambios
            consultarFacturaPorId(idFactura); // Actualiza facturaSeleccionada
            cargarTodasLasFacturas(); // Actualiza la lista
            // La actualización del estado del Pedido asociado debería ocurrir
            // vía trigger (si lo reactivas) o llamando a lógica de Pedido aquí.
        }
        return exito;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "FacturaViewModel cleared");
        // No cerrar dbHelper aquí generalmente
    }
}