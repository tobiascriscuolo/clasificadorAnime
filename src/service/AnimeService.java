package service;

import model.*;
import repository.AnimeRepository;
import exception.*;
import util.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Servicio que coordina los casos de uso relacionados con anime.
 * 
 * GRASP - Controller: Esta clase actúa como controlador de casos de uso,
 * recibiendo eventos del sistema (desde la UI) y coordinando las operaciones.
 * 
 * GRASP - Indirection: Actúa como intermediario entre la UI y el repositorio,
 * reduciendo el acoplamiento.
 * 
 * GRASP - High Cohesion: Todos los métodos están relacionados con la gestión de anime.
 * 
 * SOLID - SRP: Esta clase solo coordina operaciones de anime.
 * 
 * SOLID - DIP: Depende de la abstracción AnimeRepository, no de la implementación.
 */
public class AnimeService {
    
    private final AnimeRepository animeRepository;
    
    /**
     * Constructor con inyección de dependencia.
     * 
     * SOLID - DIP: El repositorio se inyecta como interfaz.
     * 
     * @param animeRepository repositorio de anime
     */
    public AnimeService(AnimeRepository animeRepository) {
        this.animeRepository = animeRepository;
    }
    
    // ========== RF1: Gestión de Anime ==========
    
    /**
     * Registra una nueva serie de anime en el catálogo.
     * 
     * GRASP - Controller: Coordina el caso de uso de registro.
     * GRASP - Creator: El servicio crea la instancia de AnimeSerie.
     * 
     * @throws ValidacionException si los datos no son válidos
     * @throws AnimeYaExistenteException si ya existe un anime con ese título
     * @throws PersistenciaException si hay error de I/O
     */
    public AnimeSerie registrarSerie(String titulo, int anioLanzamiento, String estudio,
                                     int cantidadCapitulos, Set<Genero> generos,
                                     boolean enEmision) 
            throws ValidacionException, AnimeYaExistenteException, PersistenciaException {
        
        // Validaciones
        validarTitulo(titulo);
        validarAnio(anioLanzamiento);
        validarCapitulos(cantidadCapitulos);
        validarGeneros(generos);
        
        // Verificar duplicado
        if (animeRepository.existsByTitulo(titulo)) {
            throw new AnimeYaExistenteException(titulo);
        }
        
        // Crear y persistir
        AnimeSerie serie = new AnimeSerie(titulo.trim(), anioLanzamiento, 
            estudio != null ? estudio.trim() : "", cantidadCapitulos, generos, enEmision);
        
        animeRepository.save(serie);
        
        return serie;
    }
    
    /**
     * Registra una nueva película de anime en el catálogo.
     */
    public AnimePelicula registrarPelicula(String titulo, int anioLanzamiento, String estudio,
                                           int duracionMinutos, Set<Genero> generos,
                                           String director)
            throws ValidacionException, AnimeYaExistenteException, PersistenciaException {
        
        validarTitulo(titulo);
        validarAnio(anioLanzamiento);
        validarDuracion(duracionMinutos);
        validarGeneros(generos);
        
        if (animeRepository.existsByTitulo(titulo)) {
            throw new AnimeYaExistenteException(titulo);
        }
        
        AnimePelicula pelicula = new AnimePelicula(titulo.trim(), anioLanzamiento,
            estudio != null ? estudio.trim() : "", duracionMinutos, generos,
            director != null ? director.trim() : "");
        
        animeRepository.save(pelicula);
        
        return pelicula;
    }
    
