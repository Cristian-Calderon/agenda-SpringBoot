// Incidencia.java
package net.elpuig.agenda.model;

public class Incidencia {
    private String sala;
    private String descripcion;   // “Solapamiento con Tancat”, etc.

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Incidencia{" +
                "sala='" + sala + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
// getters/setters, toString…
}
