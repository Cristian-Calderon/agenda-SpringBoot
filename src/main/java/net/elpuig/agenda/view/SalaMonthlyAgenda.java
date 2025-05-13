// SalaMonthlyAgenda.java
package net.elpuig.agenda.view;

import java.util.List;

public class SalaMonthlyAgenda {
    private String sala;
    private List<WeekView> semanas;

    public SalaMonthlyAgenda(String sala, List<WeekView> semanas) {
        this.sala   = sala;
        this.semanas= semanas;
    }

    public String getSala()    { return sala; }
    public List<WeekView> getSemanas() { return semanas; }
}
