package org.vaadin.tinymce;

public enum Menubar {
    //@formatter:off
    FILE("file"),
    EDIT("edit"),
    VIEW("view"),
    INSERT("insert"),
    FORMAT("format"),
    TOOLS("tools"),
    TABLE("table"),
    HELP("help");
    //@formatter:on

    public final String menubarLabel;

    private Menubar(String menubarLabel) {
        this.menubarLabel = menubarLabel;
    }

}
