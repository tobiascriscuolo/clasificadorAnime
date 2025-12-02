package servicio;

import modelo.AnimeBase;
import modelo.Estado;
import modelo.Genero;
import repositorio.RepositorioAnime;
import excepcion.ExcepcionPersistencia;

import java.util.*;

/**
 * Servicio que proporciona estadísticas sobre el catálogo de anime.
 */
public class ServicioEstadisticas {
    
    private final RepositorioAnime repositorioAnime;
    
    public ServicioEstadisticas(RepositorioAnime repositorioAnime) {
        this.repositorioAnime = repositorioAnime;
    }
    
    public double obtenerPromedioCalificacionGlobal() throws ExcepcionPersistencia {
        List<AnimeBase> animes = repositorioAnime.obtenerTodos();
        
        int suma = 0;
        int cantidad = 0;
        
        for (AnimeBase anime : animes) {
            if (anime.tieneCalificacion()) {
                suma += anime.obtenerCalificacion();
                cantidad++;
            }
        }
        
        if (cantidad == 0) {
            return 0.0;
        }
        
        return (double) suma / cantidad;
    }
    
    public double obtenerPromedioCalificacionPorGenero(Genero genero) throws ExcepcionPersistencia {
        List<AnimeBase> animes = repositorioAnime.obtenerTodos();
        
        int suma = 0;
        int cantidad = 0;
        
        for (AnimeBase anime : animes) {
            if (anime.perteneceAGenero(genero) && anime.tieneCalificacion()) {
                suma += anime.obtenerCalificacion();
                cantidad++;
            }
        }
        
        if (cantidad == 0) {
            return 0.0;
        }
        
        return (double) suma / cantidad;
    }
    
    public Map<Genero, Double> obtenerPromediosCalificacionPorGenero() throws ExcepcionPersistencia {
        Map<Genero, Double> promedios = new EnumMap<>(Genero.class);
        
        for (Genero genero : Genero.values()) {
            double promedio = obtenerPromedioCalificacionPorGenero(genero);
            if (promedio > 0) {
                promedios.put(genero, promedio);
            }
        }
        
        return promedios;
    }
    
    public Map<Estado, Long> obtenerCantidadPorEstado() throws ExcepcionPersistencia {
        List<AnimeBase> animes = repositorioAnime.obtenerTodos();
        
        Map<Estado, Long> conteo = new EnumMap<>(Estado.class);
        for (Estado estado : Estado.values()) {
            conteo.put(estado, 0L);
        }
        
        for (AnimeBase anime : animes) {
            Estado estado = anime.obtenerEstado();
            conteo.put(estado, conteo.get(estado) + 1);
        }
        
        return conteo;
    }
    
    public List<Map.Entry<Genero, Long>> obtenerTop3GenerosMasFrecuentes() throws ExcepcionPersistencia {
        List<AnimeBase> animes = repositorioAnime.obtenerTodos();
        
        Map<Genero, Long> conteoGeneros = new EnumMap<>(Genero.class);
        
        for (AnimeBase anime : animes) {
            for (Genero genero : anime.obtenerGeneros()) {
                Long actual = conteoGeneros.get(genero);
                if (actual == null) {
                    conteoGeneros.put(genero, 1L);
                } else {
                    conteoGeneros.put(genero, actual + 1);
                }
            }
        }
        
        List<Map.Entry<Genero, Long>> listaOrdenada = new ArrayList<>(conteoGeneros.entrySet());
        Collections.sort(listaOrdenada, new Comparator<Map.Entry<Genero, Long>>() {
            @Override
            public int compare(Map.Entry<Genero, Long> e1, Map.Entry<Genero, Long> e2) {
                return e2.getValue().compareTo(e1.getValue());
            }
        });
        
        List<Map.Entry<Genero, Long>> top3 = new ArrayList<>();
        for (int i = 0; i < Math.min(3, listaOrdenada.size()); i++) {
            top3.add(listaOrdenada.get(i));
        }
        
        return top3;
    }
    
    public Map<Genero, Long> obtenerDistribucionGeneros() throws ExcepcionPersistencia {
        List<AnimeBase> animes = repositorioAnime.obtenerTodos();
        
        Map<Genero, Long> distribucion = new EnumMap<>(Genero.class);
        
        for (AnimeBase anime : animes) {
            for (Genero genero : anime.obtenerGeneros()) {
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
    
    public ResumenEstadisticas obtenerResumenEstadisticas() throws ExcepcionPersistencia {
        List<AnimeBase> animes = repositorioAnime.obtenerTodos();
        
        int totalAnimes = animes.size();
        
        int animesCalificados = 0;
        for (AnimeBase anime : animes) {
            if (anime.tieneCalificacion()) {
                animesCalificados++;
            }
        }
        
        double promedioGlobal = obtenerPromedioCalificacionGlobal();
        Map<Estado, Long> porEstado = obtenerCantidadPorEstado();
        List<Map.Entry<Genero, Long>> topGeneros = obtenerTop3GenerosMasFrecuentes();
        
        AnimeBase masAntiguo = null;
        AnimeBase masNuevo = null;
        AnimeBase mejorCalificado = null;
        
        for (AnimeBase anime : animes) {
            if (masAntiguo == null || anime.obtenerAnioLanzamiento() < masAntiguo.obtenerAnioLanzamiento()) {
                masAntiguo = anime;
            }
            
            if (masNuevo == null || anime.obtenerAnioLanzamiento() > masNuevo.obtenerAnioLanzamiento()) {
                masNuevo = anime;
            }
            
            if (anime.tieneCalificacion()) {
                if (mejorCalificado == null || anime.obtenerCalificacion() > mejorCalificado.obtenerCalificacion()) {
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
        
        public int obtenerTotalAnimes() { return totalAnimes; }
        public int obtenerAnimesCalificados() { return animesCalificados; }
        public double obtenerPromedioCalificacion() { return promedioCalificacion; }
        public Map<Estado, Long> obtenerCantidadPorEstado() { return cantidadPorEstado; }
        public List<Map.Entry<Genero, Long>> obtenerTopGeneros() { return topGeneros; }
        public AnimeBase obtenerAnimeMasAntiguo() { return animeMasAntiguo; }
        public AnimeBase obtenerAnimeMasNuevo() { return animeMasNuevo; }
        public AnimeBase obtenerAnimeMejorCalificado() { return animeMejorCalificado; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Resumen del Catálogo ===\n");
            sb.append(String.format("Total de anime: %d\n", totalAnimes));
            sb.append(String.format("Anime calificados: %d\n", animesCalificados));
            sb.append(String.format("Promedio de calificación: %.2f\n", promedioCalificacion));
            
            sb.append("\nPor estado:\n");
            for (Map.Entry<Estado, Long> entry : cantidadPorEstado.entrySet()) {
                sb.append(String.format("  %s: %d\n", entry.getKey().obtenerDescripcion(), entry.getValue()));
            }
            
            sb.append("\nTop 3 géneros:\n");
            for (int i = 0; i < topGeneros.size(); i++) {
                Map.Entry<Genero, Long> entry = topGeneros.get(i);
                sb.append(String.format("  %d. %s (%d anime)\n", 
                    i + 1, entry.getKey().obtenerDescripcion(), entry.getValue()));
            }
            
            if (animeMejorCalificado != null) {
                sb.append(String.format("\nMejor calificado: %s (★%d)\n", 
                    animeMejorCalificado.obtenerTitulo(), animeMejorCalificado.obtenerCalificacion()));
            }
            
            return sb.toString();
        }
    }
}

