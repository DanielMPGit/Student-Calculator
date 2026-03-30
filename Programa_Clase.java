package programa_clase;

import java.util.ArrayList;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;

public class Programa_Clase {

    static final String RESET     = "\033[0m";
    static final String BOLD      = "\033[1m";
    static final String BG_BLUE   = "\033[44m";
    static final String FG_WHITE  = "\033[97m";
    static final String FG_CYAN   = "\033[96m";
    static final String FG_YELLOW = "\033[93m";
    static final String FG_GREEN  = "\033[92m";
    static final String FG_RED    = "\033[91m";
    static final String FG_GRAY   = "\033[90m";

    static final int ANCHO = 60;

    static ArrayList<Alumnos>     alumnos     = new ArrayList<>();
    static ArrayList<Asignaturas> asignaturas = new ArrayList<>();

    static Terminal terminal;

    public static void main(String[] args) throws Exception {
        terminal = TerminalBuilder.builder()
                .system(true)
                .jansi(true)
                .build();

        DatabaseManager.inicializar();
        alumnos     = DatabaseManager.cargarAlumnos();
        asignaturas = DatabaseManager.cargarAsignaturas();

        try {
            menuPrincipal();
        } finally {
            DatabaseManager.guardarTodo(alumnos, asignaturas);
            terminal.close();
            clearScreen();
            System.out.println(FG_GREEN + "Hasta luego!" + RESET);
        }
    }

    static void menuPrincipal() throws Exception {
        String[] opciones = {
            "  Anadir  alumno / nota / asignatura",
            "  Borrar  alumno / nota / asignatura",
            "  Calculos y estadisticas",
            "  Mostrar alumnos",
            "  Mostrar asignaturas",
            "  Mostrar asignaturas por alumno",
            "  Salir"
        };
        while (true) {
            int sel = mostrarMenu("GESTION DE CLASE", "Menu Principal", opciones);
            switch (sel) {
                case 0: menuAñadir();                break;
                case 1: menuBorrar();                break;
                case 2: calculos();                  break;
                case 3: pantallaListarAlumnos();     break;
                case 4: pantallaListarAsignaturas(); break;
                case 5: pantallaAsignaturasAlumno(); break;
                case 6: case -1: return;
            }
        }
    }

    static void menuAñadir() throws Exception {
        String[] opciones = {
            "  Anadir alumno",
            "  Anadir asignatura",
            "  Anadir nota",
            "  Asignar asignatura a alumno",
            "  Anadir faltas",
            "  Volver"
        };
        while (true) {
            int sel = mostrarMenu("ANADIR", "Que deseas anadir?", opciones);
            switch (sel) {
                case 0: añadirAlumno(); break;
                case 1: añadirAsignatura(); break;
                case 2: añadirNota(); break;
                case 3: asignarAsignatura(); break;
                case 4:
                    double[] datos4 = pedirAlumnoFaltas();
                    if (datos4 == null) break;
                    alumnos.get((int) datos4[0]).getAsignaturas().get((int) datos4[1]).añadirFaltas((int) datos4[2]);
                    DatabaseManager.guardarTodo(alumnos, asignaturas);
                    break;
                case 5: case -1: return;
            }
        }
    }

    static void menuBorrar() throws Exception {
        String[] opciones = {
            "  Borrar alumno",
            "  Borrar asignatura",
            "  Borrar nota",
            "  Desasignar asignatura de alumno",
            "  Restar faltas",
            "  Volver"
        };
        while (true) {
            int sel = mostrarMenu("BORRAR", "Que deseas borrar?", opciones);
            switch (sel) {
                case 0: borrarAlumno(); break;
                case 1: borrarAsignatura(); break;
                case 2: borrarNota(); break;
                case 3: desasignarAsignatura(); break;
                case 4:
                    double[] datos4 = pedirAlumnoFaltas();
                    if (datos4 == null) break;
                    alumnos.get((int) datos4[0]).getAsignaturas().get((int) datos4[1]).restarFaltas((int) datos4[2]);
                    DatabaseManager.guardarTodo(alumnos, asignaturas);
                    break;
                case 5: case -1: return;
            }
        }
    }

