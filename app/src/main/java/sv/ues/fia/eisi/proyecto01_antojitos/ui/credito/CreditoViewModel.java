package sv.ues.fia.eisi.proyecto01_antojitos.ui.credito; // Ajusta el paquete si es necesario

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
// import android.database.sqlite.SQLiteException;
import android.util.Log;
// import android.widget.Toast; // No se usa Toast directamente en ViewModels

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// import java.util.stream.Collectors; // API 24+

// import sv.ues.fia.eisi.proyecto01_antojitos.R;
import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
// Importar POJO y DAO
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.Credito;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.credito.CreditoDAO;
// Importar FacturaDAO y Factura para la lógica de filtrado y actualización
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.Factura;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.factura.FacturaDAO;


public class CreditoViewModel extends AndroidViewModel {

    private static final String TAG = "CreditoViewModel";

    // LiveData
    private final MutableLiveData<List<Credito>> listaTodosCreditos = new MutableLiveData<>();
    private final MutableLiveData<List<Credito>> listaCreditosFiltradosParaUi = new MutableLiveData<>();
    private final MutableLiveData<Credito> creditoSeleccionado = new MutableLiveData<>();

    private DBHelper dbHelper;

    // Constantes para estados de Crédito
    private static final String ESTADO_CREDITO_ACTIVO = "Activo";
    private static final String ESTADO_CREDITO_PAGADO = "Pagado";
    // ESTADO_CREDITO_CANCELADO ya no se usa para el flujo de eliminación física,
    // pero podría ser útil para otras lógicas si existieran. Se puede comentar o eliminar si no se usa en ningún otro lado.
    // private static final String ESTADO_CREDITO_CANCELADO = "Cancelado";

    // Constantes para estados/tipos de Factura relevantes
    private static final String ESTADO_FACTURA_PENDIENTE = "Pendiente"; // Estado al que vuelve la factura
    // private static final String TIPO_PAGO_CONTADO = "Contado"; // Opcional

