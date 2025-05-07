package net.elpuig.agenda.service;

import net.elpuig.agenda.model.Config;
import net.elpuig.agenda.model.Peticion;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.time.LocalDate;


@Service
public class DataLoaderImpl implements DataLoader {

    private static final DateTimeFormatter D_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public Config parseConfig(MultipartFile f) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(f.getInputStream()))) {
            String[] ym = br.readLine().trim().split("\\s+");
            int year  = Integer.parseInt(ym[0]);
            int month = Integer.parseInt(ym[1]);
            String[] langs = br.readLine().trim().split("\\s+");
            // Puedes mapear "ESP" a new Locale("es") y "ENG" a new Locale("en")
            Locale inLang  = mapLangCode(langs[0]);
            Locale outLang = mapLangCode(langs[1]);
            return new Config(YearMonth.of(year, month), inLang, outLang);
        }
    }

    @Override
    public List<Peticion> parseRequests(MultipartFile f, Config cfg) throws Exception {
        List<Peticion> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(f.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tok = line.trim().split("\\s+");
                if (tok.length < 6) continue;
                String actividad = tok[0];
                String sala      = tok[1];
                LocalDate fi     = LocalDate.parse(tok[2], D_FMT);
                LocalDate ff     = LocalDate.parse(tok[3], D_FMT);
                String dayMask   = tok[4];
                String hourMask  = tok[5];
                lista.add(new Peticion(actividad, sala, fi, ff, dayMask, hourMask));
            }
        }
        return lista;
    }

    private Locale mapLangCode(String code) {
        switch(code.toUpperCase()) {
            case "ESP": return new Locale("es");
            case "CAT": return new Locale("ca");
            case "ENG": return new Locale("en");
            // …
            default:    return Locale.getDefault();
        }
    }
}
