package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Alerta;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;

/**
 * Este repositorio maneja las operaciones con la tabla de 'Alerta' en la base de datos.
 * Extiende de CrudRepository, así que automáticamente tenemos métodos básicos
 * como guardar, eliminar, buscar por ID, etc.
 */
public interface AlertaRepository extends CrudRepository<Alerta, Long> {

    /**
     * Método personalizado para buscar una alerta a partir de su umbral asociado.
     * Spring Data se encarga de implementar esto automáticamente
     * solo con leer el nombre del método.
     *
     * @param umbral Objeto Umbral para el que queremos encontrar una alerta
     * @return La alerta que tenga ese umbral, o null si no existe.
     */
    Alerta findByUmbral(Umbral umbral);
}
