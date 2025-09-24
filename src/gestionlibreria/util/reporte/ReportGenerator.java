package gestionlibreria.util.reporte;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase base para generadores de reportes en texto plano.
 */
public abstract class ReportGenerator {
    private final Path directorio;

    protected ReportGenerator() {
        this(Paths.get("reportes"));
    }

    protected ReportGenerator(Path directorio) {
        this.directorio = directorio;
    }

    public Path generar() throws IOException {
        Files.createDirectories(directorio);
        Path archivo = directorio.resolve(nombreArchivo());
        try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            writer.write("Reporte generado el " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            writer.newLine();
            writer.write("----------------------------------------------------");
            writer.newLine();
            escribirContenido(writer);
        }
        return archivo;
    }

    protected abstract String nombreArchivo();

    protected abstract void escribirContenido(BufferedWriter writer) throws IOException;
}
