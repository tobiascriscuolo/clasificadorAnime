package util;

import model.AnimeBase;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Estrategia de recomendación: Top N anime mejor calificados globalmente.
 * 
 * SOLID - SRP: Solo implementa la recomendación por calificación global.
 * SOLID - OCP: Nueva estrategia sin modificar código existente.
 * 
 * GRASP - Strategy: Implementación concreta del patrón Strategy.
 */
public class RecomendacionTopGlobal implements CriterioRecomendacion {
    
    @Override
    public List<AnimeBase> recomendar(List<AnimeBase> animes, int cantidad) {
        return animes.stream()
            .filter(AnimeBase::tieneCalificacion) // Solo calificados
            .sorted(Comparator.comparingInt(AnimeBase::getCalificacion).reversed())
            .limit(cantidad)
            .collect(Collectors.toList());
    }
    
    @Override
    public String getNombre() {
        return "Top Global";
    }
    
    @Override
    public String getDescripcion() {
        return "Los anime mejor calificados de todo el catálogo";
    }
}

