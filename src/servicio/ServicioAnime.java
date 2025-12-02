package servicio;

import modelo.*;
import repositorio.RepositorioAnime;
import excepcion.*;
import utilidad.*;

import java.util.*;

/**
 * Servicio que coordina los casos de uso relacionados con anime.
 */
public class ServicioAnime {
    
    private final RepositorioAnime repositorioAnime;
    
    public ServicioAnime(RepositorioAnime repositorioAnime) {
        this.repositorioAnime = repositorioAnime;
    }
    
    // ========== Gestión de Anime ==========
    
    public AnimeSerie registrarSerie(String titulo, int anioLanzamiento, String estudio,
                                     int cantidadCapitulos, Set<Genero> generos,
                                     boolean enEmision) 
            throws ExcepcionValidacion, ExcepcionAnimeYaExistente, ExcepcionPersistencia {
        
        validarTitulo(titulo);
        validarAnio(anioLanzamiento);
        validarCapitulos(cantidadCapitulos);
        validarGeneros(generos);
        
        if (repositorioAnime.existePorTitulo(titulo)) {
            throw new ExcepcionAnimeYaExistente(titulo);
        }
        
        AnimeSerie serie = new AnimeSerie(titulo.trim(), anioLanzamiento, 
            estudio != null ? estudio.trim() : "", cantidadCapitulos, generos, enEmision);
        
        repositorioAnime.guardar(serie);
        
        return serie;
    }
    
    public AnimePelicula registrarPelicula(String titulo, int anioLanzamiento, String estudio,
                                           int duracionMinutos, Set<Genero> generos,
                                           String director)
            throws ExcepcionValidacion, ExcepcionAnimeYaExistente, ExcepcionPersistencia {
        
        validarTitulo(titulo);
        validarAnio(anioLanzamiento);
        validarDuracion(duracionMinutos);
        validarGeneros(generos);
        
        if (repositorioAnime.existePorTitulo(titulo)) {
            throw new ExcepcionAnimeYaExistente(titulo);
        }
        
        AnimePelicula pelicula = new AnimePelicula(titulo.trim(), anioLanzamiento,
            estudio != null ? estudio.trim() : "", duracionMinutos, generos,
            director != null ? director.trim() : "");
        
        repositorioAnime.guardar(pelicula);
        
        return pelicula;
    }
    
    public void actualizarAnime(String tituloOriginal, String nuevoTitulo, int anioLanzamiento,
                                String estudio, Estado estado, Integer calificacion,
                                Set<Genero> generos)
            throws ExcepcionValidacion, ExcepcionAnimeNoEncontrado, 
                   ExcepcionAnimeYaExistente, ExcepcionPersistencia {
        
        AnimeBase anime = buscarPorTituloExacto(tituloOriginal);
        
        if (!tituloOriginal.equalsIgnoreCase(nuevoTitulo)) {
            validarTitulo(nuevoTitulo);
            if (repositorioAnime.existePorTitulo(nuevoTitulo)) {
                throw new ExcepcionAnimeYaExistente(nuevoTitulo);
            }
            anime.establecerTitulo(nuevoTitulo.trim());
        }
        
        validarAnio(anioLanzamiento);
        anime.establecerAnioLanzamiento(anioLanzamiento);
        
        if (estudio != null) {
            anime.establecerEstudio(estudio.trim());
        }
        
        if (estado != null) {
            anime.establecerEstado(estado);
        }
        
        if (calificacion != null) {
            validarCalificacion(calificacion);
            anime.establecerCalificacion(calificacion);
        }
        
        if (generos != null) {
            validarGeneros(generos);
            anime.establecerGeneros(generos);
        }
        
        repositorioAnime.guardar(anime);
    }
    
    public void calificarAnime(String titulo, int calificacion)
            throws ExcepcionValidacion, ExcepcionAnimeNoEncontrado, ExcepcionPersistencia {
        
        validarCalificacion(calificacion);
        
        AnimeBase anime = buscarPorTituloExacto(titulo);
        anime.establecerCalificacion(calificacion);
        repositorioAnime.guardar(anime);
    }
    
    public void cambiarEstado(String titulo, Estado nuevoEstado)
            throws ExcepcionAnimeNoEncontrado, ExcepcionPersistencia {
        
        AnimeBase anime = buscarPorTituloExacto(titulo);
        anime.establecerEstado(nuevoEstado);
        repositorioAnime.guardar(anime);
    }
    
    public boolean eliminarAnime(String titulo) throws ExcepcionPersistencia {
        return repositorioAnime.eliminarPorTitulo(titulo);
    }
    
