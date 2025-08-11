package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
import java.util.List;

/**
 * Repositorio para la entidad {@link Umbral}.
 *
 * Extiende de {@link CrudRepository} para proporcionar operaciones
 * CRUD básicas, además de consultas personalizadas sobre la tabla
 * de umbrales.
 *
 * Se utiliza en la capa de negocio para acceder a los umbrales
 * asociados a productos y verificar sus valores mínimos de stock.
 *
 * @author
 */
public interface UmbralRepository extends CrudRepository<Umbral, Long> {

    /**
     * Busca un umbral a partir del ID de su producto asociado.
     *
     * @param idProducto ID del producto
     * @return umbral correspondiente o {@code null} si no existe
     */
    Umbral findByProductoIdProducto(Long idProducto);


    /**
     * Busca un umbral por su identificador único.
     *
     * @param idUmbral ID del umbral
     * @return umbral encontrado o {@code null} si no existe
     */
    Umbral findByIdUmbral(Long idUmbral);
}
