package utilidad;

import modelo.AnimeBase;
import java.util.Comparator;

/**
 * Interfaz funcional que define un criterio de ordenamiento para anime.
 */
@FunctionalInterface
public interface CriterioOrdenamiento extends Comparator<AnimeBase> {
    
    @Override
    int compare(AnimeBase a1, AnimeBase a2);
    
    default String obtenerDescripcion() {
        return "Criterio de ordenamiento";
    }
}

