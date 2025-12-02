package modelo;

/**
 * Enum que representa los tipos de anime (Serie o Película).
 */
public enum TipoAnime {
    SERIE("Serie"),
    PELICULA("Película");
    
    private final String descripcion;
    
    TipoAnime(String descripcion) {
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

