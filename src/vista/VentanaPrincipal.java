package vista;

import servicio.*;
import repositorio.*;
import excepcion.ExcepcionPersistencia;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal de la aplicación.
 */
public class VentanaPrincipal extends JFrame {
    
    private static final String TITULO = "Sistema de Clasificación de Animé";
    private static final int ANCHO = 1200;
    private static final int ALTO = 800;
    
    private final ServicioAnime servicioAnime;
    private final ServicioListaPersonalizada servicioLista;
    private final ServicioRecomendacion servicioRecomendacion;
    private final ServicioEstadisticas servicioEstadisticas;
    
    private JTabbedPane pestanias;
    private PanelAnime panelAnime;
    private PanelListas panelListas;
    private PanelRecomendaciones panelRecomendaciones;
    private PanelEstadisticas panelEstadisticas;
    
    public VentanaPrincipal(ServicioAnime servicioAnime, 
                           ServicioListaPersonalizada servicioLista,
                           ServicioRecomendacion servicioRecomendacion,
                           ServicioEstadisticas servicioEstadisticas) {
        
        this.servicioAnime = servicioAnime;
        this.servicioLista = servicioLista;
        this.servicioRecomendacion = servicioRecomendacion;
        this.servicioEstadisticas = servicioEstadisticas;
        
        configurarVentana();
        crearBarraMenu();
        crearContenido();
        configurarEventos();
    }
    
