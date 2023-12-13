package org.vaadin.tinymce;

public enum Plugin {
	ADVLIST("advlist"), ANCHOR("anchor"), AUTOLINK("autolink"), AUTORESIZE("autoresize"), AUTOSAVE("autosave"),
	CHARMAP("charmap"), CODE("code"), CODESAMPLE("codesample"), DIRECTIONALITY("directionality"),
	EMOTICONS("emoticons"), FULLSCREEN("fullscreen"), HELP("help"), IMAGE("image"), IMPORTCSS("importcss"),
	INSERTDATETIME("insertdatetime"), LINK("link"), LISTS("lists"), MEDIA("media"), NONBREAKING("nonbreaking"),
	PAGEBREAK("pagebreak"), PREVIEW("preview"), QUICKBARS("quickbars"), SAVE("save"), SEARCHREPLACE("searchreplace"),
	TABLE("table"), TEMPLATE("template"), VISUALBLOCKS("visualblocks"), VISUALCHARS("visualchars"),
	WORDCOUNT("wordcount");

	public final String pluginLabel;

	private Plugin(String pluginLabel) {
		this.pluginLabel = pluginLabel;
	}

}
