package programa_clase;

import java.util.ArrayList;

public class Asignaturas {
    private static int contador = 1;
    private int id_asignatura;
    protected String nombre_asisgnatura;
    protected int horas_semana;
    protected ArrayList<ResultadoAprendizaje> ras;
    protected int faltas;

    public Asignaturas(String nombre_asisgnatura, int horas_semana) {
        this.id_asignatura = contador++;
        this.nombre_asisgnatura = nombre_asisgnatura;
        this.horas_semana = horas_semana;
        this.ras = new ArrayList<>();
        this.faltas = 0;
    }

    public Asignaturas(Asignaturas otra) {
        this.id_asignatura = contador++;
        this.nombre_asisgnatura = otra.nombre_asisgnatura;
        this.horas_semana = otra.horas_semana;
        this.ras = new ArrayList<>();
        for (ResultadoAprendizaje ra : otra.ras) {
            this.ras.add(new ResultadoAprendizaje(ra));
        }
        this.faltas = 0;
    }

    public String getNombre_asisgnatura() {
        return nombre_asisgnatura;
    }

    public void setNombre_asisgnatura(String nombre_asisgnatura) {
        this.nombre_asisgnatura = nombre_asisgnatura;
    }

    public int getHoras_semana() {
        return horas_semana;
    }

    public void setHoras_semana(int horas_semana) {
        this.horas_semana = horas_semana;
    }

    public int getFaltas() {
        return faltas;
    }

    public void setFaltas(int faltas) {
        this.faltas = faltas;
    }

    public int getId_asignatura() {
        return id_asignatura;
    }

    public void setId_asignatura(int id_asignatura) {
        this.id_asignatura = id_asignatura;
    }

    public static void setContador(int valor) {
        contador = valor;
    }

    public ArrayList<ResultadoAprendizaje> getRas() {
        return ras;
    }


    public void añadirRA(ResultadoAprendizaje ra) {
        this.ras.add(ra);
    }

    public void borrarRA(String codigo) {
        ras.removeIf(ra -> ra.getCodigo().equalsIgnoreCase(codigo));
    }

    public ResultadoAprendizaje buscarRA(String codigo) {
        for (ResultadoAprendizaje ra : ras) {
            if (ra.getCodigo().equalsIgnoreCase(codigo)) return ra;
        }
        return null;
    }

    public double sumaPesos() {
        double suma = 0;
        for (ResultadoAprendizaje ra : ras) suma += ra.getPeso();
        return suma;
    }

    public boolean añadirNota(String codigoRA, String nombreParte, double nota) {
        ResultadoAprendizaje ra = buscarRA(codigoRA);
        if (ra == null) return false;
        Parte p = ra.buscarParte(nombreParte);
        if (p == null) return false;
        p.añadirNota(nota);
        return true;
    }

    public boolean borrarNota(String codigoRA, String nombreParte, double nota) {
        ResultadoAprendizaje ra = buscarRA(codigoRA);
        if (ra == null) return false;
        Parte p = ra.buscarParte(nombreParte);
        if (p == null) return false;
        p.borrarNota(nota);
        return true;
    }


    public void añadirFaltas(int faltas) {
        this.faltas += faltas;
    }

    public void restarFaltas(int faltas) {
        this.faltas -= faltas;
    }

    public String calcularFaltas(int semanasclase, int porcentaje) {
        int horas_totales = semanasclase * this.horas_semana;
        int maximas_faltas = horas_totales * porcentaje / 100;
        int faltas_permitibles = maximas_faltas - this.faltas;
        return " ---------------------------------------\n| Puedes faltar: " + faltas_permitibles + " | Faltas totales: " + maximas_faltas + "|\n ---------------------------------------";
    }

    public double calcularMedia() {
        if (ras.isEmpty()) {
            return -1;
        }
        if (Math.abs(sumaPesos() - 100) > 0.001) {
            return -1;
        }
        double nota_final = 0;
        for (ResultadoAprendizaje ra : ras) {
            double nota_ra = ra.calcularNota();
            if (nota_ra < 0) return -1;
            nota_final += nota_ra * ra.getPeso() / 100.0;
        }
        return Math.round(nota_final * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n==============================\n")
          .append("Asignatura: ").append(nombre_asisgnatura)
          .append("\nHoras/Semana: ").append(horas_semana);
        for (ResultadoAprendizaje ra : ras) {
            sb.append("\n").append(ra);
        }
        sb.append("\nFaltas: ").append(faltas)
          .append("\n==============================");
        return sb.toString();
    }
}