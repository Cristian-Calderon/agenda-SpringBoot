package net.elpuig.agenda.service;

import net.elpuig.agenda.model.Config;
import net.elpuig.agenda.model.Peticion;
import net.elpuig.agenda.model.Incidencia;
import net.elpuig.agenda.view.SalaMonthlyAgenda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class HtmlExportService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private AgendaProcessor agendaProcessor;

    /**
     * Genera un HTML por cada sala y cada mes presente en las peticiones,
     * y un archivo de log con las incidencias no asignadas.
     * @param peticiones Lista completa de peticiones
     * @param outputDir  Carpeta destino (por ejemplo "output")
     */
    public void exportByMonthAndSala(List<Peticion> peticiones, Path outputDir) throws IOException {
        // 1) Extrae todos los YearMonth distintos de las peticiones (por fechaInicio)
        Set<YearMonth> meses = peticiones.stream()
                .map(p -> YearMonth.from(p.getFechaInicio()))
                .collect(Collectors.toCollection(TreeSet::new));

        // Acumulador de incidencias como objetos
        List<Incidencia> allInc = new ArrayList<>();


        // 2) Para cada mes, filtra peticiones que se solapan con ese mes y procesa la agenda
        for (YearMonth ym : meses) {
            LocalDate mesStart = ym.atDay(1);
            LocalDate mesEnd   = ym.atEndOfMonth();

            List<Peticion> porMes = peticiones.stream()
                    .filter(p -> !p.getFechaFin().isBefore(mesStart)
                            && !p.getFechaInicio().isAfter(mesEnd))
                    .collect(Collectors.toList());

            var result = agendaProcessor.process(
                    createTempConfigForMonth(ym),
                    porMes
            );

            // Acumula todas las incidencias de este mes
            for (Incidencia inc : result.getIncidencias()) {
                allInc.add(inc);
            }


            // 3) Genera un HTML para cada sala de este mes
            for (SalaMonthlyAgenda salaAg : result.getSalas()) {
                Context ctx = new Context();
                ctx.setVariable("sala", salaAg.getSala());
                ctx.setVariable("periodo", ym);
                ctx.setVariable("salas", salaAg);
                // Filtra incidencias por sala
                List<Incidencia> incs = result.getIncidencias().stream()
                        .filter(i -> i.getSala().equalsIgnoreCase(salaAg.getSala()))
                        .collect(Collectors.toList());
                ctx.setVariable("incidencias", incs);

                String html = templateEngine.process("agenda-sala", ctx);

                String filename = salaAg.getSala() + "-"
                        + ym.getYear() + "-"
                        + String.format("%02d", ym.getMonthValue()) + ".html";

                Files.createDirectories(outputDir);
                Path out = outputDir.resolve(filename);
                Files.writeString(out, html, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println("Creado: " + out.toAbsolutePath());
            }
        }

        // 4) Escribir incidences.log con formato por mes, actividad y totales
        Files.createDirectories(outputDir);
        Path logFile = outputDir.resolve("incidences.log");

// Agrupamos todas las incidencias objeto por mes
        Map<YearMonth, List<Incidencia>> byMonth = allInc.stream()
                .filter(inc -> inc.getFecha() != null)      // descartamos las descripciones libres
                .collect(Collectors.groupingBy(
                        inc -> YearMonth.from(inc.getFecha()),
                        TreeMap::new,
                        Collectors.toList()
                ));

        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

// Para cada mes en orden
        for (Map.Entry<YearMonth, List<Incidencia>> entry : byMonth.entrySet()) {
            YearMonth ym = entry.getKey();
            List<Incidencia> incsMes = entry.getValue();

            // Cabecera de mes
            sb.append(String.format("# Resumen Actividades %02d/%d%n%n",
                    ym.getMonthValue(), ym.getYear()));

            // Agrupamos por actividad entrante
            Map<String, List<Incidencia>> byAct = incsMes.stream()
                    .collect(Collectors.groupingBy(
                            Incidencia::getActividadEntrante,
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            // Para cada actividad dentro del mes
            for (Map.Entry<String, List<Incidencia>> ae : byAct.entrySet()) {
                String act = ae.getKey();
                List<Incidencia> listAct = ae.getValue();

                sb.append(String.format("# Actividad %s%n", act));

                // Contamos horas de conflicto (cada incidencia ahora representa 1h no asignada)
                int horasNoAsignadas = listAct.size();

                // Para obtener horas solicitadas, sumar las duraciones originales de Peticion
                // del mismo mes y actividad (puedes recorrer la lista 'porMes' y filtrar por p.getActividad()).
                // Aquí, por simplicidad, asumimos totalSolicitado = horasNoAsignadas + horasAsignadas.
                // Si necesitas precisión, reemplaza esta línea por tu lógica de cálculo:
                int totalSolicitado = horasNoAsignadas + /* calcula aquí las horas asignadas */ 0;

                // Detalle de cada conflicto
                for (Incidencia inc : listAct) {
                    char diaInicial = inc.getFecha()
                            .getDayOfWeek()
                            .getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
                            .charAt(0);

                    sb.append(String.format("Espacio: %s  Día: %c %s  Hora: %d-%d  Conflicto con: %s%n",
                            inc.getSala(),
                            diaInicial,
                            inc.getFecha().format(dateFmt),
                            inc.getHora(), inc.getHora() + inc.getDuracion(),
                            inc.getActividadExistente()
                    ));
                }

                // Línea de totales
                sb.append(String.format("--------> Total: %d / %d h asignadas. (No asignadas: %d h)%n%n",
                        totalSolicitado - horasNoAsignadas,  // asignadas
                        totalSolicitado,
                        horasNoAsignadas
                ));
            }
        }

// Finalmente, volcamos todo el StringBuilder al archivo
        Files.writeString(logFile, sb.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("Generado log de incidencias en: " + logFile.toAbsolutePath());


    }

    // Genera un Config provisional para AgendaProcessor
    private Config createTempConfigForMonth(YearMonth ym) {
        Locale locale = Locale.getDefault();
        return new Config(ym, locale, locale);
    }
}
