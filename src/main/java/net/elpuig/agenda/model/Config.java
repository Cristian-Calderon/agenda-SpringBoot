package net.elpuig.agenda.model;

import java.time.YearMonth;
import java.util.Locale;

public class Config {
    private YearMonth periodo;
    private Locale inLang, outLang;

    public Config(YearMonth periodo, Locale inLang, Locale outLang) {
        this.periodo = periodo;
        this.inLang = inLang;
        this.outLang = outLang;
    }

    public YearMonth getPeriodo() {
        return periodo;
    }

    public void setPeriodo(YearMonth periodo) {
        this.periodo = periodo;
    }

    public Locale getInLang() {
        return inLang;
    }

    public void setInLang(Locale inLang) {
        this.inLang = inLang;
    }

    public Locale getOutLang() {
        return outLang;
    }

    public void setOutLang(Locale outLang) {
        this.outLang = outLang;
    }

    @Override
    public String toString() {
        return "Config{" +
                "periodo=" + periodo +
                ", inLang=" + inLang +
                ", outLang=" + outLang +
                '}';
    }
// getters/setters, constructor, toString…
}
