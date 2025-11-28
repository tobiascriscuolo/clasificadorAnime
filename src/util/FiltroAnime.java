package util;

import model.AnimeBase;
import model.Estado;
import model.Genero;

import java.util.HashSet;
import java.util.Set;

/**
 * Builder para construir filtros combinados de anime.
 * Permite encadenar múltiples criterios de filtrado.
 * 
 * SOLID - SRP: Solo se encarga de la construcción de filtros.
 * 
 * GRASP - Information Expert: Conoce cómo combinar criterios de filtrado.
 * 
 * Patrón Builder: Facilita la construcción de filtros complejos paso a paso.
 */
public class FiltroAnime {
    
    // Criterios de filtrado almacenados
    private String textoBusqueda;
    private Integer anioDesde;
    private Integer anioHasta;
    private Genero genero;
    private Set<Genero> generos;
    private Estado estado;
    private Integer calificacionMinima;
    private boolean soloCalificados;
    private String estudio;
    
    public FiltroAnime() {
        this.soloCalificados = false;
    }
    
    /**
     * Filtra por título (coincidencia parcial, case-insensitive).
     */
    public FiltroAnime porTitulo(String texto) {
        if (texto != null && !texto.trim().isEmpty()) {
            this.textoBusqueda = texto.trim();
        }
        return this;
    }
    
    /**
     * Filtra por rango de años.
     */
    public FiltroAnime porRangoAnios(Integer desde, Integer hasta) {
        this.anioDesde = desde;
        this.anioHasta = hasta;
        return this;
    }
    
    /**
     * Filtra por género específico.
     */
    public FiltroAnime porGenero(Genero genero) {
        this.genero = genero;
        return this;
    }
    
    /**
     * Filtra por cualquiera de los géneros especificados.
     */
    public FiltroAnime porGeneros(Set<Genero> generos) {
        if (generos != null && !generos.isEmpty()) {
            this.generos = new HashSet<>(generos);
        }
        return this;
    }
    
    /**
     * Filtra por estado.
     */
    public FiltroAnime porEstado(Estado estado) {
        this.estado = estado;
        return this;
    }
    
    /**
     * Filtra por calificación mínima.
     */
    public FiltroAnime porCalificacionMinima(Integer minima) {
        if (minima != null && minima > 0) {
            this.calificacionMinima = minima;
        }
        return this;
    }
    
    /**
     * Filtra solo anime con calificación.
     */
    public FiltroAnime soloCalificados() {
        this.soloCalificados = true;
        return this;
    }
    
    /**
     * Filtra por estudio.
     */
    public FiltroAnime porEstudio(String estudio) {
        if (estudio != null && !estudio.trim().isEmpty()) {
            this.estudio = estudio.trim().toLowerCase();
        }
        return this;
    }
    
    /**
     * Evalúa si un anime cumple TODOS los criterios del filtro.
     * Combina los criterios con AND lógico.
     * 
     * @param anime el anime a evaluar
     * @return true si cumple todos los criterios, false si no cumple alguno
     */
    public boolean cumpleFiltro(AnimeBase anime) {
        // Verificar cada criterio - si alguno falla, retorna false
        
        // Filtro por título
        if (textoBusqueda != null && !anime.tituloContiene(textoBusqueda)) {
            return false;
        }
        
        // Filtro por rango de años
        if (anioDesde != null && anioHasta != null) {
            if (!anime.lanzadoEntre(anioDesde, anioHasta)) {
                return false;
            }
        } else if (anioDesde != null) {
            if (anime.getAnioLanzamiento() < anioDesde) {
                return false;
            }
        } else if (anioHasta != null) {
            if (anime.getAnioLanzamiento() > anioHasta) {
                return false;
            }
        }
        
        // Filtro por género específico
        if (genero != null && !anime.perteneceAGenero(genero)) {
            return false;
        }
        
        // Filtro por conjunto de géneros
        if (generos != null && !generos.isEmpty() && !anime.perteneceAAlgunGenero(generos)) {
            return false;
        }
        
        // Filtro por estado
        if (estado != null && anime.getEstado() != estado) {
            return false;
        }
        
        // Filtro por calificación mínima
        if (calificacionMinima != null && !anime.cumpleCalificacionMinima(calificacionMinima)) {
            return false;
        }
        
        // Filtro solo calificados
        if (soloCalificados && !anime.tieneCalificacion()) {
            return false;
        }
        
        // Filtro por estudio
        if (estudio != null && !anime.getEstudio().toLowerCase().contains(estudio)) {
            return false;
        }
        
        // Si pasó todos los filtros, cumple
        return true;
    }
    
    /**
     * Alias de cumpleFiltro para compatibilidad.
     */
    public boolean test(AnimeBase anime) {
        return cumpleFiltro(anime);
    }
}
