package utilidad;

import modelo.AnimeBase;

/**
 * Criterio de ordenamiento por calificación del usuario.
 */
public class OrdenamientoPorCalificacion implements CriterioOrdenamiento {
    
    private final boolean descendente;
    
    public OrdenamientoPorCalificacion() {
        this(true);
    }
    
    public OrdenamientoPorCalificacion(boolean descendente) {
        this.descendente = descendente;
    }
    
    @Override
    public int compare(AnimeBase a1, AnimeBase a2) {
        if (!a1.tieneCalificacion() && !a2.tieneCalificacion()) {
            return 0;
        }
        if (!a1.tieneCalificacion()) {
            return 1;
        }
        if (!a2.tieneCalificacion()) {
            return -1;
        }
        
        int resultado = Integer.compare(a1.obtenerCalificacion(), a2.obtenerCalificacion());
        return descendente ? -resultado : resultado;
    }
    
    @Override
    public String obtenerDescripcion() {
        return "Por calificación " + (descendente ? "(mejor primero)" : "(peor primero)");
    }
}

