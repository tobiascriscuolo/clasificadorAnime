package exception;

/**
 * Excepción base para todas las excepciones del dominio de Anime.
 * 
 * SOLID - SRP: Esta clase solo define la jerarquía base de excepciones del dominio.
 * GRASP - Protected Variations: Usar una jerarquía de excepciones permite
 * manejar errores de forma uniforme y extensible.
 */
public abstract class AnimeException extends Exception {
    
    public AnimeException(String message) {
        super(message);
    }
    
    public AnimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

