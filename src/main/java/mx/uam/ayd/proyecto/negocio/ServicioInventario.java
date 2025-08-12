package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioInventario {

    private final ProductoRepository productoRepository;
    // constructor de la clase ServicioInventario,
    // donde se está utilizando inyección de dependencias para recibir un
    // objeto de tipo ProductoRepository y asignarlo a un atributo interno de la clase.
    @Autowired
    public ServicioInventario(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    //El método que se encarga de ir a recoge todos los productos que existen en la BD
    // y los devuelve en una lista.
   public List<Producto> recuperaProducto() {
        List <Producto> productos = new ArrayList<>();

        for(Producto producto:productoRepository.findAll()) {
            productos.add(producto);
        }

        return productos;
    }
    //metodo que se encarga de eliminar un producto siempre y cuando el Id ingresado sea valido de lo contrasio
    //regresa mensajes de advertencia en el caso de que se ingrese un numero nulo,negativo o el id no se encuentre
    //en la BD y este metodo recibe como el id de un producto de tipo long
    public void eliminaProducto(Long idproducto) {

        if (idproducto == null || idproducto <= 0) {
            throw new IllegalArgumentException("El Id del producto no puede ser nulo o negativo");
        }

        Producto producto = productoRepository.findById(idproducto).orElse(null);
        if (producto == null) {
            throw new IllegalArgumentException("No se encontró el producto");
        }



        productoRepository.delete(producto); // <-- se eliminan también los umbrales


    }


}
