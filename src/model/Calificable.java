package model;

/**
 * Interfaz que define el comportamiento de entidades que pueden ser calificadas.
 * 
 * SOLID - ISP (Interface Segregation Principle): Interfaz pequeña y específica
 * que define solo el comportamiento de calificación.
 * 
 * GRASP - Polymorphism: Permite tratar diferentes entidades calificables de forma uniforme.
 */
public interface Calificable {
    
    /**
     * Obtiene la calificación actual.
     * @return calificación entre 1 y 5
     */
    int getCalificacion();
    
    /**
     * Establece una nueva calificación.
     * @param calificacion valor entre 1 y 5
     * @throws IllegalArgumentException si la calificación no está en el rango válido
     */
    void setCalificacion(int calificacion);
    
    /**
     * Verifica si la entidad tiene una calificación asignada.
     * @return true si tiene calificación, false si no
     */
    boolean tieneCalificacion();
}

