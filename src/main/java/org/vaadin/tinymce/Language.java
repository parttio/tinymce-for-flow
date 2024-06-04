package org.vaadin.tinymce;

public enum Language {
    //@formatter:off
    ENGLISH(""),
    NORWEGIAN("nb_NO"),
    FINNISH("fi"),
    GERMAN("de"),
    SWEDISH("sv_SE"),
    PORTUGESE("pt_PT"),
    POLISH("pl"),
    DUTCH("nl"),
    ESTONIAN("et"),
    SERBIAN("sr"),
    SPANISH("es"),
    SLOVENIAN("sl_SI"),
    BULGARIAN("bg_BG"),
    FRENCH("fr_FR"),
    ROMANIAN("ro"),
    LATVIAN("lt"),
    LITHUANIAN("li"),
    RUSSIAN("ru"),
    UKRANIAN("uk"),
    SLOVAK("sk"),
    TURKISH("tr"),
    DANISH("da"),
    CROATIAN("hr"),
    ITALIAN("it");
    //@formatter:on

    public final String language;

    private Language(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return language;
    }
}
