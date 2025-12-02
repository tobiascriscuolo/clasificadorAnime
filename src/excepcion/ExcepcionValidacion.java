package excepcion;

/**
 * Excepción lanzada cuando los datos de entrada no cumplen las validaciones.
 */
public class ExcepcionValidacion extends ExcepcionAnime {
    
    private final String campo;
    
    public ExcepcionValidacion(String mensaje) {
        super(mensaje);
        this.campo = null;
    }
    
    public ExcepcionValidacion(String campo, String mensaje) {
        super("Error en campo '" + campo + "': " + mensaje);
        this.campo = campo;
    }
    
    public String obtenerCampo() {
        return campo;
    }
}

