package gestionlibreria.ui.swing;

import gestionlibreria.exception.PersistenciaException;
import gestionlibreria.repository.CategoriaRepository;
import gestionlibreria.repository.LibroRepository;
import gestionlibreria.util.reporte.LibroReportGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;

public class MainWindow extends JFrame {
    private final CategoriaRepository categoriaRepository;
    private final LibroRepository libroRepository;

    public MainWindow(CategoriaRepository categoriaRepository, LibroRepository libroRepository) {
        super("Sistema de Información - Librería");
        this.categoriaRepository = categoriaRepository;
        this.libroRepository = libroRepository;
        inicializar();
    }

    private void inicializar() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1024, 640);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.addTab("Libros", new BooksPanel(libroRepository, categoriaRepository));
        pestañas.addTab("Categorías", new CategoriesPanel(categoriaRepository));
        add(pestañas, BorderLayout.CENTER);

        setJMenuBar(crearMenu());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                solicitarSalida();
            }
        });
    }

    private JMenuBar crearMenu() {
        JMenuBar barra = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemReporte = new JMenuItem("Generar reporte de libros");
        JMenuItem itemSalir = new JMenuItem("Guardar y salir");

        itemReporte.addActionListener(e -> generarReporteLibros());
        itemSalir.addActionListener(e -> solicitarSalida());

        menuArchivo.add(itemReporte);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        barra.add(menuArchivo);
        return barra;
    }

    private void generarReporteLibros() {
        try {
            LibroReportGenerator generator = new LibroReportGenerator(libroRepository.findAll());
            Path ruta = generator.generar();
            JOptionPane.showMessageDialog(this, "Reporte generado en " + ruta.toAbsolutePath(), "Reporte", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No fue posible generar el reporte: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void solicitarSalida() {
        int opcion = JOptionPane.showConfirmDialog(this, "¿Desea guardar los cambios antes de salir?", "Confirmar salida", JOptionPane.YES_NO_CANCEL_OPTION);
        if (opcion == JOptionPane.CANCEL_OPTION) {
            return;
        }
        if (opcion == JOptionPane.YES_OPTION) {
            guardarDatos();
        }
        dispose();
    }

    private void guardarDatos() {
        try {
            libroRepository.save();
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