    static void calculos() throws Exception {
        String[] opciones = {
            "  Mostrar Asignaturas por alumno",
            "  Media de Asignatura",
            "  Calculo Faltas",
            "  Volver"
        };
        while (true) {
            int sel = mostrarMenu("CALCULOS", "Selecciona operacion", opciones);
            switch (sel) {
                case 0: pantallaAsignaturasAlumno(); break;
                case 1:
                    int[] datos = getDatos();
                    if (datos == null) break;
                    mostrarMensaje("Media: " + alumnos.get(datos[0]).getAsignaturas().get(datos[1]).calcularMedia());
                    break;
                case 2:
                    int[] datos2 = getDatos();
                    if (datos2 == null) break;
                    String semStr = pedirInput("Semanas del Curso: ");
                    if (semStr == null) break;
                    String porStr = pedirInput("Porcentaje Asistencia: ");
                    if (porStr == null) break;
                    mostrarMensaje(alumnos.get(datos2[0]).getAsignaturas().get(datos2[1]).calcularFaltas(Integer.parseInt(semStr), Integer.parseInt(porStr)));
                    break;
                case 3: case -1: return;
            }
        }
    }

    public static int[] getDatos() throws Exception {
        int alumno = seleccionarAlumno("Selecciona alumno");
        if (alumno == -1) {
            return null;
        }
        int asignatura = seleccionarAsignaturaDeAlumno(alumno, "Selecciona asignatura");
        if (asignatura == -1) {
            return null;
        }
        return new int[]{alumno, asignatura};
    }

    public static void añadirNota() throws Exception {
        if (asignaturas.isEmpty()) {
            System.out.println("No hay asignaturas.");
            return;
        }
        if (alumnos.isEmpty()) {
            System.out.println("No hay alumnos.");
            return;
        }
        String[] tipos = { "  Mostrar Asignaturas por alumno", "  Añadir nota examen", "  Añadir nota practica", "  Añadir nota adicional", "  Salir" };
        while (true) {
            int sel = mostrarMenu("ANADIR NOTA", "Tipo de nota", tipos);
            double[] datos;
            switch (sel) {
                case 0: pantallaAsignaturasAlumno(); break;
                case 1:
                    datos = pedirAlumnoAsignaturaNota();
                    if (datos == null) break;
                    alumnos.get((int) datos[0]).getAsignaturas().get((int) datos[1]).añadirNotaExamen(datos[2]);
                    DatabaseManager.guardarTodo(alumnos, asignaturas);
                    break;
                case 2:
                    datos = pedirAlumnoAsignaturaNota();
                    if (datos == null) break;
                    alumnos.get((int) datos[0]).getAsignaturas().get((int) datos[1]).añadirNotaPractica(datos[2]);
                    DatabaseManager.guardarTodo(alumnos, asignaturas);
                    break;
                case 3:
                    datos = pedirAlumnoAsignaturaNota();
                    if (datos == null) break;
                    alumnos.get((int) datos[0]).getAsignaturas().get((int) datos[1]).añadirNotaAdicional(datos[2]);
                    DatabaseManager.guardarTodo(alumnos, asignaturas);
                    break;
                case 4: case -1: return;
            }
        }
    }

    public static double[] pedirAlumnoFaltas() throws Exception {
        int alumno = seleccionarAlumno("Selecciona alumno");
        if (alumno == -1) {
            return null;
        }
        ArrayList<Asignaturas> asigAlumno = alumnos.get(alumno).getAsignaturas();
        for (int i = 0; i < asigAlumno.size(); i++) {
            System.out.println((i + 1) + ". " + asigAlumno.get(i).getNombre_asisgnatura());
        }
        int asignatura = seleccionarAsignaturaDeAlumno(alumno, "Selecciona asignatura");
        if (asignatura < 0 || asignatura >= asigAlumno.size()) {
            mostrarMensaje("No se ha encontrado la asignatura.");
            return null;
        }
        String faltasStr = pedirInput("Faltas: ");
        if (faltasStr == null) {
            return null;
        }
        double faltas = Double.parseDouble(faltasStr);
        return new double[]{alumno, asignatura, faltas};
    }

