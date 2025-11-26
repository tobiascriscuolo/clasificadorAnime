package util;

import model.AnimeBase;
import java.util.Comparator;

/**
 * Interfaz funcional que define un criterio de ordenamiento para anime.
 * 
 * SOLID - OCP (Open/Closed Principle): Nuevos criterios de ordenamiento se agregan
 * creando nuevas implementaciones, sin modificar código existente.
 * 
 * GRASP - Polymorphism: Permite tratar diferentes criterios de ordenamiento
 * de forma uniforme.
 * 
 * GRASP - Protected Variations: Encapsula la variación en los criterios de ordenamiento.
 */
@FunctionalInterface
public interface CriterioOrdenamiento extends Comparator<AnimeBase> {
    
    /**
     * Compara dos anime según el criterio específico.
     */
    @Override
    int compare(AnimeBase a1, AnimeBase a2);
    
    /**
     * Retorna la descripción del criterio para mostrar en UI.
     */
    default String getDescripcion() {
        return "Criterio de ordenamiento";
    }
}

