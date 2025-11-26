package util;

import model.AnimeBase;

/**
 * Criterio de ordenamiento alfabético por título.
 * 
 * SOLID - SRP: Solo implementa el ordenamiento por título.
 * SOLID - OCP: Es una extensión del sistema de ordenamiento sin modificar otras clases.
 */
public class OrdenamientoPorTitulo implements CriterioOrdenamiento {
    
    private final boolean ascendente;
    
    public OrdenamientoPorTitulo() {
        this(true);
    }
    
    public OrdenamientoPorTitulo(boolean ascendente) {
        this.ascendente = ascendente;
    }
    
    @Override
    public int compare(AnimeBase a1, AnimeBase a2) {
        int resultado = a1.getTitulo().compareToIgnoreCase(a2.getTitulo());
        return ascendente ? resultado : -resultado;
    }
    
    @Override
    public String getDescripcion() {
        return "Por título " + (ascendente ? "(A-Z)" : "(Z-A)");
    }
}

