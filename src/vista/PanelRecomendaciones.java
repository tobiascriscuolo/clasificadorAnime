package vista;

import modelo.*;
import servicio.*;
import excepcion.*;
import utilidad.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel para obtener recomendaciones de anime.
 */
public class PanelRecomendaciones extends JPanel {
    
    private final ServicioRecomendacion servicioRecomendacion;
    private final ServicioAnime servicioAnime;
    
    private JComboBox<String> cmbTipoRecomendacion;
    private JComboBox<Genero> cmbGenero;
    private JComboBox<Estado> cmbEstado;
    private JSpinner spnCantidad;
    private JTable tablaResultados;
    private ModeloTablaRecomendacion modeloTabla;
    private JLabel lblDescripcion;
    
    public PanelRecomendaciones(ServicioRecomendacion servicioRecomendacion, ServicioAnime servicioAnime) {
        this.servicioRecomendacion = servicioRecomendacion;
        this.servicioAnime = servicioAnime;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        crearUI();
    }
    
    private void crearUI() {
        JPanel panelConfig = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelConfig.setBorder(BorderFactory.createTitledBorder("Configurar Recomendación"));
        
        panelConfig.add(new JLabel("Tipo:"));
        cmbTipoRecomendacion = new JComboBox<>(new String[]{"Top Global", "Top por Género", "Top por Estado"});
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
        
        JPanel panelResultados = new JPanel(new BorderLayout(5, 5));
        panelResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));
        
        lblDescripcion = new JLabel("Configure los parámetros y obtenga sus recomendaciones");
        lblDescripcion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        lblDescripcion.setFont(lblDescripcion.getFont().deriveFont(Font.ITALIC));
        panelResultados.add(lblDescripcion, BorderLayout.NORTH);
        
        modeloTabla = new ModeloTablaRecomendacion();
        tablaResultados = new JTable(modeloTabla);
        tablaResultados.setRowHeight(30);
        
        TableColumnModel cm = tablaResultados.getColumnModel();
        cm.getColumn(0).setPreferredWidth(50);
        cm.getColumn(1).setPreferredWidth(300);
        cm.getColumn(2).setPreferredWidth(60);
        cm.getColumn(3).setPreferredWidth(150);
        cm.getColumn(4).setPreferredWidth(80);
        cm.getColumn(5).setPreferredWidth(100);
        cm.getColumn(5).setCellRenderer(new PanelAnime.RenderizadorEstrellas());
        
        JScrollPane scrollResultados = new JScrollPane(tablaResultados);
        panelResultados.add(scrollResultados, BorderLayout.CENTER);
        
        add(panelResultados, BorderLayout.CENTER);
        
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
        
        add(panelRapido, BorderLayout.SOUTH);
        
        actualizarVisibilidadFiltros();
    }
    
    private void actualizarVisibilidadFiltros() {
        int tipo = cmbTipoRecomendacion.getSelectedIndex();
        cmbGenero.setVisible(tipo == 1);
        cmbEstado.setVisible(tipo == 2);
    }
    
    private void obtenerRecomendaciones() {
        try {
            int cantidad = (Integer) spnCantidad.getValue();
            int tipo = cmbTipoRecomendacion.getSelectedIndex();
            
            List<AnimeBase> resultados;
            String descripcion;
            
            switch (tipo) {
                case 0:
                    resultados = servicioRecomendacion.obtenerTopGlobal(cantidad);
                    descripcion = "Top " + cantidad + " anime mejor calificados del catálogo";
                    break;
                case 1:
                    Genero genero = (Genero) cmbGenero.getSelectedItem();
                    resultados = servicioRecomendacion.obtenerTopPorGenero(genero, cantidad);
                    descripcion = "Top " + cantidad + " anime de género " + genero.obtenerDescripcion();
                    break;
                case 2:
                    Estado estado = (Estado) cmbEstado.getSelectedItem();
                    resultados = servicioRecomendacion.obtenerTopPorEstado(estado, cantidad);
                    descripcion = "Top " + cantidad + " anime con estado " + estado.obtenerDescripcion();
                    break;
                default:
                    resultados = new ArrayList<>();
                    descripcion = "";
                    break;
            }
            
            modeloTabla.establecerAnimes(resultados);
            lblDescripcion.setText(descripcion + " (" + resultados.size() + " resultados)");
            
            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No se encontraron anime que coincidan con los criterios.\n" +
                    "Asegúrese de tener anime calificados en el catálogo.",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (ExcepcionPersistencia e) {
            JOptionPane.showMessageDialog(this,
                "Error al obtener recomendaciones: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void refrescar() {
        modeloTabla.establecerAnimes(new ArrayList<>());
        lblDescripcion.setText("Configure los parámetros y obtenga sus recomendaciones");
    }
    
    private static class ModeloTablaRecomendacion extends AbstractTableModel {
        private final String[] columnas = {"#", "Título", "Año", "Géneros", "Estado", "★"};
        private List<AnimeBase> animes = new ArrayList<>();
        
        public void establecerAnimes(List<AnimeBase> animes) {
            this.animes = new ArrayList<>(animes);
            fireTableDataChanged();
        }
        
        @Override public int getRowCount() { return animes.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int col) { return columnas[col]; }
        
        @Override
        public Object getValueAt(int fila, int col) {
            AnimeBase a = animes.get(fila);
            switch (col) {
                case 0: return fila + 1;
                case 1: return a.obtenerTitulo();
                case 2: return a.obtenerAnioLanzamiento();
                case 3: return formatearGeneros(a.obtenerGeneros());
                case 4: return a.obtenerEstado().obtenerDescripcion();
                case 5: return a.tieneCalificacion() ? a.obtenerCalificacion() : 0;
                default: return "";
            }
        }
        
        private String formatearGeneros(Set<Genero> generos) {
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (Genero genero : generos) {
                if (count >= 3) break;
                if (count > 0) sb.append(", ");
                sb.append(genero.obtenerDescripcion());
                count++;
            }
            return sb.toString();
        }
    }
}

