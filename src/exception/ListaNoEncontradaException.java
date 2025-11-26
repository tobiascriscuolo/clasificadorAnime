package exception;

/**
 * Excepción lanzada cuando no se encuentra una lista personalizada.
 * 
 * SOLID - SRP: Esta excepción solo representa el caso de lista no encontrada.
 */
public class ListaNoEncontradaException extends AnimeException {
    
    private final String nombreLista;
    
    public ListaNoEncontradaException(String nombreLista) {
        super("No se encontró la lista: " + nombreLista);
        this.nombreLista = nombreLista;
    }
    
    public String getNombreLista() {
        return nombreLista;
    }
}

