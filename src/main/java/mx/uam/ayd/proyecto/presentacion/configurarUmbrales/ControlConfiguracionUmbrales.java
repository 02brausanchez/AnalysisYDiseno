package mx.uam.ayd.proyecto.presentacion.configurarUmbrales;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.ServicioUmbrales;

@Component
public class ControlConfiguracionUmbrales {

    @Autowired
    private ServicioUmbrales servicioUmbrales; // Servicio para manejar la lógica

    @Autowired
    private VentanaConfiguracionUmbrales ventana; // Ventana FXML

    public void inicia() {
        ventana.setControl(this);
        ventana.muestra();
    }

    public void configurarUmbral(long idProducto, int umbralMinimo) {
        servicioUmbrales.configurarUmbral(idProducto, umbralMinimo);
    }
}
