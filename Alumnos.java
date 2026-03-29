package programa_clase;

import java.util.ArrayList;

public class Alumnos {
    private static int contador = 0;
    protected int id_alumno;
    protected String nombre;
    protected String apellidos;
    protected String curso;
    protected ArrayList<Asignaturas> asignaturas;

    public Alumnos(String nombre, String apellidos, String curso) {
        this.id_alumno = contador++;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.curso = curso;
        this.asignaturas = new ArrayList<>();
    }

    public int getId_alumno() {
        return id_alumno;
    }

    public void setId_alumno(int id_alumno) {
        this.id_alumno = id_alumno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCurso() {
        return curso;
    }
    
    public ArrayList<Asignaturas> getAsignaturas() {
        return asignaturas;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public void añadirAsignatura(Asignaturas asignatura) {
        this.asignaturas.add(asignatura);
    }

    public void eliminarAsignatura(int index) {
        this.asignaturas.remove(index);
    }
   public boolean tieneAsignatura(String nombre) {
        for (int i = 0; i < asignaturas.size(); i++) {
            if (asignaturas.get(i).getNombre_asisgnatura().equals(nombre)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "\n==============================\n" + "ID: " + id_alumno + "\nNombre: " + nombre + "\nApellidos: " + apellidos + "\nCurso: " + curso + "\nAsignaturas: " + asignaturas + "\n==============================";
    }
    
}