    public CreditoViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DBHelper(application);
        Log.d(TAG, "CreditoViewModel inicializado.");
    }

    // --- Getters para LiveData ---

    public LiveData<List<Credito>> getListaTodosCreditos() { return listaTodosCreditos; }
    public LiveData<List<Credito>> getListaCreditosFiltradosParaUi() { return listaCreditosFiltradosParaUi; }
    public LiveData<Credito> getCreditoSeleccionado() { return creditoSeleccionado; }

    // --- Métodos para Cargar/Consultar (sin cambios respecto a tu versión) ---

    public void cargarTodosLosCreditosYFiltrarParaUi() {
        Log.d(TAG, "Cargando todos los créditos y filtrando para UI (Editar/Eliminar)...");
        SQLiteDatabase db = null;
        List<Credito> todosLosCreditosObtenidos = Collections.emptyList();
        List<Credito> creditosFiltrados = new ArrayList<>();

        try {
            db = dbHelper.getReadableDatabase();
            CreditoDAO creditoDAO = new CreditoDAO(db);
            FacturaDAO facturaDAO = new FacturaDAO(db);

            todosLosCreditosObtenidos = creditoDAO.obtenerTodos();
            listaTodosCreditos.postValue(todosLosCreditosObtenidos);

            if (todosLosCreditosObtenidos != null && !todosLosCreditosObtenidos.isEmpty()) {
                for (Credito credito : todosLosCreditosObtenidos) {
                    if (ESTADO_CREDITO_ACTIVO.equalsIgnoreCase(credito.getEstadoCredito())) {
                        Factura facturaAsociada = facturaDAO.consultarPorId(credito.getIdFactura());
                        if (facturaAsociada != null && facturaAsociada.getEsCredito() == 1) {
                            creditosFiltrados.add(credito);
                        }
                    }
                }
            }
            listaCreditosFiltradosParaUi.postValue(creditosFiltrados);
            Log.d(TAG, "Créditos cargados: Todos=" + (todosLosCreditosObtenidos != null ? todosLosCreditosObtenidos.size() : 0) +
                    ", Filtrados para UI (Activos y Factura.EsCredito=1)=" + creditosFiltrados.size());

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar y filtrar créditos para UI", e);
            listaTodosCreditos.postValue(Collections.emptyList());
            listaCreditosFiltradosParaUi.postValue(Collections.emptyList());
        }
    }

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

    // --- Métodos para Modificar Crédito (insertar, actualizarFechaLimite sin cambios) ---

    public long insertarCredito(Credito credito) {
        Log.d(TAG, "Intentando insertar Crédito para Factura ID: " + credito.getIdFactura());
        SQLiteDatabase db = null;
        long nuevoId = -1;
        try {
            db = dbHelper.getWritableDatabase();
            FacturaDAO facturaDAO = new FacturaDAO(db);
            Factura facturaAsociada = facturaDAO.consultarPorId(credito.getIdFactura());
            if (facturaAsociada == null || facturaAsociada.getEsCredito() != 1) {
                Log.e(TAG, "Intento de insertar crédito para una factura que NO es a crédito o no existe. Factura ID: " + credito.getIdFactura());
                return -1;
            }

            CreditoDAO creditoDAO = new CreditoDAO(db);
            nuevoId = creditoDAO.insertar(credito);

        } catch (Exception e) {
            Log.e(TAG, "Error insertando crédito VM para Factura ID " + credito.getIdFactura(), e);
            nuevoId = -1;
        }
        if(nuevoId != -1) {
            Log.i(TAG,"Crédito insertado exitosamente con ID: " + nuevoId);
            cargarTodosLosCreditosYFiltrarParaUi();
        }
        return nuevoId;
    }


    public boolean actualizarFechaLimite(int idCredito, String nuevaFechaLimite) {
        Log.d(TAG, "Intentando actualizar fecha límite para Crédito ID: " + idCredito);
        SQLiteDatabase db = null;
        boolean exito = false;
        try {
            db = dbHelper.getWritableDatabase();
            CreditoDAO creditoDAO = new CreditoDAO(db);
            Credito credito = creditoDAO.consultarPorId(idCredito);

            if (credito != null) {
                FacturaDAO facturaDAO = new FacturaDAO(db);
                Factura facturaAsociada = facturaDAO.consultarPorId(credito.getIdFactura());

                if (!ESTADO_CREDITO_ACTIVO.equalsIgnoreCase(credito.getEstadoCredito()) ||
                        facturaAsociada == null || facturaAsociada.getEsCredito() != 1) {
                    Log.w(TAG, "Crédito ID " + idCredito + " ya no es elegible para actualización de fecha (estado/factura cambió).");
                    return false;
                }

                if (!nuevaFechaLimite.equals(credito.getFechaLimitePago())) {
                    credito.setFechaLimitePago(nuevaFechaLimite);
                    int filas = creditoDAO.actualizar(credito);
                    exito = (filas > 0);
                } else {
                    Log.d(TAG,"La fecha límite proporcionada es la misma que la actual.");
                    exito = true;
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
            consultarCreditoPorId(idCredito);
            cargarTodosLosCreditosYFiltrarParaUi();
        }
        return exito;
    }

    /**
     * Elimina FÍSICAMENTE un crédito de la base de datos y actualiza la factura asociada.
     * ADVERTENCIA: Esta acción es irreversible y pierde el historial del crédito.
     * Solo se permite si el crédito no tiene pagos realizados y no está en estado "Pagado".
     *
     * @param idCredito El ID del crédito a eliminar.
     * @return true si el crédito fue eliminado exitosamente (o ya no existía), false en caso contrario.
     */
    public boolean eliminarCredito(int idCredito) { // Método renombrado y con nueva lógica
        Log.i(TAG, "Intentando ELIMINAR FÍSICAMENTE Crédito ID: " + idCredito + " y actualizar Factura asociada.");
        SQLiteDatabase db = null; // Se inicializará según sea necesario (lectura/escritura)
        boolean exitoFinal = false;

        String estadoFacturaObjetivo = ESTADO_FACTURA_PENDIENTE;
        int esCreditoFacturaObjetivo = 0; // 0 = Factura ya no es a crédito

        try {
            // Paso 1: Consultar el crédito para validaciones (fuera de la transacción de escritura)
            db = dbHelper.getReadableDatabase();
            CreditoDAO creditoDAOReadOnly = new CreditoDAO(db);
            Credito credito = creditoDAOReadOnly.consultarPorId(idCredito);

            if (credito == null) {
                Log.w(TAG, "Crédito ID " + idCredito + " no encontrado. Se considera ya eliminado.");
                return true; // Idempotencia: si no existe, la "eliminación" es exitosa.
            }

            // Paso 2: Validaciones críticas
            if (credito.getMontoPagado() > 0) {
                Log.w(TAG, "Crédito ID " + idCredito + " tiene pagos realizados (" + credito.getMontoPagado() + "). NO SE PUEDE ELIMINAR FÍSICAMENTE.");
                return false;
            }
            if (ESTADO_CREDITO_PAGADO.equalsIgnoreCase(credito.getEstadoCredito())) {
                Log.w(TAG, "Crédito ID " + idCredito + " está en estado 'Pagado'. NO SE PUEDE ELIMINAR FÍSICAMENTE.");
                return false;
            }
            // Aquí puedes añadir otras validaciones si es necesario, ej. verificar estado "Activo".
            // Por ahora, si no tiene pagos y no está "Pagado", se permite intentar la eliminación.


            // Paso 3: Proceder con la transacción para actualizar factura y eliminar crédito
            db = dbHelper.getWritableDatabase(); // Obtener instancia para escritura
            db.beginTransaction(); // Iniciar transacción

            FacturaDAO facturaDAO = new FacturaDAO(db); // DAO para operaciones de factura
            CreditoDAO creditoDAO = new CreditoDAO(db); // DAO para operaciones de crédito

            Factura facturaAsociada = facturaDAO.consultarPorId(credito.getIdFactura());
            if (facturaAsociada == null) {
                // Esto sería un estado de datos inconsistente si el crédito existe pero su factura no.
                Log.e(TAG, "Error crítico: Factura asociada (ID: " + credito.getIdFactura() +
                        ") no encontrada durante la eliminación del crédito ID " + idCredito + ". Revisar consistencia de datos.");
                // db.endTransaction() se llamará en finally, revirtiendo la transacción.
                return false;
            }

            // 3.1. Actualizar la Factura Asociada
            facturaAsociada.setEsCredito(esCreditoFacturaObjetivo);
            facturaAsociada.setEstadoFactura(estadoFacturaObjetivo);
            int filasFacturaActualizadas = facturaDAO.actualizar(facturaAsociada);

            if (filasFacturaActualizadas > 0) {
                Log.i(TAG, "Factura ID: " + facturaAsociada.getIdFactura() +
                        " asociada actualizada (ES_CREDITO=" + esCreditoFacturaObjetivo +
                        ", Estado='" + estadoFacturaObjetivo + "').");

                // 3.2. Eliminar FÍSICAMENTE el Crédito
                int filasCreditoEliminado = creditoDAO.eliminar(idCredito); // Usar el método del DAO

                if (filasCreditoEliminado > 0) {
                    Log.i(TAG, "Crédito ID: " + idCredito + " ELIMINADO FÍSICAMENTE de la base de datos.");
                    db.setTransactionSuccessful(); // Marcar la transacción como exitosa
                    exitoFinal = true;
                } else {
                    Log.e(TAG, "Error al eliminar físicamente el crédito ID: " + idCredito +
                            ". No se encontraron filas para eliminar. Se revertirá la actualización de la factura.");
                    // La transacción se revertirá porque setTransactionSuccessful no se llamó después de esto.
                }
            } else {
                Log.e(TAG, "Error al actualizar la factura asociada (ID: " + facturaAsociada.getIdFactura() +
                        ") antes de eliminar el crédito. Se revertirá.");
                // La transacción se revertirá.
            }

        } catch (Exception e) {
            Log.e(TAG, "Excepción durante el proceso de eliminación física del crédito ID " + idCredito, e);
            exitoFinal = false; // Asegurar que exitoFinal sea false en caso de excepción.
        } finally {
            if (db != null && db.inTransaction()) { // Solo finalizar si la transacción se inició realmente
                db.endTransaction(); // Commit si setTransactionSuccessful fue llamado, sino Rollback.
            }
            // No cierres la instancia de db aquí si es gestionada por dbHelper globalmente
        }

        if (exitoFinal) { // Solo si la eliminación física fue exitosa
            Log.i(TAG, "Proceso de ELIMINACIÓN FÍSICA para Crédito ID " + idCredito + " completado exitosamente.");
            // Actualizar LiveData para que la UI refleje los cambios.
            cargarTodosLosCreditosYFiltrarParaUi();
            // Limpiar la selección actual si era el crédito que se acaba de eliminar.
            if (creditoSeleccionado.getValue() != null && creditoSeleccionado.getValue().getIdCredito() == idCredito) {
                creditoSeleccionado.postValue(null);
            }
        } else {
            // Si no fue exitoso y no fue porque ya estaba eliminado (ese caso retorna true antes), loguear.
            // Las causas específicas (ej. monto pagado > 0) ya se loguearon y retornaron false antes.
            // Este log es para fallos dentro de la transacción o excepciones.
            Log.w(TAG, "Proceso de ELIMINACIÓN FÍSICA para Crédito ID " + idCredito + " falló o no se realizó un cambio efectivo.");
        }
        return exitoFinal;
    }


    // --- Método para Realizar Abonos (sin cambios respecto a tu versión) ---
    public boolean realizarAbono(int idCredito, double montoAbono) {
        Log.i(TAG, "Intentando realizar abono de " + montoAbono + " para Crédito ID: " + idCredito);
        if (montoAbono <= 0) {
            Log.w(TAG, "Monto de abono inválido (debe ser positivo): " + montoAbono);
            return false;
        }

        SQLiteDatabase db = null;
        boolean exitoFinal = false;
        boolean creditoSaldado = false;

        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            CreditoDAO creditoDAO = new CreditoDAO(db);
            FacturaDAO facturaDAO = new FacturaDAO(db);

            Credito credito = creditoDAO.consultarPorId(idCredito);

            if (credito == null) {
                Log.w(TAG, "No se encontró crédito con ID " + idCredito + " para realizar abono.");
                return false;
            }
            if (!ESTADO_CREDITO_ACTIVO.equalsIgnoreCase(credito.getEstadoCredito())) {
                Log.w(TAG, "Intento de abonar a crédito no activo. ID: " + idCredito + ", Estado: " + credito.getEstadoCredito());
                return false;
            }
            double saldoActual = credito.getSaldoPendiente();
            double tolerancia = 0.001;
            if (montoAbono > saldoActual + tolerancia) {
                Log.w(TAG, "Monto de abono (" + montoAbono + ") excede el saldo pendiente (" + saldoActual + ") para Crédito ID: " + idCredito);
                return false;
            }

            double nuevoMontoPagado = credito.getMontoPagado() + montoAbono;
            double nuevoSaldoPendiente = saldoActual - montoAbono;

            credito.setMontoPagado(nuevoMontoPagado);
            credito.setSaldoPendiente(Math.max(0.0, nuevoSaldoPendiente));

            if (credito.getSaldoPendiente() < tolerancia) {
                credito.setEstadoCredito(ESTADO_CREDITO_PAGADO);
                creditoSaldado = true;
                Log.i(TAG, "Crédito ID: " + idCredito + " saldado con este abono.");
            } else {
                Log.i(TAG, "Crédito ID: " + idCredito + " actualizado. Nuevo Saldo: " + credito.getSaldoPendiente());
            }

            int filasCredito = creditoDAO.actualizar(credito);

            if (filasCredito > 0) {
                if (creditoSaldado) {
                    Factura facturaAsociada = facturaDAO.consultarPorId(credito.getIdFactura());
                    if (facturaAsociada != null) {
                        facturaAsociada.setEstadoFactura(ESTADO_CREDITO_PAGADO); // Asumiendo que "Pagado" es un estado válido para factura
                        int filasFactura = facturaDAO.actualizar(facturaAsociada);
                        if (filasFactura > 0) {
                            Log.i(TAG,"Factura asociada ID: " + facturaAsociada.getIdFactura() + " actualizada a Pagada.");
                            db.setTransactionSuccessful();
                            exitoFinal = true;
                        } else {
                            Log.e(TAG,"Error al actualizar estado de la factura asociada ID: " + facturaAsociada.getIdFactura());
                            // exitoFinal permanece false, la transacción se revertirá
                        }
                    } else {
                        Log.e(TAG,"No se encontró la factura asociada ID: " + credito.getIdFactura() + " para actualizar su estado.");
                        // exitoFinal permanece false
                    }
                } else {
                    // Si el crédito se actualizó pero no se saldó, la operación sobre el crédito es exitosa.
                    db.setTransactionSuccessful();
                    exitoFinal = true;
                }
            } else {
                Log.e(TAG, "Error al actualizar el crédito ID: " + idCredito + " en la base de datos.");
                // exitoFinal permanece false
            }

        } catch (Exception e) {
            Log.e(TAG, "Excepción al realizar abono para crédito ID " + idCredito, e);
            exitoFinal = false;
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }

        if (exitoFinal) {
            Log.i(TAG, "Abono para Crédito ID " + idCredito + " procesado exitosamente.");
            consultarCreditoPorId(idCredito); // Actualiza el LiveData del crédito seleccionado
            cargarTodosLosCreditosYFiltrarParaUi(); // Actualiza la lista del spinner
        }
        return exitoFinal;
    }

    public void limpiarCreditoSeleccionado() {
        creditoSeleccionado.postValue(null);
        Log.d(TAG, "LiveData creditoSeleccionado limpiado.");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Generalmente no cierras dbHelper aquí si es compartido o gestionado por Application.
        Log.d(TAG, "CreditoViewModel cleared");
    }
}