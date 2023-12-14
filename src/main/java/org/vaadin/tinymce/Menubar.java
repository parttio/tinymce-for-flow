package org.vaadin.tinymce;

public enum Menubar {
	FILE("file"), EDIT("edit"), VIEW("view"), INSERT("insert"), FORMAT("format"), TOOLS("tools"), TABLE("table"),
	HELP("help");

	public final String menubarLabel;

	private Menubar(String menubarLabel) {
		this.menubarLabel = menubarLabel;
	}

}
