package net.elpuig.agenda.model;

import java.time.LocalDate;

public class Peticion {
    private String actividad;
    private String sala;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String dayMask;
    private String hourMask;

    public Peticion(String actividad,
                    String sala,
                    LocalDate fechaInicio,
                    LocalDate fechaFin,
                    String dayMask,
                    String hourMask) {
        this.actividad    = actividad;
        this.sala         = sala;
        this.fechaInicio  = fechaInicio;
        this.fechaFin     = fechaFin;
        this.dayMask      = dayMask;
        this.hourMask     = hourMask;
    }

    // getters, setters y toString…


    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getDayMask() {
        return dayMask;
    }

    public void setDayMask(String dayMask) {
        this.dayMask = dayMask;
    }

    public String getHourMask() {
        return hourMask;
    }

    public void setHourMask(String hourMask) {
        this.hourMask = hourMask;
    }

    @Override
    public String toString() {
        return "Peticion{" +
                "actividad='" + actividad + '\'' +
                ", sala='" + sala + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", dayMask='" + dayMask + '\'' +
                ", hourMask='" + hourMask + '\'' +
                '}';
    }
}
