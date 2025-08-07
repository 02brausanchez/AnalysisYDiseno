package mx.uam.ayd.proyecto.presentacion.configurarUmbrales;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

//import mx.uam.ayd.proyecto.presentacion.configurarUmbrales.ControlConfiguracionUmbrales;
//import mx.uam.ayd.proyecto.presentacion.alertas.ControlConfiguracionAlerta;
//import mx.uam.ayd.proyecto.presentacion.configurarUmbrales.VentanaConfiguracionUmbrales;

import mx.uam.ayd.proyecto.negocio.ServicioUmbrales;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

import javafx.stage.Stage;
//import mx.uam.ayd.proyecto.negocio.ServicioAlerta;
//import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//import mx.uam.ayd.proyecto.negocio.modelo.Producto;

//import mx.uam.ayd.proyecto.presentacion.alertas.ControlConfiguracionAlerta;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
public class ControlConfiguracionUmbrales {
    //private static final Logger log = LoggerFactory.getLogger(ControlConfiguracionUmbrales.class);
    @FXML private TextField txtProducto;
    @FXML private TextField txtStockActual;
    @FXML private TextField txtUmbralActual;
    @FXML private TextField txtNuevoUmbral;

    private Producto producto;
    //private ServicioUmbrales servicio;
    private Runnable callbackActualizacion;

    private ServicioUmbrales servicioUmbrales;
    //private final ServicioAlerta servicioAlerta;
    private VentanaConfiguracionUmbrales ventana;
    //private final ControlConfiguracionAlerta controlAlerta;

    //private final ControlConfiguracionUmbrales controlConfiguracionUmbrales;

    //private final ControlConfiguracionAlerta controlAlerta;


    private Stage stage;



    public ControlConfiguracionUmbrales(
            //ServicioUmbrales servicioUmbrales,
            //ServicioAlerta servicioAlerta,
            //VentanaConfiguracionUmbrales ventana
            /*ControlConfiguracionAlerta controlAlerta*/VentanaConfiguracionUmbrales ventana) {
        //this.servicioUmbrales = servicioUmbrales;
        //this.controlAlerta = controlAlerta;
        //this.ventana = ventana;
        //this.servicioAlerta = servicioAlerta;
    }

    @Autowired
    public void setVentanaConfiguracionUmbrales(VentanaConfiguracionUmbrales ventana) {
        this.ventana = ventana;
    }

    @Autowired
    public void setServicioUmbrales(ServicioUmbrales servicioUmbrales) {
        this.servicioUmbrales = servicioUmbrales;
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
        List<Producto> productos = servicioUmbrales.recuperaConStockNoCero();
        //lógica para mostrar productos
        if (productos.isEmpty()) {
            ventana.muestraDialogoConMensaje("No hay productos con stock disponible para configurar, por favor agregue manualmente productos");
        } else {
            ventana.muestra(productos); // Muestra lista filtrada
        }
    }


    public void setProducto(Producto producto) {
        this.producto = producto;
        cargarDatosProductos();
    }

    public void setCallbackActualizacion(Runnable callback) {
        this.callbackActualizacion = callbackActualizacion;
    }

    private void cargarDatosProductos(){
        txtProducto.setText(producto.getNombre());
        txtStockActual.setText(String.valueOf(producto.getCantidadStock()));

        Umbral umbral = servicioUmbrales.findByProducto(producto.getIdProducto());
        if (umbral != null) {
            txtUmbralActual.setText(String.valueOf(umbral.getValorMinimo()));
            txtNuevoUmbral.setText(String.valueOf(umbral.getValorMinimo()));
        } else {
            txtUmbralActual.setText("No configurado");
            txtNuevoUmbral.setText("1");
        }
    }

    @FXML
    private void guardarCambios() {
        try {
            int nuevoUmbral = Integer.parseInt(txtNuevoUmbral.getText());

            if (nuevoUmbral <= 1) {
                mostrarAlerta("Error", "El umbral debe ser mayor a 1");
                return;
            }

            Umbral umbral = servicioUmbrales.findByProducto(producto.getIdProducto());
            if (umbral == null) {
                servicioUmbrales.crearUmbral(producto.getIdProducto(), nuevoUmbral);
            } else {
                servicioUmbrales.actualizarUmbral(producto.getIdProducto(), nuevoUmbral);
            }

            mostrarAlerta("Éxito", "Umbral actualizado correctamente");
            if (callbackActualizacion != null) {
                callbackActualizacion.run();
            }

            // Cerrar ventana
            txtNuevoUmbral.getScene().getWindow().hide();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese un valor numérico válido");
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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


    /**
     * Cierra la ventana de configuración
     */
    public void termina() {
        ventana.setVisible(false);
    }
}