    /**
     * Actualiza los datos de un anime existente.
     * 
     * @throws AnimeNoEncontradoException si el anime no existe
     */
    public void actualizarAnime(String tituloOriginal, String nuevoTitulo, int anioLanzamiento,
                                String estudio, Estado estado, Integer calificacion,
                                Set<Genero> generos)
            throws ValidacionException, AnimeNoEncontradoException, 
                   AnimeYaExistenteException, PersistenciaException {
        
        AnimeBase anime = buscarPorTituloExacto(tituloOriginal);
        
        // Si cambia el título, verificar que no exista
        if (!tituloOriginal.equalsIgnoreCase(nuevoTitulo)) {
            validarTitulo(nuevoTitulo);
            if (animeRepository.existsByTitulo(nuevoTitulo)) {
                throw new AnimeYaExistenteException(nuevoTitulo);
            }
            anime.setTitulo(nuevoTitulo.trim());
        }
        
        validarAnio(anioLanzamiento);
        anime.setAnioLanzamiento(anioLanzamiento);
        
        if (estudio != null) {
            anime.setEstudio(estudio.trim());
        }
        
        if (estado != null) {
            anime.setEstado(estado);
        }
        
        if (calificacion != null) {
            validarCalificacion(calificacion);
            anime.setCalificacion(calificacion);
        }
        
        if (generos != null) {
            validarGeneros(generos);
            anime.setGeneros(generos);
        }
        
        animeRepository.save(anime);
    }
    
    /**
     * Actualiza solo la calificación de un anime.
     */
    public void calificarAnime(String titulo, int calificacion)
            throws ValidacionException, AnimeNoEncontradoException, PersistenciaException {
        
        validarCalificacion(calificacion);
        
        AnimeBase anime = buscarPorTituloExacto(titulo);
        anime.setCalificacion(calificacion);
        animeRepository.save(anime);
    }
    
    /**
     * Actualiza solo el estado de un anime.
     */
    public void cambiarEstado(String titulo, Estado nuevoEstado)
            throws AnimeNoEncontradoException, PersistenciaException {
        
        AnimeBase anime = buscarPorTituloExacto(titulo);
        anime.setEstado(nuevoEstado);
        animeRepository.save(anime);
    }
    
    /**
     * Elimina un anime del catálogo.
     */
    public boolean eliminarAnime(String titulo) throws PersistenciaException {
        return animeRepository.deleteByTitulo(titulo);
    }
    
    /**
     * Obtiene todos los anime del catálogo.
     */
    public List<AnimeBase> listarTodos() throws PersistenciaException {
        return animeRepository.findAll();
    }
    
    /**
     * Busca un anime por título exacto.
     * 
     * @throws AnimeNoEncontradoException si no existe
     */
    public AnimeBase buscarPorTituloExacto(String titulo) 
            throws AnimeNoEncontradoException, PersistenciaException {
        
        return animeRepository.findByTitulo(titulo)
            .orElseThrow(() -> new AnimeNoEncontradoException(titulo));
    }
    
    // ========== RF3: Búsqueda y Filtrado ==========
    
    /**
     * Busca anime por título (coincidencia parcial, case-insensitive).
     * 
     * GRASP - Information Expert: Delega al anime la verificación de su título.
     */
    public List<AnimeBase> buscarPorTitulo(String texto) throws PersistenciaException {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }
        
