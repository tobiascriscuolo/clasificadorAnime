package model;

/**
 * Enum que representa los géneros de anime disponibles.
 * 
 * GRASP - Information Expert: El enum conoce su propia descripción para mostrar en UI.
 */
public enum Genero {
    SHONEN("Shonen"),
    SHOJO("Shojo"),
    SEINEN("Seinen"),
    JOSEI("Josei"),
    MECHA("Mecha"),
    ISEKAI("Isekai"),
    SLICE_OF_LIFE("Slice of Life");
    
    private final String descripcion;
    
    Genero(String descripcion) {
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
