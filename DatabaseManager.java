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
        String sqlAsig = """
            CREATE TABLE IF NOT EXISTS asignaturas (
                id_asignatura INTEGER PRIMARY KEY,
                id_alumno INTEGER NOT NULL DEFAULT 0,
                nombre TEXT,
                horas_semana INTEGER,
                faltas INTEGER DEFAULT 0
            )""";
        String sqlRas = """
            CREATE TABLE IF NOT EXISTS ras (
                id_ra INTEGER PRIMARY KEY,
                id_asignatura INTEGER,
                codigo TEXT,
                descripcion TEXT,
                peso REAL,
                FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id_asignatura)
            )""";
        String sqlPartes = """
            CREATE TABLE IF NOT EXISTS partes (
                id_parte INTEGER PRIMARY KEY,
                id_ra INTEGER,
                nombre TEXT,
                porcentaje REAL,
                notas TEXT,
                FOREIGN KEY (id_ra) REFERENCES ras(id_ra)
            )""";

        try (Connection conn = conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlAlumnos);
            stmt.execute(sqlAsig);
            stmt.execute(sqlRas);
            stmt.execute(sqlPartes);
        } catch (SQLException e) {
            System.out.println("Error al inicializar BD: " + e.getMessage());
        }
    }


    public static void guardarTodo(ArrayList<Alumnos> alumnos, ArrayList<Asignaturas> plantillas) {
        try (Connection conn = conectar()) {
            conn.setAutoCommit(false);
            try {
                try (Statement st = conn.createStatement()) {
                    st.execute("DELETE FROM partes");
                    st.execute("DELETE FROM ras");
                    st.execute("DELETE FROM asignaturas");
                    st.execute("DELETE FROM alumnos");
                }

                for (Asignaturas asig : plantillas) {
                    guardarAsignatura(conn, asig, 0);
                }

                for (Alumnos a : alumnos) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO alumnos (id_alumno, nombre, apellidos, curso) VALUES (?,?,?,?)")) {
                        ps.setInt(1, a.getId_alumno());
                        ps.setString(2, a.getNombre());
                        ps.setString(3, a.getApellidos());
                        ps.setString(4, a.getCurso());
                        ps.executeUpdate();
                    }
                    for (Asignaturas asig : a.getAsignaturas()) {
                        guardarAsignatura(conn, asig, a.getId_alumno());
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }

    private static void guardarAsignatura(Connection conn, Asignaturas asig, int idAlumno) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO asignaturas (id_asignatura, id_alumno, nombre, horas_semana, faltas) VALUES (?,?,?,?,?)")) {
            ps.setInt(1, asig.getId_asignatura());
            ps.setInt(2, idAlumno);
            ps.setString(3, asig.getNombre_asisgnatura());
            ps.setInt(4, asig.getHoras_semana());
            ps.setInt(5, asig.getFaltas());
            ps.executeUpdate();
        }

        for (ResultadoAprendizaje ra : asig.getRas()) {
            try (PreparedStatement psRa = conn.prepareStatement(
                    "INSERT INTO ras (id_ra, id_asignatura, codigo, descripcion, peso) VALUES (?,?,?,?,?)")) {
                psRa.setInt(1, ra.getId_ra());
                psRa.setInt(2, asig.getId_asignatura());
                psRa.setString(3, ra.getCodigo());
                psRa.setString(4, ra.getDescripcion());
                psRa.setDouble(5, ra.getPeso());
                psRa.executeUpdate();
            }

            for (Parte p : ra.getPartes()) {
                try (PreparedStatement psP = conn.prepareStatement(
                        "INSERT INTO partes (id_parte, id_ra, nombre, porcentaje, notas) VALUES (?,?,?,?,?)")) {
                    psP.setInt(1, p.getId_parte());
                    psP.setInt(2, ra.getId_ra());
                    psP.setString(3, p.getNombre());
                    psP.setDouble(4, p.getPorcentaje());
                    psP.setString(5, listaATexto(p.getNotas()));
                    psP.executeUpdate();
                }
            }
        }
    }


    public static ArrayList<Alumnos> cargarAlumnos() {
        ArrayList<Alumnos> lista = new ArrayList<>();
        try (Connection conn = conectar();
             Statement st = conn.createStatement();
             ResultSet rsA = st.executeQuery("SELECT * FROM alumnos ORDER BY id_alumno")) {
            while (rsA.next()) {
                Alumnos a = new Alumnos(
                    rsA.getString("nombre"),
                    rsA.getString("apellidos"),
                    rsA.getString("curso")
                );
                a.setId_alumno(rsA.getInt("id_alumno"));
                for (Asignaturas asig : cargarAsignaturasDe(conn, a.getId_alumno())) {
                    a.añadirAsignatura(asig);
                }
                lista.add(a);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar alumnos: " + e.getMessage());
        }
        actualizarContadores();
        return lista;
    }

    public static ArrayList<Asignaturas> cargarAsignaturas() {
        ArrayList<Asignaturas> lista = new ArrayList<>();
        try (Connection conn = conectar()) {
            lista = cargarAsignaturasDe(conn, 0);
        } catch (SQLException e) {
            System.out.println("Error al cargar asignaturas: " + e.getMessage());
        }
        actualizarContadores();
        return lista;
    }

    private static ArrayList<Asignaturas> cargarAsignaturasDe(Connection conn, int idAlumno) throws SQLException {
        ArrayList<Asignaturas> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM asignaturas WHERE id_alumno = ? ORDER BY id_asignatura")) {
            ps.setInt(1, idAlumno);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Asignaturas asig = new Asignaturas(
                        rs.getString("nombre"),
                        rs.getInt("horas_semana")
                    );
                    asig.setId_asignatura(rs.getInt("id_asignatura"));
                    asig.setFaltas(rs.getInt("faltas"));
                    cargarRas(conn, asig);
                    lista.add(asig);
                }
            }
        }
        return lista;
    }

    private static void cargarRas(Connection conn, Asignaturas asig) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM ras WHERE id_asignatura = ? ORDER BY id_ra")) {
            ps.setInt(1, asig.getId_asignatura());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ResultadoAprendizaje ra = new ResultadoAprendizaje(
                        rs.getString("codigo"),
                        rs.getString("descripcion"),
                        rs.getDouble("peso")
                    );
                    ra.setId_ra(rs.getInt("id_ra"));
                    cargarPartes(conn, ra);
                    asig.añadirRA(ra);
                }
            }
        }
    }

    private static void cargarPartes(Connection conn, ResultadoAprendizaje ra) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM partes WHERE id_ra = ? ORDER BY id_parte")) {
            ps.setInt(1, ra.getId_ra());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Parte p = new Parte(
                        rs.getString("nombre"),
                        rs.getDouble("porcentaje")
                    );
                    p.setId_parte(rs.getInt("id_parte"));
                    p.setNotas(textoALista(rs.getString("notas")));
                    ra.añadirParte(p);
                }
            }
        }
    }

    private static void actualizarContadores() {
        try (Connection conn = conectar()) {
            Alumnos.setContador(maxId(conn, "alumnos", "id_alumno") + 1);
            Asignaturas.setContador(maxId(conn, "asignaturas", "id_asignatura") + 1);
            ResultadoAprendizaje.setContador(maxId(conn, "ras", "id_ra") + 1);
            Parte.setContador(maxId(conn, "partes", "id_parte") + 1);
        } catch (SQLException e) {
            System.out.println("Error al actualizar contadores: " + e.getMessage());
        }
    }

    private static int maxId(Connection conn, String tabla, String columna) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(" + columna + "), 0) FROM " + tabla)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
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