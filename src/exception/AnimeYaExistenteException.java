package exception;

/**
 * Excepción lanzada cuando se intenta agregar un anime con un título que ya existe.
 * 
 * SOLID - SRP: Esta excepción solo representa el caso de anime duplicado.
 */
public class AnimeYaExistenteException extends AnimeException {
    
    private final String titulo;
    
    public AnimeYaExistenteException(String titulo) {
        super("Ya existe un anime con el título: " + titulo);
        this.titulo = titulo;
    }
    
    public String getTitulo() {
        return titulo;
    }
}

