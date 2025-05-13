package net.elpuig.agenda.model;

import java.time.LocalDate;

public class Incidencia {
    private String sala;
    private String actividadEntrante;
    private String actividadExistente;
    private LocalDate fecha;
    private int hora;
    private int duracion;

    // Constructor para incidencias simples (texto libre)
    public Incidencia(String sala, String descripcion) {
        this(sala, descripcion, "", null, 0, 0);
    }

    // Constructor completo para conflictos hora a hora
    public Incidencia(String sala,
                      String actividadEntrante,
                      String actividadExistente,
                      LocalDate fecha,
                      int hora,
                      int duracion) {
        this.sala = sala;
        this.actividadEntrante = actividadEntrante;
        this.actividadExistente = actividadExistente;
        this.fecha = fecha;
        this.hora = hora;
        this.duracion = duracion;
    }

    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }

    public String getActividadEntrante() { return actividadEntrante; }
    public void setActividadEntrante(String actividadEntrante) { this.actividadEntrante = actividadEntrante; }

    public String getActividadExistente() { return actividadExistente; }
    public void setActividadExistente(String actividadExistente) { this.actividadExistente = actividadExistente; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public int getHora() { return hora; }
    public void setHora(int hora) { this.hora = hora; }

    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }

    @Override
    public String toString() {
        if (fecha == null) {
            return actividadEntrante;
        }
        return String.format(
                "Espacio: %s  Fecha: %s  Hora: %d-%d  Petición: %s  Conflicto con: %s",
                sala,
                fecha,
                hora, hora + duracion,
                actividadEntrante,
                actividadExistente
        );
    }
}
