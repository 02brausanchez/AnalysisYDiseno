package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;

/**
 * Entidad de negocio Umbral
 *
 * @author braulio sanchez
 *
 */
@Entity
public class Umbral{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUmbral;

    private int valorMinimo;

    @OneToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @OneToOne
    @JoinColumn(name = "alerta_id")
    private Alerta alerta;
    // Getters y setters
    public long getIdUmbral() {
        return idUmbral;
    }

    public void setIdUmbral(long idUmbral) {
        this.idUmbral = idUmbral;
    }

    public int getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(int valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Alerta getAlerta() {
        return alerta;
    }

    public void setAlerta(Alerta alerta) {
        this.alerta = alerta;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Umbral other = (Umbral) obj;
        return idUmbral == other.idUmbral;
    }

    @Override
    public int hashCode() {
        return (int) (31 * idUmbral);
    }

    @Override
    public String toString() {
        return "Umbral [idUmbral=" + idUmbral + ", valorMinimo=" + valorMinimo +
                ", producto=" + (producto != null ? producto.getNombre() : "null") +
                ", alerta=" + (alerta != null ? alerta.getIdAlerta() : "null") + "]";
    }

}