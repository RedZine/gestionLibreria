package gestionlibreria.repository;

import gestionlibreria.model.Libro;
import gestionlibreria.util.DatoObligatorioException;
import gestionlibreria.util.EntidadNoEncontradaException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CsvLibroRepository implements LibroRepository {

    // Formato del CSV: isbn,titulo,autor,categoria,precio,stock (con cabecera)
    private final Path rutaArchivo;
    private final List<Libro> memoria = new ArrayList<>();

    public CsvLibroRepository(String ruta) {
        this.rutaArchivo = Paths.get(ruta);
    }

    /** Valida campos obligatorios y reglas simples del negocio. */
    private void validar(Libro libro) {
        if (libro == null) throw new DatoObligatorioException("El libro es obligatorio.");
        if (libro.getIsbn() == null || libro.getIsbn().trim().isEmpty())
            throw new DatoObligatorioException("El ISBN es obligatorio.");
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty())
            throw new DatoObligatorioException("El título es obligatorio.");
        if (libro.getAutor() == null || libro.getAutor().trim().isEmpty())
            throw new DatoObligatorioException("El autor es obligatorio.");
        if (libro.getPrecio() < 0)
            throw new DatoObligatorioException("El precio no puede ser negativo.");
        if (libro.getStock() < 0)
            throw new DatoObligatorioException("El stock no puede ser negativo.");
    }

    // ------------------- Persistencia (SIA2.2 / SIA2.10) -------------------

    @Override
    public void load() {
        memoria.clear();
        if (!Files.exists(rutaArchivo)) {
            System.out.println("[CSV] No existe " + rutaArchivo + ". Se cargan 0 libros.");
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(rutaArchivo, StandardCharsets.UTF_8)) {
            String linea = br.readLine(); // cabecera
            while ((linea = br.readLine()) != null) {
                String[] c = linea.split(",", -1); // CSV simple (sin comillas)
                if (c.length < 6) continue;

                Libro l = new Libro();
                l.setIsbn(c[0].trim());
                l.setTitulo(c[1].trim());
                l.setAutor(c[2].trim());
                l.setCategoria(c[3].trim());
                l.setPrecio(parsearDouble(c[4]));
                l.setStock(parsearEntero(c[5]));
                memoria.add(l);
            }
            System.out.println("[CSV] Cargados " + memoria.size() + " libros desde " + rutaArchivo);
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo CSV: " + e.getMessage(), e);
        }
    }

    @Override
    public void save() {
        try {
            if (rutaArchivo.getParent() != null) {
                Files.createDirectories(rutaArchivo.getParent());
            }
            try (BufferedWriter bw = Files.newBufferedWriter(rutaArchivo, StandardCharsets.UTF_8)) {
                bw.write("isbn,titulo,autor,categoria,precio,stock");
                bw.newLine();
                for (Libro l : memoria) {
                    bw.write(String.join(",",
                        limpiarCsv(l.getIsbn()),
                        limpiarCsv(l.getTitulo()),
                        limpiarCsv(l.getAutor()),
                        limpiarCsv(l.getCategoria()),
                        String.valueOf(l.getPrecio()),
                        String.valueOf(l.getStock())
                    ));
                    bw.newLine();
                }
            }
            System.out.println("[CSV] Guardados " + memoria.size() + " libros en " + rutaArchivo);
        } catch (IOException e) {
            throw new RuntimeException("Error escribiendo CSV: " + e.getMessage(), e);
        }
    }

    // ------------------- CRUD + Filtros -------------------

    @Override
    public List<Libro> findAll() {
        return new ArrayList<>(memoria);
    }

    @Override
    public Optional<Libro> findByIsbn(String isbn) {
        for (Libro l : memoria) {
            if (l.getIsbn().equalsIgnoreCase(isbn)) {
                return Optional.of(l);
            }
        }
        return Optional.empty();
    }

    @Override
    public void add(Libro libro) {
        validar(libro);
        if (findByIsbn(libro.getIsbn()).isPresent())
            throw new DatoObligatorioException("ISBN duplicado: " + libro.getIsbn());
        memoria.add(libro);
    }

    @Override
    public void update(Libro libro) {
        validar(libro);
        for (int i = 0; i < memoria.size(); i++) {
            if (memoria.get(i).getIsbn().equalsIgnoreCase(libro.getIsbn())) {
                memoria.set(i, libro);
                return;
            }
        }
        throw new EntidadNoEncontradaException("No existe el ISBN: " + libro.getIsbn());
    }

    @Override
    public void deleteByIsbn(String isbn) {
        boolean eliminado = memoria.removeIf(l -> l.getIsbn().equalsIgnoreCase(isbn));
        if (!eliminado) {
            throw new EntidadNoEncontradaException("No existe el ISBN: " + isbn);
        }
    }

    @Override
    public List<Libro> filterByPrecio(double minimo, double maximo) {
        List<Libro> resultado = new ArrayList<>();
        for (Libro l : memoria) {
            double p = l.getPrecio();
            if (p >= minimo && p <= maximo) {
                resultado.add(l);
            }
        }
        return resultado;
    }

    @Override
    public List<Libro> filterByCategoria(String categoria) {
        String filtro = categoria == null ? "" : categoria.trim().toLowerCase();
        List<Libro> resultado = new ArrayList<>();
        for (Libro l : memoria) {
            String cat = l.getCategoria() == null ? "" : l.getCategoria().trim().toLowerCase();
            if (cat.contains(filtro)) {
                resultado.add(l);
            }
        }
        return resultado;
    }

    @Override
    public List<Libro> filterByAutor(String autorContiene) {
        String filtro = autorContiene == null ? "" : autorContiene.trim().toLowerCase();
        List<Libro> resultado = new ArrayList<>();
        for (Libro l : memoria) {
            String au = l.getAutor() == null ? "" : l.getAutor().trim().toLowerCase();
            if (au.contains(filtro)) {
                resultado.add(l);
            }
        }
        return resultado;
    }
    
    @Override
public List<Libro> filterByStock(int minimo, int maximo) {
    // Normalizamos por si vienen invertidos
    int desde = Math.min(minimo, maximo);
    int hasta = Math.max(minimo, maximo);

    List<Libro> resultado = new ArrayList<>();
    for (Libro l : memoria) {
        int s = l.getStock();
        if (s >= desde && s <= hasta) {
            resultado.add(l);
        }
    }
    return resultado;
}

    // ------------------- utilidades -------------------

    private static int parsearEntero(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }

    private static double parsearDouble(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return 0.0; }
    }

    /** Reemplaza comas para no romper el CSV simple. */
    private static String limpiarCsv(String v) {
        return (v == null) ? "" : v.replace(",", " ").trim();
    }
}
