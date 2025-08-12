package mx.uam.ayd.proyecto.presentacion.alertas;

// Importamos clases necesarias para manejar listas y trabajar con Spring
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.ServicioAlerta;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;

/**
 * Esta clase se encarga de controlar todo el flujo de alertas de la aplicación.
 * Está marcada como @Component para que Spring la detecte y pueda inyectar sus dependencias.
 */
@Component
public class ControlAlerta {

    // Inyección del servicio que maneja la lógica de alertas
    @Autowired
    private ServicioAlerta servicioAlerta;

    // Repositorio para acceder a los productos guardados en la base de datos
    @Autowired
    private ProductoRepository productoRepository;

    // Ventana que muestra visualmente las alertas al usuario
    @Autowired
    private VentanaAlerta ventanaAlerta;

    /**
     * Inicia el controlador de alertas.
     * Básicamente le pasa el control a la ventana y la muestra.
     */
    public void inicia() {
        ventanaAlerta.setControl(this);
        ventanaAlerta.muestra();
    }

    /**
     * Revisa el stock de todos los productos y genera mensajes de alerta
     * si algún producto está por debajo de su umbral mínimo.
     *
     * @return Lista de mensajes de alerta listos para mostrar en la interfaz.
     */
    public List<String> revisarStock() {
        // Lista que guardará los mensajes de alerta que se generen
        List<String> mensajesAlertas = new ArrayList<>();

        // Se obtiene la lista de productos desde la base de datos
        List<Producto> productos = new ArrayList<>();
        productoRepository.findAll().forEach(productos::add);

        // Se recorre cada producto para revisar su stock
        for (Producto producto : productos) {
            System.out.println("DEBUG - Producto: " + producto.getNombre());
            System.out.println("DEBUG - Stock actual: " + producto.getCantidadStock());

            // Obtenemos el umbral de ese producto
            Umbral umbral = producto.getUmbral();
            System.out.println("DEBUG - Umbral: " + (umbral != null ? umbral.getValorMinimo() : "SIN UMBRAL"));

            // Si el producto tiene configurado un umbral mínimo...
            if (umbral != null) {
                // ...y si su stock está por debajo de ese umbral, se genera una alerta
                if (producto.getCantidadStock() < umbral.getValorMinimo()) {
                    System.out.println("DEBUG - ALERTA GENERADA para " + producto.getNombre());

                    // Se delega al servicio para crear la alerta si es necesario
                    servicioAlerta.crearAlertaSiNecesaria(producto, umbral);

                    // Se construye el mensaje para mostrarlo después
                    String mensaje = "El producto '" + producto.getNombre() + "' está por debajo del mínimo. Stock actual: "
                            + producto.getCantidadStock() + ", mínimo permitido: " + umbral.getValorMinimo();
                    mensajesAlertas.add(mensaje);
                }
            }
        }

        // Si no se generó ninguna alerta, se avisa que todo está bien
        if (mensajesAlertas.isEmpty()) {
            mensajesAlertas.add("No hay alertas. Todos los productos están con stock suficiente.");
        }

        // Se devuelve la lista de mensajes generados
        return mensajesAlertas;
    }
}
