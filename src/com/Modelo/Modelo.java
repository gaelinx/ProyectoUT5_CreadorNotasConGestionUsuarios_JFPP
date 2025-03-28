package com.Modelo;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Base64;

public class Modelo {
    // Constantes
    private static final String ARCHIVO_USUARIOS = "usuarios.csv";
    private static final String DIRECTORIO_BASE = "D:";
    private static final String DIRECTORIO_USUARIOS = "Usuarios";
    private static final String DIRECTORIO_NOTAS = "Notas";

    // Variables
    private final Map<String, String> notas;
    private String usuarioActual;

    public Modelo() {
        this.notas = new HashMap<>();
        inicializarEstructura();
    }

    //Métodos de Autenticación
    public boolean registrarUsuario(String correo, String contraseña) {
        if (usuarioExiste(correo)) return false;

        String salt = generarSalt();
        String hash = hash(contraseña, salt);

        // Guardar en formato: correo,salt,hash
        Path rutaArchivo = Paths.get(DIRECTORIO_BASE, DIRECTORIO_USUARIOS, ARCHIVO_USUARIOS);
        try (BufferedWriter writer = Files.newBufferedWriter(rutaArchivo,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

            writer.write(String.format("%s,%s,%s\n", correo, salt, hash));
            crearDirectorioUsuario(correo);
            return true;

        } catch (IOException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean autenticarUsuario(String correo, String contraseña) {
        Path rutaArchivo = Paths.get(DIRECTORIO_BASE, DIRECTORIO_USUARIOS, ARCHIVO_USUARIOS);

        try (BufferedReader reader = Files.newBufferedReader(rutaArchivo)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3 && partes[0].equals(correo)) {
                    String salt = partes[1];
                    String hashAlmacenado = partes[2];
                    String hashCalculado = hash(contraseña, salt);
                    return hashAlmacenado.equals(hashCalculado);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        return false;
    }

    // Hash
    private String generarSalt() {
        byte[] saltBytes = new byte[16];
        ThreadLocalRandom.current().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    private String hash(String input, String salt) {
        long hash = 5381L;
        String saltedInput = salt + input;

        for (int i = 0; i < saltedInput.length(); i++) {
            hash = ((hash << 5) + hash) + saltedInput.charAt(i); // DJB2 algorithm
        }

        // esto convierte a un hexadecimal de 64 caracteres
        String hex = Long.toHexString(hash);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            result.append(hex.charAt((i * 13) % hex.length()));
        }
        return result.toString();
    }

    private void inicializarEstructura() {
        crearDirectoriosBase();
        if (!Files.exists(Paths.get(DIRECTORIO_BASE, DIRECTORIO_USUARIOS, ARCHIVO_USUARIOS))) {
            try {
                Files.createFile(Paths.get(DIRECTORIO_BASE, DIRECTORIO_USUARIOS, ARCHIVO_USUARIOS));
            } catch (IOException e) {
                System.err.println("Error al crear archivo de usuarios: " + e.getMessage());
            }
        }
    }

    private void crearDirectoriosBase() {
        try {
            Files.createDirectories(Paths.get(DIRECTORIO_BASE, DIRECTORIO_USUARIOS));
        } catch (IOException e) {
            System.err.println("Error al crear directorios base: " + e.getMessage());
        }
    }

    private boolean usuarioExiste(String correo) {
        Path rutaArchivo = Paths.get(DIRECTORIO_BASE, DIRECTORIO_USUARIOS, ARCHIVO_USUARIOS);
        try (BufferedReader reader = Files.newBufferedReader(rutaArchivo)) {
            return reader.lines().anyMatch(linea -> linea.startsWith(correo + ","));
        } catch (IOException e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
            return false;
        }
    }

    private boolean crearDirectorioUsuario(String correo) {
        try {
            Files.createDirectories(obtenerRutaDirectorioNotas(correo));
            return true;
        } catch (IOException e) {
            System.err.println("Error al crear directorio de usuario: " + e.getMessage());
            return false;
        }
    }

    // Notas
    public Map<String, String> cargarNotasUsuario(String correo) {
        notas.clear();
        usuarioActual = correo;
        Path directorio = obtenerRutaDirectorioNotas(correo);

        if (!Files.exists(directorio)) {
            try {
                Files.createDirectories(directorio);
            } catch (IOException e) {
                System.err.println("Error al crear directorio de notas: " + e.getMessage());
            }
            return notas;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directorio, "*.txt")) {
            for (Path archivo : stream) {
                String titulo = archivo.getFileName().toString().replace(".txt", "");
                String contenido = Files.readString(archivo);
                notas.put(titulo, contenido);
            }
        } catch (IOException e) {
            System.err.println("Error al cargar notas: " + e.getMessage());
        }

        return new HashMap<>(notas);
    }

    public boolean almacenarNotaEnArchivo(String titulo, String contenido) {
        if (usuarioActual == null || titulo.isEmpty()) {
            return false;
        }

        String tituloLimpio = limpiarNombreArchivo(titulo);
        Path rutaArchivo = obtenerRutaNota(tituloLimpio);

        try {
            Files.writeString(rutaArchivo, contenido, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            notas.put(tituloLimpio, contenido);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar nota: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarNota(String titulo) {
        if (usuarioActual == null || titulo == null) {
            return false;
        }

        Path rutaArchivo = obtenerRutaNota(titulo);
        try {
            boolean eliminado = Files.deleteIfExists(rutaArchivo);
            if (eliminado) {
                notas.remove(titulo);
            }
            return eliminado;
        } catch (IOException e) {
            System.err.println("Error al eliminar nota: " + e.getMessage());
            return false;
        }
    }

    public Map<String, String> filtrarNotasPorTexto(String busqueda) {
        Map<String, String> resultados = new HashMap<>();
        if (busqueda == null || busqueda.isEmpty()) {
            return new HashMap<>(notas); // Devuelve copia de todas las notas
        }

        String busquedaLower = busqueda.toLowerCase();
        notas.forEach((titulo, contenido) -> {
            if (titulo.toLowerCase().contains(busquedaLower)) {
                resultados.put(titulo, contenido);
            }
        });

        return resultados;
    }

    // Utilidades
    private String limpiarNombreArchivo(String nombre) {
        return nombre.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private Path obtenerRutaDirectorioNotas(String correo) {
        String correoLimpio = limpiarNombreArchivo(correo);
        return Paths.get(DIRECTORIO_BASE, DIRECTORIO_USUARIOS, correoLimpio, DIRECTORIO_NOTAS);
    }

    private Path obtenerRutaNota(String titulo) {
        return obtenerRutaDirectorioNotas(usuarioActual).resolve(titulo + ".txt");
    }

    // Getters
    public String obtenerContenidoNota(String titulo) {
        return notas.get(titulo);
    }

    public String obtenerUsuarioActual() {
        return usuarioActual;
    }

    public void establecerUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrarSesion() {
        usuarioActual = null;
        notas.clear();
    }
}