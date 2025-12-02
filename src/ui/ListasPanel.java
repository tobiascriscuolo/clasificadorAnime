package ui;

import model.*;
import service.*;
import exception.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Panel para gestionar listas personalizadas de anime.
 * 
 * SOLID - SRP: Solo maneja la visualización y gestión de listas.
 * 
 * MVC: Es parte de la Vista, delega lógica a ListaPersonalizadaService.
 */
public class ListasPanel extends JPanel {
    
    private final ListaPersonalizadaService listaService;
    private final AnimeService animeService;
    
    private JList<ListaPersonalizada> listaListas;
    private DefaultListModel<ListaPersonalizada> listModel;
    private JTable tablaAnimes;
    private AnimeTableModel tableModel;
    private JLabel lblInfoLista;
    
    public ListasPanel(ListaPersonalizadaService listaService, AnimeService animeService) {
        this.listaService = listaService;
        this.animeService = animeService;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        crearUI();
        refrescar();
    }
    
    private void crearUI() {
        // Panel izquierdo: lista de listas personalizadas
        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Mis Listas"));
        panelIzquierdo.setPreferredSize(new Dimension(250, 0));
        
        listModel = new DefaultListModel<>();
        listaListas = new JList<>(listModel);
        listaListas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaListas.setCellRenderer(new ListaCellRenderer());
        listaListas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarAnimesDeListaSeleccionada();
            }
        });
        
        // Menú contextual para listas
        listaListas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) mostrarMenuContextualLista(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) mostrarMenuContextualLista(e);
            }
        });
        
        JScrollPane scrollListas = new JScrollPane(listaListas);
        panelIzquierdo.add(scrollListas, BorderLayout.CENTER);
        
        // Botones de gestión de listas
        JPanel panelBotonesListas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevaLista = new JButton("➕ Nueva");
        btnNuevaLista.addActionListener(e -> mostrarDialogoNuevaLista());
        JButton btnEliminarLista = new JButton("🗑️ Eliminar");
        btnEliminarLista.addActionListener(e -> eliminarListaSeleccionada());
        
        panelBotonesListas.add(btnNuevaLista);
        panelBotonesListas.add(btnEliminarLista);
        panelIzquierdo.add(panelBotonesListas, BorderLayout.SOUTH);
        
        add(panelIzquierdo, BorderLayout.WEST);
        
        // Panel central: contenido de la lista seleccionada
        JPanel panelCentral = new JPanel(new BorderLayout(5, 5));
        panelCentral.setBorder(BorderFactory.createTitledBorder("Contenido de la Lista"));
        
        lblInfoLista = new JLabel("Seleccione una lista para ver su contenido");
        lblInfoLista.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelCentral.add(lblInfoLista, BorderLayout.NORTH);
        
        tableModel = new AnimeTableModel();
        tablaAnimes = new JTable(tableModel);
        tablaAnimes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAnimes.setRowHeight(25);
        
        // Renderer de estrellas amarillas para la columna de calificación
        tablaAnimes.getColumnModel().getColumn(3).setCellRenderer(new StarRatingRenderer());
        
        // Menú contextual para anime en lista
        tablaAnimes.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) mostrarMenuContextualAnime(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) mostrarMenuContextualAnime(e);
            }
        });
        
        JScrollPane scrollAnimes = new JScrollPane(tablaAnimes);
        panelCentral.add(scrollAnimes, BorderLayout.CENTER);
        
        // Botones de gestión de anime en lista
        JPanel panelBotonesAnime = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("➕ Agregar anime...");
        btnAgregar.addActionListener(e -> agregarAnimeALista());
        JButton btnRemover = new JButton("➖ Quitar de la lista");
        btnRemover.addActionListener(e -> removerAnimeDeLista());
        
        panelBotonesAnime.add(btnAgregar);
        panelBotonesAnime.add(btnRemover);
        panelCentral.add(panelBotonesAnime, BorderLayout.SOUTH);
        
        add(panelCentral, BorderLayout.CENTER);
    }
    
    private void mostrarMenuContextualLista(MouseEvent e) {
        int index = listaListas.locationToIndex(e.getPoint());
        if (index >= 0) {
            listaListas.setSelectedIndex(index);
            
            JPopupMenu menu = new JPopupMenu();
            
            JMenuItem itemRenombrar = new JMenuItem("Renombrar...");
            itemRenombrar.addActionListener(ev -> renombrarListaSeleccionada());
            menu.add(itemRenombrar);
            
            JMenuItem itemEliminar = new JMenuItem("Eliminar");
            itemEliminar.addActionListener(ev -> eliminarListaSeleccionada());
            menu.add(itemEliminar);
            
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    private void mostrarMenuContextualAnime(MouseEvent e) {
        int row = tablaAnimes.rowAtPoint(e.getPoint());
        if (row >= 0) {
            tablaAnimes.setRowSelectionInterval(row, row);
            
            JPopupMenu menu = new JPopupMenu();
            
            JMenuItem itemRemover = new JMenuItem("Quitar de la lista");
            itemRemover.addActionListener(ev -> removerAnimeDeLista());
            menu.add(itemRemover);
            
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    public void mostrarDialogoNuevaLista() {
        JTextField txtNombre = new JTextField(20);
        JTextField txtDescripcion = new JTextField(30);
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Descripción:"));
        panel.add(txtDescripcion);
        
        int resultado = JOptionPane.showConfirmDialog(this, panel, 
            "Nueva Lista Personalizada", JOptionPane.OK_CANCEL_OPTION);
        
        if (resultado == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "El nombre no puede estar vacío", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                listaService.crearLista(nombre, descripcion);
                JOptionPane.showMessageDialog(this, "Lista creada correctamente");
                refrescar();
            } catch (AnimeYaExistenteException e) {
                JOptionPane.showMessageDialog(this, 
                    "Ya existe una lista con ese nombre", "Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al crear lista: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void renombrarListaSeleccionada() {
        ListaPersonalizada lista = listaListas.getSelectedValue();
        if (lista == null) return;
        
        String nuevoNombre = JOptionPane.showInputDialog(this, 
            "Nuevo nombre para la lista:", lista.getNombre());
        
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            try {
                listaService.actualizarLista(lista.getNombre(), nuevoNombre.trim(), lista.getDescripcion());
                refrescar();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al renombrar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void eliminarListaSeleccionada() {
        ListaPersonalizada lista = listaListas.getSelectedValue();
        if (lista == null) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione una lista", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar la lista '" + lista.getNombre() + "'?\nLos anime no se eliminarán del catálogo.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                listaService.eliminarLista(lista.getNombre());
                refrescar();
                tableModel.setAnimes(new ArrayList<>());
                lblInfoLista.setText("Seleccione una lista");
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void mostrarAnimesDeListaSeleccionada() {
        ListaPersonalizada lista = listaListas.getSelectedValue();
        if (lista == null) {
            tableModel.setAnimes(new ArrayList<>());
            lblInfoLista.setText("Seleccione una lista");
            return;
        }
        
        try {
            List<AnimeBase> animes = listaService.obtenerAnimesDeListat(lista.getNombre());
            tableModel.setAnimes(animes);
            lblInfoLista.setText(String.format("Lista: %s (%d anime%s) - %s", 
                lista.getNombre(), animes.size(), 
                animes.size() != 1 ? "s" : "",
                lista.getDescripcion()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar anime: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void agregarAnimeALista() {
        ListaPersonalizada lista = listaListas.getSelectedValue();
        if (lista == null) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione una lista primero", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            List<AnimeBase> todosLosAnimes = animeService.listarOrdenadosPorTitulo();
            if (todosLosAnimes.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No hay anime en el catálogo", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            AnimeBase anime = (AnimeBase) JOptionPane.showInputDialog(
                this,
                "Seleccione un anime para agregar a '" + lista.getNombre() + "':",
                "Agregar anime a lista",
                JOptionPane.QUESTION_MESSAGE,
                null,
                todosLosAnimes.toArray(),
                todosLosAnimes.get(0));
            
            if (anime != null) {
                boolean agregado = listaService.agregarAnimeALista(lista.getNombre(), anime.getTitulo());
                if (agregado) {
                    JOptionPane.showMessageDialog(this, "Anime agregado a la lista");
                    mostrarAnimesDeListaSeleccionada();
                } else {
                    JOptionPane.showMessageDialog(this, "El anime ya está en la lista");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removerAnimeDeLista() {
        ListaPersonalizada lista = listaListas.getSelectedValue();
        if (lista == null) return;
        
        int row = tablaAnimes.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un anime para quitar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        AnimeBase anime = tableModel.getAnimeAt(row);
        
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Quitar '" + anime.getTitulo() + "' de la lista?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                listaService.removerAnimeDeLista(lista.getNombre(), anime.getTitulo());
                mostrarAnimesDeListaSeleccionada();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void refrescar() {
        try {
            List<ListaPersonalizada> listas = listaService.listarTodas();
            listModel.clear();
            for (ListaPersonalizada lista : listas) {
                listModel.addElement(lista);
            }
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar listas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Renderer personalizado para mostrar listas en el JList.
     */
    private static class ListaCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof ListaPersonalizada) {
                ListaPersonalizada lista = (ListaPersonalizada) value;
                setText(String.format("📋 %s (%d)", lista.getNombre(), lista.getCantidadAnimes()));
                setToolTipText(lista.getDescripcion());
            }
            
            return this;
        }
    }
    
    /**
     * Modelo de tabla simplificado para mostrar anime.
     */
    private static class AnimeTableModel extends AbstractTableModel {
        private final String[] columnas = {"Título", "Año", "Estado", "★"};
        private List<AnimeBase> animes = new ArrayList<>();
        
        public void setAnimes(List<AnimeBase> animes) {
            this.animes = new ArrayList<>(animes);
            fireTableDataChanged();
        }
        
        public AnimeBase getAnimeAt(int row) {
            return row >= 0 && row < animes.size() ? animes.get(row) : null;
        }
        
        @Override public int getRowCount() { return animes.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int col) { return columnas[col]; }
        
        @Override
        public Object getValueAt(int row, int col) {
            AnimeBase a = animes.get(row);
            switch (col) {
                case 0: return a.getTitulo();
                case 1: return a.getAnioLanzamiento();
                case 2: return a.getEstado().getDescripcion();
                case 3: return a.tieneCalificacion() ? a.getCalificacion() : "-";
                default: return "";
            }
        }
    }
    
    /**
     * Renderer para mostrar calificaciones como estrellas amarillas.
     */
    static class StarRatingRenderer extends JPanel implements TableCellRenderer {
        private int calificacion = 0;
        private static final Color STAR_FILLED = new Color(255, 200, 50);   // Amarillo dorado
        private static final Color STAR_EMPTY = new Color(200, 200, 200);   // Gris claro
        private static final Color STAR_BORDER = new Color(180, 140, 20);   // Borde dorado oscuro
        
        public StarRatingRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (value instanceof Integer) {
                calificacion = (Integer) value;
            } else if (value instanceof String && !"-".equals(value)) {
                try {
                    calificacion = Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    calificacion = 0;
                }
            } else {
                calificacion = 0;
            }
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            
            return this;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int starSize = 14;
            int spacing = 2;
            int totalWidth = 5 * starSize + 4 * spacing;
            int startX = (getWidth() - totalWidth) / 2;
            int startY = (getHeight() - starSize) / 2;
            
            for (int i = 0; i < 5; i++) {
                int x = startX + i * (starSize + spacing);
                if (i < calificacion) {
                    drawStar(g2, x, startY, starSize, STAR_FILLED, STAR_BORDER);
                } else {
                    drawStar(g2, x, startY, starSize, STAR_EMPTY, new Color(160, 160, 160));
                }
            }
            
            g2.dispose();
        }
        
        private void drawStar(Graphics2D g2, int x, int y, int size, Color fill, Color border) {
            int[] xPoints = new int[10];
            int[] yPoints = new int[10];
            double angle = -Math.PI / 2;
            double deltaAngle = Math.PI / 5;
            int outerRadius = size / 2;
            int innerRadius = size / 5;
            int centerX = x + size / 2;
            int centerY = y + size / 2;
            
            for (int i = 0; i < 10; i++) {
                int radius = (i % 2 == 0) ? outerRadius : innerRadius;
                xPoints[i] = centerX + (int)(radius * Math.cos(angle));
                yPoints[i] = centerY + (int)(radius * Math.sin(angle));
                angle += deltaAngle;
            }
            
            g2.setColor(fill);
            g2.fillPolygon(xPoints, yPoints, 10);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1f));
            g2.drawPolygon(xPoints, yPoints, 10);
        }
    }
}

