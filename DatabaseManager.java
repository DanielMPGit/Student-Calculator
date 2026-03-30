package programa_clase;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:colegio.db";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void inicializar() {
        String sqlAlumnos = """
            CREATE TABLE IF NOT EXISTS alumnos (
                id_alumno INTEGER PRIMARY KEY,
                nombre TEXT,
                apellidos TEXT,
                curso TEXT
            )""";
        String sqlAsigPlantilla = """
            CREATE TABLE IF NOT EXISTS asignaturas_plantilla (
                id_asignatura INTEGER PRIMARY KEY,
                nombre TEXT,
                horas_semana INTEGER,
                porc_examen INTEGER,
                porc_practicas INTEGER,
                porc_adicional INTEGER
            )""";
        String sqlAsigAlumno = """
            CREATE TABLE IF NOT EXISTS asignaturas_alumno (
                id_asignatura INTEGER PRIMARY KEY,
                id_alumno INTEGER,
                nombre TEXT,
                horas_semana INTEGER,
                porc_examen INTEGER,
                porc_practicas INTEGER,
                porc_adicional INTEGER,
                faltas INTEGER,
                notas_examen TEXT,
                notas_practicas TEXT,
                notas_adicionales TEXT,
                FOREIGN KEY (id_alumno) REFERENCES alumnos(id_alumno)
            )""";

        try (Connection conn = conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlAlumnos);
            stmt.execute(sqlAsigPlantilla);
            stmt.execute(sqlAsigAlumno);
        } catch (SQLException e) {
            System.out.println("Error al inicializar BD: " + e.getMessage());
        }
    }

    public static void guardarTodo(ArrayList<Alumnos> alumnos, ArrayList<Asignaturas> asignaturas) {
        try (Connection conn = conectar()) {
            conn.createStatement().execute("DELETE FROM asignaturas_alumno");
            conn.createStatement().execute("DELETE FROM alumnos");
            conn.createStatement().execute("DELETE FROM asignaturas_plantilla");

            for (Asignaturas asig : asignaturas) {
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO asignaturas_plantilla VALUES (?,?,?,?,?,?)");
                ps.setInt(1, asig.getId_asignatura());
                ps.setString(2, asig.getNombre_asisgnatura());
                ps.setInt(3, asig.getHoras_semana());
                ps.setInt(4, (int) asig.getPorcentaje_examen());
                ps.setInt(5, (int) asig.getPorcentaje_practicas());
                ps.setInt(6, asig.getPorcentaje_adicional());
                ps.executeUpdate();
            }

            for (Alumnos a : alumnos) {
                PreparedStatement psA = conn.prepareStatement(
                    "INSERT INTO alumnos VALUES (?,?,?,?)");
                psA.setInt(1, a.getId_alumno());
                psA.setString(2, a.getNombre());
                psA.setString(3, a.getApellidos());
                psA.setString(4, a.getCurso());
                psA.executeUpdate();

                for (Asignaturas asig : a.getAsignaturas()) {
                    PreparedStatement psAsig = conn.prepareStatement(
                        "INSERT INTO asignaturas_alumno VALUES (?,?,?,?,?,?,?,?,?,?,?)");
                    psAsig.setInt(1, asig.getId_asignatura());
                    psAsig.setInt(2, a.getId_alumno());
                    psAsig.setString(3, asig.getNombre_asisgnatura());
                    psAsig.setInt(4, asig.getHoras_semana());
                    psAsig.setInt(5, (int) asig.getPorcentaje_examen());
                    psAsig.setInt(6, (int) asig.getPorcentaje_practicas());
                    psAsig.setInt(7, asig.getPorcentaje_adicional());
                    psAsig.setInt(8, asig.getFaltas());
                    psAsig.setString(9, listaATexto(asig.getNotas_examen()));
                    psAsig.setString(10, listaATexto(asig.getNotas_practicas()));
                    psAsig.setString(11, listaATexto(asig.getNotas_adicionales()));
                    psAsig.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }

    public static ArrayList<Alumnos> cargarAlumnos() {
        ArrayList<Alumnos> lista = new ArrayList<>();
        try (Connection conn = conectar()) {
            ResultSet rsA = conn.createStatement()
                .executeQuery("SELECT * FROM alumnos");
            while (rsA.next()) {
                Alumnos a = new Alumnos(
                    rsA.getString("nombre"),
                    rsA.getString("apellidos"),
                    rsA.getString("curso")
                );
                a.setId_alumno(rsA.getInt("id_alumno"));

                PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM asignaturas_alumno WHERE id_alumno = ?");
                ps.setInt(1, a.getId_alumno());
                ResultSet rsAsig = ps.executeQuery();
                while (rsAsig.next()) {
                    Asignaturas asig = new Asignaturas(
                        rsAsig.getString("nombre"),
                        rsAsig.getInt("horas_semana"),
                        rsAsig.getInt("porc_examen"),
                        rsAsig.getInt("porc_practicas"),
                        rsAsig.getInt("porc_adicional")
                    );
                    asig.setFaltas(rsAsig.getInt("faltas"));
                    asig.setNotas_examen(textoALista(rsAsig.getString("notas_examen")));
                    asig.setNotas_practicas(textoALista(rsAsig.getString("notas_practicas")));
                    asig.setNotas_adicionales(textoALista(rsAsig.getString("notas_adicionales")));
                    a.añadirAsignatura(asig);
                }
                lista.add(a);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar alumnos: " + e.getMessage());
        }
        int maxId = 0;
        for (Alumnos a : lista) {
            if (a.getId_alumno() > maxId) maxId = a.getId_alumno();
        }
        Alumnos.setContador(maxId + 1);
        return lista;
    }

    public static ArrayList<Asignaturas> cargarAsignaturas() {
        ArrayList<Asignaturas> lista = new ArrayList<>();
        try (Connection conn = conectar()) {
            ResultSet rs = conn.createStatement()
                .executeQuery("SELECT * FROM asignaturas_plantilla");
            while (rs.next()) {
                Asignaturas asig = new Asignaturas(
                    rs.getString("nombre"),
                    rs.getInt("horas_semana"),
                    rs.getInt("porc_examen"),
                    rs.getInt("porc_practicas"),
                    rs.getInt("porc_adicional")
                );
                lista.add(asig);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar asignaturas: " + e.getMessage());
        }
        int maxId = 0;
        for (Asignaturas a : lista) {
            if (a.getId_asignatura() > maxId) maxId = a.getId_asignatura();
        }
        Asignaturas.setContador(maxId + 1);
        return lista;
    }

    private static String listaATexto(ArrayList<Double> lista) {
        if (lista.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            sb.append(lista.get(i));
            if (i < lista.size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    private static ArrayList<Double> textoALista(String texto) {
        ArrayList<Double> lista = new ArrayList<>();
        if (texto == null || texto.isEmpty()) return lista;
        for (String s : texto.split(",")) {
            lista.add(Double.parseDouble(s));
        }
        return lista;
    }
}