    private void configurarVentana() {
        setTitle(TITULO);
        setSize(ANCHO, ALTO);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void crearBarraMenu() {
        JMenuBar barraMenu = new JMenuBar();
        
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setMnemonic('A');
        
        JMenuItem itemGuardar = new JMenuItem("Guardar cambios");
        itemGuardar.setAccelerator(KeyStroke.getKeyStroke("control S"));
        itemGuardar.addActionListener(e -> guardarCambios());
        
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setAccelerator(KeyStroke.getKeyStroke("alt F4"));
        itemSalir.addActionListener(e -> confirmarSalida());
        
        JMenuItem itemExportar = new JMenuItem("Exportar catálogo a TXT...");
        itemExportar.addActionListener(e -> exportarCatalogo());
        
        JMenuItem itemImportar = new JMenuItem("Importar desde TXT...");
        itemImportar.addActionListener(e -> importarCatalogo());
        
        menuArchivo.add(itemGuardar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemExportar);
        menuArchivo.add(itemImportar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        
        JMenu menuAnime = new JMenu("Animé");
        menuAnime.setMnemonic('n');
        
        JMenuItem itemNuevaSerie = new JMenuItem("Nueva Serie...");
        itemNuevaSerie.addActionListener(e -> panelAnime.mostrarDialogoNuevaSerie());
        
        JMenuItem itemNuevaPelicula = new JMenuItem("Nueva Película...");
        itemNuevaPelicula.addActionListener(e -> panelAnime.mostrarDialogoNuevaPelicula());
        
        menuAnime.add(itemNuevaSerie);
        menuAnime.add(itemNuevaPelicula);
        
        JMenu menuListas = new JMenu("Listas");
        menuListas.setMnemonic('L');
        
        JMenuItem itemNuevaLista = new JMenuItem("Nueva Lista...");
        itemNuevaLista.addActionListener(e -> panelListas.mostrarDialogoNuevaLista());
        
        menuListas.add(itemNuevaLista);
        
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setMnemonic('y');
        
        JMenuItem itemAcercaDe = new JMenuItem("Acerca de...");
        itemAcercaDe.addActionListener(e -> mostrarAcercaDe());
        
        menuAyuda.add(itemAcercaDe);
        
        barraMenu.add(menuArchivo);
        barraMenu.add(menuAnime);
        barraMenu.add(menuListas);
        barraMenu.add(Box.createHorizontalGlue());
        barraMenu.add(menuAyuda);
        
        setJMenuBar(barraMenu);
    }
    
    private void crearContenido() {
        pestanias = new JTabbedPane();
        pestanias.setUI(new UIPestaniasRedondeadas());
        pestanias.setOpaque(false);
        
        panelAnime = new PanelAnime(servicioAnime, servicioLista);
        panelListas = new PanelListas(servicioLista, servicioAnime);
        panelRecomendaciones = new PanelRecomendaciones(servicioRecomendacion, servicioAnime);
        panelEstadisticas = new PanelEstadisticas(servicioEstadisticas);
        
        Icon iconoEstrella = new IconoEstrella(16, new Color(255, 200, 50));
        pestanias.addTab("Catálogo de Animé", iconoEstrella, panelAnime);
        pestanias.addTab("Listas Personalizadas", iconoEstrella, panelListas);
        pestanias.addTab("Recomendaciones", iconoEstrella, panelRecomendaciones);
        pestanias.addTab("Estadísticas", iconoEstrella, panelEstadisticas);
        
        pestanias.addChangeListener(e -> refrescarPestaniaActual());
        
        add(pestanias, BorderLayout.CENTER);
        
        JPanel barraEstado = crearBarraEstado();
        add(barraEstado, BorderLayout.SOUTH);
    }
    
    private JPanel crearBarraEstado() {
        JPanel barraEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barraEstado.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel etiquetaEstado = new JLabel("Sistema de Clasificación de Animé - Listo");
        barraEstado.add(etiquetaEstado);
        
        return barraEstado;
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
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Desea guardar los cambios antes de salir?",
            "Confirmar salida", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            guardarCambios();
            System.exit(0);
        } else if (opcion == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }
    
    private void guardarCambios() {
        JOptionPane.showMessageDialog(this, "Los cambios se han guardado correctamente.",
            "Guardar", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exportarCatalogo() {
        try {
            String contenido = servicioAnime.exportarATxt();
            
            JFileChooser selectorArchivo = new JFileChooser();
            selectorArchivo.setDialogTitle("Exportar catálogo");
            selectorArchivo.setSelectedFile(new java.io.File("catalogo_anime.txt"));
            
            int resultado = selectorArchivo.showSaveDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                java.io.File archivo = selectorArchivo.getSelectedFile();
                if (!archivo.getName().toLowerCase().endsWith(".txt")) {
                    archivo = new java.io.File(archivo.getAbsolutePath() + ".txt");
                }
                
                java.io.FileWriter writer = new java.io.FileWriter(archivo);
                writer.write(contenido);
                writer.close();
                
                JOptionPane.showMessageDialog(this,
                    "Catálogo exportado exitosamente a:\n" + archivo.getAbsolutePath(),
                    "Exportación completada", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void importarCatalogo() {
        JFileChooser selectorArchivo = new JFileChooser();
        selectorArchivo.setDialogTitle("Importar catálogo");
        
        int resultado = selectorArchivo.showOpenDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) return;
        
        java.io.File archivo = selectorArchivo.getSelectedFile();
        
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(archivo));
            String linea;
            int importados = 0, omitidos = 0;
            
            while ((linea = reader.readLine()) != null) {
                modelo.AnimeBase anime = servicioAnime.parsearLineaAnime(linea);
                if (anime != null) {
                    if (!servicioAnime.existeAnime(anime.obtenerTitulo())) {
                        servicioAnime.registrarAnimeDirecto(anime);
                        importados++;
                    } else {
                        omitidos++;
                    }
                }
            }
            reader.close();
            
            refrescarTodo();
            
            JOptionPane.showMessageDialog(this,
                String.format("Importación completada:\n• Importados: %d\n• Omitidos (duplicados): %d", importados, omitidos),
                "Importación", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al importar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refrescarPestaniaActual() {
        int indice = pestanias.getSelectedIndex();
        switch (indice) {
            case 0: panelAnime.refrescar(); break;
            case 1: panelListas.refrescar(); break;
            case 2: panelRecomendaciones.refrescar(); break;
            case 3: panelEstadisticas.refrescar(); break;
        }
    }
    
    private void mostrarAcercaDe() {
        String mensaje = "Sistema de Clasificación de Animé\nVersión 1.0\n\n" +
            "Desarrollado aplicando:\n• Principios SOLID\n• Patrones GRASP\n• Arquitectura MVC por capas\n\nJava + Swing";
        JOptionPane.showMessageDialog(this, mensaje, "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void refrescarTodo() {
        panelAnime.refrescar();
        panelListas.refrescar();
        panelRecomendaciones.refrescar();
        panelEstadisticas.refrescar();
    }
    
    // ========== TEMA WISTERIA ==========
    
    private static void configurarTemaWisteria() {
        Font fuenteBase = new Font("Gill Sans MT", Font.PLAIN, 13);
        Font fuenteBold = new Font("Gill Sans MT", Font.BOLD, 13);
        Font fuenteGrande = new Font("Gill Sans MT", Font.PLAIN, 14);
        
        if (!esFuenteDisponible("Gill Sans MT")) {
            fuenteBase = new Font("Segoe UI", Font.PLAIN, 13);
            fuenteBold = new Font("Segoe UI", Font.BOLD, 13);
            fuenteGrande = new Font("Segoe UI", Font.PLAIN, 14);
        }
        
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
        UIManager.put("Spinner.font", fuenteBase);
        
        // Paleta Wisteria
        Color grisMuyClaro = new Color(250, 248, 252);
        Color grisClaro = new Color(240, 236, 244);
        Color grisMedio = new Color(220, 215, 228);
        Color wisteriaClaro = new Color(230, 220, 245);
        Color wisteriaMedio = new Color(200, 170, 220);
        Color wisteriaIntenso = new Color(160, 120, 190);
        Color wisteriaOscuro = new Color(120, 80, 160);
        Color textoOscuro = new Color(60, 50, 70);
        Color textoBlanco = new Color(255, 255, 255);
        
        UIManager.put("Panel.background", grisMuyClaro);
        UIManager.put("Panel.foreground", textoOscuro);
        UIManager.put("Label.foreground", textoOscuro);
        UIManager.put("Label.background", grisMuyClaro);
        
        UIManager.put("Button.background", wisteriaClaro);
        UIManager.put("Button.foreground", textoOscuro);
        UIManager.put("Button.focus", wisteriaClaro);
        UIManager.put("Button.select", wisteriaMedio);
        
        UIManager.put("TextField.background", textoBlanco);
        UIManager.put("TextField.foreground", textoOscuro);
        UIManager.put("TextField.caretForeground", wisteriaOscuro);
        UIManager.put("TextField.selectionBackground", wisteriaMedio);
        UIManager.put("TextField.selectionForeground", textoBlanco);
        
        UIManager.put("TextArea.background", textoBlanco);
        UIManager.put("TextArea.foreground", textoOscuro);
        UIManager.put("TextArea.selectionBackground", wisteriaMedio);
        UIManager.put("TextArea.selectionForeground", textoBlanco);
        
        UIManager.put("ComboBox.background", textoBlanco);
        UIManager.put("ComboBox.foreground", textoOscuro);
        UIManager.put("ComboBox.selectionBackground", wisteriaIntenso);
        UIManager.put("ComboBox.selectionForeground", textoBlanco);
        UIManager.put("ComboBox.buttonBackground", wisteriaClaro);
        
        UIManager.put("List.background", textoBlanco);
        UIManager.put("List.foreground", textoOscuro);
        UIManager.put("List.selectionBackground", wisteriaIntenso);
        UIManager.put("List.selectionForeground", textoBlanco);
        
        UIManager.put("Table.background", textoBlanco);
        UIManager.put("Table.foreground", textoOscuro);
        UIManager.put("Table.selectionBackground", wisteriaIntenso);
        UIManager.put("Table.selectionForeground", textoBlanco);
        UIManager.put("Table.gridColor", grisMedio);
        UIManager.put("TableHeader.background", wisteriaClaro);
        UIManager.put("TableHeader.foreground", textoOscuro);
        
        UIManager.put("TabbedPane.background", grisClaro);
        UIManager.put("TabbedPane.foreground", textoOscuro);
        UIManager.put("TabbedPane.selected", textoBlanco);
        UIManager.put("TabbedPane.contentAreaColor", grisMuyClaro);
        UIManager.put("TabbedPane.focus", grisClaro);
        UIManager.put("TabbedPane.selectHighlight", wisteriaMedio);
        UIManager.put("TabbedPane.selectedForeground", wisteriaOscuro);
        
        UIManager.put("ScrollPane.background", grisMuyClaro);
        UIManager.put("ScrollBar.background", grisClaro);
        UIManager.put("ScrollBar.thumb", wisteriaMedio);
        UIManager.put("ScrollBar.track", grisClaro);
        
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
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(grisMedio));
        
        UIManager.put("OptionPane.background", grisMuyClaro);
        UIManager.put("OptionPane.messageForeground", textoOscuro);
        
        UIManager.put("Spinner.background", textoBlanco);
        UIManager.put("Spinner.foreground", textoOscuro);
        
        UIManager.put("CheckBox.background", grisMuyClaro);
        UIManager.put("CheckBox.foreground", textoOscuro);
        
        UIManager.put("Viewport.background", grisMuyClaro);
        
        UIManager.put("ToolTip.background", wisteriaClaro);
        UIManager.put("ToolTip.foreground", textoOscuro);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(wisteriaMedio));
        
        UIManager.put("ProgressBar.background", grisClaro);
        UIManager.put("ProgressBar.foreground", wisteriaIntenso);
        
        UIManager.put("Button.focusPainted", false);
        
        int radio = 10;
        UIManager.put("TextField.border", new BordeRedondeado(grisMedio, radio));
        UIManager.put("TextArea.border", new BordeRedondeado(grisMedio, radio));
        UIManager.put("ComboBox.border", new BordeRedondeado(grisMedio, radio));
        UIManager.put("Spinner.border", new BordeRedondeado(grisMedio, radio));
        UIManager.put("ScrollPane.border", new BordeRedondeado(grisMedio, radio));
        UIManager.put("Button.border", new BordeRedondeado(wisteriaMedio, radio));
        UIManager.put("ToolTip.border", new BordeRedondeado(wisteriaMedio, 8));
    }
    
    private static boolean esFuenteDisponible(String nombreFuente) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fuentes = ge.getAvailableFontFamilyNames();
        for (String fuente : fuentes) {
            if (fuente.equalsIgnoreCase(nombreFuente)) {
                return true;
            }
        }
        return false;
    }
    
    // ========== UI PESTAÑAS REDONDEADAS ==========
    
    static class UIPestaniasRedondeadas extends javax.swing.plaf.basic.BasicTabbedPaneUI {
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
        }
        
        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (isSelected) {
                int tabH = h + 15;
                g2.setColor(fondoTab);
                g2.fillRoundRect(x + 2, y, w - 4, tabH, radio, radio);
                
                g2.setColor(wisteriaMedio);
                g2.setStroke(new BasicStroke(2f));
                g2.drawArc(x + 2, y, radio, radio, 90, 90);
                g2.drawLine(x + 2 + radio/2, y, x + w - 4 - radio/2, y);
                g2.drawArc(x + w - 4 - radio, y, radio, radio, 0, 90);
                g2.drawLine(x + 2, y + radio/2, x + 2, y + tabH - 5);
                g2.drawLine(x + w - 4, y + radio/2, x + w - 4, y + tabH - 5);
            } else {
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
            g2.setFont(font);
            g2.setColor(isSelected ? wisteriaOscuro : textoOscuro);
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
            
            g2.setColor(fondoTab);
            g2.fillRoundRect(x, y, w, h, radio, radio);
            
            Rectangle tabRect = null;
            if (selectedIndex >= 0 && selectedIndex < tabPane.getTabCount()) {
                tabRect = getTabBounds(tabPane, selectedIndex);
            }
            
            g2.setColor(wisteriaMedio);
            g2.setStroke(new BasicStroke(2f));
            
            g2.drawLine(x, y + radio/2, x, y + h - radio);
            g2.drawArc(x, y + h - radio - 1, radio, radio, 180, 90);
            g2.drawLine(x + radio/2, y + h - 1, x + w - radio/2 - 1, y + h - 1);
            g2.drawArc(x + w - radio - 1, y + h - radio - 1, radio, radio, 270, 90);
            g2.drawLine(x + w - 1, y + h - radio, x + w - 1, y + radio/2);
            
            if (tabRect != null) {
                g2.drawArc(x, y, radio, radio, 90, 90);
                if (tabRect.x > x + radio/2) {
                    g2.drawLine(x + radio/2, y, tabRect.x + 2, y);
                }
                if (tabRect.x + tabRect.width < x + w - radio/2) {
                    g2.drawLine(tabRect.x + tabRect.width - 4, y, x + w - radio/2 - 1, y);
                }
                g2.drawArc(x + w - radio - 1, y, radio, radio, 0, 90);
            }
            
            g2.dispose();
        }
        
        @Override
        protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, 
                int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        }
        
        @Override
        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + 8;
        }
    }
    
    // ========== ICONO ESTRELLA ==========
    
    static class IconoEstrella implements Icon {
        private int tamanio;
        private Color color;
        
        public IconoEstrella(int tamanio, Color color) {
            this.tamanio = tamanio;
            this.color = color;
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int[] puntosX = new int[10];
            int[] puntosY = new int[10];
            double angulo = -Math.PI / 2;
            double deltaAngulo = Math.PI / 5;
            int radioExterno = tamanio / 2;
            int radioInterno = tamanio / 5;
            int centroX = x + tamanio / 2;
            int centroY = y + tamanio / 2;
            
            for (int i = 0; i < 10; i++) {
                int radio = (i % 2 == 0) ? radioExterno : radioInterno;
                puntosX[i] = centroX + (int)(radio * Math.cos(angulo));
                puntosY[i] = centroY + (int)(radio * Math.sin(angulo));
                angulo += deltaAngulo;
            }
            
            g2.setColor(new Color(180, 140, 20));
            g2.fillPolygon(puntosX, puntosY, 10);
            
            angulo = -Math.PI / 2;
            for (int i = 0; i < 10; i++) {
                int radio = (i % 2 == 0) ? radioExterno - 2 : radioInterno;
                puntosX[i] = centroX + (int)(radio * Math.cos(angulo));
                puntosY[i] = centroY + (int)(radio * Math.sin(angulo));
                angulo += deltaAngulo;
            }
            g2.setColor(color);
            g2.fillPolygon(puntosX, puntosY, 10);
            
            g2.dispose();
        }
        
        @Override
        public int getIconWidth() { return tamanio; }
        
        @Override
        public int getIconHeight() { return tamanio; }
    }
    
    // ========== BORDE REDONDEADO ==========
    
    static class BordeRedondeado implements javax.swing.border.Border {
        private Color color;
        private int radio;
        
        public BordeRedondeado(Color color, int radio) {
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
    
    // ========== PUNTO DE ENTRADA ==========
    
    public static void main(String[] args) {
        configurarTemaWisteria();
        
        SwingUtilities.invokeLater(() -> {
            try {
                RepositorioAnime repositorioAnime = new RepositorioAnimeArchivo();
                RepositorioListaPersonalizada repositorioLista = new RepositorioListaPersonalizadaArchivo();
                
                ServicioAnime servicioAnime = new ServicioAnime(repositorioAnime);
                ServicioListaPersonalizada servicioLista = 
                    new ServicioListaPersonalizada(repositorioLista, repositorioAnime);
                ServicioRecomendacion servicioRecomendacion = 
                    new ServicioRecomendacion(repositorioAnime);
                ServicioEstadisticas servicioEstadisticas = 
                    new ServicioEstadisticas(repositorioAnime);
                
                VentanaPrincipal ventana = new VentanaPrincipal(
                    servicioAnime, servicioLista, servicioRecomendacion, servicioEstadisticas);
                ventana.setVisible(true);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Error al iniciar la aplicación: " + e.getMessage(),
                    "Error Fatal", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
