package mx.uam.ayd.proyecto.negocio.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

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

    @ManyToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;

    public long getIdDetalleVenta() {
        return idDetalleVenta;
    }

    public void setIdDetalleVenta(long idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public float getSubtotal() {return subtotal;}

    public void setSubtotal(float subtotal) {this.subtotal = subtotal;}

    public void setVenta(Venta venta){
        if(venta == null){
            throw new IllegalArgumentException("La venta no puede ser null");
        }
        this.venta = venta;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        DetalleVenta other = (DetalleVenta) obj;
        return java.util.Objects.equals(idDetalleVenta, other.idDetalleVenta);
    }

    @Override
    public int hashCode() {
        return (int) (31 * idDetalleVenta);
    }

    @Override
    public String toString() {
        return "DetalleVenta [idDetalleVenta=" + idDetalleVenta + ", cantidadVendida=" + cantidadVendida + ", subtotal=" + subtotal + "idVenta=" + (venta != null ? venta.getIdVenta() : "null") + "]";
    }
}