package sv.ues.fia.eisi.proyecto01_antojitos.ui.producto;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import sv.ues.fia.eisi.proyecto01_antojitos.db.DBHelper;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.ProductoDAO;
import sv.ues.fia.eisi.proyecto01_antojitos.ui.producto.Producto;

public class ProductoViewModel extends AndroidViewModel {
    private ProductoDAO productoDAO;
    private MutableLiveData<List<Producto>> productos;
    private MutableLiveData<String> mensajeError;
    private MutableLiveData<Boolean> operacionExitosa;
    private SQLiteDatabase db;

    public ProductoViewModel(Application application) {
        super(application);
        DBHelper dbHelper = new DBHelper(application);
        db = dbHelper.getWritableDatabase();
        productoDAO = new ProductoDAO(db);
        productos = new MutableLiveData<>();
        mensajeError = new MutableLiveData<>();
        operacionExitosa = new MutableLiveData<>();
    }

    public LiveData<List<Producto>> getProductos() {
        return productos;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<Boolean> getOperacionExitosa() {
        return operacionExitosa;
    }

    public void cargarProductos() {
        try {
            List<Producto> listaProductos = productoDAO.obtenerTodosLosProductos();
            productos.setValue(listaProductos);
        } catch (Exception e) {
            mensajeError.setValue("Error al cargar los productos: " + e.getMessage());
        }
    }

    public void guardarProducto(String nombre, int idCategoria, String descripcion) {
        try {
            Producto producto = new Producto();
            producto.setNombreProducto(nombre);
            producto.setIdCategoriaProducto(idCategoria);
            producto.setDescripcionProducto(descripcion);
            producto.setActivoProducto(1);

            if (validarDatosProducto(producto)) {
                long resultado = productoDAO.insertarProducto(producto);
                operacionExitosa.setValue(resultado != -1);
                if (resultado == -1) {
                    mensajeError.setValue("Error al guardar el producto");
                }
            }
        } catch (Exception e) {
            mensajeError.setValue("Error al guardar el producto: " + e.getMessage());
            operacionExitosa.setValue(false);
        }
    }

    public void actualizarProducto(int idProducto, String nombre, int idCategoria, String descripcion) {
        try {
            Producto producto = new Producto();
            producto.setIdProducto(idProducto);
            producto.setNombreProducto(nombre);
            producto.setIdCategoriaProducto(idCategoria);
            producto.setDescripcionProducto(descripcion);

            if (validarDatosProducto(producto)) {
                int resultado = productoDAO.actualizarProducto(producto);
                operacionExitosa.setValue(resultado > 0);
                if (resultado <= 0) {
                    mensajeError.setValue("Error al actualizar el producto");
                }
            }
        } catch (Exception e) {
            mensajeError.setValue("Error al actualizar el producto: " + e.getMessage());
            operacionExitosa.setValue(false);
        }
    }

    public void eliminarProducto(int idProducto) {
        try {
            int resultado = productoDAO.eliminarProducto(idProducto);
            operacionExitosa.setValue(resultado > 0);
            if (resultado <= 0) {
                mensajeError.setValue("Error al eliminar el producto");
            }
        } catch (Exception e) {
            mensajeError.setValue("Error al eliminar el producto: " + e.getMessage());
            operacionExitosa.setValue(false);
        }
    }

    private boolean validarDatosProducto(Producto producto) {
        if (producto.getNombreProducto() == null || producto.getNombreProducto().trim().isEmpty()) {
            mensajeError.setValue("El nombre del producto es requerido");
            return false;
        }

        if (producto.getIdCategoriaProducto() <= 0) {
            mensajeError.setValue("La categoría del producto es requerida");
            return false;
        }

        return true;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}