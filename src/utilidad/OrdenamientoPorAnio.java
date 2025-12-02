package utilidad;

import modelo.AnimeBase;

/**
 * Criterio de ordenamiento por año de lanzamiento.
 */
public class OrdenamientoPorAnio implements CriterioOrdenamiento {
    
    private final boolean descendente;
    
    public OrdenamientoPorAnio() {
        this(true);
    }
    
    public OrdenamientoPorAnio(boolean descendente) {
        this.descendente = descendente;
    }
    
    @Override
    public int compare(AnimeBase a1, AnimeBase a2) {
        int resultado = Integer.compare(a1.obtenerAnioLanzamiento(), a2.obtenerAnioLanzamiento());
        return descendente ? -resultado : resultado;
    }
    
    @Override
    public String obtenerDescripcion() {
        return "Por año " + (descendente ? "(recientes primero)" : "(antiguos primero)");
    }
}

