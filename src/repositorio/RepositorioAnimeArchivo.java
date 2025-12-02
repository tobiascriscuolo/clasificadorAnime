package repositorio;

import modelo.AnimeBase;
import excepcion.ExcepcionPersistencia;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de RepositorioAnime que persiste datos en archivo binario.
 */
public class RepositorioAnimeArchivo implements RepositorioAnime {
    
    private final String rutaArchivo;
    private List<AnimeBase> cache;
    private boolean cacheCargada;
    
    public RepositorioAnimeArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.cache = new ArrayList<>();
        this.cacheCargada = false;
    }
    
    public RepositorioAnimeArchivo() {
        this("data/animes.dat");
    }
    
    // ========== Implementación de RepositorioAnime ==========
    
    @Override
    public void guardar(AnimeBase anime) throws ExcepcionPersistencia {
        cargarSiNecesario();
        
        AnimeBase existente = buscarEnCache(anime.obtenerTitulo());
        if (existente != null) {
            int indice = cache.indexOf(existente);
            cache.set(indice, anime);
        } else {
            cache.add(anime);
        }
        
        persistir();
    }
    
    @Override
    public void guardarTodos(List<AnimeBase> animes) throws ExcepcionPersistencia {
        this.cache = new ArrayList<>(animes);
        this.cacheCargada = true;
        persistir();
    }
    
    @Override
    public AnimeBase buscarPorTitulo(String titulo) throws ExcepcionPersistencia {
        cargarSiNecesario();
        return buscarEnCache(titulo);
    }
    
    @Override
    public List<AnimeBase> obtenerTodos() throws ExcepcionPersistencia {
        cargarSiNecesario();
        return new ArrayList<>(cache);
    }
    
    @Override
    public boolean eliminarPorTitulo(String titulo) throws ExcepcionPersistencia {
        cargarSiNecesario();
        
        AnimeBase anime = buscarEnCache(titulo);
        if (anime != null) {
            cache.remove(anime);
            persistir();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean eliminar(AnimeBase anime) throws ExcepcionPersistencia {
        return eliminarPorTitulo(anime.obtenerTitulo());
    }
    
    @Override
    public boolean existePorTitulo(String titulo) throws ExcepcionPersistencia {
        cargarSiNecesario();
        return buscarEnCache(titulo) != null;
    }
    
    @Override
    public int contar() throws ExcepcionPersistencia {
        cargarSiNecesario();
        return cache.size();
    }
    
    @Override
    public void eliminarTodos() throws ExcepcionPersistencia {
        cache.clear();
        cacheCargada = true;
        persistir();
    }
    
    // ========== Métodos privados de persistencia ==========
    
    private void cargarSiNecesario() throws ExcepcionPersistencia {
        if (!cacheCargada) {
            cargar();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void cargar() throws ExcepcionPersistencia {
        File archivo = new File(rutaArchivo);
        
        if (!archivo.exists()) {
            cache = new ArrayList<>();
            cacheCargada = true;
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                cache = (List<AnimeBase>) obj;
            } else {
                throw new ExcepcionPersistencia("Formato de archivo inválido");
            }
            cacheCargada = true;
        } catch (FileNotFoundException e) {
            cache = new ArrayList<>();
            cacheCargada = true;
        } catch (IOException e) {
            throw new ExcepcionPersistencia("Error al leer el archivo de anime: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new ExcepcionPersistencia("Error al deserializar: clase no encontrada", e);
        }
    }
    
    private void persistir() throws ExcepcionPersistencia {
        File archivo = new File(rutaArchivo);
        
        File dirPadre = archivo.getParentFile();
        if (dirPadre != null && !dirPadre.exists()) {
            if (!dirPadre.mkdirs()) {
                throw new ExcepcionPersistencia("No se pudo crear el directorio: " + dirPadre.getPath());
            }
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(cache);
        } catch (IOException e) {
            throw new ExcepcionPersistencia("Error al guardar el archivo de anime: " + e.getMessage(), e);
        }
    }
    
    private AnimeBase buscarEnCache(String titulo) {
        for (AnimeBase anime : cache) {
            if (anime.obtenerTitulo().equalsIgnoreCase(titulo)) {
                return anime;
            }
        }
        return null;
    }
    
    public void invalidarCache() {
        cacheCargada = false;
        cache.clear();
    }
}

