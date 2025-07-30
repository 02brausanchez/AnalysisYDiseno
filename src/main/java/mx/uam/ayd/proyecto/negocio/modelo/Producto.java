package mx.uam.ayd.proyecto.negocio.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
//import java.time;

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
    private Long precio;
    private int cantidadStock;
    private LocalDate fechaCaducidad;

    @OneToMany(targetEntity = Usuario.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)

    private final List<Usuario> usuarios = new ArrayList<>();

    /**
     * @return get
     */
    public Long getIdProducto() {
        return idProducto;
    }
    public String getNombre() {
        return nombre;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public String getMarca() {
        return marca;
    }
    public Long getPrecio() {
        return precio;
    }
    public int getCantidadStock() {
        return cantidadStock;
    }
    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    /**
     //* @param  the nombre to set
     */
    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }
    public void setPrecio(Long precio) {
        this.precio = precio;
    }
    public void setCantidadStock(int cantidadStock) {
        this.cantidadStock = cantidadStock;
    }
    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    /**
     *
     * Permite agregar un usuario al grupo
     * Nota: un mismo usuario no puede estar dos veces en el grupo
     *
     //* @param usuario el usuario que deseo agregar al grupo
     * @return true si el usuario se agregó correctamente, false si ya estaba en el grupo
     * @throws IllegalArgumentException si el usuario es nulo
     **/
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