    public static double[] pedirAlumnoAsignaturaNota() throws Exception {
        int alumno = seleccionarAlumno("Selecciona alumno");
        if (alumno == -1) {
            return null;
        }
        ArrayList<Asignaturas> asigAlumno = alumnos.get(alumno).getAsignaturas();
        for (int i = 0; i < asigAlumno.size(); i++) {
            System.out.println((i + 1) + ". " + asigAlumno.get(i).getNombre_asisgnatura());
        }
        int asignatura = seleccionarAsignaturaDeAlumno(alumno, "Selecciona asignatura");
        if (asignatura < 0 || asignatura >= asigAlumno.size()) {
            mostrarMensaje("No se ha encontrado la asignatura.");
            return null;
        }
        String notaStr = pedirInput("Nota: ");
        if (notaStr == null) {
            return null;
        }
        double nota = Double.parseDouble(notaStr.replace(",", "."));
        return new double[]{alumno, asignatura, nota};
    }

    public static void añadirAlumno() throws Exception {
        String nombre = pedirInput("Nombre: ");
        if (nombre == null) {
            return;
        }
        String apellidos = pedirInput("Apellidos: ");
        if (apellidos == null) {
            return;
        }
        String curso = pedirInput("Curso: ");
        if (curso == null) {
            return;
        }
        alumnos.add(new Alumnos(nombre, apellidos, curso));
        DatabaseManager.guardarTodo(alumnos, asignaturas);
        if (asignaturas.isEmpty()) {
            mostrarMensaje("¡ No hay asignaturas, deberias añadir !");
        }
    }

    public static void añadirAsignatura() throws Exception {
        String nombre = pedirInput("Nombre: ");
        if (nombre == null) {
            return;
        }
        String horasStr = pedirInput("Horas a la Semana: ");
        if (horasStr == null) {
            return;
        }
        int horas_semana = Integer.parseInt(horasStr);
        String pExStr = pedirInput("Porcentaje del Examen: ");
        if (pExStr == null) {
            return;
        }
        int porcentaje_examen = Integer.parseInt(pExStr);
        String pPrStr = pedirInput("Porcentaje del Practicas: ");
        if (pPrStr == null) {
            return;
        }
        int porcentaje_practicas = Integer.parseInt(pPrStr);
        int porcentaje_adicional = 0;
        String[] opAdicional = { "  Si", "  No" };
        int selAd = mostrarMenu("ADICIONAL", "Porcentaje Adicional?", opAdicional);
        if (selAd == 0) {
            String pAdStr = pedirInput("Porcentaje Adicional: ");
            if (pAdStr == null) {
                return;
            }
            porcentaje_adicional = Integer.parseInt(pAdStr);
        }
        asignaturas.add(new Asignaturas(nombre, horas_semana, porcentaje_examen, porcentaje_practicas, porcentaje_adicional));
        DatabaseManager.guardarTodo(alumnos, asignaturas);
    }

    public static void asignarAsignatura() throws Exception {
        if (asignaturas.isEmpty()) {
            System.out.println("No hay asignaturas.");
            return;
        }
        if (alumnos.isEmpty()) {
            System.out.println("No hay alumnos.");
            return;
        }
        String[] opciones = { "  Mostrar Alumnos", "  Mostrar Asignaturas", "  Asignar", "  Salir" };
        while (true) {
            int sel = mostrarMenu("ASIGNAR ASIGNATURA", "Asignar Asignatura", opciones);
            switch (sel) {
                case 0: pantallaListarAlumnos(); break;
                case 1: pantallaListarAsignaturas(); break;
                case 2:
                    int[] datos = getDatosPlantilla();
                    if (datos == null) break;
                    if (alumnos.get(datos[0]).tieneAsignatura(asignaturas.get(datos[1]).getNombre_asisgnatura())) {
                        mostrarMensaje("Esta asignatura ya está asignada a este alumno.");
                        break;
                    }
                    alumnos.get(datos[0]).añadirAsignatura(new Asignaturas(asignaturas.get(datos[1])));
                    DatabaseManager.guardarTodo(alumnos, asignaturas);
                    break;
                case 3: case -1: return;
            }
        }
    }

    public static void borrarAlumno() throws Exception {
        if (alumnos.isEmpty()) {
            mostrarMensaje("¡ No hay alumnos, no hay nada que borrar !");
            return;
        }
        int idx = seleccionarAlumno("Selecciona alumno a eliminar");
        if (idx == -1) {
            return;
        }
        alumnos.remove(idx);
        DatabaseManager.guardarTodo(alumnos, asignaturas);
    }

    public static void borrarAsignatura() throws Exception {
        int idx = seleccionarAsignaturasPlantilla("Selecciona asignatura a eliminar");
        if (idx == -1) {
            return;
        }
        asignaturas.remove(idx);
        DatabaseManager.guardarTodo(alumnos, asignaturas);
    }

