package modelo;

/**
 * Interfaz que define el comportamiento de entidades que pueden ser calificadas.
 */
public interface Calificable {
    
    /**
     * Obtiene la calificación actual.
     * @return calificación entre 1 y 5
     */
    int obtenerCalificacion();
    
    /**
     * Establece una nueva calificación.
     * @param calificacion valor entre 1 y 5
     * @throws IllegalArgumentException si la calificación no está en el rango válido
     */
    void establecerCalificacion(int calificacion);
    
    /**
     * Verifica si la entidad tiene una calificación asignada.
     * @return true si tiene calificación, false si no
     */
    boolean tieneCalificacion();
}

