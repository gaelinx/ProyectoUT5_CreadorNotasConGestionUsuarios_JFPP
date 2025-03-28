package com.Controlador;

import com.Modelo.Modelo;
import com.Vista.Vista;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class Controlador {
    private final Modelo modelo;
    private final Vista vista;

    public Controlador(Modelo modelo, Vista vista) {
        this.modelo = modelo;
        this.vista = vista;

        configurarListeners();
        vista.MostrarLogin();
    }

    private void CerrarSesion() {
        modelo.cerrarSesion();
        vista.limpiarCampos();
        vista.MostrarLogin();
    }

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

    private void manejarBusqueda() {
        String usuarioActual = modelo.obtenerUsuarioActual();
        if (usuarioActual == null) {
            vista.mostrarMensaje("No hay sesión activa", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Configurar panel de búsqueda
            JTextField campoBusqueda = new JTextField(20);
            JPanel panelBusqueda = new JPanel(new BorderLayout());
            panelBusqueda.add(new JLabel("Nombre de la nota:"), BorderLayout.NORTH);
            panelBusqueda.add(campoBusqueda, BorderLayout.CENTER);

            // Mostrar diálogo
            int opcion = JOptionPane.showOptionDialog(
                    vista.getFrame(),
                    panelBusqueda,
                    "Buscar Notas",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new Object[]{"Buscar", "Ver todas", "Cancelar"},
                    "Buscar"
            );

            // Procesar opción
            if (opcion == 0) { // Buscar
                String termino = campoBusqueda.getText().trim();
                if (!termino.isEmpty()) {
                    Map<String, String> resultados = modelo.filtrarNotasPorTexto(termino);
                    vista.actualizarListaNotas(resultados.keySet());
                }
            }
            else if (opcion == 1) { // Ver todas
                Map<String, String> todasNotas = modelo.cargarNotasUsuario(usuarioActual);
                vista.actualizarListaNotas(todasNotas.keySet());
                vista.limpiarCampos(); // Esto solo limpiará título y contenido
            }

        } catch (Exception e) {
            vista.mostrarMensaje("Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void cargarNotasUsuario() {
        String correo = modelo.obtenerUsuarioActual();
        if (correo != null) {
            Map<String, String> notas = modelo.cargarNotasUsuario(correo);
            vista.actualizarListaNotas(notas.keySet());
        }
    }

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

    private void manejarEditarNota() {
        String notaSeleccionada = vista.getNotaSeleccionada();
        if (notaSeleccionada == null) {
            vista.mostrarMensaje("Seleccione una nota para editar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        vista.mostrarContenidoNota(notaSeleccionada, modelo.obtenerContenidoNota(notaSeleccionada));
    }

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

    private void cargarNotaSeleccionada() {
        String notaSeleccionada = vista.getNotaSeleccionada();
        if (notaSeleccionada != null) {
            String contenido = modelo.obtenerContenidoNota(notaSeleccionada);
            vista.mostrarContenidoNota(notaSeleccionada, contenido);
        }
    }

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