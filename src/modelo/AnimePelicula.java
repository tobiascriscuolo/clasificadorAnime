package modelo;

import java.util.Set;

/**
 * Representa un anime de tipo película.
 */
public class AnimePelicula extends AnimeBase {
    
    private static final long serialVersionUID = 1L;
    
    private int duracionMinutos;
    private String director;
    
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
    public TipoAnime obtenerTipo() {
        return TipoAnime.PELICULA;
    }
    
    @Override
    public int obtenerDuracion() {
        return duracionMinutos;
    }
    
    @Override
    public String obtenerDescripcionDuracion() {
        int horas = duracionMinutos / 60;
        int minutos = duracionMinutos % 60;
        if (horas > 0) {
            return String.format("%dh %dmin", horas, minutos);
        }
        return duracionMinutos + " min";
    }
    
    // ========== Getters y Setters específicos ==========
    
    public int obtenerDuracionMinutos() {
        return duracionMinutos;
    }
    
    public void establecerDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }
    
    public String obtenerDirector() {
        return director;
    }
    
    public void establecerDirector(String director) {
        this.director = director;
    }
    
    @Override
    public String toString() {
        String directorInfo = director.isEmpty() ? "" : " - Dir: " + director;
        return String.format("[PELÍCULA] %s (%d) - %s - %s%s [%s] ★%s", 
            titulo, anioLanzamiento, estudio, obtenerDescripcionDuracion(),
            directorInfo, estado.obtenerDescripcion(),
            tieneCalificacion() ? String.valueOf(calificacionUsuario) : "-");
    }
}

