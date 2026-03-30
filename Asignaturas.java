package programa_clase;

import java.util.ArrayList;

public class Asignaturas {
    private static int contador = 1;
    private int id_asignatura;
    protected String nombre_asisgnatura;
    protected int horas_semana;
    protected ArrayList<Double> notas_examen;
    protected ArrayList<Double> notas_practicas;
    protected ArrayList<Double> notas_adicionales;
    protected int porcentaje_examen;
    protected int porcentaje_practicas;
    protected int porcentaje_adicional;
    protected int faltas;

    public Asignaturas(String nombre_asisgnatura, int horas_semana, int porcentaje_examen, int porcentaje_practicas, int porcentaje_adicional) {
        this.id_asignatura = contador++;
        this.nombre_asisgnatura = nombre_asisgnatura;
        this.horas_semana = horas_semana;
        this.notas_examen = new ArrayList<>();
        this.notas_practicas = new ArrayList<>();
        this.notas_adicionales = new ArrayList<>();
        this.porcentaje_examen = porcentaje_examen;
        this.porcentaje_practicas = porcentaje_practicas;
        this.porcentaje_adicional = porcentaje_adicional;
        this.faltas = 0;
    }

    public Asignaturas(Asignaturas otra) {
        this.id_asignatura = contador++;
        this.nombre_asisgnatura = otra.nombre_asisgnatura;
        this.horas_semana = otra.horas_semana;
        this.notas_examen = new ArrayList<>();
        this.notas_practicas = new ArrayList<>();
        this.notas_adicionales = new ArrayList<>();
        this.porcentaje_examen = otra.porcentaje_examen;
        this.porcentaje_practicas = otra.porcentaje_practicas;
        this.porcentaje_adicional = otra.porcentaje_adicional;
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

    public ArrayList<Double> getNotas_examen() {
        return notas_examen;
    }

    public void setNotas_examen(ArrayList<Double> notas_examen) {
        this.notas_examen = notas_examen;
    }

    public ArrayList<Double> getNotas_practicas() {
        return notas_practicas;
    }

    public void setNotas_practicas(ArrayList<Double> notas_practicas) {
        this.notas_practicas = notas_practicas;
    }

    public ArrayList<Double> getNotas_adicionales() {
        return notas_adicionales;
    }

    public void setNotas_adicionales(ArrayList<Double> notas_adicionales) {
        this.notas_adicionales = notas_adicionales;
    }

    public double getPorcentaje_examen() {
        return porcentaje_examen;
    }

    public void setPorcentaje_examen(int porcentaje_examen) {
        this.porcentaje_examen = porcentaje_examen;
    }

    public double getPorcentaje_practicas() {
        return porcentaje_practicas;
    }

    public void setPorcentaje_practicas(int porcentaje_practicas) {
        this.porcentaje_practicas = porcentaje_practicas;
    }

    public int getPorcentaje_adicional() {
        return porcentaje_adicional;
    }

    public void setPorcentaje_adicional(int porcentaje_adicional) {
        this.porcentaje_adicional = porcentaje_adicional;
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

    public static void setContador(int valor) {
        contador = valor;
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
        if (notas_practicas.isEmpty() || notas_examen.isEmpty()) {
            System.out.println("Faltan notas");
            return -1;
        }

        double nota_p = 0;
        double nota_e = 0;
        double nota_a = 0;

        for (int i = 0; i < notas_practicas.size(); i++) {
            nota_p += notas_practicas.get(i);
        }
        nota_p /= notas_practicas.size();

        for (int i = 0; i < notas_examen.size(); i++) {
            nota_e += notas_examen.get(i);
        }
        nota_e /= notas_examen.size();

        if (porcentaje_adicional > 0) {
            if (notas_adicionales.isEmpty()) {
                System.out.println("Faltan notas adicionales");
                return -1;
            }
            for (int i = 0; i < notas_adicionales.size(); i++) {
                nota_a += notas_adicionales.get(i);
            }
            nota_a /= notas_adicionales.size();
        }

        if (porcentaje_practicas + porcentaje_examen + porcentaje_adicional != 100) {
            System.out.println("Los porcentajes no suman 100%");
            return -1;
        }

        double nota_final = nota_p * (porcentaje_practicas / 100.0) + nota_e * (porcentaje_examen / 100.0);

        if (porcentaje_adicional > 0) {
            nota_final += nota_a * (porcentaje_adicional / 100.0);
        }
        return Math.round(nota_final * 100.0) / 100.0;
    }

    public void añadirNotaExamen(double nota) {
        this.notas_examen.add(nota);
    }

    public void añadirNotaPractica(double nota) {
        this.notas_practicas.add(nota);
    }

    public void añadirNotaAdicional(double nota) {
        this.notas_adicionales.add(nota);
    }

    public void borrarNotaExamen(double nota) {
        for (int i = 0; i < notas_examen.size(); i++) {
            if (notas_examen.get(i) == nota) {
                this.notas_examen.remove(i);
                return;
            }
        }
    }

    public void borrarNotaPractica(double nota) {
        for (int i = 0; i < notas_practicas.size(); i++) {
            if (notas_practicas.get(i) == nota) {
                this.notas_practicas.remove(i);
                return;
            }
        }
    }

    public void borrarNotaAdicional(double nota) {
        for (int i = 0; i < notas_adicionales.size(); i++) {
            if (notas_adicionales.get(i) == nota) {
                this.notas_adicionales.remove(i);
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "\n==============================\n" + "Asignatura: " + nombre_asisgnatura + "\nHoras/Semana: " + horas_semana + "\nNota Examen: " + notas_examen + "\nNota Prácticas: " + notas_practicas + "\nNota Adicional: " + notas_adicionales + "\nPorcentaje Examen: " + porcentaje_examen + "%\nPorcentaje Prácticas: " + porcentaje_practicas + "%\nPorcentaje Adicional: " + porcentaje_adicional + "%\nFaltas: " + faltas + "\n==============================";
    }
}
