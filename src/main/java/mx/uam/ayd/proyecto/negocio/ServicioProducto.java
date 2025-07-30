package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ServicioProducto {

    private static final Logger log = LoggerFactory.getLogger(ServicioProducto.class);
    private final ProductoRepository productoRepository;

    @Autowired
    public ServicioProducto(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;

    }


    public Producto agregaProducto(String nombre, String tipoProducto, String marca, Long precio) {

        // Validar que ningún parámetro sea nulo o vacío
        if(nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser nulo o vacío");
        }

        if(tipoProducto == null || tipoProducto.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de producto no puede ser nulo o vacío");
        }

        if(marca == null || marca.trim().isEmpty()) {
            throw new IllegalArgumentException("la marca no puede ser nulo o vacío");
        }
        if(String.valueOf(precio).equals("0")){
            throw new IllegalArgumentException("el precio no puede ser nulo o vacío");
        }
        // Crea el Producto

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setTipoProducto(tipoProducto);
        producto.setMarca(marca);
        producto.setPrecio(precio);

        productoRepository.save(producto);

        return producto;

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





