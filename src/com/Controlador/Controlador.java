/**
 * Clase controlador que Elabora y Une la logica entre la vista y el modelo
 * coordina las interacciones del usuario en el flujo de la aplicacion
 */

package com.Controlador;

import com.Modelo.Modelo;
import com.Vista.Vista;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class Controlador {
    /** Instancia del modelo que maneja los datos */
    private final Modelo modelo;
    /** Instancia de la vista que maneja la interfaz gráfica */
    private final Vista vista;

    /**
     * Constructor del controlador que inicializa las dependencias.
     * @param modelo Instancia del modelo
     * @param vista Instancia de la vista
     */
    public Controlador(Modelo modelo, Vista vista) {
        this.modelo = modelo;
        this.vista = vista;
        configurarListeners();
        vista.MostrarLogin();
    }

    /**
     * Cierra la sesión actual y vuelve a la pantalla de login.
     */
    private void CerrarSesion() {
        modelo.cerrarSesion();
        vista.limpiarCampos();
        vista.MostrarLogin();
    }

    /**
     * Configura todos los listeners para los componentes de la vista.
     */
    private void configurarListeners() {
        // Listeners de Login
        vista.getBotonLogin().addActionListener(e -> manejarLogin());
        vista.getBotonRegistro().addActionListener(e -> manejarRegistro());

        // Listeners de Notas
        vista.getBotonGuardar().addActionListener(e -> manejarGuardarNota());
        vista.getBotonEditar().addActionListener(e -> manejarEditarNota());
        vista.getBotonEliminar().addActionListener(e -> manejarEliminarNota());
        vista.getBotonLimpiar().addActionListener(e -> vista.limpiarCampos());
        vista.getBotonBusqueda().addActionListener(e -> manejarBusqueda());
        vista.getBotonCerrarSesion().addActionListener(e -> CerrarSesion());

        // Listener de selección en la lista
        vista.getListaDeNotas().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    cargarNotaSeleccionada();
                }
            }
        });
    }

    /**
     * Maneja el proceso de inicio de sesión.
     * Verifica las credenciales y carga las notas si son correctas.
     */
    private void manejarLogin() {
        String correo = vista.getCorreo();
        String contraseña = vista.getContraseña();

        if (modelo.autenticarUsuario(correo, contraseña)) {
            modelo.establecerUsuarioActual(correo);
            cargarNotasUsuario();
            vista.MostrarPantallaNotas();
        } else {
            vista.mostrarMensaje("Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Maneja la búsqueda de notas mostrando un diálogo personalizado.
     * Permite buscar por texto o ver todas las notas.
     */
    private void manejarBusqueda() {
        String usuarioActual = modelo.obtenerUsuarioActual();
        if (usuarioActual == null) {
            vista.mostrarMensaje("No hay sesión activa", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            JPanel panelBusqueda = new JPanel(new BorderLayout(10, 10));
            panelBusqueda.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel etiqueta = new JLabel("Nombre de la nota:");
            etiqueta.setFont(new Font("Arial", Font.BOLD, 14));
            panelBusqueda.add(etiqueta, BorderLayout.NORTH);

            JTextField campoBusqueda = new JTextField(20);
            campoBusqueda.setFont(new Font("Arial", Font.PLAIN, 14));
            campoBusqueda.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 122, 204)),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            panelBusqueda.add(campoBusqueda, BorderLayout.CENTER);

            JButton btnBuscar = vista.crearBotonGuardar("Buscar");
            JButton btnVerTodas = vista.crearBotonGuardar("Ver todas");
            JButton btnCancelar = vista.crearBotonCerrarSesion("Cancelar");

            btnBuscar.setFont(new Font("Arial", Font.BOLD, 14));
            btnVerTodas.setFont(new Font("Arial", Font.BOLD, 14));
            btnCancelar.setFont(new Font("Arial", Font.BOLD, 14));

            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            panelBotones.add(btnBuscar);
            panelBotones.add(btnVerTodas);
            panelBotones.add(btnCancelar);

            JDialog dialogo = new JDialog(vista.getFrame(), "Buscar Notas", true);
            dialogo.setLayout(new BorderLayout());
            dialogo.add(panelBusqueda, BorderLayout.CENTER);
            dialogo.add(panelBotones, BorderLayout.SOUTH);

            btnBuscar.addActionListener(e -> {
                String termino = campoBusqueda.getText().trim();
                if (!termino.isEmpty()) {
                    Map<String, String> resultados = modelo.filtrarNotasPorTexto(termino);
                    vista.actualizarListaNotas(resultados.keySet());
                }
                dialogo.dispose();
            });

            btnVerTodas.addActionListener(e -> {
                Map<String, String> todasNotas = modelo.cargarNotasUsuario(usuarioActual);
                vista.actualizarListaNotas(todasNotas.keySet());
                vista.limpiarCampos();
                dialogo.dispose();
            });

            btnCancelar.addActionListener(e -> dialogo.dispose());

            dialogo.pack();
            dialogo.setLocationRelativeTo(vista.getFrame());
            dialogo.setVisible(true);

        } catch (Exception e) {
            vista.mostrarMensaje("Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga las notas del usuario actual desde el modelo.
     */
    private void cargarNotasUsuario() {
        String correo = modelo.obtenerUsuarioActual();
        if (correo != null) {
            Map<String, String> notas = modelo.cargarNotasUsuario(correo);
            vista.actualizarListaNotas(notas.keySet());
        }
    }

    /**
     * Maneja el guardado de una nueva nota o la actualización de una existente.
     * Valida que el título no esté vacío antes de guardar.
     */
    private void manejarGuardarNota() {
        String titulo = vista.getTituloNota();
        String contenido = vista.getContenidoNota();

        if (titulo.isEmpty()) {
            vista.mostrarMensaje("El título no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (modelo.almacenarNotaEnArchivo(titulo, contenido)) {
            cargarNotasUsuario();
            vista.mostrarMensaje("Nota guardada con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            vista.mostrarMensaje("Error al guardar la nota", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Maneja la edición de una nota existente.
     * Carga el contenido de la nota seleccionada en los campos de edición.
     */
    private void manejarEditarNota() {
        String notaSeleccionada = vista.getNotaSeleccionada();
        if (notaSeleccionada == null) {
            vista.mostrarMensaje("Seleccione una nota para editar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        vista.mostrarContenidoNota(notaSeleccionada, modelo.obtenerContenidoNota(notaSeleccionada));
    }

    /**
     * Maneja la eliminación de una nota existente.
     * Solicita confirmación antes de eliminar.
     */
    private void manejarEliminarNota() {
        String notaSeleccionada = vista.getNotaSeleccionada();
        if (notaSeleccionada == null) {
            vista.mostrarMensaje("Seleccione una nota para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (vista.mostrarConfirmacion("¿Está seguro de eliminar esta nota?", "Confirmar")) {
            if (modelo.eliminarNota(notaSeleccionada)) {
                cargarNotasUsuario();
                vista.limpiarCampos();
                vista.mostrarMensaje("Nota eliminada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                vista.mostrarMensaje("Error al eliminar la nota", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Carga el contenido de la nota seleccionada en la lista.
     */
    private void cargarNotaSeleccionada() {
        String notaSeleccionada = vista.getNotaSeleccionada();
        if (notaSeleccionada != null) {
            String contenido = modelo.obtenerContenidoNota(notaSeleccionada);
            vista.mostrarContenidoNota(notaSeleccionada, contenido);
        }
    }

    /**
     * Maneja el registro de nuevos usuarios.
     * Valida los datos ingresados antes de registrar.
     */
    private void manejarRegistro() {
        try {
            String correo = vista.getCorreo();
            String contraseña = vista.getContraseña();

            // Validaciones básicas
            if (correo.isEmpty() || contraseña.isEmpty()) {
                vista.mostrarMensaje("Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                vista.mostrarMensaje("Ingrese un correo electrónico válido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Intentar registro
            if (modelo.registrarUsuario(correo, contraseña)) {
                vista.mostrarMensaje("Registro exitoso. Ahora puede iniciar sesión",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarCamposLogin();
            } else {
                vista.mostrarMensaje("El usuario ya existe o hubo un error",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            vista.mostrarMensaje("Error durante el registro: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}