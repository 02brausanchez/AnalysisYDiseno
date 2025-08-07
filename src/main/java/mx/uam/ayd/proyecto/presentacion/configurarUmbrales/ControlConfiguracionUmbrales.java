package mx.uam.ayd.proyecto.presentacion.configurarUmbrales;

import mx.uam.ayd.proyecto.presentacion.configurarUmbrales.ControlConfiguracionUmbrales;
import mx.uam.ayd.proyecto.presentacion.alertas.ControlConfiguracionAlerta;
import mx.uam.ayd.proyecto.presentacion.configurarUmbrales.VentanaConfiguracionUmbrales;

import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.ServicioAlerta;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.ServicioUmbrales;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

//import mx.uam.ayd.proyecto.presentacion.alertas.ControlConfiguracionAlerta;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
public class ControlConfiguracionUmbrales {
    //private static final Logger log = LoggerFactory.getLogger(ControlConfiguracionUmbrales.class);

    private final ServicioUmbrales servicioUmbrales;
    //private final ServicioAlerta servicioAlerta;
    private final VentanaConfiguracionUmbrales ventana;
    //private final ControlConfiguracionAlerta controlAlerta;

    //private final ControlConfiguracionUmbrales controlConfiguracionUmbrales;

    //private final ControlConfiguracionAlerta controlAlerta;


    private Stage stage;


    @Autowired
    public ControlConfiguracionUmbrales(
            ServicioUmbrales servicioUmbrales,
            //ServicioAlerta servicioAlerta,
            VentanaConfiguracionUmbrales ventana
            /*ControlConfiguracionAlerta controlAlerta*/) {
        this.servicioUmbrales = servicioUmbrales;
        //this.controlAlerta = controlAlerta;
        this.ventana = ventana;
        //this.servicioAlerta = servicioAlerta;
    }

    /**
     * Método que se ejecuta después de la construcción del bean
     * y realiza la conexión bidireccional entre el control y la ventana
     */
    @PostConstruct
    public void init() {
        ventana.setControlConfiguracionUmbrales(this);
    }

    /**
     * Inicia la historia de usuario
     */
    public void inicia() {
        List<Producto> productos = servicioUmbrales.recuperaConStockNoCero(); // Usa método de Fase 1
        if (productos.isEmpty()) {
            ventana.muestraDialogoConMensaje("No hay productos con stock disponible para configurar, por favor agregue manualmente productos");
        } else {
            ventana.muestra(productos); // Muestra lista filtrada
        }
    }

    /**
     * FASE 2 - Edición de umbral (unifica iniciaEdicionUmbral y configurarUmbral)
     */
    public void manejarEdicionUmbral(Long idProducto, int nuevoMinimo) {
        try {
            // Validación básica
            if (nuevoMinimo < 1) {
                throw new IllegalArgumentException("El umbral debe ser ≥ 1");
            }

            // Verificar existencia y actualizar
            Umbral umbral = servicioUmbrales.findByProducto(idProducto);
            if (umbral == null) {
                //  Caso [No existe umbral]
                Umbral nuevoUmbral = servicioUmbrales.crearUmbral(idProducto, nuevoMinimo);
                ventana.muestraDialogoConMensaje("Umbral creado" + nuevoMinimo);
                ventana.actualizarUmbralEnTabla(idProducto, nuevoMinimo);
                //throw new IllegalStateException("El producto no tiene umbral configurado");
            }else{
                // Caso [Existe Umbral]
                Umbral umbralActualizado = servicioUmbrales.actualizarUmbral(idProducto, nuevoMinimo);
                ventana.muestraDialogoConMensaje("Umbral actualizado" + nuevoMinimo);
                ventana.actualizarUmbralEnTabla(idProducto, nuevoMinimo);
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            ventana.muestraDialogoConMensaje(e.getMessage());
        } catch (Exception e) {
            ventana.muestraDialogoConMensaje("Error técnico: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /*
    public void mostrarVentanaAlerta(Producto producto) {
        if (producto.getUmbral() == null) {
            ventana.muestraDialogoConMensaje("Configure primero un umbral");
            return;
        }

        // Verificar si ya existe alerta
        Alerta alertaExistente = servicioAlerta.buscarPorProducto(producto.getIdProducto());

        if (alertaExistente != null) {
            controlAlerta.editarAlertaExistente(alertaExistente);
        } else {
            controlAlerta.crearNuevaAlerta(producto);
        }
    }*/



    /*
    **
     * Muestra la ventana de configuración de alertas
     * @param producto Producto seleccionado para configurar alertas
     *
    public void mostrarVentanaAlerta(Producto producto) {
        try {
            if (producto.getUmbral() == null) {
                ventana.muestraDialogoConMensaje("Primero configure un umbral para este producto");
                return;
            }
            controlAlerta.inicia(producto);
        } catch (Exception e) {
            ventana.muestraDialogoConMensaje("Error al abrir configuración de alertas: " + e.getMessage());
        }
    }
    */

    /**
     * Cierra la ventana de configuración
     */
    public void termina() {
        ventana.setVisible(false);
    }
}
