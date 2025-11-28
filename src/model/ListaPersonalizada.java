package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa una lista personalizada de anime creada por el usuario.
 * Permite organizar anime en colecciones con nombres descriptivos.
 * 
 * SOLID - SRP: Esta clase solo maneja la colección de anime en una lista específica.
 * 
 * GRASP - Information Expert: La lista conoce sus propios anime y puede
 * responder preguntas sobre su contenido.
 * 
 * Nota de diseño: Se usa List en lugar de Set para permitir ordenamiento personalizado.
 * Un mismo anime puede estar en múltiples listas (relación N:M).
 */
public class ListaPersonalizada implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String nombre;
    private String descripcion;
    private List<AnimeBase> animes;
    
    /**
     * Constructor para crear una nueva lista personalizada.
     * 
     * @param nombre nombre único de la lista
     */
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
     * 
     * @param anime anime a agregar
     * @return true si se agregó, false si ya existía
     */
    public boolean agregarAnime(AnimeBase anime) {
        if (anime == null || contieneAnime(anime)) {
            return false;
        }
        return animes.add(anime);
    }
    
    /**
     * Remueve un anime de la lista.
     * 
     * @param anime anime a remover
     * @return true si se removió, false si no existía
     */
    public boolean removerAnime(AnimeBase anime) {
        return animes.remove(anime);
    }
    
    /**
     * Verifica si la lista contiene un anime específico.
     * GRASP - Information Expert: La lista sabe su contenido.
     */
    public boolean contieneAnime(AnimeBase anime) {
        return animes.contains(anime);
    }
    
    /**
     * Verifica si la lista contiene un anime por título.
     */
    public boolean contieneAnimePorTitulo(String titulo) {
        for (AnimeBase anime : animes) {
            if (anime.getTitulo().equalsIgnoreCase(titulo)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene la cantidad de anime en la lista.
     */
    public int getCantidadAnimes() {
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
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    /**
     * Retorna una vista inmutable de los anime de la lista.
     * Protege el estado interno de modificaciones externas.
     */
    public List<AnimeBase> getAnimes() {
        return Collections.unmodifiableList(animes);
    }
    
    // ========== equals, hashCode, toString ==========
    
    /**
     * Dos listas son iguales si tienen el mismo nombre (clave única).
     */
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

