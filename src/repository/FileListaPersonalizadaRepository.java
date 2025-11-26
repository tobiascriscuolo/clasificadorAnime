package repository;

import model.ListaPersonalizada;
import exception.PersistenciaException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de ListaPersonalizadaRepository que persiste en archivo binario.
 * 
 * SOLID - SRP: Esta clase solo maneja la persistencia de listas personalizadas.
 * 
 * SOLID - DIP: Implementa la interfaz ListaPersonalizadaRepository.
 * 
 * GRASP - Pure Fabrication: Clase técnica para persistencia.
 */
public class FileListaPersonalizadaRepository implements ListaPersonalizadaRepository {
    
    private final String filePath;
    private List<ListaPersonalizada> cache;
    private boolean cacheLoaded;
    
    public FileListaPersonalizadaRepository(String filePath) {
        this.filePath = filePath;
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }
    
    public FileListaPersonalizadaRepository() {
        this("data/listas.dat");
    }
    
    // ========== Implementación de ListaPersonalizadaRepository ==========
    
    @Override
    public void save(ListaPersonalizada lista) throws PersistenciaException {
        loadIfNeeded();
        
        Optional<ListaPersonalizada> existente = findInCache(lista.getNombre());
        if (existente.isPresent()) {
            int index = cache.indexOf(existente.get());
            cache.set(index, lista);
        } else {
            cache.add(lista);
        }
        
        persist();
    }
    
    @Override
    public void saveAll(List<ListaPersonalizada> listas) throws PersistenciaException {
        this.cache = new ArrayList<>(listas);
        this.cacheLoaded = true;
        persist();
    }
    
    @Override
    public Optional<ListaPersonalizada> findByNombre(String nombre) throws PersistenciaException {
        loadIfNeeded();
        return findInCache(nombre);
    }
    
    @Override
    public List<ListaPersonalizada> findAll() throws PersistenciaException {
        loadIfNeeded();
        return new ArrayList<>(cache);
    }
    
    @Override
    public boolean deleteByNombre(String nombre) throws PersistenciaException {
        loadIfNeeded();
        
        Optional<ListaPersonalizada> lista = findInCache(nombre);
        if (lista.isPresent()) {
            cache.remove(lista.get());
            persist();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean existsByNombre(String nombre) throws PersistenciaException {
        loadIfNeeded();
        return findInCache(nombre).isPresent();
    }
    
    @Override
    public int count() throws PersistenciaException {
        loadIfNeeded();
        return cache.size();
    }
    
    // ========== Métodos privados ==========
    
    private void loadIfNeeded() throws PersistenciaException {
        if (!cacheLoaded) {
            load();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void load() throws PersistenciaException {
        File file = new File(filePath);
        
        if (!file.exists()) {
            cache = new ArrayList<>();
            cacheLoaded = true;
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                cache = (List<ListaPersonalizada>) obj;
            } else {
                throw new PersistenciaException("Formato de archivo inválido");
            }
            cacheLoaded = true;
        } catch (FileNotFoundException e) {
            cache = new ArrayList<>();
            cacheLoaded = true;
        } catch (IOException e) {
            throw new PersistenciaException("Error al leer el archivo de listas: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new PersistenciaException("Error al deserializar: clase no encontrada", e);
        }
    }
    
    private void persist() throws PersistenciaException {
        File file = new File(filePath);
        
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new PersistenciaException("No se pudo crear el directorio: " + parentDir.getPath());
            }
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(cache);
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar el archivo de listas: " + e.getMessage(), e);
        }
    }
    
    private Optional<ListaPersonalizada> findInCache(String nombre) {
        return cache.stream()
            .filter(l -> l.getNombre().equalsIgnoreCase(nombre))
            .findFirst();
    }
    
    public void invalidateCache() {
        cacheLoaded = false;
        cache.clear();
    }
}

