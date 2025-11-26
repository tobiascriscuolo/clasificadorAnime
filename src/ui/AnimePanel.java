package ui;

import model.*;
import service.*;
import exception.*;
import util.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Panel para la gestión del catálogo de anime.
 * 
 * SOLID - SRP: Solo maneja la visualización y gestión del catálogo.
 * 
 * MVC: Es parte de la Vista, delega toda la lógica al AnimeService.
 */
public class AnimePanel extends JPanel {
    
    private final AnimeService animeService;
    private final ListaPersonalizadaService listaService;
    
    // Componentes de UI
    private JTable tablaAnime;
    private AnimeTableModel tableModel;
    private JTextField txtBusqueda;
    private JComboBox<String> cmbGenero;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbOrden;
    private JSpinner spnCalificacionMin;
    
    public AnimePanel(AnimeService animeService, ListaPersonalizadaService listaService) {
        this.animeService = animeService;
        this.listaService = listaService;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        crearPanelSuperior();
        crearTabla();
        crearPanelInferior();
        
        refrescar();
    }
    
    private void crearPanelSuperior() {
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("🔍 Buscar:"));
        txtBusqueda = new JTextField(20);
        txtBusqueda.addActionListener(e -> aplicarFiltros());
        panelBusqueda.add(txtBusqueda);
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> aplicarFiltros());
        panelBusqueda.add(btnBuscar);
        
        JButton btnLimpiar = new JButton("Limpiar filtros");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panelBusqueda.add(btnLimpiar);
        
        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        panelFiltros.add(new JLabel("Género:"));
        cmbGenero = new JComboBox<>();
        cmbGenero.addItem("Todos");
        for (Genero g : Genero.values()) {
            cmbGenero.addItem(g.getDescripcion());
        }
        cmbGenero.addActionListener(e -> aplicarFiltros());
        panelFiltros.add(cmbGenero);
        
        panelFiltros.add(Box.createHorizontalStrut(10));
        panelFiltros.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>();
        cmbEstado.addItem("Todos");
        for (Estado e : Estado.values()) {
            cmbEstado.addItem(e.getDescripcion());
        }
        cmbEstado.addActionListener(e -> aplicarFiltros());
        panelFiltros.add(cmbEstado);
        
        panelFiltros.add(Box.createHorizontalStrut(10));
        panelFiltros.add(new JLabel("Calificación mín:"));
        spnCalificacionMin = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
        spnCalificacionMin.addChangeListener(e -> aplicarFiltros());
        panelFiltros.add(spnCalificacionMin);
        
        panelFiltros.add(Box.createHorizontalStrut(10));
        panelFiltros.add(new JLabel("Ordenar por:"));
        cmbOrden = new JComboBox<>(new String[]{
            "Título (A-Z)", "Título (Z-A)",
            "Calificación (mejor)", "Calificación (peor)",
            "Año (reciente)", "Año (antiguo)"
        });
        cmbOrden.addActionListener(e -> aplicarFiltros());
        panelFiltros.add(cmbOrden);
        
        panelSuperior.add(panelBusqueda, BorderLayout.NORTH);
        panelSuperior.add(panelFiltros, BorderLayout.SOUTH);
        
        add(panelSuperior, BorderLayout.NORTH);
    }
    
    private void crearTabla() {
        tableModel = new AnimeTableModel();
        tablaAnime = new JTable(tableModel);
        
        // Configurar tabla
        tablaAnime.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAnime.setRowHeight(25);
        tablaAnime.getTableHeader().setReorderingAllowed(false);
        
        // Ajustar anchos de columna
        TableColumnModel columnModel = tablaAnime.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // Tipo
        columnModel.getColumn(1).setPreferredWidth(250); // Título
        columnModel.getColumn(2).setPreferredWidth(60);  // Año
        columnModel.getColumn(3).setPreferredWidth(120); // Estudio
        columnModel.getColumn(4).setPreferredWidth(100); // Duración
        columnModel.getColumn(5).setPreferredWidth(150); // Géneros
        columnModel.getColumn(6).setPreferredWidth(80);  // Estado
        columnModel.getColumn(7).setPreferredWidth(60);  // Calificación
        
        // Menú contextual
        tablaAnime.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) mostrarMenuContextual(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) mostrarMenuContextual(e);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarAnimeSeleccionado();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaAnime);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void crearPanelInferior() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton btnNuevaSerie = new JButton("➕ Nueva Serie");
        btnNuevaSerie.addActionListener(e -> mostrarDialogoNuevaSerie());
        panelBotones.add(btnNuevaSerie);
        
        JButton btnNuevaPelicula = new JButton("➕ Nueva Película");
        btnNuevaPelicula.addActionListener(e -> mostrarDialogoNuevaPelicula());
        panelBotones.add(btnNuevaPelicula);
        
        panelBotones.add(Box.createHorizontalStrut(20));
        
        JButton btnEditar = new JButton("✏️ Editar");
        btnEditar.addActionListener(e -> editarAnimeSeleccionado());
        panelBotones.add(btnEditar);
        
        JButton btnEliminar = new JButton("🗑️ Eliminar");
        btnEliminar.addActionListener(e -> eliminarAnimeSeleccionado());
        panelBotones.add(btnEliminar);
        
        panelBotones.add(Box.createHorizontalStrut(20));
        
        JButton btnAgregarALista = new JButton("📋 Agregar a lista...");
        btnAgregarALista.addActionListener(e -> agregarAListaSeleccionada());
        panelBotones.add(btnAgregarALista);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void mostrarMenuContextual(MouseEvent e) {
        int row = tablaAnime.rowAtPoint(e.getPoint());
        if (row >= 0) {
            tablaAnime.setRowSelectionInterval(row, row);
            
            JPopupMenu menu = new JPopupMenu();
            
            JMenuItem itemEditar = new JMenuItem("Editar");
            itemEditar.addActionListener(ev -> editarAnimeSeleccionado());
            menu.add(itemEditar);
            
            JMenuItem itemCalificar = new JMenuItem("Calificar...");
            itemCalificar.addActionListener(ev -> calificarAnimeSeleccionado());
            menu.add(itemCalificar);
            
            JMenuItem itemCambiarEstado = new JMenuItem("Cambiar estado...");
            itemCambiarEstado.addActionListener(ev -> cambiarEstadoAnimeSeleccionado());
            menu.add(itemCambiarEstado);
            
            menu.addSeparator();
            
            JMenuItem itemAgregarLista = new JMenuItem("Agregar a lista...");
            itemAgregarLista.addActionListener(ev -> agregarAListaSeleccionada());
            menu.add(itemAgregarLista);
            
            menu.addSeparator();
            
            JMenuItem itemEliminar = new JMenuItem("Eliminar");
            itemEliminar.addActionListener(ev -> eliminarAnimeSeleccionado());
            menu.add(itemEliminar);
            
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    public void mostrarDialogoNuevaSerie() {
        AnimeSerieDialog dialog = new AnimeSerieDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), animeService, null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmado()) {
            refrescar();
        }
    }
    
    public void mostrarDialogoNuevaPelicula() {
        AnimePeliculaDialog dialog = new AnimePeliculaDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), animeService, null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmado()) {
            refrescar();
        }
    }
    
    private void editarAnimeSeleccionado() {
        AnimeBase anime = getAnimeSeleccionado();
        if (anime == null) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un anime para editar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (anime instanceof AnimeSerie serie) {
            AnimeSerieDialog dialog = new AnimeSerieDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), animeService, serie);
            dialog.setVisible(true);
            if (dialog.isConfirmado()) refrescar();
        } else if (anime instanceof AnimePelicula pelicula) {
            AnimePeliculaDialog dialog = new AnimePeliculaDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), animeService, pelicula);
            dialog.setVisible(true);
            if (dialog.isConfirmado()) refrescar();
        }
    }
    
    private void eliminarAnimeSeleccionado() {
        AnimeBase anime = getAnimeSeleccionado();
        if (anime == null) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un anime para eliminar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar '" + anime.getTitulo() + "'?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                animeService.eliminarAnime(anime.getTitulo());
                JOptionPane.showMessageDialog(this, "Anime eliminado correctamente");
                refrescar();
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void calificarAnimeSeleccionado() {
        AnimeBase anime = getAnimeSeleccionado();
        if (anime == null) return;
        
        String[] opciones = {"1 ⭐", "2 ⭐⭐", "3 ⭐⭐⭐", "4 ⭐⭐⭐⭐", "5 ⭐⭐⭐⭐⭐"};
        int seleccion = JOptionPane.showOptionDialog(this,
            "Calificar: " + anime.getTitulo(),
            "Calificar anime",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null, opciones, opciones[2]);
        
        if (seleccion >= 0) {
            try {
                animeService.calificarAnime(anime.getTitulo(), seleccion + 1);
                refrescar();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al calificar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cambiarEstadoAnimeSeleccionado() {
        AnimeBase anime = getAnimeSeleccionado();
        if (anime == null) return;
        
        Estado nuevoEstado = (Estado) JOptionPane.showInputDialog(this,
            "Nuevo estado para: " + anime.getTitulo(),
            "Cambiar estado",
            JOptionPane.QUESTION_MESSAGE,
            null,
            Estado.values(),
            anime.getEstado());
        
        if (nuevoEstado != null) {
            try {
                animeService.cambiarEstado(anime.getTitulo(), nuevoEstado);
                refrescar();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al cambiar estado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void agregarAListaSeleccionada() {
        AnimeBase anime = getAnimeSeleccionado();
        if (anime == null) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un anime", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            List<ListaPersonalizada> listas = listaService.listarTodas();
            if (listas.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No hay listas personalizadas. Cree una primero.", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            ListaPersonalizada lista = (ListaPersonalizada) JOptionPane.showInputDialog(
                this,
                "Agregar '" + anime.getTitulo() + "' a la lista:",
                "Agregar a lista",
                JOptionPane.QUESTION_MESSAGE,
                null,
                listas.toArray(),
                listas.get(0));
            
            if (lista != null) {
                boolean agregado = listaService.agregarAnimeALista(lista.getNombre(), anime.getTitulo());
                if (agregado) {
                    JOptionPane.showMessageDialog(this, "Anime agregado a la lista");
                } else {
                    JOptionPane.showMessageDialog(this, "El anime ya está en esa lista");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private AnimeBase getAnimeSeleccionado() {
        int row = tablaAnime.getSelectedRow();
        if (row >= 0) {
            return tableModel.getAnimeAt(row);
        }
        return null;
    }
    
    private void aplicarFiltros() {
        try {
            // Construir filtro
            FiltroAnime filtro = new FiltroAnime();
            
            // Búsqueda por texto
            String busqueda = txtBusqueda.getText().trim();
            if (!busqueda.isEmpty()) {
                filtro.porTitulo(busqueda);
            }
            
            // Filtro por género
            int generoIndex = cmbGenero.getSelectedIndex();
            if (generoIndex > 0) {
                Genero genero = Genero.values()[generoIndex - 1];
                filtro.porGenero(genero);
            }
            
            // Filtro por estado
            int estadoIndex = cmbEstado.getSelectedIndex();
            if (estadoIndex > 0) {
                Estado estado = Estado.values()[estadoIndex - 1];
                filtro.porEstado(estado);
            }
            
            // Filtro por calificación mínima
            int calMin = (Integer) spnCalificacionMin.getValue();
            if (calMin > 0) {
                filtro.porCalificacionMinima(calMin);
            }
            
            // Aplicar filtro
            List<AnimeBase> resultado = animeService.busquedaAvanzada(filtro);
            
            // Aplicar ordenamiento
            CriterioOrdenamiento criterio = obtenerCriterioOrdenamiento();
            resultado = animeService.ordenar(resultado, criterio);
            
            tableModel.setAnimes(resultado);
            
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al filtrar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private CriterioOrdenamiento obtenerCriterioOrdenamiento() {
        int index = cmbOrden.getSelectedIndex();
        return switch (index) {
            case 0 -> new OrdenamientoPorTitulo(true);
            case 1 -> new OrdenamientoPorTitulo(false);
            case 2 -> new OrdenamientoPorCalificacion(true);
            case 3 -> new OrdenamientoPorCalificacion(false);
            case 4 -> new OrdenamientoPorAnio(true);
            case 5 -> new OrdenamientoPorAnio(false);
            default -> new OrdenamientoPorTitulo(true);
        };
    }
    
    private void limpiarFiltros() {
        txtBusqueda.setText("");
        cmbGenero.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        spnCalificacionMin.setValue(0);
        cmbOrden.setSelectedIndex(0);
        refrescar();
    }
    
    public void refrescar() {
        try {
            List<AnimeBase> animes = animeService.listarOrdenadosPorTitulo();
            tableModel.setAnimes(animes);
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Modelo de tabla para mostrar anime.
     */
    private static class AnimeTableModel extends AbstractTableModel {
        private final String[] columnas = {
            "Tipo", "Título", "Año", "Estudio", "Duración", "Géneros", "Estado", "★"
        };
        private List<AnimeBase> animes = new ArrayList<>();
        
        public void setAnimes(List<AnimeBase> animes) {
            this.animes = new ArrayList<>(animes);
            fireTableDataChanged();
        }
        
        public AnimeBase getAnimeAt(int row) {
            if (row >= 0 && row < animes.size()) {
                return animes.get(row);
            }
            return null;
        }
        
        @Override
        public int getRowCount() {
            return animes.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnas.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnas[column];
        }
        
        @Override
        public Object getValueAt(int row, int col) {
            AnimeBase anime = animes.get(row);
            return switch (col) {
                case 0 -> anime.getTipo().getDescripcion();
                case 1 -> anime.getTitulo();
                case 2 -> anime.getAnioLanzamiento();
                case 3 -> anime.getEstudio();
                case 4 -> anime.getDescripcionDuracion();
                case 5 -> formatearGeneros(anime.getGeneros());
                case 6 -> anime.getEstado().getDescripcion();
                case 7 -> anime.tieneCalificacion() ? anime.getCalificacion() : "-";
                default -> "";
            };
        }
        
        private String formatearGeneros(Set<Genero> generos) {
            return generos.stream()
                .map(Genero::getDescripcion)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        }
    }
}

