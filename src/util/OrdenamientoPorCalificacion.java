package util;

import model.AnimeBase;

/**
 * Criterio de ordenamiento por calificación del usuario.
 * 
 * SOLID - SRP: Solo implementa el ordenamiento por calificación.
 * SOLID - OCP: Extensión del sistema de ordenamiento.
 */
public class OrdenamientoPorCalificacion implements CriterioOrdenamiento {
    
    private final boolean descendente;
    
    public OrdenamientoPorCalificacion() {
        this(true); // Por defecto, mejores primero
    }
    
    public OrdenamientoPorCalificacion(boolean descendente) {
        this.descendente = descendente;
    }
    
    @Override
    public int compare(AnimeBase a1, AnimeBase a2) {
        // Los sin calificar van al final
        if (!a1.tieneCalificacion() && !a2.tieneCalificacion()) {
            return 0;
        }
        if (!a1.tieneCalificacion()) {
            return 1;
        }
        if (!a2.tieneCalificacion()) {
            return -1;
        }
        
        int resultado = Integer.compare(a1.getCalificacion(), a2.getCalificacion());
        return descendente ? -resultado : resultado;
    }
    
    @Override
    public String getDescripcion() {
        return "Por calificación " + (descendente ? "(mejor primero)" : "(peor primero)");
    }
}