    public static void borrarNota() throws Exception {
        if (asignaturas.isEmpty()) {
            System.out.println("No hay asignaturas.");
            return;
        }
        if (alumnos.isEmpty()) {
            System.out.println("No hay alumnos.");
            return;
        }
        String[] tipos = { "  Mostrar Asignaturas por alumno", "  Borrar nota examen", "  Borrar nota practica", "  Borrar nota adicional", "  Salir" };
        while (true) {
            int sel = mostrarMenu("BORRAR NOTA", "Tipo de nota", tipos);
            double[] datos;
            switch (sel) {
                case 0: pantallaAsignaturasAlumno(); break;
                case 1:
                    datos = pedirAlumnoAsignaturaNota();
                    if (datos == null) break;
                    alumnos.get((int) datos[0]).getAsignaturas().get((int) datos[1]).borrarNotaExamen(datos[2]);
                    DatabaseManager.guardarTodo(alumnos, asignaturas);
                    break;
                case 2:
                    datos = pedirAlumnoAsignaturaNota();
                    if (datos == null) break;
                    alumnos.get((int) datos[0]).getAsignaturas().get((int) datos[1]).borrarNotaPractica(datos[2]);
                    DatabaseManager.guardarTodo(alumnos, asignaturas);
                    break;
                case 3:
                    datos = pedirAlumnoAsignaturaNota();
                    if (datos == null) break;
                    alumnos.get((int) datos[0]).getAsignaturas().get((int) datos[1]).borrarNotaAdicional(datos[2]);
                    DatabaseManager.guardarTodo(alumnos, asignaturas);
                    break;
                case 4: case -1: return;
            }
        }
    }

    public static void desasignarAsignatura() throws Exception {
        if (asignaturas.isEmpty()) {
            System.out.println("No hay asignaturas.");
            return;
        }
        if (alumnos.isEmpty()) {
            System.out.println("No hay alumnos.");
            return;
        }
        String[] opciones = { "  Mostrar Alumnos", "  Mostrar Asignaturas", "  Desasignar", "  Salir" };
        while (true) {
            int sel = mostrarMenu("DESASIGNAR ASIGNATURA", "Desasignar Asignatura", opciones);
            switch (sel) {
                case 0: pantallaListarAlumnos(); break;
                case 1: pantallaListarAsignaturas(); break;
                case 2:
                    int[] datos = getDatosPlantilla();
                    if (datos == null) break;
                    if (alumnos.get(datos[0]).tieneAsignatura(asignaturas.get(datos[1]).getNombre_asisgnatura())) {
                        alumnos.get(datos[0]).eliminarAsignatura(datos[1]);
                    } else {
                        mostrarMensaje("Esta asignatura no esta asignada a este alumno.");
                    }
                    DatabaseManager.guardarTodo(alumnos, asignaturas);
                    break;
                case 3: case -1: return;
            }
        }
    }

    public static boolean existeAlumno(ArrayList<Alumnos> alumnos, int id) {
        for (int i = 0; i < alumnos.size(); i++) {
            if (alumnos.get(i).getId_alumno() == id) return true;
        }
        return false;
    }

    public static boolean existeAsignatura(ArrayList<Asignaturas> asignaturas, int id) {
        for (int i = 0; i < asignaturas.size(); i++) {
            if (asignaturas.get(i).getId_asignatura() == id) return true;
        }
        return false;
    }

    public static int[] getDatosPlantilla() throws Exception {
        int alumno = seleccionarAlumno("Selecciona alumno");
        if (alumno == -1) {
            return null;
        }
        int asignatura = seleccionarAsignaturasPlantilla("Selecciona asignatura");
        if (asignatura == -1) {
            return null;
        }
        return new int[]{alumno, asignatura};
    }

    static void pantallaListarAlumnos() throws Exception {
        clearScreen(); cabecera("ALUMNOS");
        if (alumnos.isEmpty()) {
            print(FG_YELLOW + "  No hay alumnos registrados." + RESET);
        } else {
            for (Alumnos a : alumnos) {
                linea();
                print(FG_CYAN + "  ID " + a.getId_alumno() + RESET + "  " + BOLD + a.getNombre() + " " + a.getApellidos() + RESET);
                print(FG_GRAY + "  Curso: " + RESET + a.getCurso() + "   " + FG_GRAY + "Asignaturas: " + RESET + a.getAsignaturas().size());
            }
        }
        linea(); esperarEnter();
    }

