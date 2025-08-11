package mx.uam.ayd.proyecto.negocio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uam.ayd.proyecto.datos.VentaRepository;
import mx.uam.ayd.proyecto.datos.DetalleVentaRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import mx.uam.ayd.proyecto.negocio.modelo.DetalleVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

@Service
public class ServicioDetalleVenta{
    private static final Logger log = LoggerFactory.getLogger(ServicioDetalleVenta.class);

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private double subtotal;

    @Autowired
    public ServicioDetalleVenta(VentaRepository ventaRepository, DetalleVentaRepository detalleVentaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
    }

    public DetalleVenta newDetalleVenta(Producto producto, int cantidadVendida, Venta venta, List<DetalleVenta> detallesVenta){
        if(producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        for(DetalleVenta detalleVentaL : detallesVenta){
            if(detalleVentaL.getProducto() == producto){
                throw new IllegalStateException("El producto ya esta en la tabla");
            }
        }
        if(cantidadVendida <= 0) {
            throw new IllegalArgumentException("La cantidad no puede ser menor o igual a 0");
        }
        if(cantidadVendida > producto.getCantidadStock()){
            throw new IllegalStateException("La cantidad vendida no puede ser mayor al stock");
        }
        if(venta == null) {
            throw new IllegalArgumentException("La venta no puede ser nulo");
        }

        log.info("Agregando producto " + producto.getNombre());

        DetalleVenta detalleVenta = new DetalleVenta();
        detalleVenta.setVenta(venta);
        detalleVenta.setProducto(producto);
        double subtotal = producto.getPrecio() * cantidadVendida;
        detalleVenta.setCantidadVendida(cantidadVendida);
        detalleVenta.setSubtotal(subtotal);

        return detalleVenta;
    }
}