package mx.uam.ayd.proyecto.presentacion.generarReporte;

import java.time.LocalDate;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.presentacion.registroVentas.VentanaRegistroVentas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioVenta;
import mx.uam.ayd.proyecto.negocio.ServicioDetalleVenta;
import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.presentacion.generarReporte.ControlReporteGenerado;

@Component
public class ControlGenerarReporte {
    private final ServicioVenta servicioVenta;
    private final ServicioDetalleVenta servicioDetalleVenta;
    private final ServicioProducto servicioProducto;
    private final VentanaGenerarReporte ventana;
    private final ControlReporteGenerado controlReporteGenerado;

    @Autowired
    public ControlGenerarReporte(
            ServicioVenta servicioVenta,
            ServicioDetalleVenta servicioDetalleVenta,
            ServicioProducto servicioProducto,
            VentanaGenerarReporte ventana,
            ControlReporteGenerado controlReporteGenerado) {
        this.servicioVenta = servicioVenta;
        this.servicioDetalleVenta = servicioDetalleVenta;
        this.servicioProducto = servicioProducto;
        this.ventana = ventana;
        this.controlReporteGenerado = controlReporteGenerado;
    }

    @PostConstruct
    public void init() {
        ventana.setControlGenerarReporte(this);}

    public void inicia() {
        ventana.muestra();
    }
    public void reporteGenerado(LocalDate desde, LocalDate hasta, String tipoReporte, String periodicidad, TipoProducto tipoProducto){
        controlReporteGenerado.inicia(desde, hasta, tipoReporte, periodicidad, tipoProducto);
    }
    public void termina(String mensaje) {
        ventana.muestraDialogoConMensaje(mensaje);
        ventana.setVisible(false);
    }
}
