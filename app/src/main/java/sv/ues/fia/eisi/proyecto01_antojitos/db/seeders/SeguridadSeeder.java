package sv.ues.fia.eisi.proyecto01_antojitos.db.seeders;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/** Inserta usuarios, opciones CRUD y accesos (tablas de seguridad). */
public final class SeguridadSeeder {

    private SeguridadSeeder() { }

    public static void poblar(SQLiteDatabase db) {

        Log.i("SeguridadSeeder", "Insertando datos de seguridad…");
        db.beginTransaction();
        try {
            /* ---------- LIMPIEZA ---------- */
            db.execSQL("DELETE FROM ACCESOUSUARIO;");
            db.execSQL("DELETE FROM OPCIONCRUD;");
            db.execSQL("DELETE FROM USUARIO;");

            /* ---------- USUARIOS ---------- */
            db.execSQL(
                    "INSERT OR REPLACE INTO USUARIO(ID_USUARIO,NOM_USUARIO,CLAVE) VALUES" +
                            "('SU','superusuario','12345')," +
                            "('CL','cliente','123')," +
                            "('RP','repartidor','123')," +
                            "('SC','sucursal','123');"
            );

            /* ---------- TODAS LAS OPCIONES CRUD NECESARIAS ---------- */
            db.execSQL(
                    "INSERT OR REPLACE INTO OPCIONCRUD(ID_OPCION,DES_OPCION,NUM_CRUD) VALUES" +

                            /* ───────────  CLIENTE  ─────────── */
                            "('cliente_crear',      'Crear Cliente',           1)," +
                            "('cliente_consultar',  'Consultar Cliente',       2)," +
                            "('cliente_editar',     'Editar Cliente',          3)," +
                            "('cliente_eliminar',   'Eliminar Cliente',        4)," +

                            /* ───────────  REPARTIDOR  ─────────── */
                            "('repartidor_consultar','Consultar Repartidor',   2)," +
                            "('repartidor_editar',   'Editar Repartidor',      3)," +

                            /* ───────────  SUCURSAL  ─────────── */
                            "('sucursal_consultar', 'Consultar Sucursal',      2)," +
                            "('sucursal_editar',    'Editar Sucursal',         3)," +

                            /* ───────────  PRODUCTO  ─────────── */
                            "('producto_crear',     'Crear Producto',          1)," +
                            "('producto_consultar', 'Consultar Producto',      2)," +
                            "('producto_editar',    'Editar Producto',         3)," +
                            "('producto_eliminar',  'Eliminar Producto',       4)," +

                            /* ───────────  DATOS PRODUCTO  ─────────── */
                            "('datosproducto_crear',     'Crear DatosProducto',     1)," +
                            "('datosproducto_consultar', 'Consultar DatosProducto', 2)," +
                            "('datosproducto_editar',    'Editar DatosProducto',    3)," +
                            "('datosproducto_eliminar',  'Eliminar DatosProducto',  4)," +

                            /* ───────────  DETALLE PEDIDO  ─────────── */
                            "('detallepedido_crear',     'Crear DetallePedido',     1)," +
                            "('detallepedido_consultar', 'Consultar DetallePedido', 2)," +
                            "('detallepedido_editar',    'Editar DetallePedido',    3)," +
                            "('detallepedido_eliminar',  'Eliminar DetallePedido',  4)," +

                            /* ───────────  PEDIDO  ─────────── */
                            "('pedido_consultar',   'Consultar Pedido',        2)," +

                            /* ───────────  REPARTO PEDIDO  ─────────── */
                            "('repartopedido_crear',     'Crear RepartoPedido',     1)," +
                            "('repartopedido_consultar', 'Consultar RepartoPedido', 2)," +
                            "('repartopedido_editar',    'Editar RepartoPedido',    3)," +
                            "('repartopedido_eliminar',  'Eliminar RepartoPedido',  4)," +
                            "('reparto_consultar',       'Consultar Repartos',      2)," +  // alias antiguo

                            /* ───────────  FACTURA  ─────────── */
                            "('factura_crear',      'Crear Factura',           1)," +
                            "('factura_consultar',  'Consultar Factura',       2)," +
                            "('factura_editar',     'Editar Factura',          3)," +
                            "('factura_eliminar',   'Eliminar Factura',        4)," +

                            /* ───────────  CREDITO  ─────────── */
                            "('credito_crear',      'Crear Crédito',           1)," +
                            "('credito_consultar',  'Consultar Crédito',       2)," +
                            "('credito_editar',     'Editar Crédito',          3)," +
                            "('credito_eliminar',   'Eliminar Crédito',        4)," +

                            /* ───────────  DIRECCIÓN  ─────────── */
                            "('direccion_crear',    'Crear Dirección',         1)," +

                            /* ───────────  TIPO EVENTO  ─────────── */
                            "('tipoevento_crear',   'Crear TipoEvento',        1)," +
                            "('tipoevento_consultar','Consultar TipoEvento',   2)," +
                            "('tipoevento_editar',  'Editar TipoEvento',       3)," +
                            "('tipoevento_eliminar','Eliminar TipoEvento',     4)," +

                            /* ───────────  CATEGORIA PRODUCTO  ─────────── */
                            "('categoriaproducto_crear',     'Crear CategoríaProducto',     1)," +
                            "('categoriaproducto_consultar', 'Consultar CategoríaProducto', 2)," +
                            "('categoriaproducto_editar',    'Editar CategoríaProducto',    3)," +
                            "('categoriaproducto_eliminar',  'Eliminar CategoríaProducto',  4)," +

                            /* ───────────  GEOGRAFÍA  ─────────── */
                            "('departamento_consultar','Consultar Departamento',2)," +
                            "('municipio_consultar',   'Consultar Municipio',   2)," +
                            "('distrito_consultar',    'Consultar Distrito',    2)," +


            /* ───────────  COMODÍN  ─────────── */
                            "('todo_admin',         'Acceso total',            0);"
            );

            /* ---------- ACCESOS POR USUARIO ---------- */

            /* Superusuario */
            db.execSQL("INSERT OR REPLACE INTO ACCESOUSUARIO VALUES ('todo_admin','SU');");

            /* Cliente */
            db.execSQL(
                    "INSERT OR REPLACE INTO ACCESOUSUARIO VALUES" +
                            "('cliente_consultar','CL')," +
                            "('cliente_editar',   'CL')," +
                            "('direccion_crear',  'CL')," +
                            "('producto_consultar','CL')," +
                            "('detallepedido_crear','CL')," +
                            "('detallepedido_consultar','CL')," +
                            "('detallepedido_editar','CL')," +
                            "('detallepedido_eliminar','CL')," +
                            "('pedido_consultar','CL')," +
                            "('factura_consultar','CL')," +
                            "('credito_consultar','CL')," +
                            "('departamento_consultar','CL')," +
                            "('municipio_consultar',   'CL')," +
                            "('distrito_consultar',    'CL')," +
                            "('sucursal_consultar','CL');"
            );

            /* Repartidor */
            db.execSQL(
                    "INSERT OR REPLACE INTO ACCESOUSUARIO VALUES" +
                            "('repartidor_consultar','RP')," +
                            "('repartidor_editar',   'RP')," +
                            "('reparto_consultar',   'RP')," +
                            "('repartopedido_consultar','RP')," +
                            "('pedido_consultar',    'RP')," +
                            "('departamento_consultar','RP')," +
                            "('municipio_consultar',   'RP')," +
                            "('distrito_consultar',    'RP');"
            );

            /* Sucursal */
            db.execSQL(
                    "INSERT OR REPLACE INTO ACCESOUSUARIO VALUES" +
                            "('sucursal_consultar','SC')," +
                            "('sucursal_editar',   'SC')," +

                            "('producto_crear',    'SC')," +
                            "('producto_consultar','SC')," +
                            "('producto_editar',   'SC')," +
                            "('producto_eliminar', 'SC')," +

                            "('datosproducto_crear',    'SC')," +
                            "('datosproducto_consultar','SC')," +
                            "('datosproducto_editar',   'SC')," +
                            "('datosproducto_eliminar', 'SC')," +

                            "('tipoevento_crear',   'SC')," +
                            "('tipoevento_consultar','SC')," +
                            "('tipoevento_editar',  'SC')," +
                            "('tipoevento_eliminar','SC')," +

                            "('pedido_consultar',   'SC')," +

                            "('repartopedido_crear',    'SC')," +
                            "('repartopedido_consultar','SC')," +
                            "('repartopedido_editar',   'SC')," +
                            "('repartopedido_eliminar', 'SC')," +

                            "('repartidor_consultar','SC')," +
                            "('cliente_consultar',  'SC')," +

                            "('factura_crear',      'SC')," +
                            "('factura_consultar',  'SC')," +
                            "('factura_editar',     'SC')," +
                            "('factura_eliminar',   'SC')," +

                            "('credito_crear',      'SC')," +
                            "('credito_consultar',  'SC')," +
                            "('credito_editar',     'SC')," +
                            "('credito_eliminar',   'SC')," +

                            "('categoriaproducto_crear',     'SC')," +
                            "('categoriaproducto_consultar', 'SC')," +
                            "('categoriaproducto_editar',    'SC')," +
                            "('categoriaproducto_eliminar',  'SC')," +

                            "('departamento_consultar','SC')," +
                            "('municipio_consultar',   'SC')," +
                            "('distrito_consultar',    'SC')," +

                            "('datosproducto_crear',    'SC'), " +
                            "('datosproducto_consultar','SC')," +
                            "('datosproducto_editar',   'SC')," +
                            "('datosproducto_eliminar', 'SC')," +

                            "('tipoevento_crear',       'SC')," +
                            "('tipoevento_consultar',   'SC')," +
                            "('tipoevento_editar',      'SC')," +
                            "('tipoevento_eliminar',    'SC');"

            );

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        Log.i("SeguridadSeeder", "Datos de seguridad insertados.");
    }
}
