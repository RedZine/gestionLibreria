# Instrucciones de uso

1. Compila el proyecto ejecutando `javac -d build/classes $(find src -name "*.java")`.
2. Para ejecutar la versión con interfaz gráfica, usa `java -cp build/classes gestionlibreria.app.Main`.
3. Para utilizar el menú de consola, ejecuta `java -cp build/classes gestionlibreria.app.Main --console`.
4. Los datos se cargan desde la carpeta `data` al iniciar y se guardan automáticamente al salir.
5. Los reportes se generan en la carpeta `reportes` mediante el menú **Archivo > Generar reporte de libros**.
