package net.elpuig.agenda.controller;

import net.elpuig.agenda.model.Config;
import net.elpuig.agenda.model.Peticion;
import net.elpuig.agenda.service.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class AgendaController {

    @Autowired
    private DataLoader dataLoader;

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
            // 1. Parsear config.txt
            Config cfg = dataLoader.parseConfig(configFile);

            // 2. Parsear peticiones.txt usando la configuración
            List<Peticion> peticiones = dataLoader.parseRequests(requestsFile, cfg);

            // 3. Añadir al modelo para la vista de prueba
            model.addAttribute("config", cfg);
            model.addAttribute("peticiones", peticiones);

            return "resultado";
        } catch (Exception e) {
            // Si algo falla, vuelve al formulario mostrando el error
            model.addAttribute("error", "Error al procesar los ficheros: " + e.getMessage());
            return "upload";
        }
    }
}
