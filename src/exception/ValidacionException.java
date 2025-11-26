package exception;

/**
 * Excepción lanzada cuando los datos de entrada no cumplen las validaciones.
 * 
 * SOLID - SRP: Esta excepción solo representa errores de validación de datos.
 */
public class ValidacionException extends AnimeException {
    
    private final String campo;
    
    public ValidacionException(String mensaje) {
        super(mensaje);
        this.campo = null;
    }
    
    public ValidacionException(String campo, String mensaje) {
        super("Error en campo '" + campo + "': " + mensaje);
        this.campo = campo;
    }
    
    public String getCampo() {
        return campo;
    }
}

