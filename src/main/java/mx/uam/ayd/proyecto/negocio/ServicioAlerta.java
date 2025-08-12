package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.uam.ayd.proyecto.datos.AlertaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Alerta;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Servicio que maneja toda la lógica relacionada con las alertas.
 * Se encarga de crearlas, editarlas y decidir si deben generarse o actualizarse.
 */
@Service
public class ServicioAlerta {

    // Repositorio para guardar y recuperar alertas de la base de datos
    private final AlertaRepository alertaRepository;

    @Autowired
    public ServicioAlerta(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    /**
     * Crea una nueva alerta vinculada a un umbral.
     * Lanza una excepción si ese umbral ya tiene una alerta.
     */
    public Alerta crearAlerta(Umbral umbral, String correo, String mensajePersonalizado, LocalDateTime fecha) {
        // Verificamos si el umbral ya tiene una alerta
        if (umbral.getAlerta() != null) {
            throw new IllegalArgumentException("Este umbral ya tiene una alerta asociada");
        }

        // Creamos una nueva alerta y le asignamos los datos
        Alerta alerta = new Alerta();
        alerta.setMensajePersonalizado(mensajePersonalizado);
        alerta.setEnviadoPorCorreo(correo != null && !correo.isEmpty());
        alerta.setFechaHoraEnvio(fecha);
        alerta.setUmbral(umbral);

        // Asociamos la alerta al umbral
        umbral.setAlerta(alerta);

        // Guardamos la alerta en la base de datos
        return alertaRepository.save(alerta);
    }

    /**
     * Edita una alerta existente usando su ID.
     * Permite actualizar el mensaje, si se envía por correo y la fecha.
     */
    public Alerta editarAlerta(long idAlerta, String correo, String mensajePersonalizado, LocalDateTime fecha) {
        // Buscamos la alerta por ID, si no existe lanzamos excepción
        Alerta alerta = alertaRepository.findById(idAlerta)
                .orElseThrow(() -> new IllegalArgumentException("No existe la alerta especificada"));

        // Actualizamos los campos con los nuevos valores
        alerta.setMensajePersonalizado(mensajePersonalizado);
        alerta.setEnviadoPorCorreo(correo != null && !correo.isEmpty());
        alerta.setFechaHoraEnvio(fecha);

        // Guardamos los cambios
        return alertaRepository.save(alerta);
    }

    /**
     * Crea o actualiza una alerta si es necesario, según el stock y el umbral.
     * - Si no existe alerta y el stock está bajo, la crea.
     * - Si ya existe alerta, la actualiza con un nuevo mensaje y fecha.
     * - Si el stock está bien, no hace nada.
     */
    public Alerta crearAlertaSiNecesaria(Producto producto, Umbral umbral) {
        // Intentamos obtener la alerta asociada (primero de memoria)
        Alerta alertaExistente = umbral.getAlerta();

        // Si no está en memoria, la buscamos en la base de datos
        if (alertaExistente == null) {
            alertaExistente = alertaRepository.findByUmbral(umbral);
        }

        // Si el stock está por debajo del mínimo...
        if (producto.getCantidadStock() < umbral.getValorMinimo()) {
            if (alertaExistente == null) {
                // No existe alerta → creamos una nueva
                String mensaje = "El producto '" + producto.getNombre() + "' está por debajo del mínimo.";
                Alerta nuevaAlerta = new Alerta();
                nuevaAlerta.setMensajePersonalizado(mensaje);
                nuevaAlerta.setEnviadoPorCorreo(false);
                nuevaAlerta.setFechaHoraEnvio(LocalDateTime.now());
                nuevaAlerta.setUmbral(umbral);

                // Asociamos la alerta al umbral
                umbral.setAlerta(nuevaAlerta);

                return alertaRepository.save(nuevaAlerta);
            } else {
                // Ya existe alerta → la actualizamos con información más reciente
                String nuevoMensaje = "El producto '" + producto.getNombre() + "' sigue por debajo del mínimo. Stock actual: "
                        + producto.getCantidadStock() + ", mínimo permitido: " + umbral.getValorMinimo();

                alertaExistente.setMensajePersonalizado(nuevoMensaje);
                alertaExistente.setFechaHoraEnvio(LocalDateTime.now());

                return alertaRepository.save(alertaExistente);
            }
        }

        // Si el stock está suficiente, no hacemos nada
        return null;
    }
}
