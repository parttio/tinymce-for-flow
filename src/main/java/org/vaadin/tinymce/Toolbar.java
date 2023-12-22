package org.vaadin.tinymce;

public enum Toolbar {
    //@formatter:off
    SEPARATOR("|"),
    UNDO("undo"),
    REDO("redo"),
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
    FONTNAME("fontname"),
    FONTSIZE("fontsize"),
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
    FONTSELECT("FONTSELECT"),
    FONTSIZESELECT("FONTSIZESELECT"),
    EMOTICONS("emoticons"),
    LINK("link"),
    IMAGE("image"),
    MEDIA("media"),
    PRINT("print"),
    INSERT_DATETIME("insertdatetime");
    //@formatter:on

    public final String toolbarLabel;

    private Toolbar(String toolbarLabel) {
        this.toolbarLabel = toolbarLabel;
    }

}
