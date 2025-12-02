package utilidad;

import modelo.AnimeBase;
import modelo.Estado;
import modelo.Genero;

import java.util.HashSet;
import java.util.Set;

/**
 * Builder para construir filtros combinados de anime.
 */
public class FiltroAnime {
    
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
    
    public FiltroAnime porTitulo(String texto) {
        if (texto != null && !texto.trim().isEmpty()) {
            this.textoBusqueda = texto.trim();
        }
        return this;
    }
    
    public FiltroAnime porRangoAnios(Integer desde, Integer hasta) {
        this.anioDesde = desde;
        this.anioHasta = hasta;
        return this;
    }
    
    public FiltroAnime porGenero(Genero genero) {
        this.genero = genero;
        return this;
    }
    
    public FiltroAnime porGeneros(Set<Genero> generos) {
        if (generos != null && !generos.isEmpty()) {
            this.generos = new HashSet<>(generos);
        }
        return this;
    }
    
    public FiltroAnime porEstado(Estado estado) {
        this.estado = estado;
        return this;
    }
    
    public FiltroAnime porCalificacionMinima(Integer minima) {
        if (minima != null && minima > 0) {
            this.calificacionMinima = minima;
        }
        return this;
    }
    
    public FiltroAnime soloCalificados() {
        this.soloCalificados = true;
        return this;
    }
    
    public FiltroAnime porEstudio(String estudio) {
        if (estudio != null && !estudio.trim().isEmpty()) {
            this.estudio = estudio.trim().toLowerCase();
        }
        return this;
    }
    
    /**
     * Evalúa si un anime cumple TODOS los criterios del filtro.
     */
    public boolean cumpleFiltro(AnimeBase anime) {
        if (textoBusqueda != null && !anime.tituloContiene(textoBusqueda)) {
            return false;
        }
        
        if (anioDesde != null && anioHasta != null) {
            if (!anime.lanzadoEntre(anioDesde, anioHasta)) {
                return false;
            }
        } else if (anioDesde != null) {
            if (anime.obtenerAnioLanzamiento() < anioDesde) {
                return false;
            }
        } else if (anioHasta != null) {
            if (anime.obtenerAnioLanzamiento() > anioHasta) {
                return false;
            }
        }
        
        if (genero != null && !anime.perteneceAGenero(genero)) {
            return false;
        }
        
        if (generos != null && !generos.isEmpty() && !anime.perteneceAAlgunGenero(generos)) {
            return false;
        }
        
        if (estado != null && anime.obtenerEstado() != estado) {
            return false;
        }
        
        if (calificacionMinima != null && !anime.cumpleCalificacionMinima(calificacionMinima)) {
            return false;
        }
        
        if (soloCalificados && !anime.tieneCalificacion()) {
            return false;
        }
        
        if (estudio != null && !anime.obtenerEstudio().toLowerCase().contains(estudio)) {
            return false;
        }
        
        return true;
    }
    
    public boolean probar(AnimeBase anime) {
        return cumpleFiltro(anime);
    }
}

