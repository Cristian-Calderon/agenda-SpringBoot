package net.elpuig.agenda.service;

import net.elpuig.agenda.model.Config;
import net.elpuig.agenda.model.Peticion;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DataLoader {
    Config parseConfig(MultipartFile configFile) throws Exception;
    List<Peticion> parseRequests(MultipartFile requestsFile, Config cfg) throws Exception;
}
