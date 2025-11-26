package model;

/**
 * Enum que representa los posibles estados de visualización de un anime.
 * 
 * GRASP - Information Expert: El enum conoce su propia descripción para mostrar en UI.
 */
public enum Estado {
    POR_VER("Por ver"),
    VIENDO("Viendo"),
    FINALIZADO("Finalizado"),
    ABANDONADO("Abandonado");
    
    private final String descripcion;
    
    Estado(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return descripcion;
    }
}

