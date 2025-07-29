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
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idDetalleVenta;

    private int cantidadVendida;
    private float subtotal;

    /**
     * @return the idProducto
     */
    public long getIdDetalleVenta() {
        return idDetalleVenta;
    }

    /**
     * @param idProducto the idProducto to set
     */
    public void setIdDetalleVenta(long idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    /**
     * @return the nombre
     */
    public String getCantidadVendida() {
        return cantidadVendida;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public float getSubtotal() {return subtotal;}

    public void setSubtotal(float subtotal) {this.subtotal = subtotal;}

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DetalleVenta other = (DetalleVenta) obj;
        return idDetalleVenta == other.idDetalleVenta;
    }

    @Override
    public int hashCode() {
        return (int) (31 * idProducto);
    }

    @Override
    public String toString() {
        return "DetalleVenta [idDetalleVenta=" + idDetalleVenta + ", cantidadVendida=" + cantidadVendida + ", subtotal=" + subtotal + "]";
    }
}