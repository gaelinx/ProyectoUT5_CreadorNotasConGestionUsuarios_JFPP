/**
 * Clase que se encarga de toda la interfaz visual del Proyecto, lo que vera y donde se movera el usuario
 */
package com.Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

public class Vista {
    /** Frame principal de la pantalla de notas */
    private JFrame notasFrame;
    private JTextField titulo;
    private JTextArea areaContenido;
    private JList<String> listaDeNotas;
    private DefaultListModel<String> ModeloDeLista;
    private JButton editar;
    private JButton guardar;
    private JButton eliminar;
    private JButton limpiarTexto;
    private JButton botonBusqueda;
    private JButton botonCerrarSesion;

    /** Frame de la pantalla de login */
    private JFrame login;
    private JPasswordField campoContraseña;
    private JTextField campoCorreo;
    private JButton botonLogin;
    private JButton botonRegistro;

    /**
     * Constructor que inicializa las pantallas de la aplicación.
     */
    public Vista() {
        PantallaLogin();
        PantallaNotas();
    }

    /**
     * Configuracion y muestra de la pantalla de login.
     */
    private void PantallaLogin() {
        login = new JFrame("Pantalla de Login");
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setSize(400, 300);
        login.setResizable(false);

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

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titulo = new JLabel("Pantalla de Login");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(new Color(0, 102, 204));
        mainPanel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Correo Electronico:"), gbc);

        gbc.gridx = 1;
        campoCorreo = new JTextField(15);
        campoCorreo.setFont(new Font("Arial", Font.PLAIN, 13));
        mainPanel.add(campoCorreo, gbc);

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

        botonLogin = crearBotonLogin("Iniciar Sesión");
        botonRegistro = crearBotonRegistro("Registrarse");

        buttonPanel.add(botonLogin);
        buttonPanel.add(botonRegistro);

        mainPanel.add(buttonPanel, gbc);

        login.add(mainPanel);
        login.setLocationRelativeTo(null);
        login.setVisible(true);
    }

