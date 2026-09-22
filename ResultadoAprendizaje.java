package programa_clase;

import java.util.ArrayList;

public class ResultadoAprendizaje {
    private static int contador = 1;
    private int id_ra;
    private String codigo;
    private String descripcion;
    private double peso;
    private ArrayList<Parte> partes;

    public ResultadoAprendizaje(String codigo, String descripcion, double peso) {
        this.id_ra = contador++;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.peso = peso;
        this.partes = new ArrayList<>();
    }

    public ResultadoAprendizaje(ResultadoAprendizaje otro) {
        this.id_ra = contador++;
        this.codigo = otro.codigo;
        this.descripcion = otro.descripcion;
        this.peso = otro.peso;
        this.partes = new ArrayList<>();
        for (Parte p : otro.partes) {
            this.partes.add(new Parte(p));
        }
    }

    public int getId_ra() {
        return id_ra;
    }

    public void setId_ra(int id_ra) {
        this.id_ra = id_ra;
    }

    public static void setContador(int valor) {
        contador = valor;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public ArrayList<Parte> getPartes() {
        return partes;
    }

    public void añadirParte(Parte parte) {
        this.partes.add(parte);
    }

    public void borrarParte(String nombre) {
        partes.removeIf(p -> p.getNombre().equalsIgnoreCase(nombre));
    }

    public Parte buscarParte(String nombre) {
        for (Parte p : partes) {
            if (p.getNombre().equalsIgnoreCase(nombre)) return p;
        }
        return null;
    }

    public double sumaPorcentajes() {
        double suma = 0;
        for (Parte p : partes) suma += p.getPorcentaje();
        return suma;
    }

    public double calcularNota() {
        if (partes.isEmpty()) {
            return -1;
        }
        if (Math.abs(sumaPorcentajes() - 100) > 0.001) {
            return -1;
        }
        double total = 0;
        for (Parte p : partes) {
            double media = p.calcularMedia();
            if (media < 0) {
                return -1;
            }
            total += media * p.getPorcentaje() / 100.0;
        }
        return Math.round(total * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(codigo).append(" - ").append(descripcion)
          .append(" (").append(peso).append("% del módulo)");
        for (Parte p : partes) {
            sb.append("\n    · ").append(p);
        }
        return sb.toString();
    }
}