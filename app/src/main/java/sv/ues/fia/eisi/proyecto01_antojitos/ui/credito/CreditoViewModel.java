package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito; // Ajusta el paquete

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException; // Importar para usar en catch específico
import android.util.Log;
import android.widget.Toast; // Importar Toast para mensajes internos (alternativa a devolver códigos)

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors; // API 24+ para filtrar

import sv.ues.fia.eisi.proyecto01_antojitos.R; // Importar R para strings
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
// Importar POJO y DAO
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.Credito;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.CreditoDAO;
// Importar FacturaDAO y FacturaViewModel si necesitas lógica relacionada (ej. actualizar estado factura)
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.Factura;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaDAO;


public class CreditoViewModel extends AndroidViewModel {

    private static final String TAG = "CreditoViewModel";

    // LiveData
    private final MutableLiveData<List<Credito>> listaTodosCreditos = new MutableLiveData<>();
    private final MutableLiveData<List<Credito>> listaCreditosActivos = new MutableLiveData<>();
    private final MutableLiveData<Credito> creditoSeleccionado = new MutableLiveData<>();

    private DBHelper dbHelper;

    // Constantes para estados (mejor definirlas aquí que hardcodear)
    private static final String ESTADO_CREDITO_ACTIVO = "Activo";
    private static final String ESTADO_CREDITO_PAGADO = "Pagado";
    private static final String ESTADO_CREDITO_CANCELADO = "Cancelado";
    private static final String ESTADO_FACTURA_PENDIENTE = "Pendiente";
    private static final String TIPO_PAGO_CONTADO = "Contado"; // O el default que uses

