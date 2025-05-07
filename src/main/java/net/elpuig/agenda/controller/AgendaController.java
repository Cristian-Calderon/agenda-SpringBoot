package net.elpuig.agenda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AgendaController {

    @GetMapping("/upload")
    public String showUploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleUpload(
            MultipartFile configFile,
            MultipartFile requestsFile,
            Model model
    ) {
        // TODO: llamar a DataLoader y AgendaProcessor
        // model.addAttribute("viewModel", resultado);
        return "resultado";  // Plantilla que renderizarás luego
    }
}
