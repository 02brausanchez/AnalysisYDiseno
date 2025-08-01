package mx.uam.ayd.proyecto.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.uam.ayd.proyecto.datos.UmbralRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;

@Service
public class ServicioUmbrales {

    private final UmbralRepository umbralRepository;

    @Autowired
    public ServicioUmbrales(UmbralRepository umbralRepository) {
        this.umbralRepository = umbralRepository;
    }

    /**
     * Busca un umbral por el ID del producto asociado
     * @param idProducto ID del producto
     * @return Umbral encontrado o null si no existe
     */
    public Umbral findByProducto(long idProducto) {
        return umbralRepository.findByProductoId(idProducto);
    }

    /**
     * Actualiza un umbral existente
     * @param idProducto ID del producto
     * @param minimo Nuevo valor mínimo
     * @return Umbral actualizado
     * @throws IllegalArgumentException si no existe el umbral
     */
    public Umbral actualizarUmbral(long idProducto, int minimo) {
        Umbral umbral = umbralRepository.findByProductoId(idProducto);

        if(umbral == null) {
            throw new IllegalArgumentException("No existe umbral para este producto");
        }

        umbral.setValorMinimo(minimo);
        return umbralRepository.save(umbral);
    }

    /**
     * Crea un nuevo umbral para un producto
     * @param idProducto ID del producto
     * @param minimo Valor mínimo del stock
     * @return Umbral creado
     * @throws IllegalArgumentException si ya existe un umbral para este producto
     */
    public Umbral crearUmbral(long idProducto, int minimo) {
        if(umbralRepository.findByProductoId(idProducto) != null) {
            throw new IllegalArgumentException("Ya existe un umbral para este producto");
        }

        Producto producto = new Producto();
        producto.setIdProducto(idProducto);

        Umbral umbral = new Umbral();
        umbral.setProducto(producto);
        umbral.setValorMinimo(minimo);

        return umbralRepository.save(umbral);
    }
}