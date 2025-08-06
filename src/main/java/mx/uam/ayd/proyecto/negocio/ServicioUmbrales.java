package mx.uam.ayd.proyecto.negocio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.uam.ayd.proyecto.datos.UmbralRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioUmbrales {

    // Define a static logger field
    private static final Logger log = LoggerFactory.getLogger(ServicioUmbrales.class);

    private final UmbralRepository umbralRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public ServicioUmbrales(UmbralRepository umbralRepository, ProductoRepository productoRepository) {
        this.umbralRepository = umbralRepository;
        this.productoRepository = productoRepository;
    }

    /*
        Recupera productos con stock > 1
        a traves de sus umbrales
     */

    public List<Producto> recuperaConStockNoCero(){
        return umbralRepository.findUmbralsConStockPositivo().stream()
                .map(Umbral::getProducto) // Convertimos Umbral → Producto
                .collect(Collectors.toList());
    }

    /**
     * Busca un umbral por el ID del producto asociado
     * @param idProducto ID del producto
     * @return Umbral encontrado o null si no existe
     */
    public Umbral findByProducto(long idProducto) {
        return umbralRepository.findByProductoIdProducto(idProducto);
    }

    /**
     * Actualiza un umbral existente (versión mejorada)
     * @param idProducto ID del producto
     * @param minimo Nuevo valor mínimo
     * @return Umbral actualizado
     * @throws IllegalArgumentException si no existe el producto o el umbral
     */
    public Umbral actualizarUmbral(long idProducto, int minimo) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Umbral umbral = umbralRepository.findByProductoIdProducto(idProducto);
        if(umbral == null) {
            throw new IllegalArgumentException("No existe umbral para este producto");
        }

        umbral.setValorMinimo(minimo);
        producto.setUmbral(umbral); // Mantiene la relación bidireccional
        return umbralRepository.save(umbral);
    }

    /**
     * Crea un nuevo umbral para un producto existente (versión mejorada)
     * @param idProducto ID del producto existente
     * @param minimo Valor mínimo del stock
     * @return Umbral creado
     * @throws IllegalArgumentException si ya existe un umbral o el producto no existe
     */
    public Umbral crearUmbral(long idProducto, int minimo) {
        // Verifica que el producto existe
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // Verifica que no tenga umbral
        if(umbralRepository.findByProductoIdProducto(idProducto) != null) {
            throw new IllegalArgumentException("El producto ya tiene umbral");
        }

        // Crear y guardar
        Umbral nuevoUmbral = new Umbral();
        nuevoUmbral.setProducto(producto);
        nuevoUmbral.setValorMinimo(minimo);

        producto.setUmbral(nuevoUmbral); // Establece la relación bidireccional
        return umbralRepository.save(nuevoUmbral);
    }

    /**
     * Recupera productos completos con sus umbrales (versión optimizada)
     * @return Lista de productos con sus umbrales configurados
     */
    public List<Producto> recuperaProductos() {
        return ((List<Umbral>) umbralRepository.findAll()).stream()
                .map(umbral -> {
                    Producto producto = umbral.getProducto();
                    producto.setUmbral(umbral); // Asegura la relación
                    return producto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Configura/actualiza el umbral para un producto (método unificado)
     * @param idProducto ID del producto
     * @param umbralMinimo Nuevo valor del umbral
     * @throws IllegalArgumentException si el producto no existe
     */
    public void configurarUmbral(long idProducto, int umbralMinimo) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Umbral umbral = umbralRepository.findByProductoIdProducto(idProducto);
        if(umbral == null) {
            umbral = new Umbral();
            umbral.setProducto(producto);
        }

        umbral.setValorMinimo(umbralMinimo);
        producto.setUmbral(umbral); // Sincronización bidireccional
        umbralRepository.save(umbral);
    }
}