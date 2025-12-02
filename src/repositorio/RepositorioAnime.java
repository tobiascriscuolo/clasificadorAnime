package repositorio;

import modelo.AnimeBase;
import excepcion.ExcepcionPersistencia;

import java.util.List;

/**
 * Interfaz que define las operaciones de persistencia para anime.
 */
public interface RepositorioAnime {
    
    /**
     * Guarda un anime en el repositorio.
     */
    void guardar(AnimeBase anime) throws ExcepcionPersistencia;
    
    /**
     * Guarda una colección de anime.
     */
    void guardarTodos(List<AnimeBase> animes) throws ExcepcionPersistencia;
    
    /**
     * Busca un anime por su título exacto (case-insensitive).
     */
    AnimeBase buscarPorTitulo(String titulo) throws ExcepcionPersistencia;
    
    /**
     * Obtiene todos los anime del repositorio.
     */
    List<AnimeBase> obtenerTodos() throws ExcepcionPersistencia;
    
    /**
     * Elimina un anime por su título.
     */
    boolean eliminarPorTitulo(String titulo) throws ExcepcionPersistencia;
    
    /**
     * Elimina un anime del repositorio.
     */
    boolean eliminar(AnimeBase anime) throws ExcepcionPersistencia;
    
    /**
     * Verifica si existe un anime con el título dado.
     */
    boolean existePorTitulo(String titulo) throws ExcepcionPersistencia;
    
    /**
     * Obtiene la cantidad total de anime en el repositorio.
     */
    int contar() throws ExcepcionPersistencia;
    
    /**
     * Elimina todos los anime del repositorio.
     */
    void eliminarTodos() throws ExcepcionPersistencia;
}