    static void pantallaListarAsignaturas() throws Exception {
        clearScreen(); cabecera("ASIGNATURAS");
        if (asignaturas.isEmpty()) {
            print(FG_YELLOW + "  No hay asignaturas registradas." + RESET);
        } else {
            for (Asignaturas a : asignaturas) {
                linea();
                print(FG_CYAN + "  ID " + a.getId_asignatura() + RESET + "  " + BOLD + a.getNombre_asisgnatura() + RESET);
                print(String.format(FG_GRAY + "  %dh/sem  |  Examen:%d%%  Pract:%d%%  Adic:%d%%" + RESET,
                        a.getHoras_semana(), (int)a.getPorcentaje_examen(),
                        (int)a.getPorcentaje_practicas(), a.getPorcentaje_adicional()));
            }
        }
        linea(); esperarEnter();
    }

    static void pantallaAsignaturasAlumno() throws Exception {
        if (alumnos.isEmpty()) {
            mostrarMensaje(FG_RED + "ERROR No hay alumnos." + RESET);
            return;
        }
        int idx = seleccionarAlumno("Selecciona alumno");
        if (idx == -1) {
            return;
        }
        Alumnos a = alumnos.get(idx);
        clearScreen();
        cabecera("ASIGNATURAS DE " + a.getNombre().toUpperCase() + " " + a.getApellidos().toUpperCase());
        if (a.getAsignaturas().isEmpty()) {
            print(FG_YELLOW + "  Sin asignaturas asignadas." + RESET);
        } else {
            for (Asignaturas asig : a.getAsignaturas()) {
                linea();
                print(BOLD + "  " + asig.getNombre_asisgnatura() + RESET);
                print(String.format(FG_GRAY + "  %dh/sem  |  Examen:%d%%  Pract:%d%%  Adic:%d%%  Faltas:%d" + RESET,
                        asig.getHoras_semana(), (int)asig.getPorcentaje_examen(),
                        (int)asig.getPorcentaje_practicas(), asig.getPorcentaje_adicional(), asig.getFaltas()));
                print(FG_GRAY + "  Notas examen:    " + RESET + asig.getNotas_examen());
                print(FG_GRAY + "  Notas practicas: " + RESET + asig.getNotas_practicas());
                print(FG_GRAY + "  Notas adicional: " + RESET + asig.getNotas_adicionales());
                double media = asig.calcularMedia();
                if (media >= 0) {
                    String col = media >= 5 ? FG_GREEN : FG_RED;
                    print(FG_GRAY + "  Media actual:    " + col + BOLD + media + RESET);
                }
            }
        }
        linea(); esperarEnter();
    }

    static int mostrarMenu(String titulo, String subtitulo, String[] opciones) throws Exception {
        int sel = 0;
        terminal.enterRawMode();
        try {
            while (true) {
                clearScreen();
                cabecera(titulo);
                print(FG_YELLOW + "  " + subtitulo + RESET);
                print("");
                for (int i = 0; i < opciones.length; i++) {
                    if (i == sel)
                        print(BG_BLUE + FG_WHITE + BOLD + ">" + opciones[i] + RESET);
                    else
                        print(RESET + " " + opciones[i] + RESET);
                }
                print("");
                print(FG_GRAY + "  Flechas: Navegar   Enter: Seleccionar   Esc: Volver" + RESET);
                linea();

                int tecla = leerTeclaJLine();
                if      (tecla == 0) sel = (sel - 1 + opciones.length) % opciones.length;
                else if (tecla == 1) sel = (sel + 1) % opciones.length;
                else if (tecla == 2) return sel;
                else if (tecla == 3) return -1;
            }
        } finally {
            terminal.puts(Capability.reset_1string);
            terminal.flush();
        }
    }

    static int leerTeclaJLine() throws Exception {
        int c = terminal.reader().read();
        if (c == 27) {
            int c2 = terminal.reader().read(50);
            if (c2 == -1) return 3;
            if (c2 == '[') {
                int c3 = terminal.reader().read(50);
                if (c3 == 'A') return 0;
                if (c3 == 'B') return 1;
            }
            return 3;
        }
        if (c == '\r' || c == '\n' || c == 13) return 2;
        if (c == 'w'  || c == 'W')             return 0;
        if (c == 's'  || c == 'S')             return 1;
        return -1;
    }

