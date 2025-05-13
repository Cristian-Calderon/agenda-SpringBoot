// src/main/java/net/elpuig/agenda/view/SalaAgenda.java
package net.elpuig.agenda.view;

import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.Map;

/**
 * Para cada sala, un mapa DayOfWeek → array de 24 booleans (hora a hora).
 */
public class SalaAgenda {
    private String sala;
    private Map<DayOfWeek, boolean[]> ocupado;

    public SalaAgenda(String sala) {
        this.sala = sala;
        this.ocupado = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek d : DayOfWeek.values()) {
            ocupado.put(d, new boolean[24]);
        }
    }

    public String getSala() { return sala; }
    public Map<DayOfWeek, boolean[]> getOcupado() { return ocupado; }
}
