package ui;

import model.*;
import service.AnimeService;
import exception.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Diálogo para crear o editar una serie de anime.
 * 
 * MVC: Es parte de la Vista. Solo captura datos y los envía al servicio.
 * Los listeners son simples: capturan datos, llaman al servicio, muestran resultado.
 */
public class AnimeSerieDialog extends JDialog {
    
    private final AnimeService animeService;
    private final AnimeSerie serieExistente;
    
    private JTextField txtTitulo;
    private JSpinner spnAnio;
    private JTextField txtEstudio;
    private JSpinner spnCapitulos;
    private JCheckBox chkEnEmision;
    private JComboBox<Estado> cmbEstado;
    private JSpinner spnCalificacion;
    private JPanel panelGeneros;
    private Map<Genero, JCheckBox> checkBoxGeneros;
    
    private boolean confirmado = false;
    
    public AnimeSerieDialog(Frame parent, AnimeService animeService, AnimeSerie serieExistente) {
        super(parent, serieExistente == null ? "Nueva Serie" : "Editar Serie", true);
        this.animeService = animeService;
        this.serieExistente = serieExistente;
        
        crearUI();
        
        if (serieExistente != null) {
            cargarDatos();
        }
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void crearUI() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Título
        gbc.gridx = 0; gbc.gridy = row;
        panelForm.add(new JLabel("Título: *"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTitulo = new JTextField(30);
        panelForm.add(txtTitulo, gbc);
        
        // Año
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        panelForm.add(new JLabel("Año de lanzamiento: *"), gbc);
        gbc.gridx = 1;
        spnAnio = new JSpinner(new SpinnerNumberModel(2020, 1917, 2030, 1));
        spnAnio.setEditor(new JSpinner.NumberEditor(spnAnio, "#"));
        panelForm.add(spnAnio, gbc);
        
        // Estudio
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panelForm.add(new JLabel("Estudio:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtEstudio = new JTextField(20);
        panelForm.add(txtEstudio, gbc);
        
        // Capítulos
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        panelForm.add(new JLabel("Cantidad de capítulos: *"), gbc);
        gbc.gridx = 1;
        spnCapitulos = new JSpinner(new SpinnerNumberModel(12, 1, 9999, 1));
        panelForm.add(spnCapitulos, gbc);
        
        // En emisión
        row++;
        gbc.gridx = 1; gbc.gridy = row;
        chkEnEmision = new JCheckBox("En emisión");
        panelForm.add(chkEnEmision, gbc);
        
        // Estado
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panelForm.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        cmbEstado = new JComboBox<>(Estado.values());
        panelForm.add(cmbEstado, gbc);
        
        // Calificación
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panelForm.add(new JLabel("Calificación (1-5):"), gbc);
        gbc.gridx = 1;
        spnCalificacion = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
        panelForm.add(spnCalificacion, gbc);
        
        // Géneros
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panelForm.add(new JLabel("Géneros: *"), gbc);
        gbc.gridx = 1;
        panelGeneros = new JPanel(new GridLayout(0, 3, 5, 2));
        checkBoxGeneros = new HashMap<>();
        for (Genero g : Genero.values()) {
            JCheckBox cb = new JCheckBox(g.getDescripcion());
            checkBoxGeneros.put(g, cb);
            panelGeneros.add(cb);
        }
        JScrollPane scrollGeneros = new JScrollPane(panelGeneros);
        scrollGeneros.setPreferredSize(new Dimension(300, 150));
        panelForm.add(scrollGeneros, gbc);
        
        add(panelForm, BorderLayout.CENTER);
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardar());
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
        
        getRootPane().setDefaultButton(btnGuardar);
    }
    
    private void cargarDatos() {
        txtTitulo.setText(serieExistente.getTitulo());
        spnAnio.setValue(serieExistente.getAnioLanzamiento());
        txtEstudio.setText(serieExistente.getEstudio());
        spnCapitulos.setValue(serieExistente.getCantidadCapitulos());
        chkEnEmision.setSelected(serieExistente.isEnEmision());
        cmbEstado.setSelectedItem(serieExistente.getEstado());
        spnCalificacion.setValue(serieExistente.getCalificacion());
        
        for (Genero g : serieExistente.getGeneros()) {
            JCheckBox cb = checkBoxGeneros.get(g);
            if (cb != null) {
                cb.setSelected(true);
            }
        }
    }
    
    private void guardar() {
        try {
            String titulo = txtTitulo.getText().trim();
            int anio = (Integer) spnAnio.getValue();
            String estudio = txtEstudio.getText().trim();
            int capitulos = (Integer) spnCapitulos.getValue();
            boolean enEmision = chkEnEmision.isSelected();
            Estado estado = (Estado) cmbEstado.getSelectedItem();
            int calificacion = (Integer) spnCalificacion.getValue();
            
            Set<Genero> generos = new HashSet<>();
            for (Map.Entry<Genero, JCheckBox> entry : checkBoxGeneros.entrySet()) {
                if (entry.getValue().isSelected()) {
                    generos.add(entry.getKey());
                }
            }
            
            if (serieExistente == null) {
                // Crear nueva serie
                AnimeSerie nueva = animeService.registrarSerie(
                    titulo, anio, estudio, capitulos, generos, enEmision);
                
                // Asignar estado y calificación si corresponde
                if (estado != Estado.POR_VER) {
                    animeService.cambiarEstado(nueva.getTitulo(), estado);
                }
                if (calificacion > 0) {
                    animeService.calificarAnime(nueva.getTitulo(), calificacion);
                }
                
                JOptionPane.showMessageDialog(this, "Serie creada correctamente");
            } else {
                // Actualizar serie existente
                animeService.actualizarAnime(
                    serieExistente.getTitulo(), titulo, anio, estudio, estado, 
                    calificacion > 0 ? calificacion : null, generos);
                
                // Actualizar campos específicos de serie
                AnimeBase actualizado = animeService.buscarPorTituloExacto(titulo);
                if (actualizado instanceof AnimeSerie s) {
                    s.setCantidadCapitulos(capitulos);
                    s.setEnEmision(enEmision);
                }
                
                JOptionPane.showMessageDialog(this, "Serie actualizada correctamente");
            }
            
            confirmado = true;
            dispose();
            
        } catch (ValidacionException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                "Error de validación", JOptionPane.WARNING_MESSAGE);
        } catch (AnimeYaExistenteException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                "Anime duplicado", JOptionPane.WARNING_MESSAGE);
        } catch (AnimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmado() {
        return confirmado;
    }
}

