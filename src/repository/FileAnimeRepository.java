package repository;

import model.AnimeBase;
import exception.PersistenciaException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de AnimeRepository que persiste datos en archivo binario (serialización).
 * 
 * SOLID - SRP: Esta clase solo se encarga de la persistencia de anime en archivo.
 * 
 * SOLID - DIP: Implementa la interfaz AnimeRepository, permitiendo que los servicios
 * dependan de la abstracción.
 * 
 * GRASP - Pure Fabrication: Clase técnica que no representa un concepto del dominio
 * pero es necesaria para la persistencia.
 * 
 * Nota de diseño: Se usa serialización Java por simplicidad. En producción se podría
 * usar JSON, XML, o una base de datos real.
 */
public class FileAnimeRepository implements AnimeRepository {
    
    private final String filePath;
    private List<AnimeBase> cache;
    private boolean cacheLoaded;
    
    /**
     * Constructor con ruta de archivo personalizada.
     * 
     * @param filePath ruta del archivo de datos
     */
    public FileAnimeRepository(String filePath) {
        this.filePath = filePath;
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }
    
    /**
     * Constructor por defecto, usa ruta estándar.
     */
    public FileAnimeRepository() {
        this("data/animes.dat");
    }
    
    // ========== Implementación de AnimeRepository ==========
    
    @Override
    public void save(AnimeBase anime) throws PersistenciaException {
        loadIfNeeded();
        
        // Buscar si ya existe y actualizar, o agregar nuevo
        AnimeBase existente = findInCache(anime.getTitulo());
        if (existente != null) {
            int index = cache.indexOf(existente);
            cache.set(index, anime);
        } else {
            cache.add(anime);
        }
        
        persist();
    }
    
    @Override
    public void saveAll(List<AnimeBase> animes) throws PersistenciaException {
        this.cache = new ArrayList<>(animes);
        this.cacheLoaded = true;
        persist();
    }
    
    @Override
    public AnimeBase findByTitulo(String titulo) throws PersistenciaException {
        loadIfNeeded();
        return findInCache(titulo);
    }
    
    @Override
    public List<AnimeBase> findAll() throws PersistenciaException {
        loadIfNeeded();
        return new ArrayList<>(cache);
    }
    
    @Override
    public boolean deleteByTitulo(String titulo) throws PersistenciaException {
        loadIfNeeded();
        
        AnimeBase anime = findInCache(titulo);
        if (anime != null) {
            cache.remove(anime);
            persist();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean delete(AnimeBase anime) throws PersistenciaException {
        return deleteByTitulo(anime.getTitulo());
    }
    
    @Override
    public boolean existsByTitulo(String titulo) throws PersistenciaException {
        loadIfNeeded();
        return findInCache(titulo) != null;
    }
    
    @Override
    public int count() throws PersistenciaException {
        loadIfNeeded();
        return cache.size();
    }
    
    @Override
    public void deleteAll() throws PersistenciaException {
        cache.clear();
        cacheLoaded = true;
        persist();
    }
    
    // ========== Métodos privados de persistencia ==========
    
    /**
     * Carga los datos del archivo si no están en caché.
     */
    private void loadIfNeeded() throws PersistenciaException {
        if (!cacheLoaded) {
            load();
        }
    }
    
    /**
     * Carga los datos desde el archivo.
     */
    @SuppressWarnings("unchecked")
    private void load() throws PersistenciaException {
        File file = new File(filePath);
        
        if (!file.exists()) {
            // Archivo no existe, inicializar caché vacía
            cache = new ArrayList<>();
            cacheLoaded = true;
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                cache = (List<AnimeBase>) obj;
            } else {
                throw new PersistenciaException("Formato de archivo inválido");
            }
            cacheLoaded = true;
        } catch (FileNotFoundException e) {
            cache = new ArrayList<>();
            cacheLoaded = true;
        } catch (IOException e) {
            throw new PersistenciaException("Error al leer el archivo de anime: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new PersistenciaException("Error al deserializar: clase no encontrada", e);
        }
    }
    
    /**
     * Persiste los datos al archivo.
     */
    private void persist() throws PersistenciaException {
        File file = new File(filePath);
        
        // Crear directorio padre si no existe
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new PersistenciaException("No se pudo crear el directorio: " + parentDir.getPath());
            }
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(cache);
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar el archivo de anime: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca un anime en la caché por título (case-insensitive).
     * 
     * @param titulo título a buscar
     * @return el anime si existe, null si no
     */
    private AnimeBase findInCache(String titulo) {
        for (AnimeBase anime : cache) {
            if (anime.getTitulo().equalsIgnoreCase(titulo)) {
                return anime;
            }
        }
        return null;
    }
    
    /**
     * Invalida la caché, forzando recarga en próxima operación.
     * Útil para testing o sincronización externa.
     */
    public void invalidateCache() {
        cacheLoaded = false;
        cache.clear();
    }
}
