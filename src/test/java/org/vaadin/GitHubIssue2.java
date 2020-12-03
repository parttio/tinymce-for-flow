package org.vaadin;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import org.vaadin.tinymce.TinyMce;

@Route
public class GitHubIssue2 extends Composite<VerticalLayout> {

	private static final long serialVersionUID = 1L;

	private Tab tab1 = new Tab("tab 1");
	private Tab tab2 = new Tab("tabl 2");
	private Tabs tabs = new Tabs(tab1, tab2);
	private VerticalLayout tab1Layout = new VerticalLayout();
	private VerticalLayout tab2Layout = new VerticalLayout();
	private Map<Tab, Component> tabsToPages = new HashMap<>();
	private VerticalLayout content = new VerticalLayout();

	public GitHubIssue2() {
		this.getContent().setSizeFull();
		TinyMce editor = new TinyMce();
		editor.setEditorContent("sample text");
		tab1Layout.add(new Span("Tiny MCE Editor"), editor);
		tab2Layout.add(new Span("tab 2"));

		tabsToPages.put(tab1, tab1Layout);
		tabsToPages.put(tab2, tab2Layout);
		this.getContent().add(tabs);
		this.getContent().add(content);
		content.add(tab1Layout);

		tabs.addSelectedChangeListener(event -> {
			content.removeAll();
			content.add(tabsToPages.get(tabs.getSelectedTab()));
		});
	}
}