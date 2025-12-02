package excepcion;

/**
 * Excepción lanzada cuando se intenta agregar un anime con un título que ya existe.
 */
public class ExcepcionAnimeYaExistente extends ExcepcionAnime {
    
    private final String titulo;
    
    public ExcepcionAnimeYaExistente(String titulo) {
        super("Ya existe un anime con el título: " + titulo);
        this.titulo = titulo;
    }
    
    public String obtenerTitulo() {
        return titulo;
    }
}

