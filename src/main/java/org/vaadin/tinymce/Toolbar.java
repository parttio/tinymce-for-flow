package org.vaadin.tinymce;

public enum Toolbar {
	SEPARATOR("|"), UNDO("undo"), REDO("redo"), BLOCKS("blocks"), BOLD("bold"), ITALIC("italic"),
	UNDERLINE("underline"), STRIKETHROUGH("strikethrough"), FORECOLOR("forecolor"), BACKCOLOR("backcolor"),
	ALIGNLEFT("alignleft"), ALIGNCENTER("aligncenter"), ALIGNRIGHT("alignright"), ALIGNJUSTIFY("alignjustify"),
	FONTNAME("fontname"), FONTSIZE("fontsize"), BLOCKQUOTE("blockquote"), NUMLIST(""), BULLIST(""), OUTDENT("outdent"),
	INDENT("indent"), REMOVEFORMAT("removeformat"), HELP("help"), TABLE("table")

	;

	public final String toolbarLabel;

	private Toolbar(String toolbarLabel) {
		this.toolbarLabel = toolbarLabel;
	}

}
