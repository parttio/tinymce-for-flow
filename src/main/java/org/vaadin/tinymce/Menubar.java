package org.vaadin.tinymce;

public enum Menubar {
	FILE("File"), EDIT("Edit"), VIEW("View"), INSERT("Insert"), FORMAT("Format"), TOOLS("Tools"), TABLE("Table"),
	HELP("Help");

	public final String menubarLabel;

	private Menubar(String menubarLabel) {
		this.menubarLabel = menubarLabel;
	}

}
