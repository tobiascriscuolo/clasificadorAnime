package util;

import model.AnimeBase;
import model.Genero;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        return animes.stream()
            .filter(a -> a.perteneceAGenero(genero))
            .filter(AnimeBase::tieneCalificacion)
            .sorted(Comparator.comparingInt(AnimeBase::getCalificacion).reversed())
            .limit(cantidad)
            .collect(Collectors.toList());
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

