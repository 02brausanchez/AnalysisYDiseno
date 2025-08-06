package mx.uam.ayd.proyecto.presentacion.agregarProducto;

import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;

import java.util.List;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

@Component
public class ControlAgregarProducto {
    private final ServicioProducto servicioProducto;
    private final VentanaAgregarProducto ventana;

    @Autowired
    public ControlAgregarProducto(
            ServicioProducto servicioProducto,
            VentanaAgregarProducto ventana) {
        this.servicioProducto = servicioProducto;
        this.ventana = ventana;
    }

    @PostConstruct
    public void init() {
        ventana.setControlAgregarProducto(this);}

    public void inicia() {
        ventana.muestra();
    }

    public void agregarProducto(String nombre, TipoProducto tipoProducto, MarcaProducto marcaProducto,
                                double precio, int cantidad, UnidadProducto unidadProducto, LocalDate fechaCaducidad) {
        try{
            servicioProducto.agregarProducto(nombre, tipoProducto, marcaProducto, precio, cantidad, unidadProducto, fechaCaducidad);
            ventana.muestraDialogoConMensaje("Producto agregado exitosamente.");
        }catch(Exception ex){
            ventana.muestraDialogoConMensaje("Error al agregar producto: "+ex.getMessage());
        }

        termina();
    }

    public void termina() {
        ventana.setVisible(false);
    }
}

