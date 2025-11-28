package util;

import model.AnimeBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        // Filtrar solo anime con calificación
        List<AnimeBase> calificados = new ArrayList<>();
        for (AnimeBase anime : animes) {
            if (anime.tieneCalificacion()) {
                calificados.add(anime);
            }
        }
        
        // Ordenar por calificación descendente
        Collections.sort(calificados, new Comparator<AnimeBase>() {
            @Override
            public int compare(AnimeBase a1, AnimeBase a2) {
                return Integer.compare(a2.getCalificacion(), a1.getCalificacion());
            }
        });
        
        // Tomar los primeros N
        List<AnimeBase> resultado = new ArrayList<>();
        for (int i = 0; i < Math.min(cantidad, calificados.size()); i++) {
            resultado.add(calificados.get(i));
        }
        
        return resultado;
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
