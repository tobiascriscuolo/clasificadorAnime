package util;

import model.AnimeBase;
import model.Estado;
import model.Genero;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Builder para construir filtros combinados de anime.
 * Permite encadenar múltiples criterios de filtrado.
 * 
 * SOLID - SRP: Solo se encarga de la construcción de filtros.
 * 
 * GRASP - Information Expert: Conoce cómo combinar predicados de filtrado.
 * 
 * Patrón Builder: Facilita la construcción de filtros complejos.
 */
public class FiltroAnime {
    
    private Predicate<AnimeBase> predicado;
    
    public FiltroAnime() {
        // Predicado inicial que acepta todo
        this.predicado = a -> true;
    }
    
    /**
     * Filtra por título (coincidencia parcial, case-insensitive).
     */
    public FiltroAnime porTitulo(String texto) {
        if (texto != null && !texto.trim().isEmpty()) {
            predicado = predicado.and(a -> a.tituloContiene(texto.trim()));
        }
        return this;
    }
    
    /**
     * Filtra por rango de años.
     */
    public FiltroAnime porRangoAnios(Integer desde, Integer hasta) {
        if (desde != null && hasta != null) {
            predicado = predicado.and(a -> a.lanzadoEntre(desde, hasta));
        } else if (desde != null) {
            predicado = predicado.and(a -> a.getAnioLanzamiento() >= desde);
        } else if (hasta != null) {
            predicado = predicado.and(a -> a.getAnioLanzamiento() <= hasta);
        }
        return this;
    }
    
    /**
     * Filtra por género específico.
     */
    public FiltroAnime porGenero(Genero genero) {
        if (genero != null) {
            predicado = predicado.and(a -> a.perteneceAGenero(genero));
        }
        return this;
    }
    
    /**
     * Filtra por cualquiera de los géneros especificados.
     */
    public FiltroAnime porGeneros(Set<Genero> generos) {
        if (generos != null && !generos.isEmpty()) {
            Set<Genero> copia = new HashSet<>(generos);
            predicado = predicado.and(a -> a.perteneceAAlgunGenero(copia));
        }
        return this;
    }
    
    /**
     * Filtra por estado.
     */
    public FiltroAnime porEstado(Estado estado) {
        if (estado != null) {
            predicado = predicado.and(a -> a.getEstado() == estado);
        }
        return this;
    }
    
    /**
     * Filtra por calificación mínima.
     */
    public FiltroAnime porCalificacionMinima(Integer minima) {
        if (minima != null && minima > 0) {
            predicado = predicado.and(a -> a.cumpleCalificacionMinima(minima));
        }
        return this;
    }
    
    /**
     * Filtra solo anime con calificación.
     */
    public FiltroAnime soloCalificados() {
        predicado = predicado.and(AnimeBase::tieneCalificacion);
        return this;
    }
    
    /**
     * Filtra por estudio.
     */
    public FiltroAnime porEstudio(String estudio) {
        if (estudio != null && !estudio.trim().isEmpty()) {
            String estudioLower = estudio.trim().toLowerCase();
            predicado = predicado.and(a -> a.getEstudio().toLowerCase().contains(estudioLower));
        }
        return this;
    }
    
    /**
     * Retorna el predicado construido.
     */
    public Predicate<AnimeBase> build() {
        return predicado;
    }
    
    /**
     * Evalúa si un anime cumple el filtro.
     */
    public boolean test(AnimeBase anime) {
        return predicado.test(anime);
    }
}

