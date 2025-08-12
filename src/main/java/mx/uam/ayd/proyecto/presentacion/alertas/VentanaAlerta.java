package mx.uam.ayd.proyecto.presentacion.alertas;

import java.util.List;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Clase que representa la ventana donde se muestran las alertas de productos.
 * Es un componente de Spring pero también usa JavaFX para la interfaz.
 */
@Component
public class VentanaAlerta {

    // Referencia al controlador que maneja la lógica de alertas
    private ControlAlerta control;
    // Ventana principal de esta parte de la aplicación
    private Stage stage;
    // Lista visual donde se mostrarán los mensajes de alerta
    private ListView<String> listView;

    // Constructor vacío: no hace nada especial al crear el objeto
    public VentanaAlerta() {
    }

    /**
     * Asigna el controlador que maneja la lógica de alertas.
     * Esto permite que la ventana pueda llamar a métodos del controlador.
     */
    public void setControl(ControlAlerta control) {
        this.control = control;
    }

    /**
     * Crea y muestra la ventana con todos sus componentes.
     * Se ejecuta con Platform.runLater para que todo ocurra en el hilo de JavaFX.
     */
    private void crearYMostrar() {
        Platform.runLater(() -> {
            // Creamos una nueva ventana
            stage = new Stage();
            stage.setTitle("Alertas de Productos");

            // Contenedor vertical con 10px de separación entre elementos
            VBox root = new VBox(10);
            root.setStyle("-fx-padding: 10;"); // Margen interno

            // Botón que, al presionarlo, revisa el stock y muestra alertas
            Button btnRevisar = new Button("Revisar stock y generar alertas");
            btnRevisar.setOnAction(e -> {
                // Llama al controlador para revisar el stock
                List<String> alertas = control.revisarStock();
                // Limpia mensajes previos
                listView.getItems().clear();
                // Muestra los nuevos mensajes
                listView.getItems().addAll(alertas);
            });

            // Lista donde se mostrarán los mensajes
            listView = new ListView<>();

            // Añadimos el botón y la lista al contenedor
            root.getChildren().addAll(btnRevisar, listView);

            // Creamos la escena y se la asignamos a la ventana
            Scene scene = new Scene(root, 400, 300);
            stage.setScene(scene);
            stage.show(); // Mostramos la ventana
        });
    }

    /**
     * Muestra la ventana de alertas.
     * Si ya existe, la trae al frente. Si no, la crea y la muestra.
     */
    public void muestra() {
        if (stage != null) {
            // Si la ventana ya está creada
            Platform.runLater(() -> {
                if (!stage.isShowing()) {
                    stage.show();
                }
                stage.toFront(); // La pone delante de cualquier otra
            });
        } else {
            // Si no está creada, la generamos y mostramos
            crearYMostrar();
        }
    }
}
