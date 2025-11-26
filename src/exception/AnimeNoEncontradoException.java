package exception;

/**
 * Excepción lanzada cuando no se encuentra un anime buscado.
 * 
 * SOLID - SRP: Esta excepción solo representa el caso de anime no encontrado.
 */
public class AnimeNoEncontradoException extends AnimeException {
    
    private final String titulo;
    
    public AnimeNoEncontradoException(String titulo) {
        super("No se encontró el anime: " + titulo);
        this.titulo = titulo;
    }
    
    public String getTitulo() {
        return titulo;
    }
}