    public CreditoViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DBHelper(application);
        Log.d(TAG, "CreditoViewModel inicializado.");
    }

    // --- Getters para LiveData ---

    public LiveData<List<Credito>> getListaTodosCreditos() { return listaTodosCreditos; }
    public LiveData<List<Credito>> getListaCreditosActivos() { return listaCreditosActivos; }
    public LiveData<Credito> getCreditoSeleccionado() { return creditoSeleccionado; }

    // --- Métodos para Cargar/Consultar ---

    /** Carga TODOS los créditos y actualiza los LiveData de listas */
    public void cargarTodosLosCreditos() {
        Log.d(TAG, "Cargando todos los créditos...");
        SQLiteDatabase db = null;
        List<Credito> todos = Collections.emptyList();
        try {
            db = dbHelper.getReadableDatabase();
            CreditoDAO dao = new CreditoDAO(db);
            todos = dao.obtenerTodos();
            listaTodosCreditos.postValue(todos);

            // Filtrar activos
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Credito> activos = todos.stream()
                        .filter(c -> ESTADO_CREDITO_ACTIVO.equalsIgnoreCase(c.getEstadoCredito()))
                        .collect(Collectors.toList());
                listaCreditosActivos.postValue(activos);
                Log.d(TAG, "Créditos cargados: Todos=" + todos.size() + ", Activos=" + activos.size());
            } else {
                List<Credito> activos = new ArrayList<>();
                if (todos != null) { // Chequeo null safety
                    for (Credito c : todos){
                        if (ESTADO_CREDITO_ACTIVO.equalsIgnoreCase(c.getEstadoCredito())){
                            activos.add(c);
                        }
                    }
                }
                listaCreditosActivos.postValue(activos);
                Log.d(TAG, "Créditos cargados: Todos=" + (todos != null ? todos.size(): 0) + ", Activos=" + activos.size());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar todos los créditos", e);
            listaTodosCreditos.postValue(Collections.emptyList());
            listaCreditosActivos.postValue(Collections.emptyList());
        } finally {
            // No cerrar db aquí si dbHelper es miembro y se reutiliza
        }
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
     * @param credito El objeto Credito a insertar.
     * @return El ID del nuevo crédito o -1 si falla.
     */
    public long insertarCredito(Credito credito) {
        Log.d(TAG, "Intentando insertar Crédito para Factura ID: " + credito.getIdFactura());
        SQLiteDatabase db = null;
        long nuevoId = -1;
        try {
            db = dbHelper.getWritableDatabase();
            CreditoDAO dao = new CreditoDAO(db);
            nuevoId = dao.insertar(credito);
        } catch (Exception e) {
            Log.e(TAG, "Error insertando crédito VM", e);
            nuevoId = -1;
        } finally {
            // No cerrar DB
        }
        if(nuevoId != -1) {
            Log.i(TAG,"Crédito insertado exitosamente con ID: " + nuevoId);
            cargarTodosLosCreditos(); // Recargar listas
        }
        return nuevoId;
    }


    /**
     * Actualiza la fecha límite de pago de un crédito.
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
            CreditoDAO dao = new CreditoDAO(db);
            Credito credito = dao.consultarPorId(idCredito);
            if (credito != null) {
                // Solo actualizar si la fecha es diferente para evitar updates innecesarios
                if (!nuevaFechaLimite.equals(credito.getFechaLimitePago())) {
                    credito.setFechaLimitePago(nuevaFechaLimite);
                    int filas = dao.actualizar(credito); // Llama a actualizar del DAO
                    exito = (filas > 0);
                } else {
                    Log.d(TAG,"La fecha límite proporcionada es la misma que la actual.");
                    exito = true; // Considerar éxito si no hay cambio necesario
                }
            } else {
                Log.w(TAG, "No se encontró crédito con ID " + idCredito + " para actualizar fecha.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar fecha límite para crédito ID " + idCredito, e);
            exito = false;
        } finally {
            // No cerrar db
        }
        if(exito) {
            Log.i(TAG, "Fecha límite actualizada para crédito ID " + idCredito);
            // Recargar el crédito específico o toda la lista
            consultarCreditoPorId(idCredito);
            cargarTodosLosCreditos();
        }
        return exito;
    }

    /**
     * Cancela un crédito (cambia su estado a "Cancelado") y actualiza la factura asociada.
     * **Nueva Validación:** No permite cancelar si el crédito ya está pagado o si tiene pagos realizados.
     * @param idCredito El ID del crédito a cancelar.
     * @return true si la cancelación (ambas actualizaciones) fue exitosa, false en caso contrario o si no se puede cancelar.
     */
    public boolean cancelarCredito(int idCredito) {
        Log.i(TAG, "Intentando cancelar Crédito ID: " + idCredito + " y actualizar Factura asociada.");
        SQLiteDatabase db = null;
        boolean exitoFinal = false;

        // Obtener strings de estados desde resources para consistencia
        String estadoCreditoCancelado = ESTADO_CREDITO_CANCELADO; // "Cancelado"
        String estadoCreditoPagado = ESTADO_CREDITO_PAGADO; // "Pagado"
        String estadoFacturaDestino = ESTADO_FACTURA_PENDIENTE; // "Pendiente"
        int esCreditoFacturaDestino = 0; // 0 = No
        String tipoPagoFacturaDestino = TIPO_PAGO_CONTADO; // Opcional: Cambiar tipo pago

        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction(); // Iniciar transacción

            CreditoDAO creditoDAO = new CreditoDAO(db);
            FacturaDAO facturaDAO = new FacturaDAO(db); // Necesitamos DAO de Factura

            Credito credito = creditoDAO.consultarPorId(idCredito);

            // --- VALIDACIONES ---
            if (credito == null) {
                Log.w(TAG, "No se encontró crédito con ID " + idCredito + " para cancelar.");
                db.endTransaction(); // Terminar transacción sin éxito
                return false; // Salir si no existe
            }
            if (estadoCreditoCancelado.equalsIgnoreCase(credito.getEstadoCredito())){
                Log.w(TAG,"El crédito ID " + idCredito + " ya estaba cancelado.");
                db.endTransaction();
                return true; // Ya estaba cancelado, considerar éxito
            }
            if (estadoCreditoPagado.equalsIgnoreCase(credito.getEstadoCredito())){
                Log.w(TAG,"El crédito ID " + idCredito + " está pagado, no se puede cancelar.");
                // Informar al usuario (idealmente desde la Activity basada en el retorno 'false')
                // Toast.makeText(getApplication(), R.string.credito_cancelar_toast_no_cancelable_pagado, Toast.LENGTH_SHORT).show();
                db.endTransaction();
                return false; // No se puede cancelar si ya está pagado
            }
            // *** NUEVA VALIDACIÓN ***
            if (credito.getMontoPagado() > 0) {
                Log.w(TAG,"El crédito ID " + idCredito + " tiene pagos realizados (" + credito.getMontoPagado() + "), no se puede cancelar.");
                // Toast.makeText(getApplication(), R.string.credito_cancelar_toast_no_cancelable_pagos, Toast.LENGTH_SHORT).show();
                db.endTransaction();
                return false; // No se puede cancelar si ya tiene pagos
            }

            // --- ACTUALIZACIONES (Si pasa validaciones) ---
            // 1. Actualizar el Crédito
            credito.setEstadoCredito(estadoCreditoCancelado);
            int filasCredito = creditoDAO.actualizar(credito);

            if (filasCredito > 0) {
                Log.i(TAG, "Crédito ID: " + idCredito + " actualizado a estado Cancelado.");
                // 2. Actualizar la Factura Asociada
                Factura factura = facturaDAO.consultarPorId(credito.getIdFactura());
                if (factura != null) {
                    factura.setEsCredito(esCreditoFacturaDestino);
                    factura.setEstadoFactura(estadoFacturaDestino);
                    // Opcional: Cambiar tipo de pago si se revierte a no crédito
                    // factura.setTipoPago(tipoPagoFacturaDestino);
                    int filasFactura = facturaDAO.actualizar(factura);
                    if (filasFactura > 0) {
                        Log.i(TAG, "Factura ID: " + factura.getIdFactura() + " asociada actualizada (ES_CREDITO=0, Estado=Pendiente).");
                        db.setTransactionSuccessful(); // Marcar éxito SOLO si AMBAS actualizaciones funcionan
                        exitoFinal = true;
                    } else {
                        Log.e(TAG, "Error al actualizar la factura asociada (ID: " + factura.getIdFactura() + ") tras cancelar crédito.");
                    }
                } else {
                    Log.e(TAG, "No se encontró la factura asociada (ID: " + credito.getIdFactura() + ") para actualizar.");
                }
            } else {
                Log.e(TAG, "Error al actualizar el estado del crédito ID: " + idCredito + " a Cancelado.");
            }

        } catch (Exception e) {
            Log.e(TAG, "Excepción al cancelar crédito ID " + idCredito, e);
            exitoFinal = false;
        } finally {
            if (db != null) {
                db.endTransaction(); // Finalizar transacción (Commit o Rollback)
            }
            // No cerrar conexión dbHelper aquí
        }
        if(exitoFinal) {
            Log.i(TAG, "Proceso de cancelación para Crédito ID " + idCredito + " completado exitosamente.");
            cargarTodosLosCreditos(); // Recargar listas
            // Limpiar selección actual
            if (creditoSeleccionado.getValue() != null && creditoSeleccionado.getValue().getIdCredito() == idCredito) {
                creditoSeleccionado.postValue(null);
            }
            // Considera notificar al FacturaViewModel si es necesario que él también refresque
            // ((FacturaViewModel) // obtener instancia // ).cargarTodasLasFacturas();
        }
        return exitoFinal;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "CreditoViewModel cleared");
    }
}