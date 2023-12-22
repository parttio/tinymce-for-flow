package org.vaadin.tinymce;

public enum Plugin {
    //@formatter:off    
    ADVLIST("advlist"),
    ANCHOR("anchor"),
    AUTOLINK("autolink"),
    AUTORESIZE("autoresize"),
    AUTOSAVE("autosave"),
    CHARMAP("charmap"),
    CODE("code"),
    CODESAMPLE("codesample"),
    COLORPICKER("colorpicker"),
    CONTEXTMENU("contextmenu"),
    DIRECTIONALITY("directionality"),
    EMOTICONS("emoticons"),
    FULLPAGE("fullpage"),
    FULLSCREEN("fullscreen"),
    HELP("help"),
    HR("hr"),
    IMAGE("image"),
    IMAGE_TOOLS("imagetools"),
    IMPORT_CSS("importcss"),
    INSERT_DATETIME("insertdatetime"),
    LEGACYOUTPUT("legacyoutput"),
    LINK("link"),
    LISTS("lists"),
    MEDIA("media"),
    NONBREAKING("nonbreaking"),
    NONEDITABLE("noneditable"),
    PAGEBREAK("pagebreak"),
    PASTE("paste"),
    PREVIEW("preview"),
    PRINT("print"),
    QUICKBARS("quickbars"),
    SAVE("save"),
    SEARCH_REPLACE("searchreplace"),
    SPELLCHECKER("spellchecker"),
    TABFOCUS("tabfocus"),
    TABLE("table"),
    TEMPLATE("template"),
    TEXT_COLOR("textcolor"),
    TEXT_PATTERN("textpattern"),
    VISUAL_BLOCKS("visualblocks"),
    VISUAL_CHARS("visualchars"),
    WORDCOUNT("wordcount");
    //@formatter:on

    public final String pluginLabel;

    private Plugin(String pluginLabel) {
        this.pluginLabel = pluginLabel;
    }

}
