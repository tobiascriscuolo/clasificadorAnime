package servicio;

import modelo.AnimeBase;
import modelo.ListaPersonalizada;
import repositorio.RepositorioAnime;
import repositorio.RepositorioListaPersonalizada;
import excepcion.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que coordina los casos de uso de listas personalizadas.
 */
public class ServicioListaPersonalizada {
    
    private final RepositorioListaPersonalizada repositorioLista;
    private final RepositorioAnime repositorioAnime;
    
    public ServicioListaPersonalizada(RepositorioListaPersonalizada repositorioLista,
                                      RepositorioAnime repositorioAnime) {
        this.repositorioLista = repositorioLista;
        this.repositorioAnime = repositorioAnime;
    }
    
    public ListaPersonalizada crearLista(String nombre, String descripcion)
            throws ExcepcionValidacion, ExcepcionAnimeYaExistente, ExcepcionPersistencia {
        
        validarNombreLista(nombre);
        
        if (repositorioLista.existePorNombre(nombre)) {
            throw new ExcepcionAnimeYaExistente("Ya existe una lista con el nombre: " + nombre);
        }
        
        ListaPersonalizada lista = new ListaPersonalizada(nombre.trim(), descripcion);
        repositorioLista.guardar(lista);
        
        return lista;
    }
    
    public ListaPersonalizada crearLista(String nombre)
            throws ExcepcionValidacion, ExcepcionAnimeYaExistente, ExcepcionPersistencia {
        return crearLista(nombre, "");
    }
    
    public boolean agregarAnimeALista(String nombreLista, String tituloAnime)
            throws ExcepcionListaNoEncontrada, ExcepcionAnimeNoEncontrado, ExcepcionPersistencia {
        
        ListaPersonalizada lista = buscarListaPorNombre(nombreLista);
        AnimeBase anime = buscarAnimePorTitulo(tituloAnime);
        
        boolean agregado = lista.agregarAnime(anime);
        if (agregado) {
            repositorioLista.guardar(lista);
        }
        
        return agregado;
    }
    
    public boolean removerAnimeDeLista(String nombreLista, String tituloAnime)
            throws ExcepcionListaNoEncontrada, ExcepcionAnimeNoEncontrado, ExcepcionPersistencia {
        
        ListaPersonalizada lista = buscarListaPorNombre(nombreLista);
        AnimeBase anime = buscarAnimePorTitulo(tituloAnime);
        
        boolean removido = lista.removerAnime(anime);
        if (removido) {
            repositorioLista.guardar(lista);
        }
        
        return removido;
    }
    
    public List<ListaPersonalizada> listarTodas() throws ExcepcionPersistencia {
        return repositorioLista.obtenerTodas();
    }
    
    public ListaPersonalizada buscarListaPorNombre(String nombre)
            throws ExcepcionListaNoEncontrada, ExcepcionPersistencia {
        
        ListaPersonalizada lista = repositorioLista.buscarPorNombre(nombre);
        if (lista == null) {
            throw new ExcepcionListaNoEncontrada(nombre);
        }
        return lista;
    }
    
    public boolean eliminarLista(String nombre) throws ExcepcionPersistencia {
        return repositorioLista.eliminarPorNombre(nombre);
    }
    
    public void actualizarLista(String nombreOriginal, String nuevoNombre, String nuevaDescripcion)
            throws ExcepcionValidacion, ExcepcionListaNoEncontrada, 
                   ExcepcionAnimeYaExistente, ExcepcionPersistencia {
        
        ListaPersonalizada lista = buscarListaPorNombre(nombreOriginal);
        
        if (!nombreOriginal.equalsIgnoreCase(nuevoNombre)) {
            validarNombreLista(nuevoNombre);
            if (repositorioLista.existePorNombre(nuevoNombre)) {
                throw new ExcepcionAnimeYaExistente("Ya existe una lista con el nombre: " + nuevoNombre);
            }
            lista.establecerNombre(nuevoNombre.trim());
        }
        
        if (nuevaDescripcion != null) {
            lista.establecerDescripcion(nuevaDescripcion);
        }
        
        repositorioLista.guardar(lista);
    }
    
    public List<AnimeBase> obtenerAnimesDeListat(String nombreLista)
            throws ExcepcionListaNoEncontrada, ExcepcionPersistencia {
        
        ListaPersonalizada lista = buscarListaPorNombre(nombreLista);
        return lista.obtenerAnimes();
    }
    
    public boolean animeEstaEnLista(String nombreLista, String tituloAnime)
            throws ExcepcionListaNoEncontrada, ExcepcionPersistencia {
        
        ListaPersonalizada lista = buscarListaPorNombre(nombreLista);
        return lista.contieneAnimePorTitulo(tituloAnime);
    }
    
    public List<ListaPersonalizada> obtenerListasConAnime(String tituloAnime)
            throws ExcepcionPersistencia {
        
        List<ListaPersonalizada> todas = repositorioLista.obtenerTodas();
        List<ListaPersonalizada> resultado = new ArrayList<>();
        
        for (ListaPersonalizada lista : todas) {
            if (lista.contieneAnimePorTitulo(tituloAnime)) {
                resultado.add(lista);
            }
        }
        
        return resultado;
    }
    
    public int contarListas() throws ExcepcionPersistencia {
        return repositorioLista.contar();
    }
    
    // ========== Métodos privados ==========
    
    private void validarNombreLista(String nombre) throws ExcepcionValidacion {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ExcepcionValidacion("nombre", "El nombre de la lista no puede estar vacío");
        }
    }
    
    private AnimeBase buscarAnimePorTitulo(String titulo)
            throws ExcepcionAnimeNoEncontrado, ExcepcionPersistencia {
        
        AnimeBase anime = repositorioAnime.buscarPorTitulo(titulo);
        if (anime == null) {
            throw new ExcepcionAnimeNoEncontrado(titulo);
        }
        return anime;
    }
}

