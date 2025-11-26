package ui;

import model.*;
import service.*;
import exception.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel para mostrar estadísticas del catálogo.
 * 
 * SOLID - SRP: Solo maneja la visualización de estadísticas.
 * 
 * MVC: Es parte de la Vista, delega la lógica al EstadisticasService.
 */
public class EstadisticasPanel extends JPanel {
    
    private final EstadisticasService estadisticasService;
    
    private JLabel lblTotalAnimes;
    private JLabel lblAnimesCalificados;
    private JLabel lblPromedioGlobal;
    private JLabel lblMejorCalificado;
    private JPanel panelPorEstado;
    private JPanel panelTopGeneros;
    private JPanel panelPromediosPorGenero;
    private JTextArea txtResumen;
    
    public EstadisticasPanel(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        crearUI();
        refrescar();
    }
    
    private void crearUI() {
        // Panel superior: estadísticas generales
        JPanel panelGeneral = new JPanel(new GridLayout(2, 2, 20, 10));
        panelGeneral.setBorder(BorderFactory.createTitledBorder("📊 Estadísticas Generales"));
        
        // Total de anime
        JPanel cardTotal = crearTarjetaEstadistica("📚 Total de Anime", "0");
        lblTotalAnimes = (JLabel) ((JPanel) cardTotal.getComponent(1)).getComponent(0);
        panelGeneral.add(cardTotal);
        
        // Anime calificados
        JPanel cardCalificados = crearTarjetaEstadistica("✅ Calificados", "0");
        lblAnimesCalificados = (JLabel) ((JPanel) cardCalificados.getComponent(1)).getComponent(0);
        panelGeneral.add(cardCalificados);
        
        // Promedio global
        JPanel cardPromedio = crearTarjetaEstadistica("⭐ Promedio Global", "0.0");
        lblPromedioGlobal = (JLabel) ((JPanel) cardPromedio.getComponent(1)).getComponent(0);
        panelGeneral.add(cardPromedio);
        
        // Mejor calificado
        JPanel cardMejor = crearTarjetaEstadistica("🏆 Mejor Calificado", "-");
        lblMejorCalificado = (JLabel) ((JPanel) cardMejor.getComponent(1)).getComponent(0);
        panelGeneral.add(cardMejor);
        
        add(panelGeneral, BorderLayout.NORTH);
        
        // Panel central: detalles
        JPanel panelDetalles = new JPanel(new GridLayout(1, 3, 10, 10));
        
        // Panel por estado
        panelPorEstado = new JPanel();
        panelPorEstado.setLayout(new BoxLayout(panelPorEstado, BoxLayout.Y_AXIS));
        panelPorEstado.setBorder(BorderFactory.createTitledBorder("Por Estado"));
        JScrollPane scrollEstado = new JScrollPane(panelPorEstado);
        panelDetalles.add(scrollEstado);
        
        // Panel top géneros
        panelTopGeneros = new JPanel();
        panelTopGeneros.setLayout(new BoxLayout(panelTopGeneros, BoxLayout.Y_AXIS));
        panelTopGeneros.setBorder(BorderFactory.createTitledBorder("🏅 Top 3 Géneros"));
        JScrollPane scrollGeneros = new JScrollPane(panelTopGeneros);
        panelDetalles.add(scrollGeneros);
        
        // Panel promedios por género
        panelPromediosPorGenero = new JPanel();
        panelPromediosPorGenero.setLayout(new BoxLayout(panelPromediosPorGenero, BoxLayout.Y_AXIS));
        panelPromediosPorGenero.setBorder(BorderFactory.createTitledBorder("📈 Promedio por Género"));
        JScrollPane scrollPromedios = new JScrollPane(panelPromediosPorGenero);
        panelDetalles.add(scrollPromedios);
        
        add(panelDetalles, BorderLayout.CENTER);
        
        // Panel inferior: resumen textual
        JPanel panelResumen = new JPanel(new BorderLayout());
        panelResumen.setBorder(BorderFactory.createTitledBorder("📝 Resumen Completo"));
        
        txtResumen = new JTextArea(8, 40);
        txtResumen.setEditable(false);
        txtResumen.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollResumen = new JScrollPane(txtResumen);
        panelResumen.add(scrollResumen, BorderLayout.CENTER);
        
        JButton btnRefrescar = new JButton("🔄 Actualizar Estadísticas");
        btnRefrescar.addActionListener(e -> refrescar());
        panelResumen.add(btnRefrescar, BorderLayout.SOUTH);
        
        add(panelResumen, BorderLayout.SOUTH);
    }
    