    /**
     * Configuracion y muestra de la pantalla principal de notas.
     */
    public void PantallaNotas() {
        notasFrame = new JFrame("Creador de Notas");
        notasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        notasFrame.setSize(1200, 800);
        notasFrame.setLayout(new BorderLayout());
        notasFrame.setLocationRelativeTo(null);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelSuperior.setOpaque(false);

        titulo = new JTextField(25);
        configurarPlaceholder(titulo, "Agregue un título aquí");
        panelSuperior.add(new JLabel("Título: "));
        panelSuperior.add(titulo);

        notasFrame.add(panelSuperior, BorderLayout.NORTH);

        areaContenido = new JTextArea();
        configurarPlaceholder(areaContenido, "Escriba una Nota Aquí");
        areaContenido.setFont(new Font("Arial", Font.PLAIN, 14));
        areaContenido.setLineWrap(true);
        areaContenido.setWrapStyleWord(true);

        JScrollPane scrollContenido = new JScrollPane(areaContenido);
        scrollContenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 5),
                BorderFactory.createLineBorder(Color.GRAY)
        ));
        notasFrame.add(scrollContenido, BorderLayout.CENTER);

        JPanel panelLateral = new JPanel(new BorderLayout());
        panelLateral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelLateral.setPreferredSize(new Dimension(250, notasFrame.getHeight()));

        ModeloDeLista = new DefaultListModel<>();
        listaDeNotas = new JList<>(ModeloDeLista);
        listaDeNotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaDeNotas.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollLista = new JScrollPane(listaDeNotas);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Mis Notas"));
        scrollLista.setFont(new Font("Arial", Font.BOLD, 14));

        panelLateral.add(scrollLista, BorderLayout.CENTER);
        notasFrame.add(panelLateral, BorderLayout.WEST);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelBotonesPrincipales = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        guardar = crearBotonGuardar("Guardar");
        editar = crearBotonEditar("Editar");
        eliminar = crearBotonEliminar("Eliminar");
        limpiarTexto = crearBotonLimpiar("Limpiar");
        botonBusqueda = crearBotonBuscar("Buscar");

        panelBotonesPrincipales.add(guardar);
        panelBotonesPrincipales.add(editar);
        panelBotonesPrincipales.add(eliminar);
        panelBotonesPrincipales.add(limpiarTexto);
        panelBotonesPrincipales.add(botonBusqueda);

        JPanel panelCerrarSesion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botonCerrarSesion = crearBotonCerrarSesion("Cerrar Sesión");
        panelCerrarSesion.add(botonCerrarSesion);

        panelInferior.add(panelBotonesPrincipales, BorderLayout.WEST);
        panelInferior.add(panelCerrarSesion, BorderLayout.EAST);

        notasFrame.add(panelInferior, BorderLayout.SOUTH);
    }

    /**
     * Configura un campo de texto con texto de placeholder.
     * @param field Campo de texto a configurar
     * @param placeholder Texto a mostrar como placeholder
     */
    private void configurarPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.setFont(field.getFont().deriveFont(Font.ITALIC));

        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setFont(field.getFont().deriveFont(Font.PLAIN));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    field.setFont(field.getFont().deriveFont(Font.ITALIC));
                }
            }
        });
    }

    /**
     * Configura un área de texto con texto de placeholder.
     * @param area Área de texto a configurar
     * @param placeholder Texto a mostrar como placeholder
     */
    private void configurarPlaceholder(JTextArea area, String placeholder) {
        area.setText(placeholder);
        area.setForeground(Color.GRAY);
        area.setFont(area.getFont().deriveFont(Font.ITALIC));

        area.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (area.getText().equals(placeholder)) {
                    area.setText("");
                    area.setForeground(Color.BLACK);
                    area.setFont(area.getFont().deriveFont(Font.PLAIN));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (area.getText().isEmpty()) {
                    area.setText(placeholder);
                    area.setForeground(Color.GRAY);
                    area.setFont(area.getFont().deriveFont(Font.ITALIC));
                }
            }
        });
    }

    /**
     * Crea un botón de búsqueda con estilo personalizado.
     * @param texto Texto del botón
     * @return Botón configurado
     */
    private JButton crearBotonBuscar(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.PLAIN, 14));
        boton.setBackground(new Color(0, 122, 204));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(100, 180, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(new Color(0, 122, 204));
            }
        });

        return boton;
    }

    /**
     * Crea un botón de cerrar sesión con estilo personalizado.
     * @param texto Texto del botón
     * @return Botón configurado
     */
    public JButton crearBotonCerrarSesion(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.PLAIN, 14));
        boton.setBackground(new Color(255, 0, 0));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(255, 102, 102));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(new Color(255, 0, 0));
            }
        });

        return boton;
    }

    /**
     * Crea un botón de guardar con estilo personalizado.
     * @param texto Texto del botón
     * @return Botón configurado
     */
    public JButton crearBotonGuardar(String texto) {
        return crearBotonAzul(texto);
    }

    /**
     * Crea un botón de editar con estilo personalizado.
     * @param texto Texto del botón
     * @return Botón configurado
     */
    private JButton crearBotonEditar(String texto) {
        return crearBotonAzul(texto);
    }

    /**
     * Crea un botón de eliminar con estilo personalizado.
     * @param texto Texto del botón
     * @return Botón configurado
     */
    private JButton crearBotonEliminar(String texto) {
        return crearBotonAzul(texto);
    }

    /**
     * Crea un botón de limpiar con estilo personalizado.
     * @param texto Texto del botón
     * @return Botón configurado
     */
    private JButton crearBotonLimpiar(String texto) {
        return crearBotonAzul(texto);
    }

    /**
     * Crea un botón de login con estilo personalizado.
     * @param texto Texto del botón
     * @return Botón configurado
     */
    private JButton crearBotonLogin(String texto) {
        return crearBotonAzul(texto);
    }

    /**
     * Crea un botón de registro con estilo personalizado.
     * @param texto Texto del botón
     * @return Botón configurado
     */
    private JButton crearBotonRegistro(String texto) {
        return crearBotonAzul(texto);
    }

    /**
     * Método base para crear botones azules con efecto hover.
     * @param texto Texto del botón
     * @return Botón configurado
     */
    private JButton crearBotonAzul(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.PLAIN, 14));
        boton.setBackground(new Color(0, 122, 204));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(100, 180, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(new Color(0, 122, 204));
            }
        });

        return boton;
    }

    /**
     * Muestra la pantalla de login.
     */
    public void MostrarLogin() {
        notasFrame.setVisible(false);
        login.setLocationRelativeTo(null);
        login.setVisible(true);
    }

    /**
     * Muestra la pantalla principal de notas.
     */
    public void MostrarPantallaNotas() {
        login.setVisible(false);
        notasFrame.setLocationRelativeTo(null);
        notasFrame.setVisible(true);
    }

    /**
     * Actualiza la lista de notas mostrada.
     * @param titulos Conjunto de títulos de notas a mostrar
     */
    public void actualizarListaNotas(Set<String> titulos) {
        ModeloDeLista.clear();
        for (String titulo : titulos) {
            ModeloDeLista.addElement(titulo);
        }
    }

    /**
     * Obtiene el frame principal de notas.
     * @return Frame de la pantalla de notas
     */
    public JFrame getFrame() {
        return this.notasFrame;
    }

    /**
     * Muestra el contenido de una nota en los campos de edición.
     * @param titulo Título de la nota
     * @param contenido Contenido de la nota
     */
    public void mostrarContenidoNota(String titulo, String contenido) {
        this.titulo.setText(titulo != null ? titulo : "");
        this.areaContenido.setText(contenido != null ? contenido : "");
    }

    /**
     * Limpia los campos de edición de notas.
     */
    public void limpiarCampos() {
        configurarPlaceholder(titulo, "Agregue un título aquí");
        configurarPlaceholder(areaContenido, "Escriba una Nota Aquí");
    }

    /**
     * Limpia los campos del formulario de login.
     */
    public void limpiarCamposLogin() {
        if (campoCorreo != null) campoCorreo.setText("");
        if (campoContraseña != null) campoContraseña.setText("");
    }

    /**
     * Muestra un mensaje al usuario.
     * @param mensaje Texto del mensaje
     * @param titulo Título del diálogo
     * @param tipo Tipo de mensaje (JOptionPane.ERROR_MESSAGE, etc.)
     */
    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(login, mensaje, titulo, tipo);
    }

    /**
     * Muestra un diálogo de confirmación.
     * @param mensaje Texto de la pregunta
     * @param titulo Título del diálogo
     * @return true si el usuario seleccionó "Sí", false en caso contrario
     */
    public boolean mostrarConfirmacion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(
                notasFrame, mensaje, titulo,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /**
     * Obtiene el correo ingresado en el login.
     * @return Correo electrónico
     */
    public String getCorreo() {
        return campoCorreo.getText();
    }

    /**
     * Obtiene la contraseña ingresada en el login.
     * @return Contraseña
     */
    public String getContraseña() {
        return new String(campoContraseña.getPassword());
    }

    /**
     * Obtiene el título de la nota actual.
     * @return Título de la nota o cadena vacía si es el placeholder
     */
    public String getTituloNota() {
        String text = titulo.getText();
        return text.equals("Agregue un título aquí") ? "" : text;
    }

    /**
     * Obtiene el contenido de la nota actual.
     * @return Contenido de la nota o cadena vacía si es el placeholder
     */
    public String getContenidoNota() {
        String text = areaContenido.getText();
        return text.equals("Escriba una Nota Aquí") ? "" : text;
    }

    /**
     * Obtiene la nota seleccionada en la lista.
     * @return Título de la nota seleccionada o null si no hay selección
     */
    public String getNotaSeleccionada() {
        return listaDeNotas.getSelectedValue();
    }

    // Getters para los botones
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