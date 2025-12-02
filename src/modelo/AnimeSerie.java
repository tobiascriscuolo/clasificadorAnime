package modelo;

import java.util.Set;

/**
 * Representa un anime de tipo serie (múltiples episodios).
 */
public class AnimeSerie extends AnimeBase {
    
    private static final long serialVersionUID = 1L;
    
    private int cantidadCapitulos;
    private boolean enEmision;
    
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
    public TipoAnime obtenerTipo() {
        return TipoAnime.SERIE;
    }
    
    @Override
    public int obtenerDuracion() {
        return cantidadCapitulos;
    }
    
    @Override
    public String obtenerDescripcionDuracion() {
        String emision = enEmision ? " (en emisión)" : "";
        return cantidadCapitulos + " capítulos" + emision;
    }
    
    // ========== Getters y Setters específicos ==========
    
    public int obtenerCantidadCapitulos() {
        return cantidadCapitulos;
    }
    
    public void establecerCantidadCapitulos(int cantidadCapitulos) {
        this.cantidadCapitulos = cantidadCapitulos;
    }
    
    public boolean estaEnEmision() {
        return enEmision;
    }
    
    public void establecerEnEmision(boolean enEmision) {
        this.enEmision = enEmision;
    }
    
    @Override
    public String toString() {
        return String.format("[SERIE] %s (%d) - %s - %s [%s] ★%s", 
            titulo, anioLanzamiento, estudio, obtenerDescripcionDuracion(),
            estado.obtenerDescripcion(),
            tieneCalificacion() ? String.valueOf(calificacionUsuario) : "-");
    }
}

