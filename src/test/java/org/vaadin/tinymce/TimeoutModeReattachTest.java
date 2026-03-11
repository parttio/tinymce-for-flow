package org.vaadin.tinymce;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route
public class TimeoutModeReattachTest extends Div {
	
	private TinyMce tinyMce;

	public TimeoutModeReattachTest() {
		tinyMce = new TinyMce();

		tinyMce.setValue("<p>Voi <strong>jorma</strong>!<p>");
		tinyMce.setHeight("700px");
		tinyMce.setValueChangeMode(ValueChangeMode.TIMEOUT);
		tinyMce.setDebounceTimeout(10);
		tinyMce.addValueChangeListener(change -> System.out.println("value change called"));
		add(tinyMce);

		Button b = new Button("Remove and reattach tinymce ", e -> {
			tinyMce.removeFromParent();
			add(tinyMce);
		});
		add(b);
	}

}
