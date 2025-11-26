package util;

import model.AnimeBase;
import java.util.List;

/**
 * Interfaz que define un criterio/estrategia de recomendación de anime.
 * 
 * SOLID - OCP (Open/Closed Principle): Para agregar nuevos criterios de recomendación
 * se crean nuevas clases que implementen esta interfaz, sin modificar las existentes.
 * 
 * GRASP - Strategy Pattern (Polymorphism): Encapsula algoritmos de recomendación
 * intercambiables, permitiendo variar el comportamiento en tiempo de ejecución.
 * 
 * GRASP - Protected Variations: Aísla los cambios en criterios de recomendación.
 */
public interface CriterioRecomendacion {
    
    /**
     * Aplica el criterio de recomendación sobre una lista de anime.
     * 
     * @param animes lista de anime disponibles
     * @param cantidad cantidad máxima de recomendaciones a retornar
     * @return lista de anime recomendados según el criterio
     */
    List<AnimeBase> recomendar(List<AnimeBase> animes, int cantidad);
    
    /**
     * Retorna el nombre descriptivo del criterio.
     */
    String getNombre();
    
    /**
     * Retorna una descripción detallada del criterio.
     */
    String getDescripcion();
}

