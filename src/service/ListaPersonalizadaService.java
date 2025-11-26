package service;

import model.AnimeBase;
import model.ListaPersonalizada;
import repository.AnimeRepository;
import repository.ListaPersonalizadaRepository;
import exception.*;

import java.util.List;

/**
 * Servicio que coordina los casos de uso de listas personalizadas.
 * 
 * GRASP - Controller: Coordina operaciones de listas personalizadas.
 * 
 * GRASP - Low Coupling: Se comunica con repositorios a través de interfaces.
 * 
 * SOLID - SRP: Solo maneja lógica de listas personalizadas.
 * 
 * SOLID - DIP: Depende de abstracciones (interfaces de repositorio).
 */
public class ListaPersonalizadaService {
    
    private final ListaPersonalizadaRepository listaRepository;
    private final AnimeRepository animeRepository;
    
    /**
     * Constructor con inyección de dependencias.
     */
    public ListaPersonalizadaService(ListaPersonalizadaRepository listaRepository,
                                     AnimeRepository animeRepository) {
        this.listaRepository = listaRepository;
        this.animeRepository = animeRepository;
    }
    
    // ========== RF2: Listas Personalizadas ==========
    
    /**
     * Crea una nueva lista personalizada.
     * 
     * @param nombre nombre único de la lista
     * @param descripcion descripción opcional
     * @throws ValidacionException si el nombre es inválido
     * @throws AnimeYaExistenteException si ya existe una lista con ese nombre
     */
    public ListaPersonalizada crearLista(String nombre, String descripcion)
            throws ValidacionException, AnimeYaExistenteException, PersistenciaException {
        
        validarNombreLista(nombre);
        
        if (listaRepository.existsByNombre(nombre)) {
            throw new AnimeYaExistenteException("Ya existe una lista con el nombre: " + nombre);
        }
        
        ListaPersonalizada lista = new ListaPersonalizada(nombre.trim(), descripcion);
        listaRepository.save(lista);
        
        return lista;
    }
    
    /**
     * Crea una lista sin descripción.
     */
    public ListaPersonalizada crearLista(String nombre)
            throws ValidacionException, AnimeYaExistenteException, PersistenciaException {
        return crearLista(nombre, "");
    }
    
    /**
     * Agrega un anime a una lista personalizada.
     * Un mismo anime puede estar en múltiples listas.
     * 
     * @param nombreLista nombre de la lista
     * @param tituloAnime título del anime a agregar
     * @return true si se agregó, false si ya estaba en la lista
     */
    public boolean agregarAnimeALista(String nombreLista, String tituloAnime)
            throws ListaNoEncontradaException, AnimeNoEncontradoException, PersistenciaException {
        
        ListaPersonalizada lista = buscarListaPorNombre(nombreLista);
        AnimeBase anime = buscarAnimePorTitulo(tituloAnime);
        
        boolean agregado = lista.agregarAnime(anime);
        if (agregado) {
            listaRepository.save(lista);
        }
        
        return agregado;
    }
    
    /**
     * Remueve un anime de una lista personalizada.
     * 
     * @return true si se removió, false si no estaba en la lista
     */
    public boolean removerAnimeDeLista(String nombreLista, String tituloAnime)
            throws ListaNoEncontradaException, AnimeNoEncontradoException, PersistenciaException {
        
        ListaPersonalizada lista = buscarListaPorNombre(nombreLista);
        AnimeBase anime = buscarAnimePorTitulo(tituloAnime);
        
        boolean removido = lista.removerAnime(anime);
        if (removido) {
            listaRepository.save(lista);
        }
        
        return removido;
    }
    
    /**
     * Obtiene todas las listas personalizadas.
     */
    public List<ListaPersonalizada> listarTodas() throws PersistenciaException {
        return listaRepository.findAll();
    }
    
    /**
     * Busca una lista por nombre.
     * 
     * @throws ListaNoEncontradaException si no existe
     */
    public ListaPersonalizada buscarListaPorNombre(String nombre)
            throws ListaNoEncontradaException, PersistenciaException {
        
        return listaRepository.findByNombre(nombre)
            .orElseThrow(() -> new ListaNoEncontradaException(nombre));
    }
    
    /**
     * Elimina una lista personalizada.
     * 
     * @return true si se eliminó, false si no existía
     */
    public boolean eliminarLista(String nombre) throws PersistenciaException {
        return listaRepository.deleteByNombre(nombre);
    }
    
    /**
     * Actualiza el nombre y descripción de una lista.
     */
    public void actualizarLista(String nombreOriginal, String nuevoNombre, String nuevaDescripcion)
            throws ValidacionException, ListaNoEncontradaException, 
                   AnimeYaExistenteException, PersistenciaException {
        
        ListaPersonalizada lista = buscarListaPorNombre(nombreOriginal);
        
        // Si cambia el nombre, verificar que no exista
        if (!nombreOriginal.equalsIgnoreCase(nuevoNombre)) {
            validarNombreLista(nuevoNombre);
            if (listaRepository.existsByNombre(nuevoNombre)) {
                throw new AnimeYaExistenteException("Ya existe una lista con el nombre: " + nuevoNombre);
            }
            lista.setNombre(nuevoNombre.trim());
        }
        
        if (nuevaDescripcion != null) {
            lista.setDescripcion(nuevaDescripcion);
        }
        
        listaRepository.save(lista);
    }
    
    /**
     * Obtiene los anime de una lista específica.
     */
    public List<AnimeBase> obtenerAnimesDeListat(String nombreLista)
            throws ListaNoEncontradaException, PersistenciaException {
        
        ListaPersonalizada lista = buscarListaPorNombre(nombreLista);
        return lista.getAnimes();
    }
    
    /**
     * Verifica si un anime está en una lista.
     */
    public boolean animeEstaEnLista(String nombreLista, String tituloAnime)
            throws ListaNoEncontradaException, PersistenciaException {
        
        ListaPersonalizada lista = buscarListaPorNombre(nombreLista);
        return lista.contieneAnimePorTitulo(tituloAnime);
    }
    
    /**
     * Obtiene todas las listas que contienen un anime específico.
     */
    public List<ListaPersonalizada> obtenerListasConAnime(String tituloAnime)
            throws PersistenciaException {
        
        return listaRepository.findAll().stream()
            .filter(lista -> lista.contieneAnimePorTitulo(tituloAnime))
            .toList();
    }
    
    /**
     * Cuenta la cantidad de listas personalizadas.
     */
    public int contarListas() throws PersistenciaException {
        return listaRepository.count();
    }
    
    // ========== Métodos privados ==========
    
    private void validarNombreLista(String nombre) throws ValidacionException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidacionException("nombre", "El nombre de la lista no puede estar vacío");
        }
    }
    
    private AnimeBase buscarAnimePorTitulo(String titulo)
            throws AnimeNoEncontradoException, PersistenciaException {
        
        return animeRepository.findByTitulo(titulo)
            .orElseThrow(() -> new AnimeNoEncontradoException(titulo));
    }
}

