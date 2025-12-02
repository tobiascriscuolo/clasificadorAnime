package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa una lista personalizada de anime creada por el usuario.
 */
public class ListaPersonalizada implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String nombre;
    private String descripcion;
    private List<AnimeBase> animes;
    
    public ListaPersonalizada(String nombre) {
        this.nombre = nombre;
        this.descripcion = "";
        this.animes = new ArrayList<>();
    }
    
    public ListaPersonalizada(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion != null ? descripcion : "";
        this.animes = new ArrayList<>();
    }
    
    // ========== Operaciones sobre la colección ==========
    
    /**
     * Agrega un anime a la lista si no existe ya.
     */
    public boolean agregarAnime(AnimeBase anime) {
        if (anime == null || contieneAnime(anime)) {
            return false;
        }
        return animes.add(anime);
    }
    
    /**
     * Remueve un anime de la lista.
     */
    public boolean removerAnime(AnimeBase anime) {
        return animes.remove(anime);
    }
    
    /**
     * Verifica si la lista contiene un anime específico.
     */
    public boolean contieneAnime(AnimeBase anime) {
        return animes.contains(anime);
    }
    
    /**
     * Verifica si la lista contiene un anime por título.
     */
    public boolean contieneAnimePorTitulo(String titulo) {
        for (AnimeBase anime : animes) {
            if (anime.obtenerTitulo().equalsIgnoreCase(titulo)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene la cantidad de anime en la lista.
     */
    public int obtenerCantidadAnimes() {
        return animes.size();
    }
    
    /**
     * Verifica si la lista está vacía.
     */
    public boolean estaVacia() {
        return animes.isEmpty();
    }
    
    /**
     * Limpia todos los anime de la lista.
     */
    public void limpiar() {
        animes.clear();
    }
    
    // ========== Getters y Setters ==========
    
    public String obtenerNombre() {
        return nombre;
    }
    
    public void establecerNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String obtenerDescripcion() {
        return descripcion;
    }
    
    public void establecerDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public List<AnimeBase> obtenerAnimes() {
        return Collections.unmodifiableList(animes);
    }
    
    // ========== equals, hashCode, toString ==========
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListaPersonalizada that = (ListaPersonalizada) o;
        return Objects.equals(nombre.toLowerCase(), that.nombre.toLowerCase());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nombre.toLowerCase());
    }
    
    @Override
    public String toString() {
        return String.format("%s (%d anime%s)", 
            nombre, animes.size(), animes.size() != 1 ? "s" : "");
    }
}

