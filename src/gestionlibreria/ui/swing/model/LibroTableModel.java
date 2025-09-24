package gestionlibreria.ui.swing.model;

import gestionlibreria.model.Libro;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel personalizado que representa los libros en una tabla Swing.
 * Implementa sobreescritura de métodos de {@link AbstractTableModel}
 * para cumplir con SIA2.7.
 */
public class LibroTableModel extends AbstractTableModel {
    private final List<Libro> libros = new ArrayList<>();
    private final String[] columnas = {"ISBN", "Título", "Autor", "Categoría", "Precio", "Stock"};

    public void setLibros(List<Libro> nuevosLibros) {
        libros.clear();
        if (nuevosLibros != null) {
            libros.addAll(nuevosLibros);
        }
        fireTableDataChanged();
    }

    public Libro getLibroAt(int fila) {
        if (fila < 0 || fila >= libros.size()) {
            return null;
        }
        return libros.get(fila);
    }

    @Override
    public int getRowCount() {
        return libros.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnas[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 4) return Double.class;
        if (columnIndex == 5) return Integer.class;
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Libro libro = libros.get(rowIndex);
        switch (columnIndex) {
            case 0: return libro.getIsbn();
            case 1: return libro.getTitulo();
            case 2: return libro.getAutor();
            case 3: return libro.getCategoria();
            case 4: return libro.getPrecio();
            case 5: return libro.getStock();
            default: return null;
        }
    }
}
