package net.elpuig.agenda.service;

import net.elpuig.agenda.model.Config;
import net.elpuig.agenda.model.Peticion;
import net.elpuig.agenda.view.AgendaResult;

import java.util.List;

public interface AgendaProcessor {
    AgendaResult process(Config cfg, List<Peticion> peticiones);
}
