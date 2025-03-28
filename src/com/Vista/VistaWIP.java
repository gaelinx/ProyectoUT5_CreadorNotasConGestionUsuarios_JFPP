package com.Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

//como su nombre lo indica, esta clase se encargara de la interaccion visual para el usuario;

public class VistaWIP {

    //Seccion de Creacion de Notas
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

    //Seccion de Logueo o Registro del Usuario
    private JFrame login;
    private JTextField campoContraseña;
    private JTextField campoCorreo;
    private JButton botonLogin;
    private JButton botonRegistro;

    public VistaWIP(){
        PantallaLogin();
        PantallaNotas();
        MostrarPantallaNotas();
    }

    private void PantallaLogin() {
        login = new JFrame("Pantalla de Login");
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setSize(400, 250);

        // Panel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Etiqueta y campo Correo Electronico
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lblCorreo = new JLabel("Correo Electronico:");
        lblCorreo.setFont(new Font("Arial", Font.PLAIN, 13)); // Fuente más moderna
        mainPanel.add(lblCorreo, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        campoCorreo = new JTextField(18);
        campoCorreo.setFont(new Font("Arial", Font.PLAIN, 13));
        campoCorreo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5))); // Borde sutil
        mainPanel.add(campoCorreo, gbc);

        // Etiqueta y campo Contraseña
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 13));
        mainPanel.add(lblPassword, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        campoContraseña = new JPasswordField(18);
        campoContraseña.setFont(new Font("Arial", Font.PLAIN, 13));
        campoContraseña.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5))); // Borde sutil
        mainPanel.add(campoContraseña, gbc);

        // Botones
        botonLogin = crearBotonPersonalizado("Iniciar Sesión");
        botonRegistro = crearBotonPersonalizado("Registrarse");

        // Panel para botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(botonLogin);
        buttonPanel.add(botonRegistro);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        login.add(mainPanel);
        login.setLocationRelativeTo(null);
        login.setVisible(false);
    }

    public void PantallaNotas(){
        notasFrame = new JFrame("Creador de Notas De ");
        notasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        notasFrame.setSize(900, 750);
        notasFrame.setLayout(new BorderLayout());

        JPanel dis_superior = new JPanel(new FlowLayout()); //distribucion superior
        titulo = new JTextField(20);
        buscar = new JTextField(15);
        botonBusqueda = new JButton("Buscar");
        dis_superior.add(new JLabel("Título: "));
        dis_superior.add(titulo);
        dis_superior.add(new JLabel("Buscar: "));
        dis_superior.add(buscar);
        dis_superior.add(botonBusqueda);

        // Área de contenido
        areaContenido = new JTextArea(15, 40);
        areaContenido.setLineWrap(true);
        areaContenido.setWrapStyleWord(true);
        JScrollPane Scroll = new JScrollPane(areaContenido);

        // Lista de notas
        ModeloDeLista = new DefaultListModel<>();
        listaDeNotas = new JList<>(ModeloDeLista);
        listaDeNotas.setPreferredSize(new Dimension(200, notasFrame.getHeight()));
        JScrollPane listScroll = new JScrollPane(listaDeNotas);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        guardar = new JButton("Guardar");
        editar = new JButton("Editar");
        eliminar = new JButton("Eliminar");
        limpiarTexto = new JButton("Limpiar");
        notasFrame.setVisible(true);

        buttonPanel.add(guardar);
        buttonPanel.add(editar);
        buttonPanel.add(eliminar);
        buttonPanel.add(limpiarTexto);


        // Panel lateral
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.add(new JLabel("  Mis Notas:", JLabel.LEFT), BorderLayout.NORTH);
        sidePanel.add(listScroll, BorderLayout.CENTER);

        // Agregar componentes al frame principal
        notasFrame.add(dis_superior, BorderLayout.NORTH);
        notasFrame.add(Scroll, BorderLayout.CENTER);
        notasFrame.add(sidePanel, BorderLayout.WEST);
        notasFrame.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void MostrarLogin() {
        login.setVisible(true);
        login.setLocationRelativeTo(null);
        if (notasFrame != null) {
            notasFrame.dispose();
        }
    }

    public void MostrarPantallaNotas() {
        notasFrame.setVisible(true);
        notasFrame.setLocationRelativeTo(null);
        login.setVisible(false);
    }

    // Métodos para actualizar la interfaz
    public void updateNoteList(Set<String> titles) {
        ModeloDeLista.clear();
        for (String title : titles) {
            ModeloDeLista.addElement(title);
        }
    }

    public void clearFields() {
        titulo.setText("");
        areaContenido.setText("");
        buscar.setText("");
    }



    private JButton crearBotonPersonalizado(String texto) {
        JButton boton = new JButton(texto);

        boton.setBackground(new Color(0, 102, 204));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 85, 170)),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        boton.setFont(new Font("Arial", Font.BOLD, 12));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(0, 204, 0));
                boton.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(new Color(0, 102, 204));
                boton.setForeground(Color.WHITE);
            }
        });

        return boton;
    }


}
