package excepcion;

/**
 * Excepción base para todas las excepciones del dominio de Anime.
 */
public abstract class ExcepcionAnime extends Exception {
    
    public ExcepcionAnime(String mensaje) {
        super(mensaje);
    }
    
    public ExcepcionAnime(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

