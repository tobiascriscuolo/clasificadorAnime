package utilidad;

import modelo.AnimeBase;
import modelo.Estado;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Estrategia de recomendación: Top N anime de un estado específico.
 */
public class RecomendacionPorEstado implements CriterioRecomendacion {
    
    private final Estado estado;
    
    public RecomendacionPorEstado(Estado estado) {
        this.estado = estado;
    }
    
    @Override
    public List<AnimeBase> recomendar(List<AnimeBase> animes, int cantidad) {
        List<AnimeBase> filtrados = new ArrayList<>();
        for (AnimeBase anime : animes) {
            if (anime.obtenerEstado() == estado && anime.tieneCalificacion()) {
                filtrados.add(anime);
            }
        }
        
        Collections.sort(filtrados, new Comparator<AnimeBase>() {
            @Override
            public int compare(AnimeBase a1, AnimeBase a2) {
                return Integer.compare(a2.obtenerCalificacion(), a1.obtenerCalificacion());
            }
        });
        
        List<AnimeBase> resultado = new ArrayList<>();
        for (int i = 0; i < Math.min(cantidad, filtrados.size()); i++) {
            resultado.add(filtrados.get(i));
        }
        
        return resultado;
    }
    
    @Override
    public String obtenerNombre() {
        return "Top " + estado.obtenerDescripcion();
    }
    
    @Override
    public String obtenerDescripcion() {
        return "Los anime mejor calificados con estado: " + estado.obtenerDescripcion();
    }
    
    public Estado obtenerEstado() {
        return estado;
    }
}

