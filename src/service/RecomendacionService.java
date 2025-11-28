package service;

import model.AnimeBase;
import model.Genero;
import model.Estado;
import repository.AnimeRepository;
import exception.PersistenciaException;
import util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Servicio que coordina los casos de uso de recomendación.
 * 
 * GRASP - Controller: Coordina las operaciones de recomendación.
 * 
 * GRASP - Polymorphism: Usa estrategias de recomendación intercambiables.
 * 
 * SOLID - OCP: Abierto a extensión (nuevas estrategias) cerrado a modificación.
 * 
 * SOLID - DIP: Depende de abstracciones (CriterioRecomendacion, AnimeRepository).
 */
public class RecomendacionService {
    
    private final AnimeRepository animeRepository;
    
    public RecomendacionService(AnimeRepository animeRepository) {
        this.animeRepository = animeRepository;
    }
    
    // ========== RF4: Recomendaciones ==========
    
    /**
     * Obtiene recomendaciones usando una estrategia específica.
     * 
     * GRASP - Strategy Pattern: El criterio de recomendación es intercambiable.
     * SOLID - OCP: Para nuevos criterios, solo se agregan nuevas clases.
     * 
     * @param criterio estrategia de recomendación a aplicar
     * @param cantidad cantidad máxima de resultados
     * @return lista de anime recomendados
     */
    public List<AnimeBase> obtenerRecomendaciones(CriterioRecomendacion criterio, int cantidad)
            throws PersistenciaException {
        
        List<AnimeBase> todosLosAnimes = animeRepository.findAll();
        return criterio.recomendar(todosLosAnimes, cantidad);
    }
    
    /**
     * Obtiene el Top N global (mejor calificados de todo el catálogo).
     */
    public List<AnimeBase> getTopGlobal(int cantidad) throws PersistenciaException {
        return obtenerRecomendaciones(new RecomendacionTopGlobal(), cantidad);
    }
    
    /**
     * Obtiene el Top N de un género específico.
     */
    public List<AnimeBase> getTopPorGenero(Genero genero, int cantidad) throws PersistenciaException {
        return obtenerRecomendaciones(new RecomendacionTopPorGenero(genero), cantidad);
    }
    
    /**
     * Obtiene el Top N de un estado específico.
     */
    public List<AnimeBase> getTopPorEstado(Estado estado, int cantidad) throws PersistenciaException {
        return obtenerRecomendaciones(new RecomendacionPorEstado(estado), cantidad);
    }
    
    /**
     * Obtiene recomendaciones combinando múltiples criterios.
     * Ejemplo: Top anime de género SHONEN con calificación >= 4.
     */
    public List<AnimeBase> getRecomendacionesAvanzadas(Genero genero, Integer calificacionMinima,
                                                        Estado estado, int cantidad)
            throws PersistenciaException {
        
        FiltroAnime filtro = new FiltroAnime()
            .porGenero(genero)
            .porEstado(estado)
            .porCalificacionMinima(calificacionMinima)
            .soloCalificados();
        
        // Filtrar anime que cumplan los criterios
        List<AnimeBase> todos = animeRepository.findAll();
        List<AnimeBase> filtrados = new ArrayList<>();
        
        for (AnimeBase anime : todos) {
            if (filtro.cumpleFiltro(anime)) {
                filtrados.add(anime);
            }
        }
        
        // Ordenar por calificación (mayor a menor)
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
}
