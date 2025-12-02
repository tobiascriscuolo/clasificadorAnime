package modelo;

/**
 * Enum que representa los géneros de anime disponibles.
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
    
    public String obtenerDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return descripcion;
    }
}

