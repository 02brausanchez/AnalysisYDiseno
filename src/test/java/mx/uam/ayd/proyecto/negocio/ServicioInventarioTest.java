package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicioInventarioTest {
    
    @Mock

    private ProductoRepository productoRepository;
    
    @InjectMocks

    private ServicioInventario servicioInventario;


    @Test
    void testInventario() {
        // Caso 1: No hay productos guardados, regresa lista vacía
        List<Producto> productos = servicioInventario.recuperaProducto();
        assertEquals(0, productos.size());

        // Caso 2: Hay productos guardados, regresa lista con productos
        ArrayList<Producto> lista = new ArrayList<>();

        Producto producto1 = new Producto();
        producto1.setNombre("croqueta");
        producto1.setPrecio(500);
        producto1.setCantidadStock(20);

        Producto producto2 = new Producto();
        producto2.setNombre("croqueta");
        producto2.setPrecio(500);
        producto2.setCantidadStock(20);

        lista.add(producto1);
        lista.add(producto2);

        when(productoRepository.findAll()).thenReturn(lista);

        productos = servicioInventario.recuperaProducto();
        assertEquals(2, productos.size());
    }


    @Test
   void testeliminaProducto() {
        Producto p=new Producto();
        p.setIdProducto(1L);
        // Validar que no sean nulos o vacíos

        if (p.getIdProducto() == null || p.getIdProducto ()<=0) {
            throw new IllegalArgumentException("El Id del producto no puede ser nulo o negativo");
        }

        // caso si el Id no es nulo pero no se encuentra en la base de datos

        Producto producto = productoRepository.findById(5L).orElse(null);

        if (producto == null) {
            throw new IllegalArgumentException("No se encontró el producto");
        }

    }
} 