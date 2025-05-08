package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito; // Ajusta el paquete

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
// import android.database.sqlite.SQLiteException; // Buen import si manejas excepciones específicas de DB
import android.util.Log;
// import android.widget.Toast; // No se usa Toast directamente en ViewModels

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// import java.util.stream.Collectors; // API 24+ para filtrar, usado abajo

// import sv.ues.fia.eisi.proyecto01_antojitos.R; // No se usa R directamente aquí
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
// Importar POJO y DAO
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.Credito;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.CreditoDAO;
// Importar FacturaDAO y Factura para la lógica de filtrado
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.Factura;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaDAO;


public class CreditoViewModel extends AndroidViewModel {

    private static final String TAG = "CreditoViewModel";

    // LiveData
    private final MutableLiveData<List<Credito>> listaTodosCreditos = new MutableLiveData<>();
    // Este LiveData contendrá la lista filtrada específicamente para las UIs de edición/eliminación
    private final MutableLiveData<List<Credito>> listaCreditosFiltradosParaUi = new MutableLiveData<>();
    private final MutableLiveData<Credito> creditoSeleccionado = new MutableLiveData<>();

    private DBHelper dbHelper;

    // Constantes para estados
    private static final String ESTADO_CREDITO_ACTIVO = "Activo";
    private static final String ESTADO_CREDITO_PAGADO = "Pagado";
    private static final String ESTADO_CREDITO_CANCELADO = "Cancelado";
    // Constantes de Factura que podrían ser relevantes
    private static final String ESTADO_FACTURA_PENDIENTE = "Pendiente";
    private static final String TIPO_PAGO_CONTADO = "Contado";


