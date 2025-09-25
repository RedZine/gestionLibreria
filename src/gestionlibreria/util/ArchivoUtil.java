package gestionlibreria.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad para leer y escribir archivos de texto en formato UTF-8.
 */
public final class ArchivoUtil {
    private ArchivoUtil() {
    }

    /**
     * Lee todas las líneas de un archivo, creando el archivo si no existe.
     */
    public static List<String> leerLineas(String ruta) throws IOException {
        Path path = Paths.get(ruta);
        if (Files.notExists(path)) {
            crearArchivo(path);
            return new ArrayList<>();
        }
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    /**
     * Escribe todas las líneas recibidas en el archivo indicado.
     */
    public static void escribirLineas(String ruta, List<String> lineas) throws IOException {
        Path path = Paths.get(ruta);
        crearArchivo(path);
        Files.write(path, lineas, StandardCharsets.UTF_8);
    }

    private static void crearArchivo(Path path) throws IOException {
        if (Files.notExists(path)) {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.createFile(path);
        }
    }
}
