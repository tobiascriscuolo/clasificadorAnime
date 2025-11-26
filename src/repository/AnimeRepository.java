package repository;

import model.AnimeBase;
import exception.PersistenciaException;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de persistencia para anime.
 * 
 * SOLID - DIP (Dependency Inversion Principle): Los servicios dependen de esta
 * abstracción, no de implementaciones concretas (ej: FileAnimeRepository).
 * 
 * SOLID - ISP: Interfaz específica para operaciones de anime.
 * 
 * GRASP - Pure Fabrication: Esta interfaz es una "invención" que no representa
 * un concepto del dominio, pero mejora la cohesión y reduce el acoplamiento.
 * 
 * GRASP - Protected Variations: Permite cambiar la implementación de persistencia
 * (archivo, base de datos, etc.) sin afectar los servicios.
 */
public interface AnimeRepository {
    
    /**
     * Guarda un anime en el repositorio.
     * Si ya existe (mismo título), lo actualiza.
     * 
     * @param anime anime a guardar
     * @throws PersistenciaException si ocurre un error de I/O
     */
    void save(AnimeBase anime) throws PersistenciaException;
    
    /**
     * Guarda una colección de anime.
     * 
     * @param animes lista de anime a guardar
     * @throws PersistenciaException si ocurre un error de I/O
     */
    void saveAll(List<AnimeBase> animes) throws PersistenciaException;
    
    /**
     * Busca un anime por su título exacto (case-insensitive).
     * 
     * @param titulo título a buscar
     * @return Optional con el anime si existe, vacío si no
     * @throws PersistenciaException si ocurre un error de I/O
     */
    Optional<AnimeBase> findByTitulo(String titulo) throws PersistenciaException;
    
    /**
     * Obtiene todos los anime del repositorio.
     * 
     * @return lista de todos los anime
     * @throws PersistenciaException si ocurre un error de I/O
     */
    List<AnimeBase> findAll() throws PersistenciaException;
    
    /**
     * Elimina un anime por su título.
     * 
     * @param titulo título del anime a eliminar
     * @return true si se eliminó, false si no existía
     * @throws PersistenciaException si ocurre un error de I/O
     */
    boolean deleteByTitulo(String titulo) throws PersistenciaException;
    
    /**
     * Elimina un anime del repositorio.
     * 
     * @param anime anime a eliminar
     * @return true si se eliminó, false si no existía
     * @throws PersistenciaException si ocurre un error de I/O
     */
    boolean delete(AnimeBase anime) throws PersistenciaException;
    
    /**
     * Verifica si existe un anime con el título dado.
     * 
     * @param titulo título a verificar
     * @return true si existe, false si no
     * @throws PersistenciaException si ocurre un error de I/O
     */
    boolean existsByTitulo(String titulo) throws PersistenciaException;
    
    /**
     * Obtiene la cantidad total de anime en el repositorio.
     * 
     * @return cantidad de anime
     * @throws PersistenciaException si ocurre un error de I/O
     */
    int count() throws PersistenciaException;
    
    /**
     * Elimina todos los anime del repositorio.
     * 
     * @throws PersistenciaException si ocurre un error de I/O
     */
    void deleteAll() throws PersistenciaException;
}

