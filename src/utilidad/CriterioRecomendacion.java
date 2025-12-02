package utilidad;

import modelo.AnimeBase;
import java.util.List;

/**
 * Interfaz que define un criterio/estrategia de recomendación de anime.
 */
public interface CriterioRecomendacion {
    
    /**
     * Aplica el criterio de recomendación sobre una lista de anime.
     */
    List<AnimeBase> recomendar(List<AnimeBase> animes, int cantidad);
    
    /**
     * Retorna el nombre descriptivo del criterio.
     */
    String obtenerNombre();
    
    /**
     * Retorna una descripción detallada del criterio.
     */
    String obtenerDescripcion();
}

