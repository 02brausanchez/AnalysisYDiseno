package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import org.springframework.data.repository.CrudRepository;

public interface DetalleVentaRepository extends CrudRepository<Venta, Long> {

}