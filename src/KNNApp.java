import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class KNNApp {

    private JFrame frame;
    private JPanel tablesPanel;
    private JButton continueButton;
    private JFrame nextFrame;
    private JTable newTable;
    private List<JTable> classTables;
    private int[] classSizes;

    public KNNApp() {
        frame = new JFrame("k-NN App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Preguntar al usuario cuántas clases se necesitan
        int numClasses = obtenerNumeroDeClases();

        // Crear un arreglo para almacenar el tamaño de cada clase
        classSizes = new int[numClasses];

        // Preguntar al usuario el tamaño de cada clase
        obtenerTamanioDeCadaClase(numClasses, classSizes);

        // Crear panel para contener las tablas
        tablesPanel = new JPanel();
        tablesPanel.setLayout(new GridLayout(numClasses, 1)); // Layout para distribuir tablas verticalmente

        // Crear tablas según el número de clases y su tamaño
        classTables = new ArrayList<>(); // Lista para almacenar tablas de clases
        for (int i = 0; i < numClasses; i++) {
            int classSize = classSizes[i];
            JPanel classPanel = new JPanel(new BorderLayout());
            classPanel.setBorder(BorderFactory.createTitledBorder("Clase " + (i + 1)));

            // Crear tabla para la clase
            String[] columnNames = {"Característica 1", "Característica 2", "Etiqueta"};
            Object[][] data = new Object[classSize][columnNames.length];
            JTable classTable = new JTable(data, columnNames);

            // Añadir la tabla al panel
            classPanel.add(new JScrollPane(classTable), BorderLayout.CENTER);

            // Añadir el panel de clase al panel de tablas
            tablesPanel.add(classPanel);

            // Añadir la tabla a la lista de tablas de clases
            classTables.add(classTable);
        }

        frame.add(tablesPanel, BorderLayout.CENTER);

        // Añadir botón para continuar a la siguiente interfaz
        continueButton = new JButton("Continuar");
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cuando se hace clic en el botón, abre la siguiente interfaz
                abrirSiguienteInterfaz();
            }
        });

        frame.add(continueButton, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private int obtenerNumeroDeClases() {
        int numClasses = 1; // Valor por defecto en caso de que el usuario no introduzca un valor válido

        // Preguntar al usuario por el número de clases
        String input = JOptionPane.showInputDialog(frame, "¿Cuántas clases se necesitan?");

        try {
            // Convertir la entrada del usuario a un número entero
            numClasses = Integer.parseInt(input);
            if (numClasses <= 0) {
                throw new NumberFormatException("El número de clases debe ser positivo.");
            }
        } catch (NumberFormatException e) {
            // Si la entrada no es válida, mostrar un mensaje de error
            JOptionPane.showMessageDialog(frame, "Por favor, introduce un número entero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            // Volver a pedir la entrada
            return obtenerNumeroDeClases();
        }
        return numClasses;
    }

    private void obtenerTamanioDeCadaClase(int numClasses, int[] classSizes) {
        for (int i = 0; i < numClasses; i++) {
            boolean validInput = false;

            while (!validInput) {
                String input = JOptionPane.showInputDialog(frame, "¿Cuál es el tamaño de la clase " + (i + 1) + "?");

                try {
                    // Convertir la entrada del usuario a un número entero
                    int size = Integer.parseInt(input);
                    if (size > 0) {
                        classSizes[i] = size;
                        validInput = true;
                    } else {
                        throw new NumberFormatException("El tamaño debe ser un número entero positivo.");
                    }
                } catch (NumberFormatException e) {
                    // Si la entrada no es válida, mostrar un mensaje de error
                    JOptionPane.showMessageDialog(frame, "Por favor, introduce un número entero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void abrirSiguienteInterfaz() {
        // Crear una nueva ventana para la siguiente interfaz
        nextFrame = new JFrame("Añadir objetos nuevos");
        nextFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        nextFrame.setSize(400, 300);
        nextFrame.setLayout(new BorderLayout());

        // Preguntar cuántos objetos nuevos se van a añadir
        int numNuevosObjetos = obtenerNumeroDeObjetosNuevos();

        // Crear tabla para los objetos nuevos
        String[] columnNames = {"X", "Y", "Etiqueta"};
        Object[][] data = new Object[numNuevosObjetos][columnNames.length]; // Datos vacíos para empezar
        newTable = new JTable(data, columnNames);

        // Añadir la tabla a la interfaz
        nextFrame.add(new JScrollPane(newTable), BorderLayout.CENTER);

        // Añadir botón para continuar a la siguiente etapa
        JButton siguientePasoButton = new JButton("Siguiente paso");
        siguientePasoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Llamar a la función para el siguiente paso
                realizarCalculos();
            }
        });

        nextFrame.add(siguientePasoButton, BorderLayout.SOUTH);

        // Mostrar la nueva ventana
        nextFrame.setVisible(true);
    }

    private int obtenerNumeroDeObjetosNuevos() {
        int numObjetosNuevos = 1; // Valor por defecto en caso de que el usuario no introduzca un valor válido

        // Preguntar al usuario cuántos objetos nuevos se van a añadir
        String input = JOptionPane.showInputDialog(frame, "¿Cuántos objetos nuevos se van a añadir?");

        try {
            // Convertir la entrada del usuario a un número entero
            numObjetosNuevos = Integer.parseInt(input);
            if (numObjetosNuevos <= 0) {
                throw new NumberFormatException("El número de objetos nuevos debe ser positivo.");
            }
        } catch (NumberFormatException e) {
            // Si la entrada no es válida, mostrar un mensaje de error
            JOptionPane.showMessageDialog(frame, "Por favor, introduce un número entero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            // Volver a pedir la entrada
            return obtenerNumeroDeObjetosNuevos();
        }
        return numObjetosNuevos;
    }

    class Resultado implements Comparable<Resultado> {
        String identificador;
        double distancia;
        String clase;

        public Resultado(String identificador, double distancia, String clase) {
            this.identificador = identificador;
            this.distancia = distancia;
            this.clase = clase;
        }

        @Override
        public String toString() {
            return String.format("%s\t%.2f\t%s", identificador, distancia, clase);
        }

        @Override
        public int compareTo(Resultado otro) {
            // Comparar por distancia
            return Double.compare(this.distancia, otro.distancia);
        }
    }

    private void realizarCalculos() {
        // Preguntar al usuario cuál será el valor de k
        int k = obtenerValorDeK();

        // Realizar cálculos y clasificaciones de distancia para cada objeto nuevo de forma independiente
        for (int i = 0; i < newTable.getRowCount(); i++) {
            // Crear una lista para almacenar los resultados de distancia de este objeto nuevo
            List<Resultado> resultados = new ArrayList<>();

            // Restablecer el contador de identificadores de distancia para cada objeto nuevo
            int contador = 1;

            // Extraer características y etiqueta del objeto nuevo
            Object xNuevoObj = newTable.getValueAt(i, 0);
            Object yNuevoObj = newTable.getValueAt(i, 1);
            Object etiquetaNuevoObj = newTable.getValueAt(i, 2);

            // Convertir las características a Double
            double xNuevo;
            double yNuevo;
            try {
                xNuevo = Double.parseDouble(xNuevoObj.toString());
                yNuevo = Double.parseDouble(yNuevoObj.toString());
            } catch (NumberFormatException e) {
                // Manejo de error si no se puede convertir a Double
                System.err.println("Error al convertir valores de la tabla a Double: " + e.getMessage());
                continue; // Omitir este objeto nuevo
            }

            // Convertir la etiqueta a String
            String etiquetaNuevo = etiquetaNuevoObj.toString();

            // Iterar a través de las tablas de clases para calcular distancias
            for (int j = 0; j < classTables.size(); j++) {
                JTable classTable = classTables.get(j);
                String nombreClase = "Clase " + (j + 1);

                for (int m = 0; m < classTable.getRowCount(); m++) {
                    // Extraer características y etiqueta del objeto de entrenamiento
                    Object xEntrenamientoObj = classTable.getValueAt(m, 0);
                    Object yEntrenamientoObj = classTable.getValueAt(m, 1);
                    Object etiquetaEntrenamientoObj = classTable.getValueAt(m, 2);

                    // Convertir las características a Double
                    double xEntrenamiento;
                    double yEntrenamiento;
                    try {
                        xEntrenamiento = Double.parseDouble(xEntrenamientoObj.toString());
                        yEntrenamiento = Double.parseDouble(yEntrenamientoObj.toString());
                    } catch (NumberFormatException e) {
                        // Manejo de error si no se puede convertir a Double
                        System.err.println("Error al convertir valores de la tabla a Double: " + e.getMessage());
                        continue; // Omitir este objeto de entrenamiento
                    }

                    // Convertir la etiqueta a String
                    String etiquetaEntrenamiento = etiquetaEntrenamientoObj.toString();

                    // Calcular distancia Manhattan
                    double distancia = calcularDistanciaManhattan(xNuevo, yNuevo, xEntrenamiento, yEntrenamiento);

                    // Imprimir resultado en consola con el formato especificado
                    System.out.printf("d%d(%s, %s) = %.2f%n", contador, etiquetaNuevo, etiquetaEntrenamiento, distancia);

                    // Añadir resultado a la lista
                    resultados.add(new Resultado(String.format("d%d", contador), distancia, nombreClase));

                    // Incrementar el contador para el siguiente cálculo
                    contador++;
                }
            }

            // Imprimir la tabla de las distancias sin ordenar
            System.out.println("\nTabla de distancias sin ordenar:");
            System.out.println("Identificador\tDistancia\tClase");
            for (Resultado resultado : resultados) {
                System.out.println(resultado);
            }

            // Ordenar la lista de resultados por distancia (de menor a mayor)
            Collections.sort(resultados);

            // Imprimir la lista ordenada de resultados en formato de tabla
            System.out.println("\nLista ordenada por distancia:");
            System.out.println("Identificador\tDistancia\tClase");
            for (Resultado resultado : resultados) {
                System.out.println(resultado);
            }

            // Clasificar el objeto nuevo basado en los primeros k objetos de la lista ordenada
            asignarClase(resultados, k, i);
        }

        // Mostrar los datos de clases en una nueva ventana
        mostrarDatosDeClases();
    }



    private void asignarClase(List<Resultado> resultados, int k, int filaObjetoNuevo) {
        // Crear un mapa para contar las ocurrencias de cada clase
        Map<String, Integer> contadorClases = new HashMap<>();

        // Iterar a través de los primeros k resultados de la lista ordenada
        for (int i = 0; i < k; i++) {
            Resultado resultado = resultados.get(i);
            String clase = resultado.clase;

            // Contar las ocurrencias de cada clase
            contadorClases.put(clase, contadorClases.getOrDefault(clase, 0) + 1);
        }

        // Encontrar la clase predominante
        String clasePredominante = null;
        int maxOcurrencias = 0;

        for (Map.Entry<String, Integer> entry : contadorClases.entrySet()) {
            if (entry.getValue() > maxOcurrencias) {
                maxOcurrencias = entry.getValue();
                clasePredominante = entry.getKey();
            }
        }

        // Asignar la clase predominante al objeto nuevo en la tabla newTable
        newTable.setValueAt(clasePredominante, filaObjetoNuevo, 2); // Columna 2 es la columna de clase

        // Imprimir la clase asignada al objeto nuevo
        System.out.println("\nObjeto nuevo " + (filaObjetoNuevo + 1) + " clasificado como: " + clasePredominante);
    }


    private void asignarClaseANuevosObjetos(String claseAsignada) {
        // Asigna la clase predominante a cada objeto nuevo
        for (int i = 0; i < newTable.getRowCount(); i++) {
            newTable.setValueAt(claseAsignada, i, 2); // Columna 2 es la columna de etiquetas
        }

        // Mostrar los datos de clases en una nueva ventana
        mostrarDatosDeClases();
    }

    private void mostrarDatosDeClases() {
        // Crear un mapa para agrupar objetos por clase
        Map<String, List<Object[]>> clases = new HashMap<>();

        // Agregar objetos de las tablas de entrenamiento a las clases correspondientes
        for (int j = 0; j < classTables.size(); j++) {
            JTable classTable = classTables.get(j);
            String nombreClase = "Clase " + (j + 1);

            for (int m = 0; m < classTable.getRowCount(); m++) {
                Object[] fila = new Object[3];
                fila[0] = classTable.getValueAt(m, 0);
                fila[1] = classTable.getValueAt(m, 1);
                fila[2] = nombreClase; // Asignar la clase original

                clases.computeIfAbsent(nombreClase, k -> new ArrayList<>()).add(fila);
            }
        }

        // Agregar objetos nuevos clasificados a las clases correspondientes
        for (int i = 0; i < newTable.getRowCount(); i++) {
            Object[] fila = new Object[3];
            fila[0] = newTable.getValueAt(i, 0);
            fila[1] = newTable.getValueAt(i, 1);
            // Usar la clase asignada (columna 2) para la nueva tabla
            String claseAsignada = (String) newTable.getValueAt(i, 2);
            fila[2] = claseAsignada;

            // Agregar el objeto nuevo a la clase correspondiente
            clases.computeIfAbsent(claseAsignada, k -> new ArrayList<>()).add(fila);
        }

        // Crear una nueva ventana para mostrar la tabla de clases
        JFrame frameClases = new JFrame("Objetos clasificados por clase");
        frameClases.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameClases.setSize(600, 400);
        frameClases.setLayout(new BorderLayout());

        // Crear un panel con GridLayout para contener las tablas y el botón
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel tablesPanel = new JPanel(new GridLayout(clases.size(), 1));
        mainPanel.add(tablesPanel, BorderLayout.CENTER);

        // Agregar una tabla para cada clase
        for (Map.Entry<String, List<Object[]>> entry : clases.entrySet()) {
            String nombreClase = entry.getKey();
            List<Object[]> objetos = entry.getValue();

            // Crear la tabla para la clase
            String[] columnNames = {"X", "Y", "Clase"};
            JTable classTable = new JTable(objetos.toArray(new Object[0][0]), columnNames);

            // Crear un panel con título para la tabla
            JPanel classPanel = new JPanel(new BorderLayout());
            classPanel.setBorder(BorderFactory.createTitledBorder(nombreClase));
            classPanel.add(new JScrollPane(classTable), BorderLayout.CENTER);

            // Añadir el panel de clase a la ventana
            tablesPanel.add(classPanel);
        }

        // Añadir botón para abrir la ventana de gráficos
        JButton botonGraficar = new JButton("Graficar Objetos");
        botonGraficar.addActionListener(e -> graficarObjetos());
        mainPanel.add(botonGraficar, BorderLayout.SOUTH);

        // Añadir el panel principal a la ventana
        frameClases.add(mainPanel);

        // Mostrar la nueva ventana
        frameClases.setVisible(true);
    }



    private int obtenerValorDeK() {
        int k = 1; // Valor por defecto en caso de que el usuario no introduzca un valor válido

        // Preguntar al usuario cuál será el valor de k
        String input = JOptionPane.showInputDialog(nextFrame, "¿Cuál será el valor de k?");

        try {
            // Convertir la entrada del usuario a un número entero
            k = Integer.parseInt(input);
            if (k <= 0) {
                throw new NumberFormatException("El valor de k debe ser un número entero positivo.");
            }
        } catch (NumberFormatException e) {
            // Si la entrada no es válida, mostrar un mensaje de error
            JOptionPane.showMessageDialog(nextFrame, "Por favor, introduce un número entero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            // Volver a pedir la entrada
            return obtenerValorDeK();
        }
        return k;
    }

    private double calcularDistanciaManhattan(double x1, double y1, double x2, double y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private void graficarObjetos() {
        // Crear una nueva ventana para graficar los objetos
        JFrame frameGrafico = new JFrame("Gráfico de objetos");
        frameGrafico.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameGrafico.setSize(1000, 800); // Aumentar el tamaño de la ventana
        frameGrafico.setLocationRelativeTo(null); // Centrar la ventana en la pantalla

        // Crear un panel de gráficos personalizado
        JPanel panelGrafico = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Llama al método graficar y pásale este panel como argumento
                graficar(g, this);
            }
        };

        // Añadir el panel de gráficos a la ventana
        frameGrafico.add(panelGrafico);

        // Mostrar la nueva ventana
        frameGrafico.setVisible(true);
    }

    private void graficar(Graphics g, JPanel panel) {
        // Crear un mapa para asignar colores a cada clase
        Map<String, Color> colores = new HashMap<>();
        colores.put("Clase 1", Color.RED);
        colores.put("Clase 2", Color.BLUE);
        colores.put("Clase 3", Color.GREEN);
        // Puedes agregar más colores según las clases necesarias

        // Obtener las dimensiones del panel de gráficos
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        // Definir un factor de escala para ajustar el tamaño de los gráficos
        double factorEscala = 50.0; // Puedes ajustar este valor según lo necesites

        // Dibujar ejes positivos
        g.setColor(Color.BLACK);
        g.drawLine(0, panelHeight, 0, 0); // Eje Y (desde la parte inferior hasta arriba)
        g.drawLine(0, panelHeight, panelWidth, panelHeight); // Eje X (desde la parte izquierda hacia la derecha)

        // Graficar los objetos de las tablas de entrenamiento
        for (int j = 0; j < classTables.size(); j++) {
            JTable classTable = classTables.get(j);
            String nombreClase = "Clase " + (j + 1);
            Color colorClase = colores.getOrDefault(nombreClase, Color.BLACK);

            g.setColor(colorClase);

            for (int m = 0; m < classTable.getRowCount(); m++) {
                double x = Double.parseDouble(classTable.getValueAt(m, 0).toString()) * factorEscala;
                double y = Double.parseDouble(classTable.getValueAt(m, 1).toString()) * factorEscala;
                String etiqueta = classTable.getValueAt(m, 2).toString();

                // Transformar coordenadas para centrarlas en el cuarto cuadrante del panel
                int xTransformado = (int) x;
                int yTransformado = (int) (panelHeight - y);

                // Dibujar punto
                g.fillOval(xTransformado - 3, yTransformado - 3, 6, 6);

                // Etiquetar el objeto
                g.drawString(etiqueta, xTransformado + 5, yTransformado);
            }
        }

        // Graficar los objetos nuevos clasificados
        for (int i = 0; i < newTable.getRowCount(); i++) {
            double x = Double.parseDouble(newTable.getValueAt(i, 0).toString()) * factorEscala;
            double y = Double.parseDouble(newTable.getValueAt(i, 1).toString()) * factorEscala;
            String etiqueta = newTable.getValueAt(i, 2).toString(); // Obtener la etiqueta original

            // Obtener el color asignado a la clase del objeto
            String clase = newTable.getValueAt(i, 2).toString();
            Color colorClase = colores.getOrDefault(clase, Color.BLACK);
            g.setColor(colorClase);

            // Transformar coordenadas para centrarlas en el cuarto cuadrante del panel
            int xTransformado = (int) x;
            int yTransformado = (int) (panelHeight - y);

            // Dibujar punto
            g.fillOval(xTransformado - 3, yTransformado - 3, 6, 6);

            // Etiquetar el objeto
            g.drawString(etiqueta, xTransformado + 5, yTransformado);
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(KNNApp::new);
    }
}
