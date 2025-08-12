package mx.uam.ayd.proyecto.presentacion.generarReporte;

import javafx.scene.control.*;
import mx.uam.ayd.proyecto.negocio.modelo.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import mx.uam.ayd.proyecto.presentacion.registroVentas.ControlRegistroVentas;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.LongToDoubleFunction;

@Component
public class VentanaGenerarReporte {
    private Stage stage;
    private ControlGenerarReporte control;

    @FXML
    private ComboBox<TipoProducto> cmbTipoProducto;

    @FXML
    private ComboBox<String> cmbTipoReporte;

    @FXML
    private ComboBox<String> cmbPeriodicidad;

    @FXML
    private DatePicker dtpDesde;

    @FXML
    private DatePicker dtpHasta;

    private boolean initialized = false;

    public VentanaGenerarReporte() {
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
            stage.setTitle("Generar Reporte");

            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-generar-reporte.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 700, 450);
            stage.setScene(scene);

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setControlGenerarReporte(ControlGenerarReporte control) {
        this.control = control;
    }

    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra());
            return;
        }

        initializeUI();

        cmbTipoProducto.setItems(FXCollections.observableArrayList(TipoProducto.values()));
        ObservableList<String> tipoReporte = FXCollections.observableArrayList(
                "Tabla",
                "Grafica"
        );

        cmbTipoReporte.setItems(tipoReporte);

        cmbTipoProducto.setItems(FXCollections.observableArrayList(TipoProducto.values()));
        ObservableList<String> periodicidad = FXCollections.observableArrayList(
                "Diario",
                "Mensual"
        );

        cmbPeriodicidad.setItems(periodicidad);

        cmbTipoReporte.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("Grafica".equals(newVal)) {
                cmbPeriodicidad.setDisable(false);
            } else {
                cmbPeriodicidad.setDisable(true);
                cmbPeriodicidad.getSelectionModel().clearSelection();
            }
        });

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
    private void handleGenerar(){
        if(Objects.equals(cmbTipoReporte.getValue(), "Grafica")){
            if(cmbPeriodicidad.getValue() == null){
                muestraDialogoConMensaje("Selecciones una periodicidad");
            }else{
                control.reporteGenerado(dtpDesde.getValue(), dtpHasta.getValue(), cmbTipoReporte.getValue(),  cmbPeriodicidad.getValue(), cmbTipoProducto.getValue());
            }
        }else{
            control.reporteGenerado(dtpDesde.getValue(), dtpHasta.getValue(), cmbTipoReporte.getValue(),  "Diario", cmbTipoProducto.getValue());
        }

    }

    @FXML
    private void handleCancelar() {
        String mensaje = "Ventana cerrada";
        control.termina(mensaje);
    }
}