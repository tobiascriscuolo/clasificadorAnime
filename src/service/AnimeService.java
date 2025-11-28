package service;

import model.*;
import repository.AnimeRepository;
import exception.*;
import util.*;

import java.util.*;

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
        
        AnimeBase encontrado = animeRepository.findByTitulo(titulo);
        if (encontrado == null) {
            throw new AnimeNoEncontradoException(titulo);
        }
        return encontrado;
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
        
        List<AnimeBase> todos = animeRepository.findAll();
        List<AnimeBase> resultado = new ArrayList<>();
        
        for (AnimeBase anime : todos) {
            if (anime.tituloContiene(texto.trim())) {
                resultado.add(anime);
            }
        }
        
        return resultado;
    }
    
    /**
     * Busca anime por rango de años.
     */
    public List<AnimeBase> buscarPorRangoAnios(int desde, int hasta) throws PersistenciaException {
        List<AnimeBase> todos = animeRepository.findAll();
        List<AnimeBase> resultado = new ArrayList<>();
        
        for (AnimeBase anime : todos) {
            if (anime.lanzadoEntre(desde, hasta)) {
                resultado.add(anime);
            }
        }
        
        return resultado;
    }
    
    /**
     * Filtra anime por género.
     */
    public List<AnimeBase> filtrarPorGenero(Genero genero) throws PersistenciaException {
        List<AnimeBase> todos = animeRepository.findAll();
        List<AnimeBase> resultado = new ArrayList<>();
        
        for (AnimeBase anime : todos) {
            if (anime.perteneceAGenero(genero)) {
                resultado.add(anime);
            }
        }
        
        return resultado;
    }
    
    /**
     * Filtra anime por estado.
     */
    public List<AnimeBase> filtrarPorEstado(Estado estado) throws PersistenciaException {
        List<AnimeBase> todos = animeRepository.findAll();
        List<AnimeBase> resultado = new ArrayList<>();
        
        for (AnimeBase anime : todos) {
            if (anime.getEstado() == estado) {
                resultado.add(anime);
            }
        }
        
        return resultado;
    }
    
    /**
     * Filtra anime por calificación mínima.
     */
    public List<AnimeBase> filtrarPorCalificacionMinima(int minima) throws PersistenciaException {
        List<AnimeBase> todos = animeRepository.findAll();
        List<AnimeBase> resultado = new ArrayList<>();
        
        for (AnimeBase anime : todos) {
            if (anime.cumpleCalificacionMinima(minima)) {
                resultado.add(anime);
            }
        }
        
        return resultado;
    }
    
    /**
     * Búsqueda avanzada con múltiples criterios combinados.
     * Usa el Builder FiltroAnime para construir los criterios.
     * 
     * GRASP - Indirection: Usa FiltroAnime como intermediario.
     */
    public List<AnimeBase> busquedaAvanzada(FiltroAnime filtro) throws PersistenciaException {
        if (filtro == null) {
            return listarTodos();
        }
        
        List<AnimeBase> todos = animeRepository.findAll();
        List<AnimeBase> resultado = new ArrayList<>();
        
        for (AnimeBase anime : todos) {
            if (filtro.cumpleFiltro(anime)) {
                resultado.add(anime);
            }
        }
        
        return resultado;
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
        Collections.sort(resultado, criterio);
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
    
    // ========== Exportación e Importación TXT ==========
    
    /**
     * Exporta todo el catálogo a formato de texto.
     * Formato: TIPO|TITULO|AÑO|ESTUDIO|DURACION|GENEROS|ESTADO|CALIFICACION|EXTRA
     * 
     * @return String con todo el catálogo en formato legible
     */
    public String exportarATxt() throws PersistenciaException {
        List<AnimeBase> animes = listarTodos();
        StringBuilder sb = new StringBuilder();
        
        // Cabecera
        sb.append("# Catálogo de Anime - Exportado\n");
        sb.append("# Formato: TIPO|TITULO|AÑO|ESTUDIO|DURACION|GENEROS|ESTADO|CALIFICACION|EXTRA\n");
        sb.append("# EXTRA para series: EN_EMISION (true/false)\n");
        sb.append("# EXTRA para películas: DIRECTOR\n");
        sb.append("#\n");
        
        for (AnimeBase anime : animes) {
            sb.append(animeALinea(anime));
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Convierte un anime a una línea de texto.
     */
    private String animeALinea(AnimeBase anime) {
        StringBuilder sb = new StringBuilder();
        
        // Tipo
        sb.append(anime.getTipo().name());
        sb.append("|");
        
        // Título
        sb.append(anime.getTitulo());
        sb.append("|");
        
        // Año
        sb.append(anime.getAnioLanzamiento());
        sb.append("|");
        
        // Estudio
        sb.append(anime.getEstudio());
        sb.append("|");
        
        // Duración
        sb.append(anime.getDuracion());
        sb.append("|");
        
        // Géneros
        boolean primero = true;
        for (Genero g : anime.getGeneros()) {
            if (!primero) sb.append(",");
            sb.append(g.name());
            primero = false;
        }
        sb.append("|");
        
        // Estado
        sb.append(anime.getEstado().name());
        sb.append("|");
        
        // Calificación
        sb.append(anime.tieneCalificacion() ? anime.getCalificacion() : "0");
        sb.append("|");
        
        // Extra (según tipo)
        if (anime instanceof AnimeSerie) {
            sb.append(((AnimeSerie) anime).isEnEmision());
        } else if (anime instanceof AnimePelicula) {
            sb.append(((AnimePelicula) anime).getDirector());
        }
        
        return sb.toString();
    }
    
    /**
     * Parsea una línea de texto a un anime.
     * 
     * @param linea línea en formato TIPO|TITULO|AÑO|ESTUDIO|DURACION|GENEROS|ESTADO|CALIFICACION|EXTRA
     * @return el anime parseado, o null si la línea es inválida o es comentario
     */
    public AnimeBase parsearLineaAnime(String linea) {
        if (linea == null || linea.trim().isEmpty() || linea.trim().startsWith("#")) {
            return null;
        }
        
        String[] partes = linea.split("\\|");
        if (partes.length < 8) {
            return null;
        }
        
        try {
            String tipo = partes[0].trim().toUpperCase();
            String titulo = partes[1].trim();
            int anio = Integer.parseInt(partes[2].trim());
            String estudio = partes[3].trim();
            int duracion = Integer.parseInt(partes[4].trim());
            
            // Parsear géneros
            Set<Genero> generos = new HashSet<>();
            String[] generosStr = partes[5].split(",");
            for (String g : generosStr) {
                try {
                    generos.add(Genero.valueOf(g.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    // Ignorar género inválido
                }
            }
            if (generos.isEmpty()) {
                generos.add(Genero.SHONEN); // Default
            }
            
            // Parsear estado
            Estado estado = Estado.POR_VER;
            try {
                estado = Estado.valueOf(partes[6].trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Usar default
            }
            
            // Parsear calificación
            int calificacion = 0;
            try {
                calificacion = Integer.parseInt(partes[7].trim());
            } catch (NumberFormatException e) {
                // Sin calificación
            }
            
            // Extra
            String extra = partes.length > 8 ? partes[8].trim() : "";
            
            // Crear anime según tipo
            AnimeBase anime;
            if (tipo.equals("SERIE")) {
                boolean enEmision = extra.equalsIgnoreCase("true");
                anime = new AnimeSerie(titulo, anio, estudio, duracion, generos, enEmision);
            } else if (tipo.equals("PELICULA")) {
                anime = new AnimePelicula(titulo, anio, estudio, duracion, generos, extra);
            } else {
                return null;
            }
            
            // Establecer estado y calificación
            anime.setEstado(estado);
            if (calificacion >= AnimeBase.CALIFICACION_MINIMA && calificacion <= AnimeBase.CALIFICACION_MAXIMA) {
                anime.setCalificacion(calificacion);
            }
            
            return anime;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Registra un anime directamente (usado para importación).
     * 
     * @return true si se registró, false si ya existía
     */
    public boolean registrarAnimeDirecto(AnimeBase anime) throws PersistenciaException {
        if (animeRepository.existsByTitulo(anime.getTitulo())) {
            return false;
        }
        animeRepository.save(anime);
        return true;
    }
}
