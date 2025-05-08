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

    // Constantes para estados/tipos de Factura relevantes
    private static final String ESTADO_FACTURA_PENDIENTE = "Pendiente";
    private static final String ESTADO_FACTURA_PAGADA = "Pagada"; // Constante para factura pagada


    public CreditoViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DBHelper(application);
        Log.d(TAG, "CreditoViewModel inicializado.");
    }

    // --- Getters para LiveData ---
    public LiveData<List<Credito>> getListaTodosCreditos() { return listaTodosCreditos; }
    public LiveData<List<Credito>> getListaCreditosFiltradosParaUi() { return listaCreditosFiltradosParaUi; }
    public LiveData<Credito> getCreditoSeleccionado() { return creditoSeleccionado; }

    // --- Métodos para Cargar/Consultar ---
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
            if (credito != null) {
                Log.d(TAG, "Crédito ID " + idCredito + " consultado. Estado actual: " + credito.getEstadoCredito() + ", Saldo: " + credito.getSaldoPendiente());
            } else {
                Log.d(TAG, "Crédito ID " + idCredito + " no encontrado en consulta.");
            }
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

    // --- Métodos para Modificar Crédito ---
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
            Credito creditoExistente = creditoDAO.consultarPorId(idCredito); // Consultar para obtener el objeto completo

            if (creditoExistente != null) {
                FacturaDAO facturaDAO = new FacturaDAO(db);
                Factura facturaAsociada = facturaDAO.consultarPorId(creditoExistente.getIdFactura());

                if (!ESTADO_CREDITO_ACTIVO.equalsIgnoreCase(creditoExistente.getEstadoCredito()) ||
                        facturaAsociada == null || facturaAsociada.getEsCredito() != 1) {
                    Log.w(TAG, "Crédito ID " + idCredito + " ya no es elegible para actualización de fecha (estado/factura cambió).");
                    return false;
                }

                if (!nuevaFechaLimite.equals(creditoExistente.getFechaLimitePago())) {
                    creditoExistente.setFechaLimitePago(nuevaFechaLimite); // Modificar el objeto
                    int filas = creditoDAO.actualizar(creditoExistente); // Pasar el objeto completo al DAO
                    exito = (filas > 0);
                } else {
                    Log.d(TAG,"La fecha límite proporcionada es la misma que la actual. No se requiere actualización.");
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
            String mensajeLog = "Fecha límite para crédito ID " + idCredito;
            if (creditoSeleccionado.getValue() != null && nuevaFechaLimite.equals(creditoSeleccionado.getValue().getFechaLimitePago())) {
                mensajeLog += " no cambió.";
            } else {
                mensajeLog += " actualizada.";
            }
            Log.i(TAG, mensajeLog);
            consultarCreditoPorId(idCredito);
            // cargarTodosLosCreditosYFiltrarParaUi(); // No es estrictamente necesario si solo cambia la fecha y el estado no
        }
        return exito;
    }

    public boolean eliminarCredito(int idCredito) {
        Log.i(TAG, "Intentando ELIMINAR FÍSICAMENTE Crédito ID: " + idCredito);
        SQLiteDatabase db = null;
        boolean exitoFinal = false;

        String estadoFacturaObjetivo = ESTADO_FACTURA_PENDIENTE;
        int esCreditoFacturaObjetivo = 0;

        try {
            db = dbHelper.getReadableDatabase();
            CreditoDAO creditoDAOReadOnly = new CreditoDAO(db);
            Credito credito = creditoDAOReadOnly.consultarPorId(idCredito);

            if (credito == null) {
                Log.w(TAG, "Crédito ID " + idCredito + " no encontrado. Se considera ya eliminado.");
                return true;
            }

            if (credito.getMontoPagado() > 0) {
                Log.w(TAG, "Crédito ID " + idCredito + " tiene pagos realizados (" + credito.getMontoPagado() + "). NO SE PUEDE ELIMINAR.");
                return false;
            }
            if (ESTADO_CREDITO_PAGADO.equalsIgnoreCase(credito.getEstadoCredito())) {
                Log.w(TAG, "Crédito ID " + idCredito + " está 'Pagado'. NO SE PUEDE ELIMINAR.");
                return false;
            }

            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            FacturaDAO facturaDAO = new FacturaDAO(db);
            CreditoDAO creditoDAO = new CreditoDAO(db);

            Factura facturaAsociada = facturaDAO.consultarPorId(credito.getIdFactura());
            if (facturaAsociada == null) {
                Log.e(TAG, "Error crítico: Factura asociada (ID: " + credito.getIdFactura() + ") no encontrada.");
                db.endTransaction(); // Asegurarse de terminar la transacción antes de retornar
                return false;
            }

            facturaAsociada.setEsCredito(esCreditoFacturaObjetivo);
            facturaAsociada.setEstadoFactura(estadoFacturaObjetivo);
            int filasFacturaActualizadas = facturaDAO.actualizar(facturaAsociada);

            if (filasFacturaActualizadas > 0) {
                Log.i(TAG, "Factura ID: " + facturaAsociada.getIdFactura() + " asociada actualizada.");
                int filasCreditoEliminado = creditoDAO.eliminar(idCredito);
                if (filasCreditoEliminado > 0) {
                    Log.i(TAG, "Crédito ID: " + idCredito + " ELIMINADO FÍSICAMENTE.");
                    db.setTransactionSuccessful();
                    exitoFinal = true;
                } else {
                    Log.e(TAG, "Error al eliminar físicamente crédito ID: " + idCredito + ". Se revertirá.");
                }
            } else {
                Log.e(TAG, "Error al actualizar factura asociada (ID: " + facturaAsociada.getIdFactura() + "). Se revertirá.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción durante eliminación física del crédito ID " + idCredito, e);
            exitoFinal = false;
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }
        if (exitoFinal) {
            Log.i(TAG, "Eliminación física para Crédito ID " + idCredito + " completada.");
            cargarTodosLosCreditosYFiltrarParaUi();
            if (creditoSeleccionado.getValue() != null && creditoSeleccionado.getValue().getIdCredito() == idCredito) {
                creditoSeleccionado.postValue(null);
            }
        } else {
            Log.w(TAG, "Eliminación física para Crédito ID " + idCredito + " falló o no se realizó.");
        }
        return exitoFinal;
    }

    public boolean realizarAbono(int idCredito, double montoAbono) {
        Log.i(TAG, "Intentando realizar abono de " + montoAbono + " para Crédito ID: " + idCredito);
        if (montoAbono <= 0) {
            Log.w(TAG, "Monto de abono inválido (debe ser positivo): " + montoAbono);
            return false;
        }

        SQLiteDatabase db = null;
        boolean exitoFinal = false;
        boolean creditoSeSaldoConEsteAbono = false;

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
            // Solo se pueden realizar abonos a créditos activos
            if (!ESTADO_CREDITO_ACTIVO.equalsIgnoreCase(credito.getEstadoCredito())) {
                Log.w(TAG, "Intento de abonar a crédito no activo. ID: " + idCredito + ", Estado: " + credito.getEstadoCredito());
                return false;
            }

            double saldoActual = credito.getSaldoPendiente();
            double toleranciaComparacionSaldo = 0.001; // Para aceptar montos ligeramente mayores por precisión

            if (montoAbono > saldoActual + toleranciaComparacionSaldo) {
                Log.w(TAG, "Monto de abono (" + montoAbono + ") excede el saldo pendiente (" + saldoActual + ") para Crédito ID: " + idCredito);
                return false;
            }
            // Ajustar montoAbono si es ligeramente superior al saldo (por errores de precisión) pero dentro de la tolerancia
            if (montoAbono > saldoActual && montoAbono <= saldoActual + toleranciaComparacionSaldo) {
                montoAbono = saldoActual;
                Log.d(TAG, "Monto de abono ajustado al saldo actual: " + montoAbono);
            }

            double nuevoMontoPagado = credito.getMontoPagado() + montoAbono;
            double nuevoSaldoPendiente = saldoActual - montoAbono;

            credito.setMontoPagado(nuevoMontoPagado);
            // Asegurar que el saldo pendiente no sea negativo debido a errores de precisión de float/double
            credito.setSaldoPendiente(Math.max(0.0, nuevoSaldoPendiente));

            Log.d(TAG, "Crédito ID " + idCredito + " después de cálculos: MontoPagado=" + credito.getMontoPagado() + ", SaldoPendiente=" + credito.getSaldoPendiente());

            // --- VERIFICAR Y CAMBIAR ESTADO A PAGADO SI CORRESPONDE ---
            // Se considera pagado si el saldo es 0 o un valor muy pequeño (menor a un centavo, si se usan 2 decimales)
            double toleranciaSaldoCero = 0.009;
            if (credito.getSaldoPendiente() <= toleranciaSaldoCero) {
                credito.setEstadoCredito(ESTADO_CREDITO_PAGADO);
                creditoSeSaldoConEsteAbono = true;
                Log.i(TAG, "Crédito ID: " + idCredito + " SALDADO. Estado cambiado a: " + ESTADO_CREDITO_PAGADO);
            } else {
                // Si no se saldó, y estaba activo, debe seguir activo.
                // No es necesario cambiar el estado aquí si ya era "Activo" y no se saldó.
                Log.i(TAG, "Crédito ID: " + idCredito + " aún con saldo pendiente ("+ credito.getSaldoPendiente() +"). Estado actual: " + credito.getEstadoCredito());
            }
            // --- FIN VERIFICACIÓN DE ESTADO ---

            int filasCreditoActualizadas = creditoDAO.actualizar(credito); // Guarda el crédito con su nuevo estado si cambió

            if (filasCreditoActualizadas > 0) {
                Log.d(TAG, "Crédito ID " + idCredito + " actualizado en BD. Filas afectadas: " + filasCreditoActualizadas);
                if (creditoSeSaldoConEsteAbono) {
                    Factura facturaAsociada = facturaDAO.consultarPorId(credito.getIdFactura());
                    if (facturaAsociada != null) {
                        facturaAsociada.setEstadoFactura(ESTADO_FACTURA_PAGADA); // Actualizar estado de la factura
                        int filasFacturaActualizadas = facturaDAO.actualizar(facturaAsociada);
                        if (filasFacturaActualizadas > 0) {
                            Log.i(TAG,"Factura asociada ID: " + facturaAsociada.getIdFactura() + " actualizada a estado: " + ESTADO_FACTURA_PAGADA);
                            db.setTransactionSuccessful();
                            exitoFinal = true;
                        } else {
                            Log.e(TAG,"Error al actualizar estado de la factura asociada ID: " + facturaAsociada.getIdFactura() + ". Abono de crédito se revertirá.");
                            // exitoFinal permanece false
                        }
                    } else {
                        Log.e(TAG,"No se encontró la factura asociada ID: " + credito.getIdFactura() + " para actualizar su estado. Abono de crédito se revertirá.");
                        // exitoFinal permanece false
                    }
                } else {
                    // Si el crédito se actualizó (monto pagado, saldo) pero no se saldó, la operación es exitosa.
                    db.setTransactionSuccessful();
                    exitoFinal = true;
                }
            } else {
                Log.e(TAG, "Error al actualizar el crédito ID: " + idCredito + " en la base de datos. No se afectaron filas.");
                // exitoFinal permanece false
            }

        } catch (Exception e) {
            Log.e(TAG, "Excepción al realizar abono para crédito ID " + idCredito, e);
            exitoFinal = false;
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction(); // Commit o Rollback
            }
        }

        if (exitoFinal) {
            Log.i(TAG, "Abono para Crédito ID " + idCredito + " procesado exitosamente. Refrescando datos para UI...");
            consultarCreditoPorId(idCredito); // Refresca LiveData de creditoSeleccionado con el estado más reciente
            cargarTodosLosCreditosYFiltrarParaUi(); // Refresca lista del spinner (crédito pagado ya no aparecerá si el filtro es por "Activo")
        } else {
            Log.w(TAG, "Abono para Crédito ID " + idCredito + " falló o no se realizó.");
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
        Log.d(TAG, "CreditoViewModel cleared");
    }
}