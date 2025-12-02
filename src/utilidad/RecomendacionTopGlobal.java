package utilidad;

import modelo.AnimeBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Estrategia de recomendación: Top N anime mejor calificados globalmente.
 */
public class RecomendacionTopGlobal implements CriterioRecomendacion {
    
    @Override
    public List<AnimeBase> recomendar(List<AnimeBase> animes, int cantidad) {
        List<AnimeBase> calificados = new ArrayList<>();
        for (AnimeBase anime : animes) {
            if (anime.tieneCalificacion()) {
                calificados.add(anime);
            }
        }
        
        Collections.sort(calificados, new Comparator<AnimeBase>() {
            @Override
            public int compare(AnimeBase a1, AnimeBase a2) {
                return Integer.compare(a2.obtenerCalificacion(), a1.obtenerCalificacion());
            }
        });
        
        List<AnimeBase> resultado = new ArrayList<>();
        for (int i = 0; i < Math.min(cantidad, calificados.size()); i++) {
            resultado.add(calificados.get(i));
        }
        
        return resultado;
    }
    
    @Override
    public String obtenerNombre() {
        return "Top Global";
    }
    
    @Override
    public String obtenerDescripcion() {
        return "Los anime mejor calificados de todo el catálogo";
    }
}

