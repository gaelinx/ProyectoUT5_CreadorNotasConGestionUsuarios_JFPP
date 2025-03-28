/**
 * Clase Modelo de la aplicación encargada de la logica inicial.
 */
package com.Modelo;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Base64;

public class Modelo {
    /** Nombre del archivo que almacena los usuarios registrados */
    private static final String ARCHIVO_USUARIOS = "usuarios.csv";
    /** Directorio base donde se almacenan los datos de la aplicación, en este caso se guarda de manera generica en el Disco Local D */
    private static final String DIRECTORIO_BASE = "D:";
    /** Nombre del directorio que contiene los usuarios */
    private static final String DIRECTORIO_USUARIOS = "Usuarios";
    /** Nombre del directorio que contiene las notas de cada usuario */
    private static final String DIRECTORIO_NOTAS = "Notas";

    /** Mapa que almacena las notas del usuario actual en memoria */
    private final Map<String, String> notas;
    /** Correo del usuario actualmente autenticado */
    private String usuarioActual;

    /**
     * Constructor que inicializa la estructura de directorios y el mapa de notas.
     */
    public Modelo() {
        this.notas = new HashMap<>();
        inicializarEstructura();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param correo Correo electrónico del usuario (debe ser único)
     * @param contraseña Contraseña del usuario (será hasheada)
     * @return true si el registro fue exitoso, false si el usuario ya existe o hubo error
     */
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

    /**
     * Autentica a un usuario en el sistema.
     * @param correo Correo electrónico del usuario
     * @param contraseña Contraseña proporcionada
     * @return true si las credenciales son válidas, false en caso contrario
     */
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

    /**
     * Genera un salt aleatorio para hashing de contraseñas.
     * @return String con el salt codificado en Base64
     */
    private String generarSalt() {
        byte[] saltBytes = new byte[16];
        ThreadLocalRandom.current().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    /**
     * Genera un hash de la contraseña usando el algoritmo DJB2.
     * @param input Contraseña a hashear
     * @param salt Valor salt para aumentar seguridad
     * @return String con el hash resultante (64 caracteres hexadecimales)
     */
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

    /**
     * Inicializa la estructura de directorios necesaria para la aplicación.
     */
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

    /**
     * Crea los directorios base si no existen.
     */
    private void crearDirectoriosBase() {
        try {
            Files.createDirectories(Paths.get(DIRECTORIO_BASE, DIRECTORIO_USUARIOS));
        } catch (IOException e) {
            System.err.println("Error al crear directorios base: " + e.getMessage());
        }
    }

    /**
     * Verifica si un usuario ya está registrado.
     * @param correo Correo electrónico a verificar
     * @return true si el usuario existe, false en caso contrario
     */
    private boolean usuarioExiste(String correo) {
        Path rutaArchivo = Paths.get(DIRECTORIO_BASE, DIRECTORIO_USUARIOS, ARCHIVO_USUARIOS);
        try (BufferedReader reader = Files.newBufferedReader(rutaArchivo)) {
            return reader.lines().anyMatch(linea -> linea.startsWith(correo + ","));
        } catch (IOException e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Crea el directorio personal para un usuario.
     * @param correo Correo del usuario
     * @return true si se creó exitosamente, false en caso de error
     */
    private boolean crearDirectorioUsuario(String correo) {
        try {
            Files.createDirectories(obtenerRutaDirectorioNotas(correo));
            return true;
        } catch (IOException e) {
            System.err.println("Error al crear directorio de usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carga todas las notas de un usuario desde el sistema de archivos.
     * @param correo Correo del usuario cuyas notas se cargarán
     * @return Mapa con los títulos y contenidos de las notas
     */
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

    /**
     * Almacena una nueva nota o actualiza una existente.
     * @param titulo Título de la nota
     * @param contenido Contenido de la nota
     * @return true si se guardó exitosamente, false en caso de error
     */
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

    /**
     * Elimina una nota del sistema.
     * @param titulo Título de la nota a eliminar
     * @return true si se eliminó exitosamente, false en caso de error o si no existía
     */
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

    /**
     * Filtra las notas por contenido de texto.
     * @param busqueda Texto a buscar en los títulos de las notas
     * @return Mapa con las notas que coinciden con la búsqueda
     */
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

    /**
     * Limpia caracteres inválidos de un nombre de archivo.
     * @param nombre Nombre original
     * @return Nombre limpio seguro para usar como archivo
     */
    private String limpiarNombreArchivo(String nombre) {
        return nombre.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    /**
     * Obtiene la ruta del directorio de notas de un usuario.
     * @param correo Correo del usuario
     * @return Path del directorio de notas
     */
    private Path obtenerRutaDirectorioNotas(String correo) {
        String correoLimpio = limpiarNombreArchivo(correo);
        return Paths.get(DIRECTORIO_BASE, DIRECTORIO_USUARIOS, correoLimpio, DIRECTORIO_NOTAS);
    }

    /**
     * Obtiene la ruta completa de una nota.
     * @param titulo Título de la nota
     * @return Path completo del archivo de la nota
     */
    private Path obtenerRutaNota(String titulo) {
        return obtenerRutaDirectorioNotas(usuarioActual).resolve(titulo + ".txt");
    }

    /**
     * Obtiene el contenido de una nota específica.
     * @param titulo Título de la nota
     * @return Contenido de la nota o null si no existe
     */
    public String obtenerContenidoNota(String titulo) {
        return notas.get(titulo);
    }

    /**
     * Obtiene el usuario actualmente autenticado.
     * @return Correo del usuario o null si no hay sesión activa
     */
    public String obtenerUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Establece el usuario actual (de manera interna).
     * @param usuario Correo del usuario
     */
    public void establecerUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }

    /**
     * Cierra la sesión actual limpiando los datos en memoria.
     */
    public void cerrarSesion() {
        usuarioActual = null;
        notas.clear();
    }
}