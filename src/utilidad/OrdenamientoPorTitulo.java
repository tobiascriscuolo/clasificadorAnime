package utilidad;

import modelo.AnimeBase;

/**
 * Criterio de ordenamiento alfabético por título.
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
        int resultado = a1.obtenerTitulo().compareToIgnoreCase(a2.obtenerTitulo());
        return ascendente ? resultado : -resultado;
    }
    
    @Override
    public String obtenerDescripcion() {
        return "Por título " + (ascendente ? "(A-Z)" : "(Z-A)");
    }
}

