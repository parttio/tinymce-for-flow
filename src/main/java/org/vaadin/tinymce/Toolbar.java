package org.vaadin.tinymce;

public enum Toolbar {
    SEPARATOR("|"), UNDO("undo"), REDO("redo"), FORMATSELECT(
            "formatselect"), BLOCKS("blocks"), BOLD("bold"), ITALIC(
                    "italic"), UNDERLINE("underline"), STRIKETHROUGH(
                            "strikethrough"), FORECOLOR("forecolor"), BACKCOLOR(
                                    "backcolor"), ALIGNLEFT(
                                            "alignleft"), ALIGNCENTER(
                                                    "aligncenter"), ALIGNRIGHT(
                                                            "alignright"), ALIGNJUSTIFY(
                                                                    "alignjustify"), FONTNAME(
                                                                            "fontname"), FONTSIZE(
                                                                                    "fontsize"), BLOCKQUOTE(
                                                                                            "blockquote"), NUMLIST(
                                                                                                    "numlist"), BULLIST(
                                                                                                            "bullist"), OUTDENT(
                                                                                                                    "outdent"), INDENT(
                                                                                                                            "indent"), REMOVEFORMAT(
                                                                                                                                    "removeformat"), HELP(
                                                                                                                                            "help"), TABLE(
                                                                                                                                                    "table"), FONTSELECT(
                                                                                                                                                            "FONTSELECT"), FONTSIZESELECT(
                                                                                                                                                                    "FONTSIZESELECT"), EMOTICONS(
                                                                                                                                                                            "emoticons"), LINK(
                                                                                                                                                                                    "link"), IMAGE(
                                                                                                                                                                                            "image"), MEDIA(
                                                                                                                                                                                                    "media"), PRINT(
                                                                                                                                                                                                            "print"), INSERTDATETIME(
                                                                                                                                                                                                                    "insertdatetime")

    ;

    public final String toolbarLabel;

    private Toolbar(String toolbarLabel) {
        this.toolbarLabel = toolbarLabel;
    }

}