    public CreditoViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DBHelper(application);
        Log.d(TAG, "CreditoViewModel inicializado.");
    }

    // --- Getters para LiveData ---

    public LiveData<List<Credito>> getListaTodosCreditos() { return listaTodosCreditos; }
    // Getter para la lista filtrada que usarán las Activities de Edición/Eliminación
    public LiveData<List<Credito>> getListaCreditosFiltradosParaUi() { return listaCreditosFiltradosParaUi; }
    public LiveData<Credito> getCreditoSeleccionado() { return creditoSeleccionado; }

    // --- Métodos para Cargar/Consultar ---

    /**
     * Carga TODOS los créditos y actualiza LiveData listaTodosCreditos.
     * También filtra y actualiza listaCreditosFiltradosParaUi para incluir solo
     * créditos ACTIVOS cuya factura asociada tenga ES_CREDITO = 1.
     * Este método reemplaza la funcionalidad anterior de cargar solo activos y debe ser llamado
     * desde las Activities que necesiten la lista filtrada.
     */
    public void cargarTodosLosCreditosYFiltrarParaUi() {
        Log.d(TAG, "Cargando todos los créditos y filtrando para UI (Editar/Eliminar)...");
        SQLiteDatabase db = null;
        List<Credito> todosLosCreditosObtenidos = Collections.emptyList();
        List<Credito> creditosFiltrados = new ArrayList<>();

        try {
            db = dbHelper.getReadableDatabase();
            CreditoDAO creditoDAO = new CreditoDAO(db);
            FacturaDAO facturaDAO = new FacturaDAO(db); // Necesario para el filtro

            todosLosCreditosObtenidos = creditoDAO.obtenerTodos();
            listaTodosCreditos.postValue(todosLosCreditosObtenidos); // Publicar la lista completa de todos los créditos

            if (todosLosCreditosObtenidos != null && !todosLosCreditosObtenidos.isEmpty()) {
                for (Credito credito : todosLosCreditosObtenidos) {
                    // Condición 1: El crédito debe estar activo
                    if (ESTADO_CREDITO_ACTIVO.equalsIgnoreCase(credito.getEstadoCredito())) {
                        Factura facturaAsociada = facturaDAO.consultarPorId(credito.getIdFactura());
                        // Condición 2: La factura asociada debe existir y tener ES_CREDITO = 1
                        if (facturaAsociada != null && facturaAsociada.getEsCredito() == 1) {
                            creditosFiltrados.add(credito);
                        }
                    }
                }
            }
            listaCreditosFiltradosParaUi.postValue(creditosFiltrados); // Publicar la lista filtrada
            Log.d(TAG, "Créditos cargados: Todos=" + (todosLosCreditosObtenidos != null ? todosLosCreditosObtenidos.size() : 0) +
                    ", Filtrados para UI (Activos y Factura.EsCredito=1)=" + creditosFiltrados.size());

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar y filtrar créditos para UI", e);
            listaTodosCreditos.postValue(Collections.emptyList());
            listaCreditosFiltradosParaUi.postValue(Collections.emptyList());
        }
        // No cerrar db aquí si dbHelper gestiona su ciclo de vida o es compartido.
    }


    /** Consulta un crédito por su ID_CREDITO y actualiza creditoSeleccionado */
    public void consultarCreditoPorId(int idCredito) {
        Log.d(TAG, "Consultando crédito por ID: " + idCredito);
        SQLiteDatabase db = null;
        Credito credito = null;
        try {
            db = dbHelper.getReadableDatabase();
            CreditoDAO dao = new CreditoDAO(db);
            credito = dao.consultarPorId(idCredito);
            creditoSeleccionado.postValue(credito);
        } catch (Exception e) {
            Log.e(TAG, "Error consultando crédito por ID " + idCredito, e);
            creditoSeleccionado.postValue(null);
        } finally {
            Log.d(TAG, "Consulta crédito ID " + idCredito + " finalizada. Encontrado: " + (credito != null));
        }
    }

    /** Consulta un crédito por el ID_FACTURA asociado y actualiza creditoSeleccionado */
    public void consultarCreditoPorIdFactura(int idFactura) {
        Log.d(TAG, "Consultando crédito por ID Factura: " + idFactura);
        SQLiteDatabase db = null;
        Credito credito = null;
        try {
            db = dbHelper.getReadableDatabase();
            CreditoDAO dao = new CreditoDAO(db);
            credito = dao.consultarPorIdFactura(idFactura);
            creditoSeleccionado.postValue(credito);
        } catch (Exception e) {
            Log.e(TAG, "Error consultando crédito por ID Factura " + idFactura, e);
            creditoSeleccionado.postValue(null);
        } finally {
            Log.d(TAG, "Consulta crédito por ID Factura " + idFactura + " finalizada. Encontrado: " + (credito != null));
        }
    }

    // --- Métodos para Modificar Crédito ---

    /**
     * Inserta un nuevo registro de crédito usando el DAO.
     * Antes de insertar, verifica que la factura asociada sea efectivamente a crédito.
     * @param credito El objeto Credito a insertar.
     * @return El ID del nuevo crédito o -1 si falla la inserción o la validación.
     */
    public long insertarCredito(Credito credito) {
        Log.d(TAG, "Intentando insertar Crédito para Factura ID: " + credito.getIdFactura());
        SQLiteDatabase db = null;
        long nuevoId = -1;
        try {
            db = dbHelper.getWritableDatabase();
            // VALIDACIÓN: Asegurar que la factura es a crédito antes de crear un crédito para ella.
            FacturaDAO facturaDAO = new FacturaDAO(db);
            Factura facturaAsociada = facturaDAO.consultarPorId(credito.getIdFactura());
            if (facturaAsociada == null || facturaAsociada.getEsCredito() != 1) {
                Log.e(TAG, "Intento de insertar crédito para una factura que NO es a crédito o no existe. Factura ID: " + credito.getIdFactura());
                return -1; // Fallo de validación
            }

            CreditoDAO creditoDAO = new CreditoDAO(db);
            nuevoId = creditoDAO.insertar(credito);

        } catch (Exception e) {
            Log.e(TAG, "Error insertando crédito VM para Factura ID " + credito.getIdFactura(), e);
            nuevoId = -1;
        }
        if(nuevoId != -1) {
            Log.i(TAG,"Crédito insertado exitosamente con ID: " + nuevoId);
            cargarTodosLosCreditosYFiltrarParaUi(); // Recargar ambas listas (la completa y la filtrada)
        }
        return nuevoId;
    }


    /**
     * Actualiza la fecha límite de pago de un crédito.
     * Valida que el crédito siga siendo elegible para edición.
     * @param idCredito El ID del crédito a actualizar.
     * @param nuevaFechaLimite La nueva fecha límite en formato `"yyyy-MM-dd"`.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarFechaLimite(int idCredito, String nuevaFechaLimite) {
        Log.d(TAG, "Intentando actualizar fecha límite para Crédito ID: " + idCredito);
        SQLiteDatabase db = null;
        boolean exito = false;
        try {
            db = dbHelper.getWritableDatabase();
            CreditoDAO creditoDAO = new CreditoDAO(db);
            Credito credito = creditoDAO.consultarPorId(idCredito);

            if (credito != null) {
                // VALIDACIÓN ADICIONAL: Antes de actualizar, verificar que el crédito aún cumple
                // con las condiciones para ser editable (activo y factura asociada es a crédito).
                FacturaDAO facturaDAO = new FacturaDAO(db);
                Factura facturaAsociada = facturaDAO.consultarPorId(credito.getIdFactura());

                if (!ESTADO_CREDITO_ACTIVO.equalsIgnoreCase(credito.getEstadoCredito()) ||
                        facturaAsociada == null || facturaAsociada.getEsCredito() != 1) {
                    Log.w(TAG, "Crédito ID " + idCredito + " ya no es elegible para actualización de fecha (estado/factura cambió).");
                    return false; // No actualizar si ya no cumple condiciones de edición
                }
                // --- Fin Validación adicional ---

                if (!nuevaFechaLimite.equals(credito.getFechaLimitePago())) {
                    credito.setFechaLimitePago(nuevaFechaLimite);
                    int filas = creditoDAO.actualizar(credito);
                    exito = (filas > 0);
                } else {
                    Log.d(TAG,"La fecha límite proporcionada es la misma que la actual.");
                    exito = true; // Considerar éxito si no hay cambio necesario
                }
            } else {
                Log.w(TAG, "No se encontró crédito con ID: " + idCredito + " para actualizar fecha.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar fecha límite para crédito ID " + idCredito, e);
            exito = false;
        }
        if(exito) {
            Log.i(TAG, "Fecha límite actualizada para crédito ID " + idCredito);
            consultarCreditoPorId(idCredito); // Actualiza el LiveData creditoSeleccionado
            cargarTodosLosCreditosYFiltrarParaUi(); // Recarga ambas listas (completa y filtrada para UI)
        }
        return exito;
    }

    /**
     * Cancela un crédito (cambia su estado a "Cancelado") y actualiza la factura asociada.
     * @param idCredito El ID del crédito a cancelar.
     * @return true si la cancelación (ambas actualizaciones) fue exitosa, false en caso contrario.
     */
    public boolean cancelarCredito(int idCredito) {
        Log.i(TAG, "Intentando cancelar Crédito ID: " + idCredito + " y actualizar Factura asociada.");
        SQLiteDatabase db = null;
        boolean exitoFinal = false;

        // Usar constantes de clase para estados
        String estadoCreditoCanceladoLocal = ESTADO_CREDITO_CANCELADO;
        String estadoCreditoPagadoLocal = ESTADO_CREDITO_PAGADO;
        String estadoFacturaDestinoLocal = ESTADO_FACTURA_PENDIENTE;
        int esCreditoFacturaDestinoLocal = 0; // 0 = No es crédito

        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction(); // Iniciar transacción

            CreditoDAO creditoDAO = new CreditoDAO(db);
            FacturaDAO facturaDAO = new FacturaDAO(db);

            Credito credito = creditoDAO.consultarPorId(idCredito);

            if (credito == null) {
                Log.w(TAG, "No se encontró crédito con ID " + idCredito + " para cancelar.");
                return false; // Salir si no existe, la transacción se revertirá por defecto
            }

            // VALIDACIÓN CRÍTICA: Asegurarse que la factura asociada ES a crédito
            // antes de proceder con la lógica de cancelación de un crédito.
            Factura facturaAsociada = facturaDAO.consultarPorId(credito.getIdFactura());
            if (facturaAsociada == null || facturaAsociada.getEsCredito() != 1) {
                Log.e(TAG, "Error de consistencia: Intento de cancelar Crédito ID " + idCredito +
                        " pero su factura asociada (ID: " + credito.getIdFactura() +
                        ") no está marcada como crédito o no existe.");
                return false; // No proceder si hay inconsistencia
            }


            // --- VALIDACIONES DEL CRÉDITO ---
            if (estadoCreditoCanceladoLocal.equalsIgnoreCase(credito.getEstadoCredito())){
                Log.w(TAG,"El crédito ID " + idCredito + " ya estaba cancelado.");
                db.setTransactionSuccessful(); // Considerar éxito si ya está en el estado deseado
                return true;
            }
            if (estadoCreditoPagadoLocal.equalsIgnoreCase(credito.getEstadoCredito())){
                Log.w(TAG,"El crédito ID " + idCredito + " está pagado, no se puede cancelar.");
                return false;
            }
            if (credito.getMontoPagado() > 0) {
                Log.w(TAG,"El crédito ID " + idCredito + " tiene pagos realizados (" + credito.getMontoPagado() + "), no se puede cancelar.");
                return false;
            }

            // --- ACTUALIZACIONES (Si pasa validaciones) ---
            credito.setEstadoCredito(estadoCreditoCanceladoLocal);
            int filasCredito = creditoDAO.actualizar(credito);

            if (filasCredito > 0) {
                Log.i(TAG, "Crédito ID: " + idCredito + " actualizado a estado Cancelado.");
                // Actualizar la Factura Asociada
                facturaAsociada.setEsCredito(esCreditoFacturaDestinoLocal); // Marcar factura como que ya NO es a crédito
                facturaAsociada.setEstadoFactura(estadoFacturaDestinoLocal); // Cambiar estado de factura a Pendiente
                // Opcional: Cambiar tipo de pago si se revierte a no crédito
                // facturaAsociada.setTipoPago(TIPO_PAGO_CONTADO);

                int filasFactura = facturaDAO.actualizar(facturaAsociada);
                if (filasFactura > 0) {
                    Log.i(TAG, "Factura ID: " + facturaAsociada.getIdFactura() + " asociada actualizada (ES_CREDITO=0, Estado=Pendiente).");
                    db.setTransactionSuccessful(); // Marcar éxito SOLO si AMBAS actualizaciones funcionan
                    exitoFinal = true;
                } else {
                    Log.e(TAG, "Error al actualizar la factura asociada (ID: " + facturaAsociada.getIdFactura() + ") tras cancelar crédito.");
                }
            } else {
                Log.e(TAG, "Error al actualizar el estado del crédito ID: " + idCredito + " a Cancelado.");
            }

        } catch (Exception e) {
            Log.e(TAG, "Excepción al cancelar crédito ID " + idCredito, e);
            exitoFinal = false;
        } finally {
            if (db != null && db.inTransaction()) { // Solo finalizar si la transacción está activa
                db.endTransaction(); // Commit si setTransactionSuccessful fue llamado, sino Rollback
            }
        }
        if(exitoFinal) {
            Log.i(TAG, "Proceso de cancelación para Crédito ID " + idCredito + " completado exitosamente.");
            cargarTodosLosCreditosYFiltrarParaUi(); // Recargar ambas listas
            // Limpiar selección actual si era el crédito cancelado
            if (creditoSeleccionado.getValue() != null && creditoSeleccionado.getValue().getIdCredito() == idCredito) {
                creditoSeleccionado.postValue(null);
            }
        }
        return exitoFinal;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        // Generalmente no cierras dbHelper aquí si es compartido o gestionado por Application.
        Log.d(TAG, "CreditoViewModel cleared");
    }
}