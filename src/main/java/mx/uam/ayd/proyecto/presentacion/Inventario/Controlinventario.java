package mx.uam.ayd.proyecto.presentacion.Inventario;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import mx.uam.ayd.proyecto.presentacion.agregarProducto.ControlAgregarProducto;



@Component
public class Controlinventario {

    private static final Logger log = LoggerFactory.getLogger(Controlinventario.class);

    private final ServicioInventario servicioInventario;
    private final VentanaInventario ventana;
    private final ControlAgregarProducto controlAgregarProducto;

    @Autowired
    public Controlinventario(ServicioInventario servicioInventario, VentanaInventario ventana, ControlAgregarProducto controlAgregarProducto) {
        this.servicioInventario = servicioInventario;
        this.ventana = ventana;
        this.controlAgregarProducto = controlAgregarProducto;
    }
    /**
     * Método que se ejecuta después de la construcción del bean
     * y realiza la conexión bidireccional entre el control y la ventana
     */
    @PostConstruct
    public void init() {
        ventana.setControlinventario(this);
    }

    /**
     * Inicia el caso de uso
     */
    public void inicia() {
        List<Producto> productos = servicioInventario.recuperaProducto();

        for(Producto producto : productos) {
            log.info("producto " + producto);
        }

       ventana.muestra(productos);
    }
    public void eliminaProducto(Long idproducto){
        try {

            servicioInventario.eliminaProducto(idproducto);
            ventana.muestra(servicioInventario.recuperaProducto());
            ventana.muestraMensaje("Producto eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            ventana.muestraMensaje("Error al eliminar: " + e.getMessage());
        }
    }


    public void agregarProducto() {
        controlAgregarProducto.inicia();
    }
}