    public List<AnimeBase> listarTodos() throws ExcepcionPersistencia {
        return repositorioAnime.obtenerTodos();
    }
    
    public AnimeBase buscarPorTituloExacto(String titulo) 
            throws ExcepcionAnimeNoEncontrado, ExcepcionPersistencia {
        
        AnimeBase encontrado = repositorioAnime.buscarPorTitulo(titulo);
        if (encontrado == null) {
            throw new ExcepcionAnimeNoEncontrado(titulo);
        }
        return encontrado;
    }
    
    // ========== Búsqueda y Filtrado ==========
    
    public List<AnimeBase> buscarPorTitulo(String texto) throws ExcepcionPersistencia {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }
        
        List<AnimeBase> todos = repositorioAnime.obtenerTodos();
        List<AnimeBase> resultado = new ArrayList<>();
        
        for (AnimeBase anime : todos) {
            if (anime.tituloContiene(texto.trim())) {
                resultado.add(anime);
            }
        }
        
        return resultado;
    }
    
    public List<AnimeBase> buscarPorRangoAnios(int desde, int hasta) throws ExcepcionPersistencia {
        List<AnimeBase> todos = repositorioAnime.obtenerTodos();
        List<AnimeBase> resultado = new ArrayList<>();
        
        for (AnimeBase anime : todos) {
            if (anime.lanzadoEntre(desde, hasta)) {
                resultado.add(anime);
            }
        }
        
        return resultado;
    }
    
    public List<AnimeBase> filtrarPorGenero(Genero genero) throws ExcepcionPersistencia {
        List<AnimeBase> todos = repositorioAnime.obtenerTodos();
        List<AnimeBase> resultado = new ArrayList<>();
        
        for (AnimeBase anime : todos) {
            if (anime.perteneceAGenero(genero)) {
                resultado.add(anime);
            }
        }
        
        return resultado;
    }
    
    public List<AnimeBase> filtrarPorEstado(Estado estado) throws ExcepcionPersistencia {
        List<AnimeBase> todos = repositorioAnime.obtenerTodos();
        List<AnimeBase> resultado = new ArrayList<>();
        
        for (AnimeBase anime : todos) {
            if (anime.obtenerEstado() == estado) {
                resultado.add(anime);
            }
        }
        
        return resultado;
    }
    
    public List<AnimeBase> filtrarPorCalificacionMinima(int minima) throws ExcepcionPersistencia {
        List<AnimeBase> todos = repositorioAnime.obtenerTodos();
        List<AnimeBase> resultado = new ArrayList<>();
        
        for (AnimeBase anime : todos) {
            if (anime.cumpleCalificacionMinima(minima)) {
                resultado.add(anime);
            }
        }
        
        return resultado;
    }
    
    public List<AnimeBase> busquedaAvanzada(FiltroAnime filtro) throws ExcepcionPersistencia {
        if (filtro == null) {
            return listarTodos();
        }
        
        List<AnimeBase> todos = repositorioAnime.obtenerTodos();
        List<AnimeBase> resultado = new ArrayList<>();
        
        for (AnimeBase anime : todos) {
            if (filtro.cumpleFiltro(anime)) {
                resultado.add(anime);
            }
        }
        
        return resultado;
    }
    
    // ========== Ordenamiento ==========
    
    public List<AnimeBase> ordenar(List<AnimeBase> animes, CriterioOrdenamiento criterio) {
        List<AnimeBase> resultado = new ArrayList<>(animes);
        Collections.sort(resultado, criterio);
        return resultado;
    }
    
    public List<AnimeBase> listarOrdenadosPorTitulo() throws ExcepcionPersistencia {
        return ordenar(listarTodos(), new OrdenamientoPorTitulo());
    }
    
    public List<AnimeBase> listarOrdenadosPorCalificacion() throws ExcepcionPersistencia {
        return ordenar(listarTodos(), new OrdenamientoPorCalificacion());
    }
    
    public List<AnimeBase> listarOrdenadosPorAnio() throws ExcepcionPersistencia {
        return ordenar(listarTodos(), new OrdenamientoPorAnio());
    }
    
    // ========== Validaciones privadas ==========
    
    private void validarTitulo(String titulo) throws ExcepcionValidacion {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new ExcepcionValidacion("titulo", "El título no puede estar vacío");
        }
    }
    
    private void validarAnio(int anio) throws ExcepcionValidacion {
        int anioActual = java.time.Year.now().getValue();
        if (anio < AnimeBase.ANIO_MINIMO || anio > anioActual + 2) {
            throw new ExcepcionValidacion("anioLanzamiento", 
                "El año debe estar entre " + AnimeBase.ANIO_MINIMO + " y " + (anioActual + 2));
        }
    }
    
    private void validarCapitulos(int capitulos) throws ExcepcionValidacion {
        if (capitulos < AnimeBase.CAPITULOS_MINIMOS) {
            throw new ExcepcionValidacion("cantidadCapitulos", 
                "La cantidad de capítulos debe ser al menos " + AnimeBase.CAPITULOS_MINIMOS);
        }
    }
    
    private void validarDuracion(int duracion) throws ExcepcionValidacion {
        if (duracion < 1) {
            throw new ExcepcionValidacion("duracionMinutos", 
                "La duración debe ser al menos 1 minuto");
        }
    }
    
    private void validarCalificacion(int calificacion) throws ExcepcionValidacion {
        if (calificacion < AnimeBase.CALIFICACION_MINIMA || 
            calificacion > AnimeBase.CALIFICACION_MAXIMA) {
            throw new ExcepcionValidacion("calificacion", 
                "La calificación debe estar entre " + AnimeBase.CALIFICACION_MINIMA + 
                " y " + AnimeBase.CALIFICACION_MAXIMA);
        }
    }
    
    private void validarGeneros(Set<Genero> generos) throws ExcepcionValidacion {
        if (generos == null || generos.isEmpty()) {
            throw new ExcepcionValidacion("generos", 
                "Debe seleccionar al menos un género");
        }
    }
    
    // ========== Métodos auxiliares ==========
    
    public int contarAnimes() throws ExcepcionPersistencia {
        return repositorioAnime.contar();
    }
    
    public boolean existeAnime(String titulo) throws ExcepcionPersistencia {
        return repositorioAnime.existePorTitulo(titulo);
    }
    
    // ========== Exportación e Importación TXT ==========
    
    public String exportarATxt() throws ExcepcionPersistencia {
        List<AnimeBase> animes = listarTodos();
        StringBuilder sb = new StringBuilder();
        
        sb.append("# Catálogo de Anime - Exportado\n");
        sb.append("# Formato: TIPO|TITULO|AÑO|ESTUDIO|DURACION|GENEROS|ESTADO|CALIFICACION|EXTRA\n");
        sb.append("#\n");
        
        for (AnimeBase anime : animes) {
            sb.append(animeALinea(anime));
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    private String animeALinea(AnimeBase anime) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(anime.obtenerTipo().name()).append("|");
        sb.append(anime.obtenerTitulo()).append("|");
        sb.append(anime.obtenerAnioLanzamiento()).append("|");
        sb.append(anime.obtenerEstudio()).append("|");
        sb.append(anime.obtenerDuracion()).append("|");
        
        boolean primero = true;
        for (Genero g : anime.obtenerGeneros()) {
            if (!primero) sb.append(",");
            sb.append(g.name());
            primero = false;
        }
        sb.append("|");
        
        sb.append(anime.obtenerEstado().name()).append("|");
        sb.append(anime.tieneCalificacion() ? anime.obtenerCalificacion() : "0").append("|");
        
        if (anime instanceof AnimeSerie) {
            sb.append(((AnimeSerie) anime).estaEnEmision());
        } else if (anime instanceof AnimePelicula) {
            sb.append(((AnimePelicula) anime).obtenerDirector());
        }
        
        return sb.toString();
    }
    
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
                generos.add(Genero.SHONEN);
            }
            
            Estado estado = Estado.POR_VER;
            try {
                estado = Estado.valueOf(partes[6].trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Usar default
            }
            
            int calificacion = 0;
            try {
                calificacion = Integer.parseInt(partes[7].trim());
            } catch (NumberFormatException e) {
                // Sin calificación
            }
            
            String extra = partes.length > 8 ? partes[8].trim() : "";
            
            AnimeBase anime;
            if (tipo.equals("SERIE")) {
                boolean enEmision = extra.equalsIgnoreCase("true");
                anime = new AnimeSerie(titulo, anio, estudio, duracion, generos, enEmision);
            } else if (tipo.equals("PELICULA")) {
                anime = new AnimePelicula(titulo, anio, estudio, duracion, generos, extra);
            } else {
                return null;
            }
            
            anime.establecerEstado(estado);
            if (calificacion >= AnimeBase.CALIFICACION_MINIMA && calificacion <= AnimeBase.CALIFICACION_MAXIMA) {
                anime.establecerCalificacion(calificacion);
            }
            
            return anime;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean registrarAnimeDirecto(AnimeBase anime) throws ExcepcionPersistencia {
        if (repositorioAnime.existePorTitulo(anime.obtenerTitulo())) {
            return false;
        }
        repositorioAnime.guardar(anime);
        return true;
    }
}

