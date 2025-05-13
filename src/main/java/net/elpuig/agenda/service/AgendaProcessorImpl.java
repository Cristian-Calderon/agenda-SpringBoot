package net.elpuig.agenda.service;

import net.elpuig.agenda.model.Config;
import net.elpuig.agenda.model.Peticion;
import net.elpuig.agenda.model.Incidencia;
import net.elpuig.agenda.view.AgendaResult;
import net.elpuig.agenda.view.WeekView;
import net.elpuig.agenda.view.SalaMonthlyAgenda;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AgendaProcessorImpl implements AgendaProcessor {

    @Override
    public AgendaResult process(Config cfg, List<Peticion> peticiones) {
        List<Incidencia> incidencias = new ArrayList<>();

        // 1) Pre-filtrado y ordenación
        List<Peticion> ordered = peticiones.stream()
                .filter(p -> {
                    if (p.getFechaInicio().isAfter(p.getFechaFin())) {
                        incidencias.add(new Incidencia(
                                p.getSala(),
                                "Rango inválido para ‘" + p.getActividad() +
                                        "’: " + p.getFechaInicio() + " > " + p.getFechaFin()
                        ));
                        return false;
                    }
                    return true;
                })
                .sorted(Comparator
                        .comparing((Peticion p) -> !p.getActividad().equalsIgnoreCase("Tancat"))
                        .thenComparing(Peticion::getSala)
                        .thenComparing(Peticion::getFechaInicio)
                )
                .collect(Collectors.toList());

        // 2) Lunes del mes
        YearMonth periodo = cfg.getPeriodo();
        List<LocalDate> mondays = mondaysOfMonth(periodo);

        // 3) Agrupar por sala
        Map<String, List<Peticion>> porSala = ordered.stream()
                .collect(Collectors.groupingBy(
                        Peticion::getSala,
                        TreeMap::new,
                        Collectors.toList()
                ));

        // 4) Construir agenda semanal para cada sala
        List<SalaMonthlyAgenda> salasMensuales = new ArrayList<>();
        LocalDate mesStart = periodo.atDay(1);
        LocalDate mesEnd   = periodo.atEndOfMonth();

        for (Map.Entry<String, List<Peticion>> entry : porSala.entrySet()) {
            String sala = entry.getKey();
            List<Peticion> lista = entry.getValue();

            // a) Inicializa las semanas vacías con nombres de actividad
            List<WeekView> semanas = new ArrayList<>();
            for (LocalDate monday : mondays) {
                semanas.add(new WeekView(monday));
            }

            // b) Rellena cada WeekView con la actividad correspondiente
            for (Peticion p : lista) {
                LocalDate start = p.getFechaInicio().isBefore(mesStart) ? mesStart : p.getFechaInicio();
                LocalDate end   = p.getFechaFin().isAfter(mesEnd)     ? mesEnd   : p.getFechaFin();
                Set<DayOfWeek> dias = mapDayMask(p.getDayMask());

                for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                    DayOfWeek dow = date.getDayOfWeek();
                    if (!dias.contains(dow)) continue;

                    LocalDate mondayOfThisWeek = date.minusDays(dow.getValue() - 1);
                    int weekIndex = mondays.indexOf(mondayOfThisWeek);
                    if (weekIndex < 0) continue;

                    WeekView week = semanas.get(weekIndex);
                    String[] horas = week.getActividad().get(dow);

                    for (String tramo : p.getHourMask().split("_")) {
                        String[] hh = tramo.split("-");
                        int hi = Integer.parseInt(hh[0]), hf = Integer.parseInt(hh[1]);
                        for (int h = hi; h < hf; h++) {
                            if (horas[h] != null) {
                                incidencias.add(new Incidencia(
                                        sala,
                                        "Solapamiento de ‘" + p.getActividad() +
                                                "’ el " + date + " a las " + h + "h"
                                ));
                            } else {
                                horas[h] = p.getActividad();
                            }
                        }
                    }
                }
            }

            salasMensuales.add(new SalaMonthlyAgenda(sala, semanas));
        }

        return new AgendaResult(salasMensuales, incidencias);
    }

    private Set<DayOfWeek> mapDayMask(String mask) {
        Set<DayOfWeek> res = new HashSet<>();
        for (char c : mask.toUpperCase().toCharArray()) {
            switch (c) {
                case 'L': res.add(DayOfWeek.MONDAY);    break;
                case 'M': res.add(DayOfWeek.TUESDAY);   break;
                case 'C': res.add(DayOfWeek.WEDNESDAY); break;
                case 'J': res.add(DayOfWeek.THURSDAY);  break;
                case 'V': res.add(DayOfWeek.FRIDAY);    break;
                case 'S': res.add(DayOfWeek.SATURDAY);  break;
                case 'G': res.add(DayOfWeek.SUNDAY);    break;
            }
        }
        return res;
    }

    private List<LocalDate> mondaysOfMonth(YearMonth ym) {
        LocalDate first = ym.atDay(1);
        LocalDate start = first.minusDays((first.getDayOfWeek().getValue() + 6) % 7);
        List<LocalDate> res = new ArrayList<>();
        while (!start.isAfter(ym.atEndOfMonth())) {
            res.add(start);
            start = start.plusWeeks(1);
        }
        return res;
    }
}
