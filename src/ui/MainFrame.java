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
        tabbedPane.setUI(new RoundedTabbedPaneUI());
        tabbedPane.setOpaque(false);
        
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
        String mensaje = "Sistema de Clasificación de Animé\n" +
            "Versión 1.0\n" +
            "\n" +
            "Trabajo Práctico Final\n" +
            "\n" +
            "Desarrollado aplicando:\n" +
            "• Principios SOLID\n" +
            "• Patrones GRASP\n" +
            "• Arquitectura MVC por capas\n" +
            "\n" +
            "Java + Swing";
        
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
     * Configura el tema Wisteria de la aplicación.
     * Paleta: Violetas suaves + Grises claros
     */
    private static void configurarTemaOscuro() {
        // === FUENTE ESTILO GILL SANS ===
        Font fuenteBase = new Font("Gill Sans MT", Font.PLAIN, 13);
        Font fuenteBold = new Font("Gill Sans MT", Font.BOLD, 13);
        Font fuenteGrande = new Font("Gill Sans MT", Font.PLAIN, 14);
        
        // Fallback si Gill Sans no está disponible
        if (!isFontAvailable("Gill Sans MT")) {
            fuenteBase = new Font("Segoe UI", Font.PLAIN, 13);
            fuenteBold = new Font("Segoe UI", Font.BOLD, 13);
            fuenteGrande = new Font("Segoe UI", Font.PLAIN, 14);
        }
        
        // Aplicar fuente globalmente
        UIManager.put("Button.font", fuenteBase);
        UIManager.put("Label.font", fuenteBase);
        UIManager.put("TextField.font", fuenteBase);
        UIManager.put("TextArea.font", fuenteBase);
        UIManager.put("ComboBox.font", fuenteBase);
        UIManager.put("List.font", fuenteBase);
        UIManager.put("Table.font", fuenteBase);
        UIManager.put("TableHeader.font", fuenteBold);
        UIManager.put("TabbedPane.font", fuenteGrande);
        UIManager.put("Menu.font", fuenteBase);
        UIManager.put("MenuItem.font", fuenteBase);
        UIManager.put("MenuBar.font", fuenteBase);
        UIManager.put("OptionPane.messageFont", fuenteBase);
        UIManager.put("OptionPane.buttonFont", fuenteBase);
        UIManager.put("ToolTip.font", fuenteBase);
        UIManager.put("CheckBox.font", fuenteBase);
        UIManager.put("RadioButton.font", fuenteBase);
        UIManager.put("Spinner.font", fuenteBase);
        
        // === PALETA WISTERIA ===
        // Grises base (de más claro a más oscuro)
        Color grisMuyClaro = new Color(250, 248, 252);    // Fondo principal
        Color grisClaro = new Color(240, 236, 244);       // Fondo secundario
        Color grisMedio = new Color(220, 215, 228);       // Bordes suaves
        Color grisOscuro = new Color(90, 85, 100);        // Texto secundario
        
        // Violetas Wisteria (de más claro a más intenso)
        Color wisteriaClaro = new Color(230, 220, 245);   // Hover suave
        Color wisteriaMedio = new Color(200, 170, 220);   // Elementos destacados
        Color wisteriaIntenso = new Color(160, 120, 190); // Selección
        Color wisteriaOscuro = new Color(120, 80, 160);   // Acentos fuertes
        
        // Texto
        Color textoOscuro = new Color(60, 50, 70);        // Texto principal
        Color textoBlanco = new Color(255, 255, 255);     // Texto sobre violeta
        
        // Configurar colores globales
        UIManager.put("Panel.background", grisMuyClaro);
        UIManager.put("Panel.foreground", textoOscuro);
        
        UIManager.put("Label.foreground", textoOscuro);
        UIManager.put("Label.background", grisMuyClaro);
        
        // Botones con estilo wisteria
        UIManager.put("Button.background", wisteriaClaro);
        UIManager.put("Button.foreground", textoOscuro);
        UIManager.put("Button.focus", wisteriaClaro);
        UIManager.put("Button.select", wisteriaMedio);
        
        // Campos de texto
        UIManager.put("TextField.background", textoBlanco);
        UIManager.put("TextField.foreground", textoOscuro);
        UIManager.put("TextField.caretForeground", wisteriaOscuro);
        UIManager.put("TextField.selectionBackground", wisteriaMedio);
        UIManager.put("TextField.selectionForeground", textoBlanco);
        
        UIManager.put("TextArea.background", textoBlanco);
        UIManager.put("TextArea.foreground", textoOscuro);
        UIManager.put("TextArea.selectionBackground", wisteriaMedio);
        UIManager.put("TextArea.selectionForeground", textoBlanco);
        
        // ComboBox
        UIManager.put("ComboBox.background", textoBlanco);
        UIManager.put("ComboBox.foreground", textoOscuro);
        UIManager.put("ComboBox.selectionBackground", wisteriaIntenso);
        UIManager.put("ComboBox.selectionForeground", textoBlanco);
        UIManager.put("ComboBox.buttonBackground", wisteriaClaro);
        
        // Listas
        UIManager.put("List.background", textoBlanco);
        UIManager.put("List.foreground", textoOscuro);
        UIManager.put("List.selectionBackground", wisteriaIntenso);
        UIManager.put("List.selectionForeground", textoBlanco);
        
        // Tablas
        UIManager.put("Table.background", textoBlanco);
        UIManager.put("Table.foreground", textoOscuro);
        UIManager.put("Table.selectionBackground", wisteriaIntenso);
        UIManager.put("Table.selectionForeground", textoBlanco);
        UIManager.put("Table.gridColor", grisMedio);
        UIManager.put("TableHeader.background", wisteriaClaro);
        UIManager.put("TableHeader.foreground", textoOscuro);
        
        // Pestañas - Estilo limpio sin bordes punteados
        UIManager.put("TabbedPane.background", grisClaro);
        UIManager.put("TabbedPane.foreground", textoOscuro);
        UIManager.put("TabbedPane.selected", textoBlanco);
        UIManager.put("TabbedPane.contentAreaColor", grisMuyClaro);
        UIManager.put("TabbedPane.focus", grisClaro);
        UIManager.put("TabbedPane.selectHighlight", wisteriaMedio);
        UIManager.put("TabbedPane.darkShadow", grisMedio);
        UIManager.put("TabbedPane.shadow", grisClaro);
        UIManager.put("TabbedPane.light", textoBlanco);
        UIManager.put("TabbedPane.highlight", wisteriaClaro);
        UIManager.put("TabbedPane.selectedForeground", wisteriaOscuro);
        
        // ScrollPane y ScrollBar
        UIManager.put("ScrollPane.background", grisMuyClaro);
        UIManager.put("ScrollBar.background", grisClaro);
        UIManager.put("ScrollBar.thumb", wisteriaMedio);
        UIManager.put("ScrollBar.thumbHighlight", wisteriaClaro);
        UIManager.put("ScrollBar.track", grisClaro);
        
        // Menu con estilo elegante
        UIManager.put("MenuBar.background", wisteriaClaro);
        UIManager.put("MenuBar.foreground", textoOscuro);
        UIManager.put("Menu.background", wisteriaClaro);
        UIManager.put("Menu.foreground", textoOscuro);
        UIManager.put("Menu.selectionBackground", wisteriaIntenso);
        UIManager.put("Menu.selectionForeground", textoBlanco);
        UIManager.put("MenuItem.background", grisMuyClaro);
        UIManager.put("MenuItem.foreground", textoOscuro);
        UIManager.put("MenuItem.selectionBackground", wisteriaIntenso);
        UIManager.put("MenuItem.selectionForeground", textoBlanco);
        UIManager.put("PopupMenu.background", grisMuyClaro);
        UIManager.put("PopupMenu.foreground", textoOscuro);
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(grisMedio));
        
        // Diálogos
        UIManager.put("OptionPane.background", grisMuyClaro);
        UIManager.put("OptionPane.messageForeground", textoOscuro);
        
        // Spinner
        UIManager.put("Spinner.background", textoBlanco);
        UIManager.put("Spinner.foreground", textoOscuro);
        
        // CheckBox
        UIManager.put("CheckBox.background", grisMuyClaro);
        UIManager.put("CheckBox.foreground", textoOscuro);
        UIManager.put("CheckBox.focus", wisteriaClaro);
        
        // Viewport
        UIManager.put("Viewport.background", grisMuyClaro);
        UIManager.put("Viewport.foreground", textoOscuro);
        
        // Separadores
        UIManager.put("Separator.foreground", grisMedio);
        UIManager.put("Separator.background", grisClaro);
        
        // ToolTips
        UIManager.put("ToolTip.background", wisteriaClaro);
        UIManager.put("ToolTip.foreground", textoOscuro);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(wisteriaMedio));
        
        // Progress Bar
        UIManager.put("ProgressBar.background", grisClaro);
        UIManager.put("ProgressBar.foreground", wisteriaIntenso);
        UIManager.put("ProgressBar.selectionBackground", textoBlanco);
        UIManager.put("ProgressBar.selectionForeground", wisteriaOscuro);
        
        // Quitar bordes de focus en todos los componentes
        UIManager.put("Button.focusPainted", false);
        UIManager.put("ToggleButton.focusPainted", false);
        UIManager.put("RadioButton.focusPainted", false);
        UIManager.put("CheckBox.focusPainted", false);
        
        // === BORDES REDONDEADOS ===
        int radio = 10; // Radio de las esquinas
        
        // Borde redondeado para campos de texto
        UIManager.put("TextField.border", new RoundedBorder(grisMedio, radio));
        UIManager.put("TextArea.border", new RoundedBorder(grisMedio, radio));
        UIManager.put("PasswordField.border", new RoundedBorder(grisMedio, radio));
        
        // Borde redondeado para ComboBox
        UIManager.put("ComboBox.border", new RoundedBorder(grisMedio, radio));
        
        // Borde redondeado para Spinner
        UIManager.put("Spinner.border", new RoundedBorder(grisMedio, radio));
        
        // Borde redondeado para ScrollPane
        UIManager.put("ScrollPane.border", new RoundedBorder(grisMedio, radio));
        
        // Borde redondeado para tablas
        UIManager.put("Table.scrollPaneBorder", new RoundedBorder(grisMedio, radio));
        
        // Borde redondeado para listas
        UIManager.put("List.border", new RoundedBorder(grisMedio, radio));
        
        // Bordes redondeados para botones
        UIManager.put("Button.border", new RoundedBorder(wisteriaMedio, radio));
        
        // ToolTip redondeado
        UIManager.put("ToolTip.border", new RoundedBorder(wisteriaMedio, 8));
    }
    
    /**
     * Verifica si una fuente está disponible en el sistema.
     */
    private static boolean isFontAvailable(String fontName) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = ge.getAvailableFontFamilyNames();
        for (String font : fonts) {
            if (font.equalsIgnoreCase(fontName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * UI personalizado para pestañas con esquinas suavemente redondeadas.
     */
    static class RoundedTabbedPaneUI extends javax.swing.plaf.basic.BasicTabbedPaneUI {
        private final int radio = 15;
        private final Color wisteriaClaro = new Color(230, 220, 245);
        private final Color wisteriaMedio = new Color(200, 170, 220);
        private final Color wisteriaOscuro = new Color(120, 80, 160);
        private final Color fondoTab = new Color(250, 248, 252);
        private final Color textoOscuro = new Color(60, 50, 70);
        
        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabAreaInsets = new Insets(6, 10, 0, 10);
            contentBorderInsets = new Insets(0, 0, 0, 0);
            tabInsets = new Insets(10, 18, 10, 18);
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
        }
        
        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, 
                int x, int y, int w, int h, boolean isSelected) {
            // No pintar borde, lo hacemos en paintTabBackground
        }
        
        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (isSelected) {
                // Pestaña seleccionada - conectada con el contenido
                int tabH = h + 15;
                
                // Fondo de la pestaña (se extiende hacia abajo)
                g2.setColor(fondoTab);
                g2.fillRoundRect(x + 2, y, w - 4, tabH, radio, radio);
                
                // Borde solo en la parte superior y lados (no abajo)
                g2.setColor(wisteriaMedio);
                g2.setStroke(new BasicStroke(2f));
                // Dibujar arco superior izquierdo
                g2.drawArc(x + 2, y, radio, radio, 90, 90);
                // Línea superior
                g2.drawLine(x + 2 + radio/2, y, x + w - 4 - radio/2, y);
                // Dibujar arco superior derecho
                g2.drawArc(x + w - 4 - radio, y, radio, radio, 0, 90);
                // Línea izquierda (hasta donde empieza el contenido)
                g2.drawLine(x + 2, y + radio/2, x + 2, y + tabH - 5);
                // Línea derecha (hasta donde empieza el contenido)
                g2.drawLine(x + w - 4, y + radio/2, x + w - 4, y + tabH - 5);
                
            } else {
                // Pestaña no seleccionada - despegada (más arriba y más corta)
                int despegue = 6;
                g2.setColor(wisteriaClaro);
                g2.fillRoundRect(x + 4, y + despegue, w - 8, h - despegue - 4, radio, radio);
                g2.setColor(wisteriaMedio);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(x + 4, y + despegue, w - 8, h - despegue - 4, radio, radio);
            }
            
            g2.dispose();
        }
        
        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font, 
                FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Mismo tamaño de fuente para evitar desplazamiento
            g2.setFont(font);
            
            if (isSelected) {
                g2.setColor(wisteriaOscuro);
            } else {
                g2.setColor(textoOscuro);
            }
            
            g2.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            g2.dispose();
        }
        
        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int x = 0;
            int y = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight) - 2;
            int w = tabPane.getWidth();
            int h = tabPane.getHeight() - y;
            
            // Fondo del contenido
            g2.setColor(fondoTab);
            g2.fillRoundRect(x, y, w, h, radio, radio);
            
            // Obtener posición de la pestaña seleccionada
            Rectangle tabRect = null;
            if (selectedIndex >= 0 && selectedIndex < tabPane.getTabCount()) {
                tabRect = getTabBounds(tabPane, selectedIndex);
            }
            
            g2.setColor(wisteriaMedio);
            g2.setStroke(new BasicStroke(2f));
            
            // Dibujar borde SIN la parte superior donde está la pestaña
            // Línea izquierda
            g2.drawLine(x, y + radio/2, x, y + h - radio);
            // Arco inferior izquierdo
            g2.drawArc(x, y + h - radio - 1, radio, radio, 180, 90);
            // Línea inferior
            g2.drawLine(x + radio/2, y + h - 1, x + w - radio/2 - 1, y + h - 1);
            // Arco inferior derecho
            g2.drawArc(x + w - radio - 1, y + h - radio - 1, radio, radio, 270, 90);
            // Línea derecha
            g2.drawLine(x + w - 1, y + h - radio, x + w - 1, y + radio/2);
            
            // Línea superior - solo las partes fuera de la pestaña seleccionada
            if (tabRect != null) {
                // Arco superior izquierdo
                g2.drawArc(x, y, radio, radio, 90, 90);
                // Línea desde arco hasta pestaña
                if (tabRect.x > x + radio/2) {
                    g2.drawLine(x + radio/2, y, tabRect.x + 2, y);
                }
                // Línea desde pestaña hasta arco derecho
                if (tabRect.x + tabRect.width < x + w - radio/2) {
                    g2.drawLine(tabRect.x + tabRect.width - 4, y, x + w - radio/2 - 1, y);
                }
                // Arco superior derecho
                g2.drawArc(x + w - radio - 1, y, radio, radio, 0, 90);
            }
            
            g2.dispose();
        }
        
        @Override
        protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, 
                int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
            // No pintar indicador de foco (quita las líneas punteadas)
        }
        
        @Override
        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + 8;
        }
    }
    
    /**
     * Borde redondeado personalizado para componentes Swing.
     */
    static class RoundedBorder implements javax.swing.border.Border {
        private Color color;
        private int radio;
        
        public RoundedBorder(Color color, int radio) {
            this.color = color;
            this.radio = radio;
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radio/2 + 2, radio/2 + 4, radio/2 + 2, radio/2 + 4);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radio, radio);
            g2.dispose();
        }
    }
    
    /**
     * Punto de entrada de la aplicación.
     */
    public static void main(String[] args) {
        // Configurar tema oscuro
        configurarTemaOscuro();
        
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

