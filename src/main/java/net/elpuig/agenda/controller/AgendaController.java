package net.elpuig.agenda.controller;

import net.elpuig.agenda.model.Config;
import net.elpuig.agenda.model.Peticion;
import net.elpuig.agenda.service.DataLoader;
import net.elpuig.agenda.service.AgendaProcessor;
import net.elpuig.agenda.service.HtmlExportService;
import net.elpuig.agenda.view.AgendaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.util.List;

@Controller
public class AgendaController {

    @Autowired
    private DataLoader dataLoader;

    @Autowired
    private AgendaProcessor agendaProcessor;

    @Autowired
    private HtmlExportService exporter;

    @GetMapping("/upload")
    public String showUploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleUpload(
            @RequestParam("configFile") MultipartFile configFile,
            @RequestParam("requestsFile") MultipartFile requestsFile,
            Model model
    ) {
        try {
            // 1) parsear config.txt
            Config cfg = dataLoader.parseConfig(configFile);
            // 2) parsear peticiones.txt
            List<Peticion> peticiones = dataLoader.parseRequests(requestsFile, cfg);

            Path outDir = Paths.get("output");  // o la carpeta que elijas
            exporter.exportByMonthAndSala(peticiones, outDir);

            // 3) procesar la agenda completa
            AgendaResult resultado = agendaProcessor.process(cfg, peticiones);

            // ← Aquí añades el atributo y devuelves la vista “agenda”
            model.addAttribute("resultado", resultado);
            return "agenda";

        } catch (Exception e) {
            model.addAttribute("error", "Error al procesar los ficheros: " + e.getMessage());
            return "upload";
        }
    }
}
