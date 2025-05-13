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
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;


@Service
public class HtmlExportService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private AgendaProcessor agendaProcessor;

    /**
     * Genera un HTML por cada sala y cada mes presente en las peticiones.
     * @param peticiones Lista completa de peticiones
     * @param outputDir  Carpeta destino (por ejemplo "output")
     */
    public void exportByMonthAndSala(List<Peticion> peticiones, Path outputDir) throws IOException {
        // 1) Extrae todos los YearMonth distintos de las peticiones
        Set<YearMonth> meses = peticiones.stream()
                .map(p -> YearMonth.from(p.getFechaInicio()))
                .collect(Collectors.toCollection(TreeSet::new));

        // 2) Para cada mes, filtra peticiones que se solapan con ese mes
        for (YearMonth ym : meses) {
            // calculamos los límites del mes
            LocalDate mesStart = ym.atDay(1);
            LocalDate mesEnd   = ym.atEndOfMonth();

            List<Peticion> porMes = peticiones.stream()
                    .filter(p ->
                            // no termina antes de comenzar el mes
                            !p.getFechaFin().isBefore(mesStart)
                                    &&
                                    // no comienza después de acabar el mes
                                    !p.getFechaInicio().isAfter(mesEnd)
                    )
                    .collect(Collectors.toList());

            // Process te devolverá AgendaResult con solo ese mes
            var result = agendaProcessor.process(
                    createTempConfigForMonth(ym),
                    porMes
            );

            // 3) Para cada SalaMonthlyAgenda en result.getSalas()
            for (SalaMonthlyAgenda salaAg : result.getSalas()) {
                Context ctx = new Context();
                ctx.setVariable("sala", salaAg.getSala());
                ctx.setVariable("periodo", ym);
                ctx.setVariable("salas", salaAg);
                // Filtra solo las incidencias de esta sala y mes
                List<Incidencia> incs = result.getIncidencias().stream()
                        .filter(i -> i.getSala().equalsIgnoreCase(salaAg.getSala()))
                        .collect(Collectors.toList());
                ctx.setVariable("incidencias", incs);

                String html = templateEngine.process("agenda-sala", ctx);
                // Nombre: Sala1-2008-01.html
                String filename = salaAg.getSala() + "-" +
                        ym.getYear() + "-" +
                        String.format("%02d", ym.getMonthValue()) + ".html";

                // Asegura carpeta
                Files.createDirectories(outputDir);
                Path out = outputDir.resolve(filename);
                Files.writeString(out, html, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println("Creado: "+ out.toAbsolutePath());
            }
        }
    }

    // Genera un Config provisional para AgendaProcessor
    private Config createTempConfigForMonth(YearMonth ym) {
        // Usamos el mismo locale para entrada y salida, o el que venga de tu cfg original
        Locale locale = Locale.getDefault();
        return new Config(ym, locale, locale);
    }

}
