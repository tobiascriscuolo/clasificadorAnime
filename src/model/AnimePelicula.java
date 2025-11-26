package model;

import java.util.Set;

/**
 * Representa un anime de tipo película.
 * 
 * SOLID - LSP: AnimePelicula puede sustituir a AnimeBase en cualquier contexto
 * sin alterar el comportamiento esperado del programa.
 * 
 * SOLID - SRP: Esta clase solo añade los atributos específicos de una película.
 */
public class AnimePelicula extends AnimeBase {
    
    private static final long serialVersionUID = 1L;
    
    private int duracionMinutos;
    private String director;
    
    /**
     * Constructor para crear una película de anime.
     * 
     * GRASP - Creator: Las instancias de AnimePelicula son creadas por AnimeService
     * que tiene la información necesaria para su inicialización.
     */
    public AnimePelicula(String titulo, int anioLanzamiento, String estudio,
                         int duracionMinutos, Set<Genero> generos) {
        super(titulo, anioLanzamiento, estudio, generos);
        this.duracionMinutos = duracionMinutos;
        this.director = "";
    }
    
    public AnimePelicula(String titulo, int anioLanzamiento, String estudio,
                         int duracionMinutos, Set<Genero> generos, String director) {
        super(titulo, anioLanzamiento, estudio, generos);
        this.duracionMinutos = duracionMinutos;
        this.director = director != null ? director : "";
    }
    
    @Override
    public TipoAnime getTipo() {
        return TipoAnime.PELICULA;
    }
    
    @Override
    public int getDuracion() {
        return duracionMinutos;
    }
    
    @Override
    public String getDescripcionDuracion() {
        int horas = duracionMinutos / 60;
        int minutos = duracionMinutos % 60;
        if (horas > 0) {
            return String.format("%dh %dmin", horas, minutos);
        }
        return duracionMinutos + " min";
    }
    
    // ========== Getters y Setters específicos ==========
    
    public int getDuracionMinutos() {
        return duracionMinutos;
    }
    
    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }
    
    public String getDirector() {
        return director;
    }
    
    public void setDirector(String director) {
        this.director = director;
    }
    
    @Override
    public String toString() {
        String directorInfo = director.isEmpty() ? "" : " - Dir: " + director;
        return String.format("[PELÍCULA] %s (%d) - %s - %s%s [%s] ★%s", 
            titulo, anioLanzamiento, estudio, getDescripcionDuracion(),
            directorInfo, estado.getDescripcion(),
            tieneCalificacion() ? String.valueOf(calificacionUsuario) : "-");
    }
}

