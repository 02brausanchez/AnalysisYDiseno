package mx.uam.ayd.proyecto;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.datos.GrupoRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.datos.UmbralRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Grupo;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
import mx.uam.ayd.proyecto.presentacion.principal.ControlPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Clase principal que arranca la aplicación
 * construida usando el principio de inversión de control
 * Adaptada para usar JavaFX
 *
 * @author Humberto Cervantes (c) 21 Nov 2022
 */
@SpringBootApplication
public class ProyectoApplication {

    private final ControlPrincipal controlPrincipal;
    private final GrupoRepository grupoRepository;
    private final ProductoRepository productoRepository;
    private final UmbralRepository umbralRepository;

    @Autowired
    public ProyectoApplication(ControlPrincipal controlPrincipal,
                               GrupoRepository grupoRepository,
                               ProductoRepository productoRepository,
                               UmbralRepository umbralRepository) {
        this.controlPrincipal = controlPrincipal;
        this.grupoRepository = grupoRepository;
        this.productoRepository = productoRepository;
        this.umbralRepository = umbralRepository;
    }

    /**
     * Método principal
     *
     * @param args argumentos de la línea de comando
     */
    public static void main(String[] args) {
        // Lanzar la aplicación JavaFX
        Application.launch(JavaFXApplication.class, args);
    }

    /**
     * Clase interna para manejar la inicialización de JavaFX
     */
    public static class JavaFXApplication extends Application {

        private static ConfigurableApplicationContext applicationContext;

        @Override
        public void init() throws Exception {
            // Crear contexto de Spring
            SpringApplicationBuilder builder = new SpringApplicationBuilder(ProyectoApplication.class);
            builder.headless(false);
            applicationContext = builder.run(getParameters().getRaw().toArray(new String[0]));
        }

        @Override
        public void start(Stage primaryStage) {
            // Ejecutar la aplicación en el hilo de JavaFX
            Platform.runLater(() -> {
                applicationContext.getBean(ProyectoApplication.class).inicia();
            });
        }

        @Override
        public void stop() throws Exception {
            applicationContext.close();
            Platform.exit();
        }
    }

    /**
     * Método que arranca la aplicación
     * inicializa la BD y arranca el controlador principal
     */
    public void inicia() {
        inicializaBD();

        // Asegurar que el controlador se cree en el hilo JavaFX
        Platform.runLater(() -> {
            controlPrincipal.inicia();
        });
    }

    /**
     * Inicializa la BD con datos de prueba
     */
    public void inicializaBD() {
        // Crear grupos de usuarios
        Grupo grupoAdmin = new Grupo();
        grupoAdmin.setNombre("Administradores");
        grupoRepository.save(grupoAdmin);

        Grupo grupoOps = new Grupo();
        grupoOps.setNombre("Operadores");
        grupoRepository.save(grupoOps);

        // Crear productos de prueba
        Producto prod1 = new Producto();
        prod1.setNombre("Producto A");
        prod1.setCantidadStock(50);
        productoRepository.save(prod1);

        Producto prod2 = new Producto();
        prod2.setNombre("Producto B");
        prod2.setCantidadStock(10);
        productoRepository.save(prod2);

        // Crear umbrales asociados a los productos
        Umbral umbral1 = new Umbral();
        umbral1.setProducto(prod1);
        umbral1.setValorMinimo(20);
        umbralRepository.save(umbral1);

        Umbral umbral2 = new Umbral();
        umbral2.setProducto(prod2);
        umbral2.setValorMinimo(5);
        umbralRepository.save(umbral2);
    }
}