        return animeRepository.findAll().stream()
            .filter(a -> a.tituloContiene(texto.trim()))
            .collect(Collectors.toList());
    }
    
    /**
     * Busca anime por rango de años.
     */
    public List<AnimeBase> buscarPorRangoAnios(int desde, int hasta) throws PersistenciaException {
        return animeRepository.findAll().stream()
            .filter(a -> a.lanzadoEntre(desde, hasta))
            .collect(Collectors.toList());
    }
    
    /**
     * Filtra anime por género.
     */
    public List<AnimeBase> filtrarPorGenero(Genero genero) throws PersistenciaException {
        return animeRepository.findAll().stream()
            .filter(a -> a.perteneceAGenero(genero))
            .collect(Collectors.toList());
    }
    
    /**
     * Filtra anime por estado.
     */
    public List<AnimeBase> filtrarPorEstado(Estado estado) throws PersistenciaException {
        return animeRepository.findAll().stream()
            .filter(a -> a.getEstado() == estado)
            .collect(Collectors.toList());
    }
    
    /**
     * Filtra anime por calificación mínima.
     */
    public List<AnimeBase> filtrarPorCalificacionMinima(int minima) throws PersistenciaException {
        return animeRepository.findAll().stream()
            .filter(a -> a.cumpleCalificacionMinima(minima))
            .collect(Collectors.toList());
    }
    
    /**
     * Búsqueda avanzada con múltiples criterios combinados.
     * Usa el Builder FiltroAnime para construir el predicado.
     * 
     * GRASP - Indirection: Usa FiltroAnime como intermediario.
     */
    public List<AnimeBase> busquedaAvanzada(FiltroAnime filtro) throws PersistenciaException {
        if (filtro == null) {
            return listarTodos();
        }
        
        return animeRepository.findAll().stream()
            .filter(filtro.build())
            .collect(Collectors.toList());
    }
    
    /**
     * Búsqueda avanzada con predicado personalizado.
     */
    public List<AnimeBase> busquedaAvanzada(Predicate<AnimeBase> predicado) throws PersistenciaException {
        return animeRepository.findAll().stream()
            .filter(predicado)
            .collect(Collectors.toList());
    }
    
    // ========== RF4: Ordenamiento ==========
    
    /**
     * Ordena una lista de anime según un criterio.
     * 
     * GRASP - Polymorphism: El criterio de ordenamiento es intercambiable.
     * SOLID - OCP: Nuevos criterios se agregan sin modificar este método.
     */
    public List<AnimeBase> ordenar(List<AnimeBase> animes, CriterioOrdenamiento criterio) {
        List<AnimeBase> resultado = new ArrayList<>(animes);
        resultado.sort(criterio);
        return resultado;
    }
    
    /**
     * Lista todos los anime ordenados por título.
     */
    public List<AnimeBase> listarOrdenadosPorTitulo() throws PersistenciaException {
        return ordenar(listarTodos(), new OrdenamientoPorTitulo());
    }
    
    /**
     * Lista todos los anime ordenados por calificación (mejores primero).
     */
    public List<AnimeBase> listarOrdenadosPorCalificacion() throws PersistenciaException {
        return ordenar(listarTodos(), new OrdenamientoPorCalificacion());
    }
    
    /**
     * Lista todos los anime ordenados por año (recientes primero).
     */
    public List<AnimeBase> listarOrdenadosPorAnio() throws PersistenciaException {
        return ordenar(listarTodos(), new OrdenamientoPorAnio());
    }
    
    // ========== Validaciones privadas ==========
    
    private void validarTitulo(String titulo) throws ValidacionException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new ValidacionException("titulo", "El título no puede estar vacío");
        }
    }
    
    private void validarAnio(int anio) throws ValidacionException {
        int anioActual = java.time.Year.now().getValue();
        if (anio < AnimeBase.ANIO_MINIMO || anio > anioActual + 2) {
            throw new ValidacionException("anioLanzamiento", 
                "El año debe estar entre " + AnimeBase.ANIO_MINIMO + " y " + (anioActual + 2));
        }
    }
    
    private void validarCapitulos(int capitulos) throws ValidacionException {
        if (capitulos < AnimeBase.CAPITULOS_MINIMOS) {
            throw new ValidacionException("cantidadCapitulos", 
                "La cantidad de capítulos debe ser al menos " + AnimeBase.CAPITULOS_MINIMOS);
        }
    }
    
    private void validarDuracion(int duracion) throws ValidacionException {
        if (duracion < 1) {
            throw new ValidacionException("duracionMinutos", 
                "La duración debe ser al menos 1 minuto");
        }
    }
    
    private void validarCalificacion(int calificacion) throws ValidacionException {
        if (calificacion < AnimeBase.CALIFICACION_MINIMA || 
            calificacion > AnimeBase.CALIFICACION_MAXIMA) {
            throw new ValidacionException("calificacion", 
                "La calificación debe estar entre " + AnimeBase.CALIFICACION_MINIMA + 
                " y " + AnimeBase.CALIFICACION_MAXIMA);
        }
    }
    
    private void validarGeneros(Set<Genero> generos) throws ValidacionException {
        if (generos == null || generos.isEmpty()) {
            throw new ValidacionException("generos", 
                "Debe seleccionar al menos un género");
        }
    }
    
    // ========== Métodos auxiliares ==========
    
    /**
     * Obtiene la cantidad total de anime en el catálogo.
     */
    public int contarAnimes() throws PersistenciaException {
        return animeRepository.count();
    }
    
    /**
     * Verifica si existe un anime con el título dado.
     */
    public boolean existeAnime(String titulo) throws PersistenciaException {
        return animeRepository.existsByTitulo(titulo);
    }
}

