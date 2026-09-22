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
                    int[] datos4 = pedirAlumnoFaltas();
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
                    int[] datos4 = pedirAlumnoFaltas();
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
                    double media = alumnos.get(datos[0]).getAsignaturas().get(datos[1]).calcularMedia();
                    if (media < 0) {
                        mostrarMensaje("No se puede calcular: faltan notas o los porcentajes no suman 100%.");
                    } else {
                        mostrarMensaje("Media: " + media);
                    }
                    break;
                case 2:
                    int[] datos2 = getDatos();
                    if (datos2 == null) break;
                    Integer sem = pedirInt("Semanas del Curso: ");
                    if (sem == null) break;
                    Integer por = pedirInt("Porcentaje Asistencia: ");
                    if (por == null) break;
                    mostrarMensaje(alumnos.get(datos2[0]).getAsignaturas().get(datos2[1]).calcularFaltas(sem, por));
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
        if (alumnos.isEmpty()) {
            mostrarMensaje(FG_RED + "ERROR No hay alumnos." + RESET);
            return;
        }
        String[] tipos = { "  Mostrar Asignaturas por alumno", "  Añadir nota", "  Salir" };
        while (true) {
            int sel = mostrarMenu("ANADIR NOTA", "Que deseas hacer?", tipos);
            switch (sel) {
                case 0: pantallaAsignaturasAlumno(); break;
                case 1: gestionarNota(true);         break;
                case 2: case -1: return;
            }
        }
    }

    static void gestionarNota(boolean añadir) throws Exception {
        int alumno = seleccionarAlumno("Selecciona alumno");
        if (alumno == -1) return;
        int idxAsig = seleccionarAsignaturaDeAlumno(alumno, "Selecciona asignatura");
        if (idxAsig == -1) return;

        Asignaturas asig = alumnos.get(alumno).getAsignaturas().get(idxAsig);
        int idxRa = seleccionarRA(asig);
        if (idxRa == -1) return;

        ResultadoAprendizaje ra = asig.getRas().get(idxRa);
        int idxParte = seleccionarParte(ra);
        if (idxParte == -1) return;

        Parte parte = ra.getPartes().get(idxParte);
        Double nota = pedirDouble("Nota: ");
        if (nota == null) return;
        if (nota < 0 || nota > 10) {
            mostrarMensaje(FG_RED + "La nota debe estar entre 0 y 10." + RESET);
            return;
        }
        if (añadir) {
            parte.añadirNota(nota);
        } else {
            parte.borrarNota(nota);
        }
        DatabaseManager.guardarTodo(alumnos, asignaturas);
    }

    static int seleccionarRA(Asignaturas asig) throws Exception {
        ArrayList<ResultadoAprendizaje> lista = asig.getRas();
        if (lista.isEmpty()) {
            mostrarMensaje(FG_RED + "ERROR Esta asignatura no tiene RA." + RESET);
            return -1;
        }
        String[] opts = new String[lista.size() + 1];
        for (int i = 0; i < lista.size(); i++) {
            ResultadoAprendizaje ra = lista.get(i);
            opts[i] = "  " + ra.getCodigo() + " - " + ra.getDescripcion() + " (" + ra.getPeso() + "%)";
        }
        opts[lista.size()] = "  <- Cancelar";
        int sel = mostrarMenu("RESULTADOS DE APRENDIZAJE", "Selecciona RA", opts);
        return (sel == lista.size() || sel == -1) ? -1 : sel;
    }

    static int seleccionarParte(ResultadoAprendizaje ra) throws Exception {
        ArrayList<Parte> lista = ra.getPartes();
        if (lista.isEmpty()) {
            mostrarMensaje(FG_RED + "ERROR Este RA no tiene partes." + RESET);
            return -1;
        }
        String[] opts = new String[lista.size() + 1];
        for (int i = 0; i < lista.size(); i++) {
            opts[i] = "  " + lista.get(i).getNombre() + " (" + lista.get(i).getPorcentaje() + "% del RA)";
        }
        opts[lista.size()] = "  <- Cancelar";
        int sel = mostrarMenu("PARTES DEL " + ra.getCodigo(), "Selecciona parte", opts);
        return (sel == lista.size() || sel == -1) ? -1 : sel;
    }

    public static int[] pedirAlumnoFaltas() throws Exception {
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
        int faltasint = pedirInt("Faltas: ");
        String faltasStr = String.valueOf(faltasint);
        if (faltasStr == null) {
            return null;
        }
        int faltas = Integer.parseInt(faltasStr);
        return new int[]{alumno, asignatura, faltas};
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
        if (nombre == null) return;
        Integer horas = pedirInt("Horas a la Semana: ");
        if (horas == null) return;

        Asignaturas nueva = new Asignaturas(nombre, horas);

        while (Math.abs(nueva.sumaPesos() - 100) > 0.001) {
            double restanteRA = 100 - nueva.sumaPesos();
            String codigo = pedirInput("Codigo del RA (ej. RA" + (nueva.getRas().size() + 1)
                    + ") - queda " + restanteRA + "% por repartir: ");
            if (codigo == null) return;
            String descripcion = pedirInput("Descripcion de " + codigo + ": ");
            if (descripcion == null) return;
            Double peso = pedirDouble("Peso de " + codigo + " en la asignatura (%), maximo "
                    + restanteRA + ": ");
            if (peso == null) return;
            if (peso <= 0 || peso > restanteRA + 0.001) {
                mostrarMensaje(FG_RED + "El peso debe ser mayor que 0 y no superar " + restanteRA + "%." + RESET);
                continue;
            }

            ResultadoAprendizaje ra = new ResultadoAprendizaje(codigo, descripcion, peso);
            while (Math.abs(ra.sumaPorcentajes() - 100) > 0.001) {
                double restanteParte = 100 - ra.sumaPorcentajes();
                String nombreParte = pedirInput("Parte de " + codigo + " (ej. Examen) - queda "
                        + restanteParte + "% por repartir: ");
                if (nombreParte == null) return;
                Double porc = pedirDouble("Porcentaje de '" + nombreParte + "' dentro de " + codigo
                        + " (%), maximo " + restanteParte + ": ");
                if (porc == null) return;
                if (porc <= 0 || porc > restanteParte + 0.001) {
                    mostrarMensaje(FG_RED + "El porcentaje debe ser mayor que 0 y no superar "
                            + restanteParte + "%." + RESET);
                    continue;
                }
                ra.añadirParte(new Parte(nombreParte, porc));
            }
            nueva.añadirRA(ra);
        }

        asignaturas.add(nueva);
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
        if (alumnos.isEmpty()) {
            mostrarMensaje(FG_RED + "ERROR No hay alumnos." + RESET);
            return;
        }
        String[] tipos = { "  Mostrar Asignaturas por alumno", "  Borrar nota", "  Salir" };
        while (true) {
            int sel = mostrarMenu("BORRAR NOTA", "Que deseas hacer?", tipos);
            switch (sel) {
                case 0: pantallaAsignaturasAlumno(); break;
                case 1: gestionarNota(false);        break;
                case 2: case -1: return;
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
                    Alumnos al = alumnos.get(datos[0]);
                    String nombreAsig = asignaturas.get(datos[1]).getNombre_asisgnatura();
                    boolean borrada = false;
                    for (int i = 0; i < al.getAsignaturas().size(); i++) {
                        if (al.getAsignaturas().get(i).getNombre_asisgnatura().equals(nombreAsig)) {
                            al.eliminarAsignatura(i);
                            borrada = true;
                            break;
                        }
                    }
                    if (!borrada) {
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
                print(FG_GRAY + "  " + a.getHoras_semana() + "h/sem" + RESET);
                for (ResultadoAprendizaje ra : a.getRas()) {
                    print(FG_GRAY + "  " + ra.toString().replace("\n", "\n  ") + RESET);
                }
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
                print(FG_GRAY + "  " + asig.getHoras_semana() + "h/sem  |  Faltas: " + asig.getFaltas() + RESET);
                for (ResultadoAprendizaje ra : asig.getRas()) {
                    double notaRa = ra.calcularNota();
                    String txt = notaRa >= 0 ? String.valueOf(notaRa) : "pendiente";
                    print(FG_CYAN + "  " + ra.getCodigo() + RESET + " (" + ra.getPeso() + "%) "
                            + ra.getDescripcion() + FG_GRAY + "  -> " + RESET + txt);
                    for (Parte p : ra.getPartes()) {
                        print(FG_GRAY + "      " + p.getNombre() + " (" + p.getPorcentaje() + "%): " + RESET + p.getNotas());
                    }
                }
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
            int c2 = terminal.reader().read(200);
            if (c2 == -1) return 3;
            if (c2 == '[' || c2 == 'O') { 
                int c3 = terminal.reader().read(200);
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

    static Integer pedirInt(String prompt) throws Exception {
        Integer numero = null;
        do {
            String linea = pedirInput(prompt);
            if (linea == null) continue;
            try {
                numero = Integer.parseInt(linea.trim());
            } catch (NumberFormatException e) {}
        } while (numero == null);
        return numero;
    }

    static Double pedirDouble(String prompt) throws Exception {
        Double numero = null;
        do {
            String linea = pedirInput(prompt);
            if (linea == null) continue;
            try {
                numero = Double.parseDouble(linea.trim().replace(",", "."));
            } catch (NumberFormatException e) {}
        } while (numero == null);
        return numero;
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