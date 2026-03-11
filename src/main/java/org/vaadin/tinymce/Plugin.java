package org.vaadin.tinymce;

public enum Plugin {
    //@formatter:off
    ACCORDION("accordion"),
    /**
     * @deprecated Merged into the {@code lists} plugin in TinyMCE 7. Use {@link #LISTS} instead.
     */
    @Deprecated
    ADVLIST("advlist"),
    ANCHOR("anchor"),
    AUTOLINK("autolink"),
    AUTORESIZE("autoresize"),
    AUTOSAVE("autosave"),
    CHARMAP("charmap"),
    CODE("code"),
    CODESAMPLE("codesample"),
    /**
     * @deprecated Removed in TinyMCE 5.
     */
    @Deprecated
    COLORPICKER("colorpicker"),
    /**
     * @deprecated Removed in TinyMCE 5.
     */
    @Deprecated
    CONTEXTMENU("contextmenu"),
    DIRECTIONALITY("directionality"),
    EMOTICONS("emoticons"),
    /**
     * @deprecated Removed in TinyMCE 6.
     */
    @Deprecated
    FULLPAGE("fullpage"),
    FULLSCREEN("fullscreen"),
    HELP("help"),
    /**
     * @deprecated Removed in TinyMCE 6.
     */
    @Deprecated
    HR("hr"),
    IMAGE("image"),
    /**
     * @deprecated Removed in TinyMCE 6.
     */
    @Deprecated
    IMAGE_TOOLS("imagetools"),
    IMPORT_CSS("importcss"),
    INSERT_DATETIME("insertdatetime"),
    /**
     * @deprecated Removed in TinyMCE 6.
     */
    @Deprecated
    LEGACYOUTPUT("legacyoutput"),
    LINK("link"),
    LISTS("lists"),
    MEDIA("media"),
    NONBREAKING("nonbreaking"),
    NONEDITABLE("noneditable"),
    PAGEBREAK("pagebreak"),
    /**
     * @deprecated Merged into core in TinyMCE 6.
     */
    @Deprecated
    PASTE("paste"),
    PREVIEW("preview"),
    /**
     * @deprecated Removed in TinyMCE 6.
     */
    @Deprecated
    PRINT("print"),
    QUICKBARS("quickbars"),
    SAVE("save"),
    SEARCH_REPLACE("searchreplace"),
    /**
     * @deprecated Cloud-only since TinyMCE 6, not available in self-hosted.
     */
    @Deprecated
    SPELLCHECKER("spellchecker"),
    /**
     * @deprecated Removed in TinyMCE 7.
     */
    @Deprecated
    TABFOCUS("tabfocus"),
    TABLE("table"),
    /**
     * @deprecated Removed in TinyMCE 7. Use the premium {@code advtemplate} plugin.
     */
    @Deprecated
    TEMPLATE("template"),
    /**
     * @deprecated Removed in TinyMCE 6.
     */
    @Deprecated
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
