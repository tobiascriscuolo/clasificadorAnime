package vista;

import modelo.*;
import servicio.*;
import excepcion.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Panel para gestionar listas personalizadas de anime.
 */
public class PanelListas extends JPanel {
    
    private final ServicioListaPersonalizada servicioLista;
    private final ServicioAnime servicioAnime;
    
    private JList<ListaPersonalizada> listaListas;
    private DefaultListModel<ListaPersonalizada> modeloLista;
    private JTable tablaAnimes;
    private ModeloTablaAnime modeloTabla;
    private JLabel lblInfoLista;
    
    public PanelListas(ServicioListaPersonalizada servicioLista, ServicioAnime servicioAnime) {
        this.servicioLista = servicioLista;
        this.servicioAnime = servicioAnime;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        crearUI();
        refrescar();
    }
    
    private void crearUI() {
        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Mis Listas"));
        panelIzquierdo.setPreferredSize(new Dimension(250, 0));
        
        modeloLista = new DefaultListModel<>();
        listaListas = new JList<>(modeloLista);
        listaListas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaListas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarAnimesDeListaSeleccionada();
            }
        });
        
        JScrollPane scrollListas = new JScrollPane(listaListas);
        panelIzquierdo.add(scrollListas, BorderLayout.CENTER);
        
        JPanel panelBotonesListas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevaLista = new JButton("➕ Nueva");
        btnNuevaLista.addActionListener(e -> mostrarDialogoNuevaLista());
        JButton btnEliminarLista = new JButton("🗑️ Eliminar");
        btnEliminarLista.addActionListener(e -> eliminarListaSeleccionada());
        
        panelBotonesListas.add(btnNuevaLista);
        panelBotonesListas.add(btnEliminarLista);
        panelIzquierdo.add(panelBotonesListas, BorderLayout.SOUTH);
        
        add(panelIzquierdo, BorderLayout.WEST);
        
        JPanel panelCentral = new JPanel(new BorderLayout(5, 5));
        panelCentral.setBorder(BorderFactory.createTitledBorder("Contenido de la Lista"));
        
        lblInfoLista = new JLabel("Seleccione una lista para ver su contenido");
        lblInfoLista.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelCentral.add(lblInfoLista, BorderLayout.NORTH);
        
        modeloTabla = new ModeloTablaAnime();
        tablaAnimes = new JTable(modeloTabla);
        tablaAnimes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAnimes.setRowHeight(25);
        tablaAnimes.getColumnModel().getColumn(3).setCellRenderer(new PanelAnime.RenderizadorEstrellas());
        
        JScrollPane scrollAnimes = new JScrollPane(tablaAnimes);
        panelCentral.add(scrollAnimes, BorderLayout.CENTER);
        
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
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                servicioLista.crearLista(nombre, descripcion);
                JOptionPane.showMessageDialog(this, "Lista creada correctamente");
                refrescar();
            } catch (ExcepcionAnimeYaExistente e) {
                JOptionPane.showMessageDialog(this, "Ya existe una lista con ese nombre", "Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al crear lista: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void eliminarListaSeleccionada() {
        ListaPersonalizada lista = listaListas.getSelectedValue();
        if (lista == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una lista", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar la lista '" + lista.obtenerNombre() + "'?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                servicioLista.eliminarLista(lista.obtenerNombre());
                refrescar();
                modeloTabla.establecerAnimes(new ArrayList<>());
                lblInfoLista.setText("Seleccione una lista");
            } catch (ExcepcionPersistencia e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void mostrarAnimesDeListaSeleccionada() {
        ListaPersonalizada lista = listaListas.getSelectedValue();
        if (lista == null) {
            modeloTabla.establecerAnimes(new ArrayList<>());
            lblInfoLista.setText("Seleccione una lista");
            return;
        }
        
        try {
            List<AnimeBase> animes = servicioLista.obtenerAnimesDeListat(lista.obtenerNombre());
            modeloTabla.establecerAnimes(animes);
            lblInfoLista.setText(String.format("Lista: %s (%d anime%s) - %s", 
                lista.obtenerNombre(), animes.size(), animes.size() != 1 ? "s" : "", lista.obtenerDescripcion()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar anime: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void agregarAnimeALista() {
        ListaPersonalizada lista = listaListas.getSelectedValue();
        if (lista == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una lista primero", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            List<AnimeBase> todosLosAnimes = servicioAnime.listarOrdenadosPorTitulo();
            if (todosLosAnimes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay anime en el catálogo", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            AnimeBase anime = (AnimeBase) JOptionPane.showInputDialog(this,
                "Seleccione un anime para agregar a '" + lista.obtenerNombre() + "':",
                "Agregar anime a lista", JOptionPane.QUESTION_MESSAGE,
                null, todosLosAnimes.toArray(), todosLosAnimes.get(0));
            
            if (anime != null) {
                boolean agregado = servicioLista.agregarAnimeALista(lista.obtenerNombre(), anime.obtenerTitulo());
                if (agregado) {
                    JOptionPane.showMessageDialog(this, "Anime agregado a la lista");
                    mostrarAnimesDeListaSeleccionada();
                } else {
                    JOptionPane.showMessageDialog(this, "El anime ya está en la lista");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removerAnimeDeLista() {
        ListaPersonalizada lista = listaListas.getSelectedValue();
        if (lista == null) return;
        
        int fila = tablaAnimes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un anime para quitar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        AnimeBase anime = modeloTabla.obtenerAnimeEn(fila);
        
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Quitar '" + anime.obtenerTitulo() + "' de la lista?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                servicioLista.removerAnimeDeLista(lista.obtenerNombre(), anime.obtenerTitulo());
                mostrarAnimesDeListaSeleccionada();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void refrescar() {
        try {
            List<ListaPersonalizada> listas = servicioLista.listarTodas();
            modeloLista.clear();
            for (ListaPersonalizada lista : listas) {
                modeloLista.addElement(lista);
            }
        } catch (ExcepcionPersistencia e) {
            JOptionPane.showMessageDialog(this, "Error al cargar listas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static class ModeloTablaAnime extends AbstractTableModel {
        private final String[] columnas = {"Título", "Año", "Estado", "★"};
        private List<AnimeBase> animes = new ArrayList<>();
        
        public void establecerAnimes(List<AnimeBase> animes) {
            this.animes = new ArrayList<>(animes);
            fireTableDataChanged();
        }
        
        public AnimeBase obtenerAnimeEn(int fila) {
            return fila >= 0 && fila < animes.size() ? animes.get(fila) : null;
        }
        
        @Override public int getRowCount() { return animes.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int col) { return columnas[col]; }
        
        @Override
        public Object getValueAt(int fila, int col) {
            AnimeBase a = animes.get(fila);
            switch (col) {
                case 0: return a.obtenerTitulo();
                case 1: return a.obtenerAnioLanzamiento();
                case 2: return a.obtenerEstado().obtenerDescripcion();
                case 3: return a.tieneCalificacion() ? a.obtenerCalificacion() : "-";
                default: return "";
            }
        }
    }
}

