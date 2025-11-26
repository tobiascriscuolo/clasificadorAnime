package model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Clase abstracta base para todos los tipos de anime.
 * 
 * SOLID - LSP (Liskov Substitution Principle): Las subclases AnimeSerie y AnimePelicula
 * pueden sustituir a AnimeBase sin alterar el comportamiento esperado.
 * 
 * SOLID - SRP: Esta clase solo maneja los atributos y comportamientos comunes a todo anime.
 * 
 * GRASP - Information Expert: AnimeBase conoce sus propios datos y puede responder
 * preguntas sobre sí mismo (pertenece a género, tiene calificación, etc.).
 */
public abstract class AnimeBase implements Calificable, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Constantes de validación
    public static final int CALIFICACION_MINIMA = 1;
    public static final int CALIFICACION_MAXIMA = 5;
    public static final int ANIO_MINIMO = 1917; // Primer anime conocido
    public static final int CAPITULOS_MINIMOS = 1;
    
    protected String titulo;
    protected int anioLanzamiento;
    protected String estudio;
    protected Estado estado;
    protected int calificacionUsuario;
    protected Set<Genero> generos;
    
    /**
     * Constructor protegido para uso de subclases.
     * 
     * @param titulo título del anime (no vacío, será clave única)
     * @param anioLanzamiento año de estreno
     * @param estudio estudio de animación
     * @param generos conjunto de géneros
     */
    protected AnimeBase(String titulo, int anioLanzamiento, String estudio, Set<Genero> generos) {
        this.titulo = titulo;
        this.anioLanzamiento = anioLanzamiento;
        this.estudio = estudio;
        this.estado = Estado.POR_VER;
        this.calificacionUsuario = 0; // 0 indica sin calificar
        this.generos = generos != null ? new HashSet<>(generos) : new HashSet<>();
    }
    
    // ========== Métodos abstractos ==========
    
    /**
     * Retorna el tipo de anime (Serie o Película).
     * Template Method: cada subclase define su tipo.
     */
    public abstract TipoAnime getTipo();
    
    /**
     * Retorna la duración representativa del anime.
     * Para series: cantidad de capítulos.
     * Para películas: duración en minutos.
     */
    public abstract int getDuracion();
    
    /**
     * Retorna una descripción formateada de la duración.
     */
    public abstract String getDescripcionDuracion();
    
    // ========== Implementación de Calificable ==========
    
    @Override
    public int getCalificacion() {
        return calificacionUsuario;
    }
    
    @Override
    public void setCalificacion(int calificacion) {
        if (calificacion < CALIFICACION_MINIMA || calificacion > CALIFICACION_MAXIMA) {
            throw new IllegalArgumentException(
                "La calificación debe estar entre " + CALIFICACION_MINIMA + " y " + CALIFICACION_MAXIMA
            );
        }
        this.calificacionUsuario = calificacion;
    }
    
    @Override
    public boolean tieneCalificacion() {
        return calificacionUsuario > 0;
    }
    
    // ========== Métodos de consulta (Information Expert) ==========
    
    /**
     * Verifica si el anime pertenece a un género específico.
     * GRASP - Information Expert: El anime sabe sus propios géneros.
     */
    public boolean perteneceAGenero(Genero genero) {
        return generos.contains(genero);
    }
    
    /**
     * Verifica si el anime tiene al menos uno de los géneros especificados.
     */
    public boolean perteneceAAlgunGenero(Set<Genero> generosABuscar) {
        for (Genero g : generosABuscar) {
            if (generos.contains(g)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Verifica si el anime fue lanzado en un rango de años.
     */
    public boolean lanzadoEntre(int anioDesde, int anioHasta) {
        return anioLanzamiento >= anioDesde && anioLanzamiento <= anioHasta;
    }
    
    /**
     * Verifica si el título contiene el texto buscado (case-insensitive).
     */
    public boolean tituloContiene(String texto) {
        return titulo.toLowerCase().contains(texto.toLowerCase());
    }
    
    /**
     * Verifica si cumple con una calificación mínima.
     */
    public boolean cumpleCalificacionMinima(int minima) {
        return tieneCalificacion() && calificacionUsuario >= minima;
    }
    
    // ========== Getters y Setters ==========
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public int getAnioLanzamiento() {
        return anioLanzamiento;
    }
    
    public void setAnioLanzamiento(int anioLanzamiento) {
        this.anioLanzamiento = anioLanzamiento;
    }
    
    public String getEstudio() {
        return estudio;
    }
    
    public void setEstudio(String estudio) {
        this.estudio = estudio;
    }
    
    public Estado getEstado() {
        return estado;
    }
    
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    
    public Set<Genero> getGeneros() {
        // Retorna copia inmutable para proteger el estado interno
        return Collections.unmodifiableSet(generos);
    }
    
    public void setGeneros(Set<Genero> generos) {
        this.generos = generos != null ? new HashSet<>(generos) : new HashSet<>();
    }
    
    public void agregarGenero(Genero genero) {
        this.generos.add(genero);
    }
    
    public void removerGenero(Genero genero) {
        this.generos.remove(genero);
    }
    
    // ========== equals, hashCode, toString ==========
    
    /**
     * Dos anime son iguales si tienen el mismo título (clave única del negocio).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof AnimeBase)) return false;
        AnimeBase animeBase = (AnimeBase) o;
        return Objects.equals(titulo.toLowerCase(), animeBase.titulo.toLowerCase());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(titulo.toLowerCase());
    }
    
    @Override
    public String toString() {
        return String.format("%s (%d) - %s [%s]", 
            titulo, anioLanzamiento, estudio, estado.getDescripcion());
    }
}

