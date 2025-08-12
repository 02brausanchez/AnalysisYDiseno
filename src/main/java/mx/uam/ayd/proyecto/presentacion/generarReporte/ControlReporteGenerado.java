package mx.uam.ayd.proyecto.presentacion.generarReporte;

import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.ReporteVentaDTO;
import java.util.List;
import java.time.LocalDate;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.ServicioVenta;

@Component
public class ControlReporteGenerado {
    private final ServicioVenta servicioVenta;
    private final VentanaReporteGenerado ventana;

    @Autowired
    public ControlReporteGenerado(
            ServicioVenta servicioVenta,
            VentanaReporteGenerado ventana) {
        this.servicioVenta = servicioVenta;
        this.ventana = ventana;
    }

    @PostConstruct
    public void init() {
        ventana.setControlReporteGenerado(this);
    }

    public void inicia(LocalDate desde, LocalDate hasta, String tipoReporte,
                       String periodicidad, TipoProducto tipoProducto) {
        try {
            List<ReporteVentaDTO> ventas = servicioVenta.recuperarVenta(
                    desde, hasta, tipoProducto, periodicidad);

            if(ventas.isEmpty()) {
                ventana.muestraDialogoConMensaje("No hay ventas con estos filtros");
            } else {
                if(!"Grafica".equals(tipoReporte) && !"Tabla".equals(tipoReporte)) {
                    ventana.muestraDialogoConMensaje("Tipo de reporte inválido: " + tipoReporte);
                    return;
                }
                ventana.muestra(ventas, tipoReporte, periodicidad);
            }
        } catch (Exception e) {
            ventana.muestraDialogoConMensaje("Error al generar reporte: " + e.getMessage());
        }
    }

    public void descargarReporte(List<ReporteVentaDTO> ventas) {
        try {
            servicioVenta.descargarReporte(ventas);
            ventana.muestraDialogoConMensaje("Reporte descargado exitosamente");
        } catch (Exception e) {
            ventana.muestraDialogoConMensaje("Error al descargar: " + e.getMessage());
        }
    }

    public void regresar(){
        ventana.setVisible(false);
    }
}