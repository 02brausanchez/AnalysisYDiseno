package mx.uam.ayd.proyecto.presentacion.principal;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.presentacion.agregarUsuario.ControlAgregarUsuario;
import mx.uam.ayd.proyecto.presentacion.listarUsuarios.ControlListarUsuarios;
import mx.uam.ayd.proyecto.presentacion.listarGrupos.ControlListarGrupos;
import mx.uam.ayd.proyecto.presentacion.configurarUmbrales.ControlConfiguracionUmbrales;
import mx.uam.ayd.proyecto.presentacion.alertas.ControlAlerta;
import mx.uam.ayd.proyecto.presentacion.alertas.VentanaAlerta;


/**
 * Esta clase lleva el flujo de control de la ventana principal
 * 
 * @author humbertocervantes
 *
 */
@Component
public class ControlPrincipal {

	private final ControlAgregarUsuario controlAgregarUsuario;
	private final ControlListarUsuarios controlListarUsuarios;
	private final ControlListarGrupos controlListarGrupos;
	private final ControlConfiguracionUmbrales controlConfiguracionUmbrales;
	private final ControlAlerta controlAlerta;
	private final VentanaAlerta ventanaAlerta;
	private final VentanaPrincipal ventana;



	@Autowired
	public ControlPrincipal(
			ControlAgregarUsuario controlAgregarUsuario,
			ControlListarUsuarios controlListarUsuarios,
			ControlListarGrupos controlListarGrupos,
			ControlConfiguracionUmbrales controlConfiguracionUmbrales,
			VentanaPrincipal ventana,
			ControlAlerta controlAlerta,
			VentanaAlerta ventanaAlerta){
		this.controlAgregarUsuario = controlAgregarUsuario;
		this.controlListarUsuarios = controlListarUsuarios;
		this.controlListarGrupos = controlListarGrupos;
		this.controlConfiguracionUmbrales = controlConfiguracionUmbrales;
		this.ventana = ventana;
		this.controlAlerta = controlAlerta;
		this.ventanaAlerta = ventanaAlerta;
	}
	
	/**
	 * Método que se ejecuta después de la construcción del bean
	 * y realiza la conexión bidireccional entre el control principal y la ventana principal
	 */
	@PostConstruct
	public void init() {
		ventana.setControlPrincipal(this);
	}
	
	/**
	 * Inicia el flujo de control de la ventana principal
	 * 
	 */
	public void inicia() {
		ventana.muestra();
	}

	/*
		Metodo que arranca la historia de usuario "configurar umbrales"
	 */
	public void configurarUmbrales() {controlConfiguracionUmbrales.inicia();}


	/**
	 * Método que arranca la historia de usuario "agregar usuario"
	 * 
	 */
	public void agregarUsuario() {
		controlAgregarUsuario.inicia();
	}
	
	/**
	 * Método que arranca la historia de usuario "listar usuarios"
	 * 
	 */
	public void listarUsuarios() {
		controlListarUsuarios.inicia();
	}

	/**
	 * Método que arranca la historia de usuario "listar grupos"
	 * 
	 */
	public void listarGrupos() {
		controlListarGrupos.inicia();
	}


	/**
	 * Método para mostrar ventana de alertas
	 */
	public void mostrarAlertas() {
		controlAlerta.inicia();
	}
}
