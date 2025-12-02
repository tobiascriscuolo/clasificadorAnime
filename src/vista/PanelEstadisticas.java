package vista;

import modelo.*;
import servicio.*;
import excepcion.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel para mostrar estadísticas del catálogo.
 */
public class PanelEstadisticas extends JPanel {
    
    private final ServicioEstadisticas servicioEstadisticas;
    
    private JLabel lblTotalAnimes;
    private JLabel lblAnimesCalificados;
    private JLabel lblPromedioGlobal;
    private JLabel lblMejorCalificado;
    private JPanel panelPorEstado;
    private JPanel panelTopGeneros;
    private JTextArea txtResumen;
    
    public PanelEstadisticas(ServicioEstadisticas servicioEstadisticas) {
        this.servicioEstadisticas = servicioEstadisticas;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        crearUI();
        refrescar();
    }
    
    private void crearUI() {
        JPanel panelGeneral = new JPanel(new GridLayout(2, 2, 20, 10));
        panelGeneral.setBorder(BorderFactory.createTitledBorder("📊 Estadísticas Generales"));
        
        JPanel cardTotal = crearTarjetaEstadistica("📚 Total de Anime", "0");
        lblTotalAnimes = (JLabel) ((JPanel) cardTotal.getComponent(1)).getComponent(0);
        panelGeneral.add(cardTotal);
        
        JPanel cardCalificados = crearTarjetaEstadistica("✅ Calificados", "0");
        lblAnimesCalificados = (JLabel) ((JPanel) cardCalificados.getComponent(1)).getComponent(0);
        panelGeneral.add(cardCalificados);
        
        JPanel cardPromedio = crearTarjetaEstadistica("⭐ Promedio Global", "0.0");
        lblPromedioGlobal = (JLabel) ((JPanel) cardPromedio.getComponent(1)).getComponent(0);
        panelGeneral.add(cardPromedio);
        
        JPanel cardMejor = crearTarjetaEstadistica("🏆 Mejor Calificado", "-");
        lblMejorCalificado = (JLabel) ((JPanel) cardMejor.getComponent(1)).getComponent(0);
        panelGeneral.add(cardMejor);
        
        add(panelGeneral, BorderLayout.NORTH);
        
        JPanel panelDetalles = new JPanel(new GridLayout(1, 2, 10, 10));
        
        panelPorEstado = new JPanel();
        panelPorEstado.setLayout(new BoxLayout(panelPorEstado, BoxLayout.Y_AXIS));
        panelPorEstado.setBorder(BorderFactory.createTitledBorder("Por Estado"));
        JScrollPane scrollEstado = new JScrollPane(panelPorEstado);
        panelDetalles.add(scrollEstado);
        
        panelTopGeneros = new JPanel();
        panelTopGeneros.setLayout(new BoxLayout(panelTopGeneros, BoxLayout.Y_AXIS));
        panelTopGeneros.setBorder(BorderFactory.createTitledBorder("🏅 Top 3 Géneros"));
        JScrollPane scrollGeneros = new JScrollPane(panelTopGeneros);
        panelDetalles.add(scrollGeneros);
        
        add(panelDetalles, BorderLayout.CENTER);
        
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
            ServicioEstadisticas.ResumenEstadisticas resumen = servicioEstadisticas.obtenerResumenEstadisticas();
            
            lblTotalAnimes.setText(String.valueOf(resumen.obtenerTotalAnimes()));
            lblAnimesCalificados.setText(String.valueOf(resumen.obtenerAnimesCalificados()));
            lblPromedioGlobal.setText(String.format("%.2f ⭐", resumen.obtenerPromedioCalificacion()));
            
            if (resumen.obtenerAnimeMejorCalificado() != null) {
                AnimeBase mejor = resumen.obtenerAnimeMejorCalificado();
                lblMejorCalificado.setText(String.format("<html><center>%s<br>(★%d)</center></html>",
                    mejor.obtenerTitulo(), mejor.obtenerCalificacion()));
            } else {
                lblMejorCalificado.setText("-");
            }
            
            panelPorEstado.removeAll();
            Map<Estado, Long> porEstado = resumen.obtenerCantidadPorEstado();
            for (Estado estado : Estado.values()) {
                Long cantidad = porEstado.getOrDefault(estado, 0L);
                JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT));
                fila.add(new JLabel(estado.obtenerDescripcion() + ": "));
                
                JProgressBar bar = new JProgressBar(0, Math.max(resumen.obtenerTotalAnimes(), 1));
                bar.setValue(cantidad.intValue());
                bar.setString(cantidad + " anime");
                bar.setStringPainted(true);
                bar.setPreferredSize(new Dimension(150, 20));
                fila.add(bar);
                
                panelPorEstado.add(fila);
            }
            
            panelTopGeneros.removeAll();
            List<Map.Entry<Genero, Long>> topGeneros = resumen.obtenerTopGeneros();
            String[] medallas = {"🥇", "🥈", "🥉"};
            for (int i = 0; i < topGeneros.size(); i++) {
                Map.Entry<Genero, Long> entry = topGeneros.get(i);
                JLabel lbl = new JLabel(String.format("%s %s: %d anime",
                    medallas[i], entry.getKey().obtenerDescripcion(), entry.getValue()));
                lbl.setFont(new Font("Arial", Font.PLAIN, 14));
                lbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                panelTopGeneros.add(lbl);
            }
            
            txtResumen.setText(resumen.toString());
            
            panelPorEstado.revalidate();
            panelPorEstado.repaint();
            panelTopGeneros.revalidate();
            panelTopGeneros.repaint();
            
        } catch (ExcepcionPersistencia e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar estadísticas: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

