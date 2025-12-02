package servicio;

import modelo.AnimeBase;
import modelo.Genero;
import modelo.Estado;
import repositorio.RepositorioAnime;
import excepcion.ExcepcionPersistencia;
import utilidad.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Servicio que coordina los casos de uso de recomendación.
 */
public class ServicioRecomendacion {
    
    private final RepositorioAnime repositorioAnime;
    
    public ServicioRecomendacion(RepositorioAnime repositorioAnime) {
        this.repositorioAnime = repositorioAnime;
    }
    
    public List<AnimeBase> obtenerRecomendaciones(CriterioRecomendacion criterio, int cantidad)
            throws ExcepcionPersistencia {
        
        List<AnimeBase> todosLosAnimes = repositorioAnime.obtenerTodos();
        return criterio.recomendar(todosLosAnimes, cantidad);
    }
    
    public List<AnimeBase> obtenerTopGlobal(int cantidad) throws ExcepcionPersistencia {
        return obtenerRecomendaciones(new RecomendacionTopGlobal(), cantidad);
    }
    
    public List<AnimeBase> obtenerTopPorGenero(Genero genero, int cantidad) throws ExcepcionPersistencia {
        return obtenerRecomendaciones(new RecomendacionTopPorGenero(genero), cantidad);
    }
    
    public List<AnimeBase> obtenerTopPorEstado(Estado estado, int cantidad) throws ExcepcionPersistencia {
        return obtenerRecomendaciones(new RecomendacionPorEstado(estado), cantidad);
    }
    
    public List<AnimeBase> obtenerRecomendacionesAvanzadas(Genero genero, Integer calificacionMinima,
                                                           Estado estado, int cantidad)
            throws ExcepcionPersistencia {
        
        FiltroAnime filtro = new FiltroAnime()
            .porGenero(genero)
            .porEstado(estado)
            .porCalificacionMinima(calificacionMinima)
            .soloCalificados();
        
        List<AnimeBase> todos = repositorioAnime.obtenerTodos();
        List<AnimeBase> filtrados = new ArrayList<>();
        
        for (AnimeBase anime : todos) {
            if (filtro.cumpleFiltro(anime)) {
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
}

