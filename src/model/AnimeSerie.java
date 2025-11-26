package model;

import java.util.Set;

/**
 * Representa un anime de tipo serie (múltiples episodios).
 * 
 * SOLID - LSP: AnimeSerie puede sustituir a AnimeBase en cualquier contexto
 * sin alterar el comportamiento esperado del programa.
 * 
 * SOLID - SRP: Esta clase solo añade los atributos específicos de una serie.
 */
public class AnimeSerie extends AnimeBase {
    
    private static final long serialVersionUID = 1L;
    
    private int cantidadCapitulos;
    private boolean enEmision;
    
    /**
     * Constructor para crear una serie de anime.
     * 
     * GRASP - Creator: Las instancias de AnimeSerie son creadas por AnimeService
     * que tiene la información necesaria para su inicialización.
     */
    public AnimeSerie(String titulo, int anioLanzamiento, String estudio, 
                      int cantidadCapitulos, Set<Genero> generos) {
        super(titulo, anioLanzamiento, estudio, generos);
        this.cantidadCapitulos = cantidadCapitulos;
        this.enEmision = false;
    }
    
    public AnimeSerie(String titulo, int anioLanzamiento, String estudio,
                      int cantidadCapitulos, Set<Genero> generos, boolean enEmision) {
        super(titulo, anioLanzamiento, estudio, generos);
        this.cantidadCapitulos = cantidadCapitulos;
        this.enEmision = enEmision;
    }
    
    @Override
    public TipoAnime getTipo() {
        return TipoAnime.SERIE;
    }
    
    @Override
    public int getDuracion() {
        return cantidadCapitulos;
    }
    
    @Override
    public String getDescripcionDuracion() {
        String emision = enEmision ? " (en emisión)" : "";
        return cantidadCapitulos + " capítulos" + emision;
    }
    
    // ========== Getters y Setters específicos ==========
    
    public int getCantidadCapitulos() {
        return cantidadCapitulos;
    }
    
    public void setCantidadCapitulos(int cantidadCapitulos) {
        this.cantidadCapitulos = cantidadCapitulos;
    }
    
    public boolean isEnEmision() {
        return enEmision;
    }
    
    public void setEnEmision(boolean enEmision) {
        this.enEmision = enEmision;
    }
    
    @Override
    public String toString() {
        return String.format("[SERIE] %s (%d) - %s - %s [%s] ★%s", 
            titulo, anioLanzamiento, estudio, getDescripcionDuracion(),
            estado.getDescripcion(),
            tieneCalificacion() ? String.valueOf(calificacionUsuario) : "-");
    }
}

