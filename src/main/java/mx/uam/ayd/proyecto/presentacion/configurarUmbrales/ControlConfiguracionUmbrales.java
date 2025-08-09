package mx.uam.ayd.proyecto.presentacion.configurarUmbrales;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
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

    private ServicioUmbrales servicioUmbrales;
    //private final ServicioAlerta servicioAlerta;
    private VentanaConfiguracionUmbrales ventana;

    private Producto producto;
    //private ServicioUmbrales servicio;
    private Runnable callbackActualizacion;

    //private static final Logger log = LoggerFactory.getLogger(ControlConfiguracionUmbrales.class);
    @FXML private TextField editUmbral;
    @FXML private TextField stockActual;
    @FXML private TextField umbralActual;
    @FXML private TextField nuevoUmbral;
    @FXML private ComboBox<Integer> nuevoUmbralCombo;

    @FXML
    private void initialize() {
        // Llenar el ComboBox del 1 al 100
        for (int i = 1; i <= 100; i++) {
            nuevoUmbralCombo.getItems().add(i);
        }

        // Hacer editable secciones necesarias
        nuevoUmbralCombo.setEditable(true);
        stockActual.setEditable(false);
        umbralActual.setEditable(false);
        editUmbral.setEditable(false);

        //Listener para actualizar el TextField "nuevoUmbral" con el formato deseado
        nuevoUmbralCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nuevoUmbral.setText("Nuevo umbral: " + newVal);
            } else {
                nuevoUmbral.clear();
            }
        });
    }

    public ControlConfiguracionUmbrales() {}
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

    @Autowired
    public void setServicioUmbrales(ServicioUmbrales servicioUmbrales) {
        this.servicioUmbrales = servicioUmbrales;
    }

    @Autowired
    public void setVentanaConfiguracionUmbrales(VentanaConfiguracionUmbrales ventana) {
        this.ventana = ventana;
    }

    // Se implementa de acuerdo al diagrama de secuencia
    public void iniciarEdicionDeUmbral(Long idProducto, int minimo) {
        cargarDatosProductos(idProducto, minimo);
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        cargarDatosProductos(producto.getIdProducto(), producto.getCantidadStock());
    }

    public void setCallbackActualizacion(Runnable callback) {
        this.callbackActualizacion = callbackActualizacion;
    }

    private void cargarDatosProductos(Long idProducto, int minimo) {
        // Configurar el campo de edicion con el nombre del producto
        editUmbral.setText(producto.getNombre());
        stockActual.setText(String.valueOf(producto.getCantidadStock()));

        Umbral umbral = servicioUmbrales.findByProducto(producto.getIdProducto());
        if (umbral != null) {
            umbralActual.setText(String.valueOf(umbral.getValorMinimo()));
            nuevoUmbralCombo.setValue(umbral.getValorMinimo());
            nuevoUmbral.setText("Nuevo umbral: " + (umbral.getValorMinimo()));
        } else {
            umbralActual.setText("No configurado");
            nuevoUmbralCombo.setValue(1);
            nuevoUmbral.setText("1");
        }
    }

    @FXML
    private void handleGuardar() {
        try {
            Integer nuevoValor = obtenerValorDesdeCombo();

            if (nuevoValor == null || nuevoValor < 1 || nuevoValor > 100) {
                mostrarAlerta("Error", "El umbral debe ser un número entre 1 y 100");
                return;
            }

            manejarEdicionUmbral(producto.getIdProducto(), nuevoValor);

            if (callbackActualizacion != null) {
                callbackActualizacion.run();
            }

            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese un valor numérico válido");
        }
    }

    private Integer obtenerValorDesdeCombo() {
        String texto = nuevoUmbralCombo.getEditor().getText();
        if (texto != null && !texto.isEmpty()) {
            return Integer.parseInt(texto);
        }
        return nuevoUmbralCombo.getValue();
    }



    private void mostrarMensajeExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        ((Stage) nuevoUmbralCombo.getScene().getWindow()).close();
    }
    /**
     * FASE 2 - Edición de umbral (unifica iniciaEdicionUmbral y configurarUmbral)
     */
    public void manejarEdicionUmbral(Long idProducto, int nuevoMinimo) {
        try {
            if (nuevoMinimo < 1) {
                throw new IllegalArgumentException("El umbral debe ser ≥ 1");
            }

            Umbral umbral = servicioUmbrales.findByProducto(idProducto);

            if (umbral == null) {
                servicioUmbrales.crearUmbral(idProducto, nuevoMinimo);
                mostrarMensajeExito("Umbral creado: " + nuevoMinimo);
            } else {
                servicioUmbrales.actualizarUmbral(idProducto, nuevoMinimo);
                mostrarMensajeExito("Umbral actualizado: " + nuevoMinimo);
            }

            ventana.actualizarUmbralEnTabla(idProducto, nuevoMinimo);

        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarAlerta("Error", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta("Error técnico", e.getMessage());
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
