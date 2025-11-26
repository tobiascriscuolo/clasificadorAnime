package ui;

import model.*;
import service.*;
import exception.*;
import util.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel para obtener recomendaciones de anime.
 * 
 * SOLID - SRP: Solo maneja la visualización de recomendaciones.
 * 
 * GRASP - Controller: Delega la lógica al RecomendacionService.
 */
public class RecomendacionesPanel extends JPanel {
    
    private final RecomendacionService recomendacionService;
    private final AnimeService animeService;
    
    private JComboBox<String> cmbTipoRecomendacion;
    private JComboBox<Genero> cmbGenero;
    private JComboBox<Estado> cmbEstado;
    private JSpinner spnCantidad;
    private JTable tablaResultados;
    private RecomendacionTableModel tableModel;
    private JLabel lblDescripcion;
    
    public RecomendacionesPanel(RecomendacionService recomendacionService, AnimeService animeService) {
        this.recomendacionService = recomendacionService;
        this.animeService = animeService;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        crearUI();
    }
    
    private void crearUI() {
        // Panel superior: configuración de recomendación
        JPanel panelConfig = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelConfig.setBorder(BorderFactory.createTitledBorder("Configurar Recomendación"));
        
        panelConfig.add(new JLabel("Tipo:"));
        cmbTipoRecomendacion = new JComboBox<>(new String[]{
            "Top Global", "Top por Género", "Top por Estado"
        });
        cmbTipoRecomendacion.addActionListener(e -> actualizarVisibilidadFiltros());
        panelConfig.add(cmbTipoRecomendacion);
        
        panelConfig.add(Box.createHorizontalStrut(20));
        
        panelConfig.add(new JLabel("Género:"));
        cmbGenero = new JComboBox<>(Genero.values());
        panelConfig.add(cmbGenero);
        
        panelConfig.add(Box.createHorizontalStrut(10));
        
        panelConfig.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>(Estado.values());
        cmbEstado.setVisible(false);
        panelConfig.add(cmbEstado);
        
        panelConfig.add(Box.createHorizontalStrut(20));
        
        panelConfig.add(new JLabel("Cantidad:"));
        spnCantidad = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
        panelConfig.add(spnCantidad);
        
        panelConfig.add(Box.createHorizontalStrut(20));
        
        JButton btnObtener = new JButton("⭐ Obtener Recomendaciones");
        btnObtener.addActionListener(e -> obtenerRecomendaciones());
        panelConfig.add(btnObtener);
        
        add(panelConfig, BorderLayout.NORTH);
        
        // Panel central: resultados
        JPanel panelResultados = new JPanel(new BorderLayout(5, 5));
        panelResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));
        
        lblDescripcion = new JLabel("Configure los parámetros y obtenga sus recomendaciones");
        lblDescripcion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        lblDescripcion.setFont(lblDescripcion.getFont().deriveFont(Font.ITALIC));
        panelResultados.add(lblDescripcion, BorderLayout.NORTH);
        
        tableModel = new RecomendacionTableModel();
        tablaResultados = new JTable(tableModel);
        tablaResultados.setRowHeight(30);
        tablaResultados.setFont(new Font("Arial", Font.PLAIN, 13));
        
        // Configurar anchos de columna
        TableColumnModel cm = tablaResultados.getColumnModel();
        cm.getColumn(0).setPreferredWidth(50);  // Posición
        cm.getColumn(1).setPreferredWidth(300); // Título
        cm.getColumn(2).setPreferredWidth(60);  // Año
        cm.getColumn(3).setPreferredWidth(150); // Géneros
        cm.getColumn(4).setPreferredWidth(80);  // Estado
        cm.getColumn(5).setPreferredWidth(80);  // Calificación
        
        JScrollPane scrollResultados = new JScrollPane(tablaResultados);
        panelResultados.add(scrollResultados, BorderLayout.CENTER);
        
        add(panelResultados, BorderLayout.CENTER);
        
        // Panel inferior: criterios predefinidos
        JPanel panelRapido = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelRapido.setBorder(BorderFactory.createTitledBorder("Acceso Rápido"));
        
        JButton btnTopShonen = new JButton("Top 5 Shonen");
        btnTopShonen.addActionListener(e -> {
            cmbTipoRecomendacion.setSelectedIndex(1);
            cmbGenero.setSelectedItem(Genero.SHONEN);
            spnCantidad.setValue(5);
            obtenerRecomendaciones();
        });
        panelRapido.add(btnTopShonen);
        
        JButton btnTopGlobal = new JButton("Top 10 Global");
        btnTopGlobal.addActionListener(e -> {
            cmbTipoRecomendacion.setSelectedIndex(0);
            spnCantidad.setValue(10);
            obtenerRecomendaciones();
        });
        panelRapido.add(btnTopGlobal);
        
        JButton btnPorVer = new JButton("Mejores Por Ver");
        btnPorVer.addActionListener(e -> {
            cmbTipoRecomendacion.setSelectedIndex(2);
            cmbEstado.setSelectedItem(Estado.POR_VER);
            spnCantidad.setValue(5);
            obtenerRecomendaciones();
        });
        panelRapido.add(btnPorVer);
        
        JButton btnFinalizados = new JButton("Mejores Finalizados");
        btnFinalizados.addActionListener(e -> {
            cmbTipoRecomendacion.setSelectedIndex(2);
            cmbEstado.setSelectedItem(Estado.FINALIZADO);
            spnCantidad.setValue(10);
            obtenerRecomendaciones();
        });
        panelRapido.add(btnFinalizados);
        
        add(panelRapido, BorderLayout.SOUTH);
        
        actualizarVisibilidadFiltros();
    }
    
    private void actualizarVisibilidadFiltros() {
        int tipo = cmbTipoRecomendacion.getSelectedIndex();
        cmbGenero.setVisible(tipo == 1); // Top por Género
        cmbEstado.setVisible(tipo == 2); // Top por Estado
    }
    
    private void obtenerRecomendaciones() {
        try {
            int cantidad = (Integer) spnCantidad.getValue();
            int tipo = cmbTipoRecomendacion.getSelectedIndex();
            
            List<AnimeBase> resultados;
            String descripcion;
            
            switch (tipo) {
                case 0 -> { // Top Global
                    resultados = recomendacionService.getTopGlobal(cantidad);
                    descripcion = "Top " + cantidad + " anime mejor calificados del catálogo";
                }
                case 1 -> { // Top por Género
                    Genero genero = (Genero) cmbGenero.getSelectedItem();
                    resultados = recomendacionService.getTopPorGenero(genero, cantidad);
                    descripcion = "Top " + cantidad + " anime de género " + genero.getDescripcion();
                }
                case 2 -> { // Top por Estado
                    Estado estado = (Estado) cmbEstado.getSelectedItem();
                    resultados = recomendacionService.getTopPorEstado(estado, cantidad);
                    descripcion = "Top " + cantidad + " anime con estado " + estado.getDescripcion();
                }
                default -> {
                    resultados = new ArrayList<>();
                    descripcion = "";
                }
            }
            
            tableModel.setAnimes(resultados);
            lblDescripcion.setText(descripcion + " (" + resultados.size() + " resultados)");
            
            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No se encontraron anime que coincidan con los criterios.\n" +
                    "Asegúrese de tener anime calificados en el catálogo.",
                    "Sin resultados",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this,
                "Error al obtener recomendaciones: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void refrescar() {
        // Limpiar resultados al refrescar
        tableModel.setAnimes(new ArrayList<>());
        lblDescripcion.setText("Configure los parámetros y obtenga sus recomendaciones");
    }
    
    /**
     * Modelo de tabla para mostrar recomendaciones con posición.
     */
    private static class RecomendacionTableModel extends AbstractTableModel {
        private final String[] columnas = {"#", "Título", "Año", "Géneros", "Estado", "★"};
        private List<AnimeBase> animes = new ArrayList<>();
        
        public void setAnimes(List<AnimeBase> animes) {
            this.animes = new ArrayList<>(animes);
            fireTableDataChanged();
        }
        
        @Override public int getRowCount() { return animes.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int col) { return columnas[col]; }
        
        @Override
        public Object getValueAt(int row, int col) {
            AnimeBase a = animes.get(row);
            return switch (col) {
                case 0 -> row + 1; // Posición
                case 1 -> a.getTitulo();
                case 2 -> a.getAnioLanzamiento();
                case 3 -> formatearGeneros(a.getGeneros());
                case 4 -> a.getEstado().getDescripcion();
                case 5 -> "★".repeat(a.getCalificacion());
                default -> "";
            };
        }
        
        private String formatearGeneros(Set<Genero> generos) {
            return generos.stream()
                .limit(3)
                .map(Genero::getDescripcion)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        }
    }
}

