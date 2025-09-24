package gestionlibreria.util.reporte;

import gestionlibreria.model.Libro;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class LibroReportGenerator extends ReportGenerator {
    private final List<Libro> libros;

    public LibroReportGenerator(List<Libro> libros) {
        this.libros = libros;
    }

    @Override
    protected String nombreArchivo() {
        return "reporte_libros.txt";
    }

    @Override
    protected void escribirContenido(BufferedWriter writer) throws IOException {
        if (libros == null || libros.isEmpty()) {
            writer.write("No hay libros registrados.");
            writer.newLine();
            return;
        }
        writer.write(String.format("%-15s %-30s %-20s %-15s %-8s %-6s", "ISBN", "Título", "Autor", "Categoría", "Precio", "Stock"));
        writer.newLine();
        for (Libro libro : libros) {
            writer.write(String.format("%-15s %-30s %-20s %-15s %8.2f %6d",
                    safe(libro.getIsbn()),
                    safe(libro.getTitulo()),
                    safe(libro.getAutor()),
                    safe(libro.getCategoria()),
                    libro.getPrecio(),
                    libro.getStock()));
            writer.newLine();
        }
    }

    private String safe(String texto) {
        return texto == null ? "" : texto;
    }
}
