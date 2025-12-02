package excepcion;

/**
 * Excepción lanzada cuando ocurre un error de persistencia (lectura/escritura de archivos).
 */
public class ExcepcionPersistencia extends ExcepcionAnime {
    
    public ExcepcionPersistencia(String mensaje) {
        super(mensaje);
    }
    
    public ExcepcionPersistencia(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

