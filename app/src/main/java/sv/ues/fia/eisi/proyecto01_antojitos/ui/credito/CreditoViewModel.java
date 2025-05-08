package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito; // Ajusta el paquete

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors; // API 24+ para filtrar

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
// Importar POJO y DAO
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.Credito;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.CreditoDAO;
// Importar FacturaDAO y FacturaViewModel si necesitas lógica relacionada (ej. actualizar estado factura)
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.Factura;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaDAO;


public class CreditoViewModel extends AndroidViewModel {

    private static final String TAG = "CreditoViewModel";

    // LiveData para listas
    private final MutableLiveData<List<Credito>> listaTodosCreditos = new MutableLiveData<>();
    private final MutableLiveData<List<Credito>> listaCreditosActivos = new MutableLiveData<>();
    // LiveData para un crédito específico (resultado de consulta)
    private final MutableLiveData<Credito> creditoSeleccionado = new MutableLiveData<>();

    private DBHelper dbHelper;
    // Podríamos necesitar FacturaDAO para actualizar estado de Factura al cancelar crédito
    // private FacturaDAO facturaDAO;

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
            todos = dao.obtenerTodos(); // Asume que existe en DAO
            listaTodosCreditos.postValue(todos);

            // Filtrar activos
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Credito> activos = todos.stream()
                        .filter(c -> "Activo".equalsIgnoreCase(c.getEstadoCredito())) // Ajusta el string si es diferente
                        .collect(Collectors.toList());
                listaCreditosActivos.postValue(activos);
                Log.d(TAG, "Créditos cargados: Todos=" + todos.size() + ", Activos=" + activos.size());
            } else {
                // Lógica de filtrado para API < 24
                List<Credito> activos = new ArrayList<>();
                for (Credito c : todos){
                    if ("Activo".equalsIgnoreCase(c.getEstadoCredito())){
                        activos.add(c);
                    }
                }
                listaCreditosActivos.postValue(activos);
                Log.d(TAG, "Créditos cargados: Todos=" + todos.size() + ", Activos=" + activos.size());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar todos los créditos", e);
            listaTodosCreditos.postValue(Collections.emptyList());
            listaCreditosActivos.postValue(Collections.emptyList());
        } finally {
            // No cerrar db
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
            credito = dao.consultarPorId(idCredito); // Usa método del DAO
            creditoSeleccionado.postValue(credito);
        } catch (Exception e) {
            Log.e(TAG, "Error consultando crédito por ID " + idCredito, e);
            creditoSeleccionado.postValue(null);
        } finally {
            // No cerrar db
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
            credito = dao.consultarPorIdFactura(idFactura); // Usa método del DAO
            creditoSeleccionado.postValue(credito);
        } catch (Exception e) {
            Log.e(TAG, "Error consultando crédito por ID Factura " + idFactura, e);
            creditoSeleccionado.postValue(null);
        } finally {
            // No cerrar db
            Log.d(TAG, "Consulta crédito por ID Factura " + idFactura + " finalizada. Encontrado: " + (credito != null));
        }
    }

    // --- Métodos para Modificar Crédito ---

    /**
     * Actualiza la fecha límite de pago de un crédito.
     * @param idCredito El ID del crédito a actualizar.
     * @param nuevaFechaLimite La nueva fecha límite en formato YYYY-MM-DD.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarFechaLimite(int idCredito, String nuevaFechaLimite) {
        Log.d(TAG, "Intentando actualizar fecha límite para Crédito ID: " + idCredito);
        SQLiteDatabase db = null;
        boolean exito = false;
        try {
            db = dbHelper.getWritableDatabase();
            CreditoDAO dao = new CreditoDAO(db);
            Credito credito = dao.consultarPorId(idCredito); // Obtener crédito actual
            if (credito != null) {
                credito.setFechaLimitePago(nuevaFechaLimite); // Cambiar solo la fecha
                int filas = dao.actualizar(credito); // Llamar al actualizar del DAO
                exito = (filas > 0);
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
            // Podrías recargar los datos si es necesario
            // consultarCreditoPorId(idCredito);
            // cargarTodosLosCreditos();
        }
        return exito;
    }

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
            nuevoId = dao.insertar(credito); // Llama al insertar del DAO
        } catch (Exception e) {
            Log.e(TAG, "Error insertando crédito VM", e);
            nuevoId = -1;
        } finally {
            // No cerrar DB aquí
        }
        if(nuevoId != -1) {
            Log.i(TAG,"Crédito insertado exitosamente con ID: " + nuevoId);
            // Podrías recargar la lista de créditos si es necesario
            // cargarTodosLosCreditos();
        }
        return nuevoId;
    }

    /**
     * Cancela un crédito (cambia su estado a "Cancelado") y actualiza la factura asociada.
     * La factura vuelve a estado "Pendiente" y se marca como ES_CREDITO = 0.
     * @param idCredito El ID del crédito a cancelar.
     * @return true si AMBAS actualizaciones (Crédito y Factura) fueron exitosas, false en caso contrario.
     */
    public boolean cancelarCredito(int idCredito) {
        Log.i(TAG, "Intentando cancelar Crédito ID: " + idCredito + " y actualizar Factura asociada.");
        SQLiteDatabase db = null;
        boolean exitoFinal = false;
        String estadoCreditoCancelado = "Cancelado";
        String estadoFacturaDestino = "Pendiente"; // Estado al que vuelve la factura
        int esCreditoFacturaDestino = 0; // Ya no será crédito

        try {
            // *** INICIO TRANSACCIÓN (MUY RECOMENDADO) ***
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            CreditoDAO creditoDAO = new CreditoDAO(db);
            FacturaDAO facturaDAO = new FacturaDAO(db); // Necesitamos DAO de Factura

            Credito credito = creditoDAO.consultarPorId(idCredito); // Obtener crédito actual

            if (credito != null) {
                // Validar si ya está cancelado o pagado? (Opcional)
                if (estadoCreditoCancelado.equalsIgnoreCase(credito.getEstadoCredito())){
                    Log.w(TAG,"El crédito ID " + idCredito + " ya estaba cancelado.");
                    db.endTransaction(); // Terminar transacción (sin marcar éxito)
                    return true; // Considerar éxito si ya estaba cancelado
                }
                if ("Pagado".equalsIgnoreCase(credito.getEstadoCredito())){
                    Log.w(TAG,"No se puede cancelar un crédito que ya está pagado (ID: " + idCredito + ").");
                    Toast.makeText(getApplication(), "No se puede cancelar un crédito pagado.", Toast.LENGTH_SHORT).show(); // Mensaje a usuario
                    db.endTransaction();
                    return false;
                }


                // 1. Actualizar el Crédito
                credito.setEstadoCredito(estadoCreditoCancelado);
                // ¿Resetear montos? Depende de reglas de negocio, por ahora solo estado.
                // credito.setMontoPagado(0);
                // credito.setSaldoPendiente(credito.getMontoAutorizadoCredito());
                int filasCredito = creditoDAO.actualizar(credito);

                if (filasCredito > 0) {
                    Log.i(TAG, "Crédito ID: " + idCredito + " actualizado a estado Cancelado.");
                    // 2. Actualizar la Factura Asociada
                    Factura factura = facturaDAO.consultarPorId(credito.getIdFactura());
                    if (factura != null) {
                        factura.setEsCredito(esCreditoFacturaDestino);
                        factura.setEstadoFactura(estadoFacturaDestino);
                        // ¿El tipo de pago vuelve a "Contado" o se queda como "Crédito"?
                        // factura.setTipoPago("Contado"); // Opcional
                        int filasFactura = facturaDAO.actualizar(factura);
                        if (filasFactura > 0) {
                            Log.i(TAG, "Factura ID: " + factura.getIdFactura() + " actualizada a ES_CREDITO=0 y Estado=Pendiente.");
                            db.setTransactionSuccessful(); // *** MARCAR ÉXITO TRANSACCIÓN ***
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

            } else {
                Log.w(TAG, "No se encontró crédito con ID " + idCredito + " para cancelar.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción al cancelar crédito ID " + idCredito, e);
            exitoFinal = false;
        } finally {
            if (db != null) {
                db.endTransaction(); // Finalizar transacción (commit si setSuccessful, rollback si no)
            }
            // No cerrar conexión db aquí si dbHelper es compartido/gestionado.
        }
        if(exitoFinal) {
            Log.i(TAG, "Proceso de cancelación para Crédito ID " + idCredito + " completado exitosamente.");
            // Recargar listas/datos para reflejar cambios
            cargarTodosLosCreditos();
            // Limpiar selección si era este
            if (creditoSeleccionado.getValue() != null && creditoSeleccionado.getValue().getIdCredito() == idCredito) {
                creditoSeleccionado.postValue(null);
            }
            // Podría ser necesario notificar al FacturaViewModel para que recargue también
        }
        return exitoFinal;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "CreditoViewModel cleared");
    }
}