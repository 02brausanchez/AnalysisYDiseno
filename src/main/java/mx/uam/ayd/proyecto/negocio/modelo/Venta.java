package mx.uam.ayd.proyecto.negocio.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Entidad de negocio Grupo
 *
 * @author humbertocervantes
 *
 */
@Entity
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idVenta;

    private LocalDate fecha;
    private float montoTotal;

    @OneToMany(targetEntity = DetalleVenta.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)

    private final List <DetalleVenta> detalleVentas = new ArrayList <> ();

    /**
     * @return the idProducto
     */
    public long getIdVenta() {
        return idVenta;
    }

    /**
     * @param idProducto the idProducto to set
     */
    public void setIdVenta(long idVenta) {
        this.idVenta = idVenta;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the productos
     */
    public List<DetalleVenta> getDetalleVentas() {
        return detalleVentas;
    }

    /**
     *
     * Permite agregar un usuario al grupo
     * Nota: un mismo usuario no puede estar dos veces en el grupo
     *
     * @param usuario el usuario que deseo agregar al grupo
     * @return true si el usuario se agregó correctamente, false si ya estaba en el grupo
     * @throws IllegalArgumentException si el usuario es nulo
     */
    public boolean addDetalleVetna(DetalleVenta detalleVenta) {


        if(detalleVenta == null) {
            throw new IllegalArgumentException("El detalleVenta no puede ser null");
        }


        if(detalleVenta.contains(detalleVenta)) {
            // Checo si el umbral está en el producto por que no se puede agregar un umbral dos veces
            return false;
        }

        return detalleVenta.add(detalleVenta);

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Venta other = (Venta) obj;
        return idVenta == other.idVenta;
    }

    @Override
    public int hashCode() {
        return (int) (31 * idProducto);
    }

    @Override
    public String toString() {
        return "Prodcuto [idprodcuto=" + idGrupo + ", nombre=" + nombre + ", marca=" + marca + "]";
    }
}