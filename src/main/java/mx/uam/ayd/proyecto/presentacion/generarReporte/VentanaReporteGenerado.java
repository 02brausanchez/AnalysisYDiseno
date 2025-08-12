package mx.uam.ayd.proyecto.presentacion.generarReporte;

import javafx.scene.Parent;
import javafx.scene.control.*;
import mx.uam.ayd.proyecto.negocio.modelo.*;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;


@Component
public class VentanaReporteGenerado {
    private Stage stage;
    private ControlReporteGenerado control;

    @FXML
    private BarChart<String, Number> barChartVentas;
    @FXML
    private TableView<ReporteVentaDTO> tblVentas;
    @FXML
    private TableColumn<ReporteVentaDTO, LocalDate> columnFecha;
    @FXML
    private TableColumn<ReporteVentaDTO, String> columnProducto;
    @FXML
    private TableColumn<ReporteVentaDTO, String> columnTipo;
    @FXML
    private TableColumn<ReporteVentaDTO, Long> columnVenta;
    @FXML
    private TableColumn<ReporteVentaDTO, Double> columnTotal;

    private ObservableList<ReporteVentaDTO> ventas2 = FXCollections.observableArrayList();;
    private boolean initialized = false;
    public VentanaReporteGenerado() {
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
            stage.setTitle("Reporte generado");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-reporte-generado.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            Scene scene = new Scene(root, 600, 400); // pon un tamaño más amplio
            stage.setScene(scene);

            columnFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
            columnProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
            columnTipo.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipoProducto().toString()));
            columnVenta.setCellValueFactory(new PropertyValueFactory<>("cantidadVendida"));
            columnTotal.setCellValueFactory(new PropertyValueFactory<>("totalVenta"));

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setControlReporteGenerado(ControlReporteGenerado control) {
        this.control = control;
    }

    public void muestra(List<ReporteVentaDTO> ventas, String tipoReporte, String periodicidad) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(ventas, tipoReporte,  periodicidad));
            return;
        }

        initializeUI();
        barChartVentas.getData().clear();
        barChartVentas.setVisible(false);
        tblVentas.setVisible(false);
        this.ventas2 = FXCollections.observableArrayList(ventas);

        if ("Grafica".equalsIgnoreCase(tipoReporte)) {
            // Limpiar datos anteriores
            barChartVentas.getData().clear();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Ventas");
            if("Diario".equals(periodicidad)) {
                for (ReporteVentaDTO dto : ventas) {
                    series.getData().add(new XYChart.Data<>(dto.getNombreProducto() + "  " + dto.getFecha().toString(), dto.getCantidadVendida()));
                }
            }
            else{
                for (ReporteVentaDTO dto : ventas) {
                    series.getData().add(new XYChart.Data<>(dto.getNombreProducto() + "  " + dto.getFecha().toString(), dto.getCantidadVendida()));
                }
            }

            barChartVentas.getData().add(series);
            barChartVentas.setVisible(true);

        }else{
            tblVentas.setVisible(true);
            tblVentas.setItems(ventas2);
        }

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
    private void handleDescargar() {
        control.descargarReporte(ventas2);
    }
    @FXML
    private void handleRegresar() {
        control.regresar();
    }
}