package net.elpuig.agenda.view;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

public class WeekView {
    private LocalDate start;
    private Map<DayOfWeek, String[]> actividad;

    public WeekView(LocalDate start) {
        this.start = start;
        this.actividad = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek d : DayOfWeek.values()) {
            actividad.put(d, new String[24]);  // null = libre
        }
    }

    public LocalDate getStart() { return start; }
    public Map<DayOfWeek, String[]> getActividad() { return actividad; }
}
