package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;

import mx.uam.ayd.proyecto.negocio.modelo.Umbral;

public interface UmbralRepository extends CrudRepository<Umbral, Long> {
    Umbral findByProductoId(Long idProducto);
}