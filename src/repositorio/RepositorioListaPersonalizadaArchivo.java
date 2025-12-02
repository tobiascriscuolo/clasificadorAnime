package repositorio;

import modelo.ListaPersonalizada;
import excepcion.ExcepcionPersistencia;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de RepositorioListaPersonalizada que persiste en archivo binario.
 */
public class RepositorioListaPersonalizadaArchivo implements RepositorioListaPersonalizada {
    
    private final String rutaArchivo;
    private List<ListaPersonalizada> cache;
    private boolean cacheCargada;
    
    public RepositorioListaPersonalizadaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.cache = new ArrayList<>();
        this.cacheCargada = false;
    }
    
    public RepositorioListaPersonalizadaArchivo() {
        this("data/listas.dat");
    }
    
    // ========== Implementación de RepositorioListaPersonalizada ==========
    
    @Override
    public void guardar(ListaPersonalizada lista) throws ExcepcionPersistencia {
        cargarSiNecesario();
        
        ListaPersonalizada existente = buscarEnCache(lista.obtenerNombre());
        if (existente != null) {
            int indice = cache.indexOf(existente);
            cache.set(indice, lista);
        } else {
            cache.add(lista);
        }
        
        persistir();
    }
    
    @Override
    public void guardarTodas(List<ListaPersonalizada> listas) throws ExcepcionPersistencia {
        this.cache = new ArrayList<>(listas);
        this.cacheCargada = true;
        persistir();
    }
    
    @Override
    public ListaPersonalizada buscarPorNombre(String nombre) throws ExcepcionPersistencia {
        cargarSiNecesario();
        return buscarEnCache(nombre);
    }
    
    @Override
    public List<ListaPersonalizada> obtenerTodas() throws ExcepcionPersistencia {
        cargarSiNecesario();
        return new ArrayList<>(cache);
    }
    
    @Override
    public boolean eliminarPorNombre(String nombre) throws ExcepcionPersistencia {
        cargarSiNecesario();
        
        ListaPersonalizada lista = buscarEnCache(nombre);
        if (lista != null) {
            cache.remove(lista);
            persistir();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean existePorNombre(String nombre) throws ExcepcionPersistencia {
        cargarSiNecesario();
        return buscarEnCache(nombre) != null;
    }
    
    @Override
    public int contar() throws ExcepcionPersistencia {
        cargarSiNecesario();
        return cache.size();
    }
    
    // ========== Métodos privados ==========
    
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
                cache = (List<ListaPersonalizada>) obj;
            } else {
                throw new ExcepcionPersistencia("Formato de archivo inválido");
            }
            cacheCargada = true;
        } catch (FileNotFoundException e) {
            cache = new ArrayList<>();
            cacheCargada = true;
        } catch (IOException e) {
            throw new ExcepcionPersistencia("Error al leer el archivo de listas: " + e.getMessage(), e);
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
            throw new ExcepcionPersistencia("Error al guardar el archivo de listas: " + e.getMessage(), e);
        }
    }
    
    private ListaPersonalizada buscarEnCache(String nombre) {
        for (ListaPersonalizada lista : cache) {
            if (lista.obtenerNombre().equalsIgnoreCase(nombre)) {
                return lista;
            }
        }
        return null;
    }
    
    public void invalidarCache() {
        cacheCargada = false;
        cache.clear();
    }
}

