package util;

import model.AnimeBase;

/**
 * Criterio de ordenamiento por año de lanzamiento.
 * 
 * SOLID - SRP: Solo implementa el ordenamiento por año.
 * SOLID - OCP: Extensión del sistema de ordenamiento.
 */
public class OrdenamientoPorAnio implements CriterioOrdenamiento {
    
    private final boolean descendente;
    
    public OrdenamientoPorAnio() {
        this(true); // Por defecto, más recientes primero
    }
    
    public OrdenamientoPorAnio(boolean descendente) {
        this.descendente = descendente;
    }
    
    @Override
    public int compare(AnimeBase a1, AnimeBase a2) {
        int resultado = Integer.compare(a1.getAnioLanzamiento(), a2.getAnioLanzamiento());
        return descendente ? -resultado : resultado;
    }
    
    @Override
    public String getDescripcion() {
        return "Por año " + (descendente ? "(recientes primero)" : "(antiguos primero)");
    }
}

