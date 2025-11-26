package util;

import model.AnimeBase;
import model.Estado;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Estrategia de recomendación: Top N anime de un estado específico.
 * Útil para encontrar los mejores anime por ver, los mejores finalizados, etc.
 * 
 * SOLID - SRP: Solo implementa la recomendación filtrada por estado.
 * SOLID - OCP: Nueva estrategia que extiende el sistema.
 */
public class RecomendacionPorEstado implements CriterioRecomendacion {
    
    private final Estado estado;
    
    public RecomendacionPorEstado(Estado estado) {
        this.estado = estado;
    }
    
    @Override
    public List<AnimeBase> recomendar(List<AnimeBase> animes, int cantidad) {
        return animes.stream()
            .filter(a -> a.getEstado() == estado)
            .filter(AnimeBase::tieneCalificacion)
            .sorted(Comparator.comparingInt(AnimeBase::getCalificacion).reversed())
            .limit(cantidad)
            .collect(Collectors.toList());
    }
    
    @Override
    public String getNombre() {
        return "Top " + estado.getDescripcion();
    }
    
    @Override
    public String getDescripcion() {
        return "Los anime mejor calificados con estado: " + estado.getDescripcion();
    }
    
    public Estado getEstado() {
        return estado;
    }
}

