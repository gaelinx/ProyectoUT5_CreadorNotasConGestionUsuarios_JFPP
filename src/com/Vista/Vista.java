package com.Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

public class Vista {
    // Componentes de Notas
    private JFrame notasFrame;
    private JTextField titulo;
    private JTextField buscar;
    private JTextArea areaContenido;
    private JList<String> listaDeNotas;
    private DefaultListModel<String> ModeloDeLista;
    private JButton editar;
    private JButton guardar;
    private JButton eliminar;
    private JButton limpiarTexto;
    private JButton botonBusqueda;
    private JButton botonCerrarSesion;

    // Componentes de Login
    private JFrame login;
    private JPasswordField campoContraseña;
    private JTextField campoCorreo;
    private JButton botonLogin;
    private JButton botonRegistro;
    private String tituloNota;

    public Vista(){
        PantallaLogin();
        PantallaNotas();
    }



    // Getter para el JFrame de notas


    private void PantallaLogin() {
        login = new JFrame("Pantalla de Login");
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setSize(400, 300);
        login.setResizable(false);

        // Configuracion del Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titulo = new JLabel("Pantalla de Login");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(new Color(0, 102, 204));
        mainPanel.add(titulo, gbc);

        // Campos de texto
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Correo
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Correo Electronico:"), gbc);

        gbc.gridx = 1;
        campoCorreo = new JTextField(15);
        campoCorreo.setFont(new Font("Arial", Font.PLAIN, 13));
        mainPanel.add(campoCorreo, gbc);

        // Contraseña
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        campoContraseña = new JPasswordField(15);
        campoContraseña.setFont(new Font("Arial", Font.PLAIN, 13));
        mainPanel.add(campoContraseña, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        botonLogin = crearBotonPersonalizado("Iniciar Sesión");
        botonRegistro = crearBotonPersonalizado("Registrarse");

        buttonPanel.add(botonLogin);
        buttonPanel.add(botonRegistro);

        mainPanel.add(buttonPanel, gbc);

        login.add(mainPanel);
        login.setLocationRelativeTo(null);
        login.setVisible(true);
    }


    // Modificar el método PantallaNotas en la clase Vista:
    public void PantallaNotas() {
        notasFrame = new JFrame("Creador de Notas");
        notasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        notasFrame.setSize(900, 750);
        notasFrame.setLayout(new BorderLayout());
        notasFrame.setLocationRelativeTo(null);

        // Panel superior - Solo título
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        titulo = new JTextField(25);
        titulo.setFont(new Font("Arial", Font.PLAIN, 14));
        panelSuperior.add(new JLabel("Título: "));
        panelSuperior.add(titulo);

        notasFrame.add(panelSuperior, BorderLayout.NORTH);

        // Área de contenido principal
        areaContenido = new JTextArea();
        areaContenido.setFont(new Font("Arial", Font.PLAIN, 14));
        areaContenido.setLineWrap(true);
        areaContenido.setWrapStyleWord(true);
        JScrollPane scrollContenido = new JScrollPane(areaContenido);
        scrollContenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 5),
                BorderFactory.createLineBorder(Color.GRAY)
        ));
        notasFrame.add(scrollContenido, BorderLayout.CENTER);

        // Panel lateral - Lista de notas
        JPanel panelLateral = new JPanel(new BorderLayout());
        panelLateral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelLateral.setPreferredSize(new Dimension(250, notasFrame.getHeight()));

        ModeloDeLista = new DefaultListModel<>();
        listaDeNotas = new JList<>(ModeloDeLista);
        listaDeNotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaDeNotas.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollLista = new JScrollPane(listaDeNotas);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Mis Notas"));

        panelLateral.add(scrollLista, BorderLayout.CENTER);
        notasFrame.add(panelLateral, BorderLayout.WEST);

        // Panel inferior - Botones
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de botones principales (izquierda)
        JPanel panelBotonesPrincipales = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        guardar = crearBotonPersonalizado("Guardar");
        editar = crearBotonPersonalizado("Editar");
        eliminar = crearBotonPersonalizado("Eliminar");
        limpiarTexto = crearBotonPersonalizado("Limpiar");
        botonBusqueda = crearBotonPersonalizado("Buscar");

        panelBotonesPrincipales.add(guardar);
        panelBotonesPrincipales.add(editar);
        panelBotonesPrincipales.add(eliminar);
        panelBotonesPrincipales.add(limpiarTexto);
        panelBotonesPrincipales.add(botonBusqueda);

        // Panel de botón cerrar sesión (derecha)
        JPanel panelCerrarSesion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botonCerrarSesion = crearBotonPersonalizado("Cerrar Sesión");
        panelCerrarSesion.add(botonCerrarSesion);

        panelInferior.add(panelBotonesPrincipales, BorderLayout.WEST);
        panelInferior.add(panelCerrarSesion, BorderLayout.EAST);

        notasFrame.add(panelInferior, BorderLayout.SOUTH);

        // Configuración final
        notasFrame.pack();
    }
    // Método para seleccionar una nota en la lista
    public void seleccionarNotaEnLista(String tituloNota) {
        if (tituloNota == null || ModeloDeLista == null || listaDeNotas == null) {
            return;
        }

        for (int i = 0; i < ModeloDeLista.size(); i++) {
            if (tituloNota.equals(ModeloDeLista.getElementAt(i))) {
                listaDeNotas.setSelectedIndex(i);
                listaDeNotas.ensureIndexIsVisible(i);
                break;
            }
        }
    }

    public void MostrarLogin() {
        notasFrame.setVisible(false);
        login.setLocationRelativeTo(null);
        login.setVisible(true);
    }

    public void MostrarPantallaNotas() {
        login.setVisible(false);
        notasFrame.setLocationRelativeTo(null);
        notasFrame.setVisible(true);
    }

    public void actualizarListaNotas(Set<String> titulos) {
        ModeloDeLista.clear();
        for (String titulo : titulos) {
            ModeloDeLista.addElement(titulo);
        }
    }

    public JFrame getFrame() {
        return this.notasFrame;
    }

    public void mostrarContenidoNota(String titulo, String contenido) {
        this.titulo.setText(titulo != null ? titulo : "");
        this.areaContenido.setText(contenido != null ? contenido : "");
    }

    public void limpiarCampos() {
        if (titulo != null) titulo.setText("");
        if (areaContenido != null) areaContenido.setText("");
    }

    public void limpiarCamposLogin() {
        if (campoCorreo != null) campoCorreo.setText("");
        if (campoContraseña != null) campoContraseña.setText("");
    }

    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(login, mensaje, titulo, tipo);
    }

    public boolean mostrarConfirmacion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(
                notasFrame, mensaje, titulo,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private JButton crearBotonPersonalizado(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isRollover()) {
                    setBackground(new Color(0, 204, 0));
                    setForeground(Color.BLACK);
                } else {
                    setBackground(new Color(0, 102, 204));
                    setForeground(Color.WHITE);
                }
                super.paintComponent(g);
            }
        };

        boton.setContentAreaFilled(false);
        boton.setOpaque(true);
        boton.setBackground(new Color(0, 102, 204));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 85, 170)),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(0, 204, 0));
                boton.setForeground(Color.BLACK);
                boton.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(new Color(0, 102, 204));
                boton.setForeground(Color.WHITE);
                boton.repaint();
            }
        });

        return boton;
    }
    // Getters
    public String getCorreo() {
        return campoCorreo.getText();
    }

    public String getContraseña() {
        return new String(campoContraseña.getPassword());
    }
    public String getTextoBusqueda() { return buscar.getText(); }
    public String getTituloNota() { return titulo.getText(); }
    public String getContenidoNota() { return areaContenido.getText(); }
    public String getNotaSeleccionada() { return listaDeNotas.getSelectedValue(); }

    // Getters para botones
    public JButton getBotonLogin() { return botonLogin; }
    public JButton getBotonRegistro() { return botonRegistro; }
    public JButton getBotonGuardar() { return guardar; }
    public JButton getBotonEditar() { return editar; }
    public JButton getBotonEliminar() { return eliminar; }
    public JButton getBotonLimpiar() { return limpiarTexto; }
    public JButton getBotonBusqueda() { return botonBusqueda; }
    public JList<String> getListaDeNotas() { return listaDeNotas; }
    public JButton getBotonCerrarSesion() { return botonCerrarSesion; }
}