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
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idProducto;

    private String nombre;
    private String tipoProducto;
    private String marca;
    private float precio;
    private int cantidadStock;
    private LocalDate fechaCaducidad;

    @OneToMany(targetEntity = Usuario.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)

    private final List <Usuario> usuarios = new ArrayList <> ();

    /**
     * @return the idProducto
     */
    public long getIdProducto() {
        return idProducto;
    }

    /**
     * @param idProducto the idProducto to set
     */
    public void setIdProducto(long idProducto) {
        this.idProducto = idProducto;
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
    public List<Producto> getProductos() {
        return productos;
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
    public boolean addUmbral(Umbral umbral) {


        if(umbral == null) {
            throw new IllegalArgumentException("El umbral no puede ser null");
        }


        if(umbral.contains(umbral)) {
            // Checo si el umbral está en el producto por que no se puede agregar un umbral dos veces
            return false;
        }

        return umbral.add(umbral);

    }

    /**
     *
     * Permite quitar un usuario al grupo
     *
     * @param usuario el usuario que deseo agregar al grupo
     * @return true si el usuario se quitó correctamente, false si no estaba en el grupo
     * @throws IllegalArgumentException si el usuario es nulo
     */
    public boolean removeUsuario(Usuario usuario) {
        if(usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }

        return usuarios.remove(usuario);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Producto other = (Producto) obj;
        return idProducto == other.idProducto;
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