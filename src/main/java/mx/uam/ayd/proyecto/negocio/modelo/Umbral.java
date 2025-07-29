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
public class Umbral{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idUmbral;

    private int stockMinimo;
    private boolean alertaAbilitada;
    private String mensaje;

    private final List <Umbral> umbrales = new ArrayList <> ();

    /**
     * @return the idProducto
     */
    public long getIdUmbral() {
        return idUmbral;
    }

    /**
     * @param idProducto the idProducto to set
     */
    public void setIdUmbral(long idUmbral) {
        this.idUmbral = idUmbral;
    }

    /**
     * @return the nombre
     */
    public String getStockMinimo() {
        return stockMinimo;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    /**
     * @return the productos
     */
    public List<Umbral> getUmbrales() {
        return umbrales;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
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
        return "Umbral [idUmbral" + idUmbral + ", stockMinimo=" + stockMinimo + ", aleraAbilitada=" + alertaAbilitada + ", mensaje=" + mensaje "]";
    }
}