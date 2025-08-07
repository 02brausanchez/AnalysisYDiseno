package mx.uam.ayd.proyecto.presentacion.configurarUmbrales;
import mx.uam.ayd.proyecto.presentacion.configurarUmbrales.ControlConfiguracionUmbrales;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import mx.uam.ayd.proyecto.negocio.modelo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.Button;
import java.awt.Label;
//import java.awt.TextField;
import javafx.scene.control.TextField;
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
    private ServicioUmbrales servicioUmbrales;
    private VentanaConfiguracionUmbrales ventana;

    public void ventanaConfiguracionUmbrales(VentanaConfiguracionUmbrales ventana){
        this.ventana=ventana;
    }

    @Autowired
    public VentanaConfiguracionUmbrales(ServicioUmbrales servicioUmbrales) {
        this.servicioUmbrales = servicioUmbrales;
    }

    public TableColumn<Producto, String> getColumnaEstado() {
        return columnaEstado;
    }

    @FXML
    private TableColumn<Producto, String> columnaEstado;

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
    private TableColumn<Producto, Void> columnaAccion; // Columna con botón

    // Campos para el panel de edición (del FXML compartido)
    //@FXML
    //private Label labelTitulo;

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

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize(){
        //Configuracion de columnas
        configurarColumnas();
    }

    private void configurarColumnas() {
        // Columna Producto
        columnaProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Columna Stock
        columnaStock.setCellValueFactory(new PropertyValueFactory<>("cantidadStock"));

        // Columna Umbral (con valor dinámico)
        columnaUmbral.setCellValueFactory(cellData -> {
            Producto producto = cellData.getValue();
            Umbral umbral = servicioUmbrales.findByProducto(producto.getIdProducto());
            return new SimpleIntegerProperty(umbral != null ? umbral.getValorMinimo() : 1).asObject();
        });

        // Columna Estado (con estilo condicional)
        columnaEstado.setCellValueFactory(cellData -> {
            Producto producto = cellData.getValue();
            Umbral umbral = servicioUmbrales.findByProducto(producto.getIdProducto());

            if (umbral == null) {
                return new SimpleStringProperty("No configurado");
            } else if (producto.getCantidadStock() < umbral.getValorMinimo()) {
                return new SimpleStringProperty("BAJO STOCK");
            } else {
                return new SimpleStringProperty("OK");
            }
        });

        columnaEstado.setCellFactory(column -> new TableCell<Producto, String>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);

                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado);
                    if (estado.equals("BAJO STOCK")) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: green;");
                    }
                }
            }
        });

        // Columna Acción - Versión corregida
        columnaAccion.setCellFactory(param -> new TableCell<Producto, Void>() {
            private final javafx.scene.control.Button btnEditar = new javafx.scene.control.Button("Editar");

            {
                btnEditar.setOnAction(event -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    abrirVentanaEdicionUmbral(producto);
                });

                btnEditar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEditar);
                }
            }
        });
    }

    /*
        Metodo para abrir la ventana de edicion
     */
    private void abrirVentanaEdicionUmbral(Producto producto) {
        try {
            // 1. Crear el controlador manualmente con su dependencia
            ControlConfiguracionUmbrales controlador = new ControlConfiguracionUmbrales(ventana);

            // 2. Cargar el FXML y asignar el controlador antes de llamar a load()
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-editar-umbral.fxml"));
            loader.setController(controlador);
            Parent root = loader.load();

            // 3. Inyectar las demás dependencias y datos
            controlador.setProducto(producto);
            controlador.setServicioUmbrales(servicioUmbrales);
            controlador.setCallbackActualizacion(() -> tablaProductos.refresh());

            // 4. Mostrar la nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Editar Umbral - " + producto.getNombre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            muestraDialogoConMensaje("Error al abrir ventana de edición:\n" + e.getMessage());

            System.err.println("Ruta intentada: " + getClass().getResource("/fxml/ventana-editar-umbral.fxml"));
            if (e.getCause() != null) {
                System.err.println("Causa: " + e.getCause().getMessage());
            }
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

    /*
    @FXML
    private void handleAgregarAlerta() {
        // Aquí puedes implementar la lógica luego
        System.out.println("Botón 'Agregar' presionado");
    }*/


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

