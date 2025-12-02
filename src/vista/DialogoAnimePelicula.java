package vista;

import modelo.*;
import servicio.ServicioAnime;
import excepcion.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Diálogo para crear o editar una película de anime.
 */
public class DialogoAnimePelicula extends JDialog {
    
    private final ServicioAnime servicioAnime;
    private final AnimePelicula peliculaExistente;
    
    private JTextField txtTitulo;
    private JSpinner spnAnio;
    private JTextField txtEstudio;
    private JSpinner spnDuracion;
    private JTextField txtDirector;
    private JComboBox<Estado> cmbEstado;
    private JSpinner spnCalificacion;
    private Map<Genero, JCheckBox> checkBoxGeneros;
    
    private boolean confirmado = false;
    
    public DialogoAnimePelicula(Frame parent, ServicioAnime servicioAnime, AnimePelicula peliculaExistente) {
        super(parent, peliculaExistente == null ? "Nueva Película" : "Editar Película", true);
        this.servicioAnime = servicioAnime;
        this.peliculaExistente = peliculaExistente;
        
        crearUI();
        
        if (peliculaExistente != null) {
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
        
        int fila = 0;
        
        gbc.gridx = 0; gbc.gridy = fila;
        panelForm.add(new JLabel("Título: *"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTitulo = new JTextField(30);
        panelForm.add(txtTitulo, gbc);
        
        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.fill = GridBagConstraints.NONE;
        panelForm.add(new JLabel("Año de lanzamiento: *"), gbc);
        gbc.gridx = 1;
        spnAnio = new JSpinner(new SpinnerNumberModel(2020, 1917, 2030, 1));
        spnAnio.setEditor(new JSpinner.NumberEditor(spnAnio, "#"));
        panelForm.add(spnAnio, gbc);
        
        fila++;
        gbc.gridx = 0; gbc.gridy = fila;
        panelForm.add(new JLabel("Estudio:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtEstudio = new JTextField(20);
        panelForm.add(txtEstudio, gbc);
        
        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.fill = GridBagConstraints.NONE;
        panelForm.add(new JLabel("Duración (minutos): *"), gbc);
        gbc.gridx = 1;
        spnDuracion = new JSpinner(new SpinnerNumberModel(120, 1, 600, 1));
        panelForm.add(spnDuracion, gbc);
        
        fila++;
        gbc.gridx = 0; gbc.gridy = fila;
        panelForm.add(new JLabel("Director:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDirector = new JTextField(20);
        panelForm.add(txtDirector, gbc);
        
        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.fill = GridBagConstraints.NONE;
        panelForm.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        cmbEstado = new JComboBox<>(Estado.values());
        panelForm.add(cmbEstado, gbc);
        
        fila++;
        gbc.gridx = 0; gbc.gridy = fila;
        panelForm.add(new JLabel("Calificación (1-5):"), gbc);
        gbc.gridx = 1;
        spnCalificacion = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
        panelForm.add(spnCalificacion, gbc);
        
        fila++;
        gbc.gridx = 0; gbc.gridy = fila;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panelForm.add(new JLabel("Géneros: *"), gbc);
        gbc.gridx = 1;
        JPanel panelGeneros = new JPanel(new GridLayout(0, 3, 5, 2));
        checkBoxGeneros = new HashMap<>();
        for (Genero g : Genero.values()) {
            JCheckBox cb = new JCheckBox(g.obtenerDescripcion());
            checkBoxGeneros.put(g, cb);
            panelGeneros.add(cb);
        }
        JScrollPane scrollGeneros = new JScrollPane(panelGeneros);
        scrollGeneros.setPreferredSize(new Dimension(300, 150));
        panelForm.add(scrollGeneros, gbc);
        
        add(panelForm, BorderLayout.CENTER);
        
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
        txtTitulo.setText(peliculaExistente.obtenerTitulo());
        spnAnio.setValue(peliculaExistente.obtenerAnioLanzamiento());
        txtEstudio.setText(peliculaExistente.obtenerEstudio());
        spnDuracion.setValue(peliculaExistente.obtenerDuracionMinutos());
        txtDirector.setText(peliculaExistente.obtenerDirector());
        cmbEstado.setSelectedItem(peliculaExistente.obtenerEstado());
        spnCalificacion.setValue(peliculaExistente.obtenerCalificacion());
        
        for (Genero g : peliculaExistente.obtenerGeneros()) {
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
            int duracion = (Integer) spnDuracion.getValue();
            String director = txtDirector.getText().trim();
            Estado estado = (Estado) cmbEstado.getSelectedItem();
            int calificacion = (Integer) spnCalificacion.getValue();
            
            Set<Genero> generos = new HashSet<>();
            for (Map.Entry<Genero, JCheckBox> entry : checkBoxGeneros.entrySet()) {
                if (entry.getValue().isSelected()) {
                    generos.add(entry.getKey());
                }
            }
            
            if (peliculaExistente == null) {
                AnimePelicula nueva = servicioAnime.registrarPelicula(titulo, anio, estudio, duracion, generos, director);
                
                if (estado != Estado.POR_VER) {
                    servicioAnime.cambiarEstado(nueva.obtenerTitulo(), estado);
                }
                if (calificacion > 0) {
                    servicioAnime.calificarAnime(nueva.obtenerTitulo(), calificacion);
                }
                
                JOptionPane.showMessageDialog(this, "Película creada correctamente");
            } else {
                servicioAnime.actualizarAnime(peliculaExistente.obtenerTitulo(), titulo, anio, estudio, estado,
                    calificacion > 0 ? calificacion : null, generos);
                
                AnimeBase actualizado = servicioAnime.buscarPorTituloExacto(titulo);
                if (actualizado instanceof AnimePelicula) {
                    AnimePelicula p = (AnimePelicula) actualizado;
                    p.establecerDuracionMinutos(duracion);
                    p.establecerDirector(director);
                }
                
                JOptionPane.showMessageDialog(this, "Película actualizada correctamente");
            }
            
            confirmado = true;
            dispose();
            
        } catch (ExcepcionValidacion e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de validación", JOptionPane.WARNING_MESSAGE);
        } catch (ExcepcionAnimeYaExistente e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Anime duplicado", JOptionPane.WARNING_MESSAGE);
        } catch (ExcepcionAnime e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean estaConfirmado() {
        return confirmado;
    }
}