    static int seleccionarAlumno(String titulo) throws Exception {
        if (alumnos.isEmpty()) {
            mostrarMensaje(FG_RED + "ERROR No hay alumnos." + RESET);
            return -1;
        }
        String[] opts = new String[alumnos.size() + 1];
        for (int i = 0; i < alumnos.size(); i++) {
            opts[i] = "  [" + alumnos.get(i).getId_alumno() + "]  "
                    + alumnos.get(i).getNombre() + " " + alumnos.get(i).getApellidos()
                    + "  (" + alumnos.get(i).getCurso() + ")";
        }
        opts[alumnos.size()] = "  <- Cancelar";
        int sel = mostrarMenu("ALUMNOS", titulo, opts);
        return (sel == alumnos.size() || sel == -1) ? -1 : sel;
    }

    static int seleccionarAsignaturasPlantilla(String titulo) throws Exception {
        if (asignaturas.isEmpty()) {
            mostrarMensaje(FG_RED + "ERROR No hay asignaturas." + RESET);
            return -1;
        }
        String[] opts = new String[asignaturas.size() + 1];
        for (int i = 0; i < asignaturas.size(); i++) {
            opts[i] = "  [" + asignaturas.get(i).getId_asignatura() + "]  "
                    + asignaturas.get(i).getNombre_asisgnatura();
        }
        opts[asignaturas.size()] = "  <- Cancelar";
        int sel = mostrarMenu("ASIGNATURAS", titulo, opts);
        return (sel == asignaturas.size() || sel == -1) ? -1 : sel;
    }

    static int seleccionarAsignaturaDeAlumno(int idxAlumno, String titulo) throws Exception {
        ArrayList<Asignaturas> lista = alumnos.get(idxAlumno).getAsignaturas();
        if (lista.isEmpty()) {
            mostrarMensaje(FG_RED + "ERROR Este alumno no tiene asignaturas." + RESET);
            return -1;
        }
        String[] opts = new String[lista.size() + 1];
        for (int i = 0; i < lista.size(); i++) {
            opts[i] = "  " + lista.get(i).getNombre_asisgnatura();
        }
        opts[lista.size()] = "  <- Cancelar";
        int sel = mostrarMenu("ASIGNATURAS DEL ALUMNO", titulo, opts);
        return (sel == lista.size() || sel == -1) ? -1 : sel;
    }

    static String pedirInput(String prompt) throws Exception {
        clearScreen();
        cabecera("ENTRADA DE DATOS");
        print(FG_CYAN + "  " + prompt + RESET);
        org.jline.reader.LineReader lr = org.jline.reader.LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
        try {
            String linea = lr.readLine("  > ");
            return (linea == null || linea.isBlank()) ? null : linea.trim();
        } catch (org.jline.reader.UserInterruptException | org.jline.reader.EndOfFileException e) {
            return null;
        }
    }

    static void print(Object msg) {
        terminal.writer().println(msg);
        terminal.flush();
    }

    static void clearScreen() {
        terminal.puts(Capability.clear_screen);
        terminal.flush();
    }

    static void cabecera(String titulo) {
        String relleno = "=".repeat(ANCHO - 2);
        print(FG_CYAN + "+" + relleno + "+" + RESET);
        int espacios = (ANCHO - 2 - titulo.length()) / 2;
        String pad = " ".repeat(Math.max(0, espacios));
        print(FG_CYAN + "|" + RESET + BOLD + FG_WHITE + pad + titulo + pad + " " + RESET + FG_CYAN + "|" + RESET);
        print(FG_CYAN + "+" + relleno + "+" + RESET);
        print("");
    }

    static void linea() {
        print(FG_GRAY + "  " + "-".repeat(ANCHO - 4) + RESET);
    }

    static void mostrarMensaje(String msg) throws Exception {
        clearScreen();
        cabecera("RESULTADO");
        print("  " + msg);
        print("");
        print(FG_GRAY + "  Pulsa cualquier tecla para continuar..." + RESET);
        terminal.enterRawMode();
        terminal.reader().read();
    }

    static void esperarEnter() throws Exception {
        print(FG_GRAY + "  Pulsa cualquier tecla para volver..." + RESET);
        terminal.enterRawMode();
        terminal.reader().read();
    }
}
