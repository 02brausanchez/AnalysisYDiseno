package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.uam.ayd.proyecto.datos.AlertaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Alerta;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;

@Service
public class ServicioAlerta {

    private final AlertaRepository alertaRepository;

    @Autowired
    public ServicioAlerta(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    /**
     * Crea una nueva alerta asociada a un umbral
     * @param umbral Umbral asociado a la alerta
     * @param correo Correo para notificación (puede ser null)
     * @param mensajePersonalizado Mensaje de la alerta
     * @param fecha Fecha programada para la alerta
     * @return Alerta creada
     * @throws IllegalArgumentException si el umbral ya tiene una alerta asociada
     */
    public Alerta crearAlerta(Umbral umbral, String correo, String mensajePersonalizado, LocalDateTime fecha) {
        if(umbral.getAlerta() != null) {
            throw new IllegalArgumentException("Este umbral ya tiene una alerta asociada");
        }

        Alerta alerta = new Alerta();
        alerta.setMensajePersonalizado(mensajePersonalizado);
        alerta.setEnviadoPorCorreo(correo != null && !correo.isEmpty());
        alerta.setFechaHoraEnvio(fecha);
        alerta.setUmbral(umbral);

        umbral.setAlerta(alerta);

        return alertaRepository.save(alerta);
    }

    /**
     * Edita una alerta existente
     * @param idAlerta ID de la alerta a editar
     * @param correo Nuevo correo para notificación (puede ser null)
     * @param mensajePersonalizado Nuevo mensaje
     * @param fecha Nueva fecha programada
     * @return Alerta actualizada
     * @throws IllegalArgumentException si no existe la alerta
     */
    public Alerta editarAlerta(long idAlerta, String correo, String mensajePersonalizado, LocalDateTime fecha) {
        Alerta alerta = alertaRepository.findById(idAlerta)
                .orElseThrow(() -> new IllegalArgumentException("No existe la alerta especificada"));

        alerta.setMensajePersonalizado(mensajePersonalizado);
        alerta.setEnviadoPorCorreo(correo != null && !correo.isEmpty());
        alerta.setFechaHoraEnvio(fecha);

        return alertaRepository.save(alerta);
    }
}