package mx.uam.ayd.proyecto.presentacion.registroVentas;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioVenta;
import mx.uam.ayd.proyecto.negocio.ServicioDetalleVenta;
import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import mx.uam.ayd.proyecto.negocio.modelo.DetalleVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

@Component
public class ControlRegistroVentas {
    private final ServicioVenta servicioVenta;
    private final ServicioDetalleVenta servicioDetalleVenta;
    private final ServicioProducto servicioProducto;
    private final VentanaRegistroVentas ventana;

    private Venta venta;

    @Autowired
    public ControlRegistroVentas(
            ServicioVenta servicioVenta,
            ServicioDetalleVenta servicioDetalleVenta,
            ServicioProducto servicioProducto,
            VentanaRegistroVentas ventana) {
        this.servicioVenta = servicioVenta;
        this.servicioDetalleVenta = servicioDetalleVenta;
        this.servicioProducto = servicioProducto;
        this.ventana = ventana;
    }

    @PostConstruct
    public void init() {
        ventana.setControlRegistroVentas(this);}

    public void inicia() {
        List <Producto> productos = servicioProducto.recuperaProductos();
        try {
            this.venta = servicioVenta.crearVenta();
        } catch(Exception ex) {
            ventana.muestraDialogoConMensaje("Ocurrio un error al crear la venta");
        }

        ventana.muestra(productos, venta);
    }

    public DetalleVenta crearDetalleVenta(Producto producto, int cantidad, List<DetalleVenta> detalleVentas) {
        try {
            DetalleVenta detalleVenta = servicioDetalleVenta.newDetalleVenta(producto, cantidad, venta, detalleVentas);
            ventana.muestraDialogoConMensaje("Producto agregado exitosamente");
            return detalleVenta;
        } catch (Exception ex) {
            ventana.muestraDialogoConMensaje("Error al agregar el producto: " + ex.getMessage());
            throw ex;
        }
    }

    public void actualizarStock(List<DetalleVenta> detallesVenta) {
        for (DetalleVenta detalle : detallesVenta) {
            servicioVenta.actualizarStock(detalle.getProducto(), detalle.getCantidadVendida());
        }
    }

    public void guardarVenta(List<DetalleVenta> detallesVenta){
        double montoTotal = 0;
        for(DetalleVenta detalleVenta : detallesVenta){
            montoTotal = montoTotal + detalleVenta.getSubtotal();
        }
        servicioVenta.guardarVenta(venta, montoTotal);
    }

    public void guardarDetallesVenta(List<DetalleVenta> detallesVenta){
        servicioVenta.agregarDetallesVenta(detallesVenta);
    }

    public void termina(String mensaje) {
        ventana.muestraDialogoConMensaje(mensaje);
        ventana.setVisible(false);
    }

    public void crearDocumento(List<DetalleVenta> detallesVenta){
        servicioVenta.crearDocumento(detallesVenta, venta);
    }
}

