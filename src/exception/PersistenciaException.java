package exception;

/**
 * Excepción lanzada cuando ocurre un error de persistencia (lectura/escritura de archivos).
 * 
 * SOLID - SRP: Esta excepción solo representa errores de I/O y persistencia.
 */
public class PersistenciaException extends AnimeException {
    
    public PersistenciaException(String mensaje) {
        super(mensaje);
    }
    
    public PersistenciaException(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }
}

