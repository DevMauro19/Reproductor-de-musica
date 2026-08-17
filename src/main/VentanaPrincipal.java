package main;

import Exceptions.ECalificacion;
import Exceptions.ENumeroNegativo;
import Exceptions.EVacia;
import modelo.Biblioteca;
import modelo.Cancion;
import reproduccion.ModoAlfabetico;
import reproduccion.ModoAleatorio;
import reproduccion.ModoLlegada;
import reproduccion.ModoReproduccion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

//Ventana principal del reproductor "EIA ON AIR".
//Se encarga UNICAMENTE de la presentacion (interfaz grafica);
//la logica de cada estructura de datos vive en Estructuras/ y reproduccion/.
//Cada cancion que se agrega vive simultaneamente en 4 lugares:
//la Biblioteca (lista simple general) y en las 3 estructuras de cada modo
//(ListaCircularDoble, Cola, ArbolBinarioBusqueda), todas apuntando al MISMO objeto Cancion.
public class VentanaPrincipal extends JFrame {

    //---------- Paleta de colores estilo Apple Music (modo oscuro) ----------
    private static final Color BG_APP        = new Color(18, 18, 20);
    private static final Color BG_SIDEBAR     = new Color(10, 10, 12);
    private static final Color BG_CARD        = new Color(28, 28, 31);
    private static final Color BG_FIELD       = new Color(42, 42, 46);
    private static final Color BG_PLAYER      = new Color(24, 24, 27);
    private static final Color TXT_PRIMARY    = new Color(242, 242, 244);
    private static final Color TXT_SECONDARY  = new Color(145, 145, 152);
    private static final Color ACCENT         = new Color(252, 61, 96);   // rojo/rosa Apple Music
    private static final Color ACCENT_DARK    = new Color(205, 42, 74);
    private static final Color DIVIDER        = new Color(48, 48, 52);

    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_SONG    = new Font("Segoe UI", Font.BOLD, 14);

    //---------- Datos y logica (provista por el resto del equipo) ----------
    private final Biblioteca biblioteca = new Biblioteca();
    private final ModoAleatorio modoAleatorio = new ModoAleatorio();
    private final ModoLlegada modoLlegada = new ModoLlegada();
    private final ModoAlfabetico modoAlfabetico = new ModoAlfabetico();
    private ModoReproduccion modoActivo = modoLlegada;

    //Canciones actualmente mostradas en la tabla (respeta el filtro de busqueda activo)
    private ArrayList<Cancion> filasVisibles = new ArrayList<>();

    //---------- Componentes de interfaz ----------
    private DefaultTableModel tableModel;
    private JTable tabla;
    private JTextField campoBusqueda;
    private JList<String> listaModos;
    private JButton btnAnterior;
    private JButton btnPlayPause;
    private JLabel labelTituloActual;
    private JLabel labelArtistaActual;
    private JLabel labelTiempoActual;
    private JLabel labelTiempoTotal;
    private JProgressBar barraProgreso;
    private PortadaPanel portada;

    private boolean reproduciendo = false;
    private int segundoActual = 0;
    private Timer timerReproduccion;

    private final String[] NOMBRES_MODO = {"Por orden de llegada", "Aleatorio", "Alfabetico"};

    public VentanaPrincipal() {
        configurarVentana();

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(BG_APP);
        raiz.add(construirSidebar(), BorderLayout.WEST);
        raiz.add(construirPanelCentral(), BorderLayout.CENTER);
        raiz.add(construirPlayerBar(), BorderLayout.SOUTH);
        setContentPane(raiz);

        actualizarInfoActual();
    }

    private void configurarVentana() {
        setTitle("EIA ON AIR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("Panel.background", BG_CARD);
    }

    // ======================================================================
    //  SIDEBAR (selector de modo)
    // ======================================================================
    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(20, 18, 20, 18));

