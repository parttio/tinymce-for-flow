package org.vaadin.tinymce;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;

@Route
public class ReplaceSelectionContent extends VerticalLayout {

    public ReplaceSelectionContent() {
    	
    	TinyMce editor = new TinyMce();
    	
    	Button insertCurrentTime = new Button("Insert time");
    	
    	insertCurrentTime.addClickListener(e -> {
    		
    		String now = LocalDateTime.now().toString();
    		
    		editor.replaceSelectionContent(now);

			// Note, this is not up to date yet as the replacement happens on the client side
			String value = editor.getValue();
		});

		editor.addValueChangeListener(e -> {
			Notification.show("ValueChange event: " + e.getValue());
			System.out.println(e.getValue());
		});
    	
    	add(editor, insertCurrentTime);

		add(new Button("Show value", e -> {
			Notification.show(editor.getValue());
		}));

    }
}
