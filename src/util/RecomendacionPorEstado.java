package util;

import model.AnimeBase;
import model.Estado;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        // Filtrar por estado y que tengan calificación
        List<AnimeBase> filtrados = new ArrayList<>();
        for (AnimeBase anime : animes) {
            if (anime.getEstado() == estado && anime.tieneCalificacion()) {
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
