package service;

import model.AnimeBase;
import model.Estado;
import model.Genero;
import repository.AnimeRepository;
import exception.PersistenciaException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que proporciona estadísticas sobre el catálogo de anime.
 * 
 * GRASP - Controller: Coordina las operaciones de estadísticas.
 * 
 * GRASP - Information Expert: Los cálculos estadísticos se realizan aquí
 * porque este servicio tiene acceso a todos los datos necesarios.
 * 
 * SOLID - SRP: Solo se encarga de calcular estadísticas.
 */
public class EstadisticasService {
    
    private final AnimeRepository animeRepository;
    
    public EstadisticasService(AnimeRepository animeRepository) {
        this.animeRepository = animeRepository;
    }
    
    // ========== RF5: Estadísticas ==========
    
    /**
     * Calcula el promedio de calificaciones global.
     * Solo considera anime con calificación asignada.
     * 
     * @return promedio de calificaciones, o 0.0 si no hay calificaciones
     */
    public double getPromedioCalificacionGlobal() throws PersistenciaException {
        List<AnimeBase> animes = animeRepository.findAll();
        
        return animes.stream()
            .filter(AnimeBase::tieneCalificacion)
            .mapToInt(AnimeBase::getCalificacion)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Calcula el promedio de calificaciones por género.
     * 
     * @param genero género a analizar
     * @return promedio de calificaciones del género
     */
    public double getPromedioCalificacionPorGenero(Genero genero) throws PersistenciaException {
        List<AnimeBase> animes = animeRepository.findAll();
        
        return animes.stream()
            .filter(a -> a.perteneceAGenero(genero))
            .filter(AnimeBase::tieneCalificacion)
            .mapToInt(AnimeBase::getCalificacion)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Calcula el promedio de calificaciones para todos los géneros.
     * 
     * @return mapa de género a promedio de calificación
     */
    public Map<Genero, Double> getPromediosCalificacionPorGenero() throws PersistenciaException {
        Map<Genero, Double> promedios = new EnumMap<>(Genero.class);
        
        for (Genero genero : Genero.values()) {
            double promedio = getPromedioCalificacionPorGenero(genero);
            if (promedio > 0) {
                promedios.put(genero, promedio);
            }
        }
        
        return promedios;
    }
    
    /**
     * Cuenta la cantidad de anime por estado.
     * 
     * @return mapa de estado a cantidad
     */
    public Map<Estado, Long> getCantidadPorEstado() throws PersistenciaException {
        List<AnimeBase> animes = animeRepository.findAll();
        
        Map<Estado, Long> conteo = animes.stream()
            .collect(Collectors.groupingBy(AnimeBase::getEstado, Collectors.counting()));
        
        // Asegurar que todos los estados estén presentes
        for (Estado estado : Estado.values()) {
            conteo.putIfAbsent(estado, 0L);
        }
        
        return conteo;
    }
    
    /**
     * Obtiene el Top 3 géneros más frecuentes en el catálogo.
     * 
     * @return lista de los 3 géneros más frecuentes con su cantidad
     */
    public List<Map.Entry<Genero, Long>> getTop3GenerosMasFrecuentes() throws PersistenciaException {
        List<AnimeBase> animes = animeRepository.findAll();
        
        // Contar ocurrencias de cada género
        Map<Genero, Long> conteoGeneros = new EnumMap<>(Genero.class);
        
        for (AnimeBase anime : animes) {
            for (Genero genero : anime.getGeneros()) {
                conteoGeneros.merge(genero, 1L, Long::sum);
            }
        }
        
        // Ordenar por frecuencia y tomar top 3
        return conteoGeneros.entrySet().stream()
            .sorted(Map.Entry.<Genero, Long>comparingByValue().reversed())
            .limit(3)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene la distribución de géneros completa.
     * 
     * @return mapa de género a cantidad de anime
     */
    public Map<Genero, Long> getDistribucionGeneros() throws PersistenciaException {
        List<AnimeBase> animes = animeRepository.findAll();
        
        Map<Genero, Long> distribucion = new EnumMap<>(Genero.class);
        
        for (AnimeBase anime : animes) {
            for (Genero genero : anime.getGeneros()) {
                distribucion.merge(genero, 1L, Long::sum);
            }
        }
        
        return distribucion;
    }
    
    /**
     * Obtiene estadísticas generales del catálogo.
     * 
     * @return objeto con todas las estadísticas básicas
     */
    public ResumenEstadisticas getResumenEstadisticas() throws PersistenciaException {
        List<AnimeBase> animes = animeRepository.findAll();
        
        int totalAnimes = animes.size();
        long animesCalificados = animes.stream().filter(AnimeBase::tieneCalificacion).count();
        double promedioGlobal = getPromedioCalificacionGlobal();
        Map<Estado, Long> porEstado = getCantidadPorEstado();
        List<Map.Entry<Genero, Long>> topGeneros = getTop3GenerosMasFrecuentes();
        
        // Anime más antiguo y más nuevo
        Optional<AnimeBase> masAntiguo = animes.stream()
            .min(Comparator.comparingInt(AnimeBase::getAnioLanzamiento));
        Optional<AnimeBase> masNuevo = animes.stream()
            .max(Comparator.comparingInt(AnimeBase::getAnioLanzamiento));
        
        // Anime mejor calificado
        Optional<AnimeBase> mejorCalificado = animes.stream()
            .filter(AnimeBase::tieneCalificacion)
            .max(Comparator.comparingInt(AnimeBase::getCalificacion));
        
        return new ResumenEstadisticas(
            totalAnimes,
            (int) animesCalificados,
            promedioGlobal,
            porEstado,
            topGeneros,
            masAntiguo.orElse(null),
            masNuevo.orElse(null),
            mejorCalificado.orElse(null)
        );
    }
    
    /**
     * Clase interna que encapsula el resumen de estadísticas.
     * 
     * GRASP - Information Expert: Esta clase agrupa datos relacionados.
     */
    public static class ResumenEstadisticas {
        private final int totalAnimes;
        private final int animesCalificados;
        private final double promedioCalificacion;
        private final Map<Estado, Long> cantidadPorEstado;
        private final List<Map.Entry<Genero, Long>> topGeneros;
        private final AnimeBase animeMasAntiguo;
        private final AnimeBase animeMasNuevo;
        private final AnimeBase animeMejorCalificado;
        
        public ResumenEstadisticas(int totalAnimes, int animesCalificados,
                                   double promedioCalificacion, Map<Estado, Long> cantidadPorEstado,
                                   List<Map.Entry<Genero, Long>> topGeneros,
                                   AnimeBase animeMasAntiguo, AnimeBase animeMasNuevo,
                                   AnimeBase animeMejorCalificado) {
            this.totalAnimes = totalAnimes;
            this.animesCalificados = animesCalificados;
            this.promedioCalificacion = promedioCalificacion;
            this.cantidadPorEstado = cantidadPorEstado;
            this.topGeneros = topGeneros;
            this.animeMasAntiguo = animeMasAntiguo;
            this.animeMasNuevo = animeMasNuevo;
            this.animeMejorCalificado = animeMejorCalificado;
        }
        
        // Getters
        public int getTotalAnimes() { return totalAnimes; }
        public int getAnimesCalificados() { return animesCalificados; }
        public double getPromedioCalificacion() { return promedioCalificacion; }
        public Map<Estado, Long> getCantidadPorEstado() { return cantidadPorEstado; }
        public List<Map.Entry<Genero, Long>> getTopGeneros() { return topGeneros; }
        public AnimeBase getAnimeMasAntiguo() { return animeMasAntiguo; }
        public AnimeBase getAnimeMasNuevo() { return animeMasNuevo; }
        public AnimeBase getAnimeMejorCalificado() { return animeMejorCalificado; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Resumen del Catálogo ===\n");
            sb.append(String.format("Total de anime: %d\n", totalAnimes));
            sb.append(String.format("Anime calificados: %d\n", animesCalificados));
            sb.append(String.format("Promedio de calificación: %.2f\n", promedioCalificacion));
            
            sb.append("\nPor estado:\n");
            cantidadPorEstado.forEach((estado, cantidad) -> 
                sb.append(String.format("  %s: %d\n", estado.getDescripcion(), cantidad)));
            
            sb.append("\nTop 3 géneros:\n");
            for (int i = 0; i < topGeneros.size(); i++) {
                Map.Entry<Genero, Long> entry = topGeneros.get(i);
                sb.append(String.format("  %d. %s (%d anime)\n", 
                    i + 1, entry.getKey().getDescripcion(), entry.getValue()));
            }
            
            if (animeMejorCalificado != null) {
                sb.append(String.format("\nMejor calificado: %s (★%d)\n", 
                    animeMejorCalificado.getTitulo(), animeMejorCalificado.getCalificacion()));
            }
            
            return sb.toString();
        }
    }
}

