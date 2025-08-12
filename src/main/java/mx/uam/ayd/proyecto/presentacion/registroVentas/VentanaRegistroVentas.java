package mx.uam.ayd.proyecto.presentacion.registroVentas;

import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.function.LongToDoubleFunction;

import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import mx.uam.ayd.proyecto.negocio.modelo.DetalleVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

@Component
public class VentanaRegistroVentas{
    private Stage stage;
    private ControlRegistroVentas control;

    private Venta venta;

    @FXML
    private TextField txtCantidad;

    @FXML
    private ComboBox<Producto> cmbProductos;

    @FXML
    private TableView<DetalleVenta> tableVenta;

    private ObservableList<DetalleVenta> detallesVenta = FXCollections.observableArrayList();

    @FXML
    private TableColumn<DetalleVenta, Long> columnIdProducto;
    @FXML
    private TableColumn<DetalleVenta, String> columnNombreProducto;
    @FXML
    private TableColumn<DetalleVenta, String> columnMarca;
    @FXML
    private TableColumn<DetalleVenta, Double> columnPrecio;
    @FXML
    private TableColumn<DetalleVenta, Integer> columnCantidad;
    @FXML
    private TableColumn<DetalleVenta, Double> columnSubtotal;
    @FXML
    private Label lblTotal;

    private boolean initialized = false;

    public VentanaRegistroVentas() {
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
            stage.setTitle("Registro de ventas");

            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-registro-ventas.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 500, 420);
            stage.setScene(scene);

            txtCantidad.textProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    txtCantidad.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            tableVenta.setItems(detallesVenta);

            columnIdProducto.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleLongProperty(
                            cellData.getValue().getProducto() != null ?
                                    cellData.getValue().getProducto().getIdProducto() :
                                    0L
                    ).asObject()
            );

            columnNombreProducto.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProducto().getNombre()));

            columnMarca.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProducto().getMarcaProducto().toString()));

            columnPrecio.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getProducto().getPrecio()).asObject());

            columnCantidad.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCantidadVendida()).asObject());

            columnSubtotal.setCellValueFactory(cellData -> {
                DetalleVenta detalle = cellData.getValue(); //Mover a un servicio
                double subtotal = detalle.getCantidadVendida() * detalle.getProducto().getPrecio();
                return new javafx.beans.property.SimpleDoubleProperty(subtotal).asObject();
            });

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setControlRegistroVentas(ControlRegistroVentas control) {
        this.control = control;
    }

    public void muestra(List<Producto> productos, Venta venta) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(productos, venta));
            return;
        }

        initializeUI();

        txtCantidad.setText("");
        cmbProductos.getItems().clear();
        for(Producto producto : productos) {
            cmbProductos.getItems().add(producto);
        }

        if(!cmbProductos.getItems().isEmpty()) {
            cmbProductos.setValue(cmbProductos.getItems().get(0));
        }

        this.venta = venta;

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
        if(cmbProductos == null || txtCantidad == null) {
            muestraDialogoConMensaje("Llene todos los campos");
        }else {
            double montoTotal = 0;
            detallesVenta.add(control.crearDetalleVenta(cmbProductos.getValue(), Integer.parseInt(txtCantidad.getText()), detallesVenta));
            tableVenta.setItems(detallesVenta);
            for(DetalleVenta detalleVenta : detallesVenta){
                montoTotal = montoTotal + detalleVenta.getSubtotal();
            }
            txtCantidad.clear();
            lblTotal.setText("Total: " + montoTotal);
        }
    }

    @FXML
    private void handleFinalizar() {
        if(detallesVenta == null) {
            String mensaje = "Venta cancelada";
            control.termina(mensaje);
        }
        control.guardarVenta(detallesVenta);
        control.guardarDetallesVenta(detallesVenta);
        control.actualizarStock(detallesVenta);
        control.crearDocumento(detallesVenta);
        String mensaje = "Se creo la venta exitosamente";
        control.termina(mensaje);
        detallesVenta.clear();
    }

    @FXML
    private void handleCancelar() {
        String mensaje = "Venta cancelada";
        detallesVenta.clear();
        control.termina(mensaje);
    }
}