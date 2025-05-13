// AgendaResult.java
package net.elpuig.agenda.view;

import net.elpuig.agenda.model.Incidencia;
import java.util.List;

public class AgendaResult {
    private List<SalaMonthlyAgenda> salas;
    private List<Incidencia> incidencias;

    public AgendaResult(List<SalaMonthlyAgenda> salas, List<Incidencia> incidencias) {
        this.salas       = salas;
        this.incidencias = incidencias;
    }

    public List<SalaMonthlyAgenda> getSalas()       { return salas; }
    public List<Incidencia>        getIncidencias(){ return incidencias; }
}
