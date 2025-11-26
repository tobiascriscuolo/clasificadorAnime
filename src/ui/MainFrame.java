package ui;

import service.*;
import repository.*;
import exception.PersistenciaException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal de la aplicación.
 * 
 * GRASP - Controller: Actúa como punto de entrada de la UI, pero delega
 * la lógica de negocio a los servicios.
 * 
 * SOLID - SRP: Solo se encarga de la estructura principal de la UI.
 * 
 * MVC: Esta clase es parte de la Vista, se comunica solo con los servicios (Controller).
 */
public class MainFrame extends JFrame {
    
    private static final String TITULO = "Sistema de Clasificación de Animé";
    private static final int ANCHO = 1200;
    private static final int ALTO = 800;
    
    // Servicios (inyectados en constructor)
    private final AnimeService animeService;
    private final ListaPersonalizadaService listaService;
    private final RecomendacionService recomendacionService;
    private final EstadisticasService estadisticasService;
    
    // Paneles principales
    private JTabbedPane tabbedPane;
    private AnimePanel animePanel;
    private ListasPanel listasPanel;
    private RecomendacionesPanel recomendacionesPanel;
    private EstadisticasPanel estadisticasPanel;
    
    /**
     * Constructor con inyección de dependencias de servicios.
     * 
     * SOLID - DIP: La UI depende de servicios abstractos, no de implementaciones.
     */
    public MainFrame(AnimeService animeService, 
                     ListaPersonalizadaService listaService,
                     RecomendacionService recomendacionService,
                     EstadisticasService estadisticasService) {
        
        this.animeService = animeService;
        this.listaService = listaService;
        this.recomendacionService = recomendacionService;
        this.estadisticasService = estadisticasService;
        
        configurarVentana();
        crearMenuBar();
        crearContenido();
        configurarEventos();
    }
    
    private void configurarVentana() {
        setTitle(TITULO);
        setSize(ANCHO, ALTO);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Icono personalizado (si existe)
        try {
            // Usar un icono por defecto del sistema
            setIconImage(UIManager.getIcon("FileView.computerIcon").toString() != null ? 
                null : null);
        } catch (Exception e) {
            // Ignorar si no hay icono
        }
    }
    
    private void crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setMnemonic('A');
        
        JMenuItem itemGuardar = new JMenuItem("Guardar cambios");
        itemGuardar.setAccelerator(KeyStroke.getKeyStroke("control S"));
        itemGuardar.addActionListener(e -> guardarCambios());
        
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setAccelerator(KeyStroke.getKeyStroke("alt F4"));
        itemSalir.addActionListener(e -> confirmarSalida());
        
