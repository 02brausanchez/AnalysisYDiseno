package mx.uam.ayd.proyecto.presentacion.configurarUmbrales;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import mx.uam.ayd.proyecto.negocio.modelo.*;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;

import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
import mx.uam.ayd.proyecto.negocio.ServicioUmbrales;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;

import java.io.InputStream;
import java.util.List;

@Component
public class VentanaConfiguracionUmbrales {
    private Stage stage;
    private ControlConfiguracionUmbrales control;
    private Producto productoSeleccionado;

    // Campos para la tabla de productos
    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, String> columnaProducto;

    @FXML
    private TableColumn<Producto, Integer> columnaStock;

    @FXML
    private TableColumn<Producto, Integer> columnaUmbral;

    @FXML
    private TableColumn<Producto, String> columnaEstado; // Nueva columna

    @FXML
    private TableColumn<Producto, Button> columnaAccion; // Columna con botón

    // Campos para el panel de edición (del FXML compartido)
    @FXML
    private Label labelTitulo;

    @FXML
    private TextField editUmbralField;
    @FXML
    private TextField stockActualField;
    @FXML
    private TextField umbralActualField;
    @FXML
    private ComboBox<Integer> nuevoUmbralCombo;

    private boolean initialized = false;

    /**
     * Constructor without UI initialization
     */
    public VentanaConfiguracionUmbrales(){
        // Don't initialize JavaFX components in constructor
    }

    /**
     * Initialize UI components on the JavaFX application thread
     */
    private void initializeUI() {
        if (initialized) {
            return;
        }

        // Create UI only if we're on JavaFX thread
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-menu-principal-umbrales.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 450, 400);
            stage = new Stage();
            stage.setTitle("Gestion de stock");
            stage.setScene(scene);


            VentanaConfiguracionUmbrales control = loader.getController();

            //Configure columns after FXML is loaded
            columnaProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            columnaStock.setCellValueFactory(new PropertyValueFactory<>("cantidadStock"));
            columnaUmbral.setCellValueFactory(new PropertyValueFactory<>("Umbral"));
            columnaEstado.setCellValueFactory(new PropertyValueFactory<>("Estado"));
            columnaAccion.setCellValueFactory(new PropertyValueFactory<>("Accion"));


            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Establece el controlador asociado a esta ventana
     *
     * @param control El controlador asociado
     */
    public void setControlConfiguracionUmbrales(ControlConfiguracionUmbrales control) {
        this.control = control;
    }

    /*
       Muestra la ventana y establece los datos
       @param umbral la lista de productos disponibles
     */
    public void muestra(List<Producto> productos) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(productos));
            return;
        }
        initializeUI();

        ObservableList<Producto> data = FXCollections.observableArrayList(productos);
        tablaProductos.setItems(data);

        stage.show();
        /*
        Platform.runLater(() -> {
            // Configurar tabla
            columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            columnaStock.setCellValueFactory(new PropertyValueFactory<>("cantidadStock"));
            columnaUmbral.setCellValueFactory(cellData -> {
                Producto producto = cellData.getValue();
                return new SimpleIntegerProperty(
                        producto.getUmbral() != null ? producto.getUmbral().getValorMinimo() : 1
                ).asObject();
            });

            tablaProductos.getItems().setAll(productos);

            // Seleccionar el primer producto por defecto
            if (!productos.isEmpty()) {
                tablaProductos.getSelectionModel().selectFirst();
                actualizarPanelEdicion(productos.get(0));
            }
        });*/
    }

    /*
        Muestra un dialogo con un mensaje
        @param mensaje El mensaje a mostrar
     */
    public void muestraDialogoConMensaje(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Configuración de Umbrales");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    public void actualizarUmbralEnTabla(Long idProducto, int nuevoMinimo) {
        tablaProductos.getItems().stream()
                .filter(p -> p.getIdProducto().equals(idProducto))
                .findFirst()
                .ifPresent(p -> {
                    if (p.getUmbral() == null) {
                        // Versión segura:
                        Umbral nuevoUmbral = new Umbral();
                        nuevoUmbral.setValorMinimo(nuevoMinimo);
                        nuevoUmbral.setProducto(p); // ¡Importante para la relación!
                        p.setUmbral(nuevoUmbral);
                    } else {
                        p.getUmbral().setValorMinimo(nuevoMinimo);
                    }
                    tablaProductos.refresh();
                });
    }

    /*
        Oculta la ventana
     */
    public void setVisible(boolean visible) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.setVisible(visible));
            return;
        }

        if (!initialized) {
            if (visible) {
                initializeUI();
            } else {
                return;
            }
        }

        if (visible) {
            stage.show();
        } else {
            stage.hide();
        }
    }


    private void actualizarPanelEdicion(Producto producto) {
        this.productoSeleccionado = producto;
        stockActualField.setText(String.valueOf(producto.getCantidadStock()));

        if (producto.getUmbral() != null) {
            umbralActualField.setText(String.valueOf(producto.getUmbral().getValorMinimo()));
            editUmbralField.setText(String.valueOf(producto.getUmbral().getValorMinimo()));
            nuevoUmbralCombo.setValue(producto.getUmbral().getValorMinimo());
        } else {
            umbralActualField.setText("No configurado");
            editUmbralField.setText("");
            nuevoUmbralCombo.setValue(1);
        }
    }

    // FXML Event Handlers

    @FXML
    private void handleEditarUmbral() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            actualizarPanelEdicion(seleccionado);
        }
    }

    @FXML
    private void handleGuardar() {
        if (productoSeleccionado == null) {
            muestraDialogoConMensaje("Seleccione un producto primero");
            return;
        }

        try {
            int nuevoUmbral = nuevoUmbralCombo.getValue();
            control.manejarEdicionUmbral(productoSeleccionado.getIdProducto(), nuevoUmbral);
            muestraDialogoConMensaje("Umbral actualizado correctamente");

            // Actualizar la tabla
            productoSeleccionado.getUmbral().setValorMinimo(nuevoUmbral);
            tablaProductos.refresh();
        } catch (Exception e) {
            muestraDialogoConMensaje("Error al actualizar: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        control.termina();
    }

}

