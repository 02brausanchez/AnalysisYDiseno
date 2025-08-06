package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.DataBufferUShort;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ServicioProducto {

    private static final Logger log = LoggerFactory.getLogger(ServicioProducto.class);
    private final ProductoRepository productoRepository;

    @Autowired
    public ServicioProducto(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public boolean buscarProducto(String nombre, TipoProducto tipoProducto, MarcaProducto marcaProducto) {
        Optional<Producto> resultado = productoRepository.findByNombreAndTipoProductoAndMarcaProducto(nombre, tipoProducto, marcaProducto);
        if (resultado.isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    public Producto agregarProducto(String nombre, TipoProducto tipoProducto, MarcaProducto marcaProducto,
                                   double precio, int cantidad, UnidadProducto unidadProducto, LocalDate fechaCaducidad) {
            if (buscarProducto(nombre, tipoProducto, marcaProducto)) {
                throw new IllegalStateException("El producto ya existe en la base de datos.");
            }

            // Validaciones
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del producto no puede ser nulo o vacío");
            }

            if (tipoProducto == null) {
                throw new IllegalArgumentException("El tipo de producto no puede ser nulo");
            }

            if (marcaProducto == null) {
                throw new IllegalArgumentException("La marca del producto no puede ser nula");
            }

            if (precio <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor a cero");
            }

            if (unidadProducto == null) {
                throw new IllegalArgumentException("La unidad del producto no puede ser nula");
            }

            // Crear el producto
            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setTipoProducto(tipoProducto);
            producto.setMarcaProducto(marcaProducto);
            producto.setPrecio(precio);
            producto.setCantidadStock(cantidad);
            producto.setUnidadProducto(unidadProducto);
            producto.setFechaCaducidad(fechaCaducidad);

            return productoRepository.save(producto);
    }

    public Producto eliminarProducto(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new NoSuchElementException("El campo no puede estar vacio " );
        }
        // Buscar el producto por nombre
        Producto producto= productoRepository.findByNombre(nombre);

        // Eliminar el producto
        productoRepository.delete(producto);

        return producto;
    }

    /**
     * Recupera todos los prodcutos existentes
     *
     * @return Una lista con los productos (o lista vacía)
     */
    public List<Producto> recuperaProductos() {

        System.out.println("productoRepository = "+productoRepository);

        List <Producto> productos = new ArrayList<>();

        for(Producto producto:productoRepository.findAll()) {
            productos.add(producto);
        }

        return productos;
    }
}





