package org.vaadin.tinymce;

public enum Toolbar {
    //@formatter:off
    SEPARATOR("|"),
    UNDO("undo"),
    REDO("redo"),
    /**
     * @deprecated Renamed to {@code blocks} in TinyMCE 6. Use {@link #BLOCKS} instead.
     */
    @Deprecated
    FORMAT_SELECT("formatselect"),
    BLOCKS("blocks"),
    BOLD("bold"),
    ITALIC("italic"),
    UNDERLINE("underline"),
    STRIKETHROUGH("strikethrough"),
    FORECOLOR("forecolor"),
    BACKCOLOR("backcolor"),
    ALIGN_LEFT("alignleft"),
    ALIGN_CENTER("aligncenter"),
    ALIGN_RIGHT("alignright"),
    ALIGN_JUSTIFY("alignjustify"),
    /**
     * @deprecated Renamed to {@code fontfamily} in TinyMCE 7. Use {@link #FONT_FAMILY} instead.
     */
    @Deprecated
    FONTNAME("fontname"),
    FONT_FAMILY("fontfamily"),
    FONTSIZE("fontsize"),
    FONT_SIZE_INPUT("fontsizeinput"),
    BLOCKQUOTE("blockquote"),
    NUMLIST("numlist"),
    BULLIST("bullist"),
    OUTDENT("outdent"),
    INDENT("indent"),
    REMOVE_FORMAT("removeformat"),
    HELP("help"),
    TABLE("table"),
    TABLE_DELETE("tabledelete"),
    TABLE_PROPS("tableprops"),
    TABLE_ROWPROPS("tablerowprops"),
    TABLE_CELLPROPS("tablecellprops"),
    TABLE_INSERT_ROW_BEFORE("tableinsertrowbefore"),
    TABLE_INSERT_ROW_AFTER("tableinsertrowafter"),
    TABLE_DELETE_ROW("tabledeleterow"),
    TABLE_INSERT_COL_BEFORE("tableinsertcolbefore"),
    TABLE_INSERT_COL_AFTER("tableinsertcolafter"),
    /**
     * @deprecated String value has wrong case and was never functional. Use {@link #FONT_FAMILY} instead.
     */
    @Deprecated
    FONTSELECT("FONTSELECT"),
    /**
     * @deprecated String value has wrong case and was never functional. Use {@link #FONTSIZE} instead.
     */
    @Deprecated
    FONTSIZESELECT("FONTSIZESELECT"),
    EMOTICONS("emoticons"),
    LINK("link"),
    IMAGE("image"),
    MEDIA("media"),
    /**
     * @deprecated The print plugin was removed in TinyMCE 6.
     */
    @Deprecated
    PRINT("print"),
    INSERT_DATETIME("insertdatetime");
    //@formatter:on

    public final String toolbarLabel;

    private Toolbar(String toolbarLabel) {
        this.toolbarLabel = toolbarLabel;
    }

}
