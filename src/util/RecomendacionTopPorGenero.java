package util;

import model.AnimeBase;
import model.Genero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Estrategia de recomendación: Top N anime mejor calificados de un género específico.
 * 
 * SOLID - SRP: Solo implementa la recomendación por género.
 * SOLID - OCP: Nueva estrategia sin modificar código existente.
 * 
 * GRASP - Strategy: Implementación concreta del patrón Strategy.
 */
public class RecomendacionTopPorGenero implements CriterioRecomendacion {
    
    private final Genero genero;
    
    public RecomendacionTopPorGenero(Genero genero) {
        this.genero = genero;
    }
    
    @Override
    public List<AnimeBase> recomendar(List<AnimeBase> animes, int cantidad) {
        // Filtrar por género y que tengan calificación
        List<AnimeBase> filtrados = new ArrayList<>();
        for (AnimeBase anime : animes) {
            if (anime.perteneceAGenero(genero) && anime.tieneCalificacion()) {
                filtrados.add(anime);
            }
        }
        
        // Ordenar por calificación descendente
        Collections.sort(filtrados, new Comparator<AnimeBase>() {
            @Override
            public int compare(AnimeBase a1, AnimeBase a2) {
                return Integer.compare(a2.getCalificacion(), a1.getCalificacion());
            }
        });
        
        // Tomar los primeros N
        List<AnimeBase> resultado = new ArrayList<>();
        for (int i = 0; i < Math.min(cantidad, filtrados.size()); i++) {
            resultado.add(filtrados.get(i));
        }
        
        return resultado;
    }
    
    @Override
    public String getNombre() {
        return "Top " + genero.getDescripcion();
    }
    
    @Override
    public String getDescripcion() {
        return "Los anime mejor calificados del género " + genero.getDescripcion();
    }
    
    public Genero getGenero() {
        return genero;
    }
}
