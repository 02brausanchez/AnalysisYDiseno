package mx.uam.ayd.proyecto.datos;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface VentaRepository extends CrudRepository<Venta, Long> {

    List<Venta> findByFecha(LocalDate fecha);
}