        menuArchivo.add(itemGuardar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        
        // Menú Anime
        JMenu menuAnime = new JMenu("Animé");
        menuAnime.setMnemonic('n');
        
        JMenuItem itemNuevaSerie = new JMenuItem("Nueva Serie...");
        itemNuevaSerie.addActionListener(e -> animePanel.mostrarDialogoNuevaSerie());
        
        JMenuItem itemNuevaPelicula = new JMenuItem("Nueva Película...");
        itemNuevaPelicula.addActionListener(e -> animePanel.mostrarDialogoNuevaPelicula());
        
        menuAnime.add(itemNuevaSerie);
        menuAnime.add(itemNuevaPelicula);
        
        // Menú Listas
        JMenu menuListas = new JMenu("Listas");
        menuListas.setMnemonic('L');
        
        JMenuItem itemNuevaLista = new JMenuItem("Nueva Lista...");
        itemNuevaLista.addActionListener(e -> listasPanel.mostrarDialogoNuevaLista());
        
        menuListas.add(itemNuevaLista);
        
        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setMnemonic('y');
        
        JMenuItem itemAcercaDe = new JMenuItem("Acerca de...");
        itemAcercaDe.addActionListener(e -> mostrarAcercaDe());
        
        menuAyuda.add(itemAcercaDe);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuAnime);
        menuBar.add(menuListas);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuAyuda);
        
        setJMenuBar(menuBar);
    }
    
    private void crearContenido() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Crear paneles
        animePanel = new AnimePanel(animeService, listaService);
        listasPanel = new ListasPanel(listaService, animeService);
        recomendacionesPanel = new RecomendacionesPanel(recomendacionService, animeService);
        estadisticasPanel = new EstadisticasPanel(estadisticasService);
        
        // Agregar pestañas
        tabbedPane.addTab("🎬 Catálogo de Animé", animePanel);
        tabbedPane.addTab("📋 Listas Personalizadas", listasPanel);
        tabbedPane.addTab("⭐ Recomendaciones", recomendacionesPanel);
        tabbedPane.addTab("📊 Estadísticas", estadisticasPanel);
        
        // Listener para refrescar al cambiar de pestaña
        tabbedPane.addChangeListener(e -> refrescarPestañaActual());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Barra de estado
        JPanel statusBar = crearBarraEstado();
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel crearBarraEstado() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel statusLabel = new JLabel("Sistema de Clasificación de Animé - Listo");
        statusBar.add(statusLabel);
        
        return statusBar;
    }
    
    private void configurarEventos() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });
    }
    
    private void confirmarSalida() {
        int opcion = JOptionPane.showConfirmDialog(
            this,
            "¿Desea guardar los cambios antes de salir?",
            "Confirmar salida",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (opcion == JOptionPane.YES_OPTION) {
            guardarCambios();
            System.exit(0);
        } else if (opcion == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
        // Si es CANCEL, no hacer nada
    }
    
    private void guardarCambios() {
        try {
            // Los cambios se guardan automáticamente, pero podríamos forzar aquí
            JOptionPane.showMessageDialog(
                this,
                "Los cambios se han guardado correctamente.",
                "Guardar",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error al guardar: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void refrescarPestañaActual() {
        int index = tabbedPane.getSelectedIndex();
        switch (index) {
            case 0: animePanel.refrescar(); break;
            case 1: listasPanel.refrescar(); break;
            case 2: recomendacionesPanel.refrescar(); break;
            case 3: estadisticasPanel.refrescar(); break;
        }
    }
    
    private void mostrarAcercaDe() {
        String mensaje = """
            Sistema de Clasificación de Animé
            Versión 1.0
            
            Trabajo Práctico Final
            
            Desarrollado aplicando:
            • Principios SOLID
            • Patrones GRASP
            • Arquitectura MVC por capas
            
            Java + Swing
            """;
        
        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Acerca de",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Refresca todos los paneles.
     * Útil después de operaciones que afectan múltiples vistas.
     */
    public void refrescarTodo() {
        animePanel.refrescar();
        listasPanel.refrescar();
        recomendacionesPanel.refrescar();
        estadisticasPanel.refrescar();
    }
    
    /**
     * Punto de entrada de la aplicación.
     */
    public static void main(String[] args) {
        // Configurar Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Usar L&F por defecto si falla
        }
        
        // Ejecutar en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Crear repositorios
                AnimeRepository animeRepository = new FileAnimeRepository();
                ListaPersonalizadaRepository listaRepository = new FileListaPersonalizadaRepository();
                
                // Crear servicios con inyección de dependencias
                AnimeService animeService = new AnimeService(animeRepository);
                ListaPersonalizadaService listaService = 
                    new ListaPersonalizadaService(listaRepository, animeRepository);
                RecomendacionService recomendacionService = 
                    new RecomendacionService(animeRepository);
                EstadisticasService estadisticasService = 
                    new EstadisticasService(animeRepository);
                
                // Crear y mostrar la ventana principal
                MainFrame mainFrame = new MainFrame(
                    animeService, listaService, recomendacionService, estadisticasService
                );
                mainFrame.setVisible(true);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    null,
                    "Error al iniciar la aplicación: " + e.getMessage(),
                    "Error Fatal",
                    JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}