        JLabel logo = new JLabel("EIA ON AIR");
        logo.setFont(FONT_TITLE);
        logo.setForeground(TXT_PRIMARY);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Lenguajes y Compiladores");
        subtitulo.setFont(FONT_SMALL);
        subtitulo.setForeground(TXT_SECONDARY);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitulo.setBorder(new EmptyBorder(2, 0, 24, 0));

        JLabel seccionModos = new JLabel("MODO DE REPRODUCCION");
        seccionModos.setFont(FONT_SECTION);
        seccionModos.setForeground(TXT_SECONDARY);
        seccionModos.setAlignmentX(Component.LEFT_ALIGNMENT);
        seccionModos.setBorder(new EmptyBorder(0, 4, 8, 0));

        listaModos = new JList<>(NOMBRES_MODO);
        listaModos.setSelectedIndex(0); // Por orden de llegada, por defecto
        listaModos.setBackground(BG_SIDEBAR);
        listaModos.setForeground(TXT_PRIMARY);
        listaModos.setFont(FONT_BODY);
        listaModos.setFixedCellHeight(38);
        listaModos.setAlignmentX(Component.LEFT_ALIGNMENT);
        listaModos.setSelectionBackground(ACCENT);
        listaModos.setSelectionForeground(Color.WHITE);
        listaModos.setCellRenderer(new ModoCellRenderer());
        listaModos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cambiarModoActivo(listaModos.getSelectedIndex());
            }
        });

        JLabel seccionInfo = new JLabel("ESTRUCTURA ACTIVA");
        seccionInfo.setFont(FONT_SECTION);
        seccionInfo.setForeground(TXT_SECONDARY);
        seccionInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        seccionInfo.setBorder(new EmptyBorder(24, 4, 6, 0));

        JLabel labelEstructura = new JLabel(descripcionEstructura(0));
        labelEstructura.setFont(FONT_SMALL);
        labelEstructura.setForeground(TXT_SECONDARY);
        labelEstructura.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelEstructura.setBorder(new EmptyBorder(0, 4, 0, 0));
        this.labelEstructuraActiva = labelEstructura;

        sidebar.add(logo);
        sidebar.add(subtitulo);
        sidebar.add(seccionModos);
        sidebar.add(listaModos);
        sidebar.add(seccionInfo);
        sidebar.add(labelEstructura);
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JLabel labelEstructuraActiva; // referencia para actualizar el texto explicativo

    private String descripcionEstructura(int indiceModo) {
        switch (indiceModo) {
            case 0: return "Cola simple (FIFO). Cada cancion sale de la cola una vez reproducida.";
            case 1: return "Lista circular doble. Navegacion infinita en ambas direcciones.";
            case 2: return "Arbol binario de busqueda. Recorrido inorden alfabetico.";
            default: return "";
        }
    }

    private void cambiarModoActivo(int indice) {
        switch (indice) {
            case 0 -> modoActivo = modoLlegada;
            case 1 -> modoActivo = modoAleatorio;
            case 2 -> modoActivo = modoAlfabetico;
        }
        labelEstructuraActiva.setText("<html><body style='width:150px'>" + descripcionEstructura(indice) + "</body></html>");
        pausarSiEstaReproduciendo();
        //El modo "Por orden de llegada" no permite regresar (lo exige la especificacion)
        btnAnterior.setEnabled(indice != 0);
        actualizarInfoActual();
    }

    private class ModoCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(new EmptyBorder(0, 10, 0, 0));
            label.setOpaque(true);
            if (isSelected) {
                label.setBackground(ACCENT);
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(BG_SIDEBAR);
                label.setForeground(TXT_PRIMARY);
            }
            return label;
        }
    }

    // ======================================================================
    //  PANEL CENTRAL (busqueda + tabla de biblioteca)
    // ======================================================================
    private JPanel construirPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_APP);
        panel.setBorder(new EmptyBorder(20, 20, 10, 20));

        panel.add(construirBarraSuperior(), BorderLayout.NORTH);
        panel.add(construirTabla(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout(12, 0));
        barra.setBackground(BG_APP);
        barra.setBorder(new EmptyBorder(0, 0, 16, 0));

        campoBusqueda = new RoundedTextField("Buscar por nombre o artista...");
        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarBiblioteca(); }
            public void removeUpdate(DocumentEvent e) { filtrarBiblioteca(); }
            public void changedUpdate(DocumentEvent e) { filtrarBiblioteca(); }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setBackground(BG_APP);

        JButton btnAgregar = crearBotonPildora("+ Agregar", ACCENT);
        btnAgregar.addActionListener(e -> abrirDialogoCancion(null));

        JButton btnEditar = crearBotonPildora("Editar", BG_FIELD);
        btnEditar.addActionListener(e -> editarSeleccionada());

        JButton btnCalificar = crearBotonPildora("\u2605 Calificar", BG_FIELD);
        btnCalificar.addActionListener(e -> calificarSeleccionada());

        JButton btnEliminar = crearBotonPildora("Eliminar", BG_FIELD);
        btnEliminar.addActionListener(e -> eliminarSeleccionada());

        botones.add(btnEditar);
        botones.add(btnCalificar);
        botones.add(btnEliminar);
        botones.add(btnAgregar);

        barra.add(campoBusqueda, BorderLayout.CENTER);
        barra.add(botones, BorderLayout.EAST);
        return barra;
    }

    private JScrollPane construirTabla() {
        String[] columnas = {"Nombre", "Artista", "Album", "Duracion", "Genero", "Anio", "Calificacion"};
        tableModel = new DefaultTableModel(columnas, 0) {
            //La calificacion ya no se edita en la celda: se gestiona con el boton "Calificar"
            //y su dialogo dedicado (ver calificarSeleccionada / abrirDialogoCalificacion).
            @Override public boolean isCellEditable(int row, int col) { return false; }
            @Override public Class<?> getColumnClass(int col) { return col == 6 ? Integer.class : String.class; }
        };

        tabla = new JTable(tableModel);
        tabla.setBackground(BG_CARD);
        tabla.setForeground(TXT_PRIMARY);
        tabla.setSelectionBackground(new Color(60, 60, 66));
        tabla.setSelectionForeground(TXT_PRIMARY);
        tabla.setFont(FONT_BODY);
        tabla.setRowHeight(32);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setBackground(BG_CARD);
        tabla.getTableHeader().setForeground(TXT_SECONDARY);
        tabla.getTableHeader().setFont(FONT_SECTION);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER));
        tabla.setDefaultRenderer(Object.class, new FilaRenderer());
        tabla.setDefaultRenderer(Integer.class, new FilaRenderer());

        //Doble clic sobre una fila reproduce esa cancion directamente (como en Apple Music)
        tabla.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tabla.getSelectedRow();
                    if (fila >= 0 && fila < filasVisibles.size()) {
                        reproducirDesdeTabla(filasVisibles.get(fila));
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(DIVIDER));
        return scroll;
    }

    private class FilaRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            boolean esActual = row < filasVisibles.size()
                    && modoActivo.getActual() != null
                    && filasVisibles.get(row).equals(modoActivo.getActual());

            if (isSelected) {
                c.setBackground(new Color(60, 60, 66));
            } else if (esActual) {
                c.setBackground(new Color(60, 24, 33)); // tinte del acento, sutil
            } else {
                c.setBackground(BG_CARD);
            }
            c.setForeground(esActual ? ACCENT : TXT_PRIMARY);
            setBorder(new EmptyBorder(0, 10, 0, 10));
            return c;
        }
    }

    // ======================================================================
    //  BARRA DE REPRODUCCION (inferior)
    // ======================================================================
    private JPanel construirPlayerBar() {
        JPanel barra = new JPanel(new BorderLayout(16, 0));
        barra.setBackground(BG_PLAYER);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, DIVIDER),
                new EmptyBorder(12, 20, 12, 20)));
        barra.setPreferredSize(new Dimension(0, 90));

        // ---- Info de la cancion actual (izquierda) ----
        portada = new PortadaPanel();
        portada.setPreferredSize(new Dimension(58, 58));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(BG_PLAYER);
        info.setBorder(new EmptyBorder(0, 12, 0, 0));

        labelTituloActual = new JLabel("Sin canciones");
        labelTituloActual.setFont(FONT_SONG);
        labelTituloActual.setForeground(TXT_PRIMARY);

        labelArtistaActual = new JLabel("Agrega una cancion para comenzar");
        labelArtistaActual.setFont(FONT_SMALL);
        labelArtistaActual.setForeground(TXT_SECONDARY);

        info.add(labelTituloActual);
        info.add(labelArtistaActual);

        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.setBackground(BG_PLAYER);
        izquierda.add(portada, BorderLayout.WEST);
        izquierda.add(info, BorderLayout.CENTER);
        izquierda.setPreferredSize(new Dimension(280, 0));

        // ---- Controles + progreso (centro) ----
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(BG_PLAYER);

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        controles.setBackground(BG_PLAYER);

        btnAnterior = crearBotonControl(IconoControl.ANTERIOR);
        btnAnterior.addActionListener(e -> irAnterior());

        btnPlayPause = crearBotonCircular(IconoControl.PLAY);
        btnPlayPause.addActionListener(e -> togglePlayPause());

        JButton btnSiguiente = crearBotonControl(IconoControl.SIGUIENTE);
        btnSiguiente.addActionListener(e -> irSiguiente());

        controles.add(btnAnterior);
        controles.add(btnPlayPause);
        controles.add(btnSiguiente);

        JPanel progresoPanel = new JPanel(new BorderLayout(8, 0));
        progresoPanel.setBackground(BG_PLAYER);
        progresoPanel.setBorder(new EmptyBorder(4, 0, 0, 0));

        labelTiempoActual = new JLabel("0:00");
        labelTiempoActual.setFont(FONT_SMALL);
        labelTiempoActual.setForeground(TXT_SECONDARY);

        labelTiempoTotal = new JLabel("0:00");
        labelTiempoTotal.setFont(FONT_SMALL);
        labelTiempoTotal.setForeground(TXT_SECONDARY);

        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setValue(0);
        barraProgreso.setForeground(ACCENT);
        barraProgreso.setBackground(BG_FIELD);
        barraProgreso.setBorderPainted(false);
        barraProgreso.setPreferredSize(new Dimension(0, 5));

        progresoPanel.add(labelTiempoActual, BorderLayout.WEST);
        progresoPanel.add(barraProgreso, BorderLayout.CENTER);
        progresoPanel.add(labelTiempoTotal, BorderLayout.EAST);

        centro.add(controles);
        centro.add(Box.createVerticalStrut(6));
        centro.add(progresoPanel);

        barra.add(izquierda, BorderLayout.WEST);
        barra.add(centro, BorderLayout.CENTER);
        return barra;
    }

    // ======================================================================
    //  LOGICA DE REPRODUCCION
    // ======================================================================
    private void reproducirDesdeTabla(Cancion c) {
        //Avanzamos el modo activo hasta llegar a la cancion elegida (o simplemente
        //la confirmamos si ya es la actual). Mantiene la logica dentro de cada modo.
        int intentos = biblioteca.getTamano() + 1;
        while (intentos-- > 0 && modoActivo.getActual() != null && !modoActivo.getActual().equals(c)) {
            if (modoActivo.siguiente() == null) break;
        }
        actualizarInfoActual();
        if (!reproduciendo) togglePlayPause();
    }

    private void irSiguiente() {
        modoActivo.siguiente();
        actualizarInfoActual();
    }

    private void irAnterior() {
        try {
            modoActivo.anterior();
        } catch (UnsupportedOperationException ex) {
            // El modo "Por orden de llegada" no permite retroceder; el boton ya deberia
            // estar deshabilitado en ese caso, esto es solo una proteccion extra.
        }
        actualizarInfoActual();
    }

    private void togglePlayPause() {
        if (modoActivo.getActual() == null) return;

        reproduciendo = !reproduciendo;
        btnPlayPause.putClientProperty("icono", reproduciendo ? IconoControl.PAUSA : IconoControl.PLAY);
        btnPlayPause.repaint();

        if (reproduciendo) {
            iniciarTimerSimulado();
        } else if (timerReproduccion != null) {
            timerReproduccion.stop();
        }
    }

    private void pausarSiEstaReproduciendo() {
        if (reproduciendo) {
            reproduciendo = false;
            btnPlayPause.putClientProperty("icono", IconoControl.PLAY);
            btnPlayPause.repaint();
            if (timerReproduccion != null) timerReproduccion.stop();
        }
    }

    //Simula el avance del tiempo de la cancion (la rubrica no exige audio real)
    private void iniciarTimerSimulado() {
        if (timerReproduccion != null) timerReproduccion.stop();
        segundoActual = 0;

        timerReproduccion = new Timer(1000, e -> {
            Cancion actual = modoActivo.getActual();
            if (actual == null) {
                togglePlayPause();
                return;
            }
            segundoActual++;
            int duracion = Math.max(actual.getDuracionEnSegundos(), 1);
            barraProgreso.setValue((int) ((segundoActual / (double) duracion) * 100));
            labelTiempoActual.setText(formatoTiempo(segundoActual));

            if (segundoActual >= duracion) {
                //La cancion "termino": pasamos automaticamente a la siguiente
                modoActivo.siguiente();
                segundoActual = 0;
                actualizarInfoActual();
                if (modoActivo.getActual() == null) togglePlayPause();
            }
        });
        timerReproduccion.start();
    }

    private void actualizarInfoActual() {
        Cancion actual = modoActivo.getActual();
        segundoActual = 0;
        barraProgreso.setValue(0);
        labelTiempoActual.setText("0:00");

        if (actual == null) {
            labelTituloActual.setText("Sin canciones");
            labelArtistaActual.setText("Agrega una cancion para comenzar");
            labelTiempoTotal.setText("0:00");
        } else {
            labelTituloActual.setText(actual.getNombre());
            labelArtistaActual.setText(actual.getArtista() + " \u2014 " + actual.getAlbum());
            labelTiempoTotal.setText(formatoTiempo(actual.getDuracionEnSegundos()));
        }
        portada.repaint();
        tabla.repaint(); // refresca el resaltado de la fila "actual"
    }

    private String formatoTiempo(int segundos) {
        return String.format("%d:%02d", segundos / 60, segundos % 60);
    }

    // ======================================================================
    //  CRUD DE CANCIONES
    // ======================================================================
    private void filtrarBiblioteca() {
        String texto = campoBusqueda.getText().trim().toLowerCase();
        ArrayList<Cancion> resultado = new ArrayList<>();
        for (Cancion c : biblioteca.getCanciones()) {
            if (texto.isEmpty()
                    || c.getNombre().toLowerCase().contains(texto)
                    || c.getArtista().toLowerCase().contains(texto)) {
                resultado.add(c);
            }
        }
        refrescarTabla(resultado);
    }

    private void refrescarTabla(ArrayList<Cancion> canciones) {
        filasVisibles = canciones;
        tableModel.setRowCount(0);
        for (Cancion c : canciones) {
            tableModel.addRow(new Object[]{
                    c.getNombre(), c.getArtista(), c.getAlbum(),
                    formatoTiempo(c.getDuracionEnSegundos()), c.getGenero(),
                    String.valueOf(c.getAnioLanzamiento()), c.getCalificacion()
            });
        }
    }

    private void agregarATodasLasEstructuras(Cancion c) {
        biblioteca.agregarCancion(c);
        modoAleatorio.agregarCancion(c);
        modoLlegada.agregarCancion(c);
        modoAlfabetico.agregarCancion(c);
    }

    private void eliminarDeTodasLasEstructuras(Cancion c) {
        biblioteca.eliminarCancion(c);
        try { modoAleatorio.eliminarCancion(c); } catch (EVacia ignored) { }
        try { modoLlegada.eliminarCancion(c); } catch (EVacia ignored) { }
        modoAlfabetico.eliminarCancion(c); // no lanza excepcion si no la encuentra
    }

    private void eliminarSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una cancion de la tabla primero.");
            return;
        }
        Cancion c = filasVisibles.get(fila);
        int confirmar = JOptionPane.showConfirmDialog(this,
                "\u00bfEliminar \"" + c.getNombre() + "\" de la biblioteca?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;

        eliminarDeTodasLasEstructuras(c);
        filtrarBiblioteca();
        actualizarInfoActual();
    }

    private void editarSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una cancion de la tabla primero.");
            return;
        }
        abrirDialogoCancion(filasVisibles.get(fila));
    }

    //Dialogo unico para Agregar (cancionExistente == null) y Editar (cancionExistente != null)
    private void abrirDialogoCancion(Cancion cancionExistente) {
        boolean esEdicion = cancionExistente != null;

        JTextField campoNombre = new JTextField(esEdicion ? cancionExistente.getNombre() : "");
        JTextField campoArtista = new JTextField(esEdicion ? cancionExistente.getArtista() : "");
        JTextField campoAlbum = new JTextField(esEdicion ? cancionExistente.getAlbum() : "");
        JTextField campoDuracion = new JTextField(esEdicion ? String.valueOf(cancionExistente.getDuracionEnSegundos()) : "");
        JTextField campoGenero = new JTextField(esEdicion ? cancionExistente.getGenero() : "");
        JTextField campoAnio = new JTextField(esEdicion ? String.valueOf(cancionExistente.getAnioLanzamiento()) : "");

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Nombre:"));      panel.add(campoNombre);
        panel.add(new JLabel("Artista:"));     panel.add(campoArtista);
        panel.add(new JLabel("Album:"));       panel.add(campoAlbum);
        panel.add(new JLabel("Duracion (segundos):")); panel.add(campoDuracion);
        panel.add(new JLabel("Genero:"));      panel.add(campoGenero);
        panel.add(new JLabel("Anio de lanzamiento:"));  panel.add(campoAnio);

        int resultado = JOptionPane.showConfirmDialog(this, panel,
                esEdicion ? "Editar cancion" : "Agregar cancion",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) return;

        try {
            int duracion = Integer.parseInt(campoDuracion.getText().trim());
            int anio = Integer.parseInt(campoAnio.getText().trim());
            String nuevoNombre = campoNombre.getText().trim();
            String nuevoArtista = campoArtista.getText().trim();

            if (esEdicion) {
                //El Arbol Binario de Busqueda ordena por nombre+artista (ver Cancion.compareTo).
                //Si esos campos cambian, el nodo debe reubicarse: lo sacamos ANTES de mutar
                //los datos (para que la busqueda use el valor viejo) y lo reinsertamos despues.
                boolean cambiaOrdenAlfabetico =
                        !nuevoNombre.equalsIgnoreCase(cancionExistente.getNombre())
                                || !nuevoArtista.equalsIgnoreCase(cancionExistente.getArtista());

                if (cambiaOrdenAlfabetico) {
                    modoAlfabetico.eliminarCancion(cancionExistente);
                }

                biblioteca.actualizarCancion(cancionExistente, nuevoNombre, nuevoArtista,
                        campoAlbum.getText().trim(), duracion, campoGenero.getText().trim(), anio);

                if (cambiaOrdenAlfabetico) {
                    modoAlfabetico.agregarCancion(cancionExistente);
                }
            } else {
                Cancion nueva = new Cancion(nuevoNombre, nuevoArtista, campoAlbum.getText().trim(),
                        duracion, campoGenero.getText().trim(), anio);
                agregarATodasLasEstructuras(nueva);
            }

            filtrarBiblioteca();
            actualizarInfoActual();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Duracion y anio deben ser numeros validos.",
                    "Datos invalidos", JOptionPane.ERROR_MESSAGE);
        } catch (EVacia | ENumeroNegativo ex) {
            JOptionPane.showMessageDialog(this,
                    "Revisa los datos ingresados: ningun campo puede estar vacio ni ser negativo.",
                    "Datos invalidos", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ======================================================================
    //  CALIFICACION DE CANCIONES
    // ======================================================================
    private void calificarSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una cancion de la tabla primero.");
            return;
        }
        Cancion c = filasVisibles.get(fila);
        abrirDialogoCalificacion(c);
    }

    //Dialogo con un slider libre (0-100) para calificar la cancion seleccionada.
    private void abrirDialogoCalificacion(Cancion c) {
        JDialog dialogo = new JDialog(this, "Calificar cancion", true);
        dialogo.setUndecorated(true);
        dialogo.getRootPane().setBorder(BorderFactory.createLineBorder(DIVIDER));

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(BG_CARD);
        contenido.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel titulo = new JLabel(c.getNombre());
        titulo.setFont(FONT_SONG);
        titulo.setForeground(TXT_PRIMARY);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel(c.getArtista());
        subtitulo.setFont(FONT_SMALL);
        subtitulo.setForeground(TXT_SECONDARY);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(new EmptyBorder(2, 0, 22, 0));

        JLabel labelValor = new JLabel(c.getCalificacion() + " / 100");
        labelValor.setFont(FONT_TITLE);
        labelValor.setForeground(ACCENT);
        labelValor.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelValor.setBorder(new EmptyBorder(0, 0, 10, 0));

        JSlider slider = new JSlider(0, 100, c.getCalificacion());
        slider.setBackground(BG_CARD);
        slider.setForeground(TXT_SECONDARY);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setAlignmentX(Component.CENTER_ALIGNMENT);
        slider.setPreferredSize(new Dimension(280, 45));
        slider.addChangeListener(e -> labelValor.setText(slider.getValue() + " / 100"));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botones.setBackground(BG_CARD);
        botones.setBorder(new EmptyBorder(18, 0, 0, 0));

        JButton btnCancelar = crearBotonPildora("Cancelar", BG_FIELD);
        btnCancelar.addActionListener(e -> dialogo.dispose());

        JButton btnGuardar = crearBotonPildora("Guardar", ACCENT);
        btnGuardar.addActionListener(e -> {
            try {
                c.setCalificacion(slider.getValue());
            } catch (ECalificacion ex) {
                JOptionPane.showMessageDialog(dialogo,
                        "La calificacion debe estar entre 0 y 100.",
                        "Calificacion invalida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            filtrarBiblioteca();
            dialogo.dispose();
        });

        botones.add(btnCancelar);
        botones.add(btnGuardar);

        contenido.add(titulo);
        contenido.add(subtitulo);
        contenido.add(labelValor);
        contenido.add(slider);
        contenido.add(botones);

        dialogo.setContentPane(contenido);
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    // ======================================================================
    //  COMPONENTES VISUALES REUTILIZABLES
    // ======================================================================

    //Iconos que se dibujan a mano (evita depender de que la fuente del sistema
    //tenga los glifos Unicode de los simbolos de reproduccion, que no siempre estan disponibles)
    private enum IconoControl { PLAY, PAUSA, ANTERIOR, SIGUIENTE }

    private void dibujarIcono(Graphics2D g2, IconoControl icono, int w, int h, Color color) {
        g2.setColor(color);
        int cx = w / 2, cy = h / 2;
        switch (icono) {
            case PLAY -> {
                int lado = Math.min(w, h) / 3;
                int[] xs = {cx - lado / 2, cx - lado / 2, cx + lado};
                int[] ys = {cy - lado, cy + lado, cy};
                g2.fillPolygon(xs, ys, 3);
            }
            case PAUSA -> {
                int barW = Math.max(w / 8, 3), barH = h / 3, espacio = barW + 2;
                g2.fillRect(cx - espacio, cy - barH / 2, barW, barH);
                g2.fillRect(cx + espacio - barW, cy - barH / 2, barW, barH);
            }
            case SIGUIENTE -> {
                int lado = h / 3;
                int[] xs = {cx - lado, cx - lado, cx};
                int[] ys = {cy - lado, cy + lado, cy};
                g2.fillPolygon(xs, ys, 3);
                g2.fillRect(cx, cy - lado, Math.max(w / 12, 2), lado * 2);
            }
            case ANTERIOR -> {
                int lado = h / 3;
                int[] xs = {cx + lado, cx + lado, cx};
                int[] ys = {cy - lado, cy + lado, cy};
                g2.fillPolygon(xs, ys, 3);
                g2.fillRect(cx - Math.max(w / 12, 2), cy - lado, Math.max(w / 12, 2), lado * 2);
            }
        }
    }

    //Boton tipo "pildora" (rectangulo con esquinas muy redondeadas), usado en la barra superior
    private JButton crearBotonPildora(String texto, Color fondo) {
        JButton boton = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? getBackground().brighter() : getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setForeground(fondo.equals(ACCENT) ? Color.WHITE : TXT_PRIMARY);
        boton.setBackground(fondo);
        boton.setFont(FONT_SECTION);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setBorder(new EmptyBorder(8, 18, 8, 18));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    //Boton circular grande (Play/Pausa), con acento de color e icono dibujado a mano
    private JButton crearBotonCircular(IconoControl iconoInicial) {
        JButton boton = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ACCENT_DARK : ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                IconoControl actual = (IconoControl) getClientProperty("icono");
                if (actual == null) actual = iconoInicial;
                dibujarIcono(g2, actual, getWidth(), getHeight(), Color.WHITE);
                g2.dispose();
            }
        };
        boton.putClientProperty("icono", iconoInicial);
        boton.setPreferredSize(new Dimension(42, 42));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    //Boton de control simple (Anterior/Siguiente), sin fondo, solo icono dibujado a mano
    private JButton crearBotonControl(IconoControl icono) {
        JButton boton = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                dibujarIcono(g2, icono, getWidth(), getHeight(), isEnabled() ? TXT_PRIMARY : TXT_SECONDARY);
                g2.dispose();
            }
        };
        boton.setPreferredSize(new Dimension(28, 28));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    //Campo de texto con esquinas redondeadas y texto de ayuda (placeholder)
    private class RoundedTextField extends JTextField {
        private final String placeholder;

        RoundedTextField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setForeground(TXT_PRIMARY);
            setCaretColor(TXT_PRIMARY);
            setFont(FONT_BODY);
            setBorder(new EmptyBorder(9, 16, 9, 16));
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BG_FIELD);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
            g2.dispose();
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D gp = (Graphics2D) g.create();
                gp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                gp.setColor(TXT_SECONDARY);
                gp.setFont(FONT_BODY);
                gp.drawString(placeholder, 16, getHeight() / 2 + 5);
                gp.dispose();
            }
        }
    }

    //Portada generica (la especificacion permite "una imagen cualquiera")
    private class PortadaPanel extends JPanel {
        PortadaPanel() { setOpaque(false); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint degradado = new GradientPaint(0, 0, ACCENT, getWidth(), getHeight(), new Color(120, 40, 160));
            g2.setPaint(degradado);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 22));
            FontMetrics fm = g2.getFontMetrics();
            String nota = "\u266A";
            int x = (getWidth() - fm.stringWidth(nota)) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 4;
            g2.drawString(nota, x, y);
            g2.dispose();
        }
    }

    // ======================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}