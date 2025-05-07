package net.elpuig.agenda.model;

import java.time.YearMonth;
import java.util.Locale;

public class Config {
    private YearMonth periodo;
    private Locale locale;


    public Config(YearMonth periodo, Locale locale) {
        this.periodo = periodo;
        this.locale = locale;
    }

    public YearMonth getPeriodo() {
        return periodo;
    }

    public void setPeriodo(YearMonth periodo) {
        this.periodo = periodo;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        return "Config{" +
                "periodo=" + periodo +
                ", locale=" + locale +
                '}';
    }

// getters/setters, constructor, toString…
}
