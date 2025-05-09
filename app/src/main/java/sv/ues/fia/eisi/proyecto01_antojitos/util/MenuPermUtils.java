package sv.ues.fia.eisi.proyecto01_antojitos.util;

import java.util.Map;
import static java.util.Map.entry;

import sv.ues.fia.eisi.proyecto01_antojitos.R;

/** Mapea cada ítem del Navigation Drawer a su permiso CRUD. */
public final class MenuPermUtils {

    private MenuPermUtils() { /* clase estática */ }

    /** id de menu → id de permiso */
    public static final Map<Integer, String> MENU_TO_PERM = Map.ofEntries(
            entry(R.id.nav_home,             "todo_admin"),         // visible para todos
            entry(R.id.nav_cliente,          "cliente_consultar"),
            entry(R.id.nav_sucursal,         "sucursal_consultar"),
            entry(R.id.nav_repartidor,       "reparto_consultar"),
            entry(R.id.nav_producto,         "producto_consultar"),
            entry(R.id.nav_categoria_producto,"categoriaproducto_consultar"),
            entry(R.id.nav_pedido,           "pedido_consultar"),
            entry(R.id.nav_factura,          "factura_consultar"),
            entry(R.id.nav_credito,          "credito_consultar"),
            entry(R.id.nav_detalle_pedido,   "detallepedido_consultar"),
            entry(R.id.nav_tipo_evento,      "tipoevento_consultar"),
            entry(R.id.nav_direccion,        "direccion_consultar"),
            entry(R.id.nav_departamento,     "departamento_consultar"),
            entry(R.id.nav_municipio,        "municipio_consultar"),
            entry(R.id.nav_distrito,         "distrito_consultar"),
            entry(R.id.nav_datos_producto,   "datosproducto_consultar")
    );

}
