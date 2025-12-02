package vista;

import modelo.*;
import servicio.*;
import excepcion.*;
import utilidad.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Panel para la gestión del catálogo de anime.
 */
public class PanelAnime extends JPanel {
    
    private final ServicioAnime servicioAnime;
    private final ServicioListaPersonalizada servicioLista;
    
    private JTable tablaAnime;
    private ModeloTablaAnime modeloTabla;
    private JTextField txtBusqueda;
    private JComboBox<String> cmbGenero;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbOrden;
    private JSpinner spnCalificacionMin;
    
    public PanelAnime(ServicioAnime servicioAnime, ServicioListaPersonalizada servicioLista) {
        this.servicioAnime = servicioAnime;
        this.servicioLista = servicioLista;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        crearPanelSuperior();
        crearTabla();
        crearPanelInferior();
        
        refrescar();
    }
    
    private void crearPanelSuperior() {
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        
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
        
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        panelFiltros.add(new JLabel("Género:"));
        cmbGenero = new JComboBox<>();
        cmbGenero.addItem("Todos");
        for (Genero g : Genero.values()) {
            cmbGenero.addItem(g.obtenerDescripcion());
        }
        cmbGenero.addActionListener(e -> aplicarFiltros());
        panelFiltros.add(cmbGenero);
        
        panelFiltros.add(Box.createHorizontalStrut(10));
        panelFiltros.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>();
        cmbEstado.addItem("Todos");
        for (Estado e : Estado.values()) {
            cmbEstado.addItem(e.obtenerDescripcion());
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
        modeloTabla = new ModeloTablaAnime();
        tablaAnime = new JTable(modeloTabla);
        
        tablaAnime.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaAnime.setRowHeight(25);
        tablaAnime.getTableHeader().setReorderingAllowed(false);
        
        TableColumnModel columnModel = tablaAnime.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(250);
        columnModel.getColumn(2).setPreferredWidth(60);
        columnModel.getColumn(3).setPreferredWidth(120);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(150);
        columnModel.getColumn(6).setPreferredWidth(80);
        columnModel.getColumn(7).setPreferredWidth(100);
        
        columnModel.getColumn(7).setCellRenderer(new RenderizadorEstrellas());
        
        tablaAnime.addMouseListener(new MouseAdapter() {
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
        
        JButton btnNuevaSerie = new JButton("Nueva Serie");
        btnNuevaSerie.setIcon(new IconoMas(18, new Color(76, 175, 80)));
        btnNuevaSerie.addActionListener(e -> mostrarDialogoNuevaSerie());
        panelBotones.add(btnNuevaSerie);
        
        JButton btnNuevaPelicula = new JButton("Nueva Película");
        btnNuevaPelicula.setIcon(new IconoMas(18, new Color(76, 175, 80)));
        btnNuevaPelicula.addActionListener(e -> mostrarDialogoNuevaPelicula());
        panelBotones.add(btnNuevaPelicula);
        
        panelBotones.add(Box.createHorizontalStrut(20));
        
        JButton btnEditar = new JButton("Editar");
        btnEditar.setIcon(new IconoLapiz(18, new Color(255, 152, 0)));
        btnEditar.addActionListener(e -> editarAnimeSeleccionado());
        panelBotones.add(btnEditar);
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setIcon(new IconoCruz(18, new Color(244, 67, 54)));
        btnEliminar.addActionListener(e -> eliminarAnimeSeleccionado());
        panelBotones.add(btnEliminar);
        
        JButton btnCalificar = new JButton("Calificar");
        btnCalificar.setIcon(new IconoEstrella(18, new Color(255, 200, 50)));
        btnCalificar.addActionListener(e -> calificarAnimeSeleccionado());
        panelBotones.add(btnCalificar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    public void mostrarDialogoNuevaSerie() {
        DialogoAnimeSerie dialogo = new DialogoAnimeSerie(
            (Frame) SwingUtilities.getWindowAncestor(this), servicioAnime, null);
        dialogo.setVisible(true);
        
        if (dialogo.estaConfirmado()) {
            refrescar();
        }
    }
    
    public void mostrarDialogoNuevaPelicula() {
        DialogoAnimePelicula dialogo = new DialogoAnimePelicula(
            (Frame) SwingUtilities.getWindowAncestor(this), servicioAnime, null);
        dialogo.setVisible(true);
        
        if (dialogo.estaConfirmado()) {
            refrescar();
        }
    }
    
    private void editarAnimeSeleccionado() {
        AnimeBase anime = obtenerAnimeSeleccionado();
        if (anime == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un anime para editar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (anime instanceof AnimeSerie) {
            DialogoAnimeSerie dialogo = new DialogoAnimeSerie(
                (Frame) SwingUtilities.getWindowAncestor(this), servicioAnime, (AnimeSerie) anime);
            dialogo.setVisible(true);
            if (dialogo.estaConfirmado()) refrescar();
        } else if (anime instanceof AnimePelicula) {
            DialogoAnimePelicula dialogo = new DialogoAnimePelicula(
                (Frame) SwingUtilities.getWindowAncestor(this), servicioAnime, (AnimePelicula) anime);
            dialogo.setVisible(true);
            if (dialogo.estaConfirmado()) refrescar();
        }
    }
    
    private void eliminarAnimeSeleccionado() {
        List<AnimeBase> animesSeleccionados = obtenerAnimesSeleccionados();
        if (animesSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione uno o más anime para eliminar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String mensaje;
        if (animesSeleccionados.size() == 1) {
            mensaje = "¿Está seguro de eliminar '" + animesSeleccionados.get(0).obtenerTitulo() + "'?";
        } else {
            mensaje = "¿Está seguro de eliminar " + animesSeleccionados.size() + " anime seleccionados?\n\n";
            for (int i = 0; i < Math.min(animesSeleccionados.size(), 5); i++) {
                mensaje += "• " + animesSeleccionados.get(i).obtenerTitulo() + "\n";
            }
            if (animesSeleccionados.size() > 5) {
                mensaje += "• ... y " + (animesSeleccionados.size() - 5) + " más";
            }
        }
        
        int opcion = JOptionPane.showConfirmDialog(this, mensaje,
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            int eliminados = 0;
            int errores = 0;
            for (AnimeBase anime : animesSeleccionados) {
                try {
                    servicioAnime.eliminarAnime(anime.obtenerTitulo());
                    eliminados++;
                } catch (ExcepcionPersistencia e) {
                    errores++;
                }
            }
            
            if (errores == 0) {
                JOptionPane.showMessageDialog(this, 
                    eliminados == 1 ? "Anime eliminado correctamente" : eliminados + " anime eliminados correctamente");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Se eliminaron " + eliminados + " anime. Errores: " + errores, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
            refrescar();
        }
    }
    
    private void calificarAnimeSeleccionado() {
        AnimeBase anime = obtenerAnimeSeleccionado();
        if (anime == null) return;
        
        String[] opciones = {"1 ⭐", "2 ⭐⭐", "3 ⭐⭐⭐", "4 ⭐⭐⭐⭐", "5 ⭐⭐⭐⭐⭐"};
        int seleccion = JOptionPane.showOptionDialog(this,
            "Calificar: " + anime.obtenerTitulo(), "Calificar anime",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, opciones, opciones[2]);
        
        if (seleccion >= 0) {
            try {
                servicioAnime.calificarAnime(anime.obtenerTitulo(), seleccion + 1);
                refrescar();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al calificar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private AnimeBase obtenerAnimeSeleccionado() {
        int fila = tablaAnime.getSelectedRow();
        if (fila >= 0) {
            return modeloTabla.obtenerAnimeEn(fila);
        }
        return null;
    }
    
    private List<AnimeBase> obtenerAnimesSeleccionados() {
        List<AnimeBase> seleccionados = new ArrayList<>();
        int[] filas = tablaAnime.getSelectedRows();
        for (int fila : filas) {
            AnimeBase anime = modeloTabla.obtenerAnimeEn(fila);
            if (anime != null) {
                seleccionados.add(anime);
            }
        }
        return seleccionados;
    }
    
    private void aplicarFiltros() {
        try {
            FiltroAnime filtro = new FiltroAnime();
            
            String busqueda = txtBusqueda.getText().trim();
            if (!busqueda.isEmpty()) {
                filtro.porTitulo(busqueda);
            }
            
            int generoIndex = cmbGenero.getSelectedIndex();
            if (generoIndex > 0) {
                Genero genero = Genero.values()[generoIndex - 1];
                filtro.porGenero(genero);
            }
            
            int estadoIndex = cmbEstado.getSelectedIndex();
            if (estadoIndex > 0) {
                Estado estado = Estado.values()[estadoIndex - 1];
                filtro.porEstado(estado);
            }
            
            int calMin = (Integer) spnCalificacionMin.getValue();
            if (calMin > 0) {
                filtro.porCalificacionMinima(calMin);
            }
            
            List<AnimeBase> resultado = servicioAnime.busquedaAvanzada(filtro);
            
            CriterioOrdenamiento criterio = obtenerCriterioOrdenamiento();
            resultado = servicioAnime.ordenar(resultado, criterio);
            
            modeloTabla.establecerAnimes(resultado);
            
        } catch (ExcepcionPersistencia e) {
            JOptionPane.showMessageDialog(this, "Error al filtrar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private CriterioOrdenamiento obtenerCriterioOrdenamiento() {
        int indice = cmbOrden.getSelectedIndex();
        switch (indice) {
            case 0: return new OrdenamientoPorTitulo(true);
            case 1: return new OrdenamientoPorTitulo(false);
            case 2: return new OrdenamientoPorCalificacion(true);
            case 3: return new OrdenamientoPorCalificacion(false);
            case 4: return new OrdenamientoPorAnio(true);
            case 5: return new OrdenamientoPorAnio(false);
            default: return new OrdenamientoPorTitulo(true);
        }
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
            List<AnimeBase> animes = servicioAnime.listarOrdenadosPorTitulo();
            modeloTabla.establecerAnimes(animes);
        } catch (ExcepcionPersistencia e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Modelo de tabla para mostrar anime.
     */
    private static class ModeloTablaAnime extends AbstractTableModel {
        private final String[] columnas = {"Tipo", "Título", "Año", "Estudio", "Duración", "Géneros", "Estado", "★"};
        private List<AnimeBase> animes = new ArrayList<>();
        
        public void establecerAnimes(List<AnimeBase> animes) {
            this.animes = new ArrayList<>(animes);
            fireTableDataChanged();
        }
        
        public AnimeBase obtenerAnimeEn(int fila) {
            if (fila >= 0 && fila < animes.size()) {
                return animes.get(fila);
            }
            return null;
        }
        
        @Override public int getRowCount() { return animes.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int col) { return columnas[col]; }
        
        @Override
        public Object getValueAt(int fila, int col) {
            AnimeBase anime = animes.get(fila);
            switch (col) {
                case 0: return anime.obtenerTipo().obtenerDescripcion();
                case 1: return anime.obtenerTitulo();
                case 2: return anime.obtenerAnioLanzamiento();
                case 3: return anime.obtenerEstudio();
                case 4: return anime.obtenerDescripcionDuracion();
                case 5: return formatearGeneros(anime.obtenerGeneros());
                case 6: return anime.obtenerEstado().obtenerDescripcion();
                case 7: return anime.tieneCalificacion() ? anime.obtenerCalificacion() : "-";
                default: return "";
            }
        }
        
        private String formatearGeneros(Set<Genero> generos) {
            StringBuilder sb = new StringBuilder();
            boolean primero = true;
            for (Genero genero : generos) {
                if (!primero) sb.append(", ");
                sb.append(genero.obtenerDescripcion());
                primero = false;
            }
            return sb.toString();
        }
    }
    
    /**
     * Renderer para mostrar calificaciones como estrellas amarillas.
     */
    static class RenderizadorEstrellas extends JPanel implements TableCellRenderer {
        private int calificacion = 0;
        private static final Color ESTRELLA_LLENA = new Color(255, 200, 50);
        private static final Color ESTRELLA_VACIA = new Color(200, 200, 200);
        private static final Color BORDE_ESTRELLA = new Color(180, 140, 20);
        
        public RenderizadorEstrellas() { setOpaque(true); }
        
        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor,
                boolean seleccionado, boolean conFoco, int fila, int columna) {
            
            if (valor instanceof Integer) {
                calificacion = (Integer) valor;
            } else if (valor instanceof String && !"-".equals(valor)) {
                try { calificacion = Integer.parseInt((String) valor); }
                catch (NumberFormatException e) { calificacion = 0; }
            } else {
                calificacion = 0;
            }
            
            setBackground(seleccionado ? tabla.getSelectionBackground() : tabla.getBackground());
            return this;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int tamanio = 14, espaciado = 2;
            int anchoTotal = 5 * tamanio + 4 * espaciado;
            int inicioX = (getWidth() - anchoTotal) / 2;
            int inicioY = (getHeight() - tamanio) / 2;
            
            for (int i = 0; i < 5; i++) {
                int x = inicioX + i * (tamanio + espaciado);
                dibujarEstrella(g2, x, inicioY, tamanio, 
                    i < calificacion ? ESTRELLA_LLENA : ESTRELLA_VACIA,
                    i < calificacion ? BORDE_ESTRELLA : new Color(160, 160, 160));
            }
            g2.dispose();
        }
        
        private void dibujarEstrella(Graphics2D g2, int x, int y, int tamanio, Color relleno, Color borde) {
            int[] puntosX = new int[10], puntosY = new int[10];
            double angulo = -Math.PI / 2, delta = Math.PI / 5;
            int radioExt = tamanio / 2, radioInt = tamanio / 5;
            int cx = x + tamanio / 2, cy = y + tamanio / 2;
            
            for (int i = 0; i < 10; i++) {
                int r = (i % 2 == 0) ? radioExt : radioInt;
                puntosX[i] = cx + (int)(r * Math.cos(angulo));
                puntosY[i] = cy + (int)(r * Math.sin(angulo));
                angulo += delta;
            }
            
            g2.setColor(relleno);
            g2.fillPolygon(puntosX, puntosY, 10);
            g2.setColor(borde);
            g2.setStroke(new BasicStroke(1f));
            g2.drawPolygon(puntosX, puntosY, 10);
        }
    }
    
    /**
     * Icono de signo + (verde) para botones de Nueva Serie/Película.
     */
    static class IconoMas implements Icon {
        private final int tamanio;
        private final Color color;
        
        public IconoMas(int tamanio, Color color) {
            this.tamanio = tamanio;
            this.color = color;
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Círculo de fondo
            g2.setColor(color);
            g2.fillOval(x, y, tamanio, tamanio);
            
            // Símbolo +
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int margen = tamanio / 4;
            int centro = tamanio / 2;
            g2.drawLine(x + margen, y + centro, x + tamanio - margen, y + centro);
            g2.drawLine(x + centro, y + margen, x + centro, y + tamanio - margen);
            
            g2.dispose();
        }
        
        @Override public int getIconWidth() { return tamanio; }
        @Override public int getIconHeight() { return tamanio; }
    }
    
    /**
     * Icono de lápiz (naranja) para botón Editar.
     */
    static class IconoLapiz implements Icon {
        private final int tamanio;
        private final Color color;
        
        public IconoLapiz(int tamanio, Color color) {
            this.tamanio = tamanio;
            this.color = color;
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Rotar para dibujar el lápiz inclinado
            g2.translate(x + tamanio/2, y + tamanio/2);
            g2.rotate(Math.toRadians(45));
            g2.translate(-tamanio/2, -tamanio/2);
            
            int ancho = tamanio / 3;
            int alto = tamanio - 2;
            int offsetX = (tamanio - ancho) / 2;
            
            // Cuerpo del lápiz
            g2.setColor(color);
            g2.fillRect(offsetX, 2, ancho, alto - 4);
            
            // Punta del lápiz
            int[] puntaX = {offsetX, offsetX + ancho/2, offsetX + ancho};
            int[] puntaY = {alto - 2, alto + 1, alto - 2};
            g2.setColor(new Color(80, 80, 80));
            g2.fillPolygon(puntaX, puntaY, 3);
            
            // Borde superior (borrador)
            g2.setColor(new Color(255, 182, 193));
            g2.fillRect(offsetX, 0, ancho, 3);
            
            g2.dispose();
        }
        
        @Override public int getIconWidth() { return tamanio; }
        @Override public int getIconHeight() { return tamanio; }
    }
    
    /**
     * Icono de cruz X (rojo) para botón Eliminar.
     */
    static class IconoCruz implements Icon {
        private final int tamanio;
        private final Color color;
        
        public IconoCruz(int tamanio, Color color) {
            this.tamanio = tamanio;
            this.color = color;
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Círculo de fondo
            g2.setColor(color);
            g2.fillOval(x, y, tamanio, tamanio);
            
            // Cruz X
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int margen = tamanio / 4;
            g2.drawLine(x + margen, y + margen, x + tamanio - margen, y + tamanio - margen);
            g2.drawLine(x + tamanio - margen, y + margen, x + margen, y + tamanio - margen);
            
            g2.dispose();
        }
        
        @Override public int getIconWidth() { return tamanio; }
        @Override public int getIconHeight() { return tamanio; }
    }
    
    /**
     * Icono de estrella (amarillo) para botón Calificar.
     */
    static class IconoEstrella implements Icon {
        private final int tamanio;
        private final Color color;
        
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
            double delta = Math.PI / 5;
            int radioExt = tamanio / 2;
            int radioInt = tamanio / 5;
            int cx = x + tamanio / 2;
            int cy = y + tamanio / 2;
            
            for (int i = 0; i < 10; i++) {
                int r = (i % 2 == 0) ? radioExt : radioInt;
                puntosX[i] = cx + (int)(r * Math.cos(angulo));
                puntosY[i] = cy + (int)(r * Math.sin(angulo));
                angulo += delta;
            }
            
            // Relleno
            g2.setColor(color);
            g2.fillPolygon(puntosX, puntosY, 10);
            
            // Borde
            g2.setColor(color.darker());
            g2.setStroke(new BasicStroke(1f));
            g2.drawPolygon(puntosX, puntosY, 10);
            
            g2.dispose();
        }
        
        @Override public int getIconWidth() { return tamanio; }
        @Override public int getIconHeight() { return tamanio; }
    }
}

