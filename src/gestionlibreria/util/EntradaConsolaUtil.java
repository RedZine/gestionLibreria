package gestionlibreria.util;

import java.util.Scanner;

/**
 * Utilidades para leer datos desde la consola con validaciones básicas.
 */
public final class EntradaConsolaUtil {
    private EntradaConsolaUtil() {
    }

    /**
     * Lee un texto obligatorio desde consola.
     */
    public static String leerTexto(Scanner scanner, String mensaje) {
        String valor;
        do {
            System.out.print(mensaje);
            valor = scanner.nextLine().trim();
        } while (valor.isEmpty());
        return valor;
    }

    /**
     * Lee un entero validando el formato.
     */
    public static int leerEntero(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException ex) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }

    /**
     * Lee un valor decimal validando el formato.
     */
    public static double leerDecimal(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                return Double.parseDouble(entrada);
            } catch (NumberFormatException ex) {
                System.out.println("Ingrese un número decimal válido.");
            }
        }
    }
}