    private JPanel crearTarjetaEstadistica(String titulo, String valor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(new Color(245, 245, 245));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        card.add(lblTitulo, BorderLayout.NORTH);
        
        JPanel panelValor = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelValor.setOpaque(false);
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.BOLD, 24));
        lblValor.setForeground(new Color(0, 100, 200));
        panelValor.add(lblValor);
        card.add(panelValor, BorderLayout.CENTER);
        
        return card;
    }
    
    public void refrescar() {
        try {
            EstadisticasService.ResumenEstadisticas resumen = 
                estadisticasService.getResumenEstadisticas();
            
            // Actualizar tarjetas
            lblTotalAnimes.setText(String.valueOf(resumen.getTotalAnimes()));
            lblAnimesCalificados.setText(String.valueOf(resumen.getAnimesCalificados()));
            lblPromedioGlobal.setText(String.format("%.2f ⭐", resumen.getPromedioCalificacion()));
            
            if (resumen.getAnimeMejorCalificado() != null) {
                AnimeBase mejor = resumen.getAnimeMejorCalificado();
                lblMejorCalificado.setText(String.format("<html><center>%s<br>(★%d)</center></html>",
                    mejor.getTitulo(), mejor.getCalificacion()));
            } else {
                lblMejorCalificado.setText("-");
            }
            
            // Actualizar panel por estado
            panelPorEstado.removeAll();
            Map<Estado, Long> porEstado = resumen.getCantidadPorEstado();
            for (Estado estado : Estado.values()) {
                Long cantidad = porEstado.getOrDefault(estado, 0L);
                JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT));
                fila.add(new JLabel(estado.getDescripcion() + ": "));
                
                JProgressBar bar = new JProgressBar(0, Math.max(resumen.getTotalAnimes(), 1));
                bar.setValue(cantidad.intValue());
                bar.setString(cantidad + " anime");
                bar.setStringPainted(true);
                bar.setPreferredSize(new Dimension(150, 20));
                fila.add(bar);
                
                panelPorEstado.add(fila);
            }
            
            // Actualizar top géneros
            panelTopGeneros.removeAll();
            List<Map.Entry<Genero, Long>> topGeneros = resumen.getTopGeneros();
            String[] medallas = {"🥇", "🥈", "🥉"};
            for (int i = 0; i < topGeneros.size(); i++) {
                Map.Entry<Genero, Long> entry = topGeneros.get(i);
                JLabel lbl = new JLabel(String.format("%s %s: %d anime",
                    medallas[i], entry.getKey().getDescripcion(), entry.getValue()));
                lbl.setFont(new Font("Arial", Font.PLAIN, 14));
                lbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                panelTopGeneros.add(lbl);
            }
            
            // Actualizar promedios por género
            panelPromediosPorGenero.removeAll();
            Map<Genero, Double> promedios = estadisticasService.getPromediosCalificacionPorGenero();
            
            // Ordenar por promedio descendente
            promedios.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(10)
                .forEach(entry -> {
                    JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    fila.add(new JLabel(String.format("%s: %.2f ⭐",
                        entry.getKey().getDescripcion(), entry.getValue())));
                    panelPromediosPorGenero.add(fila);
                });
            
            // Actualizar resumen textual
            txtResumen.setText(resumen.toString());
            
            // Refrescar visualmente
            panelPorEstado.revalidate();
            panelPorEstado.repaint();
            panelTopGeneros.revalidate();
            panelTopGeneros.repaint();
            panelPromediosPorGenero.revalidate();
            panelPromediosPorGenero.repaint();
            
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar estadísticas: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

