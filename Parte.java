package programa_clase;

import java.util.ArrayList;

public class Parte {
    private static int contador = 1;
    private int id_parte;
    private String nombre;
    private double porcentaje;
    private ArrayList<Double> notas;

    public Parte(String nombre, double porcentaje) {
        this.id_parte = contador++;
        this.nombre = nombre;
        this.porcentaje = porcentaje;
        this.notas = new ArrayList<>();
    }

    public Parte(Parte otra) {
        this.id_parte = contador++;
        this.nombre = otra.nombre;
        this.porcentaje = otra.porcentaje;
        this.notas = new ArrayList<>();
    }

    public int getId_parte() {
        return id_parte;
    }

    public void setId_parte(int id_parte) {
        this.id_parte = id_parte;
    }

    public static void setContador(int valor) {
        contador = valor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public ArrayList<Double> getNotas() {
        return notas;
    }

    public void setNotas(ArrayList<Double> notas) {
        this.notas = notas;
    }

    public void añadirNota(double nota) {
        this.notas.add(nota);
    }

    public void borrarNota(double nota) {
        for (int i = 0; i < notas.size(); i++) {
            if (notas.get(i) == nota) {
                notas.remove(i);
                return;
            }
        }
    }

    public double calcularMedia() {
        if (notas.isEmpty()) return -1;
        double suma = 0;
        for (double n : notas) suma += n;
        return suma / notas.size();
    }

    @Override
    public String toString() {
        return nombre + " (" + porcentaje + "% del RA) Notas: " + notas;
    }
}