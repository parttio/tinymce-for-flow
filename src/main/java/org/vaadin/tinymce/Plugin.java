package org.vaadin.tinymce;

public enum Plugin {
    ADVLIST("advlist"), ANCHOR("anchor"), AUTOLINK("autolink"), AUTORESIZE(
            "autoresize"), AUTOSAVE("autosave"), CHARMAP("charmap"), CODE(
                    "code"), CODESAMPLE("codesample"), COLORPICKER(
                            "colorpicker"), CONTEXTMENU(
                                    "contextmenu"), DIRECTIONALITY(
                                            "directionality"), EMOTICONS(
                                                    "emoticons"), FULLPAGE(
                                                            "fullpage"), FULLSCREEN(
                                                                    "fullscreen"), HELP(
                                                                            "help"), HR(
                                                                                    "hr"), IMAGE(
                                                                                            "image"), IMAGETOOLS(
                                                                                                    "imagetools"), IMPORTCSS(
                                                                                                            "importcss"), INSERTDATETIME(
                                                                                                                    "insertdatetime"), LEGACYOUTPUT(
                                                                                                                            "legacyoutput"), LINK(
                                                                                                                                    "link"), LISTS(
                                                                                                                                            "lists"), MEDIA(
                                                                                                                                                    "media"), NONBREAKING(
                                                                                                                                                            "nonbreaking"), NONEDITABLE(
                                                                                                                                                                    "noneditable"), PAGEBREAK(
                                                                                                                                                                            "pagebreak"), PASTE(
                                                                                                                                                                                    "paste"), PREVIEW(
                                                                                                                                                                                            "preview"), PRINT(
                                                                                                                                                                                                    "print"), QUICKBARS(
                                                                                                                                                                                                            "quickbars"), SAVE(
                                                                                                                                                                                                                    "save"), SEARCHREPLACE(
                                                                                                                                                                                                                            "searchreplace"), SPELLCHECKER(
                                                                                                                                                                                                                                    "spellchecker"), TABFOCUS(
                                                                                                                                                                                                                                            "tabfocus"), TABLE(
                                                                                                                                                                                                                                                    "table"), TEMPLATE(
                                                                                                                                                                                                                                                            "template"), TEXTCOLOR(
                                                                                                                                                                                                                                                                    "textcolor"), TEXTPATTERN(
                                                                                                                                                                                                                                                                            "textpattern"), VISUALBLOCKS(
                                                                                                                                                                                                                                                                                    "visualblocks"), VISUALCHARS(
                                                                                                                                                                                                                                                                                            "visualchars"), WORDCOUNT(
                                                                                                                                                                                                                                                                                                    "wordcount");

    public final String pluginLabel;

    private Plugin(String pluginLabel) {
        this.pluginLabel = pluginLabel;
    }

}
