package repositorio;

import modelo.ListaPersonalizada;
import excepcion.ExcepcionPersistencia;

import java.util.List;

/**
 * Interfaz que define las operaciones de persistencia para listas personalizadas.
 */
public interface RepositorioListaPersonalizada {
    
    /**
     * Guarda una lista personalizada.
     */
    void guardar(ListaPersonalizada lista) throws ExcepcionPersistencia;
    
    /**
     * Guarda todas las listas personalizadas.
     */
    void guardarTodas(List<ListaPersonalizada> listas) throws ExcepcionPersistencia;
    
    /**
     * Busca una lista por su nombre (case-insensitive).
     */
    ListaPersonalizada buscarPorNombre(String nombre) throws ExcepcionPersistencia;
    
    /**
     * Obtiene todas las listas personalizadas.
     */
    List<ListaPersonalizada> obtenerTodas() throws ExcepcionPersistencia;
    
    /**
     * Elimina una lista por su nombre.
     */
    boolean eliminarPorNombre(String nombre) throws ExcepcionPersistencia;
    
    /**
     * Verifica si existe una lista con el nombre dado.
     */
    boolean existePorNombre(String nombre) throws ExcepcionPersistencia;
    
    /**
     * Obtiene la cantidad de listas personalizadas.
     */
    int contar() throws ExcepcionPersistencia;
}

