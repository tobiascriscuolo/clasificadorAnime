package service;

import model.AnimeBase;
import model.Estado;
import model.Genero;
import repository.AnimeRepository;
import exception.PersistenciaException;

import java.util.*;

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
        
        int suma = 0;
        int cantidad = 0;
        
        for (AnimeBase anime : animes) {
            if (anime.tieneCalificacion()) {
                suma += anime.getCalificacion();
                cantidad++;
            }
        }
        
        if (cantidad == 0) {
            return 0.0;
        }
        
        return (double) suma / cantidad;
    }
    
    /**
     * Calcula el promedio de calificaciones por género.
     * 
     * @param genero género a analizar
     * @return promedio de calificaciones del género
     */
    public double getPromedioCalificacionPorGenero(Genero genero) throws PersistenciaException {
        List<AnimeBase> animes = animeRepository.findAll();
        
        int suma = 0;
        int cantidad = 0;
        
        for (AnimeBase anime : animes) {
            if (anime.perteneceAGenero(genero) && anime.tieneCalificacion()) {
                suma += anime.getCalificacion();
                cantidad++;
            }
        }
        
        if (cantidad == 0) {
            return 0.0;
        }
        
        return (double) suma / cantidad;
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
        
        // Inicializar contadores para todos los estados
        Map<Estado, Long> conteo = new EnumMap<>(Estado.class);
        for (Estado estado : Estado.values()) {
            conteo.put(estado, 0L);
        }
        
        // Contar
        for (AnimeBase anime : animes) {
            Estado estado = anime.getEstado();
            conteo.put(estado, conteo.get(estado) + 1);
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
                Long actual = conteoGeneros.get(genero);
                if (actual == null) {
                    conteoGeneros.put(genero, 1L);
                } else {
                    conteoGeneros.put(genero, actual + 1);
                }
            }
        }
        
        // Convertir a lista y ordenar por cantidad (mayor a menor)
        List<Map.Entry<Genero, Long>> listaOrdenada = new ArrayList<>(conteoGeneros.entrySet());
        Collections.sort(listaOrdenada, new Comparator<Map.Entry<Genero, Long>>() {
            @Override
            public int compare(Map.Entry<Genero, Long> e1, Map.Entry<Genero, Long> e2) {
                return e2.getValue().compareTo(e1.getValue()); // Orden descendente
            }
        });
        
        // Tomar los primeros 3
        List<Map.Entry<Genero, Long>> top3 = new ArrayList<>();
        for (int i = 0; i < Math.min(3, listaOrdenada.size()); i++) {
            top3.add(listaOrdenada.get(i));
        }
        
        return top3;
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
                Long actual = distribucion.get(genero);
                if (actual == null) {
                    distribucion.put(genero, 1L);
                } else {
                    distribucion.put(genero, actual + 1);
                }
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
        
        // Contar anime calificados
        int animesCalificados = 0;
        for (AnimeBase anime : animes) {
            if (anime.tieneCalificacion()) {
                animesCalificados++;
            }
        }
        
        double promedioGlobal = getPromedioCalificacionGlobal();
        Map<Estado, Long> porEstado = getCantidadPorEstado();
        List<Map.Entry<Genero, Long>> topGeneros = getTop3GenerosMasFrecuentes();
        
        // Encontrar anime más antiguo, más nuevo y mejor calificado
        AnimeBase masAntiguo = null;
        AnimeBase masNuevo = null;
        AnimeBase mejorCalificado = null;
        
        for (AnimeBase anime : animes) {
            // Más antiguo
            if (masAntiguo == null || anime.getAnioLanzamiento() < masAntiguo.getAnioLanzamiento()) {
                masAntiguo = anime;
            }
            
            // Más nuevo
            if (masNuevo == null || anime.getAnioLanzamiento() > masNuevo.getAnioLanzamiento()) {
                masNuevo = anime;
            }
            
            // Mejor calificado
            if (anime.tieneCalificacion()) {
                if (mejorCalificado == null || anime.getCalificacion() > mejorCalificado.getCalificacion()) {
                    mejorCalificado = anime;
                }
            }
        }
        
        return new ResumenEstadisticas(
            totalAnimes,
            animesCalificados,
            promedioGlobal,
            porEstado,
            topGeneros,
            masAntiguo,
            masNuevo,
            mejorCalificado
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
            for (Map.Entry<Estado, Long> entry : cantidadPorEstado.entrySet()) {
                sb.append(String.format("  %s: %d\n", entry.getKey().getDescripcion(), entry.getValue()));
            }
            
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
