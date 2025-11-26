package repository;

import model.ListaPersonalizada;
import exception.PersistenciaException;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de persistencia para listas personalizadas.
 * 
 * SOLID - DIP: Los servicios dependen de esta abstracción.
 * 
 * SOLID - ISP: Interfaz específica para operaciones de listas personalizadas.
 * 
 * GRASP - Pure Fabrication: Abstracción de persistencia que mejora el diseño.
 */
public interface ListaPersonalizadaRepository {
    
    /**
     * Guarda una lista personalizada.
     * Si ya existe (mismo nombre), la actualiza.
     * 
     * @param lista lista a guardar
     * @throws PersistenciaException si ocurre un error de I/O
     */
    void save(ListaPersonalizada lista) throws PersistenciaException;
    
    /**
     * Guarda todas las listas personalizadas.
     * 
     * @param listas listas a guardar
     * @throws PersistenciaException si ocurre un error de I/O
     */
    void saveAll(List<ListaPersonalizada> listas) throws PersistenciaException;
    
    /**
     * Busca una lista por su nombre (case-insensitive).
     * 
     * @param nombre nombre de la lista
     * @return Optional con la lista si existe
     * @throws PersistenciaException si ocurre un error de I/O
     */
    Optional<ListaPersonalizada> findByNombre(String nombre) throws PersistenciaException;
    
    /**
     * Obtiene todas las listas personalizadas.
     * 
     * @return lista de todas las listas personalizadas
     * @throws PersistenciaException si ocurre un error de I/O
     */
    List<ListaPersonalizada> findAll() throws PersistenciaException;
    
    /**
     * Elimina una lista por su nombre.
     * 
     * @param nombre nombre de la lista a eliminar
     * @return true si se eliminó, false si no existía
     * @throws PersistenciaException si ocurre un error de I/O
     */
    boolean deleteByNombre(String nombre) throws PersistenciaException;
    
    /**
     * Verifica si existe una lista con el nombre dado.
     * 
     * @param nombre nombre a verificar
     * @return true si existe, false si no
     * @throws PersistenciaException si ocurre un error de I/O
     */
    boolean existsByNombre(String nombre) throws PersistenciaException;
    
    /**
     * Obtiene la cantidad de listas personalizadas.
     * 
     * @return cantidad de listas
     * @throws PersistenciaException si ocurre un error de I/O
     */
    int count() throws PersistenciaException;
}

