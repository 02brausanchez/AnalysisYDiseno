package mx.uam.ayd.proyecto.presentacion.agregarProducto;

import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DateCell;
import javafx.util.Callback;
import java.time.LocalDate;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import mx.uam.ayd.proyecto.negocio.modelo.Producto;

@Component
public class VentanaAgregarProducto{
    private Stage stage;
    private ControlAgregarProducto control;


    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtCantidad;

    @FXML
    private TextField txtPrecio;

    @FXML
    private ComboBox<TipoProducto> cmbTipo;

    @FXML
    private ComboBox<UnidadProducto> cmbUnidad;

    @FXML
    private ComboBox<MarcaProducto> cmbMarca;

    @FXML
    private DatePicker dtpFechaCaducidad;

    @FXML
    private void initialize() {
        // Deshabilitar fechas anteriores a partir de una semana
        dtpFechaCaducidad.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item.isBefore(LocalDate.now().plusWeeks(1))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #cccccc;");
                }
            }
        });
    }

    private boolean initialized = false;

    public VentanaAgregarProducto() {
        // Don't initialize JavaFX components in constructor
    }

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
            stage = new Stage();
            stage.setTitle("Agregar producto");

            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-agregar-producto.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 700, 500);
            System.out.println("FXML cargado correctamente"); //
            stage.setScene(scene);
            txtCantidad.textProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    txtCantidad.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            UnaryOperator<TextFormatter.Change> filter = change -> {
                String newText = change.getControlNewText();
                // Permite vacío o números con un solo punto decimal
                if (newText.matches("\\d*\\.?\\d*")) {
                    return change;
                }
                return null;
            };

            TextFormatter<String> textFormatter = new TextFormatter<>(filter);
            txtPrecio.setTextFormatter(textFormatter);
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setControlAgregarProducto(ControlAgregarProducto control) {
        this.control = control;
    }

    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra());
            return;
        }

        initializeUI();

        txtNombre.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");

        cmbTipo.setItems(FXCollections.observableArrayList(TipoProducto.values()));

        cmbUnidad.setItems(FXCollections.observableArrayList(UnidadProducto.values()));

        cmbMarca.setItems(FXCollections.observableArrayList(MarcaProducto.values()));

        stage.show();
    }

    public void muestraDialogoConMensaje(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDialogoConMensaje(mensaje));
            return;
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

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

    @FXML
    private void handleAgregar() {
        if(cmbUnidad == null || cmbTipo == null || cmbMarca == null || txtCantidad == null || txtNombre == null || txtPrecio == null) {
            muestraDialogoConMensaje("Llene los campos obligatorios");
        } else {
            control.agregarProducto(txtNombre.getText(), cmbTipo.getValue(), cmbMarca.getValue(), Double.parseDouble(txtPrecio.getText()), Integer.parseInt(txtCantidad.getText()), cmbUnidad.getValue(), dtpFechaCaducidad.getValue());
        }
    }

    @FXML
    private void handleCancelar() {
        control.termina();
    }
}