package excepcion;

/**
 * Excepción lanzada cuando no se encuentra un anime buscado.
 */
public class ExcepcionAnimeNoEncontrado extends ExcepcionAnime {
    
    private final String titulo;
    
    public ExcepcionAnimeNoEncontrado(String titulo) {
        super("No se encontró el anime: " + titulo);
        this.titulo = titulo;
    }
    
    public String obtenerTitulo() {
        return titulo;
    }
}

