package model;

/**
 * Enum que representa los tipos de anime (Serie o Película).
 * Utilizado para diferenciar subtipos en la jerarquía de herencia.
 */
public enum TipoAnime {
    SERIE("Serie"),
    PELICULA("Película");
    
    private final String descripcion;
    
    TipoAnime(String descripcion) {
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

