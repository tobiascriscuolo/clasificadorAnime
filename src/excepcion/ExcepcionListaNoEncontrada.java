package excepcion;

/**
 * Excepción lanzada cuando no se encuentra una lista personalizada.
 */
public class ExcepcionListaNoEncontrada extends ExcepcionAnime {
    
    private final String nombreLista;
    
    public ExcepcionListaNoEncontrada(String nombreLista) {
        super("No se encontró la lista: " + nombreLista);
        this.nombreLista = nombreLista;
    }
    
    public String obtenerNombreLista() {
        return nombreLista;
    }
}

