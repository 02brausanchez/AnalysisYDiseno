package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;

import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface ProductoRepository extends CrudRepository <Producto, Long> {

    public Producto findByNombre(String nombre);

    Optional<Producto> findByNombreAndTipoProductoAndMarcaProducto(String nombre, TipoProducto tipoProducto, MarcaProducto marcaProducto);